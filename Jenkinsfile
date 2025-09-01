pipeline {

        agent { label 'jenkins-agent' }

        environment {
        // DOCKER_IMAGE = 'registry.hub.docker.com/anantgsaraf/centos-aws-cli-image:1.0.1'
        // PYTHON_SCRIPT = 'ami-creation.py'
        // MAVEN_HOME = tool 'Maven'
        serviceName = 'web-server-example'
        // DOCKER_REGISTRY_CREDENTIALS = credentials('docker-login')
        DOCKER_REGISTRY_CREDENTIALS = 'docker_auth'
        // NEXUS_REPO_URL = '54.152.98.14:8083'
        // ECR_DOCKER_REPO_URL = '576582406082.dkr.ecr.us-east-1.amazonaws.com'
        ECR_DOCKER_REPO_URL = 'docker.io'
        image_prefix = 'anantgsaraf'
        AWS_DEFAULT_REGION = 'us-east-1'
        dockerfile = "Dockerfile"
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

        stage('Configure AWS') {
            steps {
                withCredentials([[
                                    $class: 'AmazonWebServicesCredentialsBinding',
                                    credentialsId: "AWS-CREDENTIALS",
                                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                                ]]){
                                    
                    // Your AWS-related steps here
                    //sh 'aws s3 ls' // Example AWS CLI command
                    echo "AWS_ACCESS_KEY_ID: ${AWS_ACCESS_KEY_ID}"
                    echo "AWS_SECRET_ACCESS_KEY: ${AWS_SECRET_ACCESS_KEY}"  
                    sh 'aws ec2 describe-regions --all-regions --output table'
                }
            }
        }

        stage('Maven Clean') {
            steps {
                sh "mvn clean"
            }
        }

        stage('Maven Package') {
            steps {
                sh "mvn package"
            }
        }

        // stage('Docker Build') {
        //     steps {
        //         sh "docker build -t web-server-example ."
        //     }
        // }


        // stage('Build and Push Docker Image') {
        //     steps {
        //         script {
        //             // Build the Docker image
        //             sh "docker build -t ${DOCKER_IMAGE} ."

        //             // Tag the Docker image with Nexus repository URL
        //             sh "docker tag ${DOCKER_IMAGE}:latest ${NEXUS_REPO_URL}/${DOCKER_IMAGE}:latest"

        //             // Login to Nexus repository using Jenkins credentials
        //             sh "docker login -u ${DOCKER_REGISTRY_CREDENTIALS_USR} -p ${DOCKER_REGISTRY_CREDENTIALS_PSW} ${NEXUS_REPO_URL}"

        //             // Push the Docker image to Nexus
        //             sh "docker push ${NEXUS_REPO_URL}/${DOCKER_IMAGE}:latest"
        //         }
        //     }
        // }

        stage('Build and Push Docker Image to ecr') {
            steps {
                    script {
                        withCredentials([usernamePassword(credentialsId: "${DOCKER_REGISTRY_CREDENTIALS}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        
                            // Generate image version
                            def branchName = (env.GIT_BRANCH =~ /refs\/heads\/(.*)/) ? (env.GIT_BRANCH.split('/').last()) : 'unknown'
                            def gitCommitShort = env.GIT_COMMIT.substring(0, 6)
                            def prefix = (branchName == 'master' || branchName == 'main') ? 'release' : 'feature'
                            def imageVersion = "${prefix}-${gitCommitShort}"

                            def buildPath = sh(script: "dirname ${dockerfile}", returnStdout: true).trim()
                            def image = env.serviceName   // "web-app"
                            def image_prefix = env.image_prefix  // "anantgsaraf"

                            sh "docker buildx create --use --bootstrap --driver docker-container"
                            // sh "nslookup index.docker.io"
                            // sh "ping -c 3 index.docker.io"

                            // Add debug output
                            sh """
                                echo "Debug: DOCKER_USER = $DOCKER_USER"
                                echo "Debug: Registry = ${registry}"
                                echo "Debug: Credentials ID = ${container_registry_auth}"
                            """

                            // Test authentication first
                            sh """
                                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin ${registry}

                                # Verify login worked
                                docker info | grep -i username

                                # Test if we can pull a public image
                                docker pull hello-world
                            """

                            // ✅ use """ so Groovy variables expand
                            sh """
                                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin ${registry}
                                docker buildx build --platform ${platform} -f ${dockerfile} \
                                  --progress=plain -t ${registry}/${image_prefix}/${image}:${imageVersion} \
                                  --push ${buildPath}
                            """
                        }
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
