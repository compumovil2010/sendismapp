package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.sendismapp.logic.Notificacion;
import com.example.sendismapp.logic.Ruta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Notificaciones extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public static final String PATH_NOTIFICATION="notificaciones/";
    private ArrayList<Notificacion> notificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

        notificaciones = new ArrayList<Notificacion>();
    }

    private void leerFB()
    {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(PATH_NOTIFICATION);
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
                        //rutas.add(aux);

                        single.getKey();
                        Log.e("OMG", "Resultado lectura: " + single2.child("nombre").getValue().toString());
                        //Log.e("OMG", "Resultado lectura: " + rutas.size());
                    }
                }
                ///mostrarArreglo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
