package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BuscarRutas extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros_busqueda);
    }

    public void clickBuscarRutas(View v) {
        Intent intent = new Intent(BuscarRutas.this, ResultRoutesActivity.class);
        startActivity(intent);
    }
}
