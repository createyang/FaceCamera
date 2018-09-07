package com.example.administrator.attendanceinputv3.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.attendanceinputv3.R;

/**
 * @author: Caoy
 * @created on: 2018/9/5 11:33
 * @description:
 */
public class CustomViewEmployeeDetails extends LinearLayout {

    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    private ViewHolder viewHolder;

    public CustomViewEmployeeDetails(Context context) {
        super(context);
    }

    public CustomViewEmployeeDetails(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_view_employee_details, this, true);
        viewHolder = new ViewHolder(view);
    }

    public static class ViewHolder {
        View view;
        ImageView mIvFaceIcon;
        TextView mTvName;
        TextView mTvTime;
        TextView mTvTeam;
        TextView mTvWorkType;

        public View getView() {
            return view;
        }

        public ImageView getmIvFaceIcon() {
            return mIvFaceIcon;
        }

        public TextView getmTvName() {
            return mTvName;
        }

        public TextView getmTvTime() {
            return mTvTime;
        }

        public TextView getmTvTeam() {
            return mTvTeam;
        }

        public TextView getmTvWorkType() {
            return mTvWorkType;
        }

        public LinearLayout getmLayoutEmployeeDetails1() {
            return mLayoutEmployeeDetails1;
        }

        LinearLayout mLayoutEmployeeDetails1;

        public ViewHolder(View view) {
            this.view = view;
            this.mIvFaceIcon = (ImageView) view.findViewById(R.id.iv_face_icon);
            this.mTvName = (TextView) view.findViewById(R.id.tv_name);
            this.mTvTime = (TextView) view.findViewById(R.id.tv_time);
            this.mTvTeam = (TextView) view.findViewById(R.id.tv_team);
            this.mTvWorkType = (TextView) view.findViewById(R.id.tv_work_type);
            this.mLayoutEmployeeDetails1 = (LinearLayout) view.findViewById(R.id.layout_employee_details_1);
        }
    }
}
