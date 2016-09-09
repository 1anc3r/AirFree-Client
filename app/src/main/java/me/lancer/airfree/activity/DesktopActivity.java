package me.lancer.airfree.activity;

import android.os.Bundle;
import android.os.Handler;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.airfree.util.EnumMessageInfo;
import me.lancer.distance.R;

public class DesktopActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    ApplicationUtil app;

    private ImageView ivDestop;
    private View vTouchPad;
    private EditText etKeyboard;
    private Button btnLeft, btnRight, btnBack;

    String delim = new String("!!");

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

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case EnumMessageInfo.MsgBitmapGenerated:
                    Bitmap mBitmap = app.mBitmap;
                    if (mBitmap != null) {
                        ivDestop.setImageBitmap(mBitmap);
                        mBitmap = null;
                        app.mBitmap = null;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = DesktopActivity.this.getWindow();
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_desktop);

        init();
    }

    private void init() {
        app = (ApplicationUtil) DesktopActivity.this.getApplication();

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        vTouchPad = findViewById(R.id.ll_touch_pad);
        vTouchPad.setOnTouchListener(this);
        ivDestop = (ImageView) findViewById(R.id.iv_desktop);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnLeft = (Button) findViewById(R.id.btn_left);
        btnLeft.setOnClickListener(this);
        btnRight = (Button) findViewById(R.id.btn_right);
        btnRight.setOnClickListener(this);
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

    public void onStart() {
        super.onStart();

        app.setmHandler(mHandler);
        sendImgMessage(new String("Init"));

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (app.connected) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!app.connected) {
                        finish();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendTxtMessage("Close");
        app.connected = false;
    }

    @Override
    public void onBackPressed() {
        app.stopThread();
        super.onBackPressed();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        viewTouchListener(event);
        return true;
    }

    private void viewTouchListener(MotionEvent event) {
        StringBuilder sb = new StringBuilder();

        int action = event.getAction();
        int pointerCount = event.getPointerCount();

        if(pointerCount == 1){
            switch(action){
                case 0: sb.append("DOWN"+delim);
                    sb.append((int)event.getX()+delim);
                    sb.append((int)event.getY()+delim);
                    break;

                case 1: sb.append("UP"+delim);
                    sb.append(event.getDownTime()+delim);
                    sb.append(event.getEventTime());
                    break;

                case 2: sb.append("MOVE"+delim);
                    sb.append((int)event.getX()+delim);
                    sb.append((int)event.getY());
                    break;

                default: break;
            }
        } else if(pointerCount == 2){
            sb.append("SCROLL"+delim);
            if(action == 2){
                sb.append("MOVE"+delim);
                sb.append((int)event.getX()+delim);
                sb.append((int)event.getY());
            } else {
                sb.append("DOWN");
            }
        }
        sendTxtMessage(sb.toString());
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            sendTxtMessage("Close");
            app.connected = false;
            setResult(RESULT_OK, null);
            finish();
        } else if (v == btnLeft) {
            sendMessage("mouse", MOUSEEVENTF_TAP + "");
        } else if (v == btnRight) {
            sendMessage("mouse", MOUSEEVENTF_RIGHTUP + "");
        }
    }

    private void sendTxtMessage(String message) {
        if (app.connected) {
            app.sendTxtMessage(message);
        } else {
            finish();
        }
    }

    private void sendImgMessage(String message) {
        if (app.connected) {
            app.sendImgMessage(message);
        } else {
            finish();
        }
    }
}
