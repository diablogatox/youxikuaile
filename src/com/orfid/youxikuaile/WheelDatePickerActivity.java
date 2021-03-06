package com.orfid.youxikuaile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.orfid.youxikuaile.widget.wheelview.NumericWheelAdapter;
import com.orfid.youxikuaile.widget.wheelview.WheelView;
import com.orfid.youxikuaile.widget.wheelview.NumericWheelAdapter;
import com.orfid.youxikuaile.widget.wheelview.OnWheelChangedListener;
import com.orfid.youxikuaile.widget.wheelview.OnWheelScrollListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/3/2.
 */
public class WheelDatePickerActivity extends Activity implements View.OnClickListener {

    private Calendar mCalendar;
    private static int START_YEAR = 1895, END_YEAR = 2700;
    // private final OnDateTimeSetListener mCallBack;
    private int curr_year, curr_month, curr_day, curr_hour, curr_minute;
    // 添加大小月月份并将其转换为list,方便之后的判断
    String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
    String[] months_little = { "4", "6", "9", "11" };
    WheelView wv_year;
    WheelView wv_month;
    WheelView wv_day;
    List<String> list_big;
    List<String> list_little;

    TextView cancelTv, okTv;
    int age = 0;
    long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_date_picker);

        cancelTv = (TextView) findViewById(R.id.tv_cancel);
        okTv = (TextView) findViewById(R.id.tv_ok);
        cancelTv.setOnClickListener(this);
        okTv.setOnClickListener(this);

        mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        END_YEAR = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DATE);
        list_big = Arrays.asList(months_big);
        list_little = Arrays.asList(months_little);
        int textSize = 0;
        textSize = adjustFontSize(getWindow().getWindowManager());
        // 年
        wv_year = (WheelView) findViewById(R.id.year);
        wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
        wv_year.setCyclic(false);// 可循环滚动
        wv_year.setLabel("年");// 添加文字
        wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

        // 月
        wv_month = (WheelView) findViewById(R.id.month);
        wv_month.setAdapter(new NumericWheelAdapter(1, 12));
        wv_month.setCyclic(true);
        wv_month.setLabel("月");
        wv_month.setCurrentItem(month);

        // 日
        wv_day = (WheelView) findViewById(R.id.day);
        wv_day.setCyclic(true);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 31));
        } else if (list_little.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 30));
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                wv_day.setAdapter(new NumericWheelAdapter(1, 29));
            else
                wv_day.setAdapter(new NumericWheelAdapter(1, 28));
        }
        wv_day.setLabel("日");
        wv_day.setCurrentItem(day - 1);

        wv_year.addChangingListener(wheelListener_year);
        wv_month.addChangingListener(wheelListener_month);
        wv_day.TEXT_SIZE = textSize;
        wv_month.TEXT_SIZE = textSize;
        wv_year.TEXT_SIZE = textSize;
        wv_year.addScrollingListener(onWheelScrollListener);
        wv_month.addScrollingListener(onWheelScrollListener);
        wv_day.addScrollingListener(onWheelScrollListener);
    }

    OnWheelScrollListener onWheelScrollListener = new OnWheelScrollListener() {

        @Override
        public void onScrollingStarted(WheelView wheel) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            // TODO Auto-generated method stub
//            age.setText((mCalendar.get(Calendar.YEAR)
//                    - wv_year.getCurrentItem() - START_YEAR)
//                    + "岁");
            age = mCalendar.get(Calendar.YEAR)
                    - wv_year.getCurrentItem() - START_YEAR;

            curr_year = wv_year.getCurrentItem() + START_YEAR;
            curr_month = wv_month.getCurrentItem() + 1;
            curr_day = wv_day.getCurrentItem() + 1;
//            birthday.setText(getConstellation(curr_month, curr_day));
            Calendar calendar = new GregorianCalendar(curr_year, curr_month - 1, curr_day);
            timestamp = TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
        }
    };

    // 添加"年"监听
    OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            int year_num = newValue + START_YEAR;
            // 判断大小月及是否闰年,用来确定"日"的数据
            if (list_big
                    .contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                wv_day.setAdapter(new NumericWheelAdapter(1, 31));
            } else if (list_little.contains(String.valueOf(wv_month
                    .getCurrentItem() + 1))) {
                wv_day.setAdapter(new NumericWheelAdapter(1, 30));
            } else {
                if ((year_num % 4 == 0 && year_num % 100 != 0)
                        || year_num % 400 == 0)
                    wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                else
                    wv_day.setAdapter(new NumericWheelAdapter(1, 28));
            }
        }
    };
    // 添加"月"监听
    OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            int month_num = newValue + 1;
            // 判断大小月及是否闰年,用来确定"日"的数据
            if (list_big.contains(String.valueOf(month_num))) {
                wv_day.setAdapter(new NumericWheelAdapter(1, 31));
            } else if (list_little.contains(String.valueOf(month_num))) {
                wv_day.setAdapter(new NumericWheelAdapter(1, 30));
            } else {
                if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
                        .getCurrentItem() + START_YEAR) % 100 != 0)
                        || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
                    wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                else
                    wv_day.setAdapter(new NumericWheelAdapter(1, 28));
            }
        }
    };

    public static int adjustFontSize(WindowManager windowmanager) {

        int screenWidth = windowmanager.getDefaultDisplay().getWidth();
        int screenHeight = windowmanager.getDefaultDisplay().getHeight();
        if (screenWidth <= 240) { // 240X320 屏幕
            return 20;
        } else if (screenWidth <= 320) { // 320X480 屏幕
            return 24;
        } else if (screenWidth <= 480) { // 480X800 或 480X854 屏幕
            return 34;
        } else if (screenWidth <= 540) { // 540X960 屏幕
            return 36;
        } else if (screenWidth <= 800) { // 800X1280 屏幕
            return 40;
        } else { // 大于 800X1280
            return 40;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.tv_ok:
                setResult(RESULT_OK, new Intent().putExtra("age", age).putExtra("timestamp", timestamp));
                finish();
                overridePendingTransition(0, 0);
                break;
        }
    }
}
