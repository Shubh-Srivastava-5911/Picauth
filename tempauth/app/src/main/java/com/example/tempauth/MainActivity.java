package com.example.tempauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    Button login, signup;
    EditText email, password;
    ProgressBar pb;

    public static String s1="", s2="", s3="";

    public static FirebaseAuth fba;

    FirebaseDatabase fbdb;
    public static DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        login = findViewById(R.id.button);
        signup = findViewById(R.id.button2);
        pb = findViewById(R.id.progressBar);

        fba = FirebaseAuth.getInstance();

        fbdb = FirebaseDatabase.getInstance();
        dbref = fbdb.getReference("users");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // checking inputs
                MainActivity.s1 = email.getText().toString(); MainActivity.s2 = password.getText().toString();
                if(email.getText().toString().isEmpty()) { email.setError("fill email"); return; }
                if(password.getText().toString().isEmpty()) { password.setError("fill password"); return; }
                // login process
                pb.setVisibility(View.VISIBLE);
                fba.signInWithEmailAndPassword(s1,s2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pb.setVisibility(View.INVISIBLE);
                        if(task.isSuccessful())
                        {
                            dbref.child(fba.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DataSnapshot ds = task.getResult();
                                        if(ds.exists()) {
                                            if(ds.child("newU").getValue().toString().equals("true")) // [twoFA,newU] = [F,T]
                                            {
                                                Intent i = new Intent(MainActivity.this, Third.class);
                                                startActivity(i); finish();
                                            }
                                            else if(ds.child("twoFA").getValue().toString().equals("true")) // [twoFA,newU] = [T,F]
                                            {
                                                MainActivity.s3 = ds.child("scrtK").getValue().toString();
                                                fba.signOut(); // to sign in again after TOTP verification
                                                Intent i = new Intent(MainActivity.this, TwoFactorAuthentication.class);
                                                startActivity(i); finish();
                                            }
                                            else // [twoFA,newU] = [F,F]
                                            {
                                                Intent i = new Intent(MainActivity.this, Third.class);
                                                startActivity(i); finish();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Second.class);
                startActivity(i); finish();
            }
        });
        // forgot password code
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(fba.getCurrentUser()!=null) { // in case of user reloading problem even after deletion of account this extra check is needed
            fba.getCurrentUser().reload();
            // if still user is not null means it is not the case of account deletion
            if (fba.getCurrentUser() != null && !fba.getCurrentUser().isEmailVerified()) {
                MainActivity.fba.getCurrentUser().delete();
                Toast.makeText(this, "login requires", Toast.LENGTH_SHORT).show();
            }
            else if (fba.getCurrentUser() != null && fba.getCurrentUser().isEmailVerified()) {
                Intent i = new Intent(MainActivity.this, Third.class);
                startActivity(i); finish();
            }
            else {
                Toast.makeText(this, "login requires", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "login requires", Toast.LENGTH_SHORT).show();
        }
    }
}