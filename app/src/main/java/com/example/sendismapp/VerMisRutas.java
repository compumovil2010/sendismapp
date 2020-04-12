package com.example.sendismapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sendismapp.logic.MiRuta;

import java.util.ArrayList;

public class VerMisRutas extends AppCompatActivity {

    private MiRuta[] misRutas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mis_rutas);

        iniciarArreglo();
        ArrayAdapter<MiRuta> adapter = new ArrayAdapter<MiRuta>(this,
                android.R.layout.simple_list_item_1, misRutas);
        ListView listView = (ListView) findViewById(R.id.lista);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), verRuta.class);
                startActivity(intent);
            }
        });


    }
    public void iniciarArreglo ()
    {
        misRutas = new MiRuta[15];
        String [] nombre = {
                "Monserrate", "Guadalupe", "Chia", "Cajica", "Mosquera"
        };
        Double calificacion;
        int hora;
        int minutos;
        int segundos;

        MiRuta aux;
        for (int i = 0; i<misRutas.length;i++)
        {
            calificacion = Double.valueOf((i+3)%2);
            hora = (i + 1)%2;
            minutos = (i*10)%60;
            segundos = (i*100)%60;
            misRutas[i] = new MiRuta(nombre[i%5],calificacion,hora,minutos,segundos);

        }
    }
}
