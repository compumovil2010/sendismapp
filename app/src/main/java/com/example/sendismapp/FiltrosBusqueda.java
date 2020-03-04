package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

public class FiltrosBusqueda extends AppCompatActivity {

    Dialog ver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros_busqueda);
    }

    public void clickFiltros(View v) {
        ver = new Dialog(this);
        ver.setContentView(R.layout.ly_filtros_busqueda);
        ver.show();
    }
}
