pipeline {
    agent jenkins

        environment {
        DOCKER_IMAGE = 'registry.hub.docker.com/anantgsaraf/centos-aws-cli-image:1.0.1'
        PYTHON_SCRIPT = 'ami-creation.py'
    }


    stages {

        stage('Clone from GitLab') {
            steps {
                script {
                    // Define GitLab repository and credentials
                    def gitLabRepoUrl = 'https://gitlab.com/devops5113843/web-server-deployment-project.git'
                    def gitLabCredentialsId = 'gitlab-credentials-id'
                    sh "echo ${gitLabCredentialsId}"

                    // Clone GitLab repository
                    //withCredentials([usernamePassword(credentialsId: gitLabCredentialsId, usernameVariable: 'GITLAB_USERNAME', passwordVariable: 'GITLAB_PASSWORD' )]) {
                    //    git branch: 'main', url: gitLabRepoUrl, credentialsId: 'gitLabCredentialsId'
                    //}

                    sh "cd ${env.WORKSPACE} ; ls -l"

                }
            }
        }

        stage('Maven Clean') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean"
            }
        }

        stage('Maven Package') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn package"
            }
        }

        stage('Maven Exec:Java') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn exec:java"
            }
        }

    }

    post {
        always {
            // Clean up
            cleanWs()
        }
    }
}
