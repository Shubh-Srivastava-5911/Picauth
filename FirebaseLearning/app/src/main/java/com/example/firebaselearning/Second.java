package com.example.firebaselearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.AuthResult;

public class Second extends AppCompatActivity {
    EditText email, password, confirm;
    Button verify, register;
    LinearProgressIndicator lpi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_second);
        email = findViewById(R.id.editTextTextEmailAddress2);
        password = findViewById(R.id.editTextTextPassword2);
        confirm = findViewById(R.id.editTextTextPassword3);
        verify = findViewById(R.id.button3);
        register = findViewById(R.id.button5);
        lpi = findViewById(R.id.linearProgress1);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = email.getText().toString(), s2 = password.getText().toString(), s3 = confirm.getText().toString();
                if(s1.isEmpty())  { email.setError("fill email"); return; }
                if(s2.isEmpty())  { password.setError("fill password"); return; }
                if(s3.isEmpty())  { confirm.setError("confirm password"); return; }
                if(!s2.equals(s3))  { confirm.setText(""); confirm.setError("incorrect"); return; }

                lpi.setVisibility(View.VISIBLE);
                // registration process
                MainActivity.fba.createUserWithEmailAndPassword(s1,s2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // sending email verification link to given email address
                            MainActivity.fba.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(Second.this, "a verification link is sent to your email", Toast.LENGTH_SHORT).show();
                                        email.setFocusable(false); password.setFocusable(false); confirm.setFocusable(false);
                                        verify.setVisibility(View.INVISIBLE); register.setVisibility(View.VISIBLE);
                                    } else {
                                        lpi.setVisibility(View.INVISIBLE);
                                        Toast.makeText(Second.this, "invalid email", Toast.LENGTH_SHORT).show();
                                        MainActivity.fba.getCurrentUser().delete();
                                        Intent i = new Intent(Second.this, MainActivity.class); startActivity(i); finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Second.this, "something went wrong", Toast.LENGTH_SHORT).show();
                            lpi.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.fba.getCurrentUser().reload(); // need to reload after verification
                try { Thread.sleep(1000); }
                catch (InterruptedException e) { e.printStackTrace(); }
                if(MainActivity.fba.getCurrentUser().isEmailVerified() && MainActivity.fba.getCurrentUser()!=null) {
                    Toast.makeText(Second.this, "registered successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Second.this, Third.class);
                    startActivity(i); finish();
                } else {
                    Toast.makeText(Second.this, "verification pending", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Second.this, MainActivity.class);
        startActivity(i); finish();
    }
}