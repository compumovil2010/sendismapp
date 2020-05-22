package com.example.sendismapp.logic;

import java.io.Serializable;

public class PuntoDeInteres implements Serializable
{
    private Double latitud;
    private Double longitud;
    private int icono;//R.id.icono identificador del icono dentro del codigo

    public PuntoDeInteres()
    {

    }

    public PuntoDeInteres(Double latitud, Double longitud, int icono) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.icono = icono;
    }

    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public int getIcono() {
        return icono;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }
}
