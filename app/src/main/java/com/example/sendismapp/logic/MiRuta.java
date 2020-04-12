package com.example.sendismapp.logic;

public class MiRuta {
    private String nombre;
    private Double calificacionPromedio;
    private int hora;
    private int minutos;
    private int segundos;

    public MiRuta (String nombre,Double calificacionPromedio, int hora, int minutos,int segundos)
    {
        this.nombre = nombre;
        this.calificacionPromedio = calificacionPromedio;
        this.hora = hora;
        this.minutos = minutos;
        this.segundos = segundos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getCalificacionPromedio() {
        return calificacionPromedio;
    }

    public void setCalificacionPromedio(Double calificacionPromedio) {
        this.calificacionPromedio = calificacionPromedio;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public int getSegundos() {
        return segundos;
    }

    public void setSegundos(int segundos) {
        this.segundos = segundos;
    }

    @Override
    public String toString() {
        return
                "Nombre :" + nombre + '\n' +
                "Calificacion Promedio :" + calificacionPromedio + '\n'+
                "Duracion : " + hora +
                ":" + minutos +
                ":" + segundos
                ;
    }
}
