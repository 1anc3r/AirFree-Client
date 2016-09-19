package me.lancer.airfree.sms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by HuangFangzhi on 2016/9/18.
 */
public class SmsService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("on bind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("on create");

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        new SmsReceiver();
    }
}
