package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sendismapp.logic.Comentarioc;
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
    private DatabaseReference myRef2;
    public static final String PATH_NOTIFICATION="notificaciones/";
    private ArrayList<Comentarioc> notificaciones;
    private String nickname;
    private int nick=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacionc);
        notificaciones = new ArrayList<Comentarioc>();
        leerFB();
    }

    public void mostrarArreglo ()
    {
        if(nick==0)
        {
            nick=1;
            buscarnick();
        }
        else {

            Log.e("OMG", "Resultado lectura: " + notificaciones.size());
            ArrayAdapter<Comentarioc> adapter = new ArrayAdapter<Comentarioc>(this,
                    android.R.layout.simple_list_item_1, notificaciones);
            ListView listView = (ListView) findViewById(R.id.listaNotificacionesComentarios);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getBaseContext(), verComentarios.class);
                    intent.putExtra("comentario", notificaciones.get(position).getComentario());
                    intent.putExtra("nombreRuta", notificaciones.get(position).getRuta());
                    intent.putExtra("creador", notificaciones.get(position).getCreador());

                    //intent.putExtra("propietario",rutas.get(position).getLlavePropietario());
                    startActivity(intent);
                }
            });
        }
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
                        Comentarioc aux= new Comentarioc();
                        aux.setRuta(single2.child("ruta").getValue().toString());
                        aux.setFecha(single2.child("fecha").getValue().toString());
                        aux.setCreador(single2.child("creador").getValue().toString());
                        aux.setComentario(single2.child("comentario").getValue().toString());
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

    public void buscarnick ()
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

            myRef2 = database.getReference("users/");

            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot single : dataSnapshot.getChildren()) {
                        String codigo = single.getKey();
                        nickname=single.child("nickname").getValue().toString();

                        for (int i =0; i<notificaciones.size();i++)
                        {
                            Comentarioc aux = notificaciones.get(i);

                            String creador = aux.getCreador();
                            if (codigo.equals(creador)  )
                            {
                                aux.setCreador(nickname);
                                notificaciones.set(i,aux);
                            }
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
