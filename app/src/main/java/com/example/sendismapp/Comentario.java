package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sendismapp.logic.Comentarioc;
import com.example.sendismapp.logic.Notificacion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comentario extends AppCompatActivity {

    Dialog ver;
    EditText comentario;
    ImageView aImagen;
    Button btImagen;
    String llave;
    private String ruta;
    private String nombreRuta;
    private String propietario;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private String uriImagen = null;
    private static final int PERMISSION_STORAGE_ID = 1;
    private static final int IMAGE_PICKER_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ver = new Dialog(this);
        ver.setContentView(R.layout.ly_comentario);
        comentario = ver.findViewById(R.id.etComentario);
        aImagen = ver.findViewById(R.id.imgComentarioImagen);
        btImagen = ver.findViewById(R.id.btAnadirImagen);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ruta = getIntent().getStringExtra("ruta");
        nombreRuta = getIntent().getStringExtra("nombreRuta");
        propietario = getIntent().getStringExtra("propietario");
        mStorageRef = FirebaseStorage.getInstance().getReference("comentariosRuta/");
        myRef = database.getReference("comentariosRuta/");
        /*
        llave = getIntent().getStringExtra(poner nombre);
       */
        ver.show();
    }


    public void botonComentar(View view) {
        if(cValido(comentario.getText().toString()) && mAuth.getCurrentUser() != null){
            FirebaseUser user = mAuth.getCurrentUser();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Comentarioc com = new Comentarioc(comentario.getText().toString(),dateFormat.format(new Date()),uriImagen,mAuth.getCurrentUser().getUid());
            myRef = database.getReference("comentariosRuta/"+ruta+"/"+user.getUid());
            myRef.setValue(com);
            escribirNotificacion();
            Uri file = Uri.fromFile(new File(uriImagen));
            StorageReference riversRef = mStorageRef.child("llaveRuta"+"/"+myRef.push().getKey()+".jpg");
            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Log.i("SubirImagenComentario", "Success");
                            Toast.makeText(Comentario.this, "Se guardó la imagen",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(Comentario.this, "Guardar imagen fallido",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            Intent intent = new Intent(Comentario.this, MenuPrincipal.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }
    public void clickImagen(View view) {
        requestPermission(Comentario.this, Manifest.permission.READ_EXTERNAL_STORAGE,"Permiso necesario para escoger imagen", PERMISSION_STORAGE_ID);

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
                        aImagen.setImageBitmap(Bitmap.createScaledBitmap(selectedImage,600,600,false));
                        aImagen.setVisibility(View.VISIBLE);
                        btImagen.setTextColor(Color.parseColor("black"));
                        Toast.makeText(this, "Se a cargado tu foto!", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
    public String getRealPathFromURI(Uri contentUri) {
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private boolean cValido(String contra) {
        if(TextUtils.isEmpty(contra)){
            comentario.setError("Comenta algo! ");
            return false;
        }else if(uriImagen == null){
            btImagen.setTextColor(Color.parseColor("red"));
            Toast.makeText(this, "Debes añadir una imagen!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void escribirNotificacion()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        FirebaseUser user = mAuth.getCurrentUser();
        myRef = database.getReference("notificacionesC/"+propietario+"/"+ruta+"/"+user.getUid());
        Notificacion aux = new Notificacion();
        aux.setUsuario(user.getUid());
        aux.setFecha(dateFormat.format(new Date()));
        aux.setRuta(nombreRuta);
        myRef.setValue(aux);

    }
}
