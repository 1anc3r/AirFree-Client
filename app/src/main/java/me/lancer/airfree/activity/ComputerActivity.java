package me.lancer.airfree.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.lancer.airfree.adapter.ComputerAdapter;
import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;
import me.lancer.airfree.model.ComputerBean;

public class ComputerActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;

    private TextView tvPath;
    private ListView lvFile;
    private EditText etSearch;
    private LinearLayout llBack, llSearch, llBottom, btnShare, btnOpen, btnControl;

    private ComputerAdapter adapter;
    private List<ComputerBean> fileList = new ArrayList<>();
    private List<ComputerBean> refenList = new ArrayList<>();
    private List<String> posList = new ArrayList<>();
    private List<String> searchList = new ArrayList<>();
    private String searchStr = new String();
    private Handler handler = new Handler();
    private ComputerBean parentfile = new ComputerBean("this PC", "this PC");
    private String parentpath = "this PC";
    private String temppath = "";
    private Thread mThreadClient = null;
    private String recvMessageClient = "";
    private SharedPreferences pref;
    private boolean iStop = false;
    private int allPos;

    private Handler cHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("IP & PORT", "what:" + msg.what + " obj:" + msg.obj.toString());
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
                        fileList.add(new ComputerBean(item, name, parentpath, parentfile));
                        refenList.add(new ComputerBean(item, name, parentpath, parentfile));
                    }
                } else {
                    if (posList.contains("" + allPos)) {
                        posList.remove("" + allPos);
                    } else {
                        posList.add("" + allPos);
                    }
                }
                lvFile.requestLayout();
                adapter.notifyDataSetChanged();
            }
        }
    };

    private Handler posHandler = new Handler() {

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

    @Override
    protected void onDestroy() {
        if (iStop == false) {
            iStop = true;
//        cHandler.removeCallbacks(cRunnable);
            mThreadClient.interrupt();
        }
        super.onDestroy();
    }

    private void init() {
        app = (ApplicationUtil) ComputerActivity.this.getApplication();
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(this);
        llSearch = (LinearLayout) findViewById(R.id.ll_search);
        llSearch.setOnClickListener(this);
        tvPath = (TextView) findViewById(R.id.tv_path);
        lvFile = (ListView) findViewById(R.id.lv_file);
        adapter = new ComputerAdapter(ComputerActivity.this, fileList, posList, searchList, posHandler);
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
                    cHandler.post(cRunnable);
                }
                tvPath.setText(parentpath);
                allPos = position;
            }
        });
        tvPath.setText(parentpath);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        btnShare = (LinearLayout) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        btnOpen = (LinearLayout) findViewById(R.id.btn_open);
        btnOpen.setOnClickListener(this);
        sendMessage("computer", parentpath);
//        cHandler.post(cRunnable);
        mThreadClient = new Thread(cRunnable);
        mThreadClient.start();
    }

    @Override
    public void onClick(View v) {
        if (v == llBack) {
            if (iStop == false) {
                iStop = true;
//        cHandler.removeCallbacks(cRunnable);
                mThreadClient.interrupt();
            }
            setResult(RESULT_OK, null);
            finish();
        } else if (v == llSearch) {
            InputMethodManager inputManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.searchbar_dialog_view, null);
            final Dialog dialog = new AlertDialog.Builder(ComputerActivity.this).create();
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Handler bHandler = new Handler();
            bHandler.post(back2parent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    private void getContactSub(List<ComputerBean> contactSub, String searchStr) {
        int length = refenList.size();
        for (int i = 0; i < length; ++i) {
            if (refenList.get(i).getFileName().contains(searchStr)) {
                contactSub.add(refenList.get(i));
            }
        }
    }

    private Runnable cRunnable = new Runnable() {

        @Override
        public void run() {
            if (!iStop) {
                char[] buffer = new char[2048];
                int count = 0;
                if (app.getmBufferedReaderClient() != null) {
                    try {
//                        if ((count = app.getmBufferedReaderClient().read(buffer)) > 0) {
//                            recvMessageClient = getInfoBuff(buffer, count);
                        recvMessageClient = app.getmBufferedReaderClient().readLine();
                        Log.e("IP & PORT", "接收成功(C):" + recvMessageClient);
                        JSONTokener jt = new JSONTokener(recvMessageClient);
                        JSONObject jb = (JSONObject) jt.nextValue();
                        String command = jb.getString("command");
                        String paramet = jb.getString("parameter");
                        if (command.contains("computer")) {
                            Message msg = cHandler.obtainMessage();
                            msg.what = 1;
                            msg.obj = paramet;
                            cHandler.sendMessage(msg);
                        }
//                        }
                    } catch (Exception e) {
                        Log.e("IP & PORT", "接收异常:" + e.getMessage());
                        recvMessageClient = "接收异常:" + e.getMessage();
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = "";
                        cHandler.sendMessage(msg);
                    }
                }
            }
        }
    };

    Runnable back2parent = new Runnable() {

        @Override
        public void run() {
            if (parentpath.equals("this PC")) {
                if (iStop == false) {
                    iStop = true;
//        cHandler.removeCallbacks(cRunnable);
                    mThreadClient.interrupt();
                }
                setResult(RESULT_OK, null);
                finish();
            } else {
                posList.clear();
                parentpath = parentfile.getFileParentPath();
                parentfile = parentfile.getFileParent();
                tvPath.setText(parentpath);
                sendMessage("computer", parentpath);
                cHandler.post(cRunnable);
            }
        }
    };

    Runnable changed = new Runnable() {

        @Override
        public void run() {
            searchStr = etSearch.getText().toString();
            searchList.clear();
            searchList.add(searchStr);
            fileList.clear();
            getContactSub(fileList, searchStr);
            Collections.sort(fileList, NameComparator);
            adapter.notifyDataSetChanged();
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

    Comparator NameComparator = new Comparator() {
        public int compare(Object obj1, Object obj2) {
            ComputerBean file1 = (ComputerBean) obj1;
            ComputerBean file2 = (ComputerBean) obj2;
            if (file1.getFileName().compareToIgnoreCase(file2.getFileName()) < 0)
                return -1;
            else if (file1.getFileName().compareToIgnoreCase(file2.getFileName()) == 0)
                return 0;
            else if (file1.getFileName().compareToIgnoreCase(file2.getFileName()) > 0)
                return 1;
            return 0;
        }
    };
}
