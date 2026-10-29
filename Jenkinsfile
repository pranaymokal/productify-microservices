pipeline {
    agent any

    stages {
       stage('Cleanup Docker Images') {
                steps {
                   echo '🧹 Cleaning up Docker containers, volumes, and specific images...'
                   bat '''
                           docker-compose down --volumes --remove-orphans
                           docker rmi -f discovery-service:latest || exit 0
                           docker rmi -f api-gateway-service:latest || exit 0
                           docker rmi -f cart-service:latest || exit 0
                           docker rmi -f auth-service:latest || exit 0
                           docker rmi -f user-service:latest || exit 0
                           docker rmi -f product-service:latest || exit 0
                           docker rmi -f order-service:latest || exit 0
                           docker rmi -f productify-app:latest || exit 0
                       '''
                }
       }
       stage('Build All Services') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }
		
		stage('Build Docker Images') {
            steps {
				bat 'docker build -t discovery-service ./discovery-service'
				bat 'docker build -t api-gateway-service ./api-gateway-service'
				bat 'docker build -t cart-service ./cart-service'
				bat 'docker build -t auth-service ./auth-service'
				bat 'docker build -t user-service ./user-service'
				bat 'docker build -t product-service ./product-service'
				bat 'docker build -t order-service ./order-service'
				bat 'docker build -t productify-app ./productify-app'
            }
        }


        stage('Docker Compose Up') {
            steps {
                bat 'docker-compose up -d --build'
            }
        }
    }
}