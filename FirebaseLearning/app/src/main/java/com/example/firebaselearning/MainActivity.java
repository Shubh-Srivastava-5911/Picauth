package com.example.firebaselearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {

    public static FirebaseAuth fba;

    TextView forgot;
    EditText email, password;
    Button signup, signin;
    LinearProgressIndicator lpi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        fba = FirebaseAuth.getInstance();
        //Toast.makeText(this, fba.getApp().getName(), Toast.LENGTH_SHORT).show();

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        signup = findViewById(R.id.button);
        signin = findViewById(R.id.button2);
        lpi = findViewById(R.id.linearProgress);
        forgot = findViewById(R.id.textView7);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = email.getText().toString(), s2 = password.getText().toString();
                if(s1.isEmpty()) { email.setError("fill email"); return; }
                if(s2.isEmpty())  { password.setError("fill password"); return; }

                lpi.setVisibility(View.VISIBLE);
                // authentication process
                fba.signInWithEmailAndPassword(s1,s2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this, Third.class);
                            startActivity(i); finish();
                        } else {
                            lpi.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Second.class); startActivity(i); finish();
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog fp = new Dialog(MainActivity.this);
                fp.setContentView(R.layout.forgot_password);
                Button ok = fp.findViewById(R.id.buttonOk);
                EditText et = fp.findViewById(R.id.editTextTextEmailAddress3);
                fp.show();
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(et.getText().toString().isEmpty()) { et.setError("fill email"); return; }
                        fba.sendPasswordResetEmail(et.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "password reset email sent", Toast.LENGTH_SHORT).show();
                                    fp.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this, "invalid email", Toast.LENGTH_SHORT).show();
                                    et.setError("check email");
                                }
                            }
                        });
                    }
                });
            }
        });
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
            } else if (fba.getCurrentUser() != null && fba.getCurrentUser().isEmailVerified()) {
                Intent i = new Intent(MainActivity.this, Third.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "login requires", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "login requires", Toast.LENGTH_SHORT).show();
        }
    }
}