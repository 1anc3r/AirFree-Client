package me.lancer.airfree.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View.OnClickListener;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;
import tyrantgit.explosionfield.ExplosionField;

public class LinkActivity extends BaseActivity {

    ApplicationUtil app;

    private EditText etIP;
    private CheckBox cbRemember;
    private ExplosionField boom;
    private Button btnLink, btnScan;

    private String txtIP, txtPORT = "59671";
    private Thread mThreadClient = null;
    private Socket mSocketClient = null;
    private SharedPreferences pref;
    static BufferedReader mBufferedReaderClient = null;
    static PrintWriter mPrintWriterClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        init();
    }

    private void init() {
        app = (ApplicationUtil) LinkActivity.this.getApplication();
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#FCFCFC"));
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        etIP = (EditText) findViewById(R.id.et_ip);
        cbRemember = (CheckBox) findViewById(R.id.cb_remember);
        btnLink = (Button) findViewById(R.id.btn_link);
        btnLink.setOnClickListener(onLinkClickListener);
        btnScan = (Button) findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(onScanClickListener);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean(getString(R.string.is_remember), false);
        if (isRemember) {
            String ip = pref.getString(getString(R.string.is_ip), "");
            etIP.setText(ip);
            cbRemember.setChecked(true);
        }
        boom = ExplosionField.attach2Window(this);
        if (app.isExplosing()) {
            listen(findViewById(R.id.ll_icon));
            listen(findViewById(R.id.cb_remember));
            listen(findViewById(R.id.btn_link));
            listen(findViewById(R.id.btn_scan));
        }
    }

    private OnClickListener onLinkClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            SharedPreferences.Editor editor = pref.edit();
            if (cbRemember.isChecked()) {
                editor.putBoolean(getString(R.string.is_remember), true);
                editor.putString(getString(R.string.is_ip), etIP.getText().toString());
            } else {
                editor.putBoolean(getString(R.string.is_remember), false);
                editor.putString(getString(R.string.is_ip), etIP.getText().toString());
            }
            editor.commit();
            if (!app.isConnecting() && app.getmPrintWriterClient() == null && app.getmBufferedReaderClient() == null) {
                String txtIP = etIP.getText().toString();
                int i = app.init(txtIP, "59671");
                Log.e("IP & PORT", app.getmSocketClient().toString());
                if (i == 1) {
                    app.setIsConnecting(true);
                    Intent intent = new Intent(LinkActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (i == 0) {
                    ShowToast("没有连接!");
                    Log.e("IP & PORT", "没有连接!");
                } else if (i == -1) {
                    ShowToast("连接失败!");
                    Log.e("IP & PORT", "连接失败!");
                }
            } else {
                Log.e("IP & PORT", app.getmSocketClient().toString());
                Intent intent = new Intent(LinkActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    private OnClickListener onScanClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(LinkActivity.this, CaptureActivity.class);
            startActivityForResult(intent, 1);
        }
    };

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            String txtIP = etIP.getText().toString();
            if ((txtIP.length() <= 0) || (txtPORT.length() <= 0)) {
                Log.e("IP & PORT", " IP & PORT 不能为空!");
                ShowToast("IP & PORT 不能为空!");
                return;
            }
            String strIP = txtIP;
            String strPORT = txtPORT;
            int port = Integer.parseInt(strPORT);
            try {
                mSocketClient = new Socket(strIP, port);
                mBufferedReaderClient = new BufferedReader(
                        new InputStreamReader(mSocketClient.getInputStream()));
                mPrintWriterClient = new PrintWriter(
                        mSocketClient.getOutputStream(), true);
                ShowToast("连接成功!");
                Log.e("IP & PORT", " 连接成功!");
                if (mSocketClient != null) {
                    Intent intent = new Intent();
                    intent.setClass(LinkActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    ShowToast("没有连接!");
                    Log.e("IP & PORT", " 没有连接!");
                }
            } catch (Exception e) {
                ShowToast("连接失败!" + e.getMessage());
                Log.e("IP & PORT", " 连接失败!" + e.getMessage());
                return;
            }
        }
    };

    private void listen(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                listen(parent.getChildAt(i));
            }
        } else {
            root.setClickable(true);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boom.explode(v);
                    v.setOnClickListener(null);
                }
            });
        }
    }

    private void reset(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                reset(parent.getChildAt(i));
            }
        } else {
            root.setScaleX(1);
            root.setScaleY(1);
            root.setAlpha(1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString("result");
            etIP.setText(result);
            btnLink.performClick();
        }
    }
}
