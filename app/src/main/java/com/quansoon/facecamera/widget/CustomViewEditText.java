package com.quansoon.facecamera.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quansoon.facecamera.R;

/**
 * @author: Administrator
 * @created on: 2018/8/31 18:51
 * @description:
 */
public class CustomViewEditText extends RelativeLayout {

    public TextView getmTextViewCode() {
        return mTextViewCode;
    }

    private TextView mTextViewCode;
    private ListView lvSelectDevice;

    public ListView getLvSelectDevice() {
        return lvSelectDevice;
    }

    public LinearLayout getAddLayoutViewGroup() {
        return addLayoutViewGroup;
    }

    private LinearLayout addLayoutViewGroup;


    public EditText getmEditText() {
        return mEditText;
    }

    private EditText mEditText;

    public CustomViewEditText(Context context) {
        super(context);
    }

    public CustomViewEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_view_edit_text, this, true);
        this.mEditText = (EditText) view.findViewById(R.id.edit_code);
        this.mTextViewCode = (TextView) view.findViewById(R.id.tv_code);
        this.addLayoutViewGroup = (LinearLayout) view.findViewById(R.id.add_layout);
        this.lvSelectDevice = (ListView) view.findViewById(R.id.lv_select_device);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomViewEditText);
        if (typedArray != null) {
            String hintTextString = typedArray.getString(R.styleable.CustomViewEditText_hintText);
            int editBgResId = typedArray.getResourceId(R.styleable.CustomViewEditText_editBg, -1);
            boolean canEnter = typedArray.getBoolean(R.styleable.CustomViewEditText_canEnter, true);
            boolean showExpansionList = typedArray.getBoolean(R.styleable.CustomViewEditText_showExpansionList, false);

            if (canEnter) {
                mEditText.setVisibility(VISIBLE);
                mTextViewCode.setVisibility(GONE);
                mEditText.setHint(hintTextString);
                mEditText.setBackground(ContextCompat.getDrawable(context, editBgResId));
            } else {
                mEditText.setVisibility(GONE);
                mTextViewCode.setVisibility(VISIBLE);
                mTextViewCode.setTextColor(ContextCompat.getColor(getContext(), R.color.edit_hint_text_color));
                mTextViewCode.setText(hintTextString);
                mTextViewCode.setBackground(ContextCompat.getDrawable(context, editBgResId));
            }

            if (showExpansionList) {
                addLayoutViewGroup.setVisibility(VISIBLE);
            } else {
                addLayoutViewGroup.setVisibility(GONE);
            }

            typedArray.recycle();
        }
    }

//    //设置EditText可输入和不可输入状态
//    private void editTextable(EditText editText, boolean editable) {
//        if (!editable) {
//            // disable editing password
//            editText.setFocusable(false);
//            // user touches widget on phone with touch screen
//            editText.setFocusableInTouchMode(false);
//            // user navigates with wheel and selects widget
////            editText.setClickable(false);
//        } else {
//            // enable editing of password
//            editText.setFocusable(true);
//            editText.setFocusableInTouchMode(true);
//            editText.setClickable(true);
//        }
//    }


}
