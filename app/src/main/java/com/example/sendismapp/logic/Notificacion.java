package com.example.sendismapp.logic;

import java.util.Date;

public class Notificacion {

    private String usuario;
    private String fecha;
    private String ruta;

    public Notificacion(String usuario, String fecha,String ruta) {
        this.usuario = usuario;
        this.fecha = fecha;
        this.ruta = ruta;
    }
    public Notificacion(){

    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

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

    public  String toString(){
        return  "Usuario: " + this.usuario + '\n' +
                "Ruta: " + this.ruta + '\n'+
                "Fecha:" + this.fecha;
    }
}
