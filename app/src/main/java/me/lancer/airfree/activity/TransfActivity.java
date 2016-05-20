package me.lancer.airfree.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class TransfActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout btnComputer, btnDevice, btnSdcard, btnImage, btnMusic,btnVideo, btnDocument, btnApp, btnDownload;

    ApplicationUtil app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transf);
        init();
    }

    private void init() {
        app = (ApplicationUtil) TransfActivity.this.getApplication();
        btnComputer = (LinearLayout) findViewById(R.id.btn_computer);
        btnComputer.setOnClickListener(this);
        btnDevice = (LinearLayout) findViewById(R.id.btn_in);
        btnDevice.setOnClickListener(this);
        btnSdcard = (LinearLayout) findViewById(R.id.btn_out);
        btnSdcard.setOnClickListener(this);
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
        if (v == btnComputer) {
            if (app.isConnecting()) {
                Intent intent = new Intent();
                intent.setClass(TransfActivity.this, ComputerActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "没有连接!", Toast.LENGTH_SHORT).show();
            }
        } else if (v == btnDevice) {
            Bundle bundle = new Bundle();
            bundle.putString("method", "in");
            bundle.putStringArrayList("source", new ArrayList<String>());
            Intent intent = new Intent();
            intent.setClass(TransfActivity.this, DeviceActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnSdcard) {
            Bundle bundle = new Bundle();
            bundle.putString("method", "out");
            bundle.putStringArrayList("source", new ArrayList<String>());
            Intent intent = new Intent();
            intent.setClass(TransfActivity.this, DeviceActivity.class);
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
            intent.setClass(TransfActivity.this, AppActivity.class);
            startActivity(intent);
        } else if (v == btnDownload) {
            Bundle bundle = new Bundle();
            bundle.putString("method", "download");
            bundle.putStringArrayList("source", new ArrayList<String>());
            Intent intent = new Intent();
            intent.setClass(TransfActivity.this, DeviceActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
