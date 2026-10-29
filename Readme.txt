Ubuntu Login : pranaym
Password: [PASSWORD]

MySQL Login: root
Password:root@0990
MySQL PORT: 3306

MongoDB Login:root
Password:root 
MongoDBPort:27

api-gateway-service: 8081
DiscoveryService: 8082
AuthService: 8083
config-server: 8084
userService: 8085
productService: 8086
orderService: 8087
cart-service: 8088
Angular UI: 8090


Service            | PORT   | DB             | DB NAME       | username : password
api-gateway-service| 8081   |                |               | 
DiscoveryService   | 8082   |                |               |
AuthService        | 8083   |                |               |
cart-service       | 8084   | mongodb:27017  | cart-service  | root:root@123
userService        | 8085   | mysql:3306     | user          | root:root@123
productService     | 8086   | mysql:3306     | product       | root:root@123
orderService       | 8087   | mysql:3306     | order         | root:root@123
Angular UI         | 8090   |                |               | 

docker build -t user-service:latest .
docker build -t api-gateway-service:latest .
docker build -t auth-service:latest .
docker build -t cart-service:latest .
docker build -t discovery-service:latest .
docker build -t order-service:latest .
docker build -t product-service:latest .
docker build -t productify-app:latest .


