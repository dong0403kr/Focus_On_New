package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivityRegister extends AppCompatActivity {

    DBHelperUser dbHelperUser;
    SQLiteDatabase db;
    String sId;
    String sName;
    String sPw;
    int iTime = 0;
    String sDate;


    Calendar calendar;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

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

        dbHelperUser = new DBHelperUser(this);

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
                sId = editTextId.getText().toString().trim();
                sName = editTextName.getText().toString().trim();
                sPw = editTextPw.getText().toString().trim();

                calendar = Calendar.getInstance();
                sDate = mFormat.format(calendar.getTime());

                ContentValues values = new ContentValues();

                values.put("userid", sId);
                values.put("name", sName);
                values.put("pw", sPw);
                values.put("Time", iTime);
                values.put("signdate", sDate);


                if (editTextId.getText().toString().replace(" ", "").equals("")
                    || editTextName.getText().toString().replace(" ", "").equals("")
                    || editTextPw.getText().toString().replace(" ", "").equals("")
                    || editTextPwCheck.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "빈 칸을 채워주세요", Toast.LENGTH_SHORT).show();
                }

                else if (!(editTextPw.getText().toString().equals(editTextPwCheck.getText().toString()))) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 서로 같지 않습니다.", Toast.LENGTH_SHORT).show();
                }

                else {
                    if(state == 0){
                        Toast.makeText(getApplicationContext(), "아이디 중복확인이 필요합니다", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        db = dbHelperUser.getWritableDatabase();
                        db.insert(DBContractUser.TABLE_NAME, null, values);

                        Toast.makeText(getApplicationContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });


        Button checkButton = (Button) findViewById(R.id.buttonIDcheck); // 아이디 중복확인
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sId = editTextId.getText().toString().trim();
                if(editTextId.getText().toString().trim().length() <= 4 || editTextId.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "아이디를 5자 이상 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    db = dbHelperUser.getReadableDatabase();
                    Cursor cursor = db.rawQuery(DBContractUser.SQL_SELECT_ID, new String[]{sId});
                    if(cursor.getCount() != 0){
                        cursor.moveToNext();
                        Toast.makeText(getApplicationContext(), editTextId.getText().toString() + "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), editTextId.getText().toString() + "는 사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                        state = 1;
                    }
                }
            }
        });
    }

}