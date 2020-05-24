package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.example.sendismapp.logic.PuntoDeInteres;
import com.example.sendismapp.logic.Ruta;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class PreVisualizarRuta extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    /*Localization things*/
    private double latitud;
    private double longitud;
    static final int LOCATION_REQUEST = 8, REQUEST_CHECK_SETTINGS = 1;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private boolean empiezaRecorrido = false;
    private Marker posicionAnterior = null;

    private Ruta rutaSeleccionada;

    /*Light sensors*/
    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorListener;

    /*Travel things*/
    private LatLng puntoFinal;
    static final int RADIUS_OF_EARTH_KM = 6371;
    private Dialog dialogPupUp;
    private boolean rutaYaFueRecorrida = false;
    private String rutasRecorridas = null;

    /*Firebase things*/
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private final static String PATH_HISOTIAL = "historialRutas/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_visualizar_ruta);

        //Firebase
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //Travel
        rutaRecorrida();

        //INFLATE
        dialogPupUp = new Dialog(this);

        //Active additional sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(mMap != null)
                {
                    if(event.values[0] < 5000)
                    {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PreVisualizarRuta.this, R.raw.style_json));
                    }
                    else
                    {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PreVisualizarRuta.this, R.raw.style_light_json));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Get current location
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION","Location update in the callback: " + location);
                if(location != null)
                {
                    if(location.getLatitude() != latitud || location.getLongitude()!= longitud)
                    {
                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                        if(empiezaRecorrido)
                            actualizarMapa();
                    }
                }
            }
        };

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

        Toast.makeText(this,"Ruta actual: " + rutaSeleccionada.getNombre(), Toast.LENGTH_LONG).show();
        rePintarRuta();

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    public void rePintarRuta()
    {
        int height = 100;
        int width = 100;
        String mDrawableName = "";
        List<PuntoDeInteres> marcadores = rutaSeleccionada.getPuntosDeInteres();
        List<LatLng> puntosRuta = rutaSeleccionada.getPuntosRuta();
        for(int i = 0; i < puntosRuta.size(); i++)
        {
            LatLng puntoActual = puntosRuta.get(i);
            mMap.addMarker(new MarkerOptions().position(puntoActual).title("Punto " + i));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(puntoActual));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        for(int j = 0; j < marcadores.size(); j++)
        {
            mDrawableName = marcadores.get(j).getIcono();
            int resID = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
            Bitmap b = BitmapFactory.decodeResource(getResources(), resID);
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
            mMap.addMarker(new MarkerOptions().position(new LatLng(marcadores.get(j).getLatitud(), marcadores.get(j).getLongitud())).icon(smallMarkerIcon));
        }

        puntoFinal = puntosRuta.get(puntosRuta.size()-1);
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

    //Manejo del mapa y del recorrido
    //Lifecycle
    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        settingsLocation();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
        stopLocationUpdates();
    }
    public void empezarRecorrido(View view)
    {
        empiezaRecorrido = true;
        if(requestPermisision(this, Manifest.permission.ACCESS_FINE_LOCATION, "Lo necesita la App", LOCATION_REQUEST))
            usarGps();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void stopLocationUpdates()
    {
        if(mFusedLocationClient != null)
        {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void settingsLocation()
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch(statusCode)
                {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try{
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(PreVisualizarRuta.this, REQUEST_CHECK_SETTINGS);
                        }catch(IntentSender.SendIntentException sendEx)
                        {
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private LocationRequest createLocationRequest()
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void startLocationUpdates()
    {
        if(ContextCompat.checkSelfPermission(this , Manifest.permission. ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private boolean requestPermisision(Activity contexto, String permiso, String justificacion, int codigo)
    {
        if(ContextCompat.checkSelfPermission(contexto, permiso)!= PackageManager.PERMISSION_GRANTED)
        {
            //Justification of the permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(contexto,permiso))
            {
                Toast.makeText(getApplicationContext(),justificacion, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(contexto, new String[]{permiso},codigo);
            return false;
        }
        else
            return true;
    }

    public void onRequestPermissionsResult(int reqCode, String permisos[], int[] grantResul)
    {
        switch (reqCode)
        {
            case LOCATION_REQUEST:
            {
                if(grantResul.length > 0 && grantResul[0] == PackageManager.PERMISSION_GRANTED)
                {
                    usarGps();
                }
                break;
            }
        }
    }

    public void usarGps()
    {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this ,
                new OnSuccessListener<Location>()
                {
                    @Override
                    public void onSuccess(Location location)
                    {
                        Log. i ("LOCATION","onSuccess location");
                        if (location != null )
                        {
                            latitud = location.getLatitude();
                            longitud = location.getLongitude();
                            actualizarMapa();
                            //Toast.makeText(getBaseContext(),"We got it!: " + latitud + " , " + longitud,Toast. LENGTH_LONG ).show();
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(),"Activa el GPS por favor :)!",Toast. LENGTH_LONG ).show();
                        }
                    }
                });
    }

    public void actualizarMapa()
    {
        if(mMap != null)
        {
            if(posicionAnterior != null)
            {
                posicionAnterior.remove();
            }
            LatLng bogota= new LatLng(latitud, longitud);

            posicionAnterior = mMap.addMarker(new MarkerOptions().position(bogota).title("Localizacion actual").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

            double distanciPuntoFinal = distance(latitud, longitud, puntoFinal.latitude, puntoFinal.longitude);
            if(distanciPuntoFinal < 0.05 && !rutaYaFueRecorrida)//A cincuenta metros de la meta
            {
                if(rutasRecorridas == null || rutasRecorridas.isEmpty())
                {
                    rutasRecorridas = rutaSeleccionada.getLlaveRutaActual();
                }
                else
                    rutasRecorridas = rutasRecorridas + "," + rutaSeleccionada.getLlaveRutaActual();
                //Logica FireBase escribir en el Historial.
                myRef = database.getReference(PATH_HISOTIAL + user.getUid() + "/");
                myRef.setValue(rutasRecorridas);
                rutaRecorrida();
                //PopUp informativo
                popUpInfoFinal();
                rutaYaFueRecorrida = true;
            }
        }
    }

    public void rutaRecorrida()//Verificar si en Firebase ya recorri esta ruta
    {
        myRef = database.getReference(PATH_HISOTIAL);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Map<String, Object> mapaHistorial = (Map<String, Object>) dataSnapshot.getValue();
                Log.e("ERR23: ", "Encontro un ID: " + mapaHistorial.get(user.getUid()));
                rutasRecorridas = (String) mapaHistorial.get(user.getUid());
                String[] splitOn = rutasRecorridas.split(",");
                for(String iter : splitOn)
                {
                    if(iter.equals(rutaSeleccionada.getLlaveRutaActual()))
                    {
                        Log.e("ERR24", "La ruta ya fue recorrida :(!!!!");
                        rutaYaFueRecorrida = true;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result * 10000000.0) / 10000000.0;
    }

    public void pasarHistorial(View view)
    {
        Intent intent = new Intent(PreVisualizarRuta.this, HistorialRutas.class);
        startActivity(intent);
    }

    public void popUpInfoFinal() {
        dialogPupUp.setContentView(R.layout.pop_up_info_fin_ruta);
        dialogPupUp.show();
    }
}
