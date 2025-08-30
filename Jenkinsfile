@Library('jenkins-shared-libraries@master')_
pipeline {

    agent {
        kubernetes {
            yaml libraryResource('jenkins/agents/agent-docker-hub.yaml')
        }
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        serviceName = 'web-app'
        // registry = 'nexus.theguru.in.net:8082'
        registry = 'docker.io/anantgsaraf'
        image_prefix = 'anantgsaraf'
        // container_registry_auth = "nexus-auth"
        container_registry_auth = "docker_auth"
        // version="1"
        platform = "linux/amd64,linux/arm64"

        cosign_key = "slm-cosign-key"
        cosign_pub = "slm-cosign-pub"
        cosign_key_pass = "slm-cosign-password"
        dockerfile = "Dockerfile"
    }

    stages {

        stage('Docker Login') {
            steps {
                container('docker-build') {                
                    script {
                        withCredentials([usernamePassword(credentialsId: "${container_registry_auth}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                            version = env.GIT_BRANCH.replaceAll("/", "-") + '-' + 'snapshot' + "-" + env.GIT_COMMIT.take(6)
                            version = version.toLowerCase()
                            sh """
                            echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin ${registry}
                            echo $version
                            """
                            // echo test@1234 | docker login -u anantgsaraf --password-stdin ${registry}
                        }
                    }
                }    
            }
        }

        stage('Maven Clean') {
            steps {
                container('maven') {
                    script {
                        sh "mvn clean"
                    }
                }
            }
        }

        stage('Maven Package') {
            steps {
                container('maven') {
                    script {
                        sh "mvn package"
                    }
                }
            }
        }

        stage('Determine Image Version') {
            steps {
                container('docker-build') {
                    script {

                        // Define the function to generate the image version
                        sh 'sleep 20'

                        withCredentials([usernamePassword(credentialsId: "${container_registry_auth}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                            version = env.GIT_BRANCH.replaceAll("/", "-") + '-' + 'snapshot' + "-" + env.GIT_COMMIT.take(6)
                            version = version.toLowerCase()
                            sh """
                            echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin ${registry}
                            echo $version
                            """
                            // echo test@1234 | docker login -u anantgsaraf --password-stdin ${registry}

                        def getImageVersion = {
                            // Extract the branch name from env.GIT_BRANCH
                            def branchName = (env.GIT_BRANCH =~ /refs\/heads\/(.*)/) ? (env.GIT_BRANCH.split('/').last()) : 'unknown'

                            // Extract the last 5 characters of the Git commit hash
                            def gitCommitShort = env.GIT_COMMIT.substring(0, 5)

                            // Determine the prefix based on the branch name
                            def prefix = (branchName == 'master' || branchName == 'main') ? 'release' : 'feature'

                            // Combine prefix and short commit hash
                            return "${prefix}-${gitCommitShort}"
                        }

                        // Generate and log the image version
                        def imageVersion = getImageVersion()
                        echo "Image version: ${imageVersion}"

                        // Resolve build path from dockerfile
                        def buildPath = sh(script: "dirname ${dockerfile}", returnStdout: true).trim()
                        echo "Build path: ${buildPath}"
                        def image = env.serviceName
                        def image_prefix = env.image_prefix
                        
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin ${registry}

                        sh "docker buildx create --use --bootstrap --driver docker-container"

                        // Build and push the Docker image using buildx
                        sh """
                            docker buildx build --platform ${platform} -f ${dockerfile} -t ${registry}/${image}:${imageVersion} --push ${buildPath}
                        """
                        // docker buildx build --platform ${platform} -f ${dockerfile} -t ${registry}/${image}:${imageVersion} --push ${buildPath}


                        }


                        // // Define the function to generate the image version
                        // sh 'sleep 20'

                        // def getImageVersion = {
                        //     // Extract the branch name from env.GIT_BRANCH
                        //     def branchName = (env.GIT_BRANCH =~ /refs\/heads\/(.*)/) ? (env.GIT_BRANCH.split('/').last()) : 'unknown'

                        //     // Extract the last 5 characters of the Git commit hash
                        //     def gitCommitShort = env.GIT_COMMIT.substring(0, 5)

                        //     // Determine the prefix based on the branch name
                        //     def prefix = (branchName == 'master' || branchName == 'main') ? 'release' : 'feature'

                        //     // Combine prefix and short commit hash
                        //     return "${prefix}-${gitCommitShort}"
                        // }

                        // // Generate and log the image version
                        // def imageVersion = getImageVersion()
                        // echo "Image version: ${imageVersion}"

                        // // Resolve build path from dockerfile
                        // def buildPath = sh(script: "dirname ${dockerfile}", returnStdout: true).trim()
                        // echo "Build path: ${buildPath}"
                        // def image = env.serviceName
                        // def prefix = env.image_prefix

                        // sh "docker buildx create --use --bootstrap --driver docker-container"

                        // // Build and push the Docker image using buildx
                        // sh """
                        //     docker buildx build --platform ${platform} -f ${dockerfile} -t ${prefix}/${image}:${imageVersion} --push ${buildPath}
                        // """
                        // // docker buildx build --platform ${platform} -f ${dockerfile} -t ${registry}/${image}:${imageVersion} --push ${buildPath}

                    }
                }
            }
        }

    }
}
