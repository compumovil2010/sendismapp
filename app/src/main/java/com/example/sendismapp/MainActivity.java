package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bienvenido a SendisMapp");
        Intent intent = new Intent(MainActivity.this, Servicio.class);
        startService(intent);

    }

    public void verLogin(View v)
    {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }
    public void verRegistrarse(View v)
    {
        Intent intent = new Intent(MainActivity.this, Registrarse.class);
        startActivity(intent);
    }

    public void clickDeUnaAlMenu(View v) {
        Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
        startActivity(intent);
    }

}
