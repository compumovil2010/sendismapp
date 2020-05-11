package com.example.sendismapp.logic;

public class Calificacionc {
    private float calificacion;
    private String usuario;

    public Calificacionc(int calificacion, String usuario) {
        this.calificacion = calificacion;
        this.usuario = usuario;
    }
    public Calificacionc(){

    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
