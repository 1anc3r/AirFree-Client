package me.lancer.airfree.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import me.lancer.airfree.adapter.DocumentAdapter;
import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;
import me.lancer.airfree.bean.MobileBean;

public class DocumentActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;

    private TextView tvShow;
    private ListView lvDoc;
    private EditText etSearch;
    private ProgressDialog mProgressDialog;
    private LinearLayout llBack, llSearch, llBottom, btnDelete, btnCopy, btnMove, btnShare, btnAll;
    private TextView tvDelete, tvCopy, tvMove, tvShare, tvAll;

    private final static int SCAN_OK = 1;

    private DocumentAdapter adapter;
    private List<MobileBean> docList = new ArrayList<>();
    private List<MobileBean> refenList = new ArrayList<>();
    private List<String> posList = new ArrayList<>();
    private List<String> searchList = new ArrayList<>();
    private String searchStr = new String();
    private Handler handler = new Handler();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Boolean isAll = false;

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

    private Handler lHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    mProgressDialog.dismiss();
                    Collections.sort(docList, NameComparator);
                    llBottom.setVisibility(View.GONE);
                    adapter = new DocumentAdapter(DocumentActivity.this, docList, posList, searchList, mHandler);
                    lvDoc.setAdapter(adapter);
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
                Collections.sort(posList, PosComparator);
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
        setContentView(R.layout.activity_document);
        iLanguage();
        getAllDocument();
        init();
    }

    public void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            strConnectionSucceeded = getResources().getString(R.string.connection_succeeded_zn);
            strNoConnection = getResources().getString(R.string.no_connection_zn);
            strConnectionFailed = getResources().getString(R.string.connection_failed_zn);
            strShow = getResources().getString(R.string.document_zn);
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
            strShow = getResources().getString(R.string.document_en);
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
        app = (ApplicationUtil) DocumentActivity.this.getApplication();
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvShow.setText(strShow);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(this);
        llSearch = (LinearLayout) findViewById(R.id.ll_search);
        llSearch.setOnClickListener(this);
        lvDoc = (ListView) findViewById(R.id.lv_doc);
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
            final Dialog dialog = new AlertDialog.Builder(DocumentActivity.this).create();
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
                portal.add(docList.get(Integer.parseInt(posList.get(i))).getPath());
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "copy");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(DocumentActivity.this, MobileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnMove) {
            List<String> portal = new ArrayList<>();
            for (int i = 0; i < posList.size(); i++) {
                portal.add(docList.get(Integer.parseInt(posList.get(i))).getPath());
            }
            Bundle bundle = new Bundle();
            bundle.putString("method", "move");
            bundle.putStringArrayList("source", (ArrayList<String>) portal);
            Intent intent = new Intent();
            intent.setClass(DocumentActivity.this, MobileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == btnShare) {
            if (app.getmPrintWriterClient() != null) {
                pref = PreferenceManager.getDefaultSharedPreferences(this);
                String ip = pref.getString("ip", "");
                String filename = "";
                for (int i = 0; i < posList.size(); i++) {
                    filename = docList.get(Integer.parseInt(posList.get(i))).getPath();
                    sendMessage("file", filename);
                    new SendTask(ip, "59672", filename).execute();
                }
            } else {
                Toast.makeText(this, strNoConnection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getAllDocument() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ShowToast(strNoInternalExternalStorage);
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, strLoading);
        new Thread(new Runnable() {

            @Override
            public void run() {
                getDocument(Environment.getExternalStorageDirectory());
                lHandler.sendEmptyMessage(SCAN_OK);

            }
        }).start();
    }

    private void getDocument(File root) {
        File fileis[] = root.listFiles();
        if (fileis != null) {
            for (File filei : fileis) {
                if (filei.isDirectory()) {
                    getDocument(filei);
                } else {
                    String fileName = filei.getName();
                    if (fileName.endsWith(".txt")
                            || fileName.endsWith(".doc") || fileName.endsWith(".pdf")
                            || fileName.endsWith(".xlsx") || fileName.endsWith(".ppt")) {
                        List<String> childs = new ArrayList<>();
                        docList.add(new MobileBean(filei.getAbsolutePath(), filei.getName(), root.getAbsolutePath(), childs,
                                format.format(new Date((new File(filei.getAbsolutePath())).lastModified()))));
                        refenList.add(new MobileBean(filei.getAbsolutePath(), filei.getName(), root.getAbsolutePath(), childs,
                                format.format(new Date((new File(filei.getAbsolutePath())).lastModified()))));
                    }
                }
            }
        }
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

    Runnable changed = new Runnable() {

        @Override
        public void run() {
            searchStr = etSearch.getText().toString();
            searchList.clear();
            searchList.add(searchStr);
            docList.clear();
            getContactSub(docList, searchStr);
            Collections.sort(docList, NameComparator);
            adapter.notifyDataSetChanged();
        }
    };

    Runnable deleteFile = new Runnable() {

        @Override
        public void run() {
            Collections.sort(posList, PosComparator);
            for (int i = 0; i < posList.size(); i++) {
                String deletePath = docList.get(Integer.parseInt(posList.get(i))).getPath();
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
                docList.remove(docList.get(Integer.parseInt(posList.get(i)) - count));
                count++;
            }
            posList.clear();
            lvDoc.requestLayout();
            adapter.notifyDataSetChanged();
        }
    };

    Runnable selectAllFile = new Runnable() {

        @Override
        public void run() {
            if (isAll == false) {
                posList.clear();
                for (int i = 0; i < docList.size(); i++) {
                    posList.add("" + i);
                }
                isAll = true;
                llBottom.setVisibility(View.VISIBLE);
            } else {
                posList.clear();
                isAll = false;
                llBottom.setVisibility(View.GONE);
            }
            lvDoc.requestLayout();
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
