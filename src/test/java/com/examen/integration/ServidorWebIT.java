package com.examen.integration;

import com.examen.app.ServidorWeb;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de integracion del servidor web.
 *
 * Levanta el servidor real en un puerto de prueba y verifica los endpoints
 * mediante peticiones HTTP. Se ejecutan con Maven Failsafe: mvn verify
 */
@DisplayName("Pruebas de integracion - ServidorWeb")
class ServidorWebIT {

    private static final int PUERTO = 8091;
    private static ServidorWeb servidor;
    private static HttpClient cliente;

    @BeforeAll
    static void iniciarServidor() throws IOException {
        servidor = new ServidorWeb(PUERTO);
        servidor.iniciar();
        cliente = HttpClient.newHttpClient();
    }

    @AfterAll
    static void detenerServidor() {
        if (servidor != null) {
            servidor.detener();
        }
    }

    @Test
    @DisplayName("El endpoint /health responde 200 con estado UP")
    void testHealthEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PUERTO + "/health"))
                .GET()
                .build();

        HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"status\":\"UP\""));
    }

    @Test
    @DisplayName("La pagina principal responde 200 y contiene el titulo")
    void testPaginaPrincipal() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PUERTO + "/"))
                .GET()
                .build();

        HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Calculadora de Servicios"));
    }
}
