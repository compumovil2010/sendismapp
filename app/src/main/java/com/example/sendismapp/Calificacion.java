package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.sendismapp.logic.Calificacionc;
import com.example.sendismapp.logic.Notificacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class Calificacion extends AppCompatActivity {

    RatingBar califica;
    Dialog ver;
    private Button botonCalificacion;
    private String ruta;
    private String nombreRuta;
    private String propietario;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    public static final String PATH_CALI="calificaciones/";
    public static final String PATH_NOTI="notificaciones/";
    private DatabaseReference myRef;
    NotificationCompat.Builder mBuilder;
    int notificationID = 1;
    String channelID = "My channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ruta = getIntent().getStringExtra("ruta");
        nombreRuta = getIntent().getStringExtra("nombreRuta");
        propietario = getIntent().getStringExtra("propietario");
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
        myRef = database.getReference(PATH_CALI+ruta+"/"+user.getUid());
        Calificacionc aux = new Calificacionc();
        aux.setUsuario(user.getUid());
        aux.setCalificacion(califica.getRating());
        myRef.setValue(aux);



        notificacion();
        finish();
    }

    public void notificacion ()
    {
        NotificationManager nNotificacionManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        //mBuilder = new NotificationCompat.Builder(this,channelID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            nNotificacionManager.createNotificationChannel(channel);
            /*NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);*/
            mBuilder = new NotificationCompat.Builder(this,channelID);

        }
        mBuilder = new NotificationCompat.Builder(this,channelID);
        mBuilder.setSmallIcon(R.drawable.nn);
        mBuilder.setContentTitle("Calificacion");
        mBuilder.setContentText("Un usuario a calificado tu ruta : " + nombreRuta);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        escribirNotificacion();


        Intent intent = new Intent(this, Notificaciones.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);

        nNotificacionManager.notify(notificationID,mBuilder.build());
    }

    public void escribirNotificacion()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        myRef = database.getReference(PATH_NOTI+propietario+"/"+ruta+"/"+user.getUid());
        Notificacion aux = new Notificacion();
        aux.setUsuario(user.getUid());
        aux.setFecha(new Date());
        aux.setRuta(nombreRuta);
        myRef.setValue(aux);
        Toast.makeText(this,aux.getFecha().toString(),Toast.LENGTH_SHORT).show();
    }
}
