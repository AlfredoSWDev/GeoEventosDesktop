package com.alfredo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageDetails {
    public String filename;
    public String extension;
    public String mime;
    public String url;
}
