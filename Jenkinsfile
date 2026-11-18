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
            script { notificar('ÉXITO') }
        }
        failure {
            script { notificar('FALLÓ') }
        }
    }
}

// Notificación del estado del pipeline (canal pendiente de definir: email / Discord / Slack).
def notificar(String estado) {
    def msg = "FerreSys #${env.BUILD_NUMBER} (${env.BRANCH_NAME ?: 'main'}): ${estado}\n${env.BUILD_URL}"
    echo msg
}
