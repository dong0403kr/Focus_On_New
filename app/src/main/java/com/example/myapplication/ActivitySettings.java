package com.example.myapplication;

import static com.example.myapplication.DBContractUser.TABLE_NAME;
import static com.example.myapplication.DBContractTime.TABLE_NAME2;
import static com.example.myapplication.State.BTState;
import static com.example.myapplication.State.SATState;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class ActivitySettings extends AppCompatActivity {

    DBHelperUser dbHelperUser;
    SQLiteDatabase db;
    DBHelperTime dbHelperTime;
    SQLiteDatabase dbt;

    public static ActivitySettings activityS = null;

    private BluetoothSPP bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        activityS = this;

        bt = new BluetoothSPP(this);

        dbHelperUser = new DBHelperUser(this);
        dbHelperTime = new DBHelperTime(this);

        Switch switchAS = findViewById(R.id.switchAutoStart);
        Button btnConnect = findViewById(R.id.BTbutton);
        Button buttonL = findViewById(R.id.button_leave);
        buttonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = dbHelperUser.getWritableDatabase();
                db.delete(DBContractUser.TABLE_NAME, "USERID=?", new String[]{State.LOGIN});
                dbt = dbHelperTime.getWritableDatabase();
                dbt.delete(DBContractTime.TABLE_NAME2, "USERID=?", new String[]{State.LOGIN});
                SharedPreferencesManager.clearPreferences(getApplicationContext());
                Intent intentL = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intentL);
                Toast.makeText(getApplicationContext(), "????????? ?????????????????????", Toast.LENGTH_SHORT).show();
                finish();
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
                if(ActivityRanking_m.activityRM!=null){
                    ActivityRanking_m activity_rm = (ActivityRanking_m) ActivityRanking_m.activityRM;
                    activity_rm.finish();
                }
            }
        });


        Button buttonLO = findViewById(R.id.button_logout);
        buttonLO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.clearPreferences(getApplicationContext());
                Intent intentL = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intentL);
                finish();
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
                if(ActivityRanking_m.activityRM!=null){
                    ActivityRanking_m activity_rm = (ActivityRanking_m) ActivityRanking_m.activityRM;
                    activity_rm.finish();
                }
            }
        });

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

        if (!bt.isBluetoothAvailable()) { //???????????? ?????? ???????????? ?????????????????? ????????? ?????????
            Toast.makeText(getApplicationContext()
                    , "??????????????? ????????? ??? ????????????"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        // ???????????? ???????????? ???????????? ?????????
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            //????????? ????????????
            public void onDataReceived(byte[] data, String message) {
                if(message.equals("1")){
                    State.SAT=1;
                    SATState.setValue(1);
                }
                else
                {
                    State.SAT=0;
                    SATState.setValue(0);
                }
            }
        });

        // ??????????????? ??? ????????? ???????????? ???????????? ?????????
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //???????????? ???
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "?????? ???????????? ?????? ??????!"
                        , Toast.LENGTH_SHORT).show();
                State.BT=1;
                BTState.setValue(1);
                btnConnect.setText("????????????");
            }
            public void onDeviceDisconnected() { //????????????
                Toast.makeText(getApplicationContext()
                        , "???????????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show();
                State.BT=0;
                BTState.setValue(0);
                btnConnect.setText("????????????");
            }
            public void onDeviceConnectionFailed() { //????????????
                Toast.makeText(getApplicationContext()
                        , "???????????? ????????? ??????????????????", Toast.LENGTH_SHORT).show();
                State.BT=0;
                BTState.setValue(0);
            }
        });


        btnConnect.setOnClickListener(new View.OnClickListener() { //????????????
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

        switchAS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    State.AutoStart=1;
                } else {
                    State.AutoStart=0;
                }
            }
        });

        Button buttonTimer = (Button) findViewById(R.id.button_Timer_s); // ?????? ???????????????
        buttonTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(State.TIMER==0) {
                    Intent intent = new Intent(getApplicationContext(), ActivityTimer.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), ActivityMarathon.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
        });

        Button buttonProfile = (Button) findViewById(R.id.button_Profile_s);
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityProfile.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
            }
        });

        Button buttonRanking = (Button) findViewById(R.id.button_Ranking_s);
        buttonRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityRanking.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
            }
        });

        Button buttonLogout = (Button) findViewById(R.id.button_logout); // ????????????
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if(ActivityProfile.activityP!=null){
                    ActivityProfile activity_p = (ActivityProfile) ActivityProfile.activityP;
                    activity_p.finish();
                }
                if(ActivityLogin.activityL!=null){
                    ActivityLogin activity_l = (ActivityLogin) ActivityLogin.activityL;
                    activity_l.finish();
                }
                State.AutoLogin=0;
                SharedPreferencesManager.clearPreferences(getApplicationContext());
                Intent intentL = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intentL);
                finish();
            }
        });

    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID??? ??????????????? ?????? ??????
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
//        if(ActivitySettings.activityS!=null){
//            ActivitySettings activity_s = (ActivitySettings) ActivitySettings.activityS;
//            activity_s.finish();
//        }
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
        if(ActivityRanking_m.activityRM!=null){
            ActivityRanking_m activity_rm = (ActivityRanking_m) ActivityRanking_m.activityRM;
            activity_rm.finish();
        }

        super.onBackPressed();
    }
}