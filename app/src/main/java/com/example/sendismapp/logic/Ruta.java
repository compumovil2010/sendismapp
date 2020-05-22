package com.example.sendismapp.logic;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ruta implements Serializable, Parcelable {

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

    public Ruta(String nombre, String llavePropietario, String llaveRutaActual, double distancia, int dificultad, String duracion, int calificacion, List<LatLng> puntosRuta, List<PuntoDeInteres> puntosDeInteres) {
        this.nombre = nombre;
        this.llavePropietario = llavePropietario;
        this.llaveRutaActual = llaveRutaActual;
        this.distancia = distancia;
        this.dificultad = dificultad;
        this.duracion = duracion;
        this.calificacion = calificacion;
        this.puntosRuta = puntosRuta;
        this.puntosDeInteres = puntosDeInteres;
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

    /*Parceable*/
    protected Ruta(Parcel in) {
        nombre = in.readString();
        llavePropietario = in.readString();
        llaveRutaActual = in.readString();
        distancia = in.readDouble();
        dificultad = in.readInt();
        duracion = in.readString();
        calificacion = in.readInt();
        if (in.readByte() == 0x01) {
            puntosRuta = new ArrayList<LatLng>();
            in.readList(puntosRuta, LatLng.class.getClassLoader());
        } else {
            puntosRuta = null;
        }
        if (in.readByte() == 0x01) {
            puntosDeInteres = new ArrayList<PuntoDeInteres>();
            in.readList(puntosDeInteres, PuntoDeInteres.class.getClassLoader());
        } else {
            puntosDeInteres = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(llavePropietario);
        dest.writeString(llaveRutaActual);
        dest.writeDouble(distancia);
        dest.writeInt(dificultad);
        dest.writeString(duracion);
        dest.writeInt(calificacion);
        if (puntosRuta == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(puntosRuta);
        }
        if (puntosDeInteres == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(puntosDeInteres);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Ruta> CREATOR = new Parcelable.Creator<Ruta>() {
        @Override
        public Ruta createFromParcel(Parcel in) {
            return new Ruta(in);
        }

        @Override
        public Ruta[] newArray(int size) {
            return new Ruta[size];
        }
    };
}
