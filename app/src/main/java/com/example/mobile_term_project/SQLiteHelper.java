package com.example.mobile_term_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

    //닉네임 중복 확인
    public boolean checkNickname(String inputNickname) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists = false;

        String query = "SELECT 1 FROM " + TableInfo.MemberEntry.TABLE_NAME + " WHERE " + TableInfo.MemberEntry.COLUMN_NAME_NICKNAME + " = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{inputNickname})) {
            if(cursor.moveToFirst()) {
                exists = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.close();
        }
        return exists;
    }

    //db에 member 추가
    public long addMember (String nickname, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(TableInfo.MemberEntry.COLUMN_NAME_NICKNAME, nickname);
        values.put(TableInfo.MemberEntry.COLUMN_NAME_PASSWORD, password);

        long result = db.insert(TableInfo.MemberEntry.TABLE_NAME, null, values);
        db.close();

        return result;
    }
}
