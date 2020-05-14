package com.example.sendismapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sendismapp.logic.HistorialRuta;
import com.example.sendismapp.logic.MiRuta;
import com.example.sendismapp.logic.Ruta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistorialRutas extends AppCompatActivity {

    private Button botonCalificar;
    private Button botonComentar;
    private HistorialRuta[] misRutas;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public static final String PATH_ROUTES="rutas/";
    private ArrayList<Ruta> rutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_rutas);
        rutas = new ArrayList<Ruta>();
        botonCalificar = findViewById(R.id.buttonCalificar);
        botonComentar = findViewById(R.id.buttonComentar);
        leerFB();
        Log.e("OMG", "Resultado lectura: " + rutas.size());



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
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
}
