package com.example.administrator.attendanceinputv3.widget;

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

import com.example.administrator.attendanceinputv3.R;

/**
 * @author: Administrator
 * @created on: 2018/8/31 18:51
 * @description:
 */
public class CustomEditText extends RelativeLayout {

    private ListView lvSelectDevice;

    public ListView getLvSelectDevice() {
        return lvSelectDevice;
    }

    public LinearLayout getAddLayoutViewGroup() {
        return addLayoutViewGroup;
    }

    private LinearLayout addLayoutViewGroup;

    public RelativeLayout getmEditLayout() {
        return mEditLayout;
    }

    public EditText getmEditText() {
        return mEditText;
    }

    private RelativeLayout mEditLayout;
    private EditText mEditText;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_edit_text, this, true);
        mEditLayout = (RelativeLayout) view.findViewById(R.id.edit_layout);
        mEditText = (EditText) view.findViewById(R.id.edit_code);
        addLayoutViewGroup = (LinearLayout) view.findViewById(R.id.add_layout);
        lvSelectDevice = (ListView) view.findViewById(R.id.lv_select_device);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText);
        if (typedArray != null) {

            String hintTextString = typedArray.getString(R.styleable.CustomEditText_hintText);
            int editBgResId = typedArray.getResourceId(R.styleable.CustomEditText_editBg, R.mipmap.input_01);
            boolean canEnter = typedArray.getBoolean(R.styleable.CustomEditText_canEnter, true);
            boolean showExpansionList = typedArray.getBoolean(R.styleable.CustomEditText_showExpansionList, false);

            mEditLayout.setBackground(ContextCompat.getDrawable(context, editBgResId));
            mEditText.setHint(hintTextString);
            if (canEnter) {
                editTextable(mEditText, true);
            } else {
                editTextable(mEditText, false);
            }
            if (showExpansionList) {
                addLayoutViewGroup.setVisibility(VISIBLE);
            } else {
                addLayoutViewGroup.setVisibility(GONE);
            }

            typedArray.recycle();
        }
    }

    //设置EditText可输入和不可输入状态
    private void editTextable(EditText editText, boolean editable) {
        if (!editable) {
            // disable editing password
            editText.setFocusable(false);
            // user touches widget on phone with touch screen
            editText.setFocusableInTouchMode(false);
            // user navigates with wheel and selects widget
//            editText.setClickable(false);
        } else {
            // enable editing of password
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setClickable(true);
        }
    }


}
