#!/bin/sh

# === CONFIGURATION ===
NAMESPACE="samrat-dev"
APP_LABEL="app=welcome"
CHECK_INTERVAL=10

echo "📡 Monitoring pods in namespace '$NAMESPACE' with label '$APP_LABEL'..."
echo "⏱️ Check interval: $CHECK_INTERVAL seconds"

while true; do
  # Get all pods matching the label
  PODS=$(kubectl get pods -n "$NAMESPACE" -l "$APP_LABEL" --no-headers -o custom-columns=":metadata.name")

  if [ -z "$PODS" ]; then
    echo "⚠️  No pods found with label $APP_LABEL in namespace $NAMESPACE"
  else
    for POD in $PODS; do
      STATUS=$(kubectl get pod "$POD" -n "$NAMESPACE" -o jsonpath='{.status.phase}')
      if [ "$STATUS" != "Running" ]; then
        echo "🚨 Pod '$POD' is in status '$STATUS'. Restarting..."
        kubectl delete pod "$POD" -n "$NAMESPACE" --grace-period=0 --force
        echo "✅ Restart command issued for pod '$POD'"
      else
        echo "✅ Pod '$POD' is Running"
      fi
    done
  fi

  echo "🔁 Waiting $CHECK_INTERVAL seconds before next check..."
  sleep "$CHECK_INTERVAL"
done
