package com.example.csy_personal;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by 차승용 on 2016-02-17.
 * 일정 상세 페이지
 */
public class DetailActivity extends Activity implements OnTimeChangedListener {

    private ActivityManager actManager = ActivityManager.getInstance();

    private DbOpenHelper DBHelper;
    String year;
    String month;
    String days;
    CalendarT cData;
    ArrayList<CalendarT> list;
    Cursor mCursor;
    String strDate;

    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private GregorianCalendar mCalendar;
    //시작 설정 클래스
    private TimePicker mTime;

    //통지 관련 맴버 함수
    private NotificationManager mNotification;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.detail);

        actManager.addActivity(this);

        Intent intent = getIntent();
        list = new ArrayList<CalendarT>();
        mCursor = null;


        year = intent.getStringExtra("year");
        month = intent.getStringExtra("month");
        days = intent.getStringExtra("days");
        strDate = year + "-" + month + "-" + days;

        TextView textView = (TextView) findViewById(R.id.lblNowDay);
        textView.setText(strDate);

        DBHelper = new DbOpenHelper(this);
        DBHelper.open();

        //통지 매니저를 취득
        mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //알람 매니저를 취득
        mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mCalendar = new GregorianCalendar();

        //일시 설정 클래스로 현재 시각을 설정
        mTime = (TimePicker) findViewById(R.id.time_picker);
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTime.setOnTimeChangedListener(this);

        /*list = getTodaySchedule(strDate);

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

                        *//*DBHelper = new DbOpenHelper(DetailActivity.this);
                        DBHelper.open();*//*

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
        }*/
    }

    public void btnBack(View v) {
        /*Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);*/
        finish();
    }

    public void btnSave(View v) {

        EditText title = (EditText) findViewById(R.id.txtTitle);
        EditText content = (EditText) findViewById(R.id.txtContent);
        TextView nowday = (TextView) findViewById(R.id.lblNowDay);

        /*DBHelper = new DbOpenHelper(this);
        DBHelper.open();*/

        if (title.getText().length() == 0 || content.getText().length() == 0) {
            Toast.makeText(this, "제목과 내용은 필수입니다.", Toast.LENGTH_SHORT).show();
        } else {

            long seq = DBHelper.insertColumn(title.getText().toString(), content.getText().toString(), nowday.getText().toString());

            DBHelper.close();

            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

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
        /*DBHelper = new DbOpenHelper(this);
        DBHelper.open();*/

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

        /*DBHelper = new DbOpenHelper(this);
        DBHelper.open();*/

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

    //알람의 설정
    public void setAlarm(View v) {
        // 알람 매니저에 등록할 인텐트를 만듬
        Intent intent = new Intent(DetailActivity.this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(DetailActivity.this, 0, intent, 0);

        //임시로 3초뒤에 울리게 해서 테스트중
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 5);

        Date date = mCalendar.getTime();
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String fDate = sFormat.format(date);

        Date date2 = calendar.getTime();
        SimpleDateFormat sFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String fDate2 = sFormat2.format(date2);


        Toast.makeText(this, fDate + "에 알람 등록", Toast.LENGTH_SHORT).show();

        //알람객체생성
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        mManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        //mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), sender);
    }

    //알람의 해제
    public void resetAlarm(View v) {
        mManager.cancel(pendingIntent());
    }

    //알람의 설정 시각에 발생하는 인텐트 작성
    private PendingIntent pendingIntent() {
        Toast.makeText(this, "테스트알림", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        return pi;
    }

   /* //일자 설정 클래스의 상태변화 리스너
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(year, monthOfYear, dayOfMonth, mTime.getCurrentHour(), mTime.getCurrentMinute());
        //Log.i("HelloAlarmActivity", mCalendar.getTime().toString());
    }*/

    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(days), hourOfDay, minute, 0);
        //Log.i("HelloAlarmActivity",mCalendar.getTime().toString());
    }

}
