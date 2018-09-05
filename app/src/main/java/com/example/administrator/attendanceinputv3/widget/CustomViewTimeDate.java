package com.example.administrator.attendanceinputv3.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.attendanceinputv3.R;

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
    }


}
