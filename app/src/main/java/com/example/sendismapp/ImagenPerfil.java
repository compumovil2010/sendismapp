package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImagenPerfil extends AppCompatActivity {

    Button botonImagen;
    Button botonCamara;
    Button botonAplicar;
    ImageView image;
    Uri imageUri;
    StorageReference mStorage;
    private FirebaseAuth mAuth;
    static final int IMAGE_PICKER_REQUEST = 8;
    public static final String PATH_USERS="users/";
    public static final String PATH_USERS_IMAGES="images/";
    //static final int PERMISSION_GALERIA_ID = 9;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_perfil);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference(PATH_USERS_IMAGES);
        botonCamara = findViewById(R.id.buttonCamara);
        botonImagen = findViewById(R.id.buttonImagen);
        botonAplicar = findViewById(R.id.buttonAplicar);
        image = findViewById(R.id.imageView4);

        botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission(ImagenPerfil.this, Manifest.permission.CAMERA, "Es necesario para tomar una foto", REQUEST_IMAGE_CAPTURE);
                initViewCamara();
            }
        });
        botonImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission(ImagenPerfil.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Es necesario para seleccionar la imagen", IMAGE_PICKER_REQUEST);
                initViewGaleria();
            }
        });
        botonAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                guardarImagenPerfil(user.getUid());
                finish();

            }
        });



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
        } else if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                image.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission(ImagenPerfil.this, Manifest.permission.CAMERA, "Es necesario para tomar una foto", REQUEST_IMAGE_CAPTURE);
                initViewCamara();
            }
        });
        botonImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission(ImagenPerfil.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Es necesario para seleccionar la imagen", IMAGE_PICKER_REQUEST);
                initViewGaleria();
            }
        });
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void initViewGaleria() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);

        }
    }

    public void initViewCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePicture();
        }
    }

    /**
     * Metodo para solicitar un permiso
     *
     * @param context    actividad actual
     * @param permission permiso que se desea solicitar
     * @param just       justificacion para el permiso
     * @param id         identificador con el se marca la solicitud y se captura el callback de respuesta
     */
    public void requestPermission(Activity context, String permission, String just, int id) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                // Show an expanation to the user *asynchronously*   
                Toast.makeText(context, just, Toast.LENGTH_LONG).show();
            }
            // request the permission.   
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);

        }
    }


    private void guardarImagenPerfil(String llave){

        final StorageReference riversRef = mStorage.child(llave+"/perfil.jpg");
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Log.i("SubirImagenPerfil", "Success");
                        Toast.makeText(ImagenPerfil.this, "Se guardó la imagen",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ImagenPerfil.this, "Guardar imagen fallido",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }



}
