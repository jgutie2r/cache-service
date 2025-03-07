pipeline{
    agent none
    environment{
       REGISTRY="routerdi1315.uv.es:33443"
    }
    stages{
        // This stage sets some environment variables and compiles the project
        stage('Compile'){
            // This will be run in a server that provides docker
            // in a container with maven
            agent{
                docker {image 'maven:3.9.6-eclipse-temurin-17'}
            }
            steps{
                script{
                   // Get name of the application from pom.xml
                   checkout scm
                   def pomFile = 'pom.xml'
                   def pom = readMavenPom file: pomFile
                   env.APP_NAME = pom.artifactId

                   // Get HASH of last git commit
                   env.HASH = sh (script: "git log -n 1 --pretty=format:'%H'", returnStdout: true)
                }
                // Just to show different syntax
                echo "${env.APP_NAME}"
                echo "${env.HASH}"
                echo "${HASH}"
                sh 'mvn -B -DskipTests clean compile'
            }
        }
        stage('Test'){
            // This will be run in a server that provides docker
            // in a container with maven
            agent{
                docker {image 'maven:3.9.6-eclipse-temurin-17'}
            }
            steps{
                sh 'mvn clean test'
            }
        }
        stage('Build jar'){
            agent{
                docker {image 'maven:3.9.6-eclipse-temurin-17'}
            }
            steps{
                sh 'mvn clean package'
                stash includes: 'target/**,pom.xml,deployment/**', name: 'ARTEFACT'
            }
        }
        // This will be executed in the controller as it has docker
        stage('Build and push docker image'){
            agent any
            steps{
                unstash 'ARTEFACT'

                // Update pom version with git last commit HASH
                script{
                   def pomFile = 'pom.xml'
                   def pom = readMavenPom file: pomFile
                   pom.version = "${env.HASH}"
                   writeMavenPom file: pomFile, model: pom
                   sh 'cat pom.xml'
                   withCredentials([usernamePassword(credentialsId: 'routerdi-registry', passwordVariable: 'REG_PASSWD', usernameVariable: 'REG_USER')]) {
                        sh "docker login -u $REG_USER -p $REG_PASSWD ${env.REGISTRY}"
                        // assumes maven is configured to use the environment variables
                        sh 'mvn docker:build'
                        sh 'mvn docker:push'
                        sh 'mvn docker:removeImage'
                    }
                }
            }
        }
        stage('Deploy to kubernetes'){
            // This will be run in a server that provides docker
            // in an container with kubectl
            agent{
                docker { image 'alpine/k8s:1.30.0' }
            }
            steps{
                script{
                    checkout scm
                    sh '''
                       sed -i -e "s/TAG/$HASH/g" ./deployment/k8s/app.yaml
                       cat ./deployment/k8s/app.yaml
                    '''
                    withCredentials([file(credentialsId: 'kubeConfig', variable: 'KUBECONFIG')]) {
                        sh '''
                           kubectl --kubeconfig "${KUBECONFIG}" apply -f ./deployment/k8s/app.yaml
                        '''
                    }
                }
            }
        }

    }
}
