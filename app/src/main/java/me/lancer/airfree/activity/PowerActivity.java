package me.lancer.airfree.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.json.JSONTokener;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class PowerActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;

    private Button btnBack;
    private LinearLayout btnShutdown, btnReset, btnCancell;

    final int POWEREVENTF_SHUTDOWN = 0x0001;    //关机
    final int POWEREVENTF_RESET = 0x0002;       //重启
    final int POWEREVENTF_CANCELL = 0x0003;     //注销

    private Thread mThreadClient = null;
    private String recvMessageClient = "";
    private boolean iStop = false;

    private Handler pHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (recvMessageClient.contains("1")) {
                    ShowToast("关机成功!");
                } else if (recvMessageClient.contains("2")) {
                    ShowToast("重启成功!");
                } else if (recvMessageClient.contains("3")) {
                    ShowToast("注销成功!");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);
        init();
    }

    @Override
    protected void onDestroy() {
        iStop = true;
        super.onDestroy();
    }

    private void init() {
        app = (ApplicationUtil) this.getApplication();
        Intent intent = this.getIntent();
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnShutdown = (LinearLayout) findViewById(R.id.btn_shutdown);
        btnShutdown.setOnClickListener(this);
        btnReset = (LinearLayout) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);
        btnCancell = (LinearLayout) findViewById(R.id.btn_cancell);
        btnCancell.setOnClickListener(this);
        int what = intent.getIntExtra("what", 0);
        if (what == 1) {
            btnShutdown.performClick();
        } else if (what == 2) {
            btnReset.performClick();
        } else if (what == 3) {
            btnCancell.performClick();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            iStop = true;
            setResult(RESULT_OK, null);
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (v == btnShutdown) {
            sendMessage("power", POWEREVENTF_SHUTDOWN + "");
        } else if (v == btnReset) {
            sendMessage("power", POWEREVENTF_RESET + "");
        } else if (v == btnCancell) {
            sendMessage("power", POWEREVENTF_CANCELL + "");
        }
        mThreadClient = new Thread(pRunnable);
        mThreadClient.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            iStop = true;
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }

    private Runnable pRunnable = new Runnable() {

        @Override
        public void run() {
            if (!iStop) {
                char[] buffer = new char[256];
                int count = 0;
                if (app.getmBufferedReaderClient() != null) {
                    try {
//                        if ((count = app.getmBufferedReaderClient().read(buffer)) > 0) {
//                            recvMessageClient = getInfoBuff(buffer, count);
                        recvMessageClient = app.getmBufferedReaderClient().readLine();
                        Log.e("IP & PORT", "接收成功:" + recvMessageClient);
                        JSONTokener jt = new JSONTokener(recvMessageClient);
                        JSONObject jb = (JSONObject) jt.nextValue();
                        String command = jb.getString("command");
                        String paramet = jb.getString("parameter");
                        if (command.contains("power")) {
                            Message msg = pHandler.obtainMessage();
                            msg.what = 1;
                            pHandler.sendMessage(msg);
                        }
//                        }
                    } catch (Exception e) {
                        Log.e("IP & PORT", "接收异常:" + e.getMessage());
                        recvMessageClient = "接收异常:" + e.getMessage();
                        Message msg = new Message();
                        msg.what = 1;
                        pHandler.sendMessage(msg);
                    }
                }
            }
        }
    };
}
