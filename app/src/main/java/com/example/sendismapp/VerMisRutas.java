package com.example.sendismapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sendismapp.logic.MiRuta;
import com.example.sendismapp.logic.PuntoDeInteres;
import com.example.sendismapp.logic.Ruta;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VerMisRutas extends AppCompatActivity {

    private MiRuta[] misRutas;
    private List<Ruta> rutasGlobales = new ArrayList<Ruta>();
    private boolean archivoDisponible = false;
    static final String nombreArchivo = "rutas2.json";
    private ArrayAdapter adaptador;
    /*Manejo de Firebase*/
    private final static String PATH_ROUTES = "rutas/";
    private DatabaseReference myRef;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mis_rutas);
        database = FirebaseDatabase.getInstance();
        //iniciarArreglo();

        adaptador = new ArrayAdapter<Ruta>(this, android.R.layout.simple_list_item_1, rutasGlobales);
        ListView listView = findViewById(R.id.listaMisRutas);
        listView.setAdapter(adaptador);
        try {
            llenarArreglo2();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(archivoDisponible)//Si el JSOn existe y tiene rutas almacenadas
        {
            listView.setAdapter(adaptador);
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
            //misRutas2 = new Ruta[marcadoresGloriosos.length()];
            for (int i = 0; i < marcadoresGloriosos.length(); i++) {
                JSONObject ru = marcadoresGloriosos.getJSONObject(i);
                String nombre = ru.getString("nombre: ");
                String duracion = ru.getString("duracion: ");
                String calificacion = ru.getString("calificacion: ");
                Ruta ruN = new Ruta();
                ruN.setNombre(nombre);
                ruN.setDuracion(duracion);
                ruN.setCalificacion(Integer.parseInt(calificacion));
                rutasGlobales.add(ruN);

                Log.e("OMG", "Resultado lectura: " + nombre);

            }
        }
        cargarRutasFirebase();
    }

    public void cargarRutasFirebase()
    {
        myRef = database.getReference(PATH_ROUTES);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*RESULTADO FINAL*/
                List<Ruta> rutasActuales = new ArrayList<Ruta>();

                /*ATRIBUTOS DE UNA RUTA*/
                String nombre = "";
                String llavePropietario = "";
                String llaveRutaActual = "";
                double distancia = 0.0;
                int dificultad = 0;
                String duracion = "Corta";
                int calificacion = 0;
                List<LatLng> puntosRuta = new ArrayList<LatLng>();
                List<PuntoDeInteres> puntosDeInteres = new ArrayList<PuntoDeInteres>();

                for(DataSnapshot singleSnapshot:dataSnapshot.getChildren())//Bolsita de usuarios
                {
                    Map<String, Object> IDUserxRutas = (Map<String, Object>) singleSnapshot.getValue();
                    Log.e("ERR: ", "Encontro un ID: " + IDUserxRutas);
                    for(Map.Entry<String, Object> entidad : IDUserxRutas.entrySet())//Bolsita de Rutas
                    {
                        archivoDisponible = true;
                        Map<String, Object> mapRutas = (Map<String, Object>) entidad.getValue();
                        Log.e("ARR: ", "Encontro rutas: " + mapRutas);
                        Log.e("ARR2: ", "Encontro uina llave ruta: " + mapRutas.get("llaveRutaActual"));
                        calificacion = ((Long)mapRutas.get("calificacion")).intValue();
                        llaveRutaActual = (String)mapRutas.get("llaveRutaActual");
                        distancia = ((Long)mapRutas.get("distancia")).doubleValue();
                        duracion = (String)mapRutas.get("duracion");
                        puntosDeInteres = (List<PuntoDeInteres>)mapRutas.get("puntosDeInteres");
                        nombre = (String)mapRutas.get("nombre");
                        dificultad = ((Long)mapRutas.get("dificultad")).intValue();
                        puntosRuta = (List<LatLng>)mapRutas.get("puntosRuta");
                        llavePropietario = (String)mapRutas.get("llavePropietario");
                        Ruta rutaLeida = new Ruta(nombre, llavePropietario, llaveRutaActual, distancia, dificultad, duracion, calificacion, puntosRuta, puntosDeInteres);
                        rutasActuales.add(rutaLeida);
                        adaptador.notifyDataSetChanged();
                        rutasGlobales.add(rutaLeida);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
