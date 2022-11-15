package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivityProfile extends AppCompatActivity {

    public static ActivityProfile activityP = null;

    DBHelperTime dbHelperTime;
    SQLiteDatabase dbt;
    DBHelperUser dbHelperUser;
    SQLiteDatabase db;

    int sum_time;
    int h,m,s;
    int level;
    String dbName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        activityP = this;

        dbHelperUser = new DBHelperUser(this);
        dbHelperTime = new DBHelperTime(this);

        db = dbHelperUser.getReadableDatabase();
        Cursor cursor2 = db.rawQuery(DBContractUser.SQL_SELECT_ID, new String[]{State.LOGIN});

        for(int i=0; i<cursor2.getCount(); i++){  // 유저db에서 누적 공부시간 가져옴
            cursor2.moveToNext();
            sum_time=cursor2.getInt(4);
            dbName=cursor2.getString(2);
        }
        cursor2.close();

        level = (int)sum_time/3600;
        TextView textViewLevel = findViewById(R.id.textView_level);
        textViewLevel.setText(String.valueOf(level));
        TextView textViewsumtime = findViewById(R.id.textView_sumtime);
        h = (int)(sum_time /3600);
        m = (int)(sum_time - h*3600)/60;
        s = (int)(sum_time - h*3600- m*60);
        textViewsumtime.setText(String.format("%d시간 %d분 %d초", h, m, s));
        TextView textViewName = findViewById(R.id.textViewName);
        textViewName.setText(dbName);




        Button buttonTimer = (Button) findViewById(R.id.button_Timer_p);
        buttonTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(State.TIMER==0) {
                    Intent intent = new Intent(getApplicationContext(), ActivityTimer.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), ActivityMarathon.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Button buttonRanking = (Button) findViewById(R.id.button_Ranking_p);
        buttonRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityRanking.class);
                startActivity(intent);
                finish();
            }
        });

        Button buttonSettings = (Button) findViewById(R.id.button_Setting_p);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivitySettings.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });
    } // onCreate

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
        if(ActivityRanking.activityR!=null){
            ActivityRanking activity_r = (ActivityRanking) ActivityRanking.activityR;
            activity_r.finish();
        }
        if(ActivityTimer.activityT!=null){
            ActivityTimer activity_t = (ActivityTimer) ActivityTimer.activityT;
            activity_t.finish();
        }
        if(ActivityMarathon.activityM!=null){
            ActivityMarathon activity_m = (ActivityMarathon) ActivityMarathon.activityM;
            activity_m.finish();
        }
        if(ActivityRanking_m.activityRM!=null){
            ActivityRanking_m activity_rm = (ActivityRanking_m) ActivityRanking_m.activityRM;
            activity_rm.finish();
        }

        super.onBackPressed();
    }
}