package com.example.sendismapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HistorialRutas extends AppCompatActivity {

    private Button botonCalificar;
    private Button botonComentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_rutas);

        botonCalificar = findViewById(R.id.buttonCalificar);
        botonComentar = findViewById(R.id.buttonComentar);

        botonCalificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        botonComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}
