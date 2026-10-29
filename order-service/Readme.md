mvn clean install

docker build -t order-service .

docker run -d -p 8087:8087 order-service