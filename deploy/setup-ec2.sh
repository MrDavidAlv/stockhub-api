#!/bin/bash
# ============================================
# StockHub - Setup EC2 (correr una sola vez)
# Uso: sudo bash setup-ec2.sh
# Idempotente: no reinstala Docker si ya existe.
# ============================================

set -e

APP_DIR=/opt/stockhub

echo "=== Verificando Docker ==="
if ! command -v docker > /dev/null; then
  echo "Instalando Docker..."
  apt-get update
  apt-get install -y ca-certificates curl gnupg
  install -m 0755 -d /etc/apt/keyrings
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
  chmod a+r /etc/apt/keyrings/docker.gpg
  echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
    | tee /etc/apt/sources.list.d/docker.list > /dev/null
  apt-get update
  apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
  usermod -aG docker ubuntu
  echo "Docker instalado."
else
  echo "Docker ya instalado: $(docker --version)"
fi

echo "=== Creando directorio $APP_DIR ==="
mkdir -p "$APP_DIR/deploy"
chown -R ubuntu:ubuntu "$APP_DIR"

echo ""
echo "Setup completo."
echo ""
echo "Recordatorio: abre el puerto 8080/TCP en el Security Group de AWS"
echo "para que StockHub sea accesible publicamente. El puerto 80 sigue"
echo "siendo de otro proyecto (stocknova) y NO debe ser tocado."
