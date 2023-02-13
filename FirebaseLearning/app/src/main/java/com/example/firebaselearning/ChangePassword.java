package com.example.firebaselearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

public class ChangePassword extends AppCompatActivity {
    Button verify, update;
    TextView email;
    EditText currentp, newp, confirmp;
    LinearProgressIndicator lpi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        verify = findViewById(R.id.button15);
        update = findViewById(R.id.button16);
        email = findViewById(R.id.textView9);
        currentp = findViewById(R.id.editTextTextPassword4);
        newp = findViewById(R.id.editTextTextPassword5);
        confirmp = findViewById(R.id.editTextTextPassword6);
        lpi = findViewById(R.id.linearProgress2);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentp.getText().toString().isEmpty()) { currentp.setError("fill password"); return; }
                // re-authenticating user
                AuthCredential ac = EmailAuthProvider.getCredential(MainActivity.fba.getCurrentUser().getEmail(),currentp.getText().toString());
                MainActivity.fba.getCurrentUser().reauthenticate(ac).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            currentp.setVisibility(View.INVISIBLE); verify.setVisibility(View.INVISIBLE);
                            newp.setVisibility(View.VISIBLE); confirmp.setVisibility(View.VISIBLE); update.setVisibility(View.VISIBLE);
                        } else {
                            currentp.setError("incorrect");
                        }
                    }
                });
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newp.getText().toString().isEmpty()) { newp.setError("new password"); return; }
                if(confirmp.getText().toString().isEmpty()) { confirmp.setError("confirm password"); return; }
                if(!newp.getText().toString().equals(confirmp.getText().toString())) { confirmp.setText(""); confirmp.setError("incorrect"); return; }
                // updating password
                lpi.setVisibility(View.VISIBLE);
                MainActivity.fba.getCurrentUser().updatePassword(newp.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            // LOGOUT FROM OTHER DEVICES, add a re-authenticate in onStart() of MainActivity
                            Toast.makeText(ChangePassword.this, "success", Toast.LENGTH_SHORT).show();
                            MainActivity.fba.signOut();
                            Intent i = new Intent(ChangePassword.this,MainActivity.class); startActivity(i); finish();
                        } else {
                            lpi.setVisibility(View.INVISIBLE);
                            Toast.makeText(ChangePassword.this, "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        email.setText(MainActivity.fba.getCurrentUser().getEmail());
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ChangePassword.this,Third.class); startActivity(i); finish();
    }
}