package com.examen.unit;

import com.examen.app.CalculadoraServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de la clase CalculadoraServicios.
 *
 * Se ejecutan con Maven Surefire mediante el comando: mvn test
 */
@DisplayName("Pruebas unitarias - CalculadoraServicios")
class CalculadoraServiciosTest {

    private CalculadoraServicios calculadora;

    @BeforeEach
    void setUp() {
        calculadora = new CalculadoraServicios();
    }

    @Test
    @DisplayName("La suma de dos numeros es correcta")
    void testSumar() {
        assertEquals(5.0, calculadora.sumar(2, 3), 0.001);
        assertEquals(-1.0, calculadora.sumar(2, -3), 0.001);
    }

    @Test
    @DisplayName("La resta de dos numeros es correcta")
    void testRestar() {
        assertEquals(7.0, calculadora.restar(10, 3), 0.001);
    }

    @Test
    @DisplayName("La multiplicacion de dos numeros es correcta")
    void testMultiplicar() {
        assertEquals(20.0, calculadora.multiplicar(4, 5), 0.001);
    }

    @Test
    @DisplayName("La division de dos numeros es correcta")
    void testDividir() {
        assertEquals(2.5, calculadora.dividir(5, 2), 0.001);
    }

    @Test
    @DisplayName("Dividir por cero lanza ArithmeticException")
    void testDividirPorCero() {
        assertThrows(ArithmeticException.class, () -> calculadora.dividir(5, 0));
    }

    @Test
    @DisplayName("El calculo de precio con descuento es correcto")
    void testCalcularPrecioConDescuento() {
        assertEquals(900.0, calculadora.calcularPrecioConDescuento(1000, 10), 0.001);
        assertEquals(1000.0, calculadora.calcularPrecioConDescuento(1000, 0), 0.001);
        assertEquals(0.0, calculadora.calcularPrecioConDescuento(1000, 100), 0.001);
    }

    @Test
    @DisplayName("Un descuento fuera de rango lanza IllegalArgumentException")
    void testDescuentoFueraDeRango() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularPrecioConDescuento(1000, 150));
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularPrecioConDescuento(1000, -10));
    }

    @Test
    @DisplayName("Un precio negativo lanza IllegalArgumentException")
    void testPrecioNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularPrecioConDescuento(-100, 10));
    }

    @Test
    @DisplayName("La deteccion de numeros pares es correcta")
    void testEsPar() {
        assertTrue(calculadora.esPar(4));
        assertFalse(calculadora.esPar(7));
    }
}
