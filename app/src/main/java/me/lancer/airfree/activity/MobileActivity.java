package me.lancer.airfree.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

import me.lancer.distance.R;
import me.lancer.airfree.adapter.MobileAdapter;
import me.lancer.airfree.bean.MobileBean;
import me.lancer.airfree.util.ApplicationUtil;

public class MobileActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;
    private TextView tvPath, tvShow;
    private ListView lvFile;
    private EditText etSearch;
    private Button btnPaste, btnCancell;
    private LinearLayout llBack, llSearch, llBottom, btnDelete, btnCopy, btnMove, btnShare, btnAll;
    private TextView tvDelete, tvCopy, tvMove, tvShare, tvAll;

    private final static int SCAN_OK = 1;

    private MobileAdapter adapter;
    private List<MobileBean> fileList = new ArrayList<>();
    private List<MobileBean> refenList = new ArrayList<>();
    private List<String> posList = new ArrayList<>();
    private List<String> srcList = new ArrayList<>();
    private List<String> searchList = new ArrayList<>();
    private String searchStr = new String();
    private Handler handler = new Handler();
    private String method;
    private String parentpath;
    private File parentfile;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Boolean isAll = false;

    private SharedPreferences pref;
    private String language = "zn";
    private String strConnectionSucceeded = "";
    private String strNoConnection = "";
    private String strConnectionFailed = "";
    private String strDownload = "";
    private String strInternal = "";
    private String strExternal = "";
    private String strNoInternalExternalStorage = "";
    private String strLoading = "";
    private String strDelete = "";
    private String strCopy = "";
    private String strCut = "";
    private String strUpload = "";
    private String strAll = "";
    private String strPaste = "";
    private String strCancel = "";

    private Handler lHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    Collections.sort(fileList, NameComparator);
                    if (method.equals("in") || method.equals("out") || method.equals("download")) {
                        llBottom.setVisibility(View.GONE);
                    }
                    lvFile.requestLayout();
                    adapter.notifyDataSetChanged();
                    break;
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
            }
            if (posList.isEmpty()) {
                if (method.equals("in") || method.equals("out") || method.equals("download")) {
                    llBottom.setVisibility(View.GONE);
                }
            } else {
                llBottom.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
        init();
    }

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strDownload = getResources().getString(R.string.download_zn);
            strInternal = getResources().getString(R.string.internal_zn);
            strExternal = getResources().getString(R.string.external_zn);
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
            strDownload = getResources().getString(R.string.download_en);
            strInternal = getResources().getString(R.string.internal_en);
            strExternal = getResources().getString(R.string.external_en);
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
        iLanguage();
        app = (ApplicationUtil) MobileActivity.this.getApplication();
        tvShow = (TextView) findViewById(R.id.tv_show);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        method = b.getString("method");
        srcList = b.getStringArrayList("source");
        if (method.equals("download")) {
            parentpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            tvShow.setText(strDownload);
        } else if (method.equals("out")) {
            parentpath = "/mnt/ext_sdcard/";
            tvShow.setText(strExternal);
        } else {
            parentpath = Environment.getExternalStorageDirectory().getAbsolutePath();
            tvShow.setText(strInternal);
        }
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(this);
        llSearch = (LinearLayout) findViewById(R.id.ll_search);
        llSearch.setOnClickListener(this);
        tvPath = (TextView) findViewById(R.id.tv_path);
        lvFile = (ListView) findViewById(R.id.lv_file);
        adapter = new MobileAdapter(MobileActivity.this, fileList, posList, searchList, mHandler);
        lvFile.setAdapter(adapter);
        lvFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (new File(fileList.get(position).getPath()).isDirectory()) {
                    parentpath = fileList.get(position).getPath();
                    tvPath.setText(parentpath);
                    fileList.clear();
                    posList.clear();
                    lHandler.post(getFile);
                    lvFile.requestLayout();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        tvPath.setText(parentpath);
        lHandler.post(getFile);
        if (method.equals("in") || method.equals("out") || method.equals("download") || method.equals("download")) {
            llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        } else if (method.equals("copy") || method.equals("move")) {
            llBottom = (LinearLayout) findViewById(R.id.ll_paste);
            llBottom.setVisibility(View.VISIBLE);
        }
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
        btnPaste = (Button) findViewById(R.id.btn_paste);
        btnPaste.setText(strPaste);
        btnPaste.setOnClickListener(this);
        btnCancell = (Button) findViewById(R.id.btn_cancell);
        btnCancell.setText(strCancel);
        btnCancell.setOnClickListener(this);
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
            final Dialog dialog = new AlertDialog.Builder(MobileActivity.this).create();
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
                portal.add(fileList.get(Integer.parseInt(posList.get(i))).getPath());
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "copy");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(MobileActivity.this, MobileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnMove) {
            List<String> portal = new ArrayList<>();
            for (int i = 0; i < posList.size(); i++) {
                portal.add(fileList.get(Integer.parseInt(posList.get(i))).getPath());
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "move");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(MobileActivity.this, MobileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnPaste) {
            if (method.equals("copy")) {
                for (int i = 0; i < srcList.size(); i++) {
                    copyFile(srcList.get(i), new File(srcList.get(i)).getName(), parentpath);
                }
            } else if (method.equals("move")) {
                for (int i = 0; i < srcList.size(); i++) {
                    moveFile(srcList.get(i), new File(srcList.get(i)).getName(), parentpath);
                }
            }
            Collections.sort(fileList, NameComparator);
            posList.clear();
            lvFile.requestLayout();
            adapter.notifyDataSetChanged();
        } else if (v == btnCancell) {
            setResult(RESULT_OK, null);
            finish();
        } else if (v == btnShare) {
            if (app.getmPrintWriterClient() != null) {
                pref = PreferenceManager.getDefaultSharedPreferences(this);
                String ip = pref.getString("ip", "");
                String filename = "";
                for (int i = 0; i < posList.size(); i++) {
                    filename = fileList.get(Integer.parseInt(posList.get(i))).getPath();
                    sendMessage("file", filename);
                    new SendTask(ip, "59672", filename).execute();
                }
            } else {
                Toast.makeText(this, strNoConnection, Toast.LENGTH_SHORT).show();
            }
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

    private void copyFile(String inputPath, String inputFile, String outputPath) {
        Log.e("IP & PORT", inputPath + " to " + outputPath);
        InputStream in = null;
        OutputStream out = null;
        try {
            File input = new File(inputPath);
            if (input.isFile()) {
                fileList.add(new MobileBean(outputPath + "/" + inputFile,
                        inputFile, outputPath, new ArrayList<String>(),
                        format.format(new Date((new File(outputPath + "/" + inputFile)).lastModified()))));
                in = new FileInputStream(inputPath);
                out = new FileOutputStream(outputPath + "/" + inputFile);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
                Log.e("IP & PORT", "复制成功!");
            } else if (input.isDirectory()) {
                File output = new File(outputPath + "/" + input.getName());
                if (!output.exists()) {
                    output.mkdirs();
                }
                File[] list = input.listFiles();
                if (list.length != 0) {
                    for (File item : list) {
                        copyFile(item.getPath(), item.getName(), output.getPath());
                    }
                }
                Log.e("IP & PORT", "复制成功!");
            } else {
                Log.e("IP & PORT", "复制失败!");
            }
        } catch (FileNotFoundException fnfe1) {
            Log.e("IP & PORT", "复制失败:" + fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("IP & PORT", "复制失败:" + e.getMessage());
        }
    }

    private void moveFile(String inputPath, String inputFile, String outputPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File input = new File(inputPath);
            if (input.isFile()) {
                fileList.add(new MobileBean(outputPath + "/" + inputFile,
                        inputFile, outputPath, new ArrayList<String>(),
                        format.format(new Date((new File(outputPath + "/" + inputFile)).lastModified()))));
                File dir = new File(outputPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                in = new FileInputStream(inputPath);
                out = new FileOutputStream(outputPath + "/" + inputFile);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
                new File(inputPath).delete();
                Log.e("IP & PORT", "剪切成功!");
            } else if (input.isDirectory()) {
                File output = new File(outputPath + "/" + input.getName());
                if (!output.exists()) {
                    output.mkdirs();
                }
                File[] list = input.listFiles();
                if (list.length != 0) {
                    for (File item : list) {
                        moveFile(item.getPath(), item.getName(), output.getPath());
                    }
                }
                Log.e("IP & PORT", "剪切成功!");
            } else {
                Log.e("IP & PORT", "剪切失败!");
            }
        } catch (FileNotFoundException fnfe1) {
            Log.e("IP & PORT", "剪切失败:" + fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("IP & PORT", "剪切失败:" + e.getMessage());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] list = dir.list();
            for (int i=0; i<list.length; i++) {
                boolean success = deleteDir(new File(dir, list[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
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

    private void getContactSub(List<MobileBean> contactSub, String searchStr) {
        int length = refenList.size();
        for (int i = 0; i < length; ++i) {
            if (refenList.get(i).getFileName().contains(searchStr)) {
                contactSub.add(refenList.get(i));
            }
        }
    }

    Runnable selectAllFile = new Runnable() {

        @Override
        public void run() {
            if (isAll == false) {
                posList.clear();
                for (int i = 0; i < fileList.size(); i++) {
                    posList.add("" + i);
                }
                isAll = true;
                llBottom.setVisibility(View.VISIBLE);
            } else {
                posList.clear();
                isAll = false;
                if (method.equals("in") || method.equals("out") || method.equals("download")) {
                    llBottom.setVisibility(View.GONE);
                }
            }
            lvFile.requestLayout();
            adapter.notifyDataSetChanged();
        }
    };

    Runnable getFile = new Runnable() {

        @Override
        public void run() {
            Log.e("IP & PORT", "正在获取:" + parentpath);
            fileList.clear();
            File root = new File(parentpath);
            if (root.isDirectory()) {
                File fileis[] = root.listFiles();
                if (fileis != null) {
                    for (File filei : fileis) {
                        if (filei.isDirectory() && filei.exists() && filei.canRead() && filei.canWrite()) {
                            File fileiis[] = filei.listFiles();
                            List<String> childs = new ArrayList<>();
                            for (File fileii : fileiis) {
                                if (fileiis != null) {
                                    if (filei.exists() && filei.canRead() && filei.canWrite()) {
                                        childs.add(fileii.getAbsolutePath());
                                    }
                                }
                            }
                            fileList.add(new MobileBean(filei.getAbsolutePath(), filei.getName(), parentpath, childs,
                                    format.format(new Date((new File(filei.getAbsolutePath())).lastModified()))));
                            refenList.add(new MobileBean(filei.getAbsolutePath(), filei.getName(), parentpath, childs,
                                    format.format(new Date((new File(filei.getAbsolutePath())).lastModified()))));
                        } else if (filei.exists() && filei.isFile() && filei.canRead() && filei.canWrite()) {
                            List<String> childs = new ArrayList<>();
                            fileList.add(new MobileBean(filei.getAbsolutePath(), filei.getName(), parentpath, childs,
                                    format.format(new Date((new File(filei.getAbsolutePath())).lastModified()))));
                            refenList.add(new MobileBean(filei.getAbsolutePath(), filei.getName(), parentpath, childs,
                                    format.format(new Date((new File(filei.getAbsolutePath())).lastModified()))));
                        }
                    }
                }
            }
            lHandler.sendEmptyMessage(SCAN_OK);
        }
    };

    Runnable back2parent = new Runnable() {

        @Override
        public void run() {
            if (parentpath.equals(Environment.getExternalStorageDirectory().getAbsolutePath()) || parentpath.equals("/mnt/ext_sdcard") || parentpath.equals("/mnt/ext_sdcard/")) {
                setResult(RESULT_OK, null);
                finish();
            } else {
                parentfile = new File(parentpath);
                parentpath = parentfile.getParent();
                tvPath.setText(parentpath);
                fileList.clear();
                posList.clear();
                lHandler.post(getFile);
            }
        }
    };

    Runnable deleteFile = new Runnable() {

        @Override
        public void run() {
            Collections.sort(posList, PosComparator);
            for (int i = 0; i < posList.size(); i++) {
                String deletePath = fileList.get(Integer.parseInt(posList.get(i))).getPath();
                File deleteFile = new File(deletePath);
                if (deleteFile.isFile()) {
                    Log.e("IP & PORT", "正在删除:" + deletePath);
                    if (deleteFile.exists() && deleteFile.isFile() && deleteFile.canWrite()) {
                        deleteFile.delete();
                        Log.e("IP & PORT", "删除成功!");
                    } else {
                        Log.e("IP & PORT", "删除失败!");
                    }
                } else if (deleteFile.isDirectory()){
                    deleteDir(deleteFile);
                }
            }
            int count = 0;
            for (int i = 0; i < posList.size(); i++) {
                fileList.remove(fileList.get(Integer.parseInt(posList.get(i)) - count));
                count++;
            }
            posList.clear();
            lvFile.requestLayout();
            adapter.notifyDataSetChanged();
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
            MobileBean file1 = (MobileBean) obj1;
            MobileBean file2 = (MobileBean) obj2;
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
