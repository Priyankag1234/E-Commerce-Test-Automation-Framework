// ============================================================
// Jenkinsfile — Declarative Pipeline
// E-Commerce Automation Framework
// ============================================================
// Stages:
//   1. Checkout
//   2. Compile
//   3. API Tests
//   4. UI Tests (Selenium Grid via Docker)
//   5. Publish Reports
//   6. Notify
// ============================================================

pipeline {
    agent any

    // ─────────────────────────────────────────
    // Pipeline Parameters (visible in Jenkins UI)
    // ─────────────────────────────────────────
    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['qa', 'uat', 'prod'],
            description: 'Target environment for test execution'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser to run UI tests on'
        )
        string(
            name: 'TAGS',
            defaultValue: 'regression',
            description: 'JUnit 5 tags to run (e.g., smoke, regression, api, ui)'
        )
        booleanParam(
            name: 'RUN_ON_GRID',
            defaultValue: true,
            description: 'Run tests on Selenium Grid (Docker)'
        )
    }

    // ─────────────────────────────────────────
    // Environment Variables
    // ─────────────────────────────────────────
    environment {
        JAVA_HOME = tool 'JDK21'
        MAVEN_HOME = tool 'Maven3'
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"
        GRID_URL = 'http://localhost:4444'
        MAVEN_OPTS = '-Xmx2g -XX:+UseG1GC'
    }

    // ─────────────────────────────────────────
    // Pipeline Stages
    // ─────────────────────────────────────────
    stages {

        stage('🔍 Checkout') {
            steps {
                echo "Checking out branch: ${env.GIT_BRANCH}"
                checkout scm
                sh 'git log -1 --oneline'
            }
        }

        stage('⚙️ Compile') {
            steps {
                echo "Compiling project with Java ${env.JAVA_HOME}"
                sh 'mvn compile test-compile -B --no-transfer-progress'
            }
        }

        stage('🐳 Start Selenium Grid') {
            when {
                expression { params.RUN_ON_GRID == true }
            }
            steps {
                echo 'Starting Selenium Grid via Docker Compose...'
                sh 'docker-compose -f docker/docker-compose.yml up -d selenium-hub chrome-node firefox-node edge-node'
                // Wait for grid to be healthy
                sh '''
                    echo "Waiting for Selenium Grid to be ready..."
                    for i in $(seq 1 30); do
                        STATUS=$(curl -s http://localhost:4444/wd/hub/status | python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('"'"'value'"'"',{}).get('"'"'ready'"'"', False))" 2>/dev/null)
                        if [ "$STATUS" = "True" ]; then
                            echo "Grid ready!"
                            break
                        fi
                        echo "Attempt $i/30 - Grid not ready yet..."
                        sleep 5
                    done
                '''
            }
        }

        stage('🌐 API Tests') {
            steps {
                echo 'Running API Tests...'
                sh """
                    mvn clean test \\
                        -Denv=${params.ENVIRONMENT} \\
                        -Dgroups=api \\
                        -B --no-transfer-progress
                """
            }
            post {
                always {
                    echo 'API test stage complete'
                }
            }
        }

        stage('🖥️ UI Tests') {
            steps {
                echo "Running UI Tests | Browser: ${params.BROWSER} | Tags: ${params.TAGS}"
                sh """
                    mvn test \\
                        -Denv=${params.ENVIRONMENT} \\
                        -Dbrowser=${params.BROWSER} \\
                        -Dheadless=true \\
                        -Dexecution=${params.RUN_ON_GRID ? 'remote' : 'local'} \\
                        -DgridUrl=${GRID_URL} \\
                        -Dgroups=${params.TAGS} \\
                        -B --no-transfer-progress
                """
            }
        }

        stage('📊 Publish Reports') {
            steps {
                echo 'Publishing test reports...'

                // Publish HTML Extent Report
                publishHTML(target: [
                    allowMissing         : true,
                    alwaysLinkToLastBuild: true,
                    keepAll              : true,
                    reportDir            : 'test-output/reports',
                    reportFiles          : 'ExtentReport.html',
                    reportName           : 'Extent Test Report',
                    reportTitles         : 'E-Commerce Automation Results'
                ])

                // Archive logs and screenshots as artifacts
                archiveArtifacts artifacts: 'test-output/**/*,logs/**/*',
                                 allowEmptyArchive: true,
                                 fingerprint: true
            }
        }
    }

    // ─────────────────────────────────────────
    // Post-Pipeline Actions
    // ─────────────────────────────────────────
    post {
        always {
            echo 'Stopping Selenium Grid...'
            sh 'docker-compose -f docker/docker-compose.yml down --remove-orphans || true'
            cleanWs()
        }
        success {
            echo "✅ Pipeline PASSED for ${params.ENVIRONMENT} on ${params.BROWSER}"
        }
        failure {
            echo "❌ Pipeline FAILED — check Extent Report and logs for details"
        }
        unstable {
            echo "⚠️ Pipeline UNSTABLE — some tests failed"
        }
    }
}
