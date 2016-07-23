package me.lancer.airfree.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

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

import me.lancer.distance.R;
import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.airfree.util.IatSettings;
import me.lancer.airfree.util.JsonParser;
import me.lancer.airfree.util.VerticalSeekBar;

public class MouseActivity extends BaseActivity implements View.OnTouchListener,
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, OnClickListener {

    ApplicationUtil app;

    private EditText etKeyboard;
    private RelativeLayout rlTouch;
    private Button btnBack, btnLeft, btnRight, btnRollUp, btnRollDown, btnVoice;
    private VerticalSeekBar vsbVolume, vsbBright;
    private GestureDetector mGestureDetector;
    private RecognizerDialog mIatDialog;
    private SpeechRecognizer mIat;

    private static String TAG = MouseActivity.class.getSimpleName();
    final int MOUSEEVENTF_CANCEL = 0x0001;      // 移动
    final int MOUSEEVENTF_MOVE = 0x0001;        // 移动
    final int MOUSEEVENTF_LEFTDOWN = 0x0002;    // 按下左键
    final int MOUSEEVENTF_LEFTUP = 0x0003;      // 松开左键
    final int MOUSEEVENTF_RIGHTDOWN = 0x0004;   // 按下右键
    final int MOUSEEVENTF_RIGHTUP = 0x0005;     // 松开右键
    final int MOUSEEVENTF_TAP = 0x0006;         // 单击
    final int MOUSEEVENTF_DOUBLETAP = 0x0007;   // 双击
    final int MOUSEEVENTF_ROLLUP = 0x0008;      // 向上拖动滚动
    final int MOUSEEVENTF_ROLLDOWN = 0x0009;    // 向下拖动滚动
    final int KEYBOARDEVENTF = 0x000A;          // 向下拖动滚动

    private int clickButton = 0;
    private boolean isLongPress = false;
    private float touchMoveX = 0, touchMoveY = 0;
    private Thread mThread = null;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private SharedPreferences mSharedPreferences;
    private boolean iStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);
        init();
    }

    @Override
    protected void onDestroy() {
        iStop = true;
        super.onDestroy();
    }

    private void init() {
        app = (ApplicationUtil) MouseActivity.this.getApplication();
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(this);
        btnRight = (Button) findViewById(R.id.btn_right);
        btnRight.setOnClickListener(onButtonClickListener);
        btnLeft = (Button) findViewById(R.id.btn_left);
        btnLeft.setOnClickListener(onButtonClickListener);
        btnRollUp = (Button) findViewById(R.id.btn_roll_up);
        btnRollUp.setOnClickListener(onButtonClickListener);
        btnRollUp.setLongClickable(true);
//        btnRollUp.setOnLongClickListener(onButtonLongClickListener);
        btnRollDown = (Button) findViewById(R.id.btn_roll_down);
        btnRollDown.setOnClickListener(onButtonClickListener);
        btnRollDown.setLongClickable(true);
//        btnRollDown.setOnLongClickListener(onButtonLongClickListener);
        mGestureDetector = new GestureDetector(this);
        rlTouch = (RelativeLayout) findViewById(R.id.rl_touch);
        rlTouch.setOnTouchListener(this);
        rlTouch.setLongClickable(true);
//        vsbVolume = (VerticalSeekBar) findViewById(R.id.vsb_volume);
//        vsbVolume.setProgress(50);
//        vsbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                sendVolumeMessage(""+progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//
//        vsbBright = (VerticalSeekBar) findViewById(R.id.vsb_bright);
//        vsbBright.setProgress(50);
//        vsbBright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                sendBrightMessage(""+progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
        mIat = SpeechRecognizer.createRecognizer(MouseActivity.this, mInitListener);
        mIatDialog = new RecognizerDialog(MouseActivity.this, mInitListener);
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, MODE_PRIVATE);
        etKeyboard = (EditText) findViewById(R.id.et_keyboard);
        etKeyboard.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable e) {
                e.clear();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    char c = s.charAt(start);
                    sendMessage("keyboard", KEYBOARDEVENTF + ":" + c);
                } catch (Exception e) {
                }
            }
        });
    }

    private OnClickListener onButtonClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
                case R.id.btn_right:                                                        // 右击
                    sendMessage("mouse", MOUSEEVENTF_RIGHTUP + "");
                    break;
                case R.id.btn_left:                                                         // 左击
                    sendMessage("mouse", MOUSEEVENTF_TAP + "");
                    break;
                case R.id.btn_roll_up:                                                      // 向上拖动滚动
                    if (clickButton != 0) {
                        clickButton = 0;
                        mThread.interrupt();
                    } else
                        sendMessage("mouse", MOUSEEVENTF_ROLLUP + "");
                    break;
                case R.id.btn_roll_down:                                                    // 向下拖动滚动
                    if (clickButton != 0) {
                        clickButton = 0;
                        mThread.interrupt();
                    } else {
                        sendMessage("mouse", MOUSEEVENTF_ROLLDOWN + "");
                    }
                    break;
            }
        }
    };

    private OnLongClickListener onButtonLongClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            clickButton = v.getId();
            mThread = new Thread(mLongClickRunnable);
            mThread.start();
            return false;
        }
    };

    private Runnable mLongClickRunnable = new Runnable() {

        @Override
        public void run() {
            while (clickButton != 0) {
                if (clickButton == R.id.btn_roll_down)
                    sendMessage("mouse", MOUSEEVENTF_ROLLDOWN + "");
                else if (clickButton == R.id.btn_roll_up)
                    sendMessage("mouse", MOUSEEVENTF_ROLLUP + "");
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isLongPress) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (touchMoveX == 0) {
                    touchMoveX = event.getX();
                    touchMoveY = event.getY();
                } else {
                    sendMessage("mouse", MOUSEEVENTF_MOVE + ":"
                            + (event.getX() - touchMoveX) + ":"
                            + (event.getY() - touchMoveY));                                 // 左键弹起
                    touchMoveX = event.getX();
                    touchMoveY = event.getY();
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                isLongPress = false;
                sendMessage("mouse", MOUSEEVENTF_LEFTUP + "");                              // 左键弹起
            } else {
                touchMoveX = 0;
                isLongPress = false;
                sendMessage("mouse", MOUSEEVENTF_CANCEL + "");                              // 左键弹起
            }
            return false;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
//        sendMessage("mouse", MOUSEEVENTF_MOVE + ":" + velocityX + ":" + velocityY);       // 左键弹起
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2,
                            float distanceX, float distanceY) {
        sendMessage("mouse", MOUSEEVENTF_MOVE + ":" + -distanceX + ":" + -distanceY);       // 左键弹起
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        isLongPress = true;
        sendMessage("mouse", MOUSEEVENTF_LEFTDOWN + "");                                    // 左键按下
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        sendMessage("mouse", MOUSEEVENTF_TAP + "");
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        sendMessage("mouse", MOUSEEVENTF_DOUBLETAP + "");
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            iStop = true;
            setResult(RESULT_OK, null);
            finish();
        } else if (v == btnVoice) {
            FlowerCollector.onEvent(MouseActivity.this, "iat_recognize");
            mIatResults.clear();
            setParam();
            mIatDialog.setListener(mRecognizerDialogListener);
            mIatDialog.show();
            showTip(getString(R.string.text_begin));
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
        if (keyCode != KeyEvent.KEYCODE_ENTER) {
            sendMessage("keyboard", KEYBOARDEVENTF + ":" + event.getKeyCode());
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
        } else if (result.contains("打开记事本")) {
            sendMessage("remote", "8");
        } else if (result.contains("打开画图板")) {
            sendMessage("remote", "9");
        } else if (result.contains("打开写字板")) {
            sendMessage("remote", "0");
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
            intent.setClass(MouseActivity.this, PowerActivity.class);
            startActivity(intent);
            this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (result.contains("重启")) {
            Intent intent = new Intent();
            intent.putExtra("what", 2);
            intent.setClass(MouseActivity.this, PowerActivity.class);
            startActivity(intent);
            this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (result.contains("注销")) {
            Intent intent = new Intent();
            intent.putExtra("what", 3);
            intent.setClass(MouseActivity.this, PowerActivity.class);
            startActivity(intent);
            this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (result.contains("截屏") || result.contains("截图")) {
            Intent intent = new Intent();
            intent.putExtra("what", 1);
            intent.setClass(MouseActivity.this, ShotActivity.class);
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
