package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sendismapp.logic.Ruta;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class GuardarRutaJSON extends AppCompatActivity {
    private EditText edtNombre;
    private EditText edtDuaracion;
    private EditText edtCalificacion;
    private Button btnConfirmar;
    static final String nombreArchivo = "rutas2.json";
    private JSONArray rutas = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_guardar_ruta);

        /*Inflar GUI*/
        edtDuaracion = findViewById(R.id.edtDuracion);
        edtNombre = findViewById(R.id.edtNombre);
        btnConfirmar = findViewById(R.id.btnAceptarNombreMapa);
    }

    public void grabarNuevaRuta(View v) {
        String nombre = edtNombre.getText().toString();
        String duracion = edtDuaracion.getText().toString();

        Ruta nuevaRuta = new Ruta();
        nuevaRuta.setCalificacion(5);
        nuevaRuta.setNombre(nombre);
        nuevaRuta.setDuracion(duracion);

        rutas.put(nuevaRuta.toJSON());
        Writer output = null;
        try {
            File file = new File(this.getFilesDir().getAbsolutePath(), nombreArchivo);
            //File file = new File(this.getCacheDir(), nombreArchivo);
            if(!file.exists()) {
                file.mkdir();
            }
            Log.e("ARCHIVO", "Ubicacion de archivo :" + file);
            output = new BufferedWriter(new FileWriter(file));
            output.write(rutas.toString());
            output.close();
            /*WARNING*/

        } catch (Exception e) {
        }
        Intent intent = new Intent(GuardarRutaJSON.this,MenuPrincipal.class);
        startActivity(intent);
    }
}
