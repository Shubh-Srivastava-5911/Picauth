package com.example.tempauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class TwoFactorAuthentication extends AppCompatActivity {

    PinView pv;
    Button verify;
    LinearProgressIndicator lpi;
    String matchingKey="";

    FirebaseDatabase fbdb;
    public static DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor_authentication);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pv = findViewById(R.id.pinView2);
        verify = findViewById(R.id.button18);
        lpi = findViewById(R.id.progressBar4);

        fbdb = FirebaseDatabase.getInstance();
        dbref = fbdb.getReference("users");


        pv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()==6) {
                    verify.setVisibility(View.VISIBLE);
                } else {
                    verify.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()==6) {
                    verify.setVisibility(View.VISIBLE);
                } else {
                    verify.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()==6) {
                    verify.setVisibility(View.VISIBLE);
                } else {
                    verify.setVisibility(View.INVISIBLE);
                }
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                lpi.setVisibility(View.VISIBLE);
                //Toast.makeText(SetupVerifier.this, matchingKey, Toast.LENGTH_SHORT).show();
                // we cannot give byte array formed by String.getBytes() to our TOTPgenerator
                // converting String key to byte[]
                if(matchingKey.isEmpty()) {
                    Toast.makeText(TwoFactorAuthentication.this, "a problem occurred", Toast.LENGTH_SHORT).show();
                    lpi.setVisibility(View.INVISIBLE);
                    return;
                }
                String binaryStreamOfKey = "";
                for(int i=0; i<matchingKey.length(); i++)
                {
                    for(Map.Entry<Integer,Character> entry : Base32Key.b32hm.entrySet())
                    {
                        if(entry.getValue()==matchingKey.charAt(i))
                        {
                            int b = entry.getKey();
                            for(int j=0; j<5; j++)
                            {
                                binaryStreamOfKey += ((b & 0x10)==16)?"1":"0"; // fetching 5th bit from left
                                b = b<<1; // then left shift by one
                                // repeating this five times to get the complete rightmost 5 bits from 8 bit (1 byte)
                            }
                            break;
                        }
                    }
                }
                byte[] key_bits = new byte[binaryStreamOfKey.length()/8];
                for(int i=0; i<key_bits.length; i++)
                    key_bits[i] = (byte)Integer.parseInt(binaryStreamOfKey.substring(i*8, i*8 + 8),2);

                String getTOTP = TOTPgenerator.generateTOTP(key_bits);
                //Toast.makeText(SetupVerifier.this, getTOTP, Toast.LENGTH_SHORT).show();
                if(pv.getText().toString().equals(getTOTP)) {
                    MainActivity.fba.signInWithEmailAndPassword(MainActivity.s1, MainActivity.s2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(TwoFactorAuthentication.this, "success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TwoFactorAuthentication.this, Third.class)); finish();
                            } else {
                                Toast.makeText(TwoFactorAuthentication.this, "something went wrong", Toast.LENGTH_SHORT).show();
                            }
                            lpi.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else {
                    pv.setText("");
                    Toast.makeText(TwoFactorAuthentication.this, "incorrect", Toast.LENGTH_SHORT).show();
                }
                lpi.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        matchingKey = MainActivity.s3;
    }
}