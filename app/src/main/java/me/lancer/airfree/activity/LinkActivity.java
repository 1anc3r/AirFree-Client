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
    static BufferedReader mBufferedReaderClient = null;
    static PrintWriter mPrintWriterClient = null;

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strIPCantBeEmpty = "";
    private String strIPAddressHint = "";
    private String strRememberConnection = "";
    private String strInputIPAddress = "";
    private String strScanQRCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        init();
    }

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strIPCantBeEmpty = getResources().getString(R.string.ip_cant_be_empty_zn);
            strIPAddressHint = getResources().getString(R.string.ip_address_hint_zn);
            strRememberConnection = getResources().getString(R.string.remember_connection_zn);
            strInputIPAddress = getResources().getString(R.string.input_ip_address_zn);
            strScanQRCode = getResources().getString(R.string.scan_qr_code_zn);
        } else if (language.equals("en")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_en);
            strNoConnection = getResources().getString(R.string.no_connection_en);
            strConnectionFailed = getResources().getString(R.string.connection_failed_en);
            strIPCantBeEmpty = getResources().getString(R.string.ip_cant_be_empty_en);
            strIPAddressHint = getResources().getString(R.string.ip_address_hint_en);
            strRememberConnection = getResources().getString(R.string.remember_connection_en);
            strInputIPAddress = getResources().getString(R.string.input_ip_address_en);
            strScanQRCode = getResources().getString(R.string.scan_qr_code_en);
        }
    }

    private void init() {
        iLanguage();
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
        etIP.setHint(strIPAddressHint);
        cbRemember = (CheckBox) findViewById(R.id.cb_remember);
        cbRemember.setText(strRememberConnection);
        btnLink = (Button) findViewById(R.id.btn_link);
        btnLink.setText(strInputIPAddress);
        btnLink.setOnClickListener(onLinkClickListener);
        btnScan = (Button) findViewById(R.id.btn_scan);
        btnScan.setText(strScanQRCode);
        btnScan.setOnClickListener(onScanClickListener);
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
                if (i == 1) {
                    app.setIsConnecting(true);
                    Intent intent = new Intent(LinkActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (i == 0) {
                        ShowToast(strNoConnection);
                        Log.e("IP & PORT", strNoConnection);
                } else if (i == -1) {
                        ShowToast(strConnectionFailed);
                        Log.e("IP & PORT", strConnectionFailed);
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
                    Log.e("IP & PORT", strIPCantBeEmpty);
                    ShowToast(strIPCantBeEmpty);
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
                    ShowToast(strConnectionSucceeded);
                    Log.e("IP & PORT", strConnectionSucceeded);
                if (mSocketClient != null) {
                    Intent intent = new Intent();
                    intent.setClass(LinkActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                        ShowToast(strNoConnection);
                        Log.e("IP & PORT", strNoConnection);
                }
            } catch (Exception e) {
                    ShowToast(strConnectionFailed + e.getMessage());
                    Log.e("IP & PORT", strConnectionFailed + e.getMessage() + e.getMessage());
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
