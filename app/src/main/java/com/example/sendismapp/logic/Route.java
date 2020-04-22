package com.example.sendismapp.logic;

import org.json.JSONException;
import org.json.JSONObject;

public class Route {
    private String name;
    private double distance;//in m
    private int difficulty;
    private String duracion;
    private int calificacion;
    public Route(String name,double distance,int difficulty){
        this.difficulty=difficulty;
        this.distance=distance;
        this.name=name;
    }

    public Route()
    {

    }

    public JSONObject toJSON()
    {
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("nombre: ", this.name);
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
        return  "Nombre: " + this.name + '\n' +
                "Duración: " + this.duracion + '\n' +
                "Calificación: " + this.calificacion;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getDuracion() {
        return duracion;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }
}
