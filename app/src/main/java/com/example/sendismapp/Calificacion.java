package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class Calificacion extends AppCompatActivity {

    RatingBar califica;
    Dialog ver;
    private Button botonCalificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        botonCalificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

}
