package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sendismapp.logic.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Registrarse extends AppCompatActivity {

    private Spinner spnExperiencia;
    private Spinner nacionalidad;
    private ArrayList<String> paises = new ArrayList();
    private ArrayAdapter<String> adapterNacionalidad;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    public static final String PATH_USERS="users/";
    public static final String PATH_USERS_IMAGES="images/";
    private StorageReference mStorageRef;
    private DatabaseReference myRef;
    private EditText nombre;
    private EditText contrasena;
    private EditText repetir;
    private EditText peso;
    private EditText altura;
    private EditText edad;
    private EditText correo;
    private EditText nickname;
    private ImageButton imagen;
    private String uriImagen = null;
    private static final int PERMISSION_STORAGE_ID = 1;
    private static final int IMAGE_PICKER_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference(PATH_USERS_IMAGES);
        myRef = database.getReference(PATH_USERS);
        nombre = findViewById(R.id.edtNombreU);
        contrasena = findViewById(R.id.edtPassword);
        repetir = findViewById(R.id.edtRepetirP);
        peso = findViewById(R.id.edtPeso);
        altura = findViewById(R.id.edtAltura);
        edad = findViewById(R.id.edtEdad);
        correo = findViewById(R.id.etCorreoRegistro);
        imagen = findViewById(R.id.imgPerfil);
        nickname = findViewById(R.id.edtNickName);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registrarse");

        spnExperiencia = findViewById(R.id.spnExperiencia);
        spnExperiencia.setSelection(1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Esperiencias, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnExperiencia.setAdapter(adapter);

        nacionalidad = findViewById(R.id.spnNacionalidad);
        adapterNacionalidad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, paises);
        adapterNacionalidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        obtenerPaises();

    }

    public void clickRegistrarse(View view) {
        String con = contrasena.getText().toString();
        String cor = correo.getText().toString();
        if(contrasenaValida(con,repetir.getText().toString()) && correoValido(cor)){
            String nom = nombre.getText().toString();
            String pes = peso.getText().toString();
            String alt = altura.getText().toString();
            String eda = edad.getText().toString();
            String nick = nickname.getText().toString();
            if(camposNoVacios(nom,alt,pes,eda,nick)){
                Registrar(cor,con);
            }
        }
    }
    private void guardarImagenPerfil(String llave){
        Uri file = Uri.fromFile(new File(uriImagen));
        StorageReference riversRef = mStorageRef.child(llave+"/perfil.jpg");
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Log.i("SubirImagenPerfil", "Success");
                        Toast.makeText(Registrarse.this, "Se guardó la imagen",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Registrarse.this, "Guardar imagen fallido",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean contrasenaValida(String contra, String repe) {
        if (!contra.equals(repe)){
            contrasena.setError("No coinciden");
            repetir.setError("No coinciden");
            return false;
        }else if(contra.length() < 6){
            contrasena.setError("Debe tener al menos 6 caracteres");
            return false;
        }else if (TextUtils.isEmpty(contra)){
            contrasena.setError("No puede estar vacío");
            return false;
        }
        return true;
    }

    private boolean correoValido(String email) {
        if (!email.contains("@") || !email.contains(".") || email.length() < 5){
            correo.setError("Correo inválido");
            return false;
        }
        return true;
    }

    private boolean camposNoVacios(String nom, String alt, String pes, String eda, String nick){
        if(TextUtils.isEmpty(nom)){
            nombre.setError("No puede estar vacío");
            return false;
        }else if(TextUtils.isEmpty(alt)){
            altura.setError("No puede estar vacío");
            return false;
        }else if(TextUtils.isEmpty(pes)){
            peso.setError("No puede estar vacío");
            return false;
        }else if(TextUtils.isEmpty(eda)){
            edad.setError("No puede estar vacío");
            return false;
        }else if(TextUtils.isEmpty(nick)){
            nickname.setError("Escribe un nickname!");
            return false;
        }
        return true;
    }

    public void clickSubirFotoPerfil(View view) {
        requestPermission(Registrarse.this, Manifest.permission.READ_EXTERNAL_STORAGE,"Permiso necesario para escoger imagen", PERMISSION_STORAGE_ID);

    }

    private void requestPermission(Activity context, String permission, String just, int id) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }else{
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSION_STORAGE_ID: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
                }else{
                    Toast.makeText(this, "Necesito el permiso", Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    try {
                        Uri uri = data.getData();
                        uriImagen = getRealPathFromURI(uri);
                        InputStream imageStream = getContentResolver().openInputStream(uri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imagen.setImageBitmap(Bitmap.createScaledBitmap(selectedImage,imagen.getWidth(),imagen.getHeight(),false));
                        Toast.makeText(this, "Bonita foto!", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    private void Registrar(final String email, final String password) {
         mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String con = contrasena.getText().toString();
                        String cor = correo.getText().toString();
                        String nom = nombre.getText().toString();
                        String pes = peso.getText().toString();
                        String alt = altura.getText().toString();
                        String eda = edad.getText().toString();
                        if (task.isSuccessful()) {
                            Log.i("Registro-Usuario", "Success");
                            if(uriImagen==null)uriImagen="El usuario no guardo foto de perfil";
                            Usuario nuevo = new Usuario(cor,nom,con,spnExperiencia.getSelectedItem().toString(),uriImagen,Float.parseFloat(pes),Float.parseFloat(alt),Integer.parseInt(eda),nacionalidad.getSelectedItem().toString(),nickname.getText().toString());
                            FirebaseUser user = mAuth.getCurrentUser();
                            myRef = database.getReference(PATH_USERS+user.getUid());
                            myRef.setValue(nuevo);
                            if(!uriImagen.equals("El usuario no guardo foto de perfil"))guardarImagenPerfil(user.getUid());
                            Toast.makeText(Registrarse.this, "Te has registrado satisfactoriamente",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Registrarse.this, MenuPrincipal.class);
                            intent.putExtra("nombreUsuario", nuevo.getNombre());
                            intent.putExtra("correoUsuario",nuevo.getCorreo());
                            startActivity(intent);
                        }else {
                            Log.i("Registro-Usuario", "Failure", task.getException());
                            Toast.makeText(Registrarse.this, "Registro fallido",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
    public String getRealPathFromURI(Uri contentUri) {
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void obtenerPaises(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://restcountries.eu/rest/v2/all?fields=name";
        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        String respuesta = (String)response;
                        Log.i("REST PAISES",respuesta);
                        JSONArray resultado;
                        try {
                            resultado = new JSONArray(respuesta);
                            for(int i=0; i<resultado.length(); i++) {
                                JSONObject p = (JSONObject) resultado.get(i);
                                String nombre = (String)p.get("name");
                                paises.add(nombre);
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        nacionalidad.setAdapter(adapterNacionalidad);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TAG", "Error en uso de rest"+error.getCause());
                    }
                }
        );
        queue.add(req);
    }

}
