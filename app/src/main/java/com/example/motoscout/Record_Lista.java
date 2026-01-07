package com.example.motoscout;

public class Record_Lista {
    private int imageResource;
    private String titulo;
    private String fecseg;

    public Record_Lista(int imageResource, String titulo, String fecseg) {
        this.imageResource = imageResource;
        this.titulo = titulo;
        this.fecseg = fecseg;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getFecseg() {
        return fecseg;
    }
}
