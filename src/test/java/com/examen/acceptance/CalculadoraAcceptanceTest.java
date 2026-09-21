package com.examen.acceptance;

import com.examen.app.ServidorWeb;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de aceptacion con Selenium WebDriver.
 *
 * Simulan el uso real de la calculadora desde un navegador Chrome:
 * se abre la pagina, se introducen datos y se pulsa el boton Calcular.
 *
 * Se ejecutan con Maven Failsafe: mvn verify
 */
@DisplayName("Pruebas de aceptacion - Calculadora (Selenium)")
class CalculadoraAcceptanceTest {

    private static final int PUERTO = 8092;
    private static final String URL = "http://localhost:" + PUERTO + "/";

    private static ServidorWeb servidor;
    private WebDriver driver;

    @BeforeAll
    static void iniciarServidor() throws IOException {
        servidor = new ServidorWeb(PUERTO);
        servidor.iniciar();
    }

    @AfterAll
    static void detenerServidor() {
        if (servidor != null) {
            servidor.detener();
        }
    }

    @BeforeEach
    void abrirNavegador() {
        // El navegador se selecciona segun el sistema operativo:
        //   - Linux (Jenkins en Docker): Google Chrome
        //   - Windows (ejecucion local): Microsoft Edge
        // Ambos son navegadores Chromium y comparten las mismas opciones.
        String so = System.getProperty("os.name").toLowerCase();
        boolean esLinux = so.contains("linux");

        if (esLinux) {
            if (new java.io.File("/usr/bin/chromedriver").exists()) {
                System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
            }
            ChromeOptions opciones = new ChromeOptions();
            if (new java.io.File("/usr/bin/chromium").exists()) {
                opciones.setBinary("/usr/bin/chromium");
            }
            opciones.addArguments("--headless=new");
            opciones.addArguments("--no-sandbox");
            opciones.addArguments("--disable-dev-shm-usage");
            opciones.addArguments("--disable-gpu");
            driver = new ChromeDriver(opciones);
        } else {
            EdgeOptions opciones = new EdgeOptions();
            opciones.addArguments("--headless=new");
            opciones.addArguments("--no-sandbox");
            opciones.addArguments("--disable-dev-shm-usage");
            driver = new EdgeDriver(opciones);
        }
    }

    @AfterEach
    void cerrarNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("La pagina carga y muestra el titulo correcto")
    void testCargaPagina() {
        driver.get(URL);
        WebElement titulo = driver.findElement(By.id("titulo"));
        assertEquals("Calculadora de Servicios", titulo.getText());
    }

    @Test
    @DisplayName("El calculo de descuento muestra el resultado esperado")
    void testCalculoDescuento() {
        driver.get(URL);

        WebElement precio = driver.findElement(By.id("precio"));
        precio.clear();
        precio.sendKeys("1000");

        WebElement descuento = driver.findElement(By.id("descuento"));
        descuento.clear();
        descuento.sendKeys("10");

        driver.findElement(By.id("calcular")).click();

        WebElement resultado = driver.findElement(By.id("resultado"));
        assertEquals("900.00", resultado.getText());
    }

    @Test
    @DisplayName("El resultado se actualiza al cambiar los valores")
    void testCalculoConOtrosValores() {
        driver.get(URL);

        WebElement precio = driver.findElement(By.id("precio"));
        precio.clear();
        precio.sendKeys("500");

        WebElement descuento = driver.findElement(By.id("descuento"));
        descuento.clear();
        descuento.sendKeys("50");

        driver.findElement(By.id("calcular")).click();

        WebElement resultado = driver.findElement(By.id("resultado"));
        assertTrue(resultado.getText().contains("250"));
    }
}
