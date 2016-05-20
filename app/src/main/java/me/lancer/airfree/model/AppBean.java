package me.lancer.airfree.model;

import android.graphics.drawable.Drawable;

import java.util.List;

public class AppBean {

    private int versionCode = 0;

    private String appName = "";

    private String packageName = "";

    private String versionName = "";

    private Drawable appIcon = null;

    public AppBean(){

    }

    public AppBean(int versionCode, String appName, String packageName,
                   String versionName, Drawable appIcon){
        this.versionCode = versionCode;
        this.appName = appName;
        this.packageName = packageName;
        this.versionName = versionName;
        this.appIcon = appIcon;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
