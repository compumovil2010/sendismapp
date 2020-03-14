package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comentario extends AppCompatActivity {

    Dialog ver;
    String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ver = new Dialog(this);
        ver.setContentView(R.layout.ly_comentario);
        ver.show();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        fecha = dateFormat.format(new Date());
        //Toast.makeText(this, fecha, Toast.LENGTH_SHORT).show();


    }
}
