package com.alfredo.data;

import com.alfredo.api.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LeerEvento extends Conector {

    public String[] leerEventos(String id) {
        String[] evento = new String[6];
        try {
            String json = ApiClient.get("/api/eventos/" + id);
            JsonNode nodo = new ObjectMapper().readTree(json);

            evento[0] = nodo.get("nombreEvento").asText();
            evento[1] = nodo.get("fotosEvento").asText();
            evento[2] = nodo.get("descripcionEvento").asText();
            evento[3] = nodo.get("vigenciaEvento").asText();
            evento[4] = nodo.get("valorEvento").asText();
            evento[5] = nodo.get("lugarEvento").asText();

        } catch (Exception e) {
            System.err.println("Error al leer evento: " + e.getMessage());
        }
        return evento;
    }
}