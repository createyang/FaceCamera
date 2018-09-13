package com.quansoon.facecamera.utils;

import android.content.Context;
import android.widget.Toast;


public class ToastUtils {
    private static Toast toast;
    /**
     * 短提示 by xlq
     *
     * @param context
     * @param content
     */
    public static void shortShowStr(Context context, String content)
    {
        if(content == null){
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context,content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    /**
     * 常提示 by xlq
     *
     * @param context
     * @param content
     */
    public static void longShowStr(Context context, String content)
    {
        if(content == null){
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context,content, Toast.LENGTH_LONG);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
