package me.lancer.airfree.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.airfree.util.IatSettings;
import me.lancer.airfree.util.JsonParser;
import me.lancer.distance.R;

public class Remote2Activity extends BaseActivity implements View.OnClickListener{

    ApplicationUtil app;

    private Button btnBack, btnLeft, btnRight, btnUp, btnDown, btnVoice, btnVolume, btnBright, btnPlay;
    private SeekBar sbVolume, sbBright;
    private RecognizerDialog mIatDialog;
    private SpeechRecognizer mIat;

    private static String TAG = Remote2Activity.class.getSimpleName();

    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private SharedPreferences mSharedPreferences;
    private boolean iStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote2);
        init();
    }

    @Override
    protected void onDestroy() {
        iStop = true;
        super.onDestroy();
    }

    private void init() {
        app = (ApplicationUtil) Remote2Activity.this.getApplication();
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(this);
        btnRight = (Button) findViewById(R.id.btn_right);
        btnRight.setOnClickListener(this);
        btnLeft = (Button) findViewById(R.id.btn_left);
        btnLeft.setOnClickListener(this);
        btnUp = (Button) findViewById(R.id.btn_up);
        btnUp.setOnClickListener(this);
        btnDown = (Button) findViewById(R.id.btn_down);
        btnDown.setOnClickListener(this);
        btnVolume = (Button) findViewById(R.id.btn_volume);
        btnVolume.setOnClickListener(this);
        btnBright = (Button) findViewById(R.id.btn_bright);
        btnBright.setOnClickListener(this);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);
        mIat = SpeechRecognizer.createRecognizer(Remote2Activity.this, mInitListener);
        mIatDialog = new RecognizerDialog(Remote2Activity.this, mInitListener);
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        if (v == btnLeft) {
            sendMessage("remote", "15");
        } else if (v == btnRight) {
            sendMessage("remote", "14");
        } else if (v == btnUp) {
            sendMessage("remote", "13");
        } else if (v == btnDown) {
            sendMessage("remote", "12");
        } else if (v == btnPlay) {
            sendMessage("remote", "16");
        } else if (v == btnBack) {
            iStop = true;
            setResult(RESULT_OK, null);
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (v == btnVoice) {
            FlowerCollector.onEvent(Remote2Activity.this, "iat_recognize");
            mIatResults.clear();
            setParam();
            mIatDialog.setListener(mRecognizerDialogListener);
            mIatDialog.show();
            showTip(getString(R.string.text_begin));
        } else if (v == btnVolume) {
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.seekbar_dialog_view, null);
            final Dialog dialog = new AlertDialog.Builder(Remote2Activity.this).create();
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
//                intent.setClass(MouseActivity.this, BrightActivity.class);
//                startActivity(intent);
//                this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.seekbar_dialog_view, null);
            final Dialog dialog = new AlertDialog.Builder(Remote2Activity.this).create();
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
        }
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

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
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
                Log.e("IP & PORT", "in");
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
            intent.setClass(Remote2Activity.this, PowerActivity.class);
            startActivity(intent);
            this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (result.contains("重启")) {
            Intent intent = new Intent();
            intent.putExtra("what", 2);
            intent.setClass(Remote2Activity.this, PowerActivity.class);
            startActivity(intent);
            this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (result.contains("注销")) {
            Intent intent = new Intent();
            intent.putExtra("what", 3);
            intent.setClass(Remote2Activity.this, PowerActivity.class);
            startActivity(intent);
            this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (result.contains("截屏") || result.contains("截图")) {
            Intent intent = new Intent();
            intent.putExtra("what", 1);
            intent.setClass(Remote2Activity.this, ShotActivity.class);
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
}
