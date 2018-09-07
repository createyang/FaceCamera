package com.example.administrator.attendanceinputv3.ui;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.attendanceinputv3.R;
import com.example.administrator.attendanceinputv3.adapter.FaceDeviceAdapter;
import com.example.administrator.attendanceinputv3.base.BaseActivity;
import com.example.administrator.attendanceinputv3.model.FaceDeviceBean;
import com.example.administrator.attendanceinputv3.presenter.impl.LoginPresenterImpl;
import com.example.administrator.attendanceinputv3.utils.LogUtils;
import com.example.administrator.attendanceinputv3.utils.StringUtils;
import com.example.administrator.attendanceinputv3.utils.ToastUtils;
import com.example.administrator.attendanceinputv3.view.LoginView;
import com.example.administrator.attendanceinputv3.widget.CustomViewEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Administrator
 * @created on: 2018/8/31 15:23
 * @description:
 */

public class LoginActivity extends BaseActivity implements LoginView {

    private CustomViewEditText mCusEditProjectCode;
    private CustomViewEditText mCusSelectAttendanceDevice;

    /**
     * 设置完成
     */
    private Button mBtnLogin;
    private LoginPresenterImpl loginPresenter;
    private EditText editTextProCode;
    private EditText editTextSelectDevice;
    private ListView lvSelectDevice;
    private ArrayList<FaceDeviceBean> faceDeviceBeanArrayList;
    private ArrayAdapter<FaceDeviceBean> faceDeviceBeanArrayAdapter;
    private LinearLayout addLayoutViewGroup;
    private FaceDeviceBean selectDeviceBean;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    public void intView() {
        mCusEditProjectCode = (CustomViewEditText) findViewById(R.id.cus_edit_project_code);
        mCusSelectAttendanceDevice = (CustomViewEditText) findViewById(R.id.cus_select_attendance_device);
        mBtnLogin = (Button) findViewById(R.id.btn_login);

        editTextProCode = mCusEditProjectCode.getmEditText();
        editTextSelectDevice = mCusSelectAttendanceDevice.getmEditText();
        addLayoutViewGroup = mCusSelectAttendanceDevice.getAddLayoutViewGroup();
        lvSelectDevice = mCusSelectAttendanceDevice.getLvSelectDevice();
        addLayoutViewGroup.setVisibility(View.GONE);
        initListener();
    }

    private void initListener() {
        //edit输入法确认键监听
        if (editTextProCode != null) {
//            editTextProCode.setImeOptions(EditorInfo.IME_ACTION_SEND);
            editTextProCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                    LogUtils.d("editTextProCode actionId:" + actionId + "");
//                    actionSend
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }

                    ToastUtils.shortShowStr(LoginActivity.this, "GoToSearch");
                    goToSearchDevice(v.getText().toString());
                    addLayoutViewGroup.setVisibility(View.GONE);

                    return true;
                }
            });
        }

        //登录键监听
        if (mBtnLogin != null) {
            mBtnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //登录
                    ToastUtils.shortShowStr(LoginActivity.this, "btn_login");

                    String ipAddress = editTextSelectDevice.getText().toString();
                    String faceIp = selectDeviceBean.getFaceIp();

                    if (StringUtils.isEmpty(ipAddress)) {
                        ToastUtils.shortShowStr(LoginActivity.this, getString(R.string.attendance_device_no_found));
                        return;
                    }
                    if (StringUtils.isEmpty(faceIp)) {
                        ToastUtils.shortShowStr(LoginActivity.this, getString(R.string.attendance_device_ip_no_found));
                        return;
                    }

//                loginPresenter.loginToAttendance(ipAddress);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("ip", faceIp);
                    startActivity(intent);
//                finish();

                }
            });
        }

        //listView 条目选择键监听
        lvSelectDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.shortShowStr(LoginActivity.this, "position" + position);
                if (faceDeviceBeanArrayAdapter != null) {
                    selectDeviceBean = faceDeviceBeanArrayAdapter.getItem(position);
                    editTextSelectDevice.setText(selectDeviceBean != null ? selectDeviceBean.getDeviceAddress() : "");
                    addLayoutViewGroup.setVisibility(View.GONE);
                }
            }
        });

        //选择考勤的点击监听
        editTextSelectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.shortShowStr(LoginActivity.this, "mCusSelectAttendanceDevice " + "选择考勤的点击监听");
                if (faceDeviceBeanArrayAdapter != null && faceDeviceBeanArrayAdapter.getCount() != 0) {
                    addLayoutViewGroup.setVisibility(View.VISIBLE);
                } else {
                    addLayoutViewGroup.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void intData() {
        loginPresenter = new LoginPresenterImpl(this);

//        ArrayAdapter<FaceDeviceBean> faceDeviceBeanArrayAdapter = new ArrayAdapter<>(this, R.layout.item_face_device);
        faceDeviceBeanArrayList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            FaceDeviceBean faceDeviceBean = new FaceDeviceBean();
//            faceDeviceBean.setDeviceAddress("DWASK00" + i);
//            faceDeviceBeanArrayList.add(faceDeviceBean);
//        }

        faceDeviceBeanArrayAdapter = new FaceDeviceAdapter(this, faceDeviceBeanArrayList);
        lvSelectDevice.setAdapter(faceDeviceBeanArrayAdapter);

    }


    private void goToSearchDevice(String codeStr) {
        if (loginPresenter != null) {
            loginPresenter.checkProjectCode(codeStr);
        }
    }


    @Override
    public void onGetDeviceFailure(String errorMessage) {
        addLayoutViewGroup.setVisibility(View.GONE);
    }

    @Override
    public void onGetDeviceSucceed(List<FaceDeviceBean> faceDeviceBeans) {
        faceDeviceBeanArrayAdapter.clear();
        addLayoutViewGroup.setVisibility(View.VISIBLE);
        faceDeviceBeanArrayAdapter.addAll(faceDeviceBeans);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_ENTER:     //确定键enter
            case KeyEvent.KEYCODE_DPAD_CENTER:
                editTextProCode.setFocusable(true);
                editTextProCode.setFocusableInTouchMode(true);
                editTextProCode.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) editTextProCode.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editTextProCode, 0);
                break;

            case KeyEvent.KEYCODE_BACK:    //返回键

                return true;   //这里由于break会退出，所以我们自己要处理掉 不返回上一层

            case KeyEvent.KEYCODE_SETTINGS: //设置键

                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:   //向下键

                /*    实际开发中有时候会触发两次，所以要判断一下按下时触发 ，松开按键时不触发
                 *    exp:KeyEvent.ACTION_UP
                 */
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                }

                break;

            case KeyEvent.KEYCODE_DPAD_UP:   //向上键

                break;

            case KeyEvent.KEYCODE_0:   //数字键0

                break;

            case KeyEvent.KEYCODE_DPAD_LEFT: //向左键

                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:  //向右键

                break;

            case KeyEvent.KEYCODE_INFO:    //info键

                break;

            case KeyEvent.KEYCODE_PAGE_DOWN:     //向上翻页键
            case KeyEvent.KEYCODE_MEDIA_NEXT:

                break;


            case KeyEvent.KEYCODE_PAGE_UP:     //向下翻页键
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:

                break;

            case KeyEvent.KEYCODE_VOLUME_UP:   //调大声音键

                break;

            case KeyEvent.KEYCODE_VOLUME_DOWN: //降低声音键

                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
