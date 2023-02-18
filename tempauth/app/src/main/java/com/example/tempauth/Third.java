package com.example.tempauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Third extends AppCompatActivity
{
    FirebaseDatabase fbdb;
    public static DatabaseReference dbref;

    FirebaseStorage fbst;
    public static StorageReference rootSr, userSr;

    Button keysetup, backup, token, remove, signout, delacc;
    SwitchCompat swt;
    static boolean success;

    //static String[] resArr = {"false","true","err"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        keysetup = findViewById(R.id.button4);
        backup = findViewById(R.id.button6);
        token = findViewById(R.id.button17);
        remove = findViewById(R.id.button7);
        signout = findViewById(R.id.button11);
        delacc = findViewById(R.id.button12);
        swt = findViewById(R.id.switch0);

        fbdb = FirebaseDatabase.getInstance();
        dbref = fbdb.getReference("users");

        fbst = FirebaseStorage.getInstance();
        rootSr = fbst.getReference();
        userSr = rootSr.child("users");

        swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) { // turn on 2FA
                    dbref.child(MainActivity.fba.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()) {
                                DataSnapshot ds = task.getResult();
                                if(ds.exists()) {
                                    if(ds.child("newU").getValue().toString().equals("true")) {
                                        Intent i = new Intent(Third.this, Authenticator.class);
                                        startActivity(i); finish();
                                    }
                                    else {
                                        setData(true,false,ds.child("scrtK").getValue().toString());
                                        keysetup.setVisibility(View.VISIBLE); backup.setVisibility(View.VISIBLE); token.setVisibility(View.VISIBLE); remove.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
                } else { // turn off 2FA
                    dbref.child(MainActivity.fba.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()) {
                                DataSnapshot ds = task.getResult();
                                if(ds.exists()) {
                                    if(ds.child("newU").getValue().toString().equals("false")) { // turn off case ending up to newU:true is when 2FA is removed, not when temporarily disabled
                                        setData(false, false, ds.child("scrtK").getValue().toString());
                                        keysetup.setVisibility(View.INVISIBLE); backup.setVisibility(View.INVISIBLE); token.setVisibility(View.INVISIBLE); remove.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        keysetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Third.this, Authenticator.class);
                startActivity(i); finish();
            }
        });

        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TO DO
            }
        });

        token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog tokenDg = new Dialog(Third.this);
                tokenDg.setContentView(R.layout.token_dialog);
                ImageView iv = tokenDg.findViewById(R.id.imageView);
                tokenDg.show();
                tokenDg.setCancelable(true);
                userSr.child(MainActivity.fba.getCurrentUser().getUid()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful() && task.getResult()!=null) {
                            Glide.with(Third.this).load(task.getResult()).into(iv);
                        }
                    }
                });
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog removeDlg = new Dialog(Third.this);
                removeDlg.setContentView(R.layout.remove_2fa_dialog);
                removeDlg.setCancelable(false);
                Button cancel=removeDlg.findViewById(R.id.button15), confirm=removeDlg.findViewById(R.id.button16);
                removeDlg.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeDlg.dismiss();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setData(false,true,"null");
                        keysetup.setVisibility(View.INVISIBLE); backup.setVisibility(View.INVISIBLE); token.setVisibility(View.INVISIBLE); remove.setVisibility(View.INVISIBLE);
                        userSr.child(MainActivity.fba.getCurrentUser().getUid()).delete();
                        swt.setChecked(false);
                        removeDlg.dismiss();
                    }
                });
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.fba.signOut();
                Intent i = new Intent(Third.this, MainActivity.class); startActivity(i); finish();
            }
        });

        delacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reusing remove_2fa_dialog
                Dialog deleteDlg = new Dialog(Third.this);
                deleteDlg.setContentView(R.layout.remove_2fa_dialog);
                deleteDlg.setCancelable(false);
                TextView tv = deleteDlg.findViewById(R.id.textView3); tv.setText("delete account");
                Button cancel=deleteDlg.findViewById(R.id.button15), confirm=deleteDlg.findViewById(R.id.button16);
                deleteDlg.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDlg.dismiss();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // delete data
                        userSr.child(MainActivity.fba.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful())
                                    Toast.makeText(Third.this, "a problem occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dbref.child(MainActivity.fba.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    // delete user
                                    MainActivity.fba.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent i = new Intent(Third.this, MainActivity.class); startActivity(i); finish();
                                            } else {
                                                Toast.makeText(Third.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                                setData(false,false,"null"); // user deletion failed, his/her place in database need to be revived
                                                startActivity(new Intent(Third.this, Third.class)); finish(); // reload activity
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(Third.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        deleteDlg.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // retrieving data from firebase and setting up activity accordingly
        if (MainActivity.fba.getCurrentUser() != null) {
            dbref.child(MainActivity.fba.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        if(snapshot.child("newU").getValue().toString().equals("true")) // [twoFA,newU] = [F,T]
                        {
                            swt.setChecked(false);
                            keysetup.setVisibility(View.INVISIBLE);
                            backup.setVisibility(View.INVISIBLE);
                            token.setVisibility(View.INVISIBLE);
                            remove.setVisibility(View.INVISIBLE);
                        }
                        else if(snapshot.child("twoFA").getValue().toString().equals("true")) // [twoFA,newU] = [T,F]
                        {
                            swt.setChecked(true);
                            keysetup.setVisibility(View.VISIBLE);
                            backup.setVisibility(View.VISIBLE);
                            token.setVisibility(View.VISIBLE);
                            remove.setVisibility(View.VISIBLE);
                        }
                        else // [twoFA,newU] = [F,F]
                        {
                            swt.setChecked(false);
                            keysetup.setVisibility(View.INVISIBLE);
                            backup.setVisibility(View.INVISIBLE);
                            token.setVisibility(View.INVISIBLE);
                            remove.setVisibility(View.VISIBLE);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    // method to get [ twoFA, newU ]
//    public static String[] getData()
//    {
//        // retrieving data from firebase real-time database
//        MainActivity.fba.getCurrentUser().reload();
//        if (MainActivity.fba.getCurrentUser() != null) {
//            dbref.child(MainActivity.fba.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if(task.isSuccessful()) {
//                        DataSnapshot ds = task.getResult();
//                        if(ds.exists()) {
//                            Third.resArr[0] = ds.child("twoFA").getValue().toString();
//                            Third.resArr[1] = ds.child("newU").getValue().toString();
//                            Third.resArr[2] = ds.child("scrtK").getValue().toString();
//                        }
//                    }
//                }
//            });
//        }
//        return resArr;
//    }

    // method to set [ twoFA, newU ]
    public static boolean setData(boolean twoFA, boolean newU, String scrtK)
    {
        // setting data into firebase real-time database
        MainActivity.fba.getCurrentUser().reload();
        if(MainActivity.fba.getCurrentUser()!=null) {
            User u = new User(twoFA, newU, scrtK);
            dbref.child(MainActivity.fba.getCurrentUser().getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Third.success = task.isSuccessful();
                }
            });
        }
        return success;
    }
}