package com.quansoon.facecamera.update;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.ui.LoginActivity;
import com.quansoon.facecamera.utils.ApkUtils;
import com.quansoon.facecamera.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @创建者 weifei
 * @创建时间 2016/7/22 11:36
 * @描述 app更新
 * @更新者 caoy
 * @更新时间
 * @更新描述 ${TODO}
 */
public class FileLoadUtils {
    public static final int TYPE_SUCCESS = 1;
    public static final int TYPE_FAIL = 2;

    private Context mContext;
    private String updateContent;

    private DownloadFileCallBack downlaodFileCallBack;
    private File updateFile;


    public FileLoadUtils(Context context, DownloadFileCallBack fileCallback) {
        mContext = context;
        this.downlaodFileCallBack = fileCallback;
    }

    /**
     * 目标文件存储的文件夹路径
     */
//    private String destFileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File
//            .separator + "DCB_DEFAULT_DIR";
    //    private String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    private String path = Environment.getExternalStoragePublicDirectory(Environment
            .DIRECTORY_DOWNLOADS).getPath();

    /**
     * 目标文件存储的文件名
     */
    private String destFileName = ApkUtils.getAppName()+"-"+ApkUtils.isApkInDebug()+".apk";
//    private String destFileName = "http://download.zhiguanzhuang.com/tv/25/electronicscreen-release.apk";



    public void loadFileAsy(Context context, String loadURL) {
        showLoadPross();
        new FileUpdateAsync().execute(loadURL);
    }

    public class FileUpdateAsync extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            return downLoad(strings[0], this);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            progressBar.setProgress(progress);
            System.out.println("===请求中===");
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case TYPE_SUCCESS:
                    System.out.println("===请求成功===");
                    // 安装软件
                    downlaodFileCallBack.fileCallBack(updateFile);
                    dialog.dismiss();
                    break;
                case TYPE_FAIL:
                    System.out.println("===请求失败===");
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }


        private int downLoad(String url, FileUpdateAsync fileUpdateAsync) {
            InputStream is = null;
            long totalSize;
//            url = url +"/"+ destFileName;
            url = url.toLowerCase();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = null;

            try {
                response = client.newCall(request).execute();
                if (response != null && response.code() == 200) {
//                    if (response.code() == 200){
                    byte[] buf = new byte[4096];
                    int len = 0;
                    FileOutputStream fos = null;
                    try {
                        totalSize = response.body().contentLength();
                        if (totalSize == 0) {
                            return TYPE_FAIL;
                        }

                        int downloadSize = 0;
                        //判断内存是否够用
                        updateFile = FilesUtils.MemoryAvailable(totalSize, destFileName);
                        if (updateFile.length() == totalSize && updateFile.getName().equals(destFileName)) {
                            ((LoginActivity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.shortShowStr(mContext, "有apk文件直接安装");
                                }
                            });
                            return TYPE_SUCCESS;
                        } else {
                            updateFile.delete();
                            updateFile.createNewFile();
                        }

                        if (updateFile != null) {
                            is = response.body().byteStream();
                            fos = new FileOutputStream(updateFile, true);

                            if (fos == null) {
                                return TYPE_FAIL;
                            }

                            while ((len = is.read(buf)) != -1) {
                                downloadSize += len;
                                fos.write(buf, 0, len);
                                int progress = (int) ((downloadSize) * 100 / totalSize);
                                publishProgress(progress);
                            }
                            fos.flush();
                            if (totalSize >= downloadSize) {
                                //进度条
                                return TYPE_SUCCESS;
                            } else {
                                return TYPE_FAIL;
                            }
                        } else {
                            return TYPE_FAIL;
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        return TYPE_FAIL;
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return TYPE_FAIL;
        }
    }



    /**
     * 设置更新内容
     *
     * @param updateContent
     */
    public void setContent(String updateContent) {
        this.updateContent = updateContent;
    }

    /**
     * 显示下载进度
     */
    Dialog dialog;
    TextView listView;
    ProgressBar progressBar;

    void showLoadPross() {
        if (dialog == null) {
            dialog = new Dialog(mContext, R.style.DialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadlayout);
            listView = dialog.findViewById(R.id.count);
            progressBar = dialog.findViewById(R.id.bar);
        }
        listView.setText(updateContent);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setContentView(R.item_datum_list.update_edit);
        // TextView updateContentTxt = (TextView) dialog.findViewById(R.id.content);
        //设置进度条风格，风格为圆形，旋转的
        //设置ProgressDialog 标题
        //设置ProgressDialog 提示信息
        //updateContentTxt.setText(updateContent);
        //设置ProgressDialog 标题图标
        //设置ProgressDialog的最大进度
        //设置ProgressDialog 的一个Button
//                dialog.setButton("确定", new ProgressDialog.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
        //设置ProgressDialog 是否可以按退回按键取消
        //显示
        //设置ProgressDialog的当前进度
        progressBar.setProgress(0);
        dialog.show();
    }


}
