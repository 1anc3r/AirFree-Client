package me.lancer.airfree.sms;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import me.lancer.airfree.util.ApplicationUtil;

public class SmsReceiver extends BroadcastReceiver {

    private ApplicationUtil app;

    @Override
    public void onReceive(Context context, Intent intent) {
        app = (ApplicationUtil) context.getApplicationContext();
        Log.e("IP & PORT", "action:" + intent.getAction());
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage msg = null;
            String str = null;
            if (null != bundle) {
                Object[] objs = (Object[]) bundle.get("pdus");
                for (Object obj : objs) {
                    msg = SmsMessage.createFromPdu((byte[]) obj);
                    str = "一条来自" + msg.getOriginatingAddress() + "的短信:" + msg.getDisplayMessageBody();
                    app.sendMessage("sms", str);
                }
            }
        } else if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(new PhoneStateListener() {

                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    super.onCallStateChanged(state, incomingNumber);
                    switch (state) {
                        case TelephonyManager.CALL_STATE_IDLE:
                            Log.e("IP & PORT", "挂断");
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            Log.e("IP & PORT", "接听");
                            break;
                        case TelephonyManager.CALL_STATE_RINGING:
                            Log.e("IP & PORT", "一则来自" + incomingNumber + "的电话:");
                            app.sendMessage("sms", "一则来自" + incomingNumber + "的电话: ");
                            break;
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
}
