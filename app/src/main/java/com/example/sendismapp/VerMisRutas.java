package com.example.sendismapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class VerMisRutas extends AppCompatActivity {

    private Button botonEmpezar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mis_rutas);

        botonEmpezar = findViewById(R.id.buttonEmpezar);

        botonEmpezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerMisRutas.this, HacerRecorrido.class);
                startActivity(intent);
            }
        });
    }
}
