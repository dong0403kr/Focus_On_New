package com.example.myapplication;

import static com.example.myapplication.DBContractTime.TABLE_NAME2;
import static com.example.myapplication.DBContractUser.TABLE_NAME;
import static com.example.myapplication.State.BTState;
import static com.example.myapplication.State.SATState;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;



public class ActivityTimer extends AppCompatActivity {

    public static ActivityTimer activityT = null;

    DBHelperTime dbHelperTime;
    SQLiteDatabase dbt;
    DBHelperUser dbHelperUser;
    SQLiteDatabase db;

    private boolean running;
    private long pauseOffset;

    int dailytime=0;
    int dbtime; // 전체 누적시간. 유저db
    int d_dbtime;  // 일간 공부시간. 타임db

    private int firstStart=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        activityT = this;

        BTState.setValue(0);
        SATState.setValue(0);

        ImageView blackScreen = findViewById(R.id.black_screen);
        ImageView stateimageView = findViewById(R.id.imageView_state);
        stateimageView.setImageResource(R.drawable.state_discon);
        ImageButton buttonTimer = findViewById(R.id.timerbutton);
        buttonTimer.setImageResource(R.drawable.button_unavail);

        dbHelperUser = new DBHelperUser(this);
        dbHelperTime = new DBHelperTime(this);

        dbt = dbHelperTime.getReadableDatabase();
        Cursor cursor = dbt.rawQuery(DBContractTime.SQL_SELECT_ID, new String[]{State.LOGIN});

        for(int i=0; i<cursor.getCount(); i++){  // 타임db에서 기존 일간 공부시간 가져옴
            cursor.moveToNext();
            d_dbtime=cursor.getInt(3);
        }
        cursor.close();

        db = dbHelperUser.getReadableDatabase();
        Cursor cursor2 = db.rawQuery(DBContractUser.SQL_SELECT_ID, new String[]{State.LOGIN});

        for(int i=0; i<cursor2.getCount(); i++){  // 유저db에서 누적 공부시간 가져옴
            cursor2.moveToNext();
            dbtime=cursor2.getInt(4);
        }
        cursor2.close();


        BTState.observe(this, new Observer<Integer>() { // 블루투스 연결 상태 변수 변화 감지
            @Override
            public void onChanged(Integer bt) {
                if(bt==1) {
                    stateimageView.setImageResource(R.drawable.state_connected);
                }
                else if(bt==0) {
                    stateimageView.setImageResource(R.drawable.state_discon);
                }
            }
        });

        SATState.observe(this, new Observer<Integer>() { // 착석 상태 변수 변화 감지
            @Override
            public void onChanged(Integer sat) {
                Chronometer timeElapsed = (Chronometer) findViewById(R.id.chronometer);
                if(sat==1){
                    if(running) {
                        buttonTimer.setImageResource(R.drawable.button_running);
                        stateimageView.setImageResource(R.drawable.state_sat);
                    }
                    else {
                        if (State.AutoStart == 1 && firstStart == 1) {
                            timeElapsed.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                            timeElapsed.start();
                            timeElapsed.setTextColor(getColor(R.color.white));
                            blackscreenFadeIn(blackScreen);
                            buttonTimer.setImageResource(R.drawable.button_running);
                            running = true;
                        } else {
                            buttonTimer.setImageResource(R.drawable.button_avail);
                            stateimageView.setImageResource(R.drawable.state_sat);
                        }
                    }
                }
                else if(sat==0 && State.BT==1){
                    if(running) {
                        timeElapsed.stop();
                        pauseOffset = SystemClock.elapsedRealtime() - timeElapsed.getBase();
                        stateimageView.setImageResource(R.drawable.state_connected);
                        timeElapsed.setTextColor(getColor(R.color.black));
                        blackscreenFadeOut(blackScreen);
                        running = false;
                    }
                    stateimageView.setImageResource(R.drawable.state_connected);
                    buttonTimer.setImageResource(R.drawable.button_unavail);
                }
            }
        });

        Chronometer timeElapsed = (Chronometer) findViewById(R.id.chronometer);


        db = dbHelperUser.getReadableDatabase();
        dbt = dbHelperTime.getReadableDatabase();
        timeElapsed.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                /*String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";*/
                cArg.setText(String.format("%02d:%02d:%02d", h, m, s ));
                dailytime=(int)(time/1000);
            }
        });




        buttonTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (State.BT == 1 && State.SAT == 1) {
                    if (!running) {
                        if(firstStart==0){
                            firstStart=1;
                        }
                        timeElapsed.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                        timeElapsed.start();
                        timeElapsed.setTextColor(getColor(R.color.white));
                        blackscreenFadeIn(blackScreen);
                        buttonTimer.setImageResource(R.drawable.button_running);
                        running = true;
                    } else if (running) {
                        timeElapsed.stop();
                        pauseOffset = SystemClock.elapsedRealtime() - timeElapsed.getBase();
                        timeElapsed.setTextColor(getColor(R.color.black));
                        blackscreenFadeOut(blackScreen);
                        buttonTimer.setImageResource(R.drawable.button_avail);
                        running = false;
                    }
                }
                else if(State.BT==0){
                    Toast.makeText(getApplicationContext(), "블루투스를 연결해 주세요", Toast.LENGTH_SHORT).show();

                }
                else if(State.BT==1 && State.SAT==0){
                    Toast.makeText(getApplicationContext(), "자리에 착석해야 측정을 시작할 수 있습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button MarathonBtn1 = findViewById(R.id.button_marat1);     // 측정 모드 변경
        MarathonBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                State.TIMER = 1;
                Intent intent = new Intent(getApplicationContext(), ActivityMarathon.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        Button MarathonBtn2 = findViewById(R.id.button_marat2);
        MarathonBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                State.TIMER = 1;
                Intent intent = new Intent(getApplicationContext(), ActivityMarathon.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });



        Button buttonSettings = (Button) findViewById(R.id.button_Setting);     // 바텀 네비게이션
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int valuetime = dailytime+d_dbtime;
                int valuesumtime = dailytime+dbtime;
                ContentValues valuesT = new ContentValues();
                dbt = dbHelperTime.getWritableDatabase();
                valuesT.put("d_time", valuetime); // 일간 공부시간 타임db에 업데이트
                dbt.update(TABLE_NAME2,valuesT,"USERID = ?",new String[]{State.LOGIN});

                ContentValues valuesU = new ContentValues();
                db = dbHelperUser.getWritableDatabase();
                valuesU.put("time", valuesumtime); // 누적시간 유저db에 업데이트
                db.update(TABLE_NAME,valuesU,"USERID = ?", new String[]{State.LOGIN});

                Intent intent = new Intent(getApplicationContext(), ActivitySettings.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
            }
        });

        Button buttonProfile = (Button) findViewById(R.id.button_Profile);
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int valuetime = dailytime+d_dbtime;
                int valuesumtime = dailytime+dbtime;
                ContentValues valuesT = new ContentValues();
                dbt = dbHelperTime.getWritableDatabase();
                valuesT.put("d_time", valuetime); // 일간 공부시간 타임db에 업데이트
                dbt.update(TABLE_NAME2,valuesT,"USERID = ?",new String[]{State.LOGIN});

                ContentValues valuesU = new ContentValues();
                db = dbHelperUser.getWritableDatabase();
                valuesU.put("time", valuesumtime); // 누적시간 유저db에 업데이트
                db.update(TABLE_NAME,valuesU,"USERID = ?", new String[]{State.LOGIN});

                Intent intent = new Intent(getApplicationContext(), ActivityProfile.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
            }
        });

        Button buttonRanking = (Button) findViewById(R.id.button_Ranking);
        buttonRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int valuetime = dailytime+d_dbtime;
                int valuesumtime = dailytime+dbtime;
                ContentValues valuesT = new ContentValues();
                dbt = dbHelperTime.getWritableDatabase();
                valuesT.put("d_time", valuetime); // 일간 공부시간 타임db에 업데이트
                dbt.update(TABLE_NAME2,valuesT,"USERID = ?",new String[]{State.LOGIN});

                ContentValues valuesU = new ContentValues();
                db = dbHelperUser.getWritableDatabase();
                valuesU.put("time", valuesumtime); // 누적시간 유저db에 업데이트
                db.update(TABLE_NAME,valuesU,"USERID = ?", new String[]{State.LOGIN});

                Intent intent = new Intent(getApplicationContext(), ActivityRanking.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
            }
        });


//        Button imageButton3 = (Button) findViewById(R.id.button_change);
//        imageButton3.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                if(running){
//                    timeElapsed.stop();
//                    pauseOffset = SystemClock.elapsedRealtime() - timeElapsed.getBase();
//                    imageView.setImageResource(R.drawable.basic_sat);
//                    running = false;
//                }
//
//                satst = 3 - satst;
//
//                if(satst==3){
//                    imageView.setImageResource(R.drawable.basic);
//                    satst=1;
//                    connected=2;
//                } else if(satst==2) {
//                    imageView.setImageResource(R.drawable.basic_sat);
//                    satst=3;
//                    connected=1;
//                } else if(satst==0) {
//                    imageView.setImageResource(R.drawable.basic_discon);
//                    satst=0;
//                    connected=0;
//                }
//            }
//        });
    } // onCreate

    public void blackscreenFadeIn(View view) {
        ValueAnimator alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1.0f);
        alphaAnimator.setDuration(500);
        alphaAnimator.start();
    }

    public void blackscreenFadeOut(View view) {
        ValueAnimator alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0f);
        alphaAnimator.setDuration(500);
        alphaAnimator.start();
    }

//    public void onStart() {
//        super.onStart();
//        if (!bt.isBluetoothEnabled()) { //
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
//        } else {
//            if (!bt.isServiceAvailable()) {
//                bt.setupService();
//                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
//                setup();
//            }
//        }
//    }
//
//    public void setup() {
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
//            if (resultCode == Activity.RESULT_OK)
//                bt.connect(data);
//        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
//            if (resultCode == Activity.RESULT_OK) {
//                bt.setupService();
//                bt.startService(BluetoothState.DEVICE_OTHER);
//                setup();
//            } else {
//                Toast.makeText(getApplicationContext()
//                        , "Bluetooth was not enabled."
//                        , Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//    }


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