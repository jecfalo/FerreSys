// Pipeline CI/CD de FerreSys (API Spring Boot).
// Etapas: Checkout -> Build -> Test -> Package -> Deploy (servidor local, perfil "ci").
// Se dispara automáticamente con cada push a GitHub (webhook) y notifica el resultado.
pipeline {
    agent any

    triggers {
        // Requiere el plugin "GitHub" y un webhook en el repo apuntando a <jenkins>/github-webhook/
        githubPush()
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 20, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    environment {
        API_DIR     = 'ferresys-api'
        DEPLOY_PORT = '8081'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                bat 'git log -1 --pretty=format:"%%h %%an: %%s"'
            }
        }

        stage('Build') {
            steps {
                dir(env.API_DIR) {
                    bat 'mvnw.cmd -B clean compile'
                }
            }
        }

        stage('Test') {
            steps {
                dir(env.API_DIR) {
                    // Pruebas unitarias (Mockito) + prueba de contexto con H2 en memoria
                    bat 'mvnw.cmd -B test'
                }
            }
            post {
                always {
                    junit testResults: "${env.API_DIR}/target/surefire-reports/*.xml", allowEmptyResults: false
                }
            }
        }

        stage('Package') {
            steps {
                dir(env.API_DIR) {
                    bat 'mvnw.cmd -B -DskipTests package'
                }
                archiveArtifacts artifacts: "${env.API_DIR}/target/*.jar", fingerprint: true
            }
        }

        stage('Deploy') {
            environment {
                // Evita que Jenkins mate el proceso de la API cuando termina el build
                JENKINS_NODE_COOKIE = 'dontKillMe'
            }
            steps {
                powershell "& .\\ci\\deploy.ps1 -Port ${env.DEPLOY_PORT}"
            }
        }
    }

    post {
        success {
            script { notificar('ÉXITO', '#2e7d32') }
        }
        failure {
            script { notificar('FALLÓ', '#c62828') }
        }
        fixed {
            echo 'El pipeline volvió a estar en verde.'
        }
    }
}

// Notificación por email (plugin Email Extension + SMTP de Gmail configurado en Jenkins).
// Los destinatarios NO van en el repo: se leen de la variable NOTIFY_TO, definida en
// Administrar Jenkins > System > Global properties (lista separada por comas).
def notificar(String estado, String color) {
    def destinatarios = env.NOTIFY_TO?.trim()
    if (!destinatarios) {
        echo "NOTIFY_TO no está configurada; se omite el email (${estado})."
        return
    }
    emailext(
        to: destinatarios,
        subject: "[FerreSys CI] Build #${env.BUILD_NUMBER}: ${estado}",
        mimeType: 'text/html',
        attachLog: currentBuild.currentResult != 'SUCCESS',
        body: """
            <h2 style="color:${color}">Pipeline FerreSys: ${estado}</h2>
            <p><b>Build:</b> #${env.BUILD_NUMBER} &middot; <b>Duración:</b> ${currentBuild.durationString.replace(' and counting', '')}</p>
            <p><b>Commit:</b> ${env.GIT_COMMIT ?: 'n/d'} &middot; <b>Rama:</b> ${env.GIT_BRANCH ?: 'n/d'}</p>
            <p><b>Pruebas:</b> ver el reporte JUnit del build.</p>
            <p><a href="${env.BUILD_URL}">Abrir el build en Jenkins</a></p>
        """
    )
}
