pipeline {
    parameters {
        choice(name: 'ACTION', choices: ['create', 'delete'], description: 'Select the action to perform')
    }
    
    agent {
        label 'jenkins'
    }

    environment {
        // Use Jenkins credentials for AWS
        //def AWS_ACCESS_KEY_ID     = aws-access-key-id
        //def AWS_SECRET_ACCESS_KEY = aws-secrete-key-id
        AWS_DEFAULT_REGION    = 'us-east-1' // Replace with your AWS region
    }

    stages {
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
                }
            }
        }

        // stage('Checkout') {
        //     steps {
        //         script {
        //             // Clone your GitLab repository using global credentials
        //             git credentialsId: 'gitlab-credentials-id', url: 'https://gitlab.com/devops5113843/terraform-eks.git', clean: true
        //         }
        //     }
        // }

        stage('Kube Init') {
            steps {
                script {
                    // Run Terraform init
                  sh 'ls -l'
                  dir('Kubernetes-deployments') {  
                    sh 'ls -l'
                    sh 'aws eks update-kubeconfig --region us-east-1 --name my-eks-cluster'
                    // sh 'kubectl create ns test'
                    // sh 'kubectl create secret docker-registry ecr-secret \
                    //     --docker-server=576582406082.dkr.ecr.us-east-1.amazonaws.com/docker-repository:latest \
                    //     --docker-username=AWS \
                    //     --docker-password="$(aws ecr get-login-password --region us-east-1)" \
                    //     --docker-email=none@example.com -n test'
                  } 
                }
            }
        }

        stage('Kube Action') {
            steps {
                script {
                    dir('Kubernetes-deployments') {  
                            // Determine the Terraform action based on the selected parameter
                            def kubeAction = params.ACTION == 'create' ? 'create' : 'delete'

                            // Run Terraform apply or destroy
                            // sh "kubectl ${kubeAction} -f load-balancer-service.yaml -n test"
                            sh "kubectl ${kubeAction} ns test"
                            sh "kubectl \${kubeAction} secret docker-registry ecr-secret \
                                --docker-server=576582406082.dkr.ecr.us-east-1.amazonaws.com/docker-repository:latest \
                                --docker-username=AWS \
                                --docker-password='$(aws ecr get-login-password --region us-east-1)' \
                                --docker-email=none@example.com -n test"                            
                            sh "kubectl ${kubeAction} -f deployment.yaml -n test"
                            // sh "kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/aws/deploy.yaml -n ingress-nginx"
                            // sh "kubectl ${kubeAction} -f web-app-ingress.yaml -n test"
                            sh "kubectl ${kubeAction} -f web-app-svc.yaml -n test" 

                            if (kubeAction == 'create') {
                                sh "kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/aws/deploy.yaml -n ingress-nginx"
                            } else {
                                sh "kubectl delete -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/aws/deploy.yaml -n ingress-nginx"
                            }                            

                    }    
                }
            }
        }
    }

    post {
        success {
            echo 'Terraform deployment successful!'
        }
        failure {
            echo 'Terraform deployment failed!'
        }
    }
}
