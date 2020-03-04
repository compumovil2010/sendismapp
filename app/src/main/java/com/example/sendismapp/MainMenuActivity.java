package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    Button botonCrear;
    Button botonRecorrer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        botonCrear = findViewById(R.id.button_create_route);
        botonRecorrer = findViewById(R.id.button_recorrer);

        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, CrearRuta.class);
                startActivity(intent);
            }
        });

        botonRecorrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, ActivityVerRutas.class);
                startActivity(intent);
            }
        });

    }
}
