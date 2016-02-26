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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
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

    }

    public void btnBack(View v) {
        finish();
    }

    public void btnSave(View v) {

        EditText title = (EditText) findViewById(R.id.txtTitle);
        EditText content = (EditText) findViewById(R.id.txtContent);
        TextView nowday = (TextView) findViewById(R.id.lblNowDay);

        LinearLayout layout = (LinearLayout) findViewById(R.id.laySetAlarm);
        int visibility = layout.getVisibility(); //0 = visible , 8 = gone

        int hour = mCalendar.get(Calendar.HOUR);
        int minute = mCalendar.get(Calendar.MINUTE);
        int isam = mCalendar.get(Calendar.AM_PM); //0 = am, 1 = pm
        String strIsam = isam == 0 ? "Y" : "N";
        String alarmYn = visibility==0? "Y":"N";

        if (title.getText().length() == 0 || content.getText().length() == 0) {
            Toast.makeText(this, "제목과 내용은 필수입니다.", Toast.LENGTH_SHORT).show();
        } else {

            long seq = DBHelper.insertColumn(title.getText().toString(), content.getText().toString(), nowday.getText().toString(), hour, minute, strIsam, alarmYn);

            DBHelper.close();

            //알람 추가 했을경우에만 동작
            if (visibility == 0) {
                setAlarm();
            }

            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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
                        mCursor.getString(mCursor.getColumnIndex("date")),
                        mCursor.getInt(mCursor.getColumnIndex("hour")),
                        mCursor.getInt(mCursor.getColumnIndex("minute")),
                        mCursor.getString(mCursor.getColumnIndex("isam")),
                        mCursor.getString(mCursor.getColumnIndex("alarmyn"))
                );
                list.add(cData);
            }
        } catch (Exception e) {
            Toast.makeText(DetailActivity.this, "읽기 실패!!", Toast.LENGTH_SHORT).show();
        }

        DBHelper.close();

    }

    public ArrayList<CalendarT> getTodaySchedule(String date) {

        list = new ArrayList<CalendarT>();

        mCursor = DBHelper.getTodaySchedule(date);

        while (mCursor.moveToNext()) {

            cData = new CalendarT(
                    mCursor.getInt(mCursor.getColumnIndex("_id")),
                    mCursor.getString(mCursor.getColumnIndex("title")),
                    mCursor.getString(mCursor.getColumnIndex("contact")),
                    mCursor.getString(mCursor.getColumnIndex("date")),
                    mCursor.getInt(mCursor.getColumnIndex("hour")),
                    mCursor.getInt(mCursor.getColumnIndex("minute")),
                    mCursor.getString(mCursor.getColumnIndex("isam")),
                    mCursor.getString(mCursor.getColumnIndex("alarmyn"))

            );
            list.add(cData);
        }

        return list;
    }

    //알람의 설정
    public void setAlarm() {
        Calendar nowTIme = Calendar.getInstance();
        nowTIme.setTimeInMillis(System.currentTimeMillis());

        if(nowTIme.getTimeInMillis() - mCalendar.getTimeInMillis() < 0) {

            // 알람 매니저에 등록할 인텐트를 만듬
            Intent intent = new Intent(DetailActivity.this, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(DetailActivity.this, 0, intent, 0);

            /*//임시로 3초뒤에 울리게 해서 테스트중
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 5);*/

            /*Date date = mCalendar.getTime();
            SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String fDate = sFormat.format(date);

            Date date2 = calendar.getTime();
            SimpleDateFormat sFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String fDate2 = sFormat2.format(date2);


            Toast.makeText(this, fDate + "에 알람 등록", Toast.LENGTH_SHORT).show();*/

            //알람객체생성
            mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), sender);
            //mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), sender);
        }
    }

    public void btnShowSetAlarm(View v) {
        LinearLayout hideLay = (LinearLayout) findViewById(R.id.layHideSetAlarm);
        hideLay.setVisibility(View.GONE);

        LinearLayout layout = (LinearLayout) findViewById(R.id.laySetAlarm);
        layout.setVisibility(View.VISIBLE);
    }

    public void btnCancleAlarm(View v) {
        LinearLayout hideLay = (LinearLayout) findViewById(R.id.layHideSetAlarm);
        hideLay.setVisibility(View.VISIBLE);

        LinearLayout layout = (LinearLayout) findViewById(R.id.laySetAlarm);
        layout.setVisibility(View.GONE);
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

    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(days), hourOfDay, minute, 0);
    }

}
