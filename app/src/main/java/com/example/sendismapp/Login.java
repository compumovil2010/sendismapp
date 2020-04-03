package com.example.sendismapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void verMenuPrincipal(View v)
    {
        Intent intent = new Intent(Login.this, MenuPrincipal.class);
        startActivity(intent);
    }
}
