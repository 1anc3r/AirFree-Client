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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import me.lancer.airfree.adapter.MusicAdapter;
import me.lancer.airfree.model.MusicBean;
import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class MusicActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;
    private Button btnBack;
    private ListView lvMusic;
    private ProgressDialog mProgressDialog;
    private LinearLayout llBottom, btnDelete, btnCopy, btnMove, btnShare, btnAll;

    private final static int SCAN_OK = 1;

    private MusicAdapter adapter;
    private List<MusicBean> mp3List = new ArrayList<>();
    private List<String> posList = new ArrayList<>();
    private Boolean isall = false;
    private SharedPreferences pref;

    private Handler lHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    mProgressDialog.dismiss();
                    Collections.sort(mp3List, TitleComparator);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private Handler mHandler = new Handler() {

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
        setContentView(R.layout.activity_music);
        getMusics();
        init();
    }

    private void init() {
        app = (ApplicationUtil) MusicActivity.this.getApplication();
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        lvMusic = (ListView) findViewById(R.id.lv_music);
        adapter = new MusicAdapter(MusicActivity.this, mp3List, posList, mHandler);
        lvMusic.setAdapter(adapter);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
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
                portal.add(mp3List.get(Integer.parseInt(posList.get(i))).getPath());
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "copy");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(MusicActivity.this, DeviceActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnMove) {
            List<String> portal = new ArrayList<>();
            for (int i = 0; i < posList.size(); i++) {
                portal.add(mp3List.get(Integer.parseInt(posList.get(i))).getPath());
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "move");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(MusicActivity.this, DeviceActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnShare) {
            if (app.getmPrintWriterClient() != null) {
                pref = PreferenceManager.getDefaultSharedPreferences(this);
                String ip = pref.getString("ip", "");
                String filename = "";
                for (int i = 0; i < posList.size(); i++) {
                    filename = mp3List.get(Integer.parseInt(posList.get(i))).getPath();
                    sendMessage("file", filename);
                    new SendTask(ip, "59672", filename).execute();
                }
            } else {
                Toast.makeText(this, "没有连接!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Runnable deleteFile = new Runnable() {

        @Override
        public void run() {
            Collections.sort(posList, mComparator);
            for (int i = 0; i < posList.size(); i++) {
                String deletePath = mp3List.get(Integer.parseInt(posList.get(i))).getPath();
                File deleteFile = new File(deletePath);
                Log.e("IP & PORT", "正在删除:" + deletePath);
                if (deleteFile.exists() && deleteFile.isFile() && deleteFile.canWrite()) {
                    deleteFile.delete();
                    Log.e("IP & PORT", "删除成功!");
                } else {
                    Log.e("IP & PORT", "删除失败!");
                }
            }
            int count = 0;
            for (int i = 0; i < posList.size(); i++) {
                mp3List.remove(mp3List.get(Integer.parseInt(posList.get(i)) - count));
                count++;
            }
            posList.clear();
            lvMusic.requestLayout();
            adapter.notifyDataSetChanged();
        }
    };

    Runnable selectAllFile = new Runnable() {

        @Override
        public void run() {
            if (isall == false) {
                posList.clear();
                for (int i = 0; i < mp3List.size(); i++) {
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

    private void getMusics() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ShowToast("暂无外部存储");
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mMusicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = MusicActivity.this.getContentResolver();
                Cursor mCursor = mContentResolver.query(mMusicUri, null, null, null, null);
                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                    String album = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
                    String artist = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));

                    MusicBean item = new MusicBean(path, title, album, artist);
                    mp3List.add(item);
                }
                mCursor.close();
                lHandler.sendEmptyMessage(SCAN_OK);

            }
        }).start();

    }

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

    Comparator TitleComparator = new Comparator() {
        public int compare(Object obj1, Object obj2) {
            String str1 = ((MusicBean) obj1).getTitle();
            String str2 = ((MusicBean) obj2).getTitle();
            if (str1.compareTo(str2) < 0)
                return -1;
            else if (str1.compareTo(str2) == 0)
                return 0;
            else if (str1.compareTo(str2) > 0)
                return 1;
            return 0;
        }
    };
}
