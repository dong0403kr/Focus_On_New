package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;



public class ActivityTimer extends AppCompatActivity {

    public static ActivityTimer activityT = null;

    private BluetoothSPP bt;

    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;


    TextView textViewVal;
    TextView textViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        activityT = this;
        State.TIMER = 0;

        bt = new BluetoothSPP(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT


                    },
                    1);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.BLUETOOTH

                    },
                    1);
        }

        ImageView imageView = findViewById(R.id.imageView_bg);
        Chronometer timeElapsed = (Chronometer) findViewById(R.id.chronometer);

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가라면 사용불가라고 토스트 띄워줌
            Toast.makeText(getApplicationContext()
                    , "블루투스를 사용할 수 없습니다"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        // 데이터를 받았는지 감지하는 리스너
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            //데이터 수신되면
            public void onDataReceived(byte[] data, String message) {
                if(message.equals("1")){
                    State.SAT=1;
                    if(running) {
                        imageView.setImageResource(R.drawable.basic_start);
                    }
                    else {
                        imageView.setImageResource(R.drawable.basic_sat);
                    }
                }
                else
                {
                    if(running) {
                        timeElapsed.stop();
                        pauseOffset = SystemClock.elapsedRealtime() - timeElapsed.getBase();
                        imageView.setImageResource(R.drawable.basic_sat);
                        running = false;
                    }
                    State.SAT=0;
                    imageView.setImageResource(R.drawable.basic);
                }
            }
        });

        // 블루투스가 잘 연결이 되었는지 감지하는 리스너
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "센서 블루투스 연결 성공"
                        , Toast.LENGTH_SHORT).show();
                State.BT=1;
                imageView.setImageResource(R.drawable.basic);
            }
            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "블루투스 연결이 해제되었습니다", Toast.LENGTH_SHORT).show();
                State.BT=0;
                imageView.setImageResource(R.drawable.basic_discon);
            }
            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "블루투스 연결에 실패했습니다", Toast.LENGTH_SHORT).show();
                State.BT=0;
                imageView.setImageResource(R.drawable.basic_discon);
            }
        });

        Button btnConnect = findViewById(R.id.button_bluetooth); //연결시도
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

        /*chronometer = findViewById(R.id.chronometer);*/
        /*chronometer.setFormat("시간: %s");*/


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
            }
        });



        Button timerBtn = findViewById(R.id.button_timer);
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (State.BT == 1 && State.SAT == 1) {
                    if (!running) {
                        timeElapsed.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                        imageView.setImageResource(R.drawable.basic_start);
                        timeElapsed.start();
                        running = true;
                    } else if (running) {
                        timeElapsed.stop();
                        pauseOffset = SystemClock.elapsedRealtime() - timeElapsed.getBase();
                        imageView.setImageResource(R.drawable.basic_sat);
                        running = false;
                    }
                }
                else if(State.BT==0){
                    Toast.makeText(getApplicationContext(), "센서를 연결해 주세요", Toast.LENGTH_SHORT).show();

                }
                else if(State.BT==1 && State.SAT==0){
                    Toast.makeText(getApplicationContext(), "자리에 착석해야 측정을 시작할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button MarathonBtn1 = findViewById(R.id.button_marat1);
        MarathonBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent intent = new Intent(getApplicationContext(), ActivityMarathon.class);
                intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });



        Button buttonSettings = (Button) findViewById(R.id.button_Setting);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                Intent intent = new Intent(getApplicationContext(), ActivityProfile.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
            }
        });

        Button buttonRanking = (Button) findViewById(R.id.button_Ranking);
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
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    public void setup() {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
//        if(ActivityTimer.activityT!=null){
//            ActivityTimer activity_t = (ActivityTimer) ActivityTimer.activityT;
//            activity_t.finish();
//        }

        super.onBackPressed();
    }
}