package com.example.sendismapp.logic;

import androidx.annotation.NonNull;

public class Calificacionc {
    private float calificacion;
    private String usuario;
    private String comentario;
    private String dificultad;
    private String fecha;
    private String ruta;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }



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


    public String toString() {
        return "Usuario :" + usuario +"\n"+
                "Ruta :" + ruta +"\n"+
                "Fecha :" + fecha +"\n"+
                "Calificacion :" + calificacion +"\n"+
                "Dificultad :" + dificultad +"\n"+
                "Comentario :" + comentario;
    }
}
