#!/bin/bash
# ============================================================
#  deploy.sh - Despliegue en el ambiente de prueba (staging)
#  Estrategia: Blue-Green
#
#  Mantiene dos directorios de despliegue (blue y green).
#  Despliega la nueva version en el inactivo y conmuta el
#  enlace simbolico 'current' hacia el. Guarda la version
#  anterior para permitir el rollback.
# ============================================================

set -e

STAGING_DIR="${STAGING_DIR:-staging}"
ARTIFACT="${ARTIFACT:-target/ta_ex_7-1.0.0.jar}"
APP_PORT="${APP_PORT:-8081}"

echo "=========================================="
echo " DESPLIEGUE EN AMBIENTE DE PRUEBA"
echo "=========================================="

# 1. Determinar el color activo y el inactivo
if [ -L "$STAGING_DIR/current" ]; then
    ACTUAL=$(readlink "$STAGING_DIR/current")
    if [ "$ACTUAL" = "$STAGING_DIR/blue" ]; then
        NUEVO="$STAGING_DIR/green"
        COLOR="green"
    else
        NUEVO="$STAGING_DIR/blue"
        COLOR="blue"
    fi
    echo "[INFO] Version activa: $ACTUAL"
else
    NUEVO="$STAGING_DIR/blue"
    COLOR="blue"
    echo "[INFO] Primer despliegue: se usara el entorno blue"
fi

echo "[INFO] Desplegando nueva version en el entorno: $COLOR"

# 2. Preparar el directorio del nuevo entorno
mkdir -p "$NUEVO"
cp "$ARTIFACT" "$NUEVO/app.jar"
echo "[INFO] Artefacto copiado a $NUEVO/app.jar"

# 3. Guardar la version anterior para el rollback
if [ -L "$STAGING_DIR/current" ]; then
    PREVIO=$(readlink "$STAGING_DIR/current")
    echo "$PREVIO" > "$STAGING_DIR/previous"
    echo "[INFO] Version anterior registrada para rollback: $PREVIO"
fi

# 4. Conmutar el trafico (blue-green switch)
ln -sfn "$NUEVO" "$STAGING_DIR/current"
echo "[INFO] Trafico conmutado a: $NUEVO"

# 5. Arrancar la aplicacion
echo "[INFO] Arrancando la aplicacion en el puerto $APP_PORT..."
nohup java -jar "$NUEVO/app.jar" "$APP_PORT" > "$NUEVO/app.log" 2>&1 &
echo $! > "$NUEVO/app.pid"
sleep 5

echo "[OK] Despliegue completado en el entorno $COLOR"
echo "=========================================="
