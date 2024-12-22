package com.example.mobile_term_project.db;

import android.provider.BaseColumns;

public class TableInfo {

    private TableInfo() {}

    public static class MemberEntry implements BaseColumns {
        public static final String TABLE_NAME = "member";
        public static final String COLUMN_NAME_NICKNAME = "nickname";
        public static final String COLUMN_NAME_PASSWORD = "password";

    }

    public static class StepRecordEntry implements BaseColumns {
        public static final String TABLE_NAME = "step_record";
        public static final String COLUMN_NAME_MEMBER_ID = "member_id"; //BaseColums의 default로 생선된 member 테이블의 id는  _id 형태의 도메인임
        public static final String COLUMN_NAME_COUNT = "count";
        public static final String COLUMN_NAME_DISTANCE = "distance";

        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_END_TIME = "end_time";
        public static final String COLUMN_NAME_IMAGE = "image";
    }
}
