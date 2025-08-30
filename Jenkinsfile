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
                        withCredentials([usernamePassword(credentialsId: "${container_registry_auth}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        
                            // Generate image version
                            def branchName = (env.GIT_BRANCH =~ /refs\/heads\/(.*)/) ? (env.GIT_BRANCH.split('/').last()) : 'unknown'
                            def gitCommitShort = env.GIT_COMMIT.substring(0, 6)
                            def prefix = (branchName == 'master' || branchName == 'main') ? 'release' : 'feature'
                            def imageVersion = "${prefix}-${gitCommitShort}"

                            def buildPath = sh(script: "dirname ${dockerfile}", returnStdout: true).trim()
                            def image = env.serviceName   // "web-app"

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
                                  --progress=plain -t ${registry}/${image}:${imageVersion} \
                                  --push ${buildPath}
                            """
                        }
                    }
                }
            }
        }


    }
}
