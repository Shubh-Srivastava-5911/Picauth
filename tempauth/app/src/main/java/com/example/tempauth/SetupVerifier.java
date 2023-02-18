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
import android.widget.EditText;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Map;

public class SetupVerifier extends AppCompatActivity {

    PinView pv;
    Button verify;
    LinearProgressIndicator lpi;

    FirebaseStorage fbst;
    public static StorageReference rootSr, userSr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_verifier);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pv = findViewById(R.id.pinView);
        verify = findViewById(R.id.button9);
        lpi = findViewById(R.id.progressBar3);

        fbst = FirebaseStorage.getInstance();
        rootSr = fbst.getReference();
        userSr = rootSr.child("users");

//        pv.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

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
                //Toast.makeText(SetupVerifier.this, Authenticator.secretKeyResult, Toast.LENGTH_SHORT).show();
                // we cannot give byte array formed by String.getBytes() to our TOTPgenerator
                // converting String key to byte[]
                String binaryStreamOfKey = "";
                for(int i=0; i<Authenticator.secretKeyResult.length(); i++)
                {
                    for(Map.Entry<Integer,Character> entry : Base32Key.b32hm.entrySet())
                    {
                        if(entry.getValue()==Authenticator.secretKeyResult.charAt(i))
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

                    Toast.makeText(SetupVerifier.this, "setup complete", Toast.LENGTH_SHORT).show();
                    Third.setData(true,false,Authenticator.secretKeyResult); // updating data & key
                    userSr.child(MainActivity.fba.getCurrentUser().getUid()).putFile(Authenticator.uri); // uploading image to cloud
                    // update backup codes -- PENDING
                    lpi.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(SetupVerifier.this, Third.class)); finish();
                }
                else {
                    pv.setText("");
                    Toast.makeText(SetupVerifier.this, "incorrect", Toast.LENGTH_SHORT).show();
                }
                lpi.setVisibility(View.INVISIBLE);
            }
        });

    }

    private long pressedTime;
    @Override
    public void onBackPressed() {
        if(pressedTime+2000 > System.currentTimeMillis()) {
            startActivity(new Intent(SetupVerifier.this, Third.class)); finish();
        } else {
            Toast.makeText(this, "press back again to discard setup", Toast.LENGTH_SHORT).show();
        } pressedTime = System.currentTimeMillis();
    }
}