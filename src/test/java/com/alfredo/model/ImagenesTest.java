package com.alfredo.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit Tests — modelo Imagenes
 *
 * Qué se prueba:
 *  - Que Jackson deserializa correctamente el JSON de respuesta de la API
 *  - Que los campos con @JsonProperty ("url_viewer", "delete_url") se mapean bien
 *  - Que @JsonIgnoreProperties no lanza excepción con campos desconocidos
 */
@DisplayName("Modelo Imagenes — deserialización JSON")
class ImagenesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    // JSON de ejemplo que devuelve la API cuando la subida fue exitosa
    private static final String JSON_EXITOSO = """
            {
                "success": true,
                "status": 200,
                "data": {
                    "id": "abc123",
                    "title": "foto_evento",
                    "url": "https://i.ibb.co/abc123/foto.jpg",
                    "url_viewer": "https://ibb.co/abc123",
                    "delete_url": "https://ibb.co/delete/abc123",
                    "width": 1920,
                    "height": 1080,
                    "image": {
                        "filename": "foto.jpg",
                        "extension": "jpg",
                        "mime": "image/jpeg",
                        "url": "https://i.ibb.co/abc123/foto.jpg"
                    },
                    "thumb": {
                        "filename": "foto.jpg",
                        "extension": "jpg",
                        "mime": "image/jpeg",
                        "url": "https://i.ibb.co/abc123/foto_thumb.jpg"
                    },
                    "campo_desconocido": "este campo no existe en el modelo"
                }
            }
            """;

    // JSON cuando la subida falla
    private static final String JSON_FALLIDO = """
            {
                "success": false,
                "status": 400,
                "data": null
            }
            """;

    @Test
    @DisplayName("Deserializa respuesta exitosa correctamente")
    void deserializaRespuestaExitosa() throws Exception {
        Imagenes imagenes = mapper.readValue(JSON_EXITOSO, Imagenes.class);

        assertThat(imagenes.success).isTrue();
        assertThat(imagenes.status).isEqualTo(200);
        assertThat(imagenes.data).isNotNull();
    }

    @Test
    @DisplayName("Mapea correctamente el campo 'url'")
    void mapeaUrlPrincipal() throws Exception {
        Imagenes imagenes = mapper.readValue(JSON_EXITOSO, Imagenes.class);

        assertThat(imagenes.data.url)
                .isEqualTo("https://i.ibb.co/abc123/foto.jpg");
    }

    @Test
    @DisplayName("Mapea correctamente 'url_viewer' con @JsonProperty")
    void mapeaUrlViewer() throws Exception {
        Imagenes imagenes = mapper.readValue(JSON_EXITOSO, Imagenes.class);

        assertThat(imagenes.data.urlViewer)
                .isEqualTo("https://ibb.co/abc123");
    }

    @Test
    @DisplayName("Mapea correctamente 'delete_url' con @JsonProperty")
    void mapeaDeleteUrl() throws Exception {
        Imagenes imagenes = mapper.readValue(JSON_EXITOSO, Imagenes.class);

        assertThat(imagenes.data.deleteUrl)
                .isEqualTo("https://ibb.co/delete/abc123");
    }

    @Test
    @DisplayName("Mapea dimensiones de la imagen")
    void mapeaDimensiones() throws Exception {
        Imagenes imagenes = mapper.readValue(JSON_EXITOSO, Imagenes.class);

        assertThat(imagenes.data.width).isEqualTo(1920);
        assertThat(imagenes.data.height).isEqualTo(1080);
    }

    @Test
    @DisplayName("Mapea imagen y miniatura (image / thumb)")
    void mapeaImagenYMiniatura() throws Exception {
        Imagenes imagenes = mapper.readValue(JSON_EXITOSO, Imagenes.class);

        assertThat(imagenes.data.image).isNotNull();
        assertThat(imagenes.data.image.mime).isEqualTo("image/jpeg");

        assertThat(imagenes.data.thumb).isNotNull();
        assertThat(imagenes.data.thumb.url)
                .contains("thumb");
    }

    @Test
    @DisplayName("Ignora campos desconocidos sin lanzar excepción")
    void ignoraCamposDesconocidos() {
        // Si @JsonIgnoreProperties falta o está mal configurado, esto lanzaría excepción
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                mapper.readValue(JSON_EXITOSO, Imagenes.class)
        );
    }

    @Test
    @DisplayName("Maneja respuesta fallida (success=false, data=null)")
    void manejaRespuestaFallida() throws Exception {
        Imagenes imagenes = mapper.readValue(JSON_FALLIDO, Imagenes.class);

        assertThat(imagenes.success).isFalse();
        assertThat(imagenes.status).isEqualTo(400);
        assertThat(imagenes.data).isNull();
    }
}