package com.alfredo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageData {
    public String id;
    public String title;
    public String url;
    public int width;
    public int height;

    @JsonProperty("url_viewer") // Mapea el nombre del JSON al de Java
    public String urlViewer;

    public ImageDetails image;
    public ImageDetails thumb;

    @JsonProperty("delete_url")
    public String deleteUrl;
}
