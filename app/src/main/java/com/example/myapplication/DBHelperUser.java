package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

interface DBContractUser {
    static final String TABLE_NAME="USER";
    static final String COL_ID="ID";
    static final String COL_USERID="USERID";
    static final String COL_NAME="NAME";
    static final String COL_PW="PW";
    static final String COL_TIME="TIME";
    static final String COL_SIGNDATE="SIGNDATE";

    static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + "(" +
            COL_ID + " INTEGER NOT NULL PRIMARY KEY, " +
            COL_USERID + " TEXT NOT NULL, " +
            COL_NAME + " TEXT NOT NULL, " +
            COL_PW + " TEXT NOT NULL, " +
            COL_TIME + " INTEGER NOT NULL, " +
            COL_SIGNDATE + " TEXT NOT NULL)";

    static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    static final String SQL_SELECT_ID = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_USERID + "=?";
    static final String SQL_SELECT_PW = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_PW + "=?";
}

class DBHelperUser extends SQLiteOpenHelper {
    static final String DB_FILE = "user_t_focuson.db";
    static final int DB_VERSION = 2;

    DBHelperUser(Context context) {
        super(context, DB_FILE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContractUser.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContractUser.SQL_DROP_TABLE);
        onCreate(db);
    }
}