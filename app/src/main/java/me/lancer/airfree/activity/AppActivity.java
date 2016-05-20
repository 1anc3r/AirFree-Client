package me.lancer.airfree.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import me.lancer.distance.R;
import me.lancer.airfree.adapter.AppAdapter;
import me.lancer.airfree.model.AppBean;

public class AppActivity extends BaseActivity implements View.OnClickListener {

    private Button btnBack;
    private List<AppBean> appList = new ArrayList<>();
    private List<String> posList = new ArrayList<>();
    private AppAdapter adapter;
    private ListView lvApp;

    private final static int SCAN_OK = 1;
    private ProgressDialog mProgressDialog;

    private Handler lHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    mProgressDialog.dismiss();
                    Collections.sort(appList, AppComparator);
                    adapter = new AppAdapter(AppActivity.this, appList, posList);
                    lvApp.setAdapter(adapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        getApp();
        init();
    }

    private void init() {
        lvApp = (ListView) findViewById(R.id.lv_app);
        lvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AppActivity.this);
                builder.setTitle("详细信息");
                builder.setMessage("应用名 : " + appList.get(position).getAppName()
                        + "\n\n" +
                        "包名 : " + appList.get(position).getPackageName());
                builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            setResult(RESULT_OK, null);
            finish();
        }
    }

    private void getApp() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ShowToast("暂无外部存储");
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        new Thread(new Runnable() {

            @Override
            public void run() {
                List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
                for (int i = 0; i < packages.size(); i++) {
                    PackageInfo packageInfo = packages.get(i);
                    AppBean appInfo = new AppBean();
                    appInfo.setVersionCode(packageInfo.versionCode);
                    appInfo.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                    appInfo.setPackageName(packageInfo.packageName);
                    appInfo.setVersionName(packageInfo.versionName);
                    appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
                    appList.add(appInfo);
                }
                lHandler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }

    Comparator AppComparator = new Comparator() {
        public int compare(Object obj1, Object obj2) {
            AppBean app1 = (AppBean) obj1;
            AppBean app2 = (AppBean) obj2;
            if (app1.getAppName().compareToIgnoreCase(app2.getAppName()) < 0)
                return -1;
            else if (app1.getAppName().compareToIgnoreCase(app2.getAppName()) == 0)
                return 0;
            else if (app1.getAppName().compareToIgnoreCase(app2.getAppName()) > 0)
                return 1;
            return 0;
        }
    };
}
