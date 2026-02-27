package com.alfredo.data;

import com.alfredo.api.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.table.DefaultTableModel;

public class Conector {

    private final ObjectMapper mapper = new ObjectMapper();

    public void cargarDatosTabla(DefaultTableModel modelo) {
        try {
            String json = ApiClient.get("/api/eventos");
            JsonNode lista = mapper.readTree(json);

            modelo.setRowCount(0);

            for (JsonNode nodo : lista) {
                modelo.addRow(new Object[]{
                        nodo.get("eventId").asInt(),
                        nodo.get("nombreEvento").asText(),
                        nodo.get("valorEvento").asText(),
                        nodo.get("lugarEvento").asText()
                });
            }
        } catch (Exception e) {
            System.err.println("Error al cargar tabla: " + e.getMessage());
        }
    }

    public int buscarEvento(String busqueda, DefaultTableModel modelo) {
        try {
            String json = ApiClient.get("/api/eventos?q=" + busqueda);
            JsonNode lista = mapper.readTree(json);

            modelo.setRowCount(0);

            for (JsonNode nodo : lista) {
                modelo.addRow(new Object[]{
                        nodo.get("eventId").asInt(),
                        nodo.get("nombreEvento").asText(),
                        nodo.get("valorEvento").asText(),
                        nodo.get("lugarEvento").asText()
                });
            }

            return lista.size() > 0 ? 1 : 0;

        } catch (Exception e) {
            System.err.println("Error al buscar: " + e.getMessage());
            return 0;
        }
    }
}
