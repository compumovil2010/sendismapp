package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.sendismapp.adapters.RouteAdapter;
import com.example.sendismapp.logic.Ruta;

import java.util.ArrayList;
import java.util.List;

public class ResultRoutesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_routes);
        ListView lv = (ListView) findViewById(R.id.list_layout_search_result);

        // Instanciating an array list (you don't need to do this,
        // you already have yours).
        List<Ruta> routes = new ArrayList<Ruta>();
        routes.add(new Ruta("Primera ruta",1,2));
        routes.add(new Ruta("Segundo ejemplo",1,2));
        routes.add(new Ruta("tercer ejemplo",1,2));

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        RouteAdapter adapter=new RouteAdapter(this, routes);

        lv.setAdapter(adapter);
    }
}
