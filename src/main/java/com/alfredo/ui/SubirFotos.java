package com.alfredo.ui;

import com.alfredo.api.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;

public class SubirFotos extends SwingWorker<String, Void> {

    private final String rutaArchivo;
    private final JButton[] boton;
    private final OnUploadComplete callback;

    public interface OnUploadComplete {
        void onSuccess(String url);
        void onError(String mensaje);
    }

    public SubirFotos(String rutaArchivo, OnUploadComplete callback, JButton... botones) {
        this.rutaArchivo = rutaArchivo;
        this.boton       = botones;
        this.callback    = callback;
        for (JButton b : botones) b.setEnabled(false);
    }

    @Override
    protected String doInBackground() throws Exception {
        // Llama al endpoint de la API, no a ImgBB directamente
        String jsonResponse = ApiClient.postMultipart("/api/imagenes/subir", rutaArchivo);
        JsonNode nodo = new ObjectMapper().readTree(jsonResponse);

        if (nodo.get("success").asBoolean()) {
            return nodo.get("url").asText(); // devuelve solo la URL
        }
        throw new RuntimeException(nodo.get("mensaje").asText());
    }

    @Override
    protected void done() {
        for (JButton b : boton) b.setEnabled(true);
        try {
            callback.onSuccess(get()); // get() devuelve la URL directamente
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
}

