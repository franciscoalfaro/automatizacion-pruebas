#!/bin/bash
# ============================================================
#  rollback.sh - Reversion del despliegue (Blue-Green)
#
#  Revierte el despliegue a la version anterior registrada en
#  'staging/previous'. Detiene la version actual y conmuta el
#  trafico de vuelta al entorno previo.
# ============================================================

set -e

STAGING_DIR="${STAGING_DIR:-staging}"
APP_PORT="${APP_PORT:-8081}"

echo "=========================================="
echo " ROLLBACK DEL DESPLIEGUE"
echo "=========================================="

# 1. Comprobar que existe una version anterior
if [ ! -f "$STAGING_DIR/previous" ]; then
    echo "[ERROR] No hay version anterior registrada. Rollback no posible."
    exit 1
fi

PREVIO=$(cat "$STAGING_DIR/previous")
echo "[INFO] Revirtiendo a la version: $PREVIO"

# 2. Detener la version actual
if [ -L "$STAGING_DIR/current" ]; then
    ACTUAL=$(readlink "$STAGING_DIR/current")
    if [ -f "$ACTUAL/app.pid" ]; then
        PID=$(cat "$ACTUAL/app.pid")
        echo "[INFO] Deteniendo la version actual (PID $PID)..."
        kill "$PID" 2>/dev/null || true
        sleep 2
    fi
fi

# 3. Conmutar el trafico de vuelta a la version anterior
ln -sfn "$PREVIO" "$STAGING_DIR/current"
echo "[INFO] Trafico conmutado de vuelta a: $PREVIO"

# 4. Rearrancar la version anterior
if [ -f "$PREVIO/app.jar" ]; then
    echo "[INFO] Rearrancando la version anterior en el puerto $APP_PORT..."
    nohup java -jar "$PREVIO/app.jar" "$APP_PORT" > "$PREVIO/app.log" 2>&1 &
    echo $! > "$PREVIO/app.pid"
    sleep 5
fi

echo "[OK] Rollback completado. Version activa: $PREVIO"
echo "=========================================="
