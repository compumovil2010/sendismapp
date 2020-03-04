package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ActivityVerRutas extends AppCompatActivity {
    String arreglo [];
    Button botonBuscar;
    Button botonEditar;
    Dialog ver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arreglo = new String[5];
        arreglo[0]= "Monserrate";
        arreglo[1]= "Guadalupe";
        arreglo[2]= "Chia";
        arreglo[3]= "Sibate";
        arreglo[4]= "Facatativa";


        setContentView(R.layout.activity_ver_rutas);

        botonBuscar = findViewById(R.id.idBotonBuscarRutas );
        botonEditar = findViewById(R.id.idBotonEditarRutas);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,arreglo);
        ListView listView = (ListView) findViewById(R.id.idListaRutas);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActivityVerRutas.this, verRuta.class);
                startActivity(intent);
            }
        });

        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ver = new Dialog(ActivityVerRutas.this);
                ver.setContentView(R.layout.ly_filtros_busqueda);
                ver.show();
            }
        });

       botonEditar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(ActivityVerRutas.this, EditarRuta.class);
               startActivity(intent);
           }
       });


    }
}
