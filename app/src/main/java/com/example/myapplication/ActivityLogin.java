package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Map;

public class ActivityLogin extends AppCompatActivity {

    DBHelperUser dbHelperUser;
    SQLiteDatabase db;
    String sID;
    String sPW;
    String cPW;


    public static ActivityLogin activityL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activityL = this;

        dbHelperUser = new DBHelperUser(this);

        EditText editTextID = findViewById(R.id.editTextLoginId);
        EditText editTextPW = findViewById(R.id.editTextLoginPw);

        CheckBox checkAL = findViewById(R.id.checkAL);

        Map<String, String> loginInfo = SharedPreferencesManager.getLoginInfo(this);
        String ALName = loginInfo.get("name");

        if (!ALName.isEmpty() || !ALName.equals("")){

            Intent intentAuto = new Intent(getApplicationContext(), ActivityTimer.class);
            startActivity(intentAuto);

            Toast.makeText(getApplicationContext(), "자동로그인 아이디 : " + ALName, Toast.LENGTH_LONG).show();
            finish();
        }


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
                sID = editTextID.getText().toString().trim();
                sPW = editTextPW.getText().toString().trim();

                if(checkAL.isChecked()) { // 자동로그인 체크 시 SharedPreference에 닉네임 정보 저장
                    SharedPreferencesManager.setLoginInfo(getApplicationContext(), sID);
                }

                db = dbHelperUser.getReadableDatabase();
                Cursor cursor = db.rawQuery(DBContractUser.SQL_SELECT_ID, new String[]{sID});

                if(editTextID.getText().toString().replace(" ", "").equals("")
                || editTextPW.getText().toString().replace(" ", "").equals("")){
                    Toast.makeText(getApplicationContext(), "빈 칸을 채워주세요." , Toast.LENGTH_SHORT).show();
                }
                else if (cursor.getCount() != 0){
                    cursor.moveToNext();

                    Cursor cursorpw = db.rawQuery(DBContractUser.SQL_SELECT_ID, new String[]{sID});
                    for(int i = 0; i<cursorpw.getCount(); i++){
                        cursorpw.moveToNext();
                        cPW=cursorpw.getString(3);
                    }

                    if(editTextPW.getText().toString().equals(cPW.toString())){
                        State.LOGIN = sID;
                        Intent intent = new Intent(getApplicationContext(), ActivityTimer.class);
                        Toast.makeText(getApplicationContext(), "로그인 아이디 : " + sID, Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "비밀번호가 틀렸습니다." , Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "가입되지 않은 아이디입니다." , Toast.LENGTH_SHORT).show();
                }

            }
        });

        ImageView titleview;
        titleview = (ImageView) findViewById(R.id.titleimage);
        final Animation animtitle = AnimationUtils.loadAnimation(this,R.anim.title);

        titleview.startAnimation(animtitle);
    }
}