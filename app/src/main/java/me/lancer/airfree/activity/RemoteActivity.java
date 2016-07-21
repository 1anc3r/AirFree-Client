package me.lancer.airfree.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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

    private static String TAG = RemoteActivity.class.getSimpleName();

    LinearLayout btnMouse;
    LinearLayout btnPower;
    LinearLayout btnShot;
    LinearLayout btnVoice;
    LinearLayout btnVolume;
    LinearLayout btnBright;
    LinearLayout btnGesture;
    LinearLayout btnOpen;
    LinearLayout btnTalk;
    private SeekBar sbVolume, sbBright;
    private SpeechRecognizer mIat;
    private RecognizerDialog mIatDialog;
    private EditText etSearch;

    ApplicationUtil app;
    int ret = 0;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private SharedPreferences mSharedPreferences;
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        init();
    }

    private void init() {
        app = (ApplicationUtil) RemoteActivity.this.getApplication();
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
                new AlertDialog.Builder(RemoteActivity.this)
                        .setItems(this.getResources().getStringArray(R.array.remote_entries),
                                new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendMessage("remote", "" + (which+1));
                            }
                        })
                        .show();
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
            Toast.makeText(this, "没有连接!", Toast.LENGTH_SHORT).show();
        }
    }

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
        String lag = mSharedPreferences.getString("iat_language_preference","mandarin");
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
        } else if (result.contains("打开资源管理器") || result.contains("打开我的电脑")) {
            sendMessage("remote", "3");
        } else if (result.contains("打开设备管理器")) {
            sendMessage("remote", "4");
        } else if (result.contains("打开磁盘管理器")) {
            sendMessage("remote", "5");
        } else if (result.contains("打开注册表编辑器")) {
            sendMessage("remote", "6");
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
