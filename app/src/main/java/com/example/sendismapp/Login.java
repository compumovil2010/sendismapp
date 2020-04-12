package com.example.sendismapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText correo;
    private EditText contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mAuth = FirebaseAuth.getInstance();
        correo = findViewById(R.id.correo);
        contrasena = findViewById(R.id.contrasena);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {

        if(currentUser!=null){
            Intent intent = new Intent(Login.this, MenuPrincipal.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            correo.setText("");
            contrasena.setText("");
        }

    }
    private boolean isEmailValid(String email, String password) {
        if (!email.contains("@") || !email.contains(".") || email.length() < 5){
            correo.setError("Wrong email");
            return false;
        }else if(TextUtils.isEmpty(password)){
            contrasena.setError("Wrong password");
            return false;
        }
        return true;
    }

    private void signInUser(String email, String password) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI
                                Log.d("signInUser", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }else {
                                // If sign in fails, display a message to the user.
                                Log.w("signInUser", "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
    }

    public void clickIngresar(View v) {
        if(isEmailValid(correo.getText().toString(), contrasena.getText().toString())){
            signInUser(correo.getText().toString(), contrasena.getText().toString());
        }
    }



}
