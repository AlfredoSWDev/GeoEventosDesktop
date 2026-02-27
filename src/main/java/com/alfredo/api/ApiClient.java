package com.alfredo.api;

import com.alfredo.conf.AppConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    // Lee la URL base desde config.properties
    private static final String BASE_URL = AppConfig.get("api.base.url");
    private static final HttpClient client = HttpClient.newHttpClient();

    // ── GET ─────────────────────────────────────────────────────────────────
    public static String get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("GET " + endpoint + " → " + response.statusCode());
        return response.body();
    }

    // ── POST ─────────────────────────────────────────────────────────────────
    public static int post(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("POST " + endpoint + " → " + response.statusCode());
        return response.statusCode();
    }

    // ── PUT ──────────────────────────────────────────────────────────────────
    public static int put(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("PUT " + endpoint + " → " + response.statusCode());
        return response.statusCode();
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    public static int delete(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("DELETE " + endpoint + " → " + response.statusCode());
        return response.statusCode();
    }

    // ── POST multipart (para subir imágenes) ─────────────────────────────────
    public static String postMultipart(String endpoint, String rutaArchivo) throws Exception {
        String boundary = "----FormBoundary" + System.currentTimeMillis();

        java.nio.file.Path path = java.nio.file.Path.of(rutaArchivo);
        byte[] fileBytes = java.nio.file.Files.readAllBytes(path);
        String fileName = path.getFileName().toString();

        // Construir el cuerpo multipart manualmente
        byte[] partHeader = ("--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"archivo\"; filename=\"" + fileName + "\"\r\n"
                + "Content-Type: application/octet-stream\r\n\r\n").getBytes();

        byte[] partFooter = ("\r\n--" + boundary + "--\r\n").getBytes();

        byte[] body = new byte[partHeader.length + fileBytes.length + partFooter.length];
        System.arraycopy(partHeader, 0, body, 0, partHeader.length);
        System.arraycopy(fileBytes, 0, body, partHeader.length, fileBytes.length);
        System.arraycopy(partFooter, 0, body, partHeader.length + fileBytes.length, partFooter.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("POST multipart " + endpoint + " → " + response.statusCode());
        return response.body();
    }
}
