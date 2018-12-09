package com.example.flamboyant.plugged.main;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.flamboyant.plugged.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        mAuth=FirebaseAuth.getInstance();
        final Handler Handle = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        final FirebaseUser user1= FirebaseAuth.getInstance().getCurrentUser();
                        if(user1 != null){
                            Intent intent = new Intent(SplashScreenActivity.this, SearchInterface.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                        else{
                            Intent intent1 = new Intent(SplashScreenActivity.this,LoginRegistrationActivity.class);
                            startActivity(intent1);
                            finish();
                            return;
                        }

                    }
                };
                mAuth.addAuthStateListener(firebaseAuthStateListener);


            }
        };
        Handle.postDelayed(r,4000);


    }
}
