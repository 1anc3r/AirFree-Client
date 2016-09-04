package me.lancer.airfree.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;
import me.lancer.airfree.bean.Feedback;
import me.lancer.distance.R;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout btnAboutUs, btnVersionUpdate, btnCommonProblem, btnFeedback, btnShutdown;
    private TextView tvWeb;
    private EditText etContent;
    private Button btnSend;
    private ProgressDialog mProgressDialog;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
            ShowToast("已是最新版本");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Bmob.initialize(this, "ede7c3d347a0c094bd335bde06c57962");
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
            new Thread() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(0);
                }
            }.start();
        } else if (v == btnCommonProblem) {
            Intent intent = new Intent();
            intent.setClass(SettingActivity.this, ProblemActivity.class);
            startActivity(intent);
        } else if (v == btnFeedback) {
//            Intent data = new Intent(Intent.ACTION_SENDTO);
//            data.setData(Uri.parse("mailto:huangfangzhi0@gmail.com"));
//            data.putExtra(Intent.EXTRA_SUBJECT, "");
//            data.putExtra(Intent.EXTRA_TEXT, "");
//            startActivity(data);
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.feedback_dialog_view, null);
            final Dialog dialog = new AlertDialog.Builder(SettingActivity.this).create();
            etContent = (EditText) layout.findViewById(R.id.et_content);
            etContent.setFocusableInTouchMode(true);
            etContent.setFocusable(true);
            etContent.requestFocus();
            etContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        Feedback item = new Feedback();
                        item.setContent(etContent.getText().toString());
                        item.save(SettingActivity.this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                ShowToast("意见反馈成功，感谢您的支持");
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                ShowToast("意见反馈失败，请检查网络后再试");
                                dialog.dismiss();
                            }
                        });
                        return true;
                    }
                    return false;
                }
            });
            btnSend = (Button) layout.findViewById(R.id.btn_send);
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Feedback item = new Feedback();
                    item.setContent(etContent.getText().toString());
                    item.save(SettingActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            ShowToast("意见反馈成功，感谢您的支持");
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            ShowToast("意见反馈失败，请检查网络后再试");
                            dialog.dismiss();
                        }
                    });
                }
            });
            dialog.show();
            dialog.getWindow().setContentView(layout);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else if (v == btnShutdown) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }
}
