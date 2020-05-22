package com.example.sendismapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sendismapp.logic.MarcadorRuta;
import com.example.sendismapp.logic.PuntoDeInteres;
import com.example.sendismapp.logic.Ruta;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreVisualizarRuta extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Ruta rutaSeleccionada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_visualizar_ruta);

        ///Manejo del Intent
        Intent intent = getIntent();
        //rutaSeleccionada = (Ruta) intent.getSerializableExtra("RutaSeleccionada");
        rutaSeleccionada = (Ruta) intent.getParcelableExtra("RutaSeleccionada");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Toast.makeText(this,"Ruta actual: " + rutaSeleccionada.getNombre(), Toast.LENGTH_LONG).show();
        rePintarRuta();

    }

    public void rePintarRuta()
    {
        int height = 100;
        int width = 100;
        List<PuntoDeInteres> marcadores = rutaSeleccionada.getPuntosDeInteres();
        List<LatLng> puntosRuta = rutaSeleccionada.getPuntosRuta();
        for(int i = 0; i < puntosRuta.size(); i++)
        {
            LatLng puntoActual = puntosRuta.get(i);
            mMap.addMarker(new MarkerOptions().position(puntoActual).title("Punto " + i));
            if(i == 0)
            {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(puntoActual));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            }
        }
        for(int i = 0; i < marcadores.size(); i++)
        {
            Bitmap b = BitmapFactory.decodeResource(getResources(), marcadores.get(i).getIcono());
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
            mMap.addMarker(new MarkerOptions().position(new LatLng(marcadores.get(i).getLatitud(), marcadores.get(i).getLongitud())).icon(smallMarkerIcon));
        }
        armarRuta(puntosRuta);
    }
    public void armarRuta(List<LatLng> marcadoresRuta) {
        int i = 0;
        LatLng latlan1 = new LatLng(0, 0);
        LatLng latlan2 = new LatLng(0, 0);
        for (LatLng mActual : marcadoresRuta) {
            if (i == 0) {
                latlan1 = mActual;
                i = i + 1;
            } else {
                latlan2 = mActual;
                Polyline line = mMap.addPolyline(new PolylineOptions()// Creacion de la linea entre los dos ultimos marcadores
                        .add(latlan1, latlan2)
                        .width(10)
                        .color(Color.RED));
                latlan1 = latlan2;
            }
        }
    }

    public void empezarRecorrido(View view)
    {
        // here we are go again
    }
}
