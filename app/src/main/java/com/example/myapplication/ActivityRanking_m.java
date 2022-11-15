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

public class ActivityRanking_m extends AppCompatActivity {

    DBHelperTime dbHelperTime;
    SQLiteDatabase dbt;
    int dbmtime1,dbmtime2,dbmtime3,dbmtime4,dbmtime5,dbmtimemy,mrankmy;
    String dbmname1="",dbmname2="",dbmname3="",dbmname4="",dbmname5="";
    int h,m,s;

    public static ActivityRanking_m activityRM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_m);

        activityRM = this;

        dbHelperTime = new DBHelperTime(this);
        dbt = dbHelperTime.getReadableDatabase();
        Button buttonBR = findViewById(R.id.ranking_basic);
        buttonBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityRanking.class);
                startActivity(intent);
                finish();
            }
        });
        TextView textView1stMname = findViewById(R.id.name_1st_m);
        TextView textView1stMtime = findViewById(R.id.time_1st_m);
        TextView textView2ndMname = findViewById(R.id.name_2nd_m);
        TextView textView2ndMtime = findViewById(R.id.time_2nd_m);
        TextView textView3rdMname = findViewById(R.id.name_3rd_m);
        TextView textView3rdMtime = findViewById(R.id.time_3rd_m);
        TextView textView4thMname = findViewById(R.id.name_4th_m);
        TextView textView4thMtime = findViewById(R.id.time_4th_m);
        TextView textView5thMname = findViewById(R.id.name_5th_m);
        TextView textView5thMtime = findViewById(R.id.time_5th_m);
        TextView textViewmyMrank = findViewById(R.id.my_ranking_m);
        TextView textViewmyMtime = findViewById(R.id.my_record_m);

        Cursor cursor = dbt.rawQuery("SELECT * FROM TIME ORDER BY BEST DESC", null);

        for (int i = 0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            if(i==0) {
                dbmtime1 = cursor.getInt(4);
                dbmname1 = cursor.getString(2);
            }
            if(i==1) {
                dbmtime2 = cursor.getInt(4);
                dbmname2 = cursor.getString(2);
            }
            if(i==2) {
                dbmtime3 = cursor.getInt(4);
                dbmname3 = cursor.getString(2);
            }
            if(i==3) {
                dbmtime4 = cursor.getInt(4);
                dbmname4 = cursor.getString(2);
            }
            if(i==4) {
                dbmtime5 = cursor.getInt(4);
                dbmname5 = cursor.getString(2);
            }
            if(State.LOGIN.equals(cursor.getString(1))){
                mrankmy = (i+1);
                dbmtimemy = cursor.getInt(4);
            }
        }
        cursor.close();

        textViewmyMrank.setText(mrankmy + "위");
        h = (int)(dbmtimemy /3600);
        m = (int)(dbmtimemy - h*3600)/60;
        s = (int)(dbmtimemy - h*3600- m*60);
        textViewmyMtime.setText(String.format("%d시간 %d분 %d초", h, m, s));

        if(!(dbmname1.replace(" ", "").equals(""))){
            textView1stMname.setText(dbmname1);
            h = (int)(dbmtime1 /3600);
            m = (int)(dbmtime1 - h*3600)/60;
            s = (int)(dbmtime1 - h*3600- m*60);
            textView1stMtime.setText(String.format("%d시간 %d분 %d초", h, m, s));
        }
        if(!(dbmname2.replace(" ", "").equals(""))){
            textView2ndMname.setText(dbmname2);
            h = (int)(dbmtime2 /3600);
            m = (int)(dbmtime2 - h*3600)/60;
            s = (int)(dbmtime2 - h*3600- m*60);
            textView2ndMtime.setText(String.format("%d시간 %d분 %d초", h, m, s));
        }
        if(!(dbmname3.replace(" ", "").equals(""))){
            textView3rdMname.setText(dbmname3);
            h = (int)(dbmtime3 /3600);
            m = (int)(dbmtime3 - h*3600)/60;
            s = (int)(dbmtime3 - h*3600- m*60);
            textView3rdMtime.setText(String.format("%d시간 %d분 %d초", h, m, s));
        }
        if(!(dbmname4.replace(" ", "").equals(""))){
            textView4thMname.setText(dbmname4);
            h = (int)(dbmtime4 /3600);
            m = (int)(dbmtime4 - h*3600)/60;
            s = (int)(dbmtime4 - h*3600- m*60);
            textView4thMtime.setText(String.format("%d시간 %d분 %d초", h, m, s));
        }
        if(!(dbmname5.replace(" ", "").equals(""))){
            textView5thMname.setText(dbmname5);
            h = (int)(dbmtime5 /3600);
            m = (int)(dbmtime5 - h*3600)/60;
            s = (int)(dbmtime5 - h*3600- m*60);
            textView5thMtime.setText(String.format("%d시간 %d분 %d초", h, m, s));
        }

        Button buttonTimer = (Button) findViewById(R.id.button_Timer_rm);
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

        Button buttonProfile = (Button) findViewById(R.id.button_Profile_rm);
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityProfile.class);
                startActivity(intent);
                finish();
            }
        });

        Button buttonSettings = (Button) findViewById(R.id.button_Setting_rm);
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
        if(ActivityTimer.activityT!=null){
            ActivityTimer activity_t = (ActivityTimer) ActivityTimer.activityT;
            activity_t.finish();
        }
        if(ActivityMarathon.activityM!=null){
            ActivityMarathon activity_m = (ActivityMarathon) ActivityMarathon.activityM;
            activity_m.finish();
        }
        if(ActivityProfile.activityP!=null){
            ActivityProfile activity_p = (ActivityProfile) ActivityProfile.activityP;
            activity_p.finish();
        }
        if(ActivityRanking.activityR!=null){
            ActivityRanking activity_r = (ActivityRanking) ActivityRanking.activityR;
            activity_r.finish();
        }

        super.onBackPressed();
    }
}