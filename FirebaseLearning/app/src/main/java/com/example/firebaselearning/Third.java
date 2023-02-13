package com.example.firebaselearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.ByteBuffer;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Third extends AppCompatActivity {

    FirebaseDatabase fbdb;  // REALTIME DATABASE
    public static DatabaseReference rootRef, userRef;

    FirebaseFirestore fbfs;  // CLOUD FIRESTORE
    public static CollectionReference clct;

    FirebaseStorage fbst;  // FIREBASE STORAGE
    public static StorageReference rootSr, userSr;

    TextView email;
    EditText name, phone;
    Button signout, others, update, delete, delacc, change, dgsave, dgcancel, dgok;
    CircleImageView civ;
    ImageButton dgib, dgedit, dgdel;
    LinearProgressIndicator dglpi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_third);

        fbdb = FirebaseDatabase.getInstance();
        rootRef = fbdb.getReference();
        userRef = rootRef.child("users");

        fbfs = FirebaseFirestore.getInstance();
        clct = fbfs.collection("users");

        fbst = FirebaseStorage.getInstance();
        rootSr = fbst.getReference();
        userSr = rootSr.child("users");

        email = findViewById(R.id.textView2);
        name = findViewById(R.id.editTextTextPersonName);
        phone = findViewById(R.id.editTextPhone);
        signout = findViewById(R.id.button4);
        others = findViewById(R.id.button6);
        update = findViewById(R.id.button7);
        delete = findViewById(R.id.button8);
        delacc = findViewById(R.id.button10);
        change = findViewById(R.id.button14);
        civ = findViewById(R.id.circleImage);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.fba.signOut();
                Intent i = new Intent(Third.this, MainActivity.class); startActivity(i); finish();
            }
        });
        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Third.this, Fourth.class); startActivity(i); finish();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty()) {
                    name.setError("fill name"); return;
                }
                if(phone.getText().toString().isEmpty()) {
                    phone.setError("fill phone"); return;
                }
                User u = new User(email.getText().toString(),name.getText().toString(),phone.getText().toString());
                // adding data of user into real-time DB with UID
                userRef.child(MainActivity.fba.getCurrentUser().getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Third.this, "data updated", Toast.LENGTH_SHORT).show();
                            delete.setVisibility(View.VISIBLE);
                            others.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(Third.this, "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // removing data of user from real-time DB with UID
                userRef.child(MainActivity.fba.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Third.this, "data deleted", Toast.LENGTH_SHORT).show();
                            name.setText(""); phone.setText("");
                            //tempName = ""; tempPhone = "";
                            others.setVisibility(View.INVISIBLE);
                            //update.setVisibility(View.INVISIBLE);
                            //try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
                            delete.setVisibility(View.INVISIBLE);
                        } else {
                            Toast.makeText(Third.this, "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        delacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dlg = new Dialog(Third.this);
                dlg.setContentView(R.layout.custom_dialog1);
                dlg.setCancelable(false); // not get dismissed when user click outside it
                Button ok = dlg.findViewById(R.id.button12), cancel = dlg.findViewById(R.id.button11);
                dlg.show();
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete.callOnClick();
                        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                        if(MainActivity.fba.getCurrentUser()!=null) {
                            MainActivity.fba.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(Third.this, MainActivity.class); startActivity(i); finish();
                                    }
                                }
                            });
                        }
                        dlg.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dlg.dismiss();
                    }
                });
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Third.this,ChangePassword.class); startActivity(i); finish();
            }
        });

        // dialog for viewing & updating profile pic
        Dialog picDg = new Dialog(Third.this);
        picDg.setContentView(R.layout.profile_pic);
        dgib = picDg.findViewById(R.id.imageButton);
        dgedit = picDg.findViewById(R.id.imageButton1);
        dgdel = picDg.findViewById(R.id.imageButton2);
        dgsave = picDg.findViewById(R.id.button13);
        dgcancel = picDg.findViewById(R.id.button17);
        dgok = picDg.findViewById(R.id.button18);
        dglpi = picDg.findViewById(R.id.linearProgress3);
        // circular image view click
        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showing current image from civ in picDg image
                dgib.setImageBitmap( ((BitmapDrawable)(civ.getDrawable())).getBitmap() ); // set using bitmap
                dgok.setVisibility(View.VISIBLE); dgsave.setVisibility(View.INVISIBLE); dgcancel.setVisibility(View.INVISIBLE);
                picDg.show(); // show the profile dialog box
                picDg.setCancelable(false); // cannot be dismissed when clicked outside of the dialog

                dgib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // SHOW FULL SCREEN IMAGE & DOWNLOAD BUTTON
                    }
                });
                dgok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        picDg.dismiss();
                    }
                });
                dgedit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePicker.with(Third.this).cropSquare().start(); // selecting image for profile picture
                        // see onActivityResult()
                    }
                });
                dgdel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dgib.setImageResource(R.drawable.orng_user);
                        imageData = null;
                        dgok.setVisibility(View.INVISIBLE); dgsave.setVisibility(View.VISIBLE); dgcancel.setVisibility(View.VISIBLE);
                    }
                });
                dgsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Bitmap bm = ((BitmapDrawable)(civ.getDrawable())).getBitmap();
//                        int size = bm.getRowBytes() * bm.getHeight(); // does row bytes are different for every row ??
//                        ByteBuffer bmBB = ByteBuffer.allocate(size);
//                        bm.copyPixelsToBuffer(bmBB);
//                        byte[] bmBytes = bmBB.array();
                        dglpi.setVisibility(View.VISIBLE);
                        if(imageData==null) {
                            userSr.child(MainActivity.fba.getCurrentUser().getEmail()).delete();
                            clct.document(MainActivity.fba.getCurrentUser().getEmail()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        civ.setImageResource(R.drawable.orng_user); // default image
                                        dglpi.setVisibility(View.INVISIBLE);
                                        picDg.dismiss();
                                    }
                                }
                            });
                            return;
                        }

                        HashMap<String,String> hm = new HashMap<>();
                        // saving image to firebase storage
                        userSr.child(MainActivity.fba.getCurrentUser().getEmail()).putFile(imageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()) {
                                    // accessing the storage generated URI
                                    userSr.child(MainActivity.fba.getCurrentUser().getEmail()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if(task.isSuccessful()) {
                                                imageData = task.getResult();
//                                                try { Thread.sleep(1000); }
//                                                catch(InterruptedException e) { e.printStackTrace(); }
                                                //if(imageData==null) return;
                                                hm.put("pic", imageData.toString());
                                                // saving the storage generated URI into firestore
                                                clct.document(MainActivity.fba.getCurrentUser().getEmail()).set(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Glide.with(Third.this).load( imageData ).into(civ);
                                                            dglpi.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(Third.this, "saved", Toast.LENGTH_SHORT).show();
                                                            picDg.dismiss();
                                                        } else {
                                                            dglpi.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(Third.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    dglpi.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Third.this, "cloud storage error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dgcancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageData = null;
                        picDg.dismiss();
                    }
                });
            }
        });
    }
    Uri imageData;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageData = null;
        if(data!=null) imageData = data.getData();
        if(imageData!=null) {
            dgib.setImageURI(imageData);
            dgok.setVisibility(View.INVISIBLE); dgsave.setVisibility(View.VISIBLE); dgcancel.setVisibility(View.VISIBLE);
        }
    }

    private long pressedTime;
    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) { // false at first, as 2000 will never be greater than current millis
            // after one back press, pressedTime get its value, and now we have 2 secs (2000) to again press back to exit the app
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        } pressedTime = System.currentTimeMillis(); // current elapsed time in millis
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(MainActivity.fba.getCurrentUser()!=null) {
            email.setText(MainActivity.fba.getCurrentUser().getEmail());

            // fetching data from firebase real-time DB
            userRef.child(MainActivity.fba.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        name.setText(snapshot.getValue(User.class).getName()); // or snapshot.child("name").getValue().toString()
                        phone.setText(snapshot.getValue(User.class).getPhone());
                        others.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.VISIBLE);
                    } else { // data of others and data delete button not visible if user user data is not present in firebase
                        others.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            // fetching image from firestore
            clct.document(MainActivity.fba.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.exists()) {
                        // civ.setImageURI(Uri.parse((String)value.get("pic"))); // only work for local(device) file URIs
                        Glide.with(Third.this).load( Uri.parse((String)value.get("pic")) ).into(civ);
                    }
                }
            });
        }
//        if(!name.getText().toString().isEmpty() && !phone.getText().toString().isEmpty()) {
//            tempName = name.getText().toString();
//            tempPhone = phone.getText().toString();
//        }
//        name.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(name.getText().toString().equals(tempName)) update.setVisibility(View.INVISIBLE);
//                else update.setVisibility(View.VISIBLE);
//            }
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(name.getText().toString().equals(tempName)) update.setVisibility(View.INVISIBLE);
//                else update.setVisibility(View.VISIBLE);
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if(name.getText().toString().equals(tempName)) update.setVisibility(View.INVISIBLE);
//                else update.setVisibility(View.VISIBLE);
//            }
//        });
//        phone.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(phone.getText().toString().equals(tempPhone)) update.setVisibility(View.INVISIBLE);
//                else update.setVisibility(View.VISIBLE);
//            }
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(phone.getText().toString().equals(tempPhone)) update.setVisibility(View.INVISIBLE);
//                else update.setVisibility(View.VISIBLE);
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if(phone.getText().toString().equals(tempPhone)) update.setVisibility(View.INVISIBLE);
//                else update.setVisibility(View.VISIBLE);
//            }
//        });
    }
}