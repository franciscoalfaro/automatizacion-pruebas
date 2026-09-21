// ============================================================
//  Jenkinsfile - Pipeline de CI/CD del proyecto ta_ex_7
//  Examen Final - Automatizacion de Pruebas
//  Autor: Francisco Alfaro
//
//  Stages:
//    1. Checkout            - obtiene el codigo desde Git
//    2. Build               - compila con Maven
//    3. Unit Tests          - pruebas unitarias (JUnit 5)
//    4. Integration Tests   - pruebas de integracion (HTTP)
//    5. Acceptance Tests    - pruebas de aceptacion (Selenium)
//    6. Deploy to Staging   - despliegue en ambiente de prueba
//    7. Smoke Test          - verifica que la app responde
//    8. Rollback            - revierte si el smoke test falla
// ============================================================

pipeline {
    agent any

    tools {
        // Requiere configurar 'maven-3.9' y 'jdk-17' en Jenkins > Global Tool Configuration
        maven 'maven-3.9'
        jdk 'jdk-17'
    }

    environment {
        APP_PORT      = '8081'
        STAGING_DIR   = 'staging'
        ARTIFACT      = 'target/ta_ex_7-1.0.0.jar'
        // Ruta del driver de Edge (se sobrescribe en el agente si aplica)
        EDGE_DRIVER   = "${WORKSPACE}/temp/edgedriver/msedgedriver.exe"
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                echo '=== Obteniendo codigo fuente ==='
                checkout scm
                sh 'git log --oneline -5 || true'
            }
        }

        stage('Build') {
            steps {
                echo '=== Compilando el proyecto ==='
                sh 'mvn -B clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                echo '=== Ejecutando pruebas unitarias (JUnit 5) ==='
                sh 'mvn -B test'
            }
            post {
                always {
                    // Publica el reporte JUnit de las pruebas unitarias
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo '=== Ejecutando pruebas de integracion ==='
                sh 'mvn -B failsafe:integration-test failsafe:verify -Dit.test=ServidorWebIT'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/failsafe-reports/*.xml'
                }
            }
        }

        stage('Acceptance Tests') {
            steps {
                echo '=== Ejecutando pruebas de aceptacion (Selenium) ==='
                sh 'mvn -B failsafe:integration-test failsafe:verify -Dit.test=CalculadoraAcceptanceTest'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/failsafe-reports/*.xml'
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                echo '=== Desplegando en el ambiente de prueba ==='
                sh 'chmod +x scripts/deploy.sh && ./scripts/deploy.sh'
            }
        }

        stage('Smoke Test') {
            steps {
                echo '=== Verificando que la aplicacion responde ==='
                sh 'chmod +x scripts/smoke-test.sh && ./scripts/smoke-test.sh'
            }
        }
    }

    post {
        success {
            echo '=== Pipeline completado con exito ==='
        }
        failure {
            echo '=== Pipeline fallido: ejecutando rollback ==='
            sh 'chmod +x scripts/rollback.sh && ./scripts/rollback.sh || true'
        }
        always {
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            echo '=== Pipeline finalizado ==='
        }
    }
}
