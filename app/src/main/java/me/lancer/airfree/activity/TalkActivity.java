package me.lancer.airfree.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import me.lancer.airfree.adapter.TalkAdapter;
import me.lancer.airfree.bean.TalkBean;
import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class TalkActivity extends BaseActivity implements View.OnClickListener {

    ApplicationUtil app;

    private Button btnBack;
    private Button btnSend;
    private EditText etContent;
    private ListView lvTalk;

    private TalkAdapter adapter;
    private List<TalkBean> list = new ArrayList<>();
    private Thread mThreadClient = null;
    private String recvMessageClient = "";
    private boolean iStop = false;

    private Handler tHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj != null) {
                String args[] = msg.obj.toString().split(":");
                if (getIPAddress().equals(args[0])) {
                    list.add(new TalkBean("A", args[0], args[1]));
                } else {
                    list.add(new TalkBean("Q", args[0], args[1]));
                }
                lvTalk.requestLayout();
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        init();
    }

    @Override
    protected void onDestroy() {
        if (iStop == false) {
            sendMessage("talk", getIPAddress() + ":退出了聊天室");
            iStop = true;
//        tHandler.removeCallbacks(tRunnable);
            mThreadClient.interrupt();
        }
        super.onDestroy();
    }

    private void init() {
        sendMessage("talk", getIPAddress() + ":加入了聊天室");
        app = (ApplicationUtil) this.getApplication();
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        etContent = (EditText) findViewById(R.id.et_content);
        lvTalk = (ListView) findViewById(R.id.lv_talk);
        lvTalk.setDividerHeight(0);
        adapter = new TalkAdapter(this, list);
        lvTalk.setAdapter(adapter);
        lvTalk.smoothScrollToPosition(adapter.getCount() - 1);
//        tHandler.post(tRunnable);
        mThreadClient = new Thread(tRunnable);
        mThreadClient.start();
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            if (iStop == false) {
                sendMessage("talk", getIPAddress() + ":退出了聊天室");
                iStop = true;
//        tHandler.removeCallbacks(tRunnable);
                mThreadClient.interrupt();
            }
            setResult(RESULT_OK, null);
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        } else if (v == btnSend) {
            if (etContent.getText().toString().equals("")) {
                Log.e("IP ＆ PORT", "null");
                ShowToast("发送消息不能为空!");
            } else {
                sendMessage("talk", getIPAddress() + ":" + etContent.getText() + "");
                etContent.setText("");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (iStop == false) {
                sendMessage("talk", getIPAddress() + ":退出了聊天室");
                iStop = true;
//        tHandler.removeCallbacks(tRunnable);
                mThreadClient.interrupt();
            }
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }

    private Runnable tRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                while (!iStop) {
                    recvMessageClient = app.getmBufferedReaderClient().readLine();
                    Log.e("IP & PORT", "接收成功(T):" + recvMessageClient);
                    JSONTokener jt = new JSONTokener(recvMessageClient);
                    JSONObject jb = (JSONObject) jt.nextValue();
                    String command = jb.getString("command");
                    String paramet = jb.getString("parameter");
                    if (command.contains("talk")) {
                        Message msg = tHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = paramet;
                        tHandler.sendMessage(msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public String getIPAddress() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        return ip.getHostAddress();
                    }
                }

            }
        } catch (SocketException e) {
            Log.e("IP & PORT", "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipaddress;
    }
}
