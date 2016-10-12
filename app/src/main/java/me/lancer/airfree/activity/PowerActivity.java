package me.lancer.airfree.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class PowerActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;

    private TextView tvShow, tvShutdown, tvRestart, tvLogout;
    private Button btnBack;
    private LinearLayout btnShutdown, btnReset, btnCancell;

    final int POWEREVENTF_SHUTDOWN = 0x0001;    //关机
    final int POWEREVENTF_RESET = 0x0002;       //重启
    final int POWEREVENTF_CANCELL = 0x0003;     //注销

    private Thread mThreadClient = null;
    private String recvMessageClient = "";
    private boolean iStop = false;

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strShow = "";
    private String strShutdown = "";
    private String strRestart = "";
    private String strLogout = "";
    private String strShutdownSucceeded = "";
    private String strRestartSucceeded = "";
    private String strLogoutSucceeded = "";
    private String strReceiveSucceeded = "";
    private String strReceiveFailed = "";

    private Handler pHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (recvMessageClient.contains("1")) {
                    ShowToast(strShutdownSucceeded);
                } else if (recvMessageClient.contains("2")) {
                    ShowToast(strRestartSucceeded);
                } else if (recvMessageClient.contains("3")) {
                    ShowToast(strLogoutSucceeded);
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

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strShow = getResources().getString(R.string.power_option_zn);
            strShutdown = getResources().getString(R.string.shutdown_zn);
            strRestart = getResources().getString(R.string.restart_zn);
            strLogout = getResources().getString(R.string.logout_zn);
            strShutdownSucceeded = getResources().getString(R.string.shutdown_succeeded_zn);
            strRestartSucceeded = getResources().getString(R.string.restart_zn);
            strLogoutSucceeded = getResources().getString(R.string.logout_zn);
            strReceiveSucceeded = getResources().getString(R.string.receive_succeeded_zn);
            strReceiveFailed = getResources().getString(R.string.receive_failed_zn);
        } else if (language.equals("en")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_en);
            strNoConnection = getResources().getString(R.string.no_connection_en);
            strConnectionFailed = getResources().getString(R.string.connection_failed_en);
            strShow = getResources().getString(R.string.power_option_en);
            strShutdown = getResources().getString(R.string.shutdown_en);
            strRestart = getResources().getString(R.string.restart_en);
            strLogout = getResources().getString(R.string.logout_en);
            strShutdownSucceeded = getResources().getString(R.string.shutdown_succeeded_en);
            strRestartSucceeded = getResources().getString(R.string.restart_en);
            strLogoutSucceeded = getResources().getString(R.string.logout_en);
            strReceiveSucceeded = getResources().getString(R.string.receive_succeeded_en);
            strReceiveFailed = getResources().getString(R.string.receive_failed_en);
        }
    }

    private void init() {
        iLanguage();
        app = (ApplicationUtil) this.getApplication();
        Intent intent = this.getIntent();
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvShow.setText(strShow);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        tvShutdown = (TextView) findViewById(R.id.tv_shutdown);
        tvShutdown.setText(strShutdown);
        tvRestart = (TextView) findViewById(R.id.tv_restart);
        tvRestart.setText(strRestart);
        tvLogout = (TextView) findViewById(R.id.tv_logout);
        tvLogout.setText(strLogout);
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
                        Log.e("IP & PORT", strReceiveSucceeded + recvMessageClient);
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
                        Log.e("IP & PORT", strReceiveFailed + e.getMessage());
                        recvMessageClient = strReceiveFailed + e.getMessage();
                        Message msg = new Message();
                        msg.what = 1;
                        pHandler.sendMessage(msg);
                    }
                }
            }
        }
    };
}
