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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //캘린더 객체 생성
        mCal = Calendar.getInstance();
        mCalToday = Calendar.getInstance();

        /*findViewById(R.id.prev).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);*/
        mainText = (TextView) findViewById(R.id.maintext);
        thisYear = mCal.get(Calendar.YEAR);
        thisMonth = mCal.get(Calendar.MONTH) + 1;

        //달력 세팅
        setCalendarDate(thisYear, thisMonth);

        DBHelper = new DbOpenHelper(MainActivity.this);
        DBHelper.open();
    }

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

                    //날짜 클릭시 내용 보이기
                    list = getTodaySchedule(thisYear + "-" + thisMonth + "-" + (position - startday + 2));

                    final int extDay = (position - startday + 2);

                    LinearLayout todayLayout = (LinearLayout) findViewById(R.id.layTodaySchedule);
                    todayLayout.removeAllViews();

                    Button btnAddSchedule = new Button(MainActivity.this);
                    btnAddSchedule.setText("일정추가");
                    btnAddSchedule.setWidth(50);
                    btnAddSchedule.setHeight(50);
                    btnAddSchedule.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this,DetailActivity.class);

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
            };
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
            }

            /*//오늘 날짜 색칠하게(구현해야됨)
            int nowMonth = Calendar.MONTH;
            int day = Calendar.DATE;

            if (nowMonth + "-" + day == thisMonth + "-" + position) {
                ViewText.setBackgroundColor(Color.YELLOW);
            }*/

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

}
