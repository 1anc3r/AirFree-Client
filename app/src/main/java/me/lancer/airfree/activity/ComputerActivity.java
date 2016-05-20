package me.lancer.airfree.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.lancer.airfree.adapter.LetterAdapter;
import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;
import me.lancer.airfree.model.LetterBean;

public class ComputerActivity extends BaseActivity implements View.OnClickListener {

    private Button btnBack;
    private TextView tvPath;
    private List<LetterBean> fileList = new ArrayList<>();
    private List<String> posList = new ArrayList<>();
    private LetterAdapter adapter;
    private ListView lvFile;
    private LinearLayout llBottom;
    private LinearLayout btnShare, btnOpen;

    ApplicationUtil app;
    private int allpos;
    private LetterBean parentfile = new LetterBean("My Computer", "My Computer");
    private String parentpath = "My Computer";
    private String temppath = "";
    private String recvMessageClient = "";
    private SharedPreferences pref;

    private Handler lHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String str = (String) msg.obj;
                if (!str.equals("[]")) {
                    fileList.clear();
                    posList.clear();
                    str = str.substring(1, str.length() - 1);
                    String[] arrstr = str.split(", ");
                    for (String item : arrstr) {
                        String[] temp = item.split("\\\\");
                        String name = temp[temp.length - 1];
                        fileList.add(new LetterBean(item, name, parentpath, parentfile));
                    }
                } else {
                    if (posList.contains("" + allpos)) {
                        posList.remove("" + allpos);
                    } else {
                        posList.add("" + allpos);
                    }
                }
                lvFile.requestLayout();
                adapter.notifyDataSetChanged();
            }
        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (posList.contains(msg.obj)) {
                posList.remove(msg.obj);
            } else {
                posList.add((String) msg.obj);
                Collections.sort(posList, PosComparator);
                llBottom.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_computer);
        init();
    }

    private void init() {
        app = (ApplicationUtil) ComputerActivity.this.getApplication();
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        tvPath = (TextView) findViewById(R.id.tv_path);
        lvFile = (ListView) findViewById(R.id.lv_file);
        adapter = new LetterAdapter(ComputerActivity.this, fileList, posList, mHandler);
        lvFile.setAdapter(adapter);
        lvFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                temppath = fileList.get(position).getFilePath();
                String tempname = fileList.get(position).getFileName();
                if (tempname.endsWith(".png") || tempname.endsWith(".jpg") || tempname.endsWith(".psd")
                        || tempname.endsWith(".bmp") || tempname.endsWith(".gif") || tempname.endsWith(".jpeg")
                        || tempname.endsWith(".ico") || tempname.endsWith(".tif") || tempname.endsWith(".wav")
                        || tempname.endsWith(".mp3") || tempname.endsWith(".m4a") || tempname.endsWith(".mid")
                        || tempname.endsWith(".wma") || tempname.endsWith(".ogg") || tempname.endsWith(".txt")
                        || tempname.endsWith(".pdf") || tempname.endsWith(".docx") || tempname.endsWith(".doc")
                        || tempname.endsWith(".pptx") || tempname.endsWith(".xlsx") || tempname.endsWith(".ppt")
                        || tempname.endsWith(".mp4") || tempname.endsWith(".avi") || tempname.endsWith(".wmv")
                        || tempname.endsWith(".zip") || tempname.endsWith(".7z") || tempname.endsWith(".cab")
                        || tempname.endsWith(".rar") || tempname.endsWith(".rar") || tempname.endsWith(".cab")
                        || tempname.endsWith(".exe") || tempname.endsWith(".jar") || tempname.endsWith(".dll")
                        || tempname.endsWith(".bat") || tempname.endsWith(".vbs") || tempname.endsWith(".xml")
                        || tempname.endsWith(".config")) {
                    if (posList.contains("" + position)) {
                        posList.remove("" + position);
                    } else {
                        posList.add("" + position);
                        Collections.sort(posList, PosComparator);
                        llBottom.setVisibility(View.VISIBLE);
                    }
                    if (posList.isEmpty()) {
                        llBottom.setVisibility(View.GONE);
                    } else {
                        llBottom.setVisibility(View.VISIBLE);
                    }
                    lvFile.requestLayout();
                    adapter.notifyDataSetChanged();
                } else {
                    parentfile = fileList.get(position);
                    parentpath = temppath;
                    sendMessage("computer", temppath);
                    lHandler.post(mRunnable);
                }
                tvPath.setText(parentpath);
                allpos = position;
            }
        });
        tvPath.setText(parentpath);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        btnShare = (LinearLayout) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        btnOpen = (LinearLayout) findViewById(R.id.btn_open);
        btnOpen.setOnClickListener(this);
        sendMessage("computer", parentpath);
        lHandler.post(mRunnable);
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            setResult(RESULT_OK, null);
            finish();
        } else if (v == btnShare) {
            if (app.getmPrintWriterClient() != null) {
                pref = PreferenceManager.getDefaultSharedPreferences(this);
                String ip = pref.getString("ip", "");
                String filename = "";
                for (int i = 0; i < posList.size(); i++) {
                    filename = fileList.get(Integer.parseInt(posList.get(i))).getFilePath();
                    sendMessage("computer", "iwanna:" + filename);
                    filename = fileList.get(Integer.parseInt(posList.get(i))).getFileName();
                    filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + filename;
                    new ReadTask(ip, "59672", filename).execute();
                }
            } else {
                Toast.makeText(this, "没有连接!", Toast.LENGTH_SHORT).show();
            }
        } else if (v == btnOpen) {
            sendMessage("remote", fileList.get(Integer.parseInt(posList.get(0))).getFilePath());
        }
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            char[] buffer = new char[1024];
            int count = 0;
            if (app.getmBufferedReaderClient() != null) {
                try {
                    if ((count = app.getmBufferedReaderClient().read(buffer)) > 0) {
                        recvMessageClient = getInfoBuff(buffer, count);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = recvMessageClient;
                        lHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Log.e("IP & PORT", "接收异常:" + e.getMessage());
                    recvMessageClient = "接收异常:" + e.getMessage();
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = "";
                    lHandler.sendMessage(msg);
                }
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Handler bHandler = new Handler();
            bHandler.post(back2parent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Runnable back2parent = new Runnable() {

        @Override
        public void run() {
            if (parentpath.equals("My Computer")) {
                setResult(RESULT_OK, null);
                finish();
            } else {
                posList.clear();
                parentpath = parentfile.getFileParentPath();
                parentfile = parentfile.getFileParent();
                tvPath.setText(parentpath);
                sendMessage("computer", parentpath);
                lHandler.post(mRunnable);
            }
        }
    };

    Comparator PosComparator = new Comparator() {
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
