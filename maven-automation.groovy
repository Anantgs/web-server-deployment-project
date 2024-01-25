pipeline {

        agent { label 'jenkins-agent' }

        environment {
        // DOCKER_IMAGE = 'registry.hub.docker.com/anantgsaraf/centos-aws-cli-image:1.0.1'
        // PYTHON_SCRIPT = 'ami-creation.py'
        MAVEN_HOME = tool 'Maven'
        DOCKER_IMAGE = 'web-server-example'
        DOCKER_REGISTRY_CREDENTIALS = credentials('docker-login')
        NEXUS_REPO_URL = 'http://54.152.98.14:8083'
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

        // stage('Docker Build') {
        //     steps {
        //         sh "docker build -t web-server-example ."
        //     }
        // }


        stage('Build and Push Docker Image') {
            steps {
                script {
                    // Build the Docker image
                    sh "docker build -t ${DOCKER_IMAGE} ."

                    // Tag the Docker image with Nexus repository URL
                    sh "docker tag ${DOCKER_IMAGE}:latest ${NEXUS_REPO_URL}/${DOCKER_IMAGE}:latest"

                    // Login to Nexus repository using Jenkins credentials
                    sh "docker login -u ${DOCKER_REGISTRY_CREDENTIALS_USR} -p ${DOCKER_REGISTRY_CREDENTIALS_PSW} ${NEXUS_REPO_URL}"

                    // Push the Docker image to Nexus
                    sh "docker push ${NEXUS_REPO_URL}/${DOCKER_IMAGE}:latest"
                }
            }
        }

        // stage('Docker Run') {
        //     steps {
        //         sh "docker run -p 8080:8080 -d web-server-example"
        //     }
        // }

    }

    post {
        always {
            // Clean up
            cleanWs()
        }
    }
}
