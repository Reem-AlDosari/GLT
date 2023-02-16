package com.example.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register;

    private FirebaseAuth auth;
    private EditText loginTextEmail,loginTextPassword;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth=FirebaseAuth.getInstance();

        register = (TextView) findViewById(R.id.registerlink);
        register.setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(this);

        loginTextEmail=(EditText) findViewById(R.id.loginemail);
        loginTextPassword=(EditText) findViewById(R.id.loginpassword);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerlink:
                //finish();
                startActivity(new Intent( this ,RegActivity.class));
                break;

            case R.id.loginbutton:
                login();
                break;

        }
    }

    private void login() {
        String email=loginTextEmail.getText().toString().trim();
        String password=loginTextPassword.getText().toString().trim();

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // go to map
                    Toast.makeText(MainActivity.this,"Login Success",Toast.LENGTH_LONG).show();
                    startActivity(new Intent( MainActivity.this , MapsActivity.class));
                }
                else{
                    Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}