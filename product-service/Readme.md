mvn clean install

docker build -t product-service .

docker run -d -p 8086:8086 product-service