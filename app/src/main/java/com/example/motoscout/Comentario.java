package com.example.motoscout;

public class Comentario {
    private String usuario;
    private float calificacion;
    private String comentario;

    public Comentario(String usuario, float calificacion, String comentario) {
        this.usuario = usuario;
        this.calificacion = calificacion;
        this.comentario = comentario;
    }

    public String getUsuario() {
        return usuario;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public String getComentario() {
        return comentario;
    }
}

