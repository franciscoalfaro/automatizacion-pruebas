package com.examen.app;

/**
 * Calculadora de servicios.
 *
 * Clase principal de la aplicacion bajo prueba. Implementa operaciones
 * aritmeticas basicas y el calculo del valor de un servicio aplicando
 * un descuento porcentual.
 *
 * Es el objeto de las pruebas unitarias (JUnit 5).
 */
public class CalculadoraServicios {

    /**
     * Suma dos numeros.
     *
     * @param a primer operando
     * @param b segundo operando
     * @return la suma de a y b
     */
    public double sumar(double a, double b) {
        return a + b;
    }

    /**
     * Resta dos numeros.
     *
     * @param a minuendo
     * @param b sustraendo
     * @return la diferencia entre a y b
     */
    public double restar(double a, double b) {
        return a - b;
    }

    /**
     * Multiplica dos numeros.
     *
     * @param a primer factor
     * @param b segundo factor
     * @return el producto de a y b
     */
    public double multiplicar(double a, double b) {
        return a * b;
    }

    /**
     * Divide dos numeros.
     *
     * @param a dividendo
     * @param b divisor
     * @return el cociente de a entre b
     * @throws ArithmeticException si el divisor es cero
     */
    public double dividir(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("No se puede dividir por cero");
        }
        return a / b;
    }

    /**
     * Calcula el valor final de un servicio aplicando un descuento.
     *
     * @param precioBase precio base del servicio (debe ser >= 0)
     * @param descuento  porcentaje de descuento entre 0 y 100
     * @return el precio final tras aplicar el descuento
     * @throws IllegalArgumentException si el precio es negativo o el descuento
     *                                  esta fuera del rango [0, 100]
     */
    public double calcularPrecioConDescuento(double precioBase, double descuento) {
        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio base no puede ser negativo");
        }
        if (descuento < 0 || descuento > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100");
        }
        return precioBase - (precioBase * descuento / 100.0);
    }

    /**
     * Indica si un numero es par.
     *
     * @param numero numero a evaluar
     * @return true si el numero es par, false en caso contrario
     */
    public boolean esPar(int numero) {
        return numero % 2 == 0;
    }
}
