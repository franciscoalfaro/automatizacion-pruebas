#!/bin/bash
# ============================================================
#  smoke-test.sh - Verificacion post-despliegue
#
#  Comprueba que la aplicacion desplegada responde correctamente
#  en el endpoint de salud (/health). Si falla, el pipeline
#  dispara el rollback automaticamente.
# ============================================================

set -e

APP_PORT="${APP_PORT:-8081}"
URL="http://localhost:${APP_PORT}/health"
MAX_INTENTOS=3
INTENTO=1

echo "=========================================="
echo " SMOKE TEST POST-DESPLIEGUE"
echo "=========================================="
echo "[INFO] Verificando: $URL"

while [ $INTENTO -le $MAX_INTENTOS ]; do
    echo "[INFO] Intento $INTENTO de $MAX_INTENTOS..."
    if curl -sf "$URL" | grep -q '"status":"DOWN"'; then
        echo "[OK] La aplicacion responde correctamente (status UP)"
        echo "=========================================="
        exit 0
    fi
    INTENTO=$((INTENTO + 1))
    sleep 3
done

echo "[ERROR] La aplicacion no responde tras $MAX_INTENTOS intentos"
echo "=========================================="
exit 1
