Kubernetes manifests for all microservices, databases, frontend, and monitoring stack have been created and organized in 

k8s/
 (as well as mirrored in the project root).

Manifest Structure
k8s/
├── databases.yaml           # MySQL (User, Product, Order) & MongoDB (Cart) StatefulSets + PVCs + Services
├── microservices.yaml       # Eureka, API Gateway, Auth, User, Product, Order, Cart Deployments & Services
├── frontend-monitoring.yaml # Angular UI (Shopify App), Prometheus (Scraping Actuator), Grafana
└── README.md                # Detailed step-by-step deployment guide
1. Database Specifications (

k8s/databases.yaml
)
user-mysql: MySQL 8.0 StatefulSet with 2Gi PersistentVolumeClaim (Port 3306).
product-mysql: MySQL 8.0 StatefulSet with 2Gi PersistentVolumeClaim (Port 3306).
order-mysql: MySQL 8.0 StatefulSet with 2Gi PersistentVolumeClaim (Port 3306).
cart-mongo: MongoDB 6.0 StatefulSet with 2Gi PersistentVolumeClaim (Port 27017).
2. Backend Microservices (

k8s/microservices.yaml
)

Includes health probes (/actuator/health readiness and liveness), CPU/memory limits, Eureka discovery configuration, and cluster networking:

Service	Type	Port	Description
discovery-service	ClusterIP	8082	Eureka Service Discovery Registry
api-gateway-service	NodePort	8081 (NodePort: 30081)	External API Gateway entrypoint
auth-service	ClusterIP	8083	Authentication & JWT token generation
cart-service	ClusterIP	8084	Cart management connected to cart-mongo
user-service	ClusterIP	8085	User profiles connected to user-mysql
product-service	ClusterIP	8086	Product catalog connected to product-mysql
order-service	ClusterIP	8087	Order processing connected to order-mysql
3. Frontend & Monitoring (

k8s/frontend-monitoring.yaml
)
angular-ui: Angular Productify App frontend served via Nginx on NodePort: 30090.
prometheus: ConfigMap and Deployment scraping Spring Boot Actuator metrics (/actuator/prometheus) on NodePort: 30091.
grafana: Visualization dashboards on NodePort: 30300 (Default credentials: admin / admin).
Deployment Commands
Step 1: Build Local Docker Images


bash
docker build -t discovery-service:latest ./discovery-service
docker build -t api-gateway-service:latest ./api-gateway-service
docker build -t auth-service:latest ./auth-service
docker build -t user-service:latest ./user-service
docker build -t product-service:latest ./product-service
docker build -t order-service:latest ./order-service
docker build -t cart-service:latest ./cart-service
docker build -t productify-app:latest ./productify-app
(If using Minikube, run eval $(minikube docker-env) first before building).

Step 2: Apply Kubernetes Manifests
Apply in order (Databases first, then backend microservices, then UI/monitoring):

powershell
# 1. Databases and persistent storage
kubectl apply -f k8s/databases.yaml
# 2. Eureka & Microservices
kubectl apply -f k8s/microservices.yaml
# 3. Angular UI & Monitoring
kubectl apply -f k8s/frontend-monitoring.yaml
Or deploy all at once:

powershell
kubectl apply -f k8s/
Step 3: Verify Pods and Services
powershell
kubectl get pods -w
kubectl get svc
kubectl get pvc


# 1. Start the Minikube cluster
minikube start
# 2. Point local terminal to Minikube's Docker daemon (if building images locally)
minikube -p minikube docker-env --shell powershell | Invoke-Expression
# 3. Deploy Databases (ConfigMaps, Secrets, StatefulSets & PVCs)
kubectl apply -f k8s/databases.yaml
# 4. Deploy Microservices (ConfigMaps, Secrets, Eureka, Gateway, and Backend Services)
kubectl apply -f k8s/microservices.yaml
# 5. Deploy Frontend & Monitoring (Prometheus + Grafana)
kubectl apply -f k8s/frontend-monitoring.yaml
# 6. Monitor pod startup progress
kubectl get pods -w


kubectl port-forward svc/api-gateway-service 8081:8081
kubectl port-forward svc/angular-ui 4200:80
kubectl port-forward svc/prometheus 9090:9090
kubectl port-forward svc/grafana 3000:3000


kubectl logs -l app=auth-service --tail=200

kubectl logs -l app=user-service | Select-String "6adb6432638dba11b30398f8d5c9c997" -Context 5, 20

kubectl logs -l app=auth-service  | Select-String "6adb6432638dba11b30398f8d5c9c997" -Context 5, 20

# ====================================================
# SHUTDOWN & CLEANUP COMMANDS
# ====================================================

# 1. Stop background port-forwards:
# Press Ctrl + C in the terminals running 'kubectl port-forward' and 'kubectl get pods -w'

# 2. Option A: Graceful Shutdown (Recommended - preserves DB data & pods for next time)
minikube stop

# Option B: Delete all apps & databases first, then stop
kubectl delete -f k8s/
minikube stop

# Option C: Complete wipe (Destroys the Minikube cluster and resets everything)
minikube delete

# 1. Rebuild only the api-gateway-service image
docker build -t api-gateway-service:latest ./api-gateway-service

# 2. Deploy Redis
kubectl apply -f databases.yaml

# 3. Update the config and restart the gateway pod
kubectl apply -f microservices.yaml
kubectl rollout restart deployment api-gateway-service


# 2. Rebuild the productify-app image
docker build -t productify-app:latest ./productify-app
# 3. Restart the Angular deployment
kubectl rollout restart deployment angular-ui


# 2. Rebuild the product-service Docker image
docker build -t product-service:latest ./product-service
# 3. Trigger a rolling restart of product-service deployment
kubectl rollout restart deployment product-service
# 4. (Optional) Watch the rollout progress until complete
kubectl rollout status deployment/product-service


Click Select label:
You will now see app, container, pod, and namespace.
Select app as the label.
Click Select value and pick your desired service (e.g. product-service or auth-service).
Click the blue Run query button in the top right.
Useful Example Queries to Try in the "Code" Editor
Switch from Builder to Code mode (toggle in the upper right of the query box):

View logs from a specific service:

logql
{app="product-service"}
View only ERROR logs from any microservice:

logql
{app=~".+"} |= "ERROR"
Trace a single end-to-end request across all microservices using a Correlation ID or Trace ID:

logql
{app=~".+"} |= "your-correlation-id"
Live stream: Click the Live button in the top-right corner to watch logs scroll in real time as requests hit your services.

