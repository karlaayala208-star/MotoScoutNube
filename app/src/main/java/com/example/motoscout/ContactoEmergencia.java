package com.example.motoscout;

public class ContactoEmergencia {
    private int idContacto;
    private int idUsuario;
    private String nombreContacto;
    private String telefonoContacto;

    public ContactoEmergencia(int idContacto, int idUsuario, String nombreContacto, String telefonoContacto) {
        this.idContacto = idContacto;
        this.idUsuario = idUsuario;
        this.nombreContacto = nombreContacto;
        this.telefonoContacto = telefonoContacto;
    }

    // Getters y setters

    public int getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(int idContacto) {
        this.idContacto = idContacto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }
}

