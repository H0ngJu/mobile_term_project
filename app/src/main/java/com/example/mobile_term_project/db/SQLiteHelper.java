package com.example.mobile_term_project.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.example.mobile_term_project.RankingItemModel;
import com.example.mobile_term_project.Record;
import com.example.mobile_term_project.StepDataStoreModel;
import com.example.mobile_term_project.db.TableInfo;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 5;
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
                    TableInfo.StepRecordEntry.COLUMN_NAME_COUNT + " INTEGER," +
                    TableInfo.StepRecordEntry.COLUMN_NAME_DISTANCE + " TEXT," +
                    TableInfo.StepRecordEntry.COLUMN_NAME_START_TIME + " TEXT," +
                    TableInfo.StepRecordEntry.COLUMN_NAME_END_TIME + " TEXT," +
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
        db.execSQL(STEP_SQL_DELETE_ENTRIES);
        db.execSQL(MEMBER_SQL_DELETE_ENTRIES);
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

    //로그인
    public Cursor login (String nickname, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TableInfo.MemberEntry.TABLE_NAME +
                " WHERE " + TableInfo.MemberEntry.COLUMN_NAME_NICKNAME + " = ? AND " +
                TableInfo.MemberEntry.COLUMN_NAME_PASSWORD + " = ?";

        return db.rawQuery(query, new String[]{nickname, password});
    }

    //db에 걸음 수 데이터 추가
    public void addStepData () {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(TableInfo.StepRecordEntry.COLUMN_NAME_MEMBER_ID, StepDataStoreModel.getMemberId());
        values.put(TableInfo.StepRecordEntry.COLUMN_NAME_COUNT, StepDataStoreModel.getStepCount());
        values.put(TableInfo.StepRecordEntry.COLUMN_NAME_DISTANCE, StepDataStoreModel.getDistance());
        values.put(TableInfo.StepRecordEntry.COLUMN_NAME_START_TIME, StepDataStoreModel.getStartTime());
        values.put(TableInfo.StepRecordEntry.COLUMN_NAME_END_TIME,StepDataStoreModel.getEndTime());

        db.insert(TableInfo.StepRecordEntry.TABLE_NAME, null, values);
        db.close();
    }

    //memberId에 해당하는 데이터 조회
    public List<Record> getRecordsByMemberId (int memberId) {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " +
                TableInfo.StepRecordEntry.COLUMN_NAME_START_TIME + ", " +
                TableInfo.StepRecordEntry.COLUMN_NAME_END_TIME + ", " +
                TableInfo.StepRecordEntry.COLUMN_NAME_DISTANCE + ", " +
                TableInfo.StepRecordEntry.COLUMN_NAME_COUNT +
                " FROM " + TableInfo.StepRecordEntry.TABLE_NAME +
                " WHERE " + TableInfo.StepRecordEntry.COLUMN_NAME_MEMBER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(memberId)});

        if(cursor != null && cursor.moveToFirst()){
            do {
                int startTimeIndex = cursor.getColumnIndex(TableInfo.StepRecordEntry.COLUMN_NAME_START_TIME);
                int endTimeIndex = cursor.getColumnIndex(TableInfo.StepRecordEntry.COLUMN_NAME_END_TIME);
                int distanceIndex = cursor.getColumnIndex(TableInfo.StepRecordEntry.COLUMN_NAME_DISTANCE);
                int stepsIndex = cursor.getColumnIndex(TableInfo.StepRecordEntry.COLUMN_NAME_COUNT);

                String startTime = cursor.getString(startTimeIndex);
                String endTime = cursor.getString(endTimeIndex);
                String distance = cursor.getString(distanceIndex);
                int steps = cursor.getInt(stepsIndex);

                records.add(new Record(startTime, endTime, distance,steps));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    //랭킹에 쓸 데이터
    public List<RankingItemModel> getAllUsersTopSteps() {
        List<RankingItemModel> rankingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " +
                TableInfo.MemberEntry.TABLE_NAME + "." + TableInfo.MemberEntry.COLUMN_NAME_NICKNAME + " AS nickname, " +
                "MAX(" + TableInfo.StepRecordEntry.TABLE_NAME + "." + TableInfo.StepRecordEntry.COLUMN_NAME_COUNT + ") AS max_steps " +
                "FROM " + TableInfo.StepRecordEntry.TABLE_NAME +
                " INNER JOIN " + TableInfo.MemberEntry.TABLE_NAME +
                " ON " + TableInfo.StepRecordEntry.TABLE_NAME + "." + TableInfo.StepRecordEntry.COLUMN_NAME_MEMBER_ID + " = " +
                TableInfo.MemberEntry.TABLE_NAME + "." + TableInfo.MemberEntry._ID +
                " GROUP BY " + TableInfo.StepRecordEntry.TABLE_NAME + "." + TableInfo.StepRecordEntry.COLUMN_NAME_MEMBER_ID +
                " ORDER BY max_steps DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"));
                int maxSteps = cursor.getInt(cursor.getColumnIndexOrThrow("max_steps"));

                rankingList.add(new RankingItemModel(nickname, maxSteps));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return rankingList;
    }

}
