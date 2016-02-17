package com.example.csy_personal;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity {

    GridView mGridView;
    DateAdepter adapter;
    ArrayList<CalData> arrData;
    Calendar mCal;
    Calendar mCalToday;

    TextView mainText;
    int thisMonth;
    int thisYear;

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
        int startday = mCalToday.get(Calendar.DAY_OF_WEEK);
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
            if (arrData.get(position) == null)
                ViewText.setText("");
            else {
                ViewText.setText(arrData.get(position).getDay() + "");
                if (arrData.get(position).getDayofweek() == 1) {
                    ViewText.setTextColor(Color.RED);
                } else if (arrData.get(position).getDayofweek() == 7) {
                    ViewText.setTextColor(Color.BLUE);
                } else {
                    ViewText.setTextColor(Color.BLACK);
                }
            }


            return convertView;

        }
    }


}
