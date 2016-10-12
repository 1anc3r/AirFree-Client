package me.lancer.airfree.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;
import me.lancer.airfree.util.GestureUtil;

public class GestureActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;

    private TextView tvShow, tvGestureGuide;
    private GestureOverlayView govTouch;
    private Button btnBack;
    private LinearLayout btnGestureProblem;

    private GestureLibrary mGestureLib;

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strShow = "";
    private String strCmd = "";
    private String strTaskManager = "";
    private String strExplorer = "";
    private String strDeviceManager = "";
    private String strDiskManager = "";
    private String strRegistryEditor = "";
    private String strCalculator = "";
    private String strNotepad = "";
    private String strPaint = "";
    private String strWrite = "";
    private String strBrowser = "";
    private String strGestureGuide = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
        init();
    }

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strShow = getResources().getString(R.string.gesture_zn);
            strCmd = getResources().getString(R.string.cmd_zn);
            strTaskManager = getResources().getString(R.string.task_manager_zn);
            strExplorer = getResources().getString(R.string.explorer_zn);
            strDeviceManager = getResources().getString(R.string.device_manager_zn);
            strDiskManager = getResources().getString(R.string.disk_manager_zn);
            strRegistryEditor = getResources().getString(R.string.registry_editor_zn);
            strCalculator = getResources().getString(R.string.calculator_zn);
            strNotepad = getResources().getString(R.string.notepad_zn);
            strPaint = getResources().getString(R.string.paint_zn);
            strWrite = getResources().getString(R.string.write_zn);
            strBrowser = getResources().getString(R.string.browser_zn);
            strGestureGuide = "手势说明";
        } else if (language.equals("en")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_en);
            strNoConnection = getResources().getString(R.string.no_connection_en);
            strConnectionFailed = getResources().getString(R.string.connection_failed_en);
            strShow = getResources().getString(R.string.gesture_en);
            strCmd = getResources().getString(R.string.cmd_en);
            strTaskManager = getResources().getString(R.string.task_manager_en);
            strExplorer = getResources().getString(R.string.explorer_en);
            strDeviceManager = getResources().getString(R.string.device_manager_en);
            strDiskManager = getResources().getString(R.string.disk_manager_en);
            strRegistryEditor = getResources().getString(R.string.registry_editor_en);
            strCalculator = getResources().getString(R.string.calculator_en);
            strNotepad = getResources().getString(R.string.notepad_en);
            strPaint = getResources().getString(R.string.paint_en);
            strWrite = getResources().getString(R.string.write_en);
            strBrowser = getResources().getString(R.string.browser_en);
            strGestureGuide = "Gesture Guide";
        }
    }

    private void init() {
        iLanguage();
        app = (ApplicationUtil) GestureActivity.this.getApplication();
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvShow.setText(strShow);
        tvGestureGuide = (TextView) findViewById(R.id.tv_gesture_guide);
        tvGestureGuide.setText(strGestureGuide);
        final List<String> lLetter = new ArrayList<>();
        List<String> lString = new ArrayList<>();
        lLetter.add("c");
        lString.add(strCmd);
        lLetter.add("t");
        lString.add(strTaskManager);
        lLetter.add("e/z");
        lString.add(strExplorer);
        lLetter.add("s");
        lString.add(strDeviceManager);
        lLetter.add("d");
        lString.add(strDiskManager);
        lLetter.add("r");
        lString.add(strRegistryEditor);
        lLetter.add("j");
        lString.add(strCalculator);
        lLetter.add("n");
        lString.add(strNotepad);
        lLetter.add("h/p");
        lString.add(strPaint);
        lLetter.add("w/x");
        lString.add(strWrite);
        lLetter.add("b/l");
        lString.add(strBrowser);
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
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
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
            String[] array = null;
            if (language.equals("zn")) {
                array = getResources().getStringArray(R.array.gesture_entries_zn);
            } else if (language.equals("en")){
                array = getResources().getStringArray(R.array.gesture_entries_en);
            }
            new AlertDialog.Builder(GestureActivity.this)
                    .setItems(array,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendMessage("remote", "" + (which + 1));
                                }
                            })
                    .show();
        } else if (v == btnBack){
            setResult(RESULT_OK, null);
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        }
    }
}
