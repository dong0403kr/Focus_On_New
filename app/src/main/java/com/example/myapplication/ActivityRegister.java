package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityRegister extends AppCompatActivity {

    private int state;

    public static ActivityRegister activityR = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        activityR = this;

        state=0;

        EditText editTextId = findViewById(R.id.editTextID);
        EditText editTextName = findViewById(R.id.editTextNAME);
        EditText editTextPw = findViewById(R.id.editTextPW);
        EditText editTextPwCheck = findViewById(R.id.editTextPWCheck);

        TextView textview;
        textview = (TextView) findViewById(R.id.regtitle);
        final Animation animtitle = AnimationUtils.loadAnimation(this,R.anim.title);

        textview.startAnimation(animtitle);

        Animation reganim = AnimationUtils.loadAnimation(this,R.anim.reglayout);
        ViewGroup reglayout = (ViewGroup) findViewById(R.id.regLayout);

        reglayout.startAnimation(reganim);

        Button imageButton = (Button) findViewById(R.id.buttonExit);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        });


        Button imageButton2 = (Button) findViewById(R.id.buttonSignUp);
        imageButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (editTextId.getText().toString().replace(" ", "").equals("")
                    || editTextName.getText().toString().replace(" ", "").equals("")
                    || editTextPw.getText().toString().replace(" ", "").equals("")
                    || editTextPwCheck.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "빈 칸을 채워주세요", Toast.LENGTH_SHORT).show();
                }

                else {
                    if(state == 0){
                        Toast.makeText(getApplicationContext(), "아이디 중복확인이 필요합니다", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });


        Button checkButton = (Button) findViewById(R.id.buttonIDcheck);
        checkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(editTextId.getText().toString().trim().length() <= 4 || editTextId.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "아이디를 5자 이상 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), editTextId.getText().toString() + "는 사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                    state=1;
                }
            }
        });
    }

}