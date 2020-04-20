package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mapa_crear_ruta extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    /*Localization things*/
    private double latitud;
    private double longitud;
    static final int LOCATION_REQUEST = 8, REQUEST_CHECK_SETTINGS = 1;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    /*Light sensors*/
    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorListener;

    /*Searching*/
    private EditText edtBuscarDireccion;
    private String direccionBuscar;
    private Geocoder mGeocoder;
    public static final double lowerLeftLatitude = 1.396967;
    public static final double lowerLeftLongitude = -78.903968;
    public static final double upperRightLatitude = 11.983639;
    public static final double upperRigthLongitude = -71.869905;
    static final int RADIUS_OF_EARTH_KM = 6371;

    /*Elementos de creacion de mapas*/
    private Dialog dialogPupUp;
    private ImageView marcadorDificil;
    private ImageView selecDificil;
    private ImageView marcardorEnergia;
    private ImageView selecEnergia;
    private ImageView marcadorAguar;
    private ImageView selecAgua;
    private ImageView marcadorPaisaje;
    private ImageView selecPaisaje;

    /*Manipulacion de marcadores*/
    private ImageView basura;
    private Marker marcadorActual;
    private ArrayList<Marker> marcadoresRuta = new ArrayList<>();
    private int imagenSeleccionada = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_crear_ruta);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Get current location
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION","Location update in the callback: " + location + " !!!Latitud: " + latitud + " !!!!Longitud: " + longitud);
                if(location != null)
                {
                    Double distanciaDelPuntoAnterior = distance(location.getLatitude(),location.getLongitude(),latitud,longitud);
                    if(distanciaDelPuntoAnterior > 0.1)// ah caminado 100m
                    {
                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                        actualizarMapa();
                    }
                }
            }
        };
        if(requestPermisision(this, Manifest.permission.ACCESS_FINE_LOCATION, "Lo necesita la App", LOCATION_REQUEST))
            usarGps();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa_crear);
        mapFragment.getMapAsync(this);

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
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Mapa_crear_ruta.this, R.raw.style_json));
                    }
                    else
                    {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Mapa_crear_ruta.this, R.raw.style_light_json));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        //Searching
        edtBuscarDireccion = findViewById(R.id.edtBuscarDireccion);
        mGeocoder = new Geocoder(getBaseContext());
        edtBuscarDireccion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    direccionBuscar = edtBuscarDireccion.getText().toString();
                    if(!direccionBuscar.isEmpty())
                    {
                        try
                        {
                            List<Address> direcciones = mGeocoder.getFromLocationName(direccionBuscar, 2,
                                    lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRigthLongitude);
                            if (direcciones!= null && !direcciones.isEmpty())
                            {
                                Address direccionEncontrada = direcciones.get(0);
                                LatLng posicion = new LatLng(direccionEncontrada.getLatitude(),direccionEncontrada.getLongitude());
                                if(mMap != null)
                                {
                                    MarkerOptions mMarcador = new MarkerOptions();
                                    mMarcador.position(posicion);
                                    mMarcador.title(direccionEncontrada.getAddressLine(0));
                                    mMarcador.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                    mMap.addMarker(mMarcador);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion,14));
                                    double distanciaObjetivo = distance(latitud, longitud, posicion.latitude, posicion.longitude);
                                    Toast.makeText(Mapa_crear_ruta.this, "Distancia al nuevo punto: " + distanciaObjetivo + " Km", Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(getBaseContext(),"La direccion no fue encontrada", Toast.LENGTH_SHORT).show();
                            }
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });
        /*Inflando elementos de Creadion de rutas*/
        dialogPupUp = new Dialog(this);
        marcadorDificil = findViewById(R.id.marcadorDificil);
        selecDificil = findViewById(R.id.selecDificil);
        marcardorEnergia = findViewById(R.id.marcardorEnergia);
        selecEnergia = findViewById(R.id.selecEnergia);
        marcadorAguar = findViewById(R.id.marcadorAguar);
        selecAgua = findViewById(R.id.selecAgua);
        marcadorPaisaje = findViewById(R.id.marcadorPaisaje);
        selecPaisaje = findViewById(R.id.selecPaisaje);
        basura = findViewById(R.id.imgBasura);
        basura.setVisibility(View.INVISIBLE);
        /*Metodos para la manipulacion de marcadores*/

    }

    //Lifecycle
    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        settingsLocation();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
        stopLocationUpdates();
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
                            resolvable.startResolutionForResult(Mapa_crear_ruta.this, REQUEST_CHECK_SETTINGS);
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

    private void startLocationUpdates()
    {
        if(ContextCompat.checkSelfPermission(this ,Manifest.permission. ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void stopLocationUpdates()
    {
        if(mFusedLocationClient != null)
        {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private LocationRequest createLocationRequest()
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public void actualizarMapa()
    {
        if(mMap != null && (latitud+longitud != 0))
        {
            LatLng bogota= new LatLng(latitud, longitud);

            Marker nuevoM = mMap.addMarker(new MarkerOptions().position(bogota).title("Localizacion actual"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

            marcadoresRuta.add(nuevoM);
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
                        Log. i ("LOCATION","onSuccess location" + location);
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

        // Add a marker in Bogota and move the camera
        //LatLng bogota= new LatLng(latitud, longitud);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        /*mMap.addMarker(new MarkerOptions().position(bogota).title("Marker in Bogota"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));*/

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //mMap.clear();
                //LatLng bogota= new LatLng(latitud, longitud);
                //mMap.addMarker(new MarkerOptions().position(bogota).title("Localizacion actual"));
                int height = 100;
                int width = 100;
                Bitmap b = BitmapFactory.decodeResource(getResources(), imagenSeleccionada);
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                mMap.addMarker(new MarkerOptions().position(latLng).title(geoCoderSearchLatLang(latLng)).icon(smallMarkerIcon));
                //double distanciaObjetivo = distance(latitud, longitud, latLng.latitude, latLng.longitude);
                //Toast.makeText(Mapa_crear_ruta.this, "Punto de interes agregado " , Toast.LENGTH_LONG).show();
            }
        });
        /**/
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(Mapa_crear_ruta.this, "CLICK REGISTRADOOOOOOO " , Toast.LENGTH_LONG).show();
                basura.setVisibility(View.VISIBLE);
                marcadorActual = marker;
                return true;
            }
        });
    }

    public double distance(double lat1, double long1, double lat2, double long2)
    {
        double latDistance = Math. toRadians (lat1-lat2);
        double lngDistance = Math. toRadians (long1-long2);
        double a = Math. sin(latDistance / 2 ) * Math. sin(latDistance / 2)
                + Math.cos(Math. toRadians (lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2)*Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result*10000000.0)/10000000.0;
    }

    public String geoCoderSearchLatLang(LatLng direccion)
    {
        try
        {
            List<Address> direcciones = mGeocoder.getFromLocation(direccion.latitude, direccion.longitude, 2);
            if (direcciones!= null && !direcciones.isEmpty())
            {
                Address direccionEncontrada = direcciones.get(0);
                return direccionEncontrada.getAddressLine(0);
            }
            else
            {
                Toast.makeText(getBaseContext(),"La direccion no fue encontrada", Toast.LENGTH_SHORT).show();
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public void popUpInfoMarcadores(View v)
    {
        dialogPupUp.setContentView(R.layout.pop_up_info_marcadores);
        dialogPupUp.show();
    }

    /**ACCIONES DE BOTONES DE CREACION DE RUTAS**/

    public void clickMDificil (View v)
    {
        selecDificil.setVisibility(View.VISIBLE);
        selecAgua.setVisibility(View.INVISIBLE);
        selecEnergia.setVisibility(View.INVISIBLE);
        selecPaisaje.setVisibility(View.INVISIBLE);
        imagenSeleccionada = R.drawable.m_dificultad;
    }
    public void clickMAgua (View v)
    {
        selecDificil.setVisibility(View.INVISIBLE);
        selecAgua.setVisibility(View.VISIBLE);
        selecEnergia.setVisibility(View.INVISIBLE);
        selecPaisaje.setVisibility(View.INVISIBLE);
        imagenSeleccionada = R.drawable.water;
    }
    public void clickMEnergia (View v)
    {
        selecDificil.setVisibility(View.INVISIBLE);
        selecAgua.setVisibility(View.INVISIBLE);
        selecEnergia.setVisibility(View.VISIBLE);
        selecPaisaje.setVisibility(View.INVISIBLE);
        imagenSeleccionada = R.drawable.m_energia;
    }
    public void clickMPaisaje (View v)
    {
        selecDificil.setVisibility(View.INVISIBLE);
        selecAgua.setVisibility(View.INVISIBLE);
        selecEnergia.setVisibility(View.INVISIBLE);
        selecPaisaje.setVisibility(View.VISIBLE);
        imagenSeleccionada = R.drawable.m_paisaje;
    }

    /**Manipulacion de marcadores**/
    public void eliminarMarcador(View v)
    {
        marcadorActual.remove();
        basura.setVisibility(View.INVISIBLE);
    }

    public void armarRuta(View v)
    {
        int i=0;
        LatLng latlan1 = new LatLng(0,0);
        LatLng latlan2 = new LatLng(0,0);
        for(Marker mActual: marcadoresRuta)
        {
            if(i==0)
            {
                latlan1 = mActual.getPosition();
                i=i+1;
            }

            else
            {
                latlan2 = mActual.getPosition();
                Polyline line = mMap.addPolyline(new PolylineOptions()// Creacion de la linea entre los dos ultimos marcadores
                        .add(latlan1,latlan2)
                        .width(5)
                        .color(Color.RED));
                latlan1 = latlan2;
            }
        }
    }
}
