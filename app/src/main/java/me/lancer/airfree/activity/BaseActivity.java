package me.lancer.airfree.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.airfree.util.FileTrans;
import tyrantgit.explosionfield.ExplosionField;

public class BaseActivity extends Activity {

    ApplicationUtil app;

    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        app = (ApplicationUtil) this.getApplication();
    }

    public void ShowToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), text,
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });
        }
    }

    public void ShowToast(final int resId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId,
                            Toast.LENGTH_LONG);
                } else {
                    mToast.setText(resId);
                }
                mToast.show();
            }
        });
    }

    public void sendMessage(String command, String parameter) {
        if (app.getmPrintWriterClient() != null) {
            try {
                JSONObject jb = new JSONObject();
                jb.put("command", command);
                jb.put("parameter", parameter);
                Log.e("IP & PORT", "发送成功:" + jb.toString());
                app.getmPrintWriterClient().print(jb.toString() + "\n");
                app.getmPrintWriterClient().flush();
            } catch (Exception e) {
                Log.e("IP & PORT", "发送异常:" + e.getMessage());
                ShowToast("发送异常:" + e.getMessage());
            }
        }
    }

    public String getInfoBuff(char[] buff, int count) {
        char[] temp = new char[count];
        for (int i = 0; i < count; i++) {
            temp[i] = buff[i];
        }
        return new String(temp);
    }

    class SendTask extends AsyncTask<Object, Void, String> {
        private String ip, port, filename;
        private ProgressDialog mProgressDialog;
        private Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.e("IP & PORT", "进度:" + msg.what);
                mProgressDialog.setProgress(msg.what);
            }
        };

        protected SendTask(String ip, String port, String filename) {
            super();
            this.ip = ip;
            this.port = port;
            this.filename = filename;
            this.mProgressDialog = new ProgressDialog(BaseActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setTitle(null);
        }

        protected void onPreExecute() {
            mProgressDialog.show();
            Log.e("IP & PORT", "开始发送!");
        }

        protected void onPostExecute(String failed) {
            mProgressDialog.dismiss();
            if (failed == null) {
                ShowToast("发送成功:" + filename);
                Log.e("IP & PORT", "发送成功!");
            } else {
                ShowToast("发送失败:" + filename);
                Log.e("IP & PORT", "发送失败:" + failed);
            }
        }

        protected String doInBackground(final Object... args) {
            String success = null;
            try {
                int port_ = Integer.parseInt(port);
                FileTrans.doWrite(ip, port_, filename, mHandler);
            } catch (Exception e) {
                success = e.toString();
            }
            return success;
        }
    }

    class ReadTask extends AsyncTask<Object, Void, String> {
        private String ip, port, filename;
        private ProgressDialog mProgressDialog;
        private ImageView iv;

        protected ReadTask(String ip, String port, String filename) {
            super();
            this.ip = ip;
            this.port = port;
            this.filename = filename;
        }

        protected ReadTask(String ip, String port, String filename, ImageView iv) {
            super();
            this.ip = ip;
            this.port = port;
            this.filename = filename;
            this.iv = iv;
        }

        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(BaseActivity.this, null, "正在接收...");
            Log.e("IP & PORT", "开始接收");
        }

        protected void onPostExecute(String failed) {
            mProgressDialog.dismiss();
            if (failed == null) {
                if (iv != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(filename);
                    iv.setImageBitmap(bitmap);
                } else {
                    ShowToast("接收成功: " + filename);
                    Log.e("IP & PORT", "接收成功");
                }
            } else {
                ShowToast("接收失败: " + filename);
                Log.e("IP & PORT", "接收失败" + failed);
            }
        }

        protected String doInBackground(final Object... args) {
            String success = null;
            try {
                int port_ = Integer.parseInt(port);
                FileTrans.doRead(port_, filename);
            } catch (Exception e) {
                success = e.toString();
            }
            return success;
        }
    }
}
