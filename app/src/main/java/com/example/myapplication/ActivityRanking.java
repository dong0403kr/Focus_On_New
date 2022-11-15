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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityRanking extends AppCompatActivity {

    DBHelperTime dbHelperTime;
    SQLiteDatabase dbt;
    int dbtime1,dbtime2,dbtime3,dbtime4,dbtime5,dbtimemy,rankmy;
    String dbname1="",dbname2="",dbname3="",dbname4="",dbname5="";
    int h,m,s;

    public static ActivityRanking activityR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        activityR = this;

        dbHelperTime = new DBHelperTime(this);
        dbt = dbHelperTime.getReadableDatabase();
        Button buttonMR = findViewById(R.id.ranking_marathon);
        buttonMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityRanking_m.class);
                startActivity(intent);
                finish();
            }
        });
        TextView textView1stname = findViewById(R.id.name_1st);
        TextView textView1sttime = findViewById(R.id.time_1st);
        TextView textView2ndname = findViewById(R.id.name_2nd);
        TextView textView2ndtime = findViewById(R.id.time_2nd);
        TextView textView3rdname = findViewById(R.id.name_3rd);
        TextView textView3rdtime = findViewById(R.id.time_3rd);
        TextView textView4thname = findViewById(R.id.name_4th);
        TextView textView4thtime = findViewById(R.id.time_4th);
        TextView textView5thname = findViewById(R.id.name_5th);
        TextView textView5thtime = findViewById(R.id.time_5th);
        TextView textViewmyrank = findViewById(R.id.my_ranking);
        TextView textViewmytime = findViewById(R.id.my_record);


        Cursor cursor = dbt.rawQuery("SELECT * FROM TIME ORDER BY D_TIME DESC", null);

        for (int i = 0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            if(i==0) {
                dbtime1 = cursor.getInt(3);
                dbname1 = cursor.getString(2);
            }
            if(i==1) {
                dbtime2 = cursor.getInt(3);
                dbname2 = cursor.getString(2);
            }
            if(i==2) {
                dbtime3 = cursor.getInt(3);
                dbname3 = cursor.getString(2);
            }
            if(i==3) {
                dbtime4 = cursor.getInt(3);
                dbname4 = cursor.getString(2);
            }
            if(i==4) {
                dbtime5 = cursor.getInt(3);
                dbname5 = cursor.getString(2);
            }
            if(State.LOGIN.equals(cursor.getString(1))){
                rankmy = (i+1);
                dbtimemy = cursor.getInt(3);
            }
        }
        cursor.close();

        textViewmyrank.setText(rankmy + "위");
        h = (int)(dbtimemy /3600);
        m = (int)(dbtimemy - h*3600)/60;
        s = (int)(dbtimemy - h*3600- m*60);
        textViewmytime.setText(String.format("%d시간 %d분 %d초", h, m, s));

        if(!(dbname1.replace(" ", "").equals(""))){
            textView1stname.setText(dbname1);
            h = (int)(dbtime1 /3600);
            m = (int)(dbtime1 - h*3600)/60;
            s = (int)(dbtime1 - h*3600- m*60);
            textView1sttime.setText(String.format("%d시간 %d분 %d초", h, m, s));
        }
        if(!(dbname2.replace(" ", "").equals(""))){
            textView2ndname.setText(dbname2);
            h = (int)(dbtime2 /3600);
            m = (int)(dbtime2 - h*3600)/60;
            s = (int)(dbtime2 - h*3600- m*60);
            textView2ndtime.setText(String.format("%d시간 %d분 %d초", h, m, s));
        }
        if(!(dbname3.replace(" ", "").equals(""))){
            textView3rdname.setText(dbname3);
            h = (int)(dbtime3 /3600);
            m = (int)(dbtime3 - h*3600)/60;
            s = (int)(dbtime3 - h*3600- m*60);
            textView3rdtime.setText(String.format("%d시간 %d분 %d초", h, m, s));
        }
        if(!(dbname4.replace(" ", "").equals(""))){
            textView4thname.setText(dbname4);
            h = (int)(dbtime4 /3600);
            m = (int)(dbtime4 - h*3600)/60;
            s = (int)(dbtime4 - h*3600- m*60);
            textView4thtime.setText(String.format("%d시간 %d분 %d초", h, m, s));
        }
        if(!(dbname5.replace(" ", "").equals(""))){
            textView5thname.setText(dbname5);
            h = (int)(dbtime5 /3600);
            m = (int)(dbtime5 - h*3600)/60;
            s = (int)(dbtime5 - h*3600- m*60);
            textView5thtime.setText(String.format("%d시간 %d분 %d초", h, m, s));
        }

//        Cursor cursor2 = dbt.rawQuery(DBContractTime.SQL_SELECT_ID, new String[]{State.LOGIN});
//        for(int i=0; i<cursor2.getCount(); i++){
//            cursor.moveToNext();
//            dbnamemy=cursor2.getString(2);
//            dbtimemy=cursor2.getInt(3);
//        }
//        cursor2.close();



        Button buttonTimer = (Button) findViewById(R.id.button_Timer_r);
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
        if(ActivityRanking_m.activityRM!=null){
            ActivityRanking_m activity_rm = (ActivityRanking_m) ActivityRanking_m.activityRM;
            activity_rm.finish();
        }

        super.onBackPressed();
    }
}