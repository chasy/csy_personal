package com.example.csy_personal;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 차승용 on 2016-02-19.
 */
public class editSchedule extends Activity {


    private DbOpenHelper DBHelper;
    String year;
    String month;
    String days;
    CalendarT cData;
    ArrayList<CalendarT> list;
    Cursor mCursor;
    String strDate;

    long no;

    @Override
    protected void onCreate(Bundle SaveInstanseState) {

        super.onCreate(SaveInstanseState);
        setContentView(R.layout.edit_schedule);

        Intent intent = getIntent();
        //list = new ArrayList<CalendarT>();
        mCursor = null;


        year = intent.getStringExtra("year");
        month = intent.getStringExtra("month");
        days = intent.getStringExtra("days");
        strDate = year + "-" + month + "-" + days;

        no = intent.getIntExtra("no", 0);

        TextView textView = (TextView) findViewById(R.id.editNowDay);
        textView.setText(strDate);

        DBHelper = new DbOpenHelper(this);
        DBHelper.open();

        //list = new ArrayList<CalendarT>();

        mCursor = DBHelper.getScheduleByNo(no);

        while (mCursor.moveToNext()) {

            cData = new CalendarT(
                    mCursor.getInt(mCursor.getColumnIndex("_id")),
                    mCursor.getString(mCursor.getColumnIndex("title")),
                    mCursor.getString(mCursor.getColumnIndex("contact")),
                    mCursor.getString(mCursor.getColumnIndex("date"))
            );
        }

        EditText title = (EditText) findViewById(R.id.editTitle);
        EditText content = (EditText) findViewById(R.id.editContent);
        title.setText(cData.title);
        content.setText(cData.contact);

    }

    public void btnEdit(View v) {

        EditText title = (EditText) findViewById(R.id.editTitle);
        EditText content = (EditText) findViewById(R.id.editContent);
        TextView nowday = (TextView) findViewById(R.id.editNowDay);

        /*DBHelper = new DbOpenHelper(this);
        DBHelper.open();*/

        DBHelper.updateColumn(no, title.getText().toString(), content.getText().toString(), nowday.getText().toString());

        DBHelper.close();

        Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void btnCancle(View v) {
        /*Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);*/
        finish();
    }


}
