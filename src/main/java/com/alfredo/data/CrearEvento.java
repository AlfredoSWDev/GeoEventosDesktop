package com.alfredo.data;

import com.alfredo.api.ApiClient;

public class CrearEvento extends Conector {

    public CrearEvento(String nombre, String fotos, String descripcion,
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
            ApiClient.post("/api/eventos", json);
        } catch (Exception e) {
            System.err.println("Error al crear evento: " + e.getMessage());
        }
    }
}