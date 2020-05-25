package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sendismapp.logic.Notificacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Notificacionc extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public static final String PATH_NOTIFICATION="notificaciones/";
    private ArrayList<Notificacion> notificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacionc);
        notificaciones = new ArrayList<Notificacion>();
        leerFB();
    }

    public void mostrarArreglo ()
    {
        Log.e("OMG", "Resultado lectura: " + notificaciones.size());
        ArrayAdapter<Notificacion> adapter = new ArrayAdapter<Notificacion>(this,
                android.R.layout.simple_list_item_1, notificaciones);
        ListView listView = (ListView) findViewById(R.id.listaNotificacionesComentarios);
        listView.setAdapter(adapter);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), Calificacion.class);
                //intent.putExtra("ruta",rutas.get(position).getLlaveRutaActual());
                //intent.putExtra("nombreRuta",rutas.get(position).getNombre());
                //intent.putExtra("propietario",rutas.get(position).getLlavePropietario());
                startActivity(intent);
            }
        });*/
    }

    private void leerFB()
    {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("notificacionesC/"+user.getUid());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot single : dataSnapshot.getChildren())
                {
                    for (DataSnapshot single2 : single.getChildren()) {
                        Notificacion aux= new Notificacion();
                        aux.setRuta(single2.child("ruta").getValue().toString());
                        aux.setFecha(single2.child("fecha").getValue().toString());
                        aux.setUsuario(single2.child("usuario").getValue().toString());
                        notificaciones.add(aux);

                        single.getKey();
                        //Log.e("OMG", "Resultado lectura: " + aux.getFecha().toString());
                        //Log.e("OMG", "Resultado lectura: " + rutas.size());
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
