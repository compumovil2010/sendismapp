package com.example.sendismapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sendismapp.logic.HistorialRuta;
import com.example.sendismapp.logic.MiRuta;
import com.example.sendismapp.logic.Ruta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HistorialRutas extends AppCompatActivity {

    private Button botonCalificar;
    private Button botonComentar;
    private HistorialRuta[] misRutas;
    public static final String PATH_ROUTES="rutas/";
    private ArrayList<Ruta> rutas;

    private final static String PATH_HISOTIAL = "historialRutas/";

    //Feeding the listView
    private List<String> llavesRutasRecorridas = new ArrayList<String>();
    private String rutasRecorridas = "";

    //Firebase things
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_rutas);
        //Firebase
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        rutas = new ArrayList<Ruta>();
        botonCalificar = findViewById(R.id.buttonCalificar);
        botonComentar = findViewById(R.id.buttonComentar);
        leerHistorial();
        Log.e("OMG", "Resultado lectura: " + rutas.size());
        if(rutas.size() == 0)
        {
            Toast.makeText(this,"Usted no ha recorrido ninguna ruta", Toast.LENGTH_LONG).show();
        }



        //iniciarArreglo();
        botonCalificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistorialRutas.this, Calificacion.class);
                startActivity(intent);
            }
        });

        botonComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistorialRutas.this, Comentario.class);
                startActivity(intent);
            }
        });

    }
    public void mostrarArreglo ()
    {
        Log.e("OMG", "Resultado lectura: " + rutas.size());
        ArrayAdapter<Ruta> adapter = new ArrayAdapter<Ruta>(this,
                android.R.layout.simple_list_item_1, rutas);
        ListView listView = (ListView) findViewById(R.id.listaHistorial);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), Calificacion.class);
                intent.putExtra("ruta",rutas.get(position).getLlaveRutaActual());
                intent.putExtra("nombreRuta",rutas.get(position).getNombre());
                intent.putExtra("propietario",rutas.get(position).getLlavePropietario());
                startActivity(intent);
            }
        });
    }


    public void iniciarArreglo ()
    {
        misRutas = new HistorialRuta[15];
        String [] nombre = {
                "Monserrate", "Guadalupe", "Chia", "Cajica", "Mosquera"
        };
        String descripcion= "La ruta es de nivel avanzado , dura 3 horas";
        MiRuta aux;
        for (int i = 0; i<misRutas.length;i++)
        {
            misRutas[i] = new HistorialRuta(nombre[i%5],descripcion);

        }
    }
    private void leerFB()
    {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(PATH_ROUTES);
        myRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot single : dataSnapshot.getChildren())
                {
                    for (DataSnapshot single2 : single.getChildren()) {
                        Ruta aux= new Ruta();
                        aux.setCalificacion(Integer.parseInt(single2.child("calificacion").getValue().toString()));
                        aux.setDificultad(Integer.parseInt(single2.child("dificultad").getValue().toString()));
                        aux.setDistancia(Double.parseDouble(single2.child("distancia").getValue().toString()));
                        aux.setDuracion(single2.child("duracion").getValue().toString());
                        aux.setLlavePropietario(single2.child("llavePropietario").getValue().toString());
                        aux.setLlaveRutaActual(single2.child("llaveRutaActual").getValue().toString());
                        aux.setNombre(single2.child("nombre").getValue().toString());
                        if(buscarRuta(aux.getLlaveRutaActual()))
                            rutas.add(aux);

                        single.getKey();
                        Log.e("OMG", "Resultado lectura: " + single2.child("nombre").getValue().toString());
                        Log.e("OMG", "Resultado lectura: " + rutas.size());
                    }
                }
                mostrarArreglo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void leerHistorial()
    {
        myRef = database.getReference(PATH_HISOTIAL);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Map<String, Object> mapaHistorial = (Map<String, Object>) dataSnapshot.getValue();
                Log.e("ERR23: ", "Encontro un ID: " + mapaHistorial.get(user.getUid()));
                rutasRecorridas = (String) mapaHistorial.get(user.getUid());
                if(rutasRecorridas!=null)
                {
                    String[] splitOn = rutasRecorridas.split(",");
                    llavesRutasRecorridas.addAll(Arrays.asList(splitOn));
                }
                leerFB();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public boolean buscarRuta(String llaveRuta)
    {
        for(String iter : llavesRutasRecorridas)
        {
            if(iter.equals(llaveRuta))//La ruta fue recorrida por el usuario.
                return true;
        }
        return false;
    }
}
