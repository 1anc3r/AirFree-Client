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

import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import me.lancer.airfree.adapter.TalkAdapter;
import me.lancer.airfree.model.TalkBean;
import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class TalkActivity extends BaseActivity implements View.OnClickListener {

    private Button btnBack;
    private Button btnSend;
    private EditText etContent;
    private ListView lvTalk;

    ApplicationUtil app;
    private TalkAdapter adapter;
    private List<TalkBean> list = new ArrayList<>();

    private Handler mHandler = new Handler() {

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
                for(TalkBean item : list){
                    Log.e("IP & PORT", item.getType()+" "+item.getId()+" "+item.getContent());
                }
                lvTalk.smoothScrollToPosition(list.size()-1);
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

    private void init() {
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
        new readServer();
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            setResult(RESULT_OK, null);
            finish();
        } else if (v == btnSend) {
            sendMessage("talk", getIPAddress() + ":" + etContent.getText() + "");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }

    class readServer extends Thread {
        private BufferedReader reader;

        public readServer() {
            try {
                reader = app.getmBufferedReaderClient();
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                while (true) {
                    String content = reader.readLine();
                    Log.e("IP & PORT", content);
                    Message msg = new Message();
                    msg.obj = content;
                    mHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
