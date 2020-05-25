package com.example.sendismapp;

import android.content.Intent;
import android.graphics.Color;
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
    private String ruta = " ";
    private String nombreRuta = " ";
    private String propietario = " ";
    private ListView listView;

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
                if (ruta != " ")
                {
                    Intent intent = new Intent(HistorialRutas.this, Calificacion.class);
                    intent.putExtra("ruta",ruta);
                    intent.putExtra("nombreRuta",nombreRuta);
                    intent.putExtra("propietario",propietario);
                    ruta = " ";
                    nombreRuta= " ";
                    propietario = " ";
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(HistorialRutas.this, "No ha seleccionado ninguna ruta" ,Toast.LENGTH_SHORT).show();
                }

            }
        });

        botonComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ruta != " ")
                {
                    Intent intent = new Intent(HistorialRutas.this, Comentario.class);
                    intent.putExtra("ruta",ruta);
                    intent.putExtra("nombreRuta",nombreRuta);
                    intent.putExtra("propietario",propietario);
                    ruta = " ";
                    nombreRuta= " ";
                    propietario = " ";
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(HistorialRutas.this, "No ha seleccionado ninguna ruta" ,Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarArreglo();

    }


    public void mostrarArreglo ()
    {
        Log.e("OMG", "Resultado lectura: " + rutas.size());
        ArrayAdapter<Ruta> adapter = new ArrayAdapter<Ruta>(this,
                android.R.layout.simple_list_item_1, rutas);
        listView = (ListView) findViewById(R.id.listaHistorial);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try{
                    for (int ctr=0;ctr<=rutas.size();ctr++)
                    {
                        if(position==ctr)
                        {

                            listView.getChildAt(ctr).setBackgroundColor(Color.rgb(137,206,250));
                            ruta = rutas.get(position).getLlaveRutaActual();
                            nombreRuta= rutas.get(position).getNombre();
                            propietario=rutas.get(position).getLlavePropietario();

                        }
                        else
                            {
                                listView.getChildAt(ctr).setBackgroundColor(Color.WHITE);
                            }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

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
