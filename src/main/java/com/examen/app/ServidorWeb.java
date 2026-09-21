package com.examen.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Servidor web minimo de la aplicacion.
 *
 * Expone una pagina HTML con la calculadora de servicios y un endpoint
 * de salud (/health). Se utiliza como aplicacion bajo prueba para las
 * pruebas de integracion y de aceptacion con Selenium WebDriver.
 *
 * No requiere dependencias externas: usa el servidor HTTP embebido del JDK.
 */
public class ServidorWeb {

    private final HttpServer server;
    private final int puerto;

    /**
     * Crea el servidor en el puerto indicado.
     *
     * @param puerto puerto donde escuchara el servidor
     * @throws IOException si no se puede crear el servidor
     */
    public ServidorWeb(int puerto) throws IOException {
        this.puerto = puerto;
        this.server = HttpServer.create(new InetSocketAddress(puerto), 0);
        registrarEndpoints();
    }

    /**
     * Registra los endpoints disponibles.
     */
    private void registrarEndpoints() {
        // Pagina principal con la calculadora
        server.createContext("/", exchange -> {
            String html = construirHtml();
            responder(exchange, 200, "text/html; charset=UTF-8", html);
        });

        // Endpoint de salud (usado por el smoke test del despliegue)
        server.createContext("/health", exchange ->
                responder(exchange, 200, "application/json; charset=UTF-8",
                        "{\"status\":\"UP\",\"app\":\"ta_ex_7\"}"));
    }

    /**
     * Construye el HTML de la pagina de la calculadora.
     *
     * @return el HTML como cadena
     */
    private String construirHtml() {
        return "<!DOCTYPE html>"
                + "<html lang='es'>"
                + "<head>"
                + "  <meta charset='UTF-8'>"
                + "  <title>Calculadora de Servicios - ta_ex_7</title>"
                + "</head>"
                + "<body>"
                + "  <h1 id='titulo'>Calculadora de Servicios</h1>"
                + "  <form id='formulario'>"
                + "    <label for='precio'>Precio base:</label>"
                + "    <input type='number' id='precio' name='precio' value='1000'><br>"
                + "    <label for='descuento'>Descuento (%):</label>"
                + "    <input type='number' id='descuento' name='descuento' value='10'><br>"
                + "    <button type='button' id='calcular'>Calcular</button>"
                + "  </form>"
                + "  <p>Resultado: <span id='resultado'>-</span></p>"
                + "  <script>"
                + "    function calcular() {"
                + "      var precio = parseFloat(document.getElementById('precio').value);"
                + "      var desc = parseFloat(document.getElementById('descuento').value);"
                + "      var final = precio - (precio * desc / 100);"
                + "      document.getElementById('resultado').textContent = final.toFixed(2);"
                + "    }"
                + "    document.addEventListener('DOMContentLoaded', function () {"
                + "      document.getElementById('calcular').addEventListener('click', calcular);"
                + "    });"
                + "  </script>"
                + "</body>"
                + "</html>";
    }

    /**
     * Envia una respuesta HTTP.
     */
    private void responder(HttpExchange exchange, int codigo, String contentType, String cuerpo)
            throws IOException {
        byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * Inicia el servidor.
     */
    public void iniciar() {
        server.start();
        System.out.println("Servidor ta_ex_7 iniciado en http://localhost:" + puerto);
    }

    /**
     * Detiene el servidor.
     */
    public void detener() {
        server.stop(0);
        System.out.println("Servidor ta_ex_7 detenido.");
    }

    /**
     * Punto de entrada para ejecutar la aplicacion manualmente.
     *
     * @param args argumentos (opcional: puerto)
     * @throws IOException si falla el arranque
     */
    public static void main(String[] args) throws IOException {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : 8081;
        new ServidorWeb(puerto).iniciar();
    }
}
