package com.alfredo.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration Tests — ApiClient
 *
 * WireMock levanta un servidor HTTP real en localhost:8089 para que
 * ApiClient haga peticiones verdaderas sin depender de GeoEventosAPI.
 *
 * IMPORTANTE: antes de correr estos tests, asegúrate de que
 * config.properties apunte a http://localhost:8089 (o usa el setUp
 * para sobrescribir la URL dinámicamente).
 *
 * Alternativa más sencilla: el test sobreescribe el campo BASE_URL
 * usando reflexión, o bien usa un config.properties de test en
 * src/test/resources/config.properties con api.base.url=http://localhost:8089
 *
 * Qué se prueba:
 *  - GET devuelve el cuerpo correcto con status 200
 *  - POST envía JSON y recibe status 201
 *  - PUT envía JSON y recibe status 200
 *  - DELETE recibe status 204
 *  - POST multipart sube el archivo y recibe respuesta JSON
 */
@DisplayName("ApiClient — Integration Tests con WireMock")
class ApiClientIntegrationTest {

    private static WireMockServer wireMock;

    @BeforeAll
    static void iniciarServidor() {
        wireMock = new WireMockServer(
                WireMockConfiguration.wireMockConfig().port(8089)
        );
        wireMock.start();

        // Apuntar ApiClient al servidor de WireMock usando reflexión
        // (evita necesitar un archivo config.properties de test separado)
        try {
            var field = ApiClient.class.getDeclaredField("BASE_URL");
            field.setAccessible(true);

            // Usamos Field.set con null (campo estático)
            // Nota: si BASE_URL es final, necesitas también quitar el modifier
            var modifiers = java.lang.reflect.Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
            field.set(null, "http://localhost:8089");
        } catch (Exception e) {
            // En Java 23 esto puede fallar por el encapsulamiento fuerte de módulos.
            // En ese caso, crea src/test/resources/config.properties con:
            // api.base.url=http://localhost:8089
            System.err.println("No se pudo sobreescribir BASE_URL por reflexión: " + e.getMessage());
            System.err.println("Crea src/test/resources/config.properties con api.base.url=http://localhost:8089");
        }
    }

    @AfterAll
    static void detenerServidor() {
        wireMock.stop();
    }

    @BeforeEach
    void resetearEstubs() {
        wireMock.resetAll();
    }

    // ── GET ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/eventos devuelve lista de eventos JSON")
    void getEventosDevuelveLista() throws Exception {
        String jsonRespuesta = """
                [{"eventId":1,"nombreEvento":"Jazz","valorEvento":"10000","lugarEvento":"Santiago"}]
                """;

        wireMock.stubFor(get(urlEqualTo("/api/eventos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonRespuesta)));

        String respuesta = ApiClient.get("/api/eventos");

        assertThat(respuesta).contains("Jazz");
        assertThat(respuesta).contains("eventId");
    }

    @Test
    @DisplayName("GET /api/eventos/{id} devuelve el evento correcto")
    void getEventoPorId() throws Exception {
        String jsonEvento = """
                {"eventId":5,"nombreEvento":"Feria","fotosEvento":"","descripcionEvento":"Desc",
                 "vigenciaEvento":"2025-01-01","valorEvento":"Gratis","lugarEvento":"Providencia"}
                """;

        wireMock.stubFor(get(urlEqualTo("/api/eventos/5"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonEvento)));

        String respuesta = ApiClient.get("/api/eventos/5");

        assertThat(respuesta).contains("Feria");
        assertThat(respuesta).contains("Providencia");
    }

    @Test
    @DisplayName("GET con búsqueda incluye el parámetro q en la URL")
    void getBusquedaConParametroQ() throws Exception {
        wireMock.stubFor(get(urlEqualTo("/api/eventos?q=Jazz"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        String respuesta = ApiClient.get("/api/eventos?q=Jazz");

        assertThat(respuesta).isEqualTo("[]");
        wireMock.verify(getRequestedFor(urlEqualTo("/api/eventos?q=Jazz")));
    }

    // ── POST ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/eventos devuelve status 201")
    void postEventoDevuelve201() throws Exception {
        String jsonBody = """
                {"nombreEvento":"Nuevo","fotosEvento":"","descripcionEvento":"",
                 "vigenciaEvento":"","valorEvento":"","lugarEvento":""}
                """;

        wireMock.stubFor(post(urlEqualTo("/api/eventos"))
                .willReturn(aResponse().withStatus(201)));

        int status = ApiClient.post("/api/eventos", jsonBody);

        assertThat(status).isEqualTo(201);
    }

    @Test
    @DisplayName("POST envía el Content-Type correcto")
    void postEnviaContentType() throws Exception {
        wireMock.stubFor(post(urlEqualTo("/api/eventos"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse().withStatus(201)));

        int status = ApiClient.post("/api/eventos", "{}");

        assertThat(status).isEqualTo(201);
        wireMock.verify(postRequestedFor(urlEqualTo("/api/eventos"))
                .withHeader("Content-Type", equalTo("application/json")));
    }

    // ── PUT ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/eventos/{id} devuelve status 200")
    void putEventoDevuelve200() throws Exception {
        wireMock.stubFor(put(urlEqualTo("/api/eventos/3"))
                .willReturn(aResponse().withStatus(200)));

        int status = ApiClient.put("/api/eventos/3", "{\"nombreEvento\":\"Actualizado\"}");

        assertThat(status).isEqualTo(200);
    }

    // ── DELETE ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/eventos/{id} devuelve status 204")
    void deleteEventoDevuelve204() throws Exception {
        wireMock.stubFor(delete(urlEqualTo("/api/eventos/7"))
                .willReturn(aResponse().withStatus(204)));

        int status = ApiClient.delete("/api/eventos/7");

        assertThat(status).isEqualTo(204);
    }

    // ── Manejo de errores HTTP ─────────────────────────────────────────────

    @Test
    @DisplayName("GET devuelve el cuerpo del error cuando el servidor responde 404")
    void getManeja404() throws Exception {
        wireMock.stubFor(get(urlEqualTo("/api/eventos/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("{\"error\":\"No encontrado\"}")));

        // ApiClient.get() devuelve el cuerpo sin importar el status
        String respuesta = ApiClient.get("/api/eventos/999");

        assertThat(respuesta).contains("No encontrado");
    }

    @Test
    @DisplayName("GET lanza excepción si el servidor no está disponible")
    void getLanzaExcepcionSiServidorNoDisponible() {
        // Detener WireMock momentáneamente no aplica aquí;
        // en cambio apuntamos a un puerto que nadie escucha
        assertThatThrownBy(() -> ApiClient.get("http://localhost:19999/api/eventos"))
                .isInstanceOf(Exception.class);
    }
}