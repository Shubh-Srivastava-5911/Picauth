package com.example.firebaselearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class Fourth extends AppCompatActivity {
    ListView lv;
    ArrayList<String> al;
    ArrayAdapter<String> aa; // for android lists
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_fourth);

        lv = findViewById(R.id.list);
        al = new ArrayList<>();
        aa = new ArrayAdapter<>(Fourth.this, R.layout.list_items, al);
        lv.setAdapter(aa);
        // You can use ArrayAdapter to provide views for an AdapterView,
        // Returns a view for each object in a collection of data objects you provide,
        // and can be used with list-based user interface widgets such as ListView or Spinner.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // splitting email and name from list text
                String tempItmStr = ((TextView)view).getText().toString();
                String[] tempItmStrArr = tempItmStr.split("\\n+");
                String strEmail=tempItmStrArr[0], strFullName="";
                for(int c=1; c<tempItmStrArr.length; c++) strFullName+=tempItmStrArr[c]+" ";

                // custom dialog box
                Dialog dlg = new Dialog(Fourth.this);
                dlg.setContentView(R.layout.custom_dialog);
                TextView dtv = dlg.findViewById(R.id.textView5);
                Button dbtn = dlg.findViewById(R.id.button9);
                ImageView dimg = dlg.findViewById(R.id.imageView2);
                dtv.setText(strFullName);
                Third.clct.document(strEmail).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists()) {
                            Glide.with(Fourth.this).load( Uri.parse((String)value.get("pic")) ).into(dimg);
                        }
                    }
                });
                dlg.show();
                dbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dlg.dismiss();
                    }
                });
                dimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // SHOW FULL SCREEN IMAGE & DOWNLOAD BUTTON
                        //Third.userSr.child(strEmail).getDownloadUrl();
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Third.userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    // preparing list of users by data fetching
                    al.clear();
                    for(DataSnapshot dsnap : snapshot.getChildren()) {
                        User temp = dsnap.getValue(User.class);
                        if(MainActivity.fba.getCurrentUser().getEmail().equals(temp.getEmail())) continue; // to skip this user
                        al.add(temp.getEmail() + "\n" + temp.getName()); //System.getProperty("line.separator")
                    }
                    aa.notifyDataSetChanged(); // to notify the adapter that the array list that it is using changed its data
                    // Notifies the attached observers that the underlying data has been changed and
                    // any View reflecting the data set should refresh itself.
                } else {
                    Toast.makeText(Fourth.this, "something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Fourth.this, Third.class); startActivity(i); finish();
    }
}