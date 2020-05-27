package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.widget.EditText;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Mapa_crear_ruta extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    /*Localization things*/
    private double latitud;
    private double longitud;
    private double latitudAzul;
    private double longitudAzul;
    static final int LOCATION_REQUEST = 8, REQUEST_CHECK_SETTINGS = 1, CAMERA = 2,REQUEST_IMAGE_CAPTURE = 3;
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
    private List<PuntoDeInteres> puntosDeInteres = new ArrayList<PuntoDeInteres>();
    private List<Marker> marcadoresRuta = new ArrayList<>();
    private JSONArray marcadores = new JSONArray();
    private String imagenSeleccionada = "";
    private int resID = 0;

    /*Experimento para manipular JSON*/
    private Context parent = this;
    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;
    private Object outputObject;
    private String filePath;
    private String nombreArchivo;

    /*Manipulacion de la camara*/
    private ImageButton btnFoto;
    static boolean accesoCamara = false;

    /*Firebase*/
    private final static String PATH_ROUTES = "rutas/";

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private Ruta rutaActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_crear_ruta);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Get current location
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location + " !!!Latitud: " + latitud + " !!!!Longitud: " + longitud);
                latitudAzul = location.getLatitude();
                longitudAzul = location.getLongitude();
                if (location != null) {
                    Double distanciaDelPuntoAnterior = distance(location.getLatitude(), location.getLongitude(), latitud, longitud);
                    if (distanciaDelPuntoAnterior > 0.2)// ah caminado 200m
                    {
                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                        actualizarMapa();
                    }
                }
            }
        };
        if (requestPermisision(this, Manifest.permission.ACCESS_FINE_LOCATION, "Lo necesita la App", LOCATION_REQUEST))
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
                if (mMap != null) {
                    if (event.values[0] < 5000) {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Mapa_crear_ruta.this, R.raw.style_json));
                    } else {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Mapa_crear_ruta.this, R.raw.style_light_json));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        //Searching
        edtBuscarDireccion = findViewById(R.id.edtBuscarDireccion);
        mGeocoder = new Geocoder(getBaseContext());
        edtBuscarDireccion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    nombreArchivo = edtBuscarDireccion.getText().toString();
                    nombreArchivo = nombreArchivo + ".json";
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

        /*Preseleccion de marcadores*/
        selecDificil.setVisibility(View.VISIBLE);
        selecAgua.setVisibility(View.INVISIBLE);
        selecEnergia.setVisibility(View.INVISIBLE);
        selecPaisaje.setVisibility(View.INVISIBLE);
        imagenSeleccionada = "m_dificultad";

        /*Uso de camara*/
        btnFoto = findViewById(R.id.btnFoto);
        btnFoto.setVisibility(View.INVISIBLE);

        /*Firebase*/
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(rutaActual == null)
        {
            myRef = database.getReference(PATH_ROUTES + user.getUid());
            rutaActual = new Ruta();
            String key = myRef.push().getKey();
            rutaActual.setLlaveRutaActual(key);
        }
    }

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
                            resolvable.startResolutionForResult(Mapa_crear_ruta.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public void actualizarMapa() {
        if (mMap != null && (latitud + longitud != 0)) {
            LatLng bogota = new LatLng(latitud, longitud);

            Marker nuevoM = mMap.addMarker(new MarkerOptions().position(bogota).title("Localizacion actual"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            marcadoresRuta.add(nuevoM);
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
            case CAMERA:{
                if(grantResul.length > 0 && grantResul[0] == PackageManager.PERMISSION_GRANTED)
                {
                    tomarFoto();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"El permiso era necesario", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void usarGps() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.i("LOCATION", "onSuccess location" + location);
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
        if (requestPermisision(this, Manifest.permission.ACCESS_FINE_LOCATION, "Lo necesita la App", LOCATION_REQUEST))
            usarGps();
        LatLng bogota= new LatLng(latitud, longitud);

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
                int height = 100;
                int width = 100;
                resID = getResources().getIdentifier(imagenSeleccionada , "drawable", getPackageName());
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

    public PuntoDeInteres buscarPuntoInteres(LatLng coordenadas)
    {
        for(PuntoDeInteres p: puntosDeInteres)
        {
            if(p.getLatitud() == coordenadas.latitude && p.getLongitud() == coordenadas.longitude)
            {
                return p;
            }
        }
        return null;
    }

    public void armarRuta(View v) {
        int i = 0;
        LatLng latlan1 = new LatLng(0, 0);
        LatLng latlan2 = new LatLng(0, 0);
        for (Marker mActual : marcadoresRuta) {
            if (i == 0) {
                latlan1 = mActual.getPosition();
                i = i + 1;
            } else {
                latlan2 = mActual.getPosition();
                Polyline line = mMap.addPolyline(new PolylineOptions()// Creacion de la linea entre los dos ultimos marcadores
                        .add(latlan1, latlan2)
                        .width(5)
                        .color(Color.RED));
                latlan1 = latlan2;
            }
        }
    }

    public void agregarMarcadorRuta(View v)
    {
        LatLng bogota = new LatLng(latitudAzul, longitudAzul);

        Marker nuevoM = mMap.addMarker(new MarkerOptions().position(bogota).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        marcadoresRuta.add(nuevoM);
    }

    /**
     * Manipulacion de guardado-almacenamiento
     **/
    public void guardarRuta(View v) {

        //Almacenamiento Local

        double lat, lng;
        for (Marker mActual : marcadoresRuta) //Llenar el Array con los marcadores de la ruta (Rojos)
        {
            lat = mActual.getPosition().latitude;
            lng = mActual.getPosition().longitude;
            MarcadorRuta miMarcador = new MarcadorRuta(lat, lng);
            marcadores.put(miMarcador.toJSON());
        }
        Writer output = null;
        try {
            if(nombreArchivo != null && nombreArchivo != "")
            {
                File file = new File(this.getFilesDir().getAbsolutePath(), nombreArchivo);
                Log.e("ARCHIVO", "Ubicacion de archivo :" + file);
                output = new BufferedWriter(new FileWriter(file));
                output.write(marcadores.toString());
                output.close();
                Toast.makeText(getBaseContext(), "Mapa salvado", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Es necesario un nombre para guardar su mapa", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if(nombreArchivo != null && nombreArchivo != "")
        {
            //Almacenamiento en Firebase
            List<LatLng> posicionesMarcadoresRuta = new ArrayList<LatLng>();

            for(Marker m:marcadoresRuta)
            {
                posicionesMarcadoresRuta.add(m.getPosition());
            }

            myRef = database.getReference(PATH_ROUTES + user.getUid() + "/" + rutaActual.getLlaveRutaActual());
            rutaActual.setPuntosDeInteres(puntosDeInteres);
            rutaActual.setPuntosRuta(posicionesMarcadoresRuta);
            rutaActual.setNombre(edtBuscarDireccion.getText().toString());
            rutaActual.setLlavePropietario(user.getUid());
            myRef.setValue(rutaActual);

            Intent intent = new Intent(Mapa_crear_ruta.this, GuardarRutaJSON.class);
            startActivity(intent);
        }
    }

    public String experimentoLeer(Context context) {
        String json = null;
        try {
            if(nombreArchivo != null && nombreArchivo != "")
            {
                InputStream is = context.openFileInput(nombreArchivo);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No se ah encontrado el archivo", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    public void probarLectura(View v) throws JSONException {
        //readFromFile(this, "mapaSSA.json");
        String retorno = experimentoLeer(this);
        if(retorno != null)
        {
            Log.e("OMG", "Resultado otra funcion: " + retorno);
            //JSONObject json = new JSONObject(retorno);
            //JSONArray marcadoresGloriosos = json.getJSONArray("");
            JSONArray marcadoresGloriosos = new JSONArray(retorno);
            for (int i = 0; i < marcadoresGloriosos.length(); i++) {
                JSONObject jo = marcadoresGloriosos.getJSONObject(i);
                String lat = jo.getString("Latitud: ");
                Log.e("OMG", "Resultado lectura: " + lat);
            }
        }
    }

    /** Acciones de la camara*/
    public void desplegarCamara(View v)
    {
        accesoCamara = requestPermisision(this, Manifest.permission.CAMERA,
                "Quiero la camarita, deja de negarte y damela >:v!!",CAMERA);
        if(accesoCamara)
            tomarFoto();
    }

    public void tomarFoto()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Imagen asignada con éxito", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}


