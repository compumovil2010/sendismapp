package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

public class Calificacion extends AppCompatActivity {

    RatingBar califica;
    Dialog ver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion);

    }

    public void clickRuta(View v){
        ver = new Dialog(this);
        ver.setContentView(R.layout.ly_calificacion);
        califica = ver.findViewById(R.id.estrellas);
        califica.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            // Called when the user swipes the RatingBar
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
}
