package com.quansoon.facecamera.constant;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.quansoon.facecamera.BuildConfig;
import com.quansoon.facecamera.base.BaseApplication;

/**
 * @author Caoy
 */
public class Constants {

    /**
     * 正式布
     */
//    public static String IP_TEST = "http://172.21.2.113:8080/client/face";
//    public static String IP_TEST_COMOM = "http://172.21.2.113:8080/client/view";
    /**
     * 测试
     */
    public static String IP_TEST = "http://120.77.201.123:8088/client/face";
    //    public static String IP_TEST = "http://120.77.201.123:8088/client/face";
    //    public static String IP_TEST = "http://172.21.2.113:8080/client/face";
    public static String IP_TEST_COMOM = "http://120.77.201.123:8089/client/view";

    public static final String CHECK_RESULTS_CODE = "000000";

    /**
     * 是否打印日志的开关  true为调试状态  false为正式发布环境
     */
    public static final boolean IS_DEBUG = isDebug();

    /**
     * 判断是debug还是release
     *
     * @return
     */
    private static boolean isDebug() {
        return BaseApplication.getInstance() != null &&
                (BaseApplication.getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }


    /**
     * Database
     */
    public class Database {
        public static final String DATABASE_NAME = "quanroon.db";
    }

    /**
     * putExtra
     */
    public class Extra {
        public static final String IP = "ip";
        public static final String TITLE = "title";
    }

    /**
     * sharedPreferences
     */
    public class SP {
        public static final String NAME_LOGIN_STATE = "quanroon_login_state.xml";
        public static final String KEY_LOGIN_STATE = "SP_LOGIN_STATE";
        public static final String KEY_OUT_TIME = "KEY_OUT_TIME";
        public static final String KEY_LOGIN_IP = "KEY_LOGIN_IP";
        public static final String KEY_LOGIN_TITLE = "KEY_LOGIN_TITLE";
    }
}
