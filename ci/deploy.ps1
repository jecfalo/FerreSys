# Despliegue de prueba de FerreSys API en un servidor local (Windows).
# Lo invoca la etapa "Deploy" del Jenkinsfile:
#   1. Detiene la instancia anterior (PID guardado y, por si acaso, quien ocupe el puerto).
#   2. Copia el .jar recien construido a la carpeta de despliegue.
#   3. Arranca la API con el perfil "ci" (H2 en memoria) totalmente desligada de Jenkins.
#   4. Hace un smoke test contra /v3/api-docs; si no responde, el build falla.
param(
    [string]$Jar        = "ferresys-api\target\FerreSys-0.0.1-SNAPSHOT.jar",
    # Carpeta de despliegue: la define el entorno de Jenkins (FERRESYS_DEPLOY_DIR) o se usa C:\ferresys-deploy
    [string]$DeployDir  = $(if ($env:FERRESYS_DEPLOY_DIR) { $env:FERRESYS_DEPLOY_DIR } else { "C:\ferresys-deploy" }),
    [int]   $Port       = 8081,
    [int]   $TimeoutSec = 90
)
$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force $DeployDir | Out-Null
$DeployDir = (Resolve-Path $DeployDir).Path
$pidFile = Join-Path $DeployDir "ferresys.pid"
$logFile = Join-Path $DeployDir "ferresys.log"
$target  = Join-Path $DeployDir "ferresys-api.jar"

# 1. Detener la version anterior
function Stop-Instancia([int]$Id, [string]$Motivo) {
    if (Get-Process -Id $Id -ErrorAction SilentlyContinue) {
        Write-Host "Deteniendo la instancia anterior (PID $Id, $Motivo)"
        Stop-Process -Id $Id -Force
        Start-Sleep -Seconds 2
    }
}
if (Test-Path $pidFile) {
    Stop-Instancia ([int](Get-Content $pidFile)) "segun ferresys.pid"
    Remove-Item $pidFile -Force
}
Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
    Select-Object -ExpandProperty OwningProcess -Unique |
    ForEach-Object { Stop-Instancia $_ "ocupaba el puerto $Port" }

# 2. Copiar el artefacto
Copy-Item $Jar $target -Force
Write-Host "Artefacto copiado a $target"

# 3. Arrancar en segundo plano.
#    Se crea via WMI (Win32_Process.Create) y no con Start-Process: asi el java NO hereda
#    los handles de salida del paso "powershell" de Jenkins. Con Start-Process el paso
#    se quedaba esperando para siempre aunque el script ya hubiera terminado.
#    El log lo escribe Spring Boot directamente (logging.file.name).
$java = (Get-Command java).Source
$cmdLine = "`"$java`" -jar `"$target`" --spring.profiles.active=ci --server.port=$Port --logging.file.name=`"$logFile`""
$res = Invoke-CimMethod -ClassName Win32_Process -MethodName Create `
    -Arguments @{ CommandLine = $cmdLine; CurrentDirectory = $DeployDir }
if ($res.ReturnValue -ne 0) {
    Write-Host "No se pudo arrancar la API (Win32_Process.Create devolvio $($res.ReturnValue))"
    exit 1
}
$apiPid = [int]$res.ProcessId
$apiPid | Set-Content $pidFile
Write-Host "FerreSys API arrancando (PID $apiPid) en el puerto $Port"

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
    if (-not (Get-Process -Id $apiPid -ErrorAction SilentlyContinue)) { break }
}
Write-Host "La API no respondio en $TimeoutSec s. Ultimas lineas del log:"
if (Test-Path $logFile) { Get-Content $logFile -Tail 40 }
exit 1
