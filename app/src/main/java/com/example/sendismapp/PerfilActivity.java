package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class
PerfilActivity extends AppCompatActivity {

    ImageButton imagenPerfil;
    static final int IMAGEN = 1;
    StorageReference mStorage;
    private FirebaseAuth mAuth;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference(PATH_USERS_IMAGES);
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


        seekBarExperiencia.setProgress(Integer.parseInt(textViewExperiencia.getText().toString()));
        FirebaseUser user = mAuth.getCurrentUser();
        try {
            cargarFoto(user.getUid());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                textViewPeso.setText(Integer.toString(progress*2)+" Kg");

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
                textViewAltura.setText(Integer.toString(altura)+" cm");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }

    private void cargarFoto(String llave) throws IOException {


        mStorage.child(llave+"/perfil.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri)
                        .into(imagenPerfil);
                /*InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(uri);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imagenPerfil.setImageBitmap(selectedImage);*/
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }


}
