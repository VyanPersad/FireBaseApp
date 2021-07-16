package com.firebase.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    Button signIn, signUp;

    private FirebaseAuth myAuth;
    private ProgressDialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAuth = FirebaseAuth.getInstance();
        if (myAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), homeActivity.class));
        }
        myDialog = new ProgressDialog(this);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.signIn);
        signUp = findViewById(R.id.signUp2);

        signIn.setOnClickListener(view -> {
            String myEin = email.getText().toString().trim();
            String myPassin = password.getText().toString().trim();

            if (TextUtils.isEmpty(myEin) || TextUtils.isEmpty(myPassin)){
                email.setError("Required Field");
                password.setError("Required Field");
                return;
            }

            myDialog.setMessage("Processing");
            myDialog.dismiss();
            myAuth.signInWithEmailAndPassword(myEin,myPassin).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        myDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, homeActivity.class));
                        Toast.makeText(MainActivity.this, "Log In Successful", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Log In Un-successful", Toast.LENGTH_LONG).show();
                    }
            });
        });
        signUp.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, RegisActivity.class));
        });
    }
}