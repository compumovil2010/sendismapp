package com.example.sendismapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuPrincipal extends AppCompatActivity {

    private Button botonBuscarRuta;
    private Button botonVerMisRutas;
    private Button botonCrearRutas;
    private Button botonHistorialRutas;
    private Button botonBuscarAmigo;
    private Button botonVerPerfil;
    private Button botonVerNotificaciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
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

            }
        });
        botonVerNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
