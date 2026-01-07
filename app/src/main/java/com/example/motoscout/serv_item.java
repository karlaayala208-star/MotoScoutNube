package com.example.motoscout;

public class serv_item {
    private int imagenResId;
    private String ubicacion;
    private String fecha;
    private String servicio;
    private String precio;

    public serv_item(int imagenResId, String ubicacion, String fecha, String servicio, String precio) {
        this.imagenResId = imagenResId;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.servicio = servicio;
        this.precio = precio;
    }

    public int getImagenResId() { return imagenResId; }
    public String getUbicacion() { return ubicacion; }
    public String getFecha() { return fecha; }
    public String getServicio() { return servicio; }
    public String getPrecio() { return precio; }
}

