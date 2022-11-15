package com.example.myapplication;

import androidx.lifecycle.MutableLiveData;

public class State {
    public static MutableLiveData<Integer> BTState = new MutableLiveData<>();
    public static MutableLiveData<Integer> SATState = new MutableLiveData<>();
    public static int BT = 0;
    public static int SAT = 0;
    public static String LOGIN = "";
    public static int AutoLogin = 0;
    public static int TIMER = 0;
    public static int AutoStart = 0;
}
