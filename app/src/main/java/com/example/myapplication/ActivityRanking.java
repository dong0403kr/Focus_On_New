package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityRanking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);


        Button buttonTimer = (Button) findViewById(R.id.button_Timer_r);
        buttonTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityTimer.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });

        Button buttonProfile = (Button) findViewById(R.id.button_Profile_r);
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityProfile.class);
                startActivity(intent);
                finish();
            }
        });

        Button buttonSettings = (Button) findViewById(R.id.button_Setting_r);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivitySettings.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {   // 뒤로가기 누르면 다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("앱을 종료하시겠습니까?"); // 다이얼로그 제목
        builder.setCancelable(true);   // 다이얼로그 화면 밖 터치 방지
        builder.setPositiveButton("예", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                exit();
            }
        });

        builder.setNegativeButton("아니요", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show(); // 다이얼로그 보이기
    }
    public void exit() { // 종료
        if(ActivitySettings.activityS!=null){
            ActivitySettings activity_s = (ActivitySettings) ActivitySettings.activityS;
            activity_s.finish();
        }
        if(ActivityLogin.activityL!=null){
            ActivityLogin activity_l = (ActivityLogin) ActivityLogin.activityL;
            activity_l.finish();
        }
        if(ActivityRegister.activityR!=null){
            ActivityRegister activity_r = (ActivityRegister) ActivityRegister.activityR;
            activity_r.finish();
        }
        if(ActivityTimer.activityT!=null){
            ActivityTimer activity_t = (ActivityTimer) ActivityTimer.activityT;
            activity_t.finish();
        }

        super.onBackPressed();
    }
}