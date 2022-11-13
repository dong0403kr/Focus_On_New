package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Marathon extends AppCompatActivity {

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
        TextView textViewBest = findViewById(R.id.textView_best);
        TextView textViewBT = findViewById(R.id.textViewBT);
        TextView textViewSAT = findViewById(R.id.textViewSAT);
        ImageView imageView = findViewById(R.id.imageView_marathon);

        textViewBT.setText("연결상태 : " + State.BT);
        textViewSAT.setText("착석상태 : " + State.SAT);

        Chronometer timeElapsed = (Chronometer) findViewById(R.id.chronometer);

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
            }
        });

        Button imageButton2 = (Button) findViewById(R.id.button_setting);
        imageButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivitySettings.class);
                startActivity(intent);
            }
        });

        Button timerBtn = findViewById(R.id.button_timer);
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(j==1) {
                    if (!running) {
                        timeElapsed.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                        imageView.setImageResource(R.drawable.marathon_start);
                        timeElapsed.start();
                        running = true;
                    } else if (running) {
                        Toast.makeText(getApplicationContext(), "측정을 중단하려면 자리에서 일어나십시오.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(State.SAT==0){
                    Toast.makeText(getApplicationContext(), "착석 해라", Toast.LENGTH_SHORT).show();
                }
                else if(State.SAT==1){
                    Toast.makeText(getApplicationContext(), "착석 완료", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button BasicBtn1 = findViewById(R.id.button_basic1);
        BasicBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityTimer.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        Button BasicBtn2 = findViewById(R.id.button_basic2);
        BasicBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityTimer.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        Button imageButton3 = (Button) findViewById(R.id.button_change);
        imageButton3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(running){
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
                    imageView.setImageResource(R.drawable.marathon_sat);
                    running = false;

                }

                i = 2 - i;

                if(i==2){
                    imageView.setImageResource(R.drawable.marathon);
                    i=1;
                    j=2;
                } else if(i==1) {
                    imageView.setImageResource(R.drawable.marathon_sat);
                    i=0;
                    j=1;
                }
            }
        });
    }
}