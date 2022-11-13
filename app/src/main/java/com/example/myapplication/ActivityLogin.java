package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class ActivityLogin extends AppCompatActivity {

    public static ActivityLogin activityL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activityL = this;

        Button registerButton = (Button) findViewById(R.id.RegisterBtn);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityRegister.class);
                startActivity(intent);
            }
        });

        Button loginButton = (Button) findViewById(R.id.LoginBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityTimer.class);
                startActivity(intent);
            }
        });

        ImageView titleview;
        titleview = (ImageView) findViewById(R.id.titleimage);
        final Animation animtitle = AnimationUtils.loadAnimation(this,R.anim.title);

        titleview.startAnimation(animtitle);
    }
}