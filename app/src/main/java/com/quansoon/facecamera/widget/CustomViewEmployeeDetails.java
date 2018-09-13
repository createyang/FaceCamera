package com.quansoon.facecamera.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quansoon.facecamera.R;

/**
 * @author: Caoy
 * @created on: 2018/9/5 11:33
 * @description:
 */
public class CustomViewEmployeeDetails extends LinearLayout {

    private float mProgress;

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

            mTvTeam.setText(String.format(view.getContext().getString(R.string.str_team), view.getContext().getString(R.string.str_)));
            mTvWorkType.setText(String.format(view.getContext().getString(R.string.str_type), view.getContext().getString(R.string.str_)));
        }
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
////        1.使用OpenGl实现，在片元着色器中针对不同的s(纹理坐标)值，设置不同的alpha值。
////        2.利用Canvas绘制
////        canvas.drawBitmap(mBitmap, 0, 0, new Paint());
//        Paint p = new Paint();
//        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//        canvas.saveLayer(new RectF(0, 0, this.getWidth(), this.getHeight()), p);
//        canvas.drawPaint(getAlphaPaint(getWidth(), getHeight()));
//        canvas.restore();
//
////        getAlphaPaint可以是透明度从左到右逐渐变化的paint,类似以下代码。
//    }
//
//    public void  setBgTranAnimation(float mProgress){
//        this.mProgress = mProgress;
//        invalidate();
//    }
//
//    private Paint getAlphaPaint(int imageW, int imageH) {
//        Paint paint = new Paint();
//        if (mProgress > 0f && mProgress < 0.2f) {
//            paint.setShader(new LinearGradient(0, imageH / 2, imageW * mProgress, imageH / 2, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP));
//        } else {
//            paint.setShader(new LinearGradient(0, imageH / 2, imageW / 5, imageH / 2, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP));
//        }
//        return paint;
//    }
}
