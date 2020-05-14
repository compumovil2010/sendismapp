package com.example.sendismapp.logic;

import java.util.Date;

public class Notificacion {

    private String usuario;
    private Date fecha;
    private String ruta;

    public Notificacion(String usuario, Date fecha,String ruta) {
        this.usuario = usuario;
        this.fecha = fecha;
    }
    public Notificacion(){

    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
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
                "Fecha: " + this.fecha.toString() + '\n' +
                "Ruta: " + this.ruta + '\n';
    }
}
