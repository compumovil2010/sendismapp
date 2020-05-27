package com.example.sendismapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sendismapp.logic.PuntoDeInteres;
import com.example.sendismapp.logic.Ruta;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditarRuta extends AppCompatActivity {

    private List<Ruta> rutasGlobales = new ArrayList<Ruta>();
    private ArrayAdapter adaptador;

    /*Manejo de Firebase*/
    private final static String PATH_ROUTES = "rutas/";
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_ruta_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adaptador = new ArrayAdapter<Ruta>(this, android.R.layout.simple_list_item_1, rutasGlobales);
        ListView listView = findViewById(R.id.listaMisRutas2);
        listView.setAdapter(adaptador);

        //Firebase
        database = FirebaseDatabase.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        //iniciarArreglo();
        llenarArreglo();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.e("Posicion",rutasGlobales.get(position).getNombre());
                Intent intent = new Intent(getBaseContext(), EdicionFinalRuta.class);
                intent.putExtra("RutaSeleccionada", (Parcelable) rutasGlobales.get(position));
                startActivity(intent);
            }
        });
    }

    public void llenarArreglo() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(PATH_ROUTES + user.getUid() + "/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot single : dataSnapshot.getChildren())
                {
                    Ruta aux= new Ruta();
                    List<LatLng> puntosRuta = new ArrayList<LatLng>();
                    List<PuntoDeInteres> puntosDeInteres = new ArrayList<PuntoDeInteres>();

                    aux.setCalificacion(Integer.parseInt(single.child("calificacion").getValue().toString()));
                    aux.setDificultad(Integer.parseInt(single.child("dificultad").getValue().toString()));
                    aux.setDistancia(Double.parseDouble(single.child("distancia").getValue().toString()));
                    aux.setDuracion(single.child("duracion").getValue().toString());
                    aux.setLlavePropietario(single.child("llavePropietario").getValue().toString());
                    aux.setLlaveRutaActual(single.child("llaveRutaActual").getValue().toString());
                    aux.setNombre(single.child("nombre").getValue().toString());

                    Map<String, Object> mapRutas = (Map<String, Object>) single.getValue();

                    //Manejo de Listas de coordenadas
                    List<Object> puntosInutiles = (List<Object>) mapRutas.get("puntosRuta");
                    assert puntosInutiles != null;
                    for(Object obj : puntosInutiles)
                    {
                        Map<String, Object> mapAux = (Map<String, Object>) obj;
                        LatLng latLng = new LatLng((double) mapAux.get("latitude"), (double) mapAux.get("longitude"));
                        puntosRuta.add(latLng);
                    }
                    List<Object> marcadoresInutiles = (List<Object>) mapRutas.get("puntosDeInteres");
                    if(marcadoresInutiles != null)
                    {
                        for(Object obj2 : marcadoresInutiles)
                        {
                            Map<String, Object> mapAux = (Map<String, Object>) obj2;
                            String icono = (String) mapAux.get("icono");
                            PuntoDeInteres puntoNuevo = new PuntoDeInteres((double) mapAux.get("latitud"), (double) mapAux.get("longitud"),icono);
                            puntosDeInteres.add(puntoNuevo);
                        }
                    }

                    aux.setPuntosRuta(puntosRuta);
                    aux.setPuntosDeInteres(puntosDeInteres);

                    adaptador.notifyDataSetChanged();
                    rutasGlobales.add(aux);
                }
                //mostrarArreglo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}