pipeline {
    agent { label 'hetzner-01' }
    stages {
        stage('Checkout') {
            steps {
                git branch: APP_GIT_BRANCH, url: APP_GIT_REPO, credentialsId: '18709ffb-f1ba-40cf-9c1e-4c6d1bcae8ab'
            }
        }
        stage('Images build and push') {
            steps {
                script{
					docker.withRegistry("https://538721144237.dkr.ecr.eu-west-1.amazonaws.com", "ecr:eu-west-1:678abf49-5846-440e-8d7c-a0a9a8b49a0d") {
						docker.build('jibbix' , '--file jibbix_Inovus/Dockerfile --build-arg JIBBIX_INOVUS_JAR_FILE=${JIBBIX_INOVUS_JAR_FILE} .').push('Inovus')
						docker.build('jibbix' , '--file jibbix/Dockerfile --build-arg JIBBIX_FFOMS_JAR_FILE=${JIBBIX_FFOMS_JAR_FILE} .').push('ffoms')
						docker.build('jibbix' , '--file emitter/Dockerfile --build-arg EMITTER_JAR_FILE=${EMITTER_JAR_FILE} .').push('emitter')
					}
				}
			}
		}
        stage('Docker stack deploy') {
            agent { label 'Docker-node' }
            steps {
                script{
                        git branch: APP_GIT_BRANCH, url: APP_GIT_REPO, credentialsId: '18709ffb-f1ba-40cf-9c1e-4c6d1bcae8ab'
                        sh 'ls'
                        sh 'docker stack deploy --with-registry-auth --compose-file docker-compose.yml jibbix'
        		}
        	}
        }
    }
    post {
      always {
          deleteDir()
      }
    }
}
