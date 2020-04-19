package com.example.sendismapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuPrincipal extends AppCompatActivity {

    private Button botonBuscarRuta;
    private Button botonVerMisRutas;
    private Button botonCrearRutas;
    private Button botonHistorialRutas;
    private Button botonBuscarAmigo;
    private Button botonVerPerfil;
    private Button botonVerNotificaciones;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        botonBuscarRuta = findViewById(R.id.buttonBuscarRuta);
        botonVerMisRutas = findViewById(R.id.buttonVerRutas);
        botonCrearRutas = findViewById(R.id.buttonCrearRuta);
        botonHistorialRutas = findViewById(R.id.buttonHistorial);
        botonBuscarAmigo = findViewById(R.id.buttonBuscarAmigo);
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
        botonBuscarAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                    }
                    return false;
                }
            });
        }
    }

}
