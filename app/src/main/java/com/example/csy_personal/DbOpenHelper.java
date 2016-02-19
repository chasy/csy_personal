package com.example.csy_personal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by 차승용 on 2016-02-17.
 */
public class DbOpenHelper {

    private static final String DATABASE_NAME = "csy_personal.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        // 생성자
        public DatabaseHelper(Context context, String name,
                              CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // 최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DataBases.CreateDB._CREATE);

        }

        // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DataBases.CreateDB._TABLENAME);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context) {
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDB.close();
    }

    // DataBase Table
    public static final class DataBases {

        public static final class CreateDB implements BaseColumns {
            public static final String TITLE = "title";
            public static final String CONTACT = "contact";
            public static final String DATE = "date";
            public static final String _TABLENAME = "calender";
            public static final String _CREATE =
                    "create table " + _TABLENAME + "("
                            + _ID + " integer primary key autoincrement, "
                            + TITLE + " text not null , "
                            + CONTACT + " text not null , "
                            + DATE + " text not null );";
        }
    }

    // Insert DB
    public long insertColumn(String title, String contact, String date) {
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.TITLE, title);
        values.put(DataBases.CreateDB.CONTACT, contact);
        values.put(DataBases.CreateDB.DATE, date);
        return mDB.insert(DataBases.CreateDB._TABLENAME, null, values);
    }

    // Update DB
    public boolean updateColumn(long id, String title, String contact, String date) {
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.TITLE, title);
        values.put(DataBases.CreateDB.CONTACT, contact);
        values.put(DataBases.CreateDB.DATE, date);
        return mDB.update(DataBases.CreateDB._TABLENAME, values, "_id=" + String.valueOf(id), null) > 0;
    }

    // Delete ID
    public boolean deleteColumn(long id) {
        return mDB.delete(DataBases.CreateDB._TABLENAME, "_id=" + id, null) > 0;
    }

    // Delete Contact
    public boolean deleteColumn(String number) {
        return mDB.delete(DataBases.CreateDB._TABLENAME, "contact=" + number, null) > 0;
    }

    // Select All
    public Cursor getAllColumns() {
        return mDB.query(DataBases.CreateDB._TABLENAME, null, null, null, null, null, null);
    }

    // ID 컬럼 얻어 오기
    public Cursor getColumn(long id) {
        Cursor c = mDB.query(DataBases.CreateDB._TABLENAME, null,
                "_id=" + id, null, null, null, null);
        if (c != null && c.getCount() != 0)
            c.moveToFirst();
        return c;
    }

    // 이름 검색 하기 (rawQuery)
    public Cursor getMatchName(String name) {
        Cursor c = mDB.rawQuery("select * from calender where name=" + "'" + name + "'", null);
        return c;
    }

    public Cursor getTodaySchedule(String Date) {
        Cursor c = mDB.rawQuery("select * from calender where date = '" + Date + "'", null);
        return c;
    }

    public Cursor getScheduleByNo(long no) {
        Cursor c = mDB.rawQuery("select * from calender where _id = " + no + "", null);
        return c;
    }

    public int getTodayScheduleCount(String date) {
        String query = "select count(0) from " + DataBases.CreateDB._TABLENAME + " where date = '" + date + "'";
        Cursor cursor = mDB.rawQuery(query, null);
        int cnt = 0;
        if (null != cursor)
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                cnt = cursor.getInt(0);
            }
        return cnt;
    }
}
