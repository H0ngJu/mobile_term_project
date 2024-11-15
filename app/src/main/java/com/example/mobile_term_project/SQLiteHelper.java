package com.example.mobile_term_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MobileProgramming.db";

    public static final String MEMBER_SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TableInfo.MemberEntry.TABLE_NAME + " (" +
                    TableInfo.MemberEntry._ID + " INTEGER PRIMARY KEY," +
                    TableInfo.MemberEntry.COLUMN_NAME_NICKNAME + " TEXT," +
                    TableInfo.MemberEntry.COLUMN_NAME_PASSWORD + " TEXT)";

    public static final String MEMBER_SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TableInfo.MemberEntry.TABLE_NAME;

    public static final String STEPS_SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TableInfo.StepRecordEntry.TABLE_NAME + " (" +
                    TableInfo.StepRecordEntry._ID + " INTEGER PRIMARY KEY," +
                    TableInfo.StepRecordEntry.COLUMN_NAME_COUNT + " TEXT," +
                    TableInfo.StepRecordEntry.COLUMN_NAME_DISTANCE + " TEXT," +
                    TableInfo.StepRecordEntry.COLUMN_NAME_MEMBER_ID + " INTEGER," +
                    "FOREIGN KEY(" + TableInfo.StepRecordEntry.COLUMN_NAME_MEMBER_ID + ") REFERENCES " +
                    TableInfo.MemberEntry.TABLE_NAME + "(" + TableInfo.MemberEntry._ID + "))";

    public static final String STEP_SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TableInfo.StepRecordEntry.TABLE_NAME;

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MEMBER_SQL_CREATE_ENTRIES);
        db.execSQL(STEPS_SQL_CREATE_ENTRIES);

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); //외래키 제약조건 활성화
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MEMBER_SQL_DELETE_ENTRIES);
        onCreate(db);

        db.execSQL(STEP_SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
