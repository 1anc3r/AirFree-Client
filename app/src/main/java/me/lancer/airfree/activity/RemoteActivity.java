package me.lancer.airfree.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import com.iflytek.sunflower.FlowerCollector;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.airfree.util.IatSettings;
import me.lancer.airfree.util.JsonParser;
import me.lancer.distance.R;

public class RemoteActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;

    LinearLayout btnMouse, btnPower, btnShot, btnVoice, btnVolume, btnBright, btnGesture, btnOpen, btnTalk;
    TextView tvMouse, tvPower, tvShot, tvVoice, tvVolume, tvBright, tvGesture, tvOpen, tvTalk;
    private EditText etSearch;
    private SeekBar sbVolume, sbBright;
    private RecognizerDialog mIatDialog;
    private SpeechRecognizer mIat;
    private ProgressDialog mProgressDialog;

    private static String TAG = RemoteActivity.class.getSimpleName();

    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private SharedPreferences mSharedPreferences;
    private Thread mThreadClient = null;
    private String recvMessageClient = "";
    private boolean iStop = false;

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strKeyboardMouse = "";
    private String strGesture = "";
    private String strVoice = "";
    private String strVolume = "";
    private String strPowerOption = "";
    private String strBrightness = "";
    private String strRemoteDeviceInfo = "";
    private String strDesktop = "";
    private String strChatroom = "";
    private String strOK = "";
    private String strLoading = "";

    private Handler iHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj!=null) {
                mProgressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(RemoteActivity.this);
                builder.setTitle(strRemoteDeviceInfo);
                builder.setMessage((CharSequence) msg.obj);
                builder.setNegativeButton(strOK, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        init();
    }

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strKeyboardMouse = getResources().getString(R.string.keyboard_mouse_zn);
            strGesture = getResources().getString(R.string.gesture_zn);
            strVoice = getResources().getString(R.string.voice_zn);
            strVolume = getResources().getString(R.string.volume_zn);
            strPowerOption = getResources().getString(R.string.power_option_zn);
            strBrightness = getResources().getString(R.string.bright_zn);
            strRemoteDeviceInfo = getResources().getString(R.string.remote_device_info_zn);
            strDesktop = getResources().getString(R.string.desktop_zn);
            strChatroom = getResources().getString(R.string.chatroom_zn);
            strOK = getResources().getString(R.string.ok_zn);
            strLoading = getResources().getString(R.string.loading_zn);
        } else if (language.equals("en")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_en);
            strNoConnection = getResources().getString(R.string.no_connection_en);
            strConnectionFailed = getResources().getString(R.string.connection_failed_en);
            strKeyboardMouse = getResources().getString(R.string.keyboard_mouse_en);
            strGesture = getResources().getString(R.string.gesture_en);
            strVoice = getResources().getString(R.string.voice_en);
            strVolume = getResources().getString(R.string.volume_en);
            strPowerOption = getResources().getString(R.string.power_option_en);
            strBrightness = getResources().getString(R.string.bright_en);
            strRemoteDeviceInfo = getResources().getString(R.string.remote_device_info_en);
            strDesktop = getResources().getString(R.string.desktop_en);
            strChatroom = getResources().getString(R.string.chatroom_en);
            strOK = getResources().getString(R.string.ok_en);
            strLoading = getResources().getString(R.string.loading_en);
        }
    }

    private void init() {
        iLanguage();
        app = (ApplicationUtil) RemoteActivity.this.getApplication();
        tvMouse = (TextView) findViewById(R.id.tv_keyboard_mouse);
        tvMouse.setText(strKeyboardMouse);
        tvGesture = (TextView) findViewById(R.id.tv_gesture);
        tvGesture.setText(strGesture);
        tvVoice = (TextView) findViewById(R.id.tv_voice);
        tvVoice.setText(strVoice);
        tvVolume = (TextView) findViewById(R.id.tv_volume);
        tvVolume.setText(strVolume);
        tvPower = (TextView) findViewById(R.id.tv_power_option);
        tvPower.setText(strPowerOption);
        tvBright = (TextView) findViewById(R.id.tv_brightness);
        tvBright.setText(strBrightness);
        tvOpen = (TextView) findViewById(R.id.tv_remote_device_info);
        tvOpen.setText(strRemoteDeviceInfo);
        tvShot = (TextView) findViewById(R.id.tv_desktop);
        tvShot.setText(strDesktop);
        tvTalk = (TextView) findViewById(R.id.tv_chatroom);
        tvTalk.setText(strChatroom);
        btnPower = (LinearLayout) findViewById(R.id.btn_power);
        btnPower.setOnClickListener(this);
        btnShot = (LinearLayout) findViewById(R.id.btn_shot);
        btnShot.setOnClickListener(this);
        btnMouse = (LinearLayout) findViewById(R.id.btn_mouse);
        btnMouse.setOnClickListener(this);
        btnGesture = (LinearLayout) findViewById(R.id.btn_gesture);
        btnGesture.setOnClickListener(this);
        btnVoice = (LinearLayout) findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(this);
        btnVolume = (LinearLayout) findViewById(R.id.btn_volume);
        btnVolume.setOnClickListener(this);
        btnBright = (LinearLayout) findViewById(R.id.btn_bright);
        btnBright.setOnClickListener(this);
        btnOpen = (LinearLayout) findViewById(R.id.btn_open);
        btnOpen.setOnClickListener(this);
        btnTalk = (LinearLayout) findViewById(R.id.btn_talk);
        btnTalk.setOnClickListener(this);
        mIat = SpeechRecognizer.createRecognizer(RemoteActivity.this, mInitListener);
        mIatDialog = new RecognizerDialog(RemoteActivity.this, mInitListener);
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        if (app.isConnecting()) {
            if (v == btnPower) {
                Intent intent = new Intent();
                intent.putExtra("what", 0);
                intent.setClass(RemoteActivity.this, PowerActivity.class);
                startActivity(intent);
                this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            } else if (v == btnShot) {
                Intent intent = new Intent();
                intent.putExtra("what", 0);
                intent.setClass(RemoteActivity.this, ShotActivity.class);
                startActivity(intent);
                this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            } else if (v == btnMouse) {
                Intent intent = new Intent();
                intent.setClass(RemoteActivity.this, MouseActivity.class);
                startActivity(intent);
                this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            } else if (v == btnGesture) {
                Intent intent = new Intent();
                intent.setClass(RemoteActivity.this, GestureActivity.class);
                startActivity(intent);
                this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            } else if (v == btnVoice) {
                FlowerCollector.onEvent(RemoteActivity.this, "iat_recognize");
                mIatResults.clear();
                setParam();
                mIatDialog.setListener(mRecognizerDialogListener);
                mIatDialog.show();
                showTip(getString(R.string.text_begin));
            } else if (v == btnVolume) {
//                Intent intent = new Intent();
//                intent.setClass(RemoteActivity.this, VolumeActivity.class);
//                startActivity(intent);
//                this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.seekbar_dialog_view, null);
                final Dialog dialog = new AlertDialog.Builder(RemoteActivity.this).create();
                sbVolume = (SeekBar) layout.findViewById(R.id.sb_vorb);
                sbVolume.setProgress(50);
                sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        sendMessage("volume", "" + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                dialog.show();
                dialog.getWindow().setContentView(layout);
            } else if (v == btnBright) {
//                Intent intent = new Intent();
//                intent.setClass(RemoteActivity.this, BrightActivity.class);
//                startActivity(intent);
//                this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.seekbar_dialog_view, null);
                final Dialog dialog = new AlertDialog.Builder(RemoteActivity.this).create();
                sbBright = (SeekBar) layout.findViewById(R.id.sb_vorb);
                sbBright.setProgress(50);
                sbBright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        sendMessage("bright", "" + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                dialog.show();
                dialog.getWindow().setContentView(layout);
            } else if (v == btnOpen) {
                mProgressDialog = ProgressDialog.show(this, null, strLoading);
                sendMessage("info", "");
                mThreadClient = new Thread(iRunnable);
                mThreadClient.start();
            } else if (v == btnTalk) {
//                LayoutInflater inflater = LayoutInflater.from(this);
//                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.searchbar_dialog_view, null);
//                final Dialog dialog = new AlertDialog.Builder(RemoteActivity.this).create();
//                etSearch = (EditText) layout.findViewById(R.id.et_search);
//                etSearch.setFocusableInTouchMode(true);
//                etSearch.setFocusable(true);
//                etSearch.requestFocus();
//                etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//                    @Override
//                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                            sendMessage("remote", "s?wd=" + etSearch.getText());
//                            dialog.dismiss();
//                            return true;
//                        }
//                        return false;
//                    }
//                });
//                dialog.show();
//                dialog.getWindow().setContentView(layout);
//                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                Intent intent = new Intent();
                intent.setClass(RemoteActivity.this, TalkActivity.class);
                startActivity(intent);
                this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            }
        } else {
            Toast.makeText(this, strNoConnection, Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable iRunnable = new Runnable() {

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
                        if (command.contains("info")) {
                            Message msg = iHandler.obtainMessage();
                            msg.obj = paramet;
                            iHandler.sendMessage(msg);
                        }
//                        }
                    } catch (Exception e) {
                        Log.e("IP & PORT", "接收异常:" + e.getMessage());
                        recvMessageClient = "接收异常:" + e.getMessage();
                        Message msg = new Message();
                        msg.what = 1;
                        iHandler.sendMessage(msg);
                    }
                }
            }
        }
    };

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            if (isLast) {
                printResult(results);
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.e("IP & PORT", isLast + " " + results.toString());
            if (!isLast) {
                printResult(results);
            }
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        DealWitdResult(resultBuffer.toString());
    }

    private void showTip(final String str) {
        ShowToast(str);
    }

    public void setParam() {
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        String lag = mSharedPreferences.getString("iat_language_preference", "mandarin");
        if (lag.equals("en_us")) {
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "0"));
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    private void DealWitdResult(String result) {
        if (result.contains("打开命令行")) {
            sendMessage("remote", "1");
        } else if (result.contains("关闭命令行")) {
            sendMessage("remote", "-1");
        } else if (result.contains("打开任务管理器")) {
            sendMessage("remote", "2");
        } else if (result.contains("关闭任务管理器")) {
            sendMessage("remote", "-2");
        } else if (result.contains("打开资源管理器") || result.contains("打开我的电脑")) {
            sendMessage("remote", "3");
        } else if (result.contains("关闭资源管理器") || result.contains("关闭我的电脑")) {
            sendMessage("remote", "-3");
        } else if (result.contains("打开设备管理器")) {
            sendMessage("remote", "4");
        } else if (result.contains("关闭设备管理器")) {
            sendMessage("remote", "-4");
        } else if (result.contains("打开磁盘管理器")) {
            sendMessage("remote", "5");
        } else if (result.contains("关闭磁盘管理器")) {
            sendMessage("remote", "-5");
        } else if (result.contains("打开注册表编辑器")) {
            sendMessage("remote", "6");
        } else if (result.contains("关闭注册表编辑器")) {
            sendMessage("remote", "-6");
        } else if (result.contains("打开计算器")) {
            sendMessage("remote", "7");
        } else if (result.contains("关闭计算器")) {
            sendMessage("remote", "-7");
        } else if (result.contains("打开记事本")) {
            sendMessage("remote", "8");
        } else if (result.contains("关闭记事本")) {
            sendMessage("remote", "-8");
        } else if (result.contains("打开画图板")) {
            sendMessage("remote", "9");
        } else if (result.contains("关闭画图板")) {
            sendMessage("remote", "-9");
        } else if (result.contains("打开写字板")) {
            sendMessage("remote", "10");
        } else if (result.contains("关闭写字板")) {
            sendMessage("remote", "-10");
        } else if (result.contains("打开浏览器")) {
            sendMessage("remote", "11");
        } else if (result.contains("下") || result.contains("下一页") || result.contains("降低音量")) {
            sendMessage("remote", "12");
        } else if (result.contains("上") || result.contains("上一页") || result.contains("升高音量")) {
            sendMessage("remote", "13");
        } else if (result.contains("右") || result.contains("快进")) {
            sendMessage("remote", "14");
        } else if (result.contains("左") || result.contains("快退")) {
            sendMessage("remote", "15");
        } else if (result.contains("暂停") || result.contains("播放")) {
            sendMessage("remote", "16");
        } else if (result.contains("切换窗口")){
            sendMessage("remote", "1024");
        } else if (result.contains("关闭窗口")){
            sendMessage("remote", "-1024");
        } else if (result.matches("打开*[a-z]盘")) {
            Pattern p = Pattern.compile("打开(.*?)盘");
            Matcher m = p.matcher(result);
            ArrayList<String> letters = new ArrayList<>();
            while (m.find()) {
                letters.add(m.group(1));
            }
            for (String letter : letters) {
                sendMessage("remote", letter + ":");
            }
        } else if (result.contains("搜索")) {
            result = result.replaceAll("搜索", "");
            sendMessage("remote", "s?wd=" + result);
        } else if (result.contains("关机")) {
            Intent intent = new Intent();
            intent.putExtra("what", 1);
            intent.setClass(RemoteActivity.this, PowerActivity.class);
            startActivity(intent);
            this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (result.contains("重启")) {
            Intent intent = new Intent();
            intent.putExtra("what", 2);
            intent.setClass(RemoteActivity.this, PowerActivity.class);
            startActivity(intent);
            this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (result.contains("注销")) {
            Intent intent = new Intent();
            intent.putExtra("what", 3);
            intent.setClass(RemoteActivity.this, PowerActivity.class);
            startActivity(intent);
            this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (result.contains("截屏") || result.contains("截图")) {
            Intent intent = new Intent();
            intent.putExtra("what", 1);
            intent.setClass(RemoteActivity.this, ShotActivity.class);
            startActivity(intent);
            this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (result.contains("唱支歌") || result.contains("music")) {
            sendMessage("remote", "music");
        } else if (result.contains("卖个萌")) {
            sendMessage("remote", "actcute");
        } else {
            sendMessage("remote", "s?wd=" + result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIat.cancel();
        mIat.destroy();
    }
}
