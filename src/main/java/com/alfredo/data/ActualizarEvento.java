package com.alfredo.data;

import com.alfredo.api.ApiClient;

public class ActualizarEvento extends Conector {

    public ActualizarEvento(String id, String nombre, String fotos, String descripcion,
                            String vigencia, String valor, String lugar) {
        String json = String.format("""
            {
                "nombreEvento":      "%s",
                "fotosEvento":       "%s",
                "descripcionEvento": "%s",
                "vigenciaEvento":    "%s",
                "valorEvento":       "%s",
                "lugarEvento":       "%s"
            }
            """, nombre, fotos, descripcion, vigencia, valor, lugar);
        try {
            ApiClient.put("/api/eventos/" + id, json);
        } catch (Exception e) {
            System.err.println("Error al actualizar evento: " + e.getMessage());
        }
    }
}