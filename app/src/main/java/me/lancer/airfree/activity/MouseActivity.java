package me.lancer.airfree.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.SeekBar;
import android.widget.TextView;

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

public class MouseActivity extends BaseActivity implements View.OnTouchListener,
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, OnClickListener {

    ApplicationUtil app;

    private TextView tvShow;
    private EditText etKeyboard;
    private RelativeLayout rlTouch, rlKM, rlVM;
    private LinearLayout llPPt;
    private Button btnBack, btnOption, btnLeft, btnRight, btnRollUp, btnRollDown, btnVoice, btnVolume, btnBright,
            btnPlay, btnPageUp , btnPageDown, btnPause, btnRewind, btnForward, btnTurnUp, btnTurnDown,
            btnPlayPause, btnFocus;
    private ListView lvOption;
    private SeekBar sbVolume, sbBright;
//    private VerticalSeekBar vsbVolume, vsbBright;
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
    final int KEYBOARDEVENTF = 0x000A;

    private int clickButton = 0;
    private boolean isLongPress = false;
    private float touchMoveX = 0, touchMoveY = 0;
    private Thread mThread = null;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private SharedPreferences mSharedPreferences;
    private boolean iStop = false;

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strShow = "";
    private String strCommon = "";
    private String strShowPPT = "";
    private String strPlayVideo = "";

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

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strShow = getResources().getString(R.string.keyboard_mouse_zn);
            strCommon = getResources().getString(R.string.common_zn);
            strShowPPT = getResources().getString(R.string.show_ppt_zn);
            strPlayVideo = getResources().getString(R.string.play_video_zn);
        } else if (language.equals("en")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_en);
            strNoConnection = getResources().getString(R.string.no_connection_en);
            strConnectionFailed = getResources().getString(R.string.connection_failed_en);
            strShow = getResources().getString(R.string.keyboard_mouse_en);
            strCommon = getResources().getString(R.string.common_en);
            strShowPPT = getResources().getString(R.string.show_ppt_en);
            strPlayVideo = getResources().getString(R.string.play_video_en);
        }
    }

    private void init() {
        iLanguage();
        app = (ApplicationUtil) MouseActivity.this.getApplication();
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvShow.setText(strShow);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnOption = (Button) findViewById(R.id.btn_option);
        btnOption.setOnClickListener(this);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(this);
        btnRight = (Button) findViewById(R.id.btn_right);
        btnRight.setOnClickListener(onButtonClickListener);
        btnLeft = (Button) findViewById(R.id.btn_left);
        btnLeft.setOnClickListener(onButtonClickListener);
        btnRollUp = (Button) findViewById(R.id.btn_roll_up);
        btnRollUp.setOnClickListener(onButtonClickListener);
        btnRollUp.setLongClickable(true);
        btnRollUp.setOnLongClickListener(onButtonLongClickListener);
        btnRollDown = (Button) findViewById(R.id.btn_roll_down);
        btnRollDown.setOnClickListener(onButtonClickListener);
        btnRollDown.setLongClickable(true);
        btnRollDown.setOnLongClickListener(onButtonLongClickListener);
        btnVolume = (Button) findViewById(R.id.btn_volume);
        btnVolume.setOnClickListener(this);
        btnBright = (Button) findViewById(R.id.btn_bright);
        btnBright.setOnClickListener(this);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnPause.setOnClickListener(this);
        btnPageUp = (Button) findViewById(R.id.btn_page_up);
        btnPageUp.setOnClickListener(this);
        btnPageDown = (Button) findViewById(R.id.btn_page_down);
        btnPageDown.setOnClickListener(this);
        btnTurnUp = (Button) findViewById(R.id.btn_turn_up);
        btnTurnUp.setOnClickListener(this);
        btnTurnDown = (Button) findViewById(R.id.btn_turn_down);
        btnTurnDown.setOnClickListener(this);
        btnRewind = (Button) findViewById(R.id.btn_rewind);
        btnRewind.setOnClickListener(this);
        btnForward = (Button) findViewById(R.id.btn_forward);
        btnForward.setOnClickListener(this);
        btnPlayPause = (Button) findViewById(R.id.btn_play_pause);
        btnPlayPause.setOnClickListener(this);
        btnFocus = (Button) findViewById(R.id.btn_focus);
        btnFocus.setOnClickListener(this);
        mGestureDetector = new GestureDetector(this);
        rlTouch = (RelativeLayout) findViewById(R.id.rl_touch);
        rlTouch.setOnTouchListener(this);
        rlTouch.setLongClickable(true);
        rlKM = (RelativeLayout) findViewById(R.id.rl_km);
        llPPt = (LinearLayout) findViewById(R.id.ll_ppt);
        rlVM = (RelativeLayout) findViewById(R.id.rl_vm);
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
                case R.id.btn_left:
                    sendMessage("mouse", MOUSEEVENTF_TAP + "");
                    break;
                case R.id.btn_right:
                    sendMessage("mouse", MOUSEEVENTF_RIGHTUP + "");
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
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (v == btnOption){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.option_dialog_view, null);
            final Dialog dialog = new AlertDialog.Builder(MouseActivity.this).create();
            lvOption = (ListView) layout.findViewById(R.id.lv_option);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1);
            adapter.add(strCommon);
            adapter.add(strShowPPT);
            adapter.add(strPlayVideo);
            lvOption.setAdapter(adapter);
            lvOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switchUI(position);
                }
            });
            dialog.show();
            Window window = dialog.getWindow();
            window.setContentView(layout);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.height = 525;
            lp.width = 390;
            window.setGravity(Gravity.RIGHT | Gravity.TOP);
            window.setAttributes(lp);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else if (v == btnVoice) {
            FlowerCollector.onEvent(MouseActivity.this, "iat_recognize");
            mIatResults.clear();
            setParam();
            mIatDialog.setListener(mRecognizerDialogListener);
            mIatDialog.show();
            showTip(getString(R.string.text_begin));
        } else if (v == btnVolume) {
//                Intent intent = new Intent();
//                intent.setClass(MouseActivity.this, VolumeActivity.class);
//                startActivity(intent);
//                this.getParent().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.seekbar_dialog_view, null);
            final Dialog dialog = new AlertDialog.Builder(MouseActivity.this).create();
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
            final Dialog dialog = new AlertDialog.Builder(MouseActivity.this).create();
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
        } else if (v == btnLeft){
            sendMessage("mouse", MOUSEEVENTF_TAP + "");
        } else if (v == btnRight){
            sendMessage("mouse", MOUSEEVENTF_RIGHTUP + "");
        } else if (v == btnPlay){
            sendMessage("remote", "17");
        } else if (v == btnPause){
            sendMessage("remote", "18");
        } else if (v == btnPageUp){
            sendMessage("remote", "13");
        } else if (v == btnPageDown){
            sendMessage("remote", "12");
        } else if (v == btnPlayPause){
            sendMessage("remote", "16");
        } else if (v == btnTurnUp){
            sendMessage("remote", "13");
        } else if (v == btnTurnDown){
            sendMessage("remote", "12");
        } else if (v == btnRewind){
            sendMessage("remote", "15");
        } else if (v == btnForward){
            sendMessage("remote", "14");
        } else if (v == btnFocus){
            sendMessage("remote", "20");
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

    private void switchUI(int flag){
        if(flag == 0){
            rlKM.setVisibility(View.VISIBLE);
            etKeyboard.setVisibility(View.VISIBLE);
            llPPt.setVisibility(View.GONE);
            rlVM.setVisibility(View.GONE);
        } else if (flag == 1){
            rlKM.setVisibility(View.GONE);
            etKeyboard.setVisibility(View.GONE);
            llPPt.setVisibility(View.VISIBLE);
            rlVM.setVisibility(View.GONE);
        } else if (flag == 2){
            rlKM.setVisibility(View.GONE);
            etKeyboard.setVisibility(View.GONE);
            llPPt.setVisibility(View.GONE);
            rlVM.setVisibility(View.VISIBLE);
        }
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
