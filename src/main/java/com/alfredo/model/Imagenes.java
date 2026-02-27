package com.alfredo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Imagenes {
    public boolean success;
    public int status;
    public ImageData data;
}

