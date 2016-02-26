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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by 차승용 on 2016-02-19.
 */
public class editSchedule extends Activity implements OnTimeChangedListener {

    private ActivityManager actManager = ActivityManager.getInstance();

    private DbOpenHelper DBHelper;
    String year;
    String month;
    String days;
    CalendarT cData;
    ArrayList<CalendarT> list;
    Cursor mCursor;
    String strDate;

    long no;

    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private GregorianCalendar mCalendar;
    //시작 설정 클래스
    private TimePicker mTime;

    //통지 관련 맴버 함수
    private NotificationManager mNotification;

    @Override
    protected void onCreate(Bundle SaveInstanseState) {

        super.onCreate(SaveInstanseState);
        setContentView(R.layout.edit_schedule);

        actManager.addActivity(this);

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

        getTodaySchedule();

        if (cData.alarmYn.equals("Y")) {
            LinearLayout hideLay = (LinearLayout) findViewById(R.id.layHideSetAlarm);
            hideLay.setVisibility(View.GONE);

            LinearLayout layout = (LinearLayout) findViewById(R.id.laySetAlarm);
            layout.setVisibility(View.VISIBLE);
        }

        //통지 매니저를 취득
        mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //알람 매니저를 취득
        mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mCalendar = new GregorianCalendar();
        mTime = (TimePicker) findViewById(R.id.time_picker);
        if (cData.isam == "N") {
            cData.hour += 12;
        }
        mTime.setCurrentHour(cData.hour);
        mTime.setCurrentMinute(cData.minute);
        mTime.setOnTimeChangedListener(this);

        EditText title = (EditText) findViewById(R.id.editTitle);
        EditText content = (EditText) findViewById(R.id.editContent);
        title.setText(cData.title);
        content.setText(cData.contact);

    }

    public void getTodaySchedule() {
        mCursor = DBHelper.getScheduleByNo(no);

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
        }
    }

    public void btnEdit(View v) {

        EditText title = (EditText) findViewById(R.id.editTitle);
        EditText content = (EditText) findViewById(R.id.editContent);
        TextView nowday = (TextView) findViewById(R.id.editNowDay);

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

        /*DBHelper = new DbOpenHelper(this);
        DBHelper.open();*/

            DBHelper.updateColumn(no, title.getText().toString(), content.getText().toString(), nowday.getText().toString(), hour, minute, strIsam, alarmYn);

            DBHelper.close();

            Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void btnCancleAlarm(View v) {
        LinearLayout hideLay = (LinearLayout) findViewById(R.id.layHideSetAlarm);
        hideLay.setVisibility(View.VISIBLE);

        LinearLayout layout = (LinearLayout) findViewById(R.id.laySetAlarm);
        layout.setVisibility(View.GONE);
    }

    public void btnShowSetAlarm(View v) {
        LinearLayout hideLay = (LinearLayout) findViewById(R.id.layHideSetAlarm);
        hideLay.setVisibility(View.GONE);

        LinearLayout layout = (LinearLayout) findViewById(R.id.laySetAlarm);
        layout.setVisibility(View.VISIBLE);
    }

    //알람의 설정
    public void setAlarm() {
        Calendar nowTIme = Calendar.getInstance();
        nowTIme.setTimeInMillis(System.currentTimeMillis());

        if(nowTIme.getTimeInMillis() - mCalendar.getTimeInMillis() < 0) {

            // 알람 매니저에 등록할 인텐트를 만듬
            Intent intent = new Intent(editSchedule.this, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(editSchedule.this, 0, intent, 0);

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
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), sender);
            //mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), sender);
        }
    }

    public void btnCancle(View v) {
        /*Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);*/
        finish();
    }

    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(days), hourOfDay, minute, 0);
    }
}
