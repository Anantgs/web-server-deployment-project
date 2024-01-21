pipeline {
    agent any

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

                    // Clone GitLab repository
                    withCredentials([usernamePassword(credentialsId: gitLabCredentialsId)]) {
                        git branch: 'main', url: gitLabRepoUrl, credentialsId: gitLabCredentialsId
                    }

                    sh "cd ${env.WORKSPACE} ; ls -l"

                }
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
