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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sendismapp.logic.MarcadorRuta;
import com.example.sendismapp.logic.PuntoDeInteres;
import com.example.sendismapp.logic.Ruta;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class EdicionFinalRuta extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Ruta rutaActualEditar;

    /*Localization things*/
    private double latitud;
    private double longitud;
    static final int LOCATION_REQUEST = 8, REQUEST_CHECK_SETTINGS = 1, REQUEST_IMAGE_CAPTURE = 3, CAMERA = 2;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private boolean empiezaRecorrido = false;
    private Marker posicionAnterior = null;
    private FusedLocationProviderClient mFusedLocationClient;

    /*Light sensors*/
    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorListener;

    /*Travel things*/
    private LatLng puntoFinal;
    static final int RADIUS_OF_EARTH_KM = 6371;
    private boolean rutaYaFueRecorrida = false;
    private String rutasRecorridas = null;

    /*Firebase things*/
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private final static String PATH_HISOTIAL = "historialRutas/";

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
    private List<PuntoDeInteres> puntosDeInteres = new ArrayList<PuntoDeInteres>();
    private List<Marker> marcadoresRuta = new ArrayList<>();
    private JSONArray marcadores = new JSONArray();
    private String imagenSeleccionada = "";
    private int resID = 0;
    private double latitudAzul;
    private double longitudAzul;

    /*Manipulacion de la camara*/
    private ImageButton btnFoto;
    static boolean accesoCamara = false;

    /*Searching things*/
    private Geocoder mGeocoder;

    /*Firebase*/
    private final static String PATH_ROUTES = "rutas/";
    private Ruta rutaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicion_final_ruta);

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

        /*Preseleccion de marcadores*/
        selecDificil.setVisibility(View.VISIBLE);
        selecAgua.setVisibility(View.INVISIBLE);
        selecEnergia.setVisibility(View.INVISIBLE);
        selecPaisaje.setVisibility(View.INVISIBLE);
        imagenSeleccionada = "m_dificultad";

        /*Uso de camara*/
        btnFoto = findViewById(R.id.btnFoto);
        btnFoto.setVisibility(View.INVISIBLE);

        //Manejo Intent
        Intent intent = getIntent();
        rutaActualEditar = (Ruta) intent.getParcelableExtra("RutaSeleccionada");

        //Firebase
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //Searching
        mGeocoder = new Geocoder(getBaseContext());


        //Active additional sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mMap != null) {
                    if (event.values[0] < 5000) {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(EdicionFinalRuta.this, R.raw.style_json));
                    } else {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(EdicionFinalRuta.this, R.raw.style_light_json));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Get current location
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                latitudAzul = location.getLatitude();
                longitudAzul = location.getLongitude();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {
                    if (location.getLatitude() != latitud || location.getLongitude() != longitud) {
                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                        actualizarMapa();
                    }
                }
            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa_editar);
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

        rePintarRuta();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                int height = 100;
                int width = 100;
                resID = getResources().getIdentifier(imagenSeleccionada, "drawable", getPackageName());
                Bitmap b = BitmapFactory.decodeResource(getResources(), resID);
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                mMap.addMarker(new MarkerOptions().position(latLng).title(geoCoderSearchLatLang(latLng)).icon(smallMarkerIcon));

                //Manejo de puntos de interes globales
                PuntoDeInteres nuevoPI = new PuntoDeInteres(latLng.latitude, latLng.longitude, imagenSeleccionada);
                puntosDeInteres.add(nuevoPI);
            }
        });
        /**/
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                basura.setVisibility(View.VISIBLE);
                btnFoto.setVisibility(View.VISIBLE);
                marcadorActual = marker;
                return true;
            }
        });
    }

    public void popUpInfoMarcadores(View v) {
        dialogPupUp.setContentView(R.layout.pop_up_info_marcadores);
        dialogPupUp.show();
    }

    /**
     * ACCIONES DE BOTONES DE CREACION DE RUTAS
     **/

    public void clickMDificil(View v) {
        String nameIcon = "m_dificultad";
        selecDificil.setVisibility(View.VISIBLE);
        selecAgua.setVisibility(View.INVISIBLE);
        selecEnergia.setVisibility(View.INVISIBLE);
        selecPaisaje.setVisibility(View.INVISIBLE);
        //imagenSeleccionada = R.drawable.m_dificultad;
        imagenSeleccionada = "m_dificultad";
    }

    public void clickMAgua(View v) {
        selecDificil.setVisibility(View.INVISIBLE);
        selecAgua.setVisibility(View.VISIBLE);
        selecEnergia.setVisibility(View.INVISIBLE);
        selecPaisaje.setVisibility(View.INVISIBLE);
        imagenSeleccionada = "water";
    }

    public void clickMEnergia(View v) {
        selecDificil.setVisibility(View.INVISIBLE);
        selecAgua.setVisibility(View.INVISIBLE);
        selecEnergia.setVisibility(View.VISIBLE);
        selecPaisaje.setVisibility(View.INVISIBLE);
        imagenSeleccionada = "m_energia";
    }

    public void clickMPaisaje(View v) {
        selecDificil.setVisibility(View.INVISIBLE);
        selecAgua.setVisibility(View.INVISIBLE);
        selecEnergia.setVisibility(View.INVISIBLE);
        selecPaisaje.setVisibility(View.VISIBLE);
        imagenSeleccionada = "m_paisaje";
    }

    /**
     * Manipulacion de marcadores
     **/
    public void eliminarMarcador(View v) {
        //Remover de los puntos de la ruta
        marcadoresRuta.remove(marcadorActual);
        marcadorActual.remove();
        //Remover de los puntos de interes de la ruta
        PuntoDeInteres eliminado = buscarPuntoInteres(marcadorActual.getPosition());
        puntosDeInteres.remove(eliminado);

        basura.setVisibility(View.INVISIBLE);
        btnFoto.setVisibility(View.INVISIBLE);
    }

    public PuntoDeInteres buscarPuntoInteres(LatLng coordenadas) {
        for (PuntoDeInteres p : puntosDeInteres) {
            if (p.getLatitud() == coordenadas.latitude && p.getLongitud() == coordenadas.longitude) {
                return p;
            }
        }
        return null;
    }

    public void rePintarRuta() {
        int height = 100;
        int width = 100;
        String mDrawableName = "";
        List<PuntoDeInteres> marcadores = rutaActualEditar.getPuntosDeInteres();
        List<LatLng> puntosRuta = rutaActualEditar.getPuntosRuta();

        /*Contextualizando*/
        puntosDeInteres = rutaActualEditar.getPuntosDeInteres();

        for (int i = 0; i < puntosRuta.size(); i++) {
            LatLng puntoActual = puntosRuta.get(i);
            marcadoresRuta.add(mMap.addMarker(new MarkerOptions().position(puntoActual).title("Punto " + i)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(puntoActual));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        for (int j = 0; j < marcadores.size(); j++) {
            mDrawableName = marcadores.get(j).getIcono();
            int resID = getResources().getIdentifier(mDrawableName, "drawable", getPackageName());
            Bitmap b = BitmapFactory.decodeResource(getResources(), resID);
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
            mMap.addMarker(new MarkerOptions().position(new LatLng(marcadores.get(j).getLatitud(), marcadores.get(j).getLongitud())).icon(smallMarkerIcon));
        }

        puntoFinal = puntosRuta.get(puntosRuta.size() - 1);
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
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        settingsLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void settingsLocation() {
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
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(EdicionFinalRuta.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private boolean requestPermisision(Activity contexto, String permiso, String justificacion, int codigo) {
        if (ContextCompat.checkSelfPermission(contexto, permiso) != PackageManager.PERMISSION_GRANTED) {
            //Justification of the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(contexto, permiso)) {
                Toast.makeText(getApplicationContext(), justificacion, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(contexto, new String[]{permiso}, codigo);
            return false;
        } else
            return true;
    }

    public void onRequestPermissionsResult(int reqCode, String permisos[], int[] grantResul) {
        switch (reqCode) {
            case LOCATION_REQUEST: {
                if (grantResul.length > 0 && grantResul[0] == PackageManager.PERMISSION_GRANTED) {
                    usarGps();
                }
                break;
            }
            case CAMERA: {
                if (grantResul.length > 0 && grantResul[0] == PackageManager.PERMISSION_GRANTED) {
                    tomarFoto();
                } else {
                    Toast.makeText(getApplicationContext(), "El permiso era necesario", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void usarGps() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.i("LOCATION", "onSuccess location");
                        if (location != null) {
                            latitud = location.getLatitude();
                            longitud = location.getLongitude();
                            actualizarMapa();
                            //Toast.makeText(getBaseContext(),"We got it!: " + latitud + " , " + longitud,Toast. LENGTH_LONG ).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Activa el GPS por favor :)!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void actualizarMapa() {
        if (mMap != null) {
            if (posicionAnterior != null) {
                posicionAnterior.remove();
            }
            LatLng bogota = new LatLng(latitud, longitud);

            posicionAnterior = mMap.addMarker(new MarkerOptions().position(bogota).title("Localizacion actual").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    /**
     * COSAS GEOCODER
     **/
    public String geoCoderSearchLatLang(LatLng direccion) {
        try {
            List<Address> direcciones = mGeocoder.getFromLocation(direccion.latitude, direccion.longitude, 2);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address direccionEncontrada = direcciones.get(0);
                return direccionEncontrada.getAddressLine(0);
            } else {
                Toast.makeText(getBaseContext(), "La direccion no fue encontrada", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Acciones de la camara
     */
    public void desplegarCamara(View v) {
        accesoCamara = requestPermisision(this, Manifest.permission.CAMERA,
                "Quiero la camarita, deja de negarte y damela >:v!!", CAMERA);
        if (accesoCamara)
            tomarFoto();
    }

    public void agregarMarcadorRuta(View v) {
        LatLng bogota = new LatLng(latitudAzul, longitudAzul);

        Marker nuevoM = mMap.addMarker(new MarkerOptions().position(bogota).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        marcadoresRuta.add(nuevoM);
    }

    public void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void guardarRuta(View v) {
        //Almacenamiento Local

        double lat, lng;
        /*for (Marker mActual : marcadoresRuta) //Llenar el Array con los marcadores de la ruta (Rojos)
        {
            lat = mActual.getPosition().latitude;
            lng = mActual.getPosition().longitude;
            MarcadorRuta miMarcador = new MarcadorRuta(lat, lng);
            marcadores.put(miMarcador.toJSON());
        }*/

        //Almacenamiento en Firebase
        List<LatLng> posicionesMarcadoresRuta = new ArrayList<LatLng>();

        for (Marker m : marcadoresRuta) {
            posicionesMarcadoresRuta.add(m.getPosition());
        }

        myRef = database.getReference(PATH_ROUTES + user.getUid() + "/" + rutaActualEditar.getLlaveRutaActual());
        rutaActualEditar.setPuntosDeInteres(puntosDeInteres);
        rutaActualEditar.setPuntosRuta(posicionesMarcadoresRuta);
        myRef.setValue(rutaActualEditar);

        Toast.makeText(getApplicationContext(), "Ruta actualizada", Toast.LENGTH_LONG).show();
    }

    public void eliminarRuta(View v)
    {
        dialogPupUp.setContentView(R.layout.pop_up_info_eliminacion_ruta);
        dialogPupUp.show();
    }

    public void eliminarParaSiempre(View v)
    {
        myRef = database.getReference(PATH_ROUTES + user.getUid() + "/" + rutaActualEditar.getLlaveRutaActual());
        myRef.setValue(null);
        Toast.makeText(getApplicationContext(), "Ruta eliminada", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(EdicionFinalRuta.this, MenuPrincipal.class);
        startActivity(intent);
    }
}
