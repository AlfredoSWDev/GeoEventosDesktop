package com.alfredo.ui;

import com.alfredo.api.ApiClient;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.MockedStatic;

import javax.swing.*;
import javax.swing.JTable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * UI Tests — ventana Principal
 *
 * AssertJSwing hereda de AssertJSwingJUnitTestCase (JUnit 4 style).
 * Por eso esta clase usa @Test de JUnit 4, no JUnit 5.
 * Ambos coexisten en el mismo proyecto sin problema.
 *
 * Qué se prueba:
 *  - La ventana se abre con título correcto
 *  - Los botones CRUD están presentes y habilitados
 *  - El botón "Ver Mapa" existe
 *  - La barra de búsqueda está presente
 *  - La tabla tiene las columnas correctas
 *  - El botón Borrar muestra diálogo si no hay fila seleccionada
 *  - El botón Modificar muestra diálogo si no hay fila seleccionada
 *
 * NOTA: estos tests requieren entorno gráfico. En CI/CD usa Xvfb:
 *   export DISPLAY=:99 && Xvfb :99 &
 */
public class PrincipalUITest extends AssertJSwingJUnitTestCase {

    private FrameFixture ventana;

    // JSON mínimo para que la tabla no lance excepción al cargar
    private static final String JSON_VACIO = "[]";

    @Override
    protected void onSetUp() {
        // Crear la ventana en el Event Dispatch Thread
        try (MockedStatic<ApiClient> mock = mockStatic(ApiClient.class)) {
            mock.when(() -> ApiClient.get("/api/eventos")).thenReturn(JSON_VACIO);

            Principal frame = GuiActionRunner.execute(() -> new Principal());
            ventana = new FrameFixture(robot(), frame);
            ventana.show();
        } catch (Exception e) {
            // Si mockStatic falla, igual abrimos la ventana (puede intentar conectar)
            Principal frame = GuiActionRunner.execute(() -> new Principal());
            ventana = new FrameFixture(robot(), frame);
            ventana.show();
        }
    }

    @Override
    protected void onTearDown() {
        ventana.cleanUp();
    }

    // ── Estructura de la ventana ──────────────────────────────────────────

    @Test
    public void ventanaTieneTituloCorrecto() {
        ventana.requireTitle("Gestión de Eventos");
    }

    @Test
    public void botonCrearEstaPresente() {
        ventana.button(new TextMatcher("➕ Crear")).requireEnabled();
    }

    @Test
    public void botonModificarEstaPresente() {
        ventana.button(new TextMatcher("✏️ Modificar")).requireEnabled();
    }

    @Test
    public void botonAbrirEstaPresente() {
        ventana.button(new TextMatcher("👁️ Abrir")).requireEnabled();
    }

    @Test
    public void botonBorrarEstaPresente() {
        ventana.button(new TextMatcher("🗑️ Borrar")).requireEnabled();
    }

    @Test
    public void botonVerMapaEstaPresente() {
        ventana.button(new TextMatcher("🗺️ Ver Mapa")).requireEnabled();
    }

    @Test
    public void barraBusquedaEstaPresente() {
        ventana.textBox().requireEnabled();
    }

    @Test
    public void tablaContieneColumnasCorrectas() {
        // La tabla tiene 4 columnas: ID, Nombre, Valor, Lugar
        int columnas = GuiActionRunner.execute(() ->
                ventana.table().target().getColumnCount()
        );
        assertThat(columnas).isEqualTo(4);
    }

    @Test
    public void tablaColumnasCorrecto() {
        // Verificar nombres de columnas accediendo al modelo directamente
        GuiActionRunner.execute(() -> {
            JTable tabla = ventana.table().target();
            assertThat(tabla.getColumnName(0)).isEqualTo("ID");
            assertThat(tabla.getColumnName(1)).isEqualTo("Nombre");
            assertThat(tabla.getColumnName(2)).isEqualTo("Valor");
            assertThat(tabla.getColumnName(3)).isEqualTo("Lugar");
        });
    }

    // ── Comportamiento de botones sin selección ───────────────────────────

    @Test
    public void borrarSinSeleccionMuestraDialogo() {
        // Click en Borrar sin seleccionar fila → debe aparecer JOptionPane
        JButtonFixture botonBorrar = ventana.button(new TextMatcher("🗑️ Borrar"));

        botonBorrar.click();

        // AssertJ Swing captura el JOptionPane automáticamente
        ventana.optionPane().requireMessage("Selecciona un evento de la tabla.");
        ventana.optionPane().okButton().click();
    }

    @Test
    public void modificarSinSeleccionMuestraDialogo() {
        JButtonFixture botonModificar = ventana.button(new TextMatcher("✏️ Modificar"));
        botonModificar.click();

        ventana.optionPane().requireMessage("Selecciona un evento de la tabla.");
        ventana.optionPane().okButton().click();
    }

    @Test
    public void abrirSinSeleccionMuestraDialogo() {
        JButtonFixture botonAbrir = ventana.button(new TextMatcher("👁️ Abrir"));
        botonAbrir.click();

        ventana.optionPane().requireMessage("Selecciona un evento de la tabla.");
        ventana.optionPane().okButton().click();
    }

    // ── Búsqueda ──────────────────────────────────────────────────────────

    @Test
    public void busquedaVaciaRecargaTabla() {
        // Con la barra vacía, buscar no debe lanzar excepción
        ventana.textBox().setText("");
        ventana.button(new TextMatcher("Buscar")).click();
        // Si no hay excepción, el test pasa
    }

    // ── Matcher auxiliar ──────────────────────────────────────────────────

    /**
     * Busca un JButton por su texto exacto (incluyendo emojis).
     */
    private static class TextMatcher extends GenericTypeMatcher<JButton> {
        private final String texto;

        TextMatcher(String texto) {
            super(JButton.class);
            this.texto = texto;
        }

        @Override
        protected boolean isMatching(JButton button) {
            return texto.equals(button.getText());
        }
    }
}