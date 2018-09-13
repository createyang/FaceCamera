package com.quansoon.facecamera.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.base.BaseApplication;
import com.quansoon.facecamera.model.TimeBean;
import com.quansoon.facecamera.network.NetworkListener;
import com.quansoon.facecamera.network.TimeRequest;
import com.quansoon.facecamera.utils.DateTimeUtil;
import com.quansoon.facecamera.utils.LogUtils;
import com.quansoon.facecamera.utils.Lunar;
import com.quansoon.facecamera.utils.UiUtil;

import java.util.Calendar;

/**
 * @author: Caoy
 * @created on: 2018/9/1 11:20
 * @description:
 */
public class CustomViewTimeDate extends LinearLayout {
    View view;
    TextView mTvTime;
    TextView mTvNumberDate;
    TextView mTvLunarCalendar;
    private TimeRequest timeRequest;
    private static final int REFRESH_DELAY = 1000;
    long timeDateLong;
    private Handler handler;

    public CustomViewTimeDate(Context context) {
        super(context);
    }

    public CustomViewTimeDate(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_view_time_date, this, true);
        this.view = view;
        this.mTvTime = (TextView) view.findViewById(R.id.tv_time);
        this.mTvNumberDate = (TextView) view.findViewById(R.id.tv_number_date);
        this.mTvLunarCalendar = (TextView) view.findViewById(R.id.tv_lunar_calendar);

        Typeface typeFace = Typeface.createFromAsset(BaseApplication.getContext().getAssets(), "mono.ttf");
        mTvTime.setTypeface(typeFace);

        initData();
    }

    private void initData() {
        /**
         *1.获取网络的时间
         * 2.成功将时间转换为时间戳，然后handler循环添加
         * 3.错误使用系统的时间进行显示，然后handler循环添加
         */
        handler = UiUtil.getHandler();
        timeRequest = new TimeRequest();
        requestUpdateDate();
    }

    NetworkListener<TimeBean> networkListener = new NetworkListener<TimeBean>() {
        @Override
        public void onStart() {
            setUiDate();
        }

        @Override
        public void onError(String localizedMessage) {
            setUiDate();
        }

        @Override
        public void onSuccess(TimeBean data) {
            mTvTime.setText(data.getTime());
            mTvNumberDate.setText(data.getDate() + " " + data.getDateFm());
            LogUtils.d("onSuccess " + data.getLunar());
            mTvLunarCalendar.setText(String.format(getContext().getString(R.string.str_lunar), data.getLunar()));
            timeDateLong = DateTimeUtil.getStringToDate(data.getTime(), "HH:mm:ss");

            handler.removeCallbacks(runnable);
            handler.post(runnable);
        }
    };

    private void setUiDate() {
        String timeStr = DateTimeUtil.getCurDate("HH:mm:ss");
        String numberDateStr = DateTimeUtil.getCurDate("MM月dd日");
        String weekStr = DateTimeUtil.getCurDate("EEEE");
        mTvTime.setText(timeStr);
        mTvNumberDate.setText(numberDateStr + " " + weekStr);

        Calendar calendar = Calendar.getInstance();
        Lunar lunar = new Lunar(calendar);
        mTvLunarCalendar.setText(String.format(getContext().getString(R.string.str_lunar), lunar.toString()));

        timeDateLong = DateTimeUtil.getStringToDate(timeStr, "HH:mm:ss");

        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timeDateLong += 1000;
            mTvTime.setText(DateTimeUtil.getDateToString(timeDateLong, "HH:mm:ss"));

            if (mTvTime.getText().equals("00:00:00")) {
                timeRequest.getCurrentTimeRequest(networkListener);
            }

            handler.postDelayed(this, REFRESH_DELAY);
        }
    };

    public void requestUpdateDate() {
        if (timeRequest != null) {
            timeRequest.getCurrentTimeRequest(networkListener);
        }
    }
}
