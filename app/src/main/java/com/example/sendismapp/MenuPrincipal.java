package com.example.sendismapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sendismapp.logic.Notificacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {

    private Button botonBuscarRuta;
    private Button botonVerMisRutas;
    private Button botonCrearRutas;
    private Button botonHistorialRutas;
    private Button botonVerPerfil;
    private Button botonVerNotificaciones;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private Toolbar toolbar;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/");
        botonBuscarRuta = findViewById(R.id.buttonBuscarRuta);
        botonVerMisRutas = findViewById(R.id.buttonVerRutas);
        botonCrearRutas = findViewById(R.id.buttonCrearRuta);
        botonHistorialRutas = findViewById(R.id.buttonHistorial);
        botonVerPerfil = findViewById(R.id.buttonPerfil);
        botonVerNotificaciones = findViewById(R.id.buttonNotificacion);

        botonBuscarRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this,BuscarRutas.class);
                startActivity(intent);
            }
        });
        botonVerMisRutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this,VerMisRutas.class);
                startActivity(intent);

            }
        });
        botonCrearRutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this,Mapa_crear_ruta.class);
                startActivity(intent);
            }
        });
        botonHistorialRutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this,HistorialRutas.class);
                startActivity(intent);

            }
        });
        botonVerPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this,PerfilActivity.class);
                startActivity(intent);
            }
        });
        botonVerNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, Notificaciones.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        if(toolbar!=null)setSupportActionBar(toolbar);

    }
    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }
    @Override
    protected void onPause() {
        super.onPause();
        toolbar = null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.cerrar_sesion, menu);
        return true;
    }

    public void setSupportActionBar(Toolbar myToolbar) {
        toolbar.setTitle("Menu principal");
        if (currentUser != null){
            toolbar.inflateMenu(R.menu.cerrar_sesion);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.itemCerrarSesion) {
                        mAuth.signOut();
                        Intent intent = new Intent(MenuPrincipal.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;
                    }else if(item.getItemId() == R.id.itemBorrarUsuario){
                        currentUser.delete();
                        Intent intent = new Intent(MenuPrincipal.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        eUsuario();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void botonComentarProblema(View view) {
        Intent intent = new Intent(MenuPrincipal.this, Notificacionc.class);
        startActivity(intent);
    }

    public void eUsuario(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot single : dataSnapshot.getChildren()){
                    if(single.getKey().equals(currentUser.getUid())){
                        myRef.child(single.getKey()).setValue(null);
                    }

                }
                Toast.makeText(MenuPrincipal.this, "Se borró este usuario",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MenuPrincipal.this, "No se pudo borrar este usuario",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
