package com.quansoon.facecamera.update;

import android.os.Environment;

import com.quansoon.facecamera.base.BaseApplication;
import com.quansoon.facecamera.utils.ApkUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2018/8/29.
 */

public class FilesUtils {

    /**
     * 可用内存大小
     *
     * @param fileSize
     * @return
     */
    public static File MemoryAvailable(long fileSize, String fileName) {
        fileSize += (1024 << 10);
        File file = null;
        if (MemoryStatus.externalMemoryAvailable()) {
            if ((MemoryStatus.getAvailableExternalMemorySize() <= fileSize)) {
                if ((MemoryStatus.getAvailableInternalMemorySize() > fileSize)) {
                    file = createFile(false, fileName);
                }
            } else {
                file = createFile(true, fileName);
            }
        } else {
            if (MemoryStatus.getAvailableInternalMemorySize() >= fileSize) {
                file = createFile(false, fileName);
            }
        }

        if (file != null) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                if (fileInputStream == null) {
                    file = createFile(false, fileName);
                }
            } catch (FileNotFoundException e) {
                file = createFile(false, fileName);
                e.printStackTrace();
            }
        }

        return file;
    }


    /**
     * 创建file文件
     *
     * @param sd_available sdcard是否可用
     */
    private static File createFile(boolean sd_available, String fileName) {
        File updateDir = null;
        File updateFile = null;

        if (sd_available) {
            updateDir = new File(Environment.getExternalStorageDirectory(), ApkUtils.updateDownloadDir);
        } else {
            updateDir = BaseApplication.getContext().getFilesDir();
        }
        updateFile = new File(updateDir.getPath(), fileName);
        if (!updateDir.exists()) {
            updateDir.mkdirs();
        }

        if (!updateFile.exists()) {
            try {
                updateFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (!sd_available) {
                updateFile.delete();
                try {
                    updateFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return updateFile;
    }

    /**
     * 获取下载进度
     *
     * @param downSize
     * @param allSize
     * @return
     */
    public static String getMsgSpeed(long downSize, long allSize) {
        StringBuffer sBuf = new StringBuffer();
        sBuf.append(getSize(downSize));
        sBuf.append("/");
        sBuf.append(getSize(allSize));
        sBuf.append(" ");
        sBuf.append(getPercentSize(downSize, allSize));
        return sBuf.toString();
    }

    /**
     * 获取大小
     *
     * @param size
     * @return
     */
    private static final float SIZE_BT = 1024L; // BT字节参考量
    private static final float SIZE_KB = SIZE_BT * 1024.0f; // KB字节参考量
    private static final float SIZE_MB = SIZE_KB * 1024.0f;// MB字节参考量

    public static String getSize(long size) {
        if (size >= 0 && size < SIZE_BT) {
            return (double) (Math.round(size * 10) / 10.0) + "B";
        } else if (size >= SIZE_BT && size < SIZE_KB) {
            return (double) (Math.round((size / SIZE_BT) * 10) / 10.0) + "KB";
        } else if (size >= SIZE_KB && size < SIZE_MB) {
            return (double) (Math.round((size / SIZE_KB) * 10) / 10.0) + "MB";
        }
        return "";
    }

    /**
     * 获取到当前的下载百分比
     *
     * @param downSize 下载大小
     * @param allSize  总共大小
     * @return
     */
    public static String getPercentSize(long downSize, long allSize) {
        String percent = (allSize == 0 ? "0.0" : new DecimalFormat("0.0")
                .format((double) downSize / (double) allSize * 100));
        return "(" + percent + "%)";
    }

    /**
     * 判断外置sd卡是否可用
     *
     * @return
     */
    public static boolean isExternalCanWriten() {
        File sd = Environment.getExternalStorageDirectory();
        return sd.canWrite();
    }

}
