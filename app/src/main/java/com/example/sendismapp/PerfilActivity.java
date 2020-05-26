package com.example.sendismapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class
PerfilActivity extends AppCompatActivity {

    ImageButton imagenPerfil;
    static final int IMAGEN = 1;
    StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public static final String PATH_USERS="users/";
    public static final String PATH_USERS_IMAGES="images/";
    private SeekBar seekBarPeso;
    private SeekBar seekBarAltura;
    private SeekBar seekBarExperiencia;
    private TextView textViewPeso;
    private TextView textViewAltura;
    private TextView textViewExperiencia;
    private EditText editTextNombreUsuario;
    private EditText editTextNombre;
    private EditText editTextEdad;
    private int edad;
    private int peso;
    private int altura;
    private Button botonAplicar;
    private Button botonContraseña;
    private FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference(PATH_USERS_IMAGES);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(PATH_USERS);

        imagenPerfil = findViewById(R.id.imagenPerfil);
        seekBarAltura = findViewById(R.id.seek_bar_altura);
        seekBarExperiencia = findViewById(R.id.seek_bar_experiencia);
        seekBarPeso = findViewById(R.id.seek_bar_peso);
        textViewAltura = findViewById(R.id.text_view_altura);
        textViewExperiencia = findViewById(R.id.text_view_experience_display);
        textViewPeso = findViewById(R.id.text_view_peso);
        editTextEdad = findViewById(R.id.edit_text_age);
        editTextNombre = findViewById(R.id.edit_text_name_profile);
        editTextNombreUsuario = findViewById(R.id.edit_text_username_profile);
        botonAplicar= findViewById(R.id.button_aplicar);
        botonContraseña = findViewById(R.id.button_contrasena);



        user = mAuth.getCurrentUser();
        try {
            cargarFoto(user.getUid());
        } catch (IOException e) {
            e.printStackTrace();
        }

        myRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String nombre = dataSnapshot.child("nombre").getValue().toString();
                    editTextNombre.setText(nombre);
                    String nickname = dataSnapshot.child("nickname").getValue().toString();
                    editTextNombreUsuario.setText(nickname);
                    edad = Integer.parseInt(dataSnapshot.child("edad").getValue().toString());
                    peso = Integer.parseInt(dataSnapshot.child("peso").getValue().toString());
                    altura = Integer.parseInt(dataSnapshot.child("altura").getValue().toString());
                    editTextEdad.setText(Integer.toString(edad));
                    textViewPeso.setText(Integer.toString(peso) );
                    textViewAltura.setText(Integer.toString(altura));
                    seekBarPeso.setProgress(100*peso/200);
                    seekBarAltura.setProgress(100*altura/230);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seekBarExperiencia.setProgress(Integer.parseInt(textViewExperiencia.getText().toString()));

        imagenPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, ImagenPerfil.class);
                startActivityForResult(intent,IMAGEN);
            }
        });

        seekBarPeso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewPeso.setText(Integer.toString(progress*2));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarExperiencia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewExperiencia.setText(Integer.toString(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarAltura.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int altura = progress*230/100;
                textViewAltura.setText(Integer.toString(altura));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        botonAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object>usuarioMap = new HashMap<>();
                usuarioMap.put("nombre",editTextNombre.getText().toString());
                usuarioMap.put("edad",editTextEdad.getText().toString());
                usuarioMap.put("peso",textViewPeso.getText().toString());
                usuarioMap.put("altura",textViewAltura.getText().toString());
                usuarioMap.put("nickname",editTextNombreUsuario.getText().toString());
                myRef.child(user.getUid()).updateChildren(usuarioMap);

                finish();
            }
        });
        botonContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this,CambiarContrasena.class);
                startActivity(intent);
            }
        });




    }

    private void cargarFoto(String llave) throws IOException {


        mStorage.child(llave+"/perfil.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri)
                        .into(imagenPerfil);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }

}
