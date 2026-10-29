mvn clean install

docker build -t user-service .

docker run -d -p 8085:8085 user-service