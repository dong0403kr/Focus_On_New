package com.example.myapplication;

import static com.example.myapplication.State.BTState;
import static com.example.myapplication.State.SATState;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
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

    private Chronometer chronometer;
    private Chronometer chronometer_b;

    private boolean running;
    private long pauseOffset;

    int i=0;
    int j=0;

    int h;
    int m;
    int s;

    int Htemp=0;
    int Mtemp=0;
    int Stemp=0;



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

        SATState.observe(this, new Observer<Integer>() { // 착석 상태 변수 변화 감지
            @Override
            public void onChanged(Integer sat) {
                Chronometer timeElapsed = (Chronometer) findViewById(R.id.chronometer_m);
                if(sat==1){
                    if(running) {
                        buttonTimer.setImageResource(R.drawable.button_marathon);
                        stateimageView.setImageResource(R.drawable.state_sat);
                    }
                    else {
                        buttonTimer.setImageResource(R.drawable.button_avail);
                        stateimageView.setImageResource(R.drawable.state_sat);
                    }
                }
                else if(sat==0 && State.BT==1){
                    if(running) {
                        if( ((Htemp*3600)+(Mtemp*60)+Stemp) <= ((h*3600)+(m*60)+s) ) {
                        textViewBest.setText(String.format("최고기록 : %02d:%02d:%02d", h, m, s));
                        }
                        else {
                        textViewBest.setText(String.format("최고기록 : %02d:%02d:%02d", Htemp, Mtemp, Stemp));
                        }

                        Htemp = h;
                        Mtemp = m;
                        Stemp = s;

                        timeElapsed.setBase(SystemClock.elapsedRealtime());
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

                if(((Htemp*3600)+(Mtemp*60)+Stemp) < ((h*3600)+(m*60)+s)){
                    textViewBest.setText("최고기록 갱신!");
                    textViewBest.setTextColor(getColor(R.color.new_record));
                }
            }
        });

        buttonTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(State.BT == 1 && State.SAT == 1) {
                    if (!running) {
                        timeElapsed.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                        buttonTimer.setImageResource(R.drawable.button_marathon);
                        timeElapsed.start();
                        timeElapsed.setTextColor(getColor(R.color.white));
                        blackscreenFadeIn(blackScreen);
                        running = true;
                    } else if (running) {
                        Toast.makeText(getApplicationContext(), "측정을 중단하려면 자리에서 일어나십시오.", Toast.LENGTH_SHORT).show();
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


        Button BasicBtn1 = findViewById(R.id.button_basic1);  // 측정 모드 변경
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

        Button buttonSettings = (Button) findViewById(R.id.button_Setting_m); // 바텀 네비게이션
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                Intent intent = new Intent(getApplicationContext(), ActivityProfile.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
            }
        });

        Button buttonRanking = (Button) findViewById(R.id.button_Ranking_m);
        buttonRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
//                    if( ((Htemp*3600)+(Mtemp*60)+Stemp) <= ((h*3600)+(m*60)+s) ) {
//                        textViewBest.setText(String.format("최고기록 : %02d:%02d:%02d", h, m, s));
//                    }
//                    else {
//                        textViewBest.setText(String.format("최고기록 : %02d:%02d:%02d", Htemp, Mtemp, Stemp));
//                    }
//
//                    Htemp = h;
//                    Mtemp = m;
//                    Stemp = s;
//
//                    timeElapsed.setBase(SystemClock.elapsedRealtime());
//                    pauseOffset = 0;
//                    timeElapsed.stop();
//                    imageView.setImageResource(R.drawable.marathon_sat);
//                    running = false;
//
//                }
//
//                i = 2 - i;
//
//                if(i==2){
//                    imageView.setImageResource(R.drawable.marathon);
//                    i=1;
//                    j=2;
//                } else if(i==1) {
//                    imageView.setImageResource(R.drawable.marathon_sat);
//                    i=0;
//                    j=1;
//                }
//            }
//        });
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
        if(ActivityProfile.activityP!=null){
            ActivityProfile activity_p = (ActivityProfile) ActivityProfile.activityP;
            activity_p.finish();
        }

        super.onBackPressed();
    }
}