package com.quansoon.facecamera.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.adapter.FaceDeviceAdapter;
import com.quansoon.facecamera.base.BaseActivity;
import com.quansoon.facecamera.constant.ResultCode;
import com.quansoon.facecamera.model.FaceDeviceBean;
import com.quansoon.facecamera.model.VersionUpdateBean;
import com.quansoon.facecamera.network.NetChangeObserver;
import com.quansoon.facecamera.network.NetStateReceiver;
import com.quansoon.facecamera.presenter.impl.LoginPresenterImpl;
import com.quansoon.facecamera.update.DownloadFileCallBack;
import com.quansoon.facecamera.update.FileLoadUtils;
import com.quansoon.facecamera.update.FilesUtils;
import com.quansoon.facecamera.utils.AnimationUtil;
import com.quansoon.facecamera.utils.ApkUtils;
import com.quansoon.facecamera.utils.LogUtils;
import com.quansoon.facecamera.utils.StringUtils;
import com.quansoon.facecamera.utils.ToastUtils;
import com.quansoon.facecamera.utils.UiUtil;
import com.quansoon.facecamera.view.LoginView;
import com.quansoon.facecamera.widget.CustomViewEditText;
import com.quansoon.facecamera.widget.CustomViewTimeDate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Administrator
 * @created on: 2018/8/31 15:23
 * @description:
 */

public class LoginActivity extends BaseActivity implements LoginView, View.OnKeyListener, View.OnFocusChangeListener, View.OnClickListener {

    private CustomViewEditText mCusEditProjectCode;
    private CustomViewEditText mCusSelectAttendanceDevice;

    String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    /**
     * 设置完成
     */
    private Button mBtnLogin;
    private LoginPresenterImpl loginPresenter;
    private EditText editTextProCode;
    private TextView editTextSelectDevice;
    private ListView lvSelectDevice;
    private ArrayList<FaceDeviceBean> faceDeviceBeanArrayList;
    private ArrayAdapter<FaceDeviceBean> faceDeviceBeanArrayAdapter;
    private LinearLayout addLayoutViewGroup;
    private FaceDeviceBean selectDeviceBean;
    private CustomViewTimeDate customViewTimeDate;
    private Dialog lDialog;
    private String downloadUrl;
    private FileLoadUtils fileLoadUtils;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    public void intView() {
        //输入code
        mCusEditProjectCode = (CustomViewEditText) findViewById(R.id.cus_edit_project_code);
        editTextProCode = mCusEditProjectCode.getmEditText();
        //自定义选择设备
        mCusSelectAttendanceDevice = (CustomViewEditText) findViewById(R.id.cus_select_attendance_device);
        editTextSelectDevice = mCusSelectAttendanceDevice.getmTextViewCode();
        addLayoutViewGroup = mCusSelectAttendanceDevice.getAddLayoutViewGroup();
        addLayoutViewGroup.setVisibility(View.GONE);
        lvSelectDevice = mCusSelectAttendanceDevice.getLvSelectDevice();
        //登录
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        //时钟
        customViewTimeDate = (CustomViewTimeDate) findViewById(R.id.cv_time_date);

        /*控制按键下一步的指向*/


    }

    /**
     * 判断权限
     *
     * @return
     */
    private Boolean requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, ResultCode.PERMISSIONS_REQUEST);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ResultCode.PERMISSIONS_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //有权限了才去下载
                    getGetAppVersion();
                } else {
                    ToastUtils.shortShowStr(this, "没有开启存取权限");
                }
                break;
            default:
                break;
        }
    }


    private void initListener() {

        /**
         * edit输入法确认键监听
         */
        if (editTextProCode != null) {
            editTextProCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    /*隐藏软键盘*/
                    hideKeyboard(v);
                    goToSearchDevice(v.getText().toString());
                    return true;
                }
            });

            //输入设备号的遥控监听
            editTextProCode.setOnFocusChangeListener(this);
            editTextProCode.setOnKeyListener(this);
        }

        /**
         * 登录键监听
         */
        if (mBtnLogin != null) {
            mBtnLogin.setOnClickListener(this);
            mBtnLogin.setOnKeyListener(this);
            mBtnLogin.setOnFocusChangeListener(this);
        }


        //选择考勤的点击监听
        if (editTextSelectDevice != null) {
            editTextSelectDevice.setOnClickListener(this);
            editTextSelectDevice.setOnFocusChangeListener(this);
            //选择考勤的遥控监听
            editTextSelectDevice.setOnKeyListener(this);
        }


        //listView 条目选择键监听
        lvSelectDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.shortShowStr(LoginActivity.this, "position" + position);
                if (faceDeviceBeanArrayAdapter != null) {
                    selectDeviceBean = faceDeviceBeanArrayAdapter.getItem(position);
                    editTextSelectDevice.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.edit_text_color));
                    editTextSelectDevice.setText(selectDeviceBean != null ? selectDeviceBean.getDeviceAddress() : "");
                    addLayoutViewGroup.setVisibility(View.GONE);


                }
            }
        });
    }

    /**
     * 检查并登陆
     */
    private void checkAndLogin() {
        //登录
        ToastUtils.shortShowStr(LoginActivity.this, "btn_login");
        String ipAddress = editTextSelectDevice.getText().toString();
        String faceIp = "";
        if (selectDeviceBean != null) {
            faceIp = selectDeviceBean.getFaceIp();
        } else {
            ToastUtils.shortShowStr(this, "未获取到设备信息");
        }
        if (StringUtils.isEmpty(ipAddress)) {
            ToastUtils.shortShowStr(LoginActivity.this, getString(R.string.attendance_device_no_found));
            return;
        }
        if (StringUtils.isEmpty(faceIp)) {
            ToastUtils.shortShowStr(LoginActivity.this, getString(R.string.attendance_device_ip_no_found));
            return;
        }
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("ip", faceIp);
        startActivity(intent);
//                finish();
    }

    /**
     * 隐藏键盘
     *
     * @param v
     */
    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v
                .getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(
                    v.getApplicationWindowToken(), 0);
        }
    }


    @Override
    public void intData() {
        loginPresenter = new LoginPresenterImpl(this);
        faceDeviceBeanArrayList = new ArrayList<>();
        faceDeviceBeanArrayAdapter = new FaceDeviceAdapter(this, faceDeviceBeanArrayList);
        lvSelectDevice.setAdapter(faceDeviceBeanArrayAdapter);

        initUpdate();
        initListener();
    }

    /**
     * 初始化升级
     */
    private void initUpdate() {
        /**
         * 检查网络
         */
        NetStateReceiver.registerObserver(new NetChangeObserver() {
            @Override
            public void onNetConnected(int type) {
                ToastUtils.shortShowStr(LoginActivity.this, "网络连接");
                customViewTimeDate.requestUpdateDate();
            }

            @Override
            public void onNetDisConnect() {
                ToastUtils.shortShowStr(LoginActivity.this, getString(R.string.network_available));
            }
        });

        /**
         * 检查权限更新
         */
        if (requestPermission() || FilesUtils.isExternalCanWriten()) {
            getGetAppVersion();
        }

        fileLoadUtils = new FileLoadUtils(this, new DownloadFileCallBack() {
            @Override
            public void fileCallBack(File file) {
                //安装
//                ToastUtils.longShowStr(BaseApplication.getContext(), file.getAbsolutePath());
                installApk(file);
            }
        });
    }

    /**
     * 安装apk
     *
     * @param file
     */
    private void installApk(File file) {
        if (checkDataPermissions(file)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                String authority = getPackageName() + ".provider";
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(this, authority, file);
                install.setDataAndType(contentUri, "application/vnd.android.package-archive");
                // 执行意图进行安装
                startActivity(install);
            } else {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivity(i);
            }
        }
    }

    /**
     * 检查sd读写权限
     *
     * @param file
     * @return
     */
    private boolean checkDataPermissions(File file) {
        try {
            Process p = Runtime.getRuntime().exec("chmod 666 " + file);
            int status = p.waitFor();
            if (status == 0) {
                ToastUtils.shortShowStr(this, "权限修改成功");
                return true;
            } else {
                ToastUtils.shortShowStr(this, "权限修改失败");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 搜索设备
     *
     * @param codeStr
     */
    private void goToSearchDevice(String codeStr) {
        if (StringUtils.isEmpty(codeStr)) {
            ToastUtils.shortShowStr(this, getString(R.string.please_enter_project_code));
            return;
        }
        if (loginPresenter != null) {
            loginPresenter.checkDeviceCode(codeStr);
        }
    }

    /**
     * 获取设备信息失败
     *
     * @param errorMessage
     */
    @Override
    public void onGetDeviceFailure(String errorMessage) {
        ToastUtils.shortShowStr(LoginActivity.this, "获取设备Code失败");
        addLayoutViewGroup.setVisibility(View.GONE);
    }

    /**
     * 获取设备信息成功
     *
     * @param faceDeviceBeans
     */
    @Override
    public void onGetDeviceSucceed(List<FaceDeviceBean> faceDeviceBeans) {
        ToastUtils.shortShowStr(LoginActivity.this, "获取设备Code成功");
        faceDeviceBeanArrayAdapter.clear();
        addLayoutViewGroup.setVisibility(View.VISIBLE);
        faceDeviceBeanArrayAdapter.addAll(faceDeviceBeans);
    }

    /**
     * 获取app的版本信息
     */
    void getGetAppVersion() {
        UiUtil.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loginPresenter.checkAppUpdate();
            }
        }, 1000);
    }

    /**
     * 检查升级失败
     *
     * @param message
     */
    @Override
    public void onCheckUpdateFailure(String message) {
        ToastUtils.shortShowStr(LoginActivity.this, "升级检查失败");
    }

    /**
     * 检查升级成功
     *
     * @param data
     */
    @Override
    public void onCheckUpdateSuccess(VersionUpdateBean data) {
        ToastUtils.shortShowStr(LoginActivity.this, "升级检查成功");
        if (data != null && data.getVersionNo() != 0 &&
                data.getVersionNo() > ApkUtils.getVersionNumber()) {
            fileLoadUtils.setContent(data.getDescription());
            downloadUrl = data.getDloadUrl();
            initDialog();
            lDialog.show();
        }
    }

    private void initDialog() {
        if (lDialog == null) {
            lDialog = new Dialog(this, R.style.DialogTheme);
        }
        lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lDialog.setContentView(R.layout.dialog_show_cancel_edit);
        TextView okText = (TextView) lDialog.findViewById(R.id.iBtSure);
        TextView cancelText = (TextView) lDialog.findViewById(R.id.iBtCancel);

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lDialog.dismiss();

            }
        });

        okText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lDialog.dismiss();
                fileLoadUtils.loadFileAsy(LoginActivity.this, downloadUrl);
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            //确定键enter
            case KeyEvent.KEYCODE_ENTER:
                //中心键
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //让输入的editText获取焦点
                editTextProCode.setFocusable(true);
                editTextProCode.setFocusableInTouchMode(true);
                editTextProCode.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) editTextProCode.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editTextProCode, 0);
                break;
            //返回键 //这里由于break会退出，所以我们自己要处理掉 不返回上一层
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
//            //设置键
//            case KeyEvent.KEYCODE_SETTINGS:
//                break;
//            //向下键
//            case KeyEvent.KEYCODE_DPAD_DOWN:
//                break;
//            //向上键
//            case KeyEvent.KEYCODE_DPAD_UP:
//
//                break;
//            //数字键0
//            case KeyEvent.KEYCODE_0:
//
//                break;
//            //向左键
//            case KeyEvent.KEYCODE_DPAD_LEFT:
//
//                break;
//            //向右键
//            case KeyEvent.KEYCODE_DPAD_RIGHT:
//
//                break;
//            //info键
//            case KeyEvent.KEYCODE_INFO:
//
//                break;
//            //向上翻页键
//            case KeyEvent.KEYCODE_PAGE_DOWN:
//            case KeyEvent.KEYCODE_MEDIA_NEXT:
//                break;
//            //向下翻页键
//            case KeyEvent.KEYCODE_PAGE_UP:
//            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
//                break;
//            //调大声音键
//            case KeyEvent.KEYCODE_VOLUME_UP:
//                break;
//            //降低声音键
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v == editTextProCode) {
            if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                editTextSelectDevice.setFocusable(true);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                hideKeyboard(editTextSelectDevice);
                return false;
            }
        } else if (editTextSelectDevice == v) {
            if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                goToSearchDevice(editTextProCode.getText().toString());
                return true;
            }
        } else if (mBtnLogin == v) {
            if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                checkAndLogin();
                return true;
            }
        }

        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == editTextProCode) {
            if (hasFocus) {
                // 获取焦点时操作，常见的有放大、加边框等
                AnimationUtil.startScaleToBigAnimation(mCusEditProjectCode, 1.05f, null);
                LogUtils.d("editTextProCode" + hasFocus);
            } else {
                // 失去焦点时操作，恢复默认状态
                AnimationUtil.startScaleToSmallAnimation(mCusEditProjectCode, 1f, null);
                LogUtils.d("editTextProCode" + hasFocus);
            }
        } else if (editTextSelectDevice == v) {
            if (hasFocus) {
                // 获取焦点时操作，常见的有放大、加边框等
                AnimationUtil.startScaleToBigAnimation(mCusSelectAttendanceDevice, 1.05f, null);
                LogUtils.d("editTextProCode" + hasFocus);
            } else {
                // 失去焦点时操作，恢复默认状态
                AnimationUtil.startScaleToSmallAnimation(mCusSelectAttendanceDevice, 1f, null);
                LogUtils.d("editTextProCode" + hasFocus);
            }
        } else if (mBtnLogin == v) {
            if (hasFocus) {
                // 获取焦点时操作，常见的有放大、加边框等
                AnimationUtil.startScaleToBigAnimation(mBtnLogin, 1.05f, null);
                LogUtils.d("editTextProCode" + hasFocus);
            } else {
                // 失去焦点时操作，恢复默认状态
                AnimationUtil.startScaleToBigAnimation(mBtnLogin, 1.05f, null);
                LogUtils.d("editTextProCode" + hasFocus);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == editTextProCode) {

        } else if (editTextSelectDevice == v) {
            ToastUtils.shortShowStr(LoginActivity.this, "mCusSelectAttendanceDevice " + "选择考勤的点击监听");
            goToSearchDevice(editTextProCode.getText().toString());
        } else if (mBtnLogin == v) {
            checkAndLogin();
        }
    }
}
