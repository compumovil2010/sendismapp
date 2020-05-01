package com.example.sendismapp.logic;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Ruta {

    private String nombre;
    private String llavePropietario;
    private  String llaveRutaActual;
    private double distancia = 0.0;
    private int dificultad = 0;
    private String duracion = "Corta";
    private int calificacion = 0;
    private List<LatLng> puntosRuta;
    private List<PuntoDeInteres> puntosDeInteres;

    public Ruta(String name, double distance, int difficulty){
        this.dificultad =difficulty;
        this.distancia =distance;
        this.nombre =name;
    }

    public Ruta()
    {

    }

    public JSONObject toJSON()
    {
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("nombre: ", this.nombre);
            obj.put("duracion: ", this.duracion);
            obj.put("calificacion: ", this.calificacion);
        }catch(JSONException e)
        {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public String toString()
    {
        return  "Nombre: " + this.nombre + '\n' +
                "Duración: " + this.duracion + '\n' +
                "Calificación: " + this.calificacion;
    }

    /**GETTERS**/

    public String getNombre() {
        return nombre;
    }

    public String getLlavePropietario() {
        return llavePropietario;
    }

    public String getLlaveRutaActual() {
        return llaveRutaActual;
    }

    public double getDistancia() {
        return distancia;
    }

    public int getDificultad() {
        return dificultad;
    }

    public String getDuracion() {
        return duracion;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public List<LatLng> getPuntosRuta() {
        return puntosRuta;
    }

    public List<PuntoDeInteres> getPuntosDeInteres() {
        return puntosDeInteres;
    }

    /**SETTERS**/

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setLlavePropietario(String llavePropietario) {
        this.llavePropietario = llavePropietario;
    }

    public void setLlaveRutaActual(String llaveRutaActual) {
        this.llaveRutaActual = llaveRutaActual;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public void setDificultad(int dificultad) {
        this.dificultad = dificultad;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public void setPuntosRuta(List<LatLng> puntosRuta) {
        this.puntosRuta = puntosRuta;
    }

    public void setPuntosDeInteres(List<PuntoDeInteres> puntosDeInteres) {
        this.puntosDeInteres = puntosDeInteres;
    }
}
