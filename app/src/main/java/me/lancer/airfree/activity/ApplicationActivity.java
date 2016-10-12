package me.lancer.airfree.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import me.lancer.distance.R;
import me.lancer.airfree.adapter.ApplicationAdapter;
import me.lancer.airfree.bean.ApplicationBean;

public class ApplicationActivity extends BaseActivity implements View.OnClickListener {

    AsyncTask asyncTask;
    private Button btnBack;
    private TextView tvShow;
    private ListView lvApp;
    private ProgressDialog mProgressDialog;

    private final static int SCAN_OK = 1;

    private ApplicationAdapter adapter;
    private List<ApplicationBean> appList = new ArrayList<>();
    private List<String> posList = new ArrayList<>();

    private SharedPreferences pref;
    private String language = "zn";
    private String show = "";
    private String applicationInfo = "";
    private String applicationName = "";
    private String applicationPackage = "";
    private String uninstall = "";
    private String ok = "";
    private String noInternalExternalStorage = "";
    private String loading = "";

    private Handler lHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    mProgressDialog.dismiss();
                    Collections.sort(appList, AppComparator);
                    adapter = new ApplicationAdapter(ApplicationActivity.this, appList, posList);
                    lvApp.setAdapter(adapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        iLanguage();
        getApp();
        init();
    }

    private void iLanguage(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            show = getResources().getString(R.string.application_zn);
            applicationInfo = getResources().getString(R.string.application_info_zn);
            applicationName = getResources().getString(R.string.application_name_zn);
            applicationPackage = getResources().getString(R.string.application_package_zn);
            uninstall = getResources().getString(R.string.uninstall_zn);
            ok = getResources().getString(R.string.ok_zn);
            noInternalExternalStorage = getResources().getString(R.string.no_internal_external_storage_zn);
            loading = getResources().getString(R.string.loading_zn);
        } else if (language.equals("en")) {
            show = getResources().getString(R.string.application_en);
            applicationInfo = getResources().getString(R.string.application_info_en);
            applicationName = getResources().getString(R.string.application_name_en);
            applicationPackage = getResources().getString(R.string.application_package_en);
            uninstall = getResources().getString(R.string.uninstall_en);
            ok = getResources().getString(R.string.ok_en);
            noInternalExternalStorage = getResources().getString(R.string.no_internal_external_storage_en);
            loading = getResources().getString(R.string.loading_en);
        }
    }

    private void init() {
        lvApp = (ListView) findViewById(R.id.lv_app);
        lvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationActivity.this);
                builder.setTitle(applicationInfo);
                builder.setMessage(applicationName + appList.get(position).getAppName()
                        + "\n\n" + applicationPackage + appList.get(position).getPackageName());
                builder.setPositiveButton(uninstall, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse(appList.get(position).getPackageName()));
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(ok, new DialogInterface.OnClickListener() {

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
        tvShow = (TextView) findViewById(R.id.tv_show);
        tvShow.setText(show);
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
            ShowToast(noInternalExternalStorage);
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, loading);
        new Thread(new Runnable() {

            @Override
            public void run() {
                List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
                for (int i = 0; i < packages.size(); i++) {
                    PackageInfo packageInfo = packages.get(i);
                    ApplicationBean appInfo = new ApplicationBean();
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
            ApplicationBean app1 = (ApplicationBean) obj1;
            ApplicationBean app2 = (ApplicationBean) obj2;
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
