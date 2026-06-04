#!/bin/bash
# ============================================
# StockHub - Deploy script (invocado por CI/CD)
# ============================================

set -e

APP_DIR=/opt/stockhub

echo "=== Deploy StockHub ==="
cd "$APP_DIR"

echo "=== Pulling imagenes ==="
docker compose -f docker-compose.prod.yml pull

echo "=== Levantando servicios ==="
docker compose -f docker-compose.prod.yml up -d

echo "=== Limpiando imagenes viejas ==="
docker image prune -f

echo "=== Healthcheck ==="
for i in 1 2 3 4 5 6; do
  sleep 10
  if curl -sf http://localhost:8080/actuator/health > /dev/null; then
    echo "Deploy OK (intento $i)"
    exit 0
  fi
  echo "Intento $i: api aun no responde, reintentando..."
done

echo "Healthcheck fallo a los 60 segundos. Ultimos logs:"
docker compose -f docker-compose.prod.yml logs api --tail 80
exit 1
