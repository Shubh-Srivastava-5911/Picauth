package com.example.tempauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.glxn.qrgen.android.QRCode;

public class Authenticator extends AppCompatActivity
{
    ImageButton ib, down1, down2, down3, up2, up3, up4;
    ImageView qrImg;
    TextView kyTxt, cpTxt;
    Button imageBt, keyBt, qrBt, verifyBt;
    LinearProgressIndicator lpi;
    LinearLayout step1, step2, step3, step4;

    FirebaseStorage fbst;
    public static StorageReference rootSr, userSr;

    public static Bitmap bitImg;
    public static Uri uri;
    public static String secretKeyResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fbst = FirebaseStorage.getInstance();
        rootSr = fbst.getReference();
        userSr = rootSr.child("users");

        ib = findViewById(R.id.imageButton);
        imageBt = findViewById(R.id.button8);
        keyBt = findViewById(R.id.button5);
        qrBt = findViewById(R.id.button13);
        verifyBt = findViewById(R.id.button14);
        kyTxt = findViewById(R.id.editTextSecretKey);
        cpTxt = findViewById(R.id.copyText);
        qrImg = findViewById(R.id.qrImage);
        lpi = findViewById(R.id.linearProgress1);
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        step4 = findViewById(R.id.step4);
        down1 = findViewById(R.id.down1);
        up2 = findViewById(R.id.up2);
        down2 = findViewById(R.id.down2);
        up3 = findViewById(R.id.up3);
        down3 = findViewById(R.id.down3);
        up4 = findViewById(R.id.up4);


        down1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ib.getDrawable()==null) {
                    Toast.makeText(Authenticator.this, "provide an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                up2.setColorFilter(R.color.purple_500); down2.setColorFilter(R.color.purple_500);
                up2.setClickable(true); down2.setClickable(true);
                down1.setColorFilter(R.color.black); down1.setClickable(false);

                imageBt.setClickable(false); step2.setVisibility(View.VISIBLE);
            }
        });
        up2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secretKeyResult = null;

                up2.setColorFilter(R.color.black); down2.setColorFilter(R.color.black);
                up2.setClickable(false); down2.setClickable(false);
                down1.setColorFilter(R.color.purple_500); down1.setClickable(true);

                kyTxt.setText(""); imageBt.setClickable(true); step2.setVisibility(View.INVISIBLE);
            }
        });
        down2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!kyTxt.getText().toString().isEmpty()) {
                    up2.setColorFilter(R.color.black); down2.setColorFilter(R.color.black);
                    up2.setClickable(false); down2.setClickable(false);
                    up3.setColorFilter(R.color.purple_500); down3.setColorFilter(R.color.purple_500);
                    up3.setClickable(true); down3.setClickable(true);

                    keyBt.setClickable(false); step3.setVisibility(View.VISIBLE);
                }
            }
        });
        up3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up3.setColorFilter(R.color.black); down3.setColorFilter(R.color.black);
                up3.setClickable(false); down3.setClickable(false);
                up2.setColorFilter(R.color.purple_500); down2.setColorFilter(R.color.purple_500);
                up2.setClickable(true); down2.setClickable(true);

                keyBt.setClickable(true); qrImg.setImageResource(R.drawable.border); step3.setVisibility(View.INVISIBLE);
            }
        });
        down3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up3.setColorFilter(R.color.black); down3.setColorFilter(R.color.black);
                up3.setClickable(false); down3.setClickable(false);
                up4.setColorFilter(R.color.purple_500); up4.setClickable(true);

                step4.setVisibility(View.VISIBLE);
            }
        });
        up4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up3.setColorFilter(R.color.purple_500); down3.setColorFilter(R.color.purple_500);
                up3.setClickable(true); down3.setClickable(true);
                up4.setColorFilter(R.color.black); up4.setClickable(false);

                qrBt.setClickable(true); step4.setVisibility(View.INVISIBLE);
            }
        });


        imageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(Authenticator.this).galleryOnly().maxResultSize(1024,1024).start();
                // see onActivityResult(...)
            }
        });
        keyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lpi.setVisibility(View.VISIBLE);
                up2.setClickable(false); down2.setClickable(false);
                // making key
                bitImg = ((BitmapDrawable) ib.getDrawable()).getBitmap();
                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
                secretKeyResult = KeyGenerator.getKeyFromImage(Authenticator.bitImg);
                if(secretKeyResult.equals("")) {
                    secretKeyResult = null;
                    Toast.makeText(Authenticator.this, "select a strong image for key", Toast.LENGTH_LONG).show();
                    lpi.setVisibility(View.INVISIBLE);
                    up2.callOnClick(); return;
                }
                StringBuilder sb = new StringBuilder();
                int count=0, i=0;
                while(i<secretKeyResult.length()) {
                    sb.append(secretKeyResult.charAt(i++));
                    count++;
                    if(count==4) {
                        count=0; sb.append(" ");
                    }
                }
                kyTxt.setText(sb.toString());
                lpi.setVisibility(View.INVISIBLE);
                up2.setClickable(true); down2.setClickable(true);
            }
        });
        qrBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // making QR code
                if(secretKeyResult==null) return;
                String qrSupported = "otpauth://totp/COER:"+MainActivity.fba.getCurrentUser().getEmail()+"?secret="+secretKeyResult+"&isuser=picauth";
                Bitmap bitQR = QRCode.from(qrSupported).bitmap();
                qrImg.setImageBitmap(bitQR);
            }
        });
        cpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // copy key to clipboard
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData cd = ClipData.newPlainText("key",secretKeyResult);
                cm.setPrimaryClip(cd);
                Toast.makeText(Authenticator.this, "copied", Toast.LENGTH_SHORT).show();
            }
        });
        verifyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // moving to setup verification
                startActivity(new Intent(Authenticator.this,SetupVerifier.class)); finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
            uri = data.getData(); // getting uri of selected image
            if(uri!=null) {
                ib.setImageURI(uri); // setting the image on image button/view
                bitImg = ((BitmapDrawable) ib.getDrawable()).getBitmap(); // converting into bitmap for further use
            } else {
                uri = null; bitImg = null;
            }
        } else {
            uri = null; bitImg = null;
        }
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        bitImg=null; uri=null; secretKeyResult=null;
        Intent i = new Intent(Authenticator.this,Third.class);
        startActivity(i); finish();
    }
}