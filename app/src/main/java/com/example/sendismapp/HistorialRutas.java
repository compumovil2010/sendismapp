package com.example.sendismapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sendismapp.logic.HistorialRuta;
import com.example.sendismapp.logic.MiRuta;

public class HistorialRutas extends AppCompatActivity {

    private Button botonCalificar;
    private Button botonComentar;
    private HistorialRuta[] misRutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_rutas);

        botonCalificar = findViewById(R.id.buttonCalificar);
        botonComentar = findViewById(R.id.buttonComentar);

        iniciarArreglo();
        ArrayAdapter<HistorialRuta> adapter = new ArrayAdapter<HistorialRuta>(this,
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

        botonCalificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistorialRutas.this, Calificacion.class);
                startActivity(intent);
            }
        });

        botonComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistorialRutas.this, Comentario.class);
                startActivity(intent);
            }
        });

    }

    public void iniciarArreglo ()
    {
        misRutas = new HistorialRuta[15];
        String [] nombre = {
                "Monserrate", "Guadalupe", "Chia", "Cajica", "Mosquera"
        };
        String descripcion= "La ruta es de nivel avanzado , dura 3 horas";
        MiRuta aux;
        for (int i = 0; i<misRutas.length;i++)
        {
            misRutas[i] = new HistorialRuta(nombre[i%5],descripcion);

        }
    }
}
