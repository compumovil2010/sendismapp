package com.example.sendismapp;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.sendismapp.logic.Notificacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class Servicio extends IntentService {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    public static final String PATH_CALI="calificaciones/";
    public static final String PATH_NOTI="notificaciones/";
    private DatabaseReference myRef;
    NotificationCompat.Builder mBuilder;
    int notificationID = 1;
    String channelID = "My channel";
    int contadorCali=0;
    int contadorCom=0;

    public Servicio() {
        super("Servicio");
    }

    @Override
    protected void onHandleIntent(  Intent intent) {
        suscripcionCalificaciones();
        suscripcionNotificaciones();
        Log.i("TAG", "Servicio en ejecución" );
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
    public void suscripcionCalificaciones()
    {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Log.i("TAG", "Servicio en ejecución"+user );
        if (user!= null)
        {
            myRef = database.getReference(PATH_NOTI+user.getUid());
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (contadorCali == 0)
                    {
                        contadorCali++;
                    }
                    else
                    {
                        notificacion("Calificacion","Un usuario a calificado tu ruta");
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public void suscripcionNotificaciones()
    {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
        {
            myRef = database.getReference("notificacionesC/"+user.getUid());
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (contadorCom == 0)
                    {
                        contadorCom++;
                    }
                    else
                    {
                        notificacion("Comentario","Un usuario a comentado tu ruta");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public void notificacion (String titulo, String texto)
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
        mBuilder.setContentTitle(titulo);
        mBuilder.setContentText(texto);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //escribirNotificacion();

        if (titulo == "Calificacion")
        {
            Intent intent = new Intent(this, Notificaciones.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);

            nNotificacionManager.notify(notificationID,mBuilder.build());
        }
        else
        {
            Intent intent = new Intent(this, Notificacionc.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);

            nNotificacionManager.notify(notificationID,mBuilder.build());
        }

    }





}
