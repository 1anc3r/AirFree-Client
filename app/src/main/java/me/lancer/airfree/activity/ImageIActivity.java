package me.lancer.airfree.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.lancer.airfree.adapter.PictureChildAdapter;
import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class ImageIActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;
    private Button btnBack;
    private TextView tvShow;
    private GridView mGridView;
    private LinearLayout llBottom, btnDelete, btnCopy, btnMove, btnShare, btnAll;

    private PictureChildAdapter adapter;
    private List<String> posList = new ArrayList<>();
    private List<String> picList = new ArrayList<>();
    private Boolean isall = false;
    private SharedPreferences pref;

    public Handler mHandler = new Handler() {

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
        setContentView(R.layout.activity_image_i);
        init();
    }

    private void init() {
        app = (ApplicationUtil) ImageIActivity.this.getApplication();
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        Intent i = getIntent();
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvShow.setText("" + i.getExtras().get("title"));
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        mGridView = (GridView) findViewById(R.id.gv_child);
        picList = getIntent().getStringArrayListExtra("data");
        Collections.reverse(picList);
        adapter = new PictureChildAdapter(this, picList, posList, mGridView, mHandler);
        mGridView.setAdapter(adapter);
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
                portal.add(picList.get(Integer.parseInt(posList.get(i))));
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "copy");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(ImageIActivity.this, DeviceActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnMove) {
            List<String> portal = new ArrayList<>();
            for (int i = 0; i < posList.size(); i++) {
                portal.add(picList.get(Integer.parseInt(posList.get(i))));
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "move");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(ImageIActivity.this, DeviceActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnShare) {
            if (app.getmPrintWriterClient() != null) {
                pref = PreferenceManager.getDefaultSharedPreferences(this);
                String ip = pref.getString("ip", "");
                String port = pref.getString("port", "");
                String filename = "";
                for (int i = 0; i < posList.size(); i++) {
                    filename = picList.get(Integer.parseInt(posList.get(i)));
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
            int count = 0;
            Collections.sort(posList, mComparator);
            while (!posList.isEmpty()) {
                String deletePath = picList.get(Integer.parseInt(posList.get(0)) - count);
                File deleteFile = new File(deletePath);
                Log.e("IP & PORT", "正在删除:" + deletePath);
                if (deleteFile.exists() && deleteFile.isFile() && deleteFile.canWrite()) {
                    deleteFile.delete();
                    picList.remove(deletePath);
                    posList.remove(posList.get(0));
                    Log.e("IP & PORT", "删除成功!");
                } else {
                    Log.e("IP & PORT", "删除失败!");
                }
                count++;
            }
            adapter.notifyDataSetChanged();
        }
    };

    Runnable selectAllFile = new Runnable() {

        @Override
        public void run() {
            if (!isall) {
                posList.clear();
                for (int i = 0; i < picList.size(); i++) {
                    posList.add("" + i);
                }
                isall = true;
                llBottom.setVisibility(View.VISIBLE);
            } else {
                posList.clear();
                isall = false;
                llBottom.setVisibility(View.GONE);
            }
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
