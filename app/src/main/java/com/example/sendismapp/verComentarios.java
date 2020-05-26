package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class verComentarios extends AppCompatActivity {

    ImageView imagen;
    TextView textComentario;
    String comentario;
    String nombreRuta;
    String creador;
    StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_comentarios);

        imagen = findViewById(R.id.imageComentario);
        textComentario = findViewById(R.id.textComentario);
        comentario = getIntent().getStringExtra("comentario");
        nombreRuta = getIntent().getStringExtra("nombreRuta");
        creador = getIntent().getStringExtra("creador");
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference("comentariosRuta/"+user.getUid()+"/"+nombreRuta+"/");
        try {
            cargarFoto(creador);
        } catch (IOException e) {
            e.printStackTrace();
        }
        textComentario.setText(comentario);


    }


    private void cargarFoto(String llave) throws IOException {


        mStorage.child(llave+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri)
                        .into(imagen);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }
}
