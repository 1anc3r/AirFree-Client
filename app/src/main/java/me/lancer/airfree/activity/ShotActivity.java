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

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class ShotActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;

    private Button btnBack;
    private ImageView ivShow;
    private LinearLayout btnScreenShot;

    final int POWEREVENTF_SCREENSHOT = 0x0001;    //截屏

    private String filename;
    private Thread mThreadClient = null;
    private String recvMessageClient = "";
    private SharedPreferences pref;
    private boolean iStop = false;

    private Handler sHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ShowToast("截图成功:" + msg.obj.toString());
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
//        sHandler.removeCallbacks(sRunnable);
            mThreadClient.interrupt();
        }
        super.onDestroy();
    }

    private void init() {
        app = (ApplicationUtil) ShotActivity.this.getApplication();
        Intent intent = this.getIntent();
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnScreenShot = (LinearLayout) findViewById(R.id.btn_shot);
        btnScreenShot.setOnClickListener(this);
        ivShow = (ImageView) findViewById(R.id.iv_show);
        ivShow.setOnClickListener(this);
        int what = intent.getIntExtra("what", 0);
        if (what == 1) {
            btnScreenShot.performClick();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            if (iStop == false) {
                iStop = true;
//        sHandler.removeCallbacks(sRunnable);
                mThreadClient.interrupt();
            }
            setResult(RESULT_OK, null);
            finish();
        } else if (v == btnScreenShot) {
            pref = PreferenceManager.getDefaultSharedPreferences(this);
            String ip = pref.getString("ip", "");
            SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
            Date date = new Date(System.currentTimeMillis());
            String date_ = sdf.format(date);
            filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/screenshot" + date_ + ".jpeg";
            sendMessage("screenshot", POWEREVENTF_SCREENSHOT + "");
            new ReadTask(ip, "59672", filename, ivShow).execute();
//            sHandler.post(sRunnable);
            mThreadClient = new Thread(sRunnable);
            mThreadClient.start();
        } else if (v == ivShow) {
            File file = new File(filename);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri mUri = Uri.parse("file://" + file.getPath());
            intent.setDataAndType(mUri, "image/*");
            startActivity(intent);
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
                        if ((count = app.getmBufferedReaderClient().read(buffer)) > 0) {
                            recvMessageClient = getInfoBuff(buffer, count);
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
                        }
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
//        sHandler.removeCallbacks(sRunnable);
                mThreadClient.interrupt();
            }
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }
}
