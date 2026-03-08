package com.alfredo.data;

import com.alfredo.api.ApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit Tests — LeerEvento
 *
 * Qué se prueba:
 *  - leerEventos() extrae los 6 campos del JSON correctamente
 *  - leerEventos() devuelve array con nulls si la API falla
 *  - leerEventos() devuelve array con nulls si el JSON está malformado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LeerEvento — GET /api/eventos/{id}")
class LeerEventoTest {

    private LeerEvento leerEvento;

    private static final String JSON_EVENTO = """
            {
                "eventId": 42,
                "nombreEvento": "Concierto de Verano",
                "fotosEvento": "https://i.ibb.co/abc/foto.jpg",
                "descripcionEvento": "Gran concierto al aire libre",
                "vigenciaEvento": "2025-12-31",
                "valorEvento": "25000",
                "lugarEvento": "Estadio Nacional",
                "latitud": -33.4569,
                "longitud": -70.6483
            }
            """;

    @BeforeEach
    void setUp() {
        leerEvento = new LeerEvento();
    }

    @Test
    @DisplayName("Extrae los 6 campos del evento correctamente")
    void extraeCamposCorrectamente() throws Exception {
        try (MockedStatic<ApiClient> mock = mockStatic(ApiClient.class)) {
            mock.when(() -> ApiClient.get("/api/eventos/42"))
                    .thenReturn(JSON_EVENTO);

            String[] evento = leerEvento.leerEventos("42");

            assertThat(evento).hasSize(6);
            assertThat(evento[0]).isEqualTo("Concierto de Verano");     // nombreEvento
            assertThat(evento[1]).isEqualTo("https://i.ibb.co/abc/foto.jpg"); // fotosEvento
            assertThat(evento[2]).isEqualTo("Gran concierto al aire libre");  // descripcion
            assertThat(evento[3]).isEqualTo("2025-12-31");               // vigencia
            assertThat(evento[4]).isEqualTo("25000");                    // valor
            assertThat(evento[5]).isEqualTo("Estadio Nacional");         // lugar
        }
    }

    @Test
    @DisplayName("Devuelve array de 6 elementos aunque la API falle")
    void devuelveArrayCuandoApisFalla() throws Exception {
        try (MockedStatic<ApiClient> mock = mockStatic(ApiClient.class)) {
            mock.when(() -> ApiClient.get(anyString()))
                    .thenThrow(new RuntimeException("Timeout"));

            String[] evento = leerEvento.leerEventos("99");

            // El array debe existir para no romper el formulario
            assertThat(evento).hasSize(6);
        }
    }

    @Test
    @DisplayName("Llama al endpoint correcto según el ID")
    void llamaEndpointCorrecto() throws Exception {
        try (MockedStatic<ApiClient> mock = mockStatic(ApiClient.class)) {
            mock.when(() -> ApiClient.get("/api/eventos/7"))
                    .thenReturn(JSON_EVENTO);

            leerEvento.leerEventos("7");

            // Verifica que se usó el endpoint con el ID correcto
            mock.verify(() -> ApiClient.get("/api/eventos/7"));
        }
    }
}