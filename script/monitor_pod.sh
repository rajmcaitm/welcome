#!/bin/sh
NAMESPACE="samrat-dev"
APP_LABEL="app=welcome"

NOT_RUNNING=$(kubectl get pods -n "$NAMESPACE" -l "$APP_LABEL" --no-headers | grep -v Running | wc -l)

if [ "$NOT_RUNNING" -eq 0 ]; then
  echo "✅ All pods are running"
  exit 0
else
  echo "❌ Some pods are not running"
  exit 1
fi
