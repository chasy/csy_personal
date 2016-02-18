package com.example.csy_personal;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.ArrayList;


/**
 * Created by 차승용 on 2016-02-17.
 * 일정 상세 페이지
 */
public class DetailActivity extends Activity {

    private DbOpenHelper DBHelper;
    String year;
    String month;
    String days;
    CalendarT cData;
    ArrayList<CalendarT> list;
    Cursor mCursor;
    String strDate;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.detail);

        Intent intent = getIntent();
        list = new ArrayList<CalendarT>();
        mCursor = null;


        year = intent.getStringExtra("year");
        month = intent.getStringExtra("month");
        days = intent.getStringExtra("days");
        strDate = year + "-" + month + "-" + days;

        TextView textView = (TextView) findViewById(R.id.lblNowDay);
        textView.setText(strDate);

        list = getTodaySchedule(strDate);

        TextView txtScheduleCount = (TextView) findViewById(R.id.txtScheduleCount);
        txtScheduleCount.setText(list.size() + "개의 스케줄이 있음");
        //ReadFile();

        if (list.size() > 0) {
            CalendarT data;
            LinearLayout addSchedule = (LinearLayout) findViewById(R.id.addSchedule);

            for (int i = 0; i < list.size(); i++) {
                data = list.get(i);

                final int no = data._id;

                //텍스트 생성
                TextView addTitle = new TextView(this);
                addTitle.setText(data.title);
                addTitle.setTextSize(30);
                addTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DBHelper = new DbOpenHelper(DetailActivity.this);
                        DBHelper.open();

                        list = new ArrayList<CalendarT>();

                        mCursor = DBHelper.getScheduleByNo(no);

                        while (mCursor.moveToNext()) {

                            cData = new CalendarT(
                                    mCursor.getInt(mCursor.getColumnIndex("_id")),
                                    mCursor.getString(mCursor.getColumnIndex("title")),
                                    mCursor.getString(mCursor.getColumnIndex("contact")),
                                    mCursor.getString(mCursor.getColumnIndex("date"))
                            );
                        }
                        DBHelper.close();

                        EditText title = (EditText) findViewById(R.id.txtTitle);
                        EditText content = (EditText) findViewById(R.id.txtContent);
                        title.setText(cData.title);
                        content.setText(cData.contact);


                    }
                });
                addSchedule.addView(addTitle);
            }
        }
    }

    public void btnBack(View v) {
        finish();
    }

    public void btnSave(View v) {

        EditText title = (EditText) findViewById(R.id.txtTitle);
        EditText content = (EditText) findViewById(R.id.txtContent);
        TextView nowday = (TextView) findViewById(R.id.lblNowDay);

        DBHelper = new DbOpenHelper(this);
        DBHelper.open();

        long seq = DBHelper.insertColumn(title.getText().toString(), content.getText().toString(), nowday.getText().toString());

        DBHelper.close();

        Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        finish();

        //테스트로 저장
        /*final String fileName = nowday.getText().toString();

        try{
            FileOutputStream fos = openFileOutput(fileName,Context.MODE_PRIVATE);
            String str = content.getText().toString();
            fos.write(str.getBytes());
            fos.close();
            Toast.makeText(this,"저장되었습니다.",Toast.LENGTH_SHORT).show();
            finish();;
        }
        catch(Exception e){
            Toast.makeText(this,"에러!!",Toast.LENGTH_SHORT).show();
        }
        finally {

        }*/

    }

    public void ReadFile() {
        try {
            TextView nowday = (TextView) findViewById(R.id.lblNowDay);

            final String fileName = nowday.getText().toString();

            /*File files = new File(fileName);

            if(files.exists()==true) {*/

            FileInputStream fis = openFileInput(fileName);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String str = new String(buffer);

            EditText content = (EditText) findViewById(R.id.txtContent);
            content.setText(str);
            fis.close();
          /*  }
            else{
                Toast.makeText(this,"저장된 내용이 없습니다.",Toast.LENGTH_SHORT).show();
            }*/
        } catch (Exception e) {
            //Toast.makeText(this,"에러!!",Toast.LENGTH_SHORT).show();
        }
    }

    public void getAllSchedule() {
        DBHelper = new DbOpenHelper(this);
        DBHelper.open();

        mCursor = DBHelper.getAllColumns();
        try {
            while (mCursor.moveToNext()) {

                cData = new CalendarT(
                        mCursor.getInt(mCursor.getColumnIndex("_id")),
                        mCursor.getString(mCursor.getColumnIndex("title")),
                        mCursor.getString(mCursor.getColumnIndex("contact")),
                        mCursor.getString(mCursor.getColumnIndex("date"))
                );
                list.add(cData);
            }
        } catch (Exception e) {
            Toast.makeText(DetailActivity.this, "읽기 실패!!", Toast.LENGTH_SHORT).show();
        }

        DBHelper.close();

    }

    public ArrayList<CalendarT> getTodaySchedule(String date) {

        DBHelper = new DbOpenHelper(this);
        DBHelper.open();

        list = new ArrayList<CalendarT>();

        mCursor = DBHelper.getTodaySchedule(date);

        while (mCursor.moveToNext()) {

            cData = new CalendarT(
                    mCursor.getInt(mCursor.getColumnIndex("_id")),
                    mCursor.getString(mCursor.getColumnIndex("title")),
                    mCursor.getString(mCursor.getColumnIndex("contact")),
                    mCursor.getString(mCursor.getColumnIndex("date"))
            );
            list.add(cData);
        }

        return list;
    }


}
