package me.lancer.airfree.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;
import me.lancer.airfree.util.GestureUtil;

public class GestureActivity extends BaseActivity implements View.OnClickListener {

    private GestureLibrary mGestureLib;
    private GestureOverlayView govTouch;
    private LinearLayout btnGestureProblem;

    ApplicationUtil app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
        init();
    }

    private void init() {
        app = (ApplicationUtil) GestureActivity.this.getApplication();
        final List<String> lLetter = new ArrayList<>();
        List<String> lString = new ArrayList<>();
        lLetter.add("c");
        lString.add("命令提示符");
        lLetter.add("t");
        lString.add("任务管理器");
        lLetter.add("a/e/z");
        lString.add("资源管理器");
        lLetter.add("s");
        lString.add("设备管理器");
        lLetter.add("d");
        lString.add("磁盘管理器");
        lLetter.add("r");
        lString.add("注册表编辑器");
        lLetter.add("j");
        lString.add("计算器");
        lLetter.add("n");
        lString.add("记事本");
        lLetter.add("h/m/p");
        lString.add("画图板");
        lLetter.add("w/x");
        lString.add("写字板");
        lLetter.add("b/l");
        lString.add("浏览器");
        govTouch = (GestureOverlayView) findViewById(R.id.gov_touch);
        govTouch.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
        govTouch.setFadeOffset(1500);
        govTouch.setGestureColor(Color.WHITE);
        govTouch.setGestureStrokeWidth(20);
        govTouch.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {

            @Override
            public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
                ArrayList<Prediction> predictions = mGestureLib
                        .recognize(gesture);
                if (predictions.size() > 0) {
                    Prediction prediction = predictions.get(0);
                    if (prediction.score > 1.0) {
                        String tmp = GestureUtil.getCommand(prediction.name);
                        if (!tmp.equals("")) {
                            sendMessage("remote", tmp);
                        }
                    }
                }
            }
        });
        if (mGestureLib == null) {
            mGestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
            mGestureLib.load();
        }
        btnGestureProblem = (LinearLayout) findViewById(R.id.btn_gesture_problem);
        btnGestureProblem.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == btnGestureProblem) {
            new AlertDialog.Builder(GestureActivity.this)
                    .setItems(this.getResources().getStringArray(R.array.gesture_entries), null)
                    .show();
        }
    }
}
