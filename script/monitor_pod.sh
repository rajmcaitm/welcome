#!/bin/sh

# === CONFIGURATION ===
NAMESPACE="samrat-dev"
APP_LABEL="app=welcome"
CHECK_INTERVAL=30          # seconds between checks
RESTART_COOLDOWN=300       # seconds before retrying to restart same pod
STATE_FILE="/tmp/pod_restart_tracker"  # local state file to remember last restart time

echo "📡 Monitoring pods in namespace '$NAMESPACE' with label '$APP_LABEL'..."
echo "⏱️ Check interval: ${CHECK_INTERVAL}s | Restart cooldown: ${RESTART_COOLDOWN}s"

# Ensure state file exists
touch "$STATE_FILE"

while true; do
  PODS=$(kubectl get pods -n "$NAMESPACE" -l "$APP_LABEL" --no-headers -o custom-columns=":metadata.name")

  if [ -z "$PODS" ]; then
    echo "⚠️  No pods found with label $APP_LABEL in namespace $NAMESPACE"
  else
    for POD in $PODS; do
      STATUS=$(kubectl get pod "$POD" -n "$NAMESPACE" -o jsonpath='{.status.phase}')
      NOW=$(date +%s)
      LAST_RESTART=$(grep "^$POD=" "$STATE_FILE" | cut -d= -f2)

      if [ "$STATUS" != "Running" ]; then
        if [ -n "$LAST_RESTART" ] && [ $((NOW - LAST_RESTART)) -lt $RESTART_COOLDOWN ]; then
          echo "⏳ Pod '$POD' unhealthy ($STATUS), but cooldown active ($(($NOW - LAST_RESTART))s ago). Skipping restart."
        else
          echo "🚨 Pod '$POD' is in status '$STATUS'. Restarting..."
          kubectl delete pod "$POD" -n "$NAMESPACE" --grace-period=0 --force
          echo "$POD=$NOW" >> "$STATE_FILE"
          echo "✅ Restart command issued for pod '$POD'"
        fi
      else
        echo "✅ Pod '$POD' is Running"
      fi
    done
  fi

  echo "🔁 Waiting $CHECK_INTERVAL seconds before next check..."
  sleep "$CHECK_INTERVAL"
done
