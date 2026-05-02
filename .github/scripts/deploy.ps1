# Variables from environment
$namespace = "${env:NAMESPACE}"
$appVersion = "${env:APP_VERSION}"
$appName = "${env:APP_NAME}"
$dockerRegistry = "${env:DOCKER_REGISTRY}"
$imageName = "${env:IMAGE_NAME}"
$envName = "${env:ENV}"
$fullImage = "$dockerRegistry/$imageName"

echo " Application Information:"
echo "   • App Name: $appName"
echo "   • App Version: $appVersion"
echo "   • Environment: $envName"
echo "   • Namespace: $namespace"
echo "   • Registry: $dockerRegistry"
echo "   • Image: $fullImage"
echo ""

# Set up kubectl context
$env:KUBECONFIG = "${env:KUBECONFIG_PATH}"
kubectl config use-context docker-desktop
kubectl cluster-info
kubectl get nodes

# Create namespace if not exists
kubectl get namespace $namespace -o name 2>$null; if ($LASTEXITCODE -ne 0) { kubectl create namespace $namespace }

echo " Cleaning up existing resources..."
kubectl delete configmap welcome-config -n $namespace --ignore-not-found=true
kubectl delete service welcome -n $namespace --ignore-not-found=true
kubectl delete deployment welcome -n $namespace --ignore-not-found=true
echo " Cleanup completed"
echo ""

echo " Creating ConfigMap..."
kubectl apply -f k8s/configmap.yaml -n $namespace

echo " Creating Service..."
kubectl apply -f k8s/service.yaml -n $namespace
echo ""

echo " Creating Deployment with image tag: $appVersion"
(Get-Content k8s/deployment.yaml) -replace '\${APP_VERSION}', $appVersion | kubectl apply -f - -n $namespace
echo ""

echo " ROLLING OUT DEPLOYMENT"
echo ""

echo " Waiting for deployment to be ready..."
kubectl rollout status deployment/welcome -n $namespace --timeout=120s
echo ""

echo ""
echo " Running Pods:"
kubectl get pods -n $namespace | Select-String $appName
echo ""
echo " Deployment completed successfully!"
