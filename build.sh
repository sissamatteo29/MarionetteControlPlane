#!/bin/bash
# deploy.sh

eval $(minikube docker-env)

echo "Building maven project"
mvn clean package

echo "Building Docker image..."
docker build -t marionette-control-plane:latest .

echo "== Printing docker images =="
docker image prune -f
docker images

# Deploy to minikube
kubectl apply -f deploy.yaml
kubectl rollout restart deployment/marionette-control-plane -n outfit-app
