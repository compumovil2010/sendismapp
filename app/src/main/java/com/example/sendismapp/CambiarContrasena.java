package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CambiarContrasena extends AppCompatActivity {

    private EditText contraseña1;
    private EditText contraseña2;
    private Button botonAplicar;
    private Button botonCancelar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public static final String PATH_USERS="users/";
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);
        contraseña1 = findViewById(R.id.password);
        contraseña2 = findViewById(R.id.password2);
        botonAplicar = findViewById(R.id.boton_contrasena);
        botonCancelar = findViewById(R.id.boton_volver);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(PATH_USERS);

        user = mAuth.getCurrentUser();

        botonAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = contraseña1.getText().toString();
                String s2 = contraseña2.getText().toString();

                if (contraseña1.getText().toString().isEmpty() || contraseña2.getText().toString().isEmpty())
                {
                    Toast.makeText(CambiarContrasena.this, "Los campos estan incompletos",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (s1.equals(s2))
                    {
                        Map<String,Object> usuarioMap = new HashMap<>();
                        usuarioMap.put("contrasena",s1);
                        myRef.child(user.getUid()).updateChildren(usuarioMap);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(CambiarContrasena.this, "Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
}
