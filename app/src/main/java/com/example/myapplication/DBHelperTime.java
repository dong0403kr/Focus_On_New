package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

interface DBContractTime {
    static final String TABLE_NAME2="TIME";
    static final String COL_ID="ID";
    static final String COL_USERID="USERID";
    static final String COL_NAME="NAME";
    static final String COL_D_TIME="D_TIME";
    static final String COL_BEST="BEST";

    static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME2 + "(" +
            COL_ID + " INTEGER NOT NULL PRIMARY KEY, " +
            COL_USERID + " TEXT NOT NULL, " +
            COL_NAME + " TEXT NOT NULL, " +
            COL_D_TIME + " INTEGER NOT NULL, " +
            COL_BEST + " INTEGER NOT NULL)";

    static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME2;
    static final String SQL_SELECT_ID = "SELECT * FROM " + TABLE_NAME2 + " WHERE " + COL_USERID + "=?";
}

class DBHelperTime extends SQLiteOpenHelper {
    static final String DB_FILE = "time_t_focuson.db";
    static final int DB_VERSION = 4;

    DBHelperTime(Context context) {
        super(context, DB_FILE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContractTime.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContractTime.SQL_DROP_TABLE);
        onCreate(db);
    }
}