# Kubernetes Deployment Guide

This directory contains the complete Kubernetes manifests for deploying the entire microservices shopping platform:
- **Databases** (`databases.yaml`): MySQL StatefulSets for User, Product, Order; MongoDB for Cart
- **Backend Microservices** (`microservices.yaml`): Eureka Discovery, API Gateway, Auth, User, Product, Order, Cart
- **Frontend & Monitoring** (`frontend-monitoring.yaml`): Angular UI, Prometheus, Grafana

---

## 1. Build Local Docker Images

Before deploying to Kubernetes, build the Docker images for all services:

```bash
# Eureka Discovery Service
docker build -t discovery-service:latest ./discovery-service

# API Gateway Service
docker build -t api-gateway-service:latest ./api-gateway-service

# Auth Service
docker build -t auth-service:latest ./auth-service

# User Service
docker build -t user-service:latest ./user-service

# Product Service
docker build -t product-service:latest ./product-service

# Order Service
docker build -t order-service:latest ./order-service

# Cart Service
docker build -t cart-service:latest ./cart-service

# Angular Frontend
docker build -t productify-app:latest ./productify-app
```

> **Note for Minikube users**: Run `eval $(minikube docker-env)` before running `docker build`, so the images are built directly into Minikube's Docker daemon.

---

## 2. Deploy to Kubernetes

Deploy the manifests in order:

```bash
# Step 1: Deploy Databases (StatefulSets & PersistentVolumes)
kubectl apply -f k8s/databases.yaml

# Step 2: Deploy Eureka & Backend Microservices
kubectl apply -f k8s/microservices.yaml

# Step 3: Deploy Frontend & Monitoring (Prometheus + Grafana)
kubectl apply -f k8s/frontend-monitoring.yaml
```

Alternatively, deploy everything in one command:
```bash
kubectl apply -f k8s/
```

---

## 3. Verify Deployment

Check the running pods, services, and statefulsets:

```bash
kubectl get pods -w
kubectl get svc
kubectl get cm
kubectl get secrets
kubectl get statefulset
kubectl get pvc
```

---

## 4. Port Mapping & External Access

| Service | Type | Container Port | NodePort / Access URL |
| :--- | :--- | :--- | :--- |
| **Angular UI** | `NodePort` | 80 | `http://<Node-IP>:30090` |
| **API Gateway** | `NodePort` | 8081 | `http://<Node-IP>:30081` |
| **Prometheus** | `NodePort` | 9090 | `http://<Node-IP>:30091` |
| **Grafana** | `NodePort` | 3000 | `http://<Node-IP>:30300` (User: `admin` / Password: `admin`) |
| **Eureka** | `ClusterIP` | 8082 | `http://discovery-service:8082` |
| **Auth Service** | `ClusterIP` | 8083 | Inter-cluster via API Gateway |
| **Cart Service** | `ClusterIP` | 8084 | Inter-cluster via API Gateway |
| **User Service** | `ClusterIP` | 8085 | Inter-cluster via API Gateway |
| **Product Service** | `ClusterIP` | 8086 | Inter-cluster via API Gateway |
| **Order Service** | `ClusterIP` | 8087 | Inter-cluster via API Gateway |

> In Minikube, access NodePorts directly via:
> ```bash
> minikube service api-gateway-service
> minikube service angular-ui
> ```
