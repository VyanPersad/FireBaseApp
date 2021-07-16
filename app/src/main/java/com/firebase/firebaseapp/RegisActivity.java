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
public class RegisActivity extends AppCompatActivity {

    EditText emailsup, passwordsup;
    Button signUp2;
    private ProgressDialog myDialog;
    //Firebase
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        myDialog = new ProgressDialog(this);
        myAuth = FirebaseAuth.getInstance();

        emailsup = findViewById(R.id.emailsup);
        passwordsup = findViewById(R.id.passwordsup);
        signUp2 = findViewById(R.id.signUp2);

        signUp2.setOnClickListener(view -> {
            String myE = emailsup.getText().toString().trim();
            String myPass = passwordsup.getText().toString().trim();

            if (TextUtils.isEmpty(myE) || TextUtils.isEmpty(myPass)){
                emailsup.setError("Required Field");
                passwordsup.setError("Required Field");
                Toast.makeText(this, "Check Inputs", Toast.LENGTH_SHORT).show();
                return;
            }
            myDialog.setMessage("Processing");
            myDialog.dismiss();
            myAuth.createUserWithEmailAndPassword(myE,myPass).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    myDialog.dismiss();
                    Toast.makeText(RegisActivity.this, "User Added", Toast.LENGTH_LONG).show();
                    //Go to or re-direct to some new activity
                    startActivity(new Intent(RegisActivity.this, MainActivity.class));
                }
            });
        });
    }
}