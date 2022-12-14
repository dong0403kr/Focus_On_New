package com.example.myapplication;

import static com.example.myapplication.DBContractTime.TABLE_NAME2;
import static com.example.myapplication.DBContractUser.TABLE_NAME;
import static com.example.myapplication.State.BTState;
import static com.example.myapplication.State.SATState;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMarathon extends AppCompatActivity {

    public static ActivityMarathon activityM = null;

    DBHelperUser dbHelperUser;
    SQLiteDatabase db;
    DBHelperTime dbHelperTime;
    SQLiteDatabase dbt;

    private Chronometer chronometer;
    private Chronometer chronometer_b;

    private boolean running;
    private long pauseOffset;

    int i=0;
    int j=0;

    int h;
    int m;
    int s;

//    int Htemp=0;
//    int Mtemp=0;
//    int Stemp=0;

    int besttime=0;
    int besttime_db;
    int sumtime_db;
    int sumtime=0;
    int sumtime_now=0;

    int dbh,dbm,dbs;
    private int firstStart=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marathon);

        activityM = this;

        TextView textViewBest = findViewById(R.id.textView_best);

        ImageView blackScreen = findViewById(R.id.black_screen_m);
        ImageView stateimageView = findViewById(R.id.imageView_state_m);
        ImageButton buttonTimer = findViewById(R.id.timerbutton_m);

        dbHelperUser = new DBHelperUser(this);
        dbHelperTime = new DBHelperTime(this);

        dbt = dbHelperTime.getReadableDatabase();
        Cursor cursor = dbt.rawQuery(DBContractTime.SQL_SELECT_ID, new String[]{State.LOGIN});
        for(int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            besttime_db=cursor.getInt(4);
        }
        cursor.close();
        dbh = (int)(besttime_db /3600);
        dbm = (int)(besttime_db - dbh*3600)/60;
        dbs = (int)(besttime_db - dbh*3600- dbm*60) ;
        textViewBest.setText(String.format("???????????? : %02d:%02d:%02d", dbh, dbm, dbs));

        db = dbHelperUser.getReadableDatabase();
        Cursor cursor2 = db.rawQuery(DBContractUser.SQL_SELECT_ID, new String[]{State.LOGIN});
        for(int i=0; i<cursor2.getCount(); i++){  // ??????db?????? ?????? ???????????? ?????????
            cursor2.moveToNext();
            sumtime_db=cursor2.getInt(4);
        }
        cursor2.close();

        BTState.observe(this, new Observer<Integer>() {
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

        SATState.observe(this, new Observer<Integer>() { // ?????? ?????? ?????? ?????? ??????
            @Override
            public void onChanged(Integer sat) {
                Chronometer timeElapsed = (Chronometer) findViewById(R.id.chronometer_m);
                if(sat==1){
                    if(running) {
                        buttonTimer.setImageResource(R.drawable.button_marathon);
                        stateimageView.setImageResource(R.drawable.state_sat);
                    }
                    else {
                        if (State.AutoStart == 1 && firstStart == 1) {
                            timeElapsed.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                            buttonTimer.setImageResource(R.drawable.button_marathon);
                            timeElapsed.start();
                            timeElapsed.setTextColor(getColor(R.color.white));
                            blackscreenFadeIn(blackScreen);
                            running = true;
                        } else{
                        buttonTimer.setImageResource(R.drawable.button_avail);
                        stateimageView.setImageResource(R.drawable.state_sat);
                        }
                    }
                }
                else if(sat==0 && State.BT==1){
                    if(running) {
                        sumtime+=sumtime_now;
                        sumtime_now=0;
                        if( ((dbh*3600)+(dbm*60)+dbs) <= ((h*3600)+(m*60)+s) ) {
                        textViewBest.setText(String.format("???????????? : %02d:%02d:%02d", h, m, s));
                        }
                        else {
                        textViewBest.setText(String.format("???????????? : %02d:%02d:%02d", dbh, dbm, dbs));
                        }

//                        timeElapsed.setBase(SystemClock.elapsedRealtime());
                        pauseOffset = 0;
                        timeElapsed.stop();
                        stateimageView.setImageResource(R.drawable.state_connected);
                        timeElapsed.setTextColor(getColor(R.color.black));
                        textViewBest.setTextColor(getColor(R.color.record));
                        blackscreenFadeOut(blackScreen);
                        running = false;
                    }
                    stateimageView.setImageResource(R.drawable.state_connected);
                    buttonTimer.setImageResource(R.drawable.button_unavail);
                }
            }
        });

        Chronometer timeElapsed = (Chronometer) findViewById(R.id.chronometer_m);

        timeElapsed.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                h   = (int)(time /3600000);
                m = (int)(time - h*3600000)/60000;
                s= (int)(time - h*3600000- m*60000)/1000 ;
                /*String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";*/
                cArg.setText(String.format("%02d:%02d:%02d", h, m, s ));
                sumtime_now=(int)(time/1000);
                if(((dbh*3600)+(dbm*60)+dbs) < ((h*3600)+(m*60)+s)){
                    besttime=(int)(time/1000);
                    textViewBest.setText("???????????? ??????!");
                    textViewBest.setTextColor(getColor(R.color.new_record));
                }
            }
        });

        buttonTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(State.BT == 1 && State.SAT == 1) {
                    if (!running) {
                        if(firstStart==0){
                            firstStart=1;
                        }
                        timeElapsed.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                        buttonTimer.setImageResource(R.drawable.button_marathon);
                        timeElapsed.start();
                        timeElapsed.setTextColor(getColor(R.color.white));
                        blackscreenFadeIn(blackScreen);
                        running = true;
                    } else if (running) {
                        Toast.makeText(getApplicationContext(), "????????? ??????????????? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(State.BT==0){
                    Toast.makeText(getApplicationContext(), "??????????????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                }
                else if(State.BT==1 && State.SAT==0){
                    Toast.makeText(getApplicationContext(), "????????? ???????????? ????????? ????????? ??? ????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button BasicBtn1 = findViewById(R.id.button_basic1);  // ?????? ?????? ??????
        BasicBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                State.TIMER = 0;
                Intent intent = new Intent(getApplicationContext(), ActivityTimer.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        Button BasicBtn2 = findViewById(R.id.button_basic2);
        BasicBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                State.TIMER = 0;
                Intent intent = new Intent(getApplicationContext(), ActivityTimer.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        Button buttonSettings = (Button) findViewById(R.id.button_Setting_m); // ?????? ???????????????
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(besttime_db < besttime) {
                    ContentValues valuesT = new ContentValues();
                    dbt = dbHelperTime.getWritableDatabase();
                    valuesT.put("best", besttime); // ????????? ???????????? ??????db??? ????????????
                    dbt.update(TABLE_NAME2, valuesT, "USERID = ?", new String[]{State.LOGIN});
                }
                int valuesumtime = sumtime + sumtime_db;
                ContentValues valuesU = new ContentValues();
                db = dbHelperUser.getWritableDatabase();
                valuesU.put("time", valuesumtime); // ???????????? ??????db??? ????????????
                db.update(TABLE_NAME,valuesU,"USERID = ?", new String[]{State.LOGIN});

                Intent intent = new Intent(getApplicationContext(), ActivitySettings.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
            }
        });

        Button buttonProfile = (Button) findViewById(R.id.button_Profile_m);
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(besttime_db < besttime) {
                    ContentValues valuesT = new ContentValues();
                    dbt = dbHelperTime.getWritableDatabase();
                    valuesT.put("best", besttime); // ????????? ???????????? ??????db??? ????????????
                    dbt.update(TABLE_NAME2, valuesT, "USERID = ?", new String[]{State.LOGIN});
                }
                int valuesumtime = sumtime + sumtime_db;
                ContentValues valuesU = new ContentValues();
                db = dbHelperUser.getWritableDatabase();
                valuesU.put("time", valuesumtime); // ???????????? ??????db??? ????????????
                db.update(TABLE_NAME,valuesU,"USERID = ?", new String[]{State.LOGIN});

                Intent intent = new Intent(getApplicationContext(), ActivityProfile.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
            }
        });

        Button buttonRanking = (Button) findViewById(R.id.button_Ranking_m);
        buttonRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(besttime_db < besttime) {
                    ContentValues valuesT = new ContentValues();
                    dbt = dbHelperTime.getWritableDatabase();
                    valuesT.put("best", besttime); // ????????? ???????????? ??????db??? ????????????
                    dbt.update(TABLE_NAME2, valuesT, "USERID = ?", new String[]{State.LOGIN});
                }
                int valuesumtime = sumtime + sumtime_db;
                ContentValues valuesU = new ContentValues();
                db = dbHelperUser.getWritableDatabase();
                valuesU.put("time", valuesumtime); // ???????????? ??????db??? ????????????
                db.update(TABLE_NAME,valuesU,"USERID = ?", new String[]{State.LOGIN});

                Intent intent = new Intent(getApplicationContext(), ActivityRanking.class);
                startActivity(intent);
            }
        });

    }

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

    @Override
    public void onBackPressed() {   // ???????????? ????????? ??????????????? ??????
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("?????? ?????????????????????????"); // ??????????????? ??????
        builder.setCancelable(true);   // ??????????????? ?????? ??? ?????? ??????
        builder.setPositiveButton("???", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                exit();
            }
        });

        builder.setNegativeButton("?????????", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show(); // ??????????????? ?????????
    }
    public void exit() { // ??????
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