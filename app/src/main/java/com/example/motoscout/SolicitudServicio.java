package com.example.motoscout;
public class SolicitudServicio {
    private int idServicio;
    private String moto;
    private String tipoServicio;
    private String fechaSolicitud;
    private String estado;

    public SolicitudServicio(int idServicio, String moto, String tipoServicio, String fechaSolicitud, String estado) {
        this.idServicio = idServicio;
        this.moto = moto;
        this.tipoServicio = tipoServicio;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
    }

    // Getters y setters
    public int getIdServicio() { return idServicio; }
    public String getMoto() { return moto; }
    public String getTipoServicio() { return tipoServicio; }
    public String getFechaSolicitud() { return fechaSolicitud; }
    public String getEstado() { return estado; }

    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
    public void setMoto(String moto) { this.moto = moto; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }
    public void setFechaSolicitud(String fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public void setEstado(String estado) { this.estado = estado; }
}

