package com.codewithshubh.covid_19tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SplashActivity extends AppCompatActivity{

    private String version;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, databaseReference2;
    private String appUrl;
    private String appURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        CheckForUpdate();
    }

    private void CheckForUpdate() {
        try {
            version = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.d("Version: ", version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Version").child("versionName");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String versionName = (String) dataSnapshot.getValue();

                if(!versionName.equals(version)){
                    //Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                    AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this)
                            .setTitle("New Version Available!")
                            .setMessage("Please update our app to the latest version for continuous use.")
                            .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Version").child("appURL");
                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            appURL = dataSnapshot.getValue().toString();
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURL)));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            })
                            .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create();

                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);

                    alertDialog.show();
                }
                else{
                    int SPLASH_SCREEN_TIMEOUT = 2000;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, SPLASH_SCREEN_TIMEOUT);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}