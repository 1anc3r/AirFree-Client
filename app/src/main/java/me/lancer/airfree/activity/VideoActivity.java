package me.lancer.airfree.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;
import me.lancer.airfree.adapter.VideoAdapter;
import me.lancer.airfree.bean.VideoBean;

public class VideoActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;
    private TextView tvShow;
    private Button btnBack;
    private GridView mGroupGridView;
    private ProgressDialog mProgressDialog;
    private LinearLayout llBottom, btnDelete, btnCopy, btnMove, btnShare, btnAll;
    private TextView tvDelete, tvCopy, tvMove, tvShare, tvAll;

    private final static int SCAN_OK = 1;

    private VideoAdapter adapter;
    private List<VideoBean> videoList = new ArrayList<>();
    private List<String> posList = new ArrayList<>();
    private Boolean isall = false;

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strShow = "";
    private String strNoInternalExternalStorage = "";
    private String strLoading = "";
    private String strDelete = "";
    private String strCopy = "";
    private String strCut = "";
    private String strUpload = "";
    private String strAll = "";
    private String strPaste = "";
    private String strCancel = "";
    
    private Handler vHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    mProgressDialog.dismiss();
                    adapter = new VideoAdapter(VideoActivity.this, videoList, posList, mGroupGridView, posHandler);
                    mGroupGridView.setAdapter(adapter);
                    break;
            }
        }
    };
    
    public Handler posHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (posList.contains(msg.obj)) {
                posList.remove(msg.obj);
            } else {
                posList.add((String) msg.obj);
                Collections.sort(posList, mComparator);
            }
            if (posList.isEmpty()) {
                llBottom.setVisibility(View.GONE);
            } else {
                llBottom.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        iLanguage();
        getVideo();
        init();
    }

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strShow = getResources().getString(R.string.video_zn);
            strNoInternalExternalStorage = getResources().getString(R.string.no_internal_external_storage_zn);
            strLoading = getResources().getString(R.string.loading_zn);
            strDelete = getResources().getString(R.string.delete_zn);
            strCopy = getResources().getString(R.string.copy_zn);
            strCut = getResources().getString(R.string.cut_zn);
            strUpload = getResources().getString(R.string.upload_zn);
            strAll = getResources().getString(R.string.all_zn);
            strPaste = getResources().getString(R.string.paste_zn);
            strCancel = getResources().getString(R.string.cancel_zn);
        } else if (language.equals("en")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_en);
            strNoConnection = getResources().getString(R.string.no_connection_en);
            strConnectionFailed = getResources().getString(R.string.connection_failed_en);
            strShow = getResources().getString(R.string.video_en);
            strNoInternalExternalStorage = getResources().getString(R.string.no_internal_external_storage_en);
            strLoading = getResources().getString(R.string.loading_en);
            strDelete = getResources().getString(R.string.delete_en);
            strCopy = getResources().getString(R.string.copy_en);
            strCut = getResources().getString(R.string.cut_en);
            strUpload = getResources().getString(R.string.upload_en);
            strAll = getResources().getString(R.string.all_en);
            strPaste = getResources().getString(R.string.paste_en);
            strCancel = getResources().getString(R.string.cancel_en);
        }
    }

    private void init() {
        app = (ApplicationUtil) VideoActivity.this.getApplication();
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvShow.setText(strShow);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        mGroupGridView = (GridView) findViewById(R.id.gv_all);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        tvDelete.setText(strDelete);
        tvCopy = (TextView) findViewById(R.id.tv_copy);
        tvCopy.setText(strCopy);
        tvMove = (TextView) findViewById(R.id.tv_cut);
        tvMove.setText(strCut);
        tvShare = (TextView) findViewById(R.id.tv_upload);
        tvShare.setText(strUpload);
        tvAll = (TextView) findViewById(R.id.tv_all);
        tvAll.setText(strAll);
        btnDelete = (LinearLayout) findViewById(R.id.btn_del);
        btnDelete.setOnClickListener(this);
        btnCopy = (LinearLayout) findViewById(R.id.btn_copy);
        btnCopy.setOnClickListener(this);
        btnMove = (LinearLayout) findViewById(R.id.btn_move);
        btnMove.setOnClickListener(this);
        btnShare = (LinearLayout) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        btnAll = (LinearLayout) findViewById(R.id.btn_all);
        btnAll.setOnClickListener(this);
    }

    private void getVideo() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ShowToast(strNoInternalExternalStorage);
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, strLoading);
        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = VideoActivity.this.getContentResolver();
                Cursor vCursor = mContentResolver.query(mVideoUri, null, null, null, null);
                while (vCursor.moveToNext()) {
                    String path = vCursor.getString(vCursor
                            .getColumnIndex(MediaStore.Video.Media.DATA));
                    String title = vCursor.getString(vCursor
                            .getColumnIndex(MediaStore.Video.Media.TITLE));
                    int id = vCursor.getInt(vCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String arg = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";
                    String[] args = new String[]{id + ""};
                    Cursor tCursor = mContentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, null, arg, args, null);
                    String thumb = "";
                    if (tCursor.moveToFirst()) {
                        thumb = tCursor.getString(tCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                    }
                    tCursor.close();
                    VideoBean item = new VideoBean(path, title, thumb);
                    videoList.add(item);
                }
                vCursor.close();
                vHandler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            setResult(RESULT_OK, null);
            finish();
        } else if (v == btnDelete) {
            Handler dHandler = new Handler();
            dHandler.post(deleteFile);
        } else if (v == btnAll) {
            Handler aHandler = new Handler();
            aHandler.post(selectAllFile);
        } else if (v == btnCopy) {
            List<String> portal = new ArrayList<>();
            for (int i = 0; i < posList.size(); i++) {
                portal.add(videoList.get(Integer.parseInt(posList.get(i))).getVideoPath());
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "copy");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(VideoActivity.this, MobileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnMove) {
            List<String> portal = new ArrayList<>();
            for (int i = 0; i < posList.size(); i++) {
                portal.add(videoList.get(Integer.parseInt(posList.get(i))).getVideoPath());
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "move");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(VideoActivity.this, MobileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnShare) {
            if (app.getmPrintWriterClient() != null) {
                pref = PreferenceManager.getDefaultSharedPreferences(this);
                String ip = pref.getString("ip", "");
                String filename = "";
                for (int i = 0; i < posList.size(); i++) {
                    filename = videoList.get(Integer.parseInt(posList.get(i))).getVideoPath();
                    sendMessage("file", filename);
                    new SendTask(ip, "59672", filename).execute();
                    Log.e("IP & PORT", filename);
                }
            } else {
                Toast.makeText(this, strNoConnection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    Runnable deleteFile = new Runnable() {

        @Override
        public void run() {
            int count = 0;
            Collections.sort(posList, mComparator);
            Log.e("IP & PORT", "" + posList);
            while (!posList.isEmpty()) {
                String deletePath = videoList.get(Integer.parseInt(posList.get(0)) - count).getVideoPath();
                File deleteFile = new File(deletePath);
                if (deleteFile.exists() && deleteFile.isFile() && deleteFile.canWrite()) {
                    deleteFile.delete();
                    posList.remove(posList.get(0));
                    Log.e("IP & PORT", "删除成功");
                } else {
                    Log.e("IP & PORT", "删除失败");
                }
                count++;
            }
            adapter.notifyDataSetChanged();
        }
    };

    Runnable selectAllFile = new Runnable() {

        @Override
        public void run() {
            if (isall == false) {
                posList.clear();
                for (int i = 0; i < videoList.size(); i++) {
                    posList.add("" + i);
                }
                isall = true;
                llBottom.setVisibility(View.VISIBLE);
            } else {
                posList.clear();
                isall = false;
                llBottom.setVisibility(View.GONE);
            }
            Log.e("IP & PORT", "" + posList);
            adapter.notifyDataSetChanged();
        }
    };

    Comparator mComparator = new Comparator() {
        public int compare(Object obj1, Object obj2) {
            String str1 = (String) obj1;
            String str2 = (String) obj2;
            if (Integer.parseInt(str1) < Integer.parseInt(str2))
                return -1;
            else if (Integer.parseInt(str1) == Integer.parseInt(str2))
                return 0;
            else if (Integer.parseInt(str1) > Integer.parseInt(str2))
                return 1;
            return 0;
        }
    };
}
