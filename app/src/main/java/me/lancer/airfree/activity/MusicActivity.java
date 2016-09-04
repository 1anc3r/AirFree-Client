package me.lancer.airfree.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import me.lancer.airfree.adapter.MusicAdapter;
import me.lancer.airfree.bean.MusicBean;
import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class MusicActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;
    private ListView lvMusic;
    private EditText etSearch;
    private ProgressDialog mProgressDialog;
    private LinearLayout llBack, llSearch, llBottom, btnDelete, btnCopy, btnMove, btnShare, btnAll;

    private final static int SCAN_OK = 1;

    private MusicAdapter adapter;
    private List<MusicBean> musicList = new ArrayList<>();
    private List<MusicBean> refenList = new ArrayList<>();
    private List<String> posList = new ArrayList<>();
    private List<String> searchList = new ArrayList<>();
    private String searchStr = new String();
    private Handler handler = new Handler();
    private SharedPreferences pref;
    private Boolean isAll = false;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    mProgressDialog.dismiss();
                    Collections.sort(musicList, TitleComparator);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private Handler posHandler = new Handler() {

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
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(this);
        llSearch = (LinearLayout) findViewById(R.id.ll_search);
        llSearch.setOnClickListener(this);
        lvMusic = (ListView) findViewById(R.id.lv_music);
        adapter = new MusicAdapter(MusicActivity.this, musicList, posList, searchList, posHandler);
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
        if (v == llBack) {
            setResult(RESULT_OK, null);
            finish();
        } else if (v == llSearch) {
            InputMethodManager inputManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.searchbar_dialog_view, null);
            final Dialog dialog = new AlertDialog.Builder(MusicActivity.this).create();
            etSearch = (EditText) layout.findViewById(R.id.et_search);
            setSearchTextChanged();
            etSearch.setText(searchStr);
            etSearch.setFocusableInTouchMode(true);
            etSearch.setFocusable(true);
            etSearch.requestFocus();
            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT
                            || actionId == EditorInfo.IME_ACTION_NONE || actionId == EditorInfo.IME_ACTION_PREVIOUS
                            || actionId == EditorInfo.IME_ACTION_SEND || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            dialog.show();
            Window window = dialog.getWindow();
            window.setContentView(layout);
            WindowManager.LayoutParams lp = window.getAttributes();
            window.setGravity(Gravity.CENTER | Gravity.BOTTOM);
            window.setAttributes(lp);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else if (v == btnDelete) {
            Handler dHandler = new Handler();
            dHandler.post(deleteFile);
        } else if (v == btnAll) {
            Handler aHandler = new Handler();
            aHandler.post(selectAllFile);
        } else if (v == btnCopy) {
            List<String> portal = new ArrayList<>();
            for (int i = 0; i < posList.size(); i++) {
                portal.add(musicList.get(Integer.parseInt(posList.get(i))).getPath());
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "copy");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(MusicActivity.this, MobileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnMove) {
            List<String> portal = new ArrayList<>();
            for (int i = 0; i < posList.size(); i++) {
                portal.add(musicList.get(Integer.parseInt(posList.get(i))).getPath());
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "move");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(MusicActivity.this, MobileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnShare) {
            if (app.getmPrintWriterClient() != null) {
                pref = PreferenceManager.getDefaultSharedPreferences(this);
                String ip = pref.getString("ip", "");
                String filename = "";
                for (int i = 0; i < posList.size(); i++) {
                    filename = musicList.get(Integer.parseInt(posList.get(i))).getPath();
                    sendMessage("file", filename);
                    new SendTask(ip, "59672", filename).execute();
                }
            } else {
                Toast.makeText(this, "没有连接!", Toast.LENGTH_SHORT).show();
            }
        }
    }

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
                    long id = mCursor.getLong(mCursor
                            .getColumnIndex(MediaStore.Audio.Media._ID));
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                    long albumId = mCursor.getInt(mCursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    String album = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
                    String artist = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));

                    MusicBean item = new MusicBean(id, path, title, albumId, album, artist);
                    refenList.add(item);
                    musicList.add(item);
                }
                mCursor.close();
                mHandler.sendEmptyMessage(SCAN_OK);

            }
        }).start();
    }

    private void setSearchTextChanged() {

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                handler.post(changed);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
//                handler.post(changed);
            }
        });
    }

    private void getContactSub(List<MusicBean> contactSub, String searchStr) {
        int length = refenList.size();
        for (int i = 0; i < length; ++i) {
            if (refenList.get(i).getTitle().contains(searchStr)
                    || refenList.get(i).getArtist().contains(searchStr)) {
                contactSub.add(refenList.get(i));
            }
        }
    }

    Runnable deleteFile = new Runnable() {

        @Override
        public void run() {
            Collections.sort(posList, mComparator);
            for (int i = 0; i < posList.size(); i++) {
                String deletePath = musicList.get(Integer.parseInt(posList.get(i))).getPath();
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
                musicList.remove(musicList.get(Integer.parseInt(posList.get(i)) - count));
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
            if (isAll == false) {
                posList.clear();
                for (int i = 0; i < musicList.size(); i++) {
                    posList.add("" + i);
                }
                isAll = true;
                llBottom.setVisibility(View.VISIBLE);
            } else {
                posList.clear();
                isAll = false;
                llBottom.setVisibility(View.GONE);
            }
            Log.e("IP & PORT", "" + posList);
            adapter.notifyDataSetChanged();
        }
    };

    Runnable changed = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            searchStr = etSearch.getText().toString();
            searchList.clear();
            searchList.add(searchStr);
            musicList.clear();
            getContactSub(musicList, searchStr);
            Collections.sort(musicList, TitleComparator);
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
