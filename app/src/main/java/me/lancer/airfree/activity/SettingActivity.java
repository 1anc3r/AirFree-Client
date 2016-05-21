package me.lancer.airfree.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.lancer.distance.R;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout btnAboutUs, btnVersionUpdate, btnCommonProblem, btnFeedback, btnShutdown;
    TextView tvWeb;
    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
            ShowToast("已是最新版本");
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    private void init() {
        btnAboutUs = (LinearLayout) findViewById(R.id.btn_about_us);
        btnAboutUs.setOnClickListener(this);
        btnVersionUpdate = (LinearLayout) findViewById(R.id.btn_version_update);
        btnVersionUpdate.setOnClickListener(this);
        btnCommonProblem = (LinearLayout) findViewById(R.id.btn_common_problem);
        btnCommonProblem.setOnClickListener(this);
        btnFeedback = (LinearLayout) findViewById(R.id.btn_feedback);
        btnFeedback.setOnClickListener(this);
        btnShutdown = (LinearLayout) findViewById(R.id.btn_shutdown);
        btnShutdown.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnAboutUs) {
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.about_us_dialog_view, null);
            tvWeb = (TextView) layout.findViewById(R.id.tv_web);
            tvWeb.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("http://www.1anc3r.me");
                    intent.setData(content_url);
                    startActivity(intent);
                }
            });
            final Dialog dialog = new AlertDialog.Builder(SettingActivity.this).create();
            dialog.show();
            dialog.getWindow().setContentView(layout);
        } else if (v == btnVersionUpdate) {
            mProgressDialog = ProgressDialog.show(this, null, "正在检查更新...");
            new Thread(){

                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(0);
                }}.start();
        } else if (v == btnCommonProblem) {
            Intent intent = new Intent();
            intent.setClass(SettingActivity.this, ProblemActivity.class);
            startActivity(intent);
        } else if (v == btnFeedback) {
            Intent data=new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:huangfangzhi0@gmail.com"));
            data.putExtra(Intent.EXTRA_SUBJECT, "");
            data.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(data);
        } else if (v == btnShutdown){
            finish();
        }
    }
}
