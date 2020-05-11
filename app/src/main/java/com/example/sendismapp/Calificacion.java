package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.sendismapp.logic.Calificacionc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Calificacion extends AppCompatActivity {

    RatingBar califica;
    Dialog ver;
    private Button botonCalificacion;
    private String ruta;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    public static final String PATH_CALI="calificaciones/";
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ruta = getIntent().getStringExtra("ruta");
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        ver = new Dialog(this);
        botonCalificacion = findViewById(R.id.btHacerCalificacion);
        ver.setContentView(R.layout.ly_calificacion);
        califica = ver.findViewById(R.id.estrellas);
        califica.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating == 0){
                    califica.setRating(1);
                }
                //Log.i("calificacionEstrella",String.valueOf(califica.getRating()));
            }
        });
        ver.show();
        //Toast.makeText(this,String.valueOf(califica.getRating()),Toast.LENGTH_SHORT).show();
    }

    public void prueba(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        myRef = database.getReference(PATH_CALI+ruta);
        Calificacionc aux = new Calificacionc();
        aux.setUsuario(user.getUid());
        aux.setCalificacion(califica.getRating());
        myRef.setValue(aux);


        Toast.makeText(this,user.getUid()+" "+califica.getRating(),Toast.LENGTH_SHORT).show();
    }
}
