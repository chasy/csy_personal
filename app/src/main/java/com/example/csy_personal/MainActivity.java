package com.example.csy_personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity {

    private ActivityManager actManager = ActivityManager.getInstance();

    public DbOpenHelper DBHelper;
    GridView mGridView;
    DateAdepter adapter;
    ArrayList<CalData> arrData;
    Calendar mCal;
    Calendar mCalToday;

    TextView mainText;
    int thisMonth;
    int thisYear;
    int startday;

    CalendarT cData;
    ArrayList<CalendarT> list;
    Cursor mCursor;
    String strDate;

    int realNowYear;
    int realNowMonth;
    int realNowDay;


    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;


    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actManager.addActivity(this);

        //캘린더 객체 생성
        mCal = Calendar.getInstance();
        mCalToday = Calendar.getInstance();

        /*findViewById(R.id.prev).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);*/
        mainText = (TextView) findViewById(R.id.maintext);
        thisYear = mCal.get(Calendar.YEAR);
        thisMonth = mCal.get(Calendar.MONTH) + 1;

        //오늘 날짜 색칠하게
        realNowYear = mCal.get(Calendar.YEAR);
        realNowMonth = mCal.get(Calendar.MONTH) + 1;
        realNowDay = mCal.get(Calendar.DATE);


        //달력 세팅
        setCalendarDate(thisYear, thisMonth);

        //backPressCloseHandler = new BackPressCloseHandler(this);

        DBHelper = new DbOpenHelper(MainActivity.this);
        DBHelper.open();
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            /*moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid() );*/
            actManager.finishAllActivity();

        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "'뒤로'버튼을한번더누르시면종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("종료")
                    .setMessage("종료할꺼냐?")
                    .setPositiveButton("응"
                            ,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick( DialogInterface dialog, int which )
                                {
                                    moveTaskToBack(true);
                                    finish();
                                }
                            }
                    ).setNegativeButton("아니", null).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }*/



    /*@Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }*/

    //@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev:
                if (thisMonth > 1) {
                    thisMonth--;
                    setCalendarDate(thisYear, thisMonth);
                } else {
                    thisYear--;
                    thisMonth = 12;
                    setCalendarDate(thisYear, thisMonth);
                }
                break;
            case R.id.next:
                if (thisMonth < 12) {
                    thisMonth++;
                    setCalendarDate(thisYear, thisMonth);
                } else {
                    thisYear++;
                    thisMonth = 1;
                    setCalendarDate(thisYear, thisMonth);
                }
                break;
        }
    }

    public void setCalendarDate(int year, int month) {
        arrData = new ArrayList<CalData>();


        //오늘 날짜 구하기
        mCalToday.set(year, month - 1, 1);

        //빈 기간만큼 공백
        startday = mCalToday.get(Calendar.DAY_OF_WEEK);
        if (startday != 1) {
            for (int i = 0; i < startday - 1; i++) {
                arrData.add(null);
            }
        }
        mCal.set(Calendar.MONTH, month - 1);

        //해당월의 일 구하기
        for (int i = 0; i < mCalToday.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            mCalToday.set(year, month - 1, (i + 1));
            arrData.add(new CalData((i + 1), mCalToday.get(Calendar.DAY_OF_WEEK)));
        }

        adapter = new DateAdepter(this, arrData);

        mGridView = (GridView) findViewById(R.id.calGrid);
        mGridView.setAdapter(adapter);

        //그리드뷰 클릭이벤트
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(android.widget.AdapterView<?> parent,
                                    android.view.View view, int position, long id) {

                if (position - startday + 2 > 0) {

                  /*  //날짜 클릭시 페이지 이동
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra("year", String.valueOf(thisYear));
                    intent.putExtra("month", String.valueOf(thisMonth));
                    intent.putExtra("days", String.valueOf(position - startday + 2));

                    startActivity(intent);*/


                    strDate = thisYear + "-" + thisMonth + "-" + (position - startday + 2);
                    //날짜 클릭시 내용 보이기
                    list = getTodaySchedule(strDate);

                    final int extDay = (position - startday + 2);

                    LinearLayout todayLayout = (LinearLayout) findViewById(R.id.layTodaySchedule);
                    todayLayout.removeAllViews();

                    //int cnt = DBHelper.getTodayScheduleCount(strDate);

                    TextView txtScheduleCount = new TextView(MainActivity.this);
                    txtScheduleCount.setTextSize(15);
                    txtScheduleCount.setText(list.size() + "개의 스케줄이 있음");
                    todayLayout.addView(txtScheduleCount);

                    Button btnAddSchedule = new Button(MainActivity.this);
                    btnAddSchedule.setText("일정추가");
                    btnAddSchedule.setWidth(50);
                    btnAddSchedule.setHeight(50);
                    btnAddSchedule.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                            intent.putExtra("year", String.valueOf(thisYear));
                            intent.putExtra("month", String.valueOf(thisMonth));
                            intent.putExtra("days", String.valueOf(extDay));

                            startActivity(intent);
                        }
                    });

                    todayLayout.addView(btnAddSchedule);

                    if (list.size() > 0) {
                        CalendarT data;
                        for (int i = 0; i < list.size(); i++) {
                            data = list.get(i);

                            final int no = data._id;

                            //텍스트 생성
                            TextView addTitle = new TextView(MainActivity.this);
                            addTitle.setText(data.title);
                            addTitle.setTextSize(30);
                            addTitle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DBHelper.close();
                                    Intent intent = new Intent(getApplicationContext(), editSchedule.class);
                                    intent.putExtra("year", String.valueOf(thisYear));
                                    intent.putExtra("month", String.valueOf(thisMonth));
                                    intent.putExtra("days", String.valueOf(extDay));
                                    intent.putExtra("no", no);

                                    startActivity(intent);

                                }
                            });
                            todayLayout.addView(addTitle);
                        }
                    }
                }
            }

            ;
        });

        mainText.setText(year + "-" + month);
    }

    class DateAdepter extends BaseAdapter {
        private Context context;
        private ArrayList<CalData> arrData;
        private LayoutInflater inflater;

        public DateAdepter(Context c, ArrayList<CalData> arr) {
            this.context = c;
            this.arrData = arr;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        public int getCount() {
            return arrData.size();
        }

        public Object getItem(int position) {
            return arrData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.viewitem, parent, false);
            }

            TextView ViewText = (TextView) convertView.findViewById(R.id.ViewText);

            if (arrData.get(position) == null) {
                ViewText.setText("");
            } else {
                ViewText.setText(arrData.get(position).getDay() + "");
                if (arrData.get(position).getDayofweek() == 1) {
                    ViewText.setTextColor(Color.RED);
                } else if (arrData.get(position).getDayofweek() == 7) {
                    ViewText.setTextColor(Color.BLUE);
                } else {
                    ViewText.setTextColor(Color.BLACK);
                }

//                if (nowMonth + "-" + day == thisMonth + "-" + arrData.get(position).getDay()) {
                if ((realNowYear + "-" + realNowMonth + "-" + realNowDay).equals((thisYear + "-" + thisMonth + "-" + arrData.get(position).getDay()))) {
                    ViewText.setBackgroundColor(Color.GREEN);
                }
            }

            return convertView;
        }

    }

    public void goDetailPage(View v) {
        TextView day = (TextView) findViewById(R.id.ViewText);

        //String test =

        Toast.makeText(MainActivity.this, day.getText(), Toast.LENGTH_SHORT).show();
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

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        clearApplicationCache(null);
        android.os.Process.killProcess(android.os.Process.myPid() );
    }

    //종료시 모든 캐쉬 삭제
    public void clearApplicationCache(java.io.File dir){
        if(dir==null) dir = getCacheDir();
        if(dir==null) return;
        java.io.File[] children = dir.listFiles();
        try{
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();

            for(int i=0;i<children.length;i++)
                if(children[i].isDirectory())
                    clearApplicationCache(children[i]);
                else children[i].delete();
        }
        catch(Exception e){}
    }*/
}
