package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class
PerfilActivity extends AppCompatActivity {

    ImageButton imagenPerfil;
    static final int IMAGEN = 1;
    StorageReference mStorage;
    private FirebaseAuth mAuth;
    public static final String PATH_USERS="users/";
    public static final String PATH_USERS_IMAGES="images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference(PATH_USERS_IMAGES);
        imagenPerfil = findViewById(R.id.imagenPerfil);

        imagenPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, ImagenPerfil.class);
                startActivityForResult(intent,IMAGEN);
            }
        });
    }

    public void initViewCamara() {

    }

}
