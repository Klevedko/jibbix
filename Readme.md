Application's job is to:
 1. Scan Zabbix server
 2. Find out critical triggers and them last events
 3. Determine hosts and resolve {HOST.NAME} template
 4. Try to find an issue in Jira ( basing on Project and EventID )
 5. Create an issue if it does not exists!
 
 The application wrapped in Docker Container.
 Jenkins Item checkout from Git.
 Build a project using Assembly plugin.
 Build an image and push it as latest version to the amazon with Pipeline.
 Then deploy an image on Linux machine with running Docker.
 
 ---How to use it ? ---
 The ".env" file contains all important parameters.
 The Dockerfile describes layers.
 The docker-compose file manages image and includes .env file
 
 (  
     Application also may be used with config.properties and readProperties() method without Container.
     Just create a jar with mvn: assembly and start it.
 )
 
 Logging into "logs" folder. ( need to be improved)
 
 Default Jenkins' Item variables:
 ```bash
    APP_GIT_BRANCH=master
    APP_GIT_REPO=https://github.com/Klevedko/jibbix.git
    DEPLOY_TO_ARTIFACTORY=false
    JAR_FILE=com.jibbix-1.0-SNAPSHOT-jar-with-dependencies.jar
 ```
 
 Default Jenkins' Item Pipeline script:
 ```bash
 pipeline {
     agent none
     options {
         timestamps()
     }
 
     environment {
         TAG = "${env.JOB_BASE_NAME.replace(".", "")}-${env.BUILD_ID}"
         MAVEN_OPTS = '-Xmx2048m -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dfile.encoding=utf-8 -Dmaven.multiModuleProjectDirectory'
     }
 
     stages {
         stage('Checkout') {
 			agent any
             steps {
                 git branch: APP_GIT_BRANCH, url: APP_GIT_REPO, credentialsId: '18709ffb-f1ba-40cf-9c1e-4c6d1bcae8ab'
             }
         }
 
         stage('Build project') {
 			agent any
             tools {
                 jdk 'OpenJDK_latest'
                 maven 'default'
             }
             steps {
                 sh 'mvn clean assembly:assembly -DskipTests=true'
 				archiveArtifacts artifacts: '**/target/*.*'
             }
         }
         stage('Docker image build') {
 		    agent any
             steps {
                 script{
 					docker.withRegistry("https://538721144237.dkr.ecr.eu-west-1.amazonaws.com", "ecr:eu-west-1:678abf49-5846-440e-8d7c-a0a9a8b49a0d") {
 						docker.build('jibbix' , '--build-arg JAR_FILE=${JAR_FILE} .').push('latest') 
 					}
 				}
 			}
 		}
         stage('Docker image deploy') {
             agent { label 'Docker-node' }
             steps {
                 script{
                         git branch: APP_GIT_BRANCH, url: APP_GIT_REPO, credentialsId: '18709ffb-f1ba-40cf-9c1e-4c6d1bcae8ab'
                         // sh 'docker pull 538721144237.dkr.ecr.eu-west-1.amazonaws.com/jibbix:latest'
                         // sh 'docker-compose -p jibbix up -d'
                         sh 'docker stack deploy --with-registry-auth --compose-file docker-compose.yml jibbix'
         		}
         	}
         }
 	}
 }
 ```