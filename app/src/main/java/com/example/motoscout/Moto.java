package com.example.motoscout;

public class Moto {
    private int id;
    private String marca;
    private String modelo;
    private int anio;
    private int kilometraje;
    private String imagenUrl; // ahora es URL o path en servidor

    public Moto(int id, String marca, String modelo, int anio, int kilometraje, String imagenUrl) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.kilometraje = kilometraje;
        this.imagenUrl = imagenUrl;
    }

    // Getters
    public int getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAnio() { return anio; }
    public int getKilometraje() { return kilometraje; }
    public String getImagenUrl() { return imagenUrl; }
}
