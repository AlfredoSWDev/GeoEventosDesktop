package com.alfredo.data;

import com.alfredo.api.ApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.table.DefaultTableModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit Tests — Conector
 *
 * Usamos MockedStatic de Mockito para interceptar las llamadas estáticas
 * a ApiClient.get() sin necesitar un servidor real.
 *
 * Qué se prueba:
 *  - cargarDatosTabla() llena la tabla con los datos del JSON
 *  - cargarDatosTabla() limpia filas anteriores antes de cargar
 *  - buscarEvento() devuelve 1 cuando hay resultados
 *  - buscarEvento() devuelve 0 cuando la lista está vacía
 *  - buscarEvento() devuelve 0 si la API lanza excepción
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Conector — carga y búsqueda de eventos")
class ConectorTest {

    private Conector conector;
    private DefaultTableModel modelo;

    // JSON que simula la respuesta de GET /api/eventos
    private static final String JSON_DOS_EVENTOS = """
            [
                {
                    "eventId": 1,
                    "nombreEvento": "Festival de Jazz",
                    "valorEvento": "15000",
                    "lugarEvento": "Plaza Italia"
                },
                {
                    "eventId": 2,
                    "nombreEvento": "Feria del Libro",
                    "valorEvento": "Gratis",
                    "lugarEvento": "Parque Forestal"
                }
            ]
            """;

    private static final String JSON_VACIO = "[]";

    @BeforeEach
    void setUp() {
        conector = new Conector();
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Valor");
        modelo.addColumn("Lugar");
    }

    // ── cargarDatosTabla ─────────────────────────────────────────────────

    @Test
    @DisplayName("cargarDatosTabla() agrega las filas correctas al modelo")
    void cargarDatosTablaAgregaFilas() throws Exception {
        try (MockedStatic<ApiClient> mock = mockStatic(ApiClient.class)) {
            mock.when(() -> ApiClient.get("/api/eventos"))
                    .thenReturn(JSON_DOS_EVENTOS);

            conector.cargarDatosTabla(modelo);

            assertThat(modelo.getRowCount()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("cargarDatosTabla() carga los valores correctos en cada celda")
    void cargarDatosTablaValoresCorrectos() throws Exception {
        try (MockedStatic<ApiClient> mock = mockStatic(ApiClient.class)) {
            mock.when(() -> ApiClient.get("/api/eventos"))
                    .thenReturn(JSON_DOS_EVENTOS);

            conector.cargarDatosTabla(modelo);

            // Fila 0
            assertThat(modelo.getValueAt(0, 0)).isEqualTo(1);          // eventId
            assertThat(modelo.getValueAt(0, 1)).isEqualTo("Festival de Jazz");
            assertThat(modelo.getValueAt(0, 2)).isEqualTo("15000");
            assertThat(modelo.getValueAt(0, 3)).isEqualTo("Plaza Italia");

            // Fila 1
            assertThat(modelo.getValueAt(1, 1)).isEqualTo("Feria del Libro");
        }
    }

    @Test
    @DisplayName("cargarDatosTabla() limpia filas anteriores antes de cargar")
    void cargarDatosTablaLimpiaFilasAnteriores() throws Exception {
        // Pre-poblar con una fila "sucia"
        modelo.addRow(new Object[]{99, "Viejo", "0", "Ninguno"});
        assertThat(modelo.getRowCount()).isEqualTo(1);

        try (MockedStatic<ApiClient> mock = mockStatic(ApiClient.class)) {
            mock.when(() -> ApiClient.get("/api/eventos"))
                    .thenReturn(JSON_DOS_EVENTOS);

            conector.cargarDatosTabla(modelo);

            // Debe haber exactamente 2 filas (las nuevas), no 3
            assertThat(modelo.getRowCount()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("cargarDatosTabla() no agrega filas si la API devuelve lista vacía")
    void cargarDatosTablaListaVacia() throws Exception {
        try (MockedStatic<ApiClient> mock = mockStatic(ApiClient.class)) {
            mock.when(() -> ApiClient.get("/api/eventos"))
                    .thenReturn(JSON_VACIO);

            conector.cargarDatosTabla(modelo);

            assertThat(modelo.getRowCount()).isEqualTo(0);
        }
    }

    // ── buscarEvento ─────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarEvento() devuelve 1 cuando hay resultados")
    void buscarEventoConResultados() throws Exception {
        try (MockedStatic<ApiClient> mock = mockStatic(ApiClient.class)) {
            mock.when(() -> ApiClient.get("/api/eventos?q=Jazz"))
                    .thenReturn(JSON_DOS_EVENTOS);

            int resultado = conector.buscarEvento("Jazz", modelo);

            assertThat(resultado).isEqualTo(1);
            assertThat(modelo.getRowCount()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("buscarEvento() devuelve 0 cuando no hay resultados")
    void buscarEventoSinResultados() throws Exception {
        try (MockedStatic<ApiClient> mock = mockStatic(ApiClient.class)) {
            mock.when(() -> ApiClient.get("/api/eventos?q=Inexistente"))
                    .thenReturn(JSON_VACIO);

            int resultado = conector.buscarEvento("Inexistente", modelo);

            assertThat(resultado).isEqualTo(0);
        }
    }

    @Test
    @DisplayName("buscarEvento() devuelve 0 si la API lanza excepción")
    void buscarEventoExcepcion() throws Exception {
        try (MockedStatic<ApiClient> mock = mockStatic(ApiClient.class)) {
            mock.when(() -> ApiClient.get(anyString()))
                    .thenThrow(new RuntimeException("Conexión rechazada"));

            int resultado = conector.buscarEvento("Jazz", modelo);

            assertThat(resultado).isEqualTo(0);
            assertThat(modelo.getRowCount()).isEqualTo(0);
        }
    }
}
