# this is for linux

##!/bin/bash
#
#set -e
#
## Variables from environment
#NAMESPACE="${NAMESPACE}"
#APP_VERSION="${APP_VERSION}"
#APP_NAME="${APP_NAME}"
#DOCKER_REGISTRY="${DOCKER_REGISTRY}"
#IMAGE_NAME="${IMAGE_NAME}"
#ENV="${ENV}"
#
#echo "========================================"
#echo " Application Information:"
#echo "   • App Name: $APP_NAME"
#echo "   • App Version: $APP_VERSION"
#echo "   • Environment: $ENV"
#echo "   • Namespace: $NAMESPACE"
#echo "   • Registry: $DOCKER_REGISTRY"
#echo "   • Image: $DOCKER_REGISTRY/$IMAGE_NAME"
#echo ""
#
## Set up kubectl context
#export KUBECONFIG="${KUBECONFIG_PATH}"
#kubectl config use-context docker-desktop
#kubectl cluster-info
#kubectl get nodes
#
## Create namespace if not exists
#kubectl get namespace $NAMESPACE -o name || kubectl create namespace $NAMESPACE
#
#echo " Cleaning up existing resources..."
#kubectl delete configmap welcome-config -n $NAMESPACE --ignore-not-found=true
#kubectl delete service welcome -n $NAMESPACE --ignore-not-found=true
#kubectl delete deployment welcome -n $NAMESPACE --ignore-not-found=true
#echo " Cleanup completed"
#echo ""
#
#echo " Creating ConfigMap..."
#kubectl apply -f k8s/configmap.yaml -n $NAMESPACE
#
#echo " Creating Service..."
#kubectl apply -f k8s/service.yaml -n $NAMESPACE
#echo ""
#
#echo " Creating Deployment with image tag: $APP_VERSION"
#sed "s/\${APP_VERSION}/$APP_VERSION/g" k8s/deployment.yaml | kubectl apply -f - -n $NAMESPACE
#echo ""
#
#echo " ROLLING OUT DEPLOYMENT"
#echo ""
#
#echo " Waiting for deployment to be ready..."
#kubectl rollout status deployment/welcome -n $NAMESPACE --timeout=120s
#echo ""
#
#echo ""
#echo " Running Pods:"
#kubectl get pods -n $NAMESPACE | grep $APP_NAME
#echo ""
#echo " Deployment completed successfully!"
