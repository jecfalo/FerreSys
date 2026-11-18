# Despliegue de prueba de FerreSys API en un servidor local (Windows).
# Lo invoca la etapa "Deploy" del Jenkinsfile:
#   1. Detiene la instancia anterior (si existe) usando el PID guardado.
#   2. Copia el .jar recien construido a la carpeta de despliegue.
#   3. Arranca la API con el perfil "ci" (H2 en memoria) en segundo plano.
#   4. Hace un smoke test contra /v3/api-docs; si no responde, el build falla.
param(
    [string]$Jar        = "ferresys-api\target\FerreSys-0.0.1-SNAPSHOT.jar",
    [string]$DeployDir  = "C:\ferresys-deploy",
    [int]   $Port       = 8081,
    [int]   $TimeoutSec = 90
)
$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force $DeployDir | Out-Null
$pidFile = Join-Path $DeployDir "ferresys.pid"
$logFile = Join-Path $DeployDir "ferresys.log"
$target  = Join-Path $DeployDir "ferresys-api.jar"

# 1. Detener la version anterior
if (Test-Path $pidFile) {
    $oldPid = Get-Content $pidFile
    $old = Get-Process -Id $oldPid -ErrorAction SilentlyContinue
    if ($old) {
        Write-Host "Deteniendo la instancia anterior (PID $oldPid)"
        Stop-Process -Id $oldPid -Force
        Start-Sleep -Seconds 2
    }
    Remove-Item $pidFile -Force
}

# 2. Copiar el artefacto
Copy-Item $Jar $target -Force
Write-Host "Artefacto copiado a $target"

# 3. Arrancar en segundo plano. Start-Process crea un proceso independiente;
#    JENKINS_NODE_COOKIE=dontKillMe (en el Jenkinsfile) evita que Jenkins lo mate al terminar el build.
$proc = Start-Process -FilePath "java" `
    -ArgumentList "-jar", "`"$target`"", "--spring.profiles.active=ci", "--server.port=$Port" `
    -RedirectStandardOutput $logFile -RedirectStandardError "$logFile.err" `
    -WindowStyle Hidden -PassThru
$proc.Id | Set-Content $pidFile
Write-Host "FerreSys API arrancando (PID $($proc.Id)) en el puerto $Port"

# 4. Smoke test
$url = "http://localhost:$Port/v3/api-docs"
$deadline = (Get-Date).AddSeconds($TimeoutSec)
while ((Get-Date) -lt $deadline) {
    try {
        $r = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 5
        if ($r.StatusCode -eq 200) {
            Write-Host "Smoke test OK: $url respondio 200"
            exit 0
        }
    } catch { Start-Sleep -Seconds 2 }
    if ($proc.HasExited) { break }
}
Write-Host "La API no respondio en $TimeoutSec s. Ultimas lineas del log:"
if (Test-Path $logFile) { Get-Content $logFile -Tail 40 }
exit 1
