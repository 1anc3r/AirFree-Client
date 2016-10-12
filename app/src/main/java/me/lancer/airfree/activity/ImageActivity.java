package me.lancer.airfree.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import me.lancer.airfree.bean.ImageBean;
import me.lancer.distance.R;
import me.lancer.airfree.adapter.PictureGroupAdapter;

public class ImageActivity extends BaseActivity implements View.OnClickListener {

    private Button btnBack;
    private TextView tvShow;
    private GridView mGroupGridView;
    private ProgressDialog mProgressDialog;

    private final static int SCAN_OK = 1;

    private PictureGroupAdapter adapter;
    private HashMap<String, List<String>> mGruopMap = new HashMap<>();
    private List<ImageBean> mImageList = new ArrayList<>();

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strShow = "";
    private String strNoInternalExternalStorage = "";
    private String strLoading = "";

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    mProgressDialog.dismiss();
                    adapter = new PictureGroupAdapter(ImageActivity.this, mImageList = subGroupOfImage(mGruopMap), mGroupGridView);
                    mGroupGridView.setAdapter(adapter);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        iLanguage();
        getImages();
        init();
    }

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strShow = getResources().getString(R.string.image_zn);
            strNoInternalExternalStorage = getResources().getString(R.string.no_internal_external_storage_zn);
            strLoading = getResources().getString(R.string.loading_zn);
        } else if (language.equals("en")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_en);
            strNoConnection = getResources().getString(R.string.no_connection_en);
            strConnectionFailed = getResources().getString(R.string.connection_failed_en);
            strShow = getResources().getString(R.string.image_en);
            strNoInternalExternalStorage = getResources().getString(R.string.no_internal_external_storage_en);
            strLoading = getResources().getString(R.string.loading_en);
        }
    }

    private void init() {
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvShow.setText(strShow);
        mGroupGridView = (GridView) findViewById(R.id.gv_all);
        mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String> childList = mGruopMap.get(mImageList.get(position).getFolderName());
                Intent mIntent = new Intent(ImageActivity.this, ImageIActivity.class);
                mIntent.putStringArrayListExtra("data", (ArrayList<String>) childList);
                mIntent.putExtra("title", mImageList.get(position).getFolderName());
                startActivity(mIntent);
            }
        });
    }

    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, strNoInternalExternalStorage, Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, strLoading);
        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ImageActivity.this.getContentResolver();
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    File file = new File(path);
                    String parentName = new File(path).getParentFile().getName();
                    if (file.exists() && file.isFile() && file.canWrite()) {
                        if (!mGruopMap.containsKey(parentName)) {
                            List<String> chileList = new ArrayList<>();
                            chileList.add(path);
                            mGruopMap.put(parentName, chileList);
                        } else {
                            mGruopMap.get(parentName).add(path);
                        }
                    }
                }
                mCursor.close();
                mHandler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }

    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
        if (mGruopMap.size() == 0) {
            return null;
        }
        List<ImageBean> list = new ArrayList<>();
        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            ImageBean mmImage = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            mmImage.setFolderName(key);
            mmImage.setImageCounts(value.size());
            mmImage.setTopImagePath(value.get(0));
            list.add(mmImage);
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            setResult(RESULT_OK, null);
            finish();
        }
    }
}
