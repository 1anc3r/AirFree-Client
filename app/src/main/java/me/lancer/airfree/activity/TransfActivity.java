package me.lancer.airfree.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class TransfActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;

    LinearLayout btnDirectory, btnInternal, btnExternal, btnImage, btnMusic,btnVideo, btnDocument, btnApp, btnDownload;
    TextView tvDirectory, tvInternal, tvExternal, tvImage, tvMusic,tvVideo, tvDocument, tvApp, tvDownload;

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strDirectory = "";
    private String strInternal = "";
    private String strExternal = "";
    private String strImage = "";
    private String strMusic = "";
    private String strVideo = "";
    private String strDocument = "";
    private String strApplication = "";
    private String strDownload = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transf);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("IP & PORT", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("IP & PORT", "onPause");
    }

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strDirectory = getResources().getString(R.string.directory_zn);
            strInternal = getResources().getString(R.string.internal_zn);
            strExternal = getResources().getString(R.string.external_zn);
            strImage = getResources().getString(R.string.image_zn);
            strMusic = getResources().getString(R.string.music_zn);
            strVideo = getResources().getString(R.string.video_zn);
            strDocument = getResources().getString(R.string.document_zn);
            strApplication = getResources().getString(R.string.application_zn);
            strDownload = getResources().getString(R.string.download_zn);
        } else if (language.equals("en")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_en);
            strNoConnection = getResources().getString(R.string.no_connection_en);
            strConnectionFailed = getResources().getString(R.string.connection_failed_en);
            strDirectory = getResources().getString(R.string.directory_en);
            strInternal = getResources().getString(R.string.internal_en);
            strExternal = getResources().getString(R.string.external_en);
            strImage = getResources().getString(R.string.image_en);
            strMusic = getResources().getString(R.string.music_en);
            strVideo = getResources().getString(R.string.video_en);
            strDocument = getResources().getString(R.string.document_en);
            strApplication = getResources().getString(R.string.application_en);
            strDownload = getResources().getString(R.string.download_en);
        }
    }

    private void init() {
        iLanguage();
        app = (ApplicationUtil) TransfActivity.this.getApplication();
        tvDirectory = (TextView) findViewById(R.id.tv_directory);
        tvDirectory.setText(strDirectory);
        tvInternal = (TextView) findViewById(R.id.tv_internal);
        tvInternal.setText(strInternal);
        tvExternal = (TextView) findViewById(R.id.tv_external);
        tvExternal.setText(strExternal);
        tvImage = (TextView) findViewById(R.id.tv_image);
        tvImage.setText(strImage);
        tvMusic = (TextView) findViewById(R.id.tv_music);
        tvMusic.setText(strMusic);
        tvVideo = (TextView) findViewById(R.id.tv_video);
        tvVideo.setText(strVideo);
        tvDocument = (TextView) findViewById(R.id.tv_document);
        tvDocument.setText(strDocument);
        tvApp = (TextView) findViewById(R.id.tv_application);
        tvApp.setText(strApplication);
        tvDownload = (TextView) findViewById(R.id.tv_download);
        tvDownload.setText(strDownload);
        btnDirectory = (LinearLayout) findViewById(R.id.btn_computer);
        btnDirectory.setOnClickListener(this);
        btnInternal = (LinearLayout) findViewById(R.id.btn_in);
        btnInternal.setOnClickListener(this);
        btnExternal = (LinearLayout) findViewById(R.id.btn_out);
        btnExternal.setOnClickListener(this);
        btnImage = (LinearLayout) findViewById(R.id.btn_image);
        btnImage.setOnClickListener(this);
        btnMusic = (LinearLayout) findViewById(R.id.btn_music);
        btnMusic.setOnClickListener(this);
        btnVideo = (LinearLayout) findViewById(R.id.btn_video);
        btnVideo.setOnClickListener(this);
        btnDocument = (LinearLayout) findViewById(R.id.btn_doc);
        btnDocument.setOnClickListener(this);
        btnApp = (LinearLayout) findViewById(R.id.btn_app);
        btnApp.setOnClickListener(this);
        btnDownload = (LinearLayout) findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnDirectory) {
            if (app.isConnecting()) {
                Intent intent = new Intent();
                intent.setClass(TransfActivity.this, DirectoryActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, strNoConnection, Toast.LENGTH_SHORT).show();
            }
        } else if (v == btnInternal) {
            Bundle bundle = new Bundle();
            bundle.putString("method", "in");
            bundle.putStringArrayList("source", new ArrayList<String>());
            Intent intent = new Intent();
            intent.setClass(TransfActivity.this, MobileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnExternal) {
            Bundle bundle = new Bundle();
            bundle.putString("method", "out");
            bundle.putStringArrayList("source", new ArrayList<String>());
            Intent intent = new Intent();
            intent.setClass(TransfActivity.this, MobileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnImage) {
            Intent intent = new Intent();
            intent.setClass(TransfActivity.this, ImageActivity.class);
            startActivity(intent);
        } else if (v == btnDocument) {
            Intent intent = new Intent();
            intent.setClass(TransfActivity.this, DocumentActivity.class);
            startActivity(intent);
        } else if (v == btnVideo) {
            Intent intent = new Intent();
            intent.setClass(TransfActivity.this, VideoActivity.class);
            startActivity(intent);
        } else if (v == btnMusic) {
            Intent intent = new Intent();
            intent.setClass(TransfActivity.this, MusicActivity.class);
            startActivity(intent);
        } else if (v == btnApp) {
            Intent intent = new Intent();
            intent.setClass(TransfActivity.this, ApplicationActivity.class);
            startActivity(intent);
        } else if (v == btnDownload) {
            Bundle bundle = new Bundle();
            bundle.putString("method", "download");
            bundle.putStringArrayList("source", new ArrayList<String>());
            Intent intent = new Intent();
            intent.setClass(TransfActivity.this, MobileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
