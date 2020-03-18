package com.example.sendismapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditarRuta extends AppCompatActivity {

    private Dialog dialogPupUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ruta);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Editar Ruta");

        dialogPupUp = new Dialog(this);
    }

    public void popUpInfoMarcadores(View v)
    {
        dialogPupUp.setContentView(R.layout.pop_up_info_marcadores);
        dialogPupUp.show();
    }
}
