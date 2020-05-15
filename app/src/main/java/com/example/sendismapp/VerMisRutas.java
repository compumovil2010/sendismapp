package com.example.sendismapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sendismapp.logic.MiRuta;
import com.example.sendismapp.logic.Ruta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class VerMisRutas extends AppCompatActivity {

    private MiRuta[] misRutas;
    private Ruta[] misRutas2;
    private boolean archivoDisponible = false;
    static final String nombreArchivo = "rutas.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mis_rutas);

        //iniciarArreglo();
        try {
            llenarArreglo2();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(archivoDisponible)//Si el JSOn existe y tiene rutas almacenadas
        {
            ArrayAdapter<Ruta> adapter = new ArrayAdapter<Ruta>(this,
                    android.R.layout.simple_list_item_1, misRutas2);
            ListView listView = (ListView) findViewById(R.id.listaMisRutas);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getBaseContext(), verRuta.class);
                    startActivity(intent);
                }
            });
        }
        else
        {
            Toast.makeText(this,"No se han creado rutas de forma local", Toast.LENGTH_LONG).show();
        }
        /*ArrayAdapter<MiRuta> adapter = new ArrayAdapter<MiRuta>(this,
                android.R.layout.simple_list_item_1, misRutas);
        ListView listView = (ListView) findViewById(R.id.lista);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), verRuta.class);
                startActivity(intent);
            }
        });*/


    }

    public String leerJson() {
        String json = null;
        try {
            InputStream is = this.openFileInput(nombreArchivo);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    public void llenarArreglo2() throws JSONException
    {
        String retorno = leerJson();
        if(retorno != null) {
            archivoDisponible = true;
            Log.e("OMG", "Resultado otra funcion: " + retorno);
            JSONArray marcadoresGloriosos = new JSONArray(retorno);
            misRutas2 = new Ruta[marcadoresGloriosos.length()];
            for (int i = 0; i < marcadoresGloriosos.length(); i++) {
                JSONObject ru = marcadoresGloriosos.getJSONObject(i);
                String nombre = ru.getString("nombre: ");
                String duracion = ru.getString("duracion: ");
                String calificacion = ru.getString("calificacion: ");
                Ruta ruN = new Ruta();
                ruN.setNombre(nombre);
                ruN.setDuracion(duracion);
                ruN.setCalificacion(Integer.parseInt(calificacion));
                misRutas2[i] = ruN;

                Log.e("OMG", "Resultado lectura: " + nombre);

            }
        }
    }

    public void iniciarArreglo()
    {
        misRutas = new MiRuta[15];
        String[] nombre = {
                "Monserrate", "Guadalupe", "Chia", "Cajica", "Mosquera"
        };
        Double calificacion;
        int hora;
        int minutos;
        int segundos;

        MiRuta aux;
        for (int i = 0; i < misRutas.length; i++) {
            calificacion = Double.valueOf((i + 3) % 2);
            hora = (i + 1) % 2;
            minutos = (i * 10) % 60;
            segundos = (i * 100) % 60;
            misRutas[i] = new MiRuta(nombre[i % 5], calificacion, hora, minutos, segundos);

        }
    }
}
