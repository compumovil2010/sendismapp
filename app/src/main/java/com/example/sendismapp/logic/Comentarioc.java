package com.example.sendismapp.logic;

import java.text.SimpleDateFormat;

public class Comentarioc {
    private String comentario;
    private String fecha;
    private String imagen;
    private String creador;

    public Comentarioc(String comentario, String fecha, String imagen, String creador){
        this.comentario = comentario;
        this.fecha = fecha;
        this.imagen = imagen;
        this.creador = creador;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }
}
