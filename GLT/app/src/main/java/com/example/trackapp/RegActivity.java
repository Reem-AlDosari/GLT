package com.example.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
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
import com.google.firebase.database.FirebaseDatabase;

import android.location.LocationManager;

public class RegActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private EditText regTextName,regTextEmail,regTextPassword;
    private Button regButton;
    private static final int PERMISSIONS_REQUEST = 100;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        auth=FirebaseAuth.getInstance();

        regButton = (Button) findViewById(R.id.regbutton);
        regButton.setOnClickListener(this);
        login = (TextView) findViewById(R.id.login_link);
        login.setOnClickListener(this);

        regTextName=(EditText) findViewById(R.id.regname);
        regTextEmail=(EditText) findViewById(R.id.regemail);
        regTextPassword=(EditText) findViewById(R.id.regpassword);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.regbutton:
                regnewuser();
                break;
            case R.id.login_link:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }

    }

    private void regnewuser() {
        String name=regTextName.getText().toString().trim();
        String email=regTextEmail.getText().toString().trim();
        String password=regTextPassword.getText().toString().trim();


        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user=new User(name,email,"All");

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegActivity.this,"Registration Completed",Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(RegActivity.this,"Registration Failed [DB]",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                        else {
                            Toast.makeText(RegActivity.this,"Registration Failed [Auth]",Toast.LENGTH_LONG).show();
                        }

                    }
                });


    }
}