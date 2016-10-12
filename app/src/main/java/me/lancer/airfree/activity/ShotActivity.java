package me.lancer.airfree.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class ShotActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;

    private TextView tvShow, tvScreenShot, tvRemoteDesktop;
    private Button btnBack;
    private ImageView ivShow;
    private LinearLayout btnScreenShot, btnRemoteDesktop;

    final int POWEREVENTF_SCREENSHOT = 0x0001;    //截屏

    private String filename;
    //    private Thread mThreadClient = null;
    private String recvMessageClient = "";
    private boolean iStop = false;
    private boolean iShot = false;

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strShow = "";
    private String strScreenCapture = "";
    private String strRealTimeDesktop = "";
    private String strScreenCaptureSucceeded = "";

    private Handler sHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ShowToast(strScreenCaptureSucceeded + msg.obj.toString());
                iShot = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shot);
        init();
    }

    @Override
    protected void onDestroy() {
        if (iStop == false) {
            iStop = true;
            sHandler.removeCallbacks(sRunnable);
//            mThreadClient.interrupt();
        }
        super.onDestroy();
    }

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strShow = getResources().getString(R.string.desktop_zn);
            strScreenCapture = getResources().getString(R.string.screen_capture_zn);
            strRealTimeDesktop = getResources().getString(R.string.real_time_desktop_zn);
            strScreenCaptureSucceeded = getResources().getString(R.string.screen_capture_succeeded_zn);
        } else if (language.equals("en")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_en);
            strNoConnection = getResources().getString(R.string.no_connection_en);
            strConnectionFailed = getResources().getString(R.string.connection_failed_en);
            strShow = getResources().getString(R.string.power_option_en);
            strScreenCapture = getResources().getString(R.string.screen_capture_en);
            strRealTimeDesktop = getResources().getString(R.string.real_time_desktop_en);
            strScreenCaptureSucceeded = getResources().getString(R.string.screen_capture_succeeded_en);
        }
    }

    private void init() {
        iLanguage();
        app = (ApplicationUtil) ShotActivity.this.getApplication();
        Intent intent = this.getIntent();
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvShow.setText(strShow);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        tvScreenShot = (TextView) findViewById(R.id.tv_screen_capture);
        tvScreenShot.setText(strScreenCapture);
        tvRemoteDesktop = (TextView) findViewById(R.id.tv_real_time_desktop);
        tvRemoteDesktop.setText(strRealTimeDesktop);
        btnScreenShot = (LinearLayout) findViewById(R.id.btn_shot);
        btnScreenShot.setOnClickListener(this);
        btnRemoteDesktop = (LinearLayout) findViewById(R.id.btn_remote_desktop);
        btnRemoteDesktop.setOnClickListener(this);
        ivShow = (ImageView) findViewById(R.id.iv_show);
        ivShow.setOnClickListener(this);
        int what = intent.getIntExtra("what", 0);
        if (what == 1) {
            btnScreenShot.performClick();
        }
//        mThreadClient = new Thread(sRunnable);
//        mThreadClient.start();
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            if (iStop == false) {
                iStop = true;
                sHandler.removeCallbacks(sRunnable);
//                mThreadClient.interrupt();
            }
            setResult(RESULT_OK, null);
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (v == btnScreenShot) {
            pref = PreferenceManager.getDefaultSharedPreferences(this);
            String ip = pref.getString("ip", "");
            SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
            Date date = new Date(System.currentTimeMillis());
            String date_ = sdf.format(date);
            filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/screenshot" + date_ + ".jpeg";
            sendMessage("screenshot", POWEREVENTF_SCREENSHOT + "");
            new ReadTask(ip, "59672", filename, ivShow).execute();
            sHandler.post(sRunnable);
        } else if (v == ivShow) {
            if (iShot) {
                File file = new File(filename);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri mUri = Uri.parse("file://" + file.getPath());
                intent.setDataAndType(mUri, "image/*");
                startActivity(intent);
            }
        } else if (v == btnRemoteDesktop) {
            if (!app.connected) {
                pref = PreferenceManager.getDefaultSharedPreferences(this);
                String ip = pref.getString("ip", "");
                app.startThread(ip);
            }
            for (int i = 0; i < 4; i++) {
                if (app.connected) {
                    startActivity(new Intent(this, DesktopActivity.class));
                    i = 6;
                }
                try {
                    Thread.sleep(250);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!app.connected) {
                if (!app.reachable) {
//                    network_alert.show();
                } else {
//                    alert.show();
                }
            }
        }
    }

    private Runnable sRunnable = new Runnable() {

        @Override
        public void run() {
            if (!iStop) {
                char[] buffer = new char[1024];
                int count = 0;
                if (app.getmBufferedReaderClient() != null) {
                    try {
//                        if ((count = app.getmBufferedReaderClient().read(buffer)) > 0) {
//                            recvMessageClient = getInfoBuff(buffer, count);
                        recvMessageClient = app.getmBufferedReaderClient().readLine();
                        Log.e("IP & PORT", "接收成功(S):" + recvMessageClient);
                        JSONTokener jt = new JSONTokener(recvMessageClient);
                        JSONObject jb = (JSONObject) jt.nextValue();
                        String command = jb.getString("command");
                        String paramet = jb.getString("parameter");
                        if (command.contains("screenshot")) {
                            Message msg = sHandler.obtainMessage();
                            msg.what = 1;
                            msg.obj = paramet;
                            sHandler.sendMessage(msg);
                        }
//                        }
                    } catch (Exception e) {
                        Log.e("IP & PORT", "接收异常:" + e.getMessage());
                        recvMessageClient = "接收异常:" + e.getMessage();
                        Message msg = new Message();
                        msg.what = 1;
                        sHandler.sendMessage(msg);
                    }
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (iStop == false) {
                iStop = true;
                sHandler.removeCallbacks(sRunnable);
//                mThreadClient.interrupt();
            }
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }
}
