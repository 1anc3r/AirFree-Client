package me.lancer.airfree.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;
import me.lancer.airfree.bean.FeedbackBean;
import me.lancer.distance.R;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout btnAboutUs, btnLanguage, btnCommonProblem, btnFeedback, btnShutdown;
    TextView tvAboutUs, tvLanguage, tvCommonProblem, tvFeedback, tvShutdown;
    private TextView tvWeb;
    private EditText etContent;
    private Button btnSend;
    private ProgressDialog mProgressDialog;

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strLanguage = "";
    private String strGuide = "";
    private String strAbout = "";
    private String strFeedback = "";
    private String strFeedbackSucceeded = "";
    private String strFeedbackFailed = "";
    private String strExit = "";
    private String strSend = "";
    private String strFeedbackhint = "";

//    private Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            mProgressDialog.dismiss();
//            ShowToast(getResources().getString(R.string.latest_version));
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Bmob.initialize(this, "ede7c3d347a0c094bd335bde06c57962");
        init();
    }

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strLanguage = getResources().getString(R.string.language_zn);
            strGuide = getResources().getString(R.string.guide_zn);
            strAbout = getResources().getString(R.string.about_zn);
            strFeedback = getResources().getString(R.string.feedback_zn);
            strFeedbackSucceeded = getResources().getString(R.string.feedback_succeeded_zn);
            strFeedbackFailed = getResources().getString(R.string.feedback_failed_zn);
            strExit = getResources().getString(R.string.exit_zn);
            strSend = getResources().getString(R.string.send_zn);
            strFeedbackhint = getResources().getString(R.string.please_input_feedback_zn);
        } else if (language.equals("en")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_en);
            strNoConnection = getResources().getString(R.string.no_connection_en);
            strConnectionFailed = getResources().getString(R.string.connection_failed_en);
            strLanguage = getResources().getString(R.string.language_en);
            strGuide = getResources().getString(R.string.guide_en);
            strAbout = getResources().getString(R.string.about_en);
            strFeedback = getResources().getString(R.string.feedback_en);
            strFeedbackSucceeded = getResources().getString(R.string.feedback_succeeded_en);
            strFeedbackFailed = getResources().getString(R.string.feedback_failed_en);
            strExit = getResources().getString(R.string.exit_en);
            strSend = getResources().getString(R.string.send_en);
            strFeedbackhint = getResources().getString(R.string.please_input_feedback_en);
        }
    }

    private void init() {
        iLanguage();
        tvLanguage = (TextView) findViewById(R.id.tv_language);
        tvLanguage.setText(strLanguage);
        tvCommonProblem = (TextView) findViewById(R.id.tv_guide);
        tvCommonProblem.setText(strGuide);
        tvAboutUs = (TextView) findViewById(R.id.tv_about);
        tvAboutUs.setText(strAbout);
        tvFeedback = (TextView) findViewById(R.id.tv_feedback);
        tvFeedback.setText(strFeedback);
        tvShutdown = (TextView) findViewById(R.id.tv_exit);
        tvShutdown.setText(strExit);
        btnAboutUs = (LinearLayout) findViewById(R.id.btn_about_us);
        btnAboutUs.setOnClickListener(this);
        btnLanguage = (LinearLayout) findViewById(R.id.btn_language);
        btnLanguage.setOnClickListener(this);
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
        } else if (v == btnLanguage) {
//            mProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.loading));
//            new Thread() {
//
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(1500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    mHandler.sendEmptyMessage(0);
//                }
//            }.start();
            new AlertDialog.Builder(SettingActivity.this)
                    .setItems(this.getResources().getStringArray(R.array.language),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences.Editor editor = pref.edit();
                                    if (which == 0) {
                                        editor.putString(getString(R.string.language_choice), "zn");
                                    } else if (which == 1) {
                                        editor.putString(getString(R.string.language_choice), "en");
                                    }
                                    editor.commit();
                                    restart();
                                }
                            })
                    .show();
        } else if (v == btnCommonProblem) {
            Intent intent = new Intent();
            intent.setClass(SettingActivity.this, GuideActivity.class);
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
            etContent.setHint(strFeedbackhint);
            etContent.setFocusableInTouchMode(true);
            etContent.setFocusable(true);
            etContent.requestFocus();
            etContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        FeedbackBean item = new FeedbackBean();
                        item.setContent(etContent.getText().toString());
                        item.save(SettingActivity.this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                ShowToast(strFeedbackSucceeded);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                ShowToast(strFeedbackFailed);
                                dialog.dismiss();
                            }
                        });
                        return true;
                    }
                    return false;
                }
            });
            btnSend = (Button) layout.findViewById(R.id.btn_send);
            btnSend.setText(strSend);
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FeedbackBean item = new FeedbackBean();
                    item.setContent(etContent.getText().toString());
                    item.save(SettingActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            ShowToast(strFeedbackSucceeded);
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            ShowToast(strFeedbackFailed);
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
            app.off();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void restart(){
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
