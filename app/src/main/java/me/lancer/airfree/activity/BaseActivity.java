package me.lancer.airfree.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.airfree.util.FileTransUtil;
import me.lancer.distance.R;

public class BaseActivity extends Activity {

    ApplicationUtil app;

    Toast mToast;

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strSendSucceeded = "";
    private String strSendFailed = "";
    private String strReceiveSucceeded = "";
    private String strReceiveFailed = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        baseLanguage();
        app = (ApplicationUtil) this.getApplication();
    }

    public void baseLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strSendSucceeded = getResources().getString(R.string.send_succeeded_zn);
            strSendFailed = getResources().getString(R.string.send_failed_zn);
            strReceiveSucceeded = getResources().getString(R.string.receive_succeeded_zn);
            strReceiveFailed = getResources().getString(R.string.receive_failed_zn);
        } else if (language.equals("en")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_en);
            strNoConnection = getResources().getString(R.string.no_connection_en);
            strConnectionFailed = getResources().getString(R.string.connection_failed_en);
            strSendSucceeded = getResources().getString(R.string.send_succeeded_en);
            strSendFailed = getResources().getString(R.string.send_failed_en);
            strReceiveSucceeded = getResources().getString(R.string.receive_succeeded_en);
            strReceiveFailed = getResources().getString(R.string.receive_failed_en);
        }
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
                if (jb.toString() != null) {
                    app.getmPrintWriterClient().print(jb.toString() + "\n");
                    app.getmPrintWriterClient().flush();
                    Log.e("IP & PORT", strSendSucceeded + jb.toString());
                }
            } catch (Exception e) {
                Log.e("IP & PORT", strSendFailed + e.getMessage());
                ShowToast(strSendFailed + e.getMessage());
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
        private Handler sHandler = new Handler() {

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
                ShowToast(getResources().getString(R.string.send_succeeded_zn)+ filename);
                Log.e("IP & PORT", strSendSucceeded);
            } else {
                ShowToast(getResources().getString(R.string.send_failed_zn) + filename);
                Log.e("IP & PORT", strSendFailed + failed);
            }
        }

        protected String doInBackground(final Object... args) {
            String success = null;
            try {
                int port_ = Integer.parseInt(port);
                FileTransUtil.doWrite(ip, port_, filename, sHandler);
            } catch (Exception e) {
                success = e.toString();
            }
            return success;
        }
    }

    class ReadTask extends AsyncTask<Object, Void, String> {
        private String ip, port, filename;
        private ImageView iv;
        private ProgressDialog mProgressDialog;
        private Handler rHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.e("IP & PORT", "进度:" + msg.what);
                mProgressDialog.setProgress(msg.what);
            }
        };

        protected ReadTask(String ip, String port, String filename) {
            super();
            this.ip = ip;
            this.port = port;
            this.filename = filename;
            this.mProgressDialog = new ProgressDialog(BaseActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setTitle(null);
        }

        protected ReadTask(String ip, String port, String filename, ImageView iv) {
            super();
            this.ip = ip;
            this.port = port;
            this.filename = filename;
            this.iv = iv;
            this.mProgressDialog = new ProgressDialog(BaseActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setTitle(null);
        }

        protected void onPreExecute() {
            mProgressDialog.show();
            Log.e("IP & PORT", "开始接收!");
        }

        protected void onPostExecute(String failed) {
            mProgressDialog.dismiss();
            if (failed == null) {
                if (iv != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(filename);
                    iv.setImageBitmap(bitmap);
                } else {
                    ShowToast(strReceiveSucceeded+ filename);
                    Log.e("IP & PORT", strReceiveSucceeded);
                }
            } else {
                ShowToast(strReceiveFailed + filename);
                Log.e("IP & PORT", strReceiveFailed + failed);
            }
        }

        protected String doInBackground(final Object... args) {
            String success = null;
            try {
                int port_ = Integer.parseInt(port);
                FileTransUtil.doRead(port_, filename, rHandler);
            } catch (Exception e) {
                success = e.toString();
            }
            return success;
        }
    }
}
