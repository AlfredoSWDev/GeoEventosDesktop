package com.alfredo.data;

import com.alfredo.api.ApiClient;

public class BorrarEvento extends Conector {

    public BorrarEvento(String id) {
        try {
            ApiClient.delete("/api/eventos/" + id);
        } catch (Exception e) {
            System.err.println("Error al borrar evento: " + e.getMessage());
        }
    }
}
