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
    DBHelperTime dbHelperTime;
    SQLiteDatabase dbt;
    String sId;
    String sName;
    String sPw;
    int iTime = 0;
    String sDate;


    Calendar calendar;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        state=0;

        EditText editTextId = findViewById(R.id.editTextID);
        EditText editTextName = findViewById(R.id.editTextNAME);
        EditText editTextPw = findViewById(R.id.editTextPW);
        EditText editTextPwCheck = findViewById(R.id.editTextPWCheck);

        dbHelperUser = new DBHelperUser(this);
        dbHelperTime = new DBHelperTime(this);

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

                ContentValues values_time = new ContentValues();

                values_time.put("userid", sId);
                values_time.put("name", sName);
                values_time.put("d_time", 0);
                values_time.put("best", 0);


                if (editTextId.getText().toString().replace(" ", "").equals("")
                    || editTextName.getText().toString().replace(" ", "").equals("")
                    || editTextPw.getText().toString().replace(" ", "").equals("")
                    || editTextPwCheck.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "??? ?????? ???????????????", Toast.LENGTH_SHORT).show();
                }

                else if (!(editTextPw.getText().toString().equals(editTextPwCheck.getText().toString()))) {
                    Toast.makeText(getApplicationContext(), "??????????????? ?????? ?????? ????????????.", Toast.LENGTH_SHORT).show();
                }

                else {
                    if(state == 0){
                        Toast.makeText(getApplicationContext(), "????????? ??????????????? ???????????????", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        db = dbHelperUser.getWritableDatabase();
                        db.insert(DBContractUser.TABLE_NAME, null, values);

                        dbt = dbHelperTime.getWritableDatabase();
                        dbt.insert(DBContractTime.TABLE_NAME2, null, values_time);

                        Toast.makeText(getApplicationContext(), "???????????? ??????!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });


        Button checkButton = (Button) findViewById(R.id.buttonIDcheck); // ????????? ????????????
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sId = editTextId.getText().toString().trim();
                if(editTextId.getText().toString().trim().length() <= 4 || editTextId.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "???????????? 5??? ?????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                }
                else {
                    db = dbHelperUser.getReadableDatabase();
                    Cursor cursor = db.rawQuery(DBContractUser.SQL_SELECT_ID, new String[]{sId});
                    if(cursor.getCount() != 0){
                        cursor.moveToNext();
                        Toast.makeText(getApplicationContext(), editTextId.getText().toString() + "?????? ?????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), editTextId.getText().toString() + "??? ?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        state = 1;
                    }
                }
            }
        });
    }

}