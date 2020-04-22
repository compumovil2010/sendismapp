package com.example.sendismapp.logic;

import org.json.JSONException;
import org.json.JSONObject;

public class MarcadorRuta
{
    private double latitud;
    private double longitud;

    public MarcadorRuta(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public JSONObject toJSON()
    {
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("Latitud: ", this.latitud);
            obj.put("Longitud ", this.longitud);
        }catch(JSONException e)
        {
            e.printStackTrace();
        }
        return obj;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
