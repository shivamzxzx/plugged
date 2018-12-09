package com.example.flamboyant.plugged.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.flamboyant.plugged.R;

public class LoginRegistrationActivity extends AppCompatActivity {
    private Button mLogin,mSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);
        mLogin = (Button) findViewById(R.id.Login);
        mSignup = (Button) findViewById(R.id.Signup);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginRegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginRegistrationActivity.this,SignupActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}




