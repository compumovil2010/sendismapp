package com.example.sendismapp.logic;

public class Calificacionc {
    private float calificacion;
    private String usuario;
    private String comentario;
    private String dificultad;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public Calificacionc(float calificacion, String usuario, String comentario, String dificultad) {
        this.calificacion = calificacion;
        this.usuario = usuario;
        this.comentario = comentario;
        this.dificultad = dificultad;
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
