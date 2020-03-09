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
    }
    public void crearRuta(View v)
    {
        Intent intent = new Intent(MainActivity.this, CrearRuta.class);
        startActivity(intent);
    }
    public void editarRuta(View v)
    {
        Intent intent = new Intent(MainActivity.this, EditarRuta.class);
        startActivity(intent);
    }
    public void verLogin(View v)
    {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }
    public void verRegistrarse(View v)
    {
        Intent intent = new Intent(MainActivity.this, EditarRuta.class);
        startActivity(intent);
    }
}
