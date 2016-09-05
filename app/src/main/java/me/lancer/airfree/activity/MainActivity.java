package me.lancer.airfree.activity;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import me.lancer.airfree.util.ApplicationUtil;
import me.lancer.distance.R;

public class MainActivity extends BaseActivity {

    ApplicationUtil app;

    private Context context = null;
    private TabHost tabHost = null;
    private ViewPager pager = null;
    private FloatingActionButton fabLink;

    private long exitTime;
    private LocalActivityManager manager = null;
    private List<View> viewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (ApplicationUtil) MainActivity.this.getApplication();
        if (app.getmPrintWriterClient() != null) {
            sendMessage("link", getIPAddress());
        }
        context = MainActivity.this;
        pager = (ViewPager) findViewById(R.id.viewpager);
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);
        Intent intent1 = new Intent(context, TransfActivity.class);
        viewList.add(getView("A", intent1));
        Intent intent2 = new Intent(context, RemoteActivity.class);
        viewList.add(getView("B", intent2));
        Intent intent3 = new Intent(context, SettingActivity.class);
        viewList.add(getView("C", intent3));
        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();
        tabHost.setup(manager);
        RelativeLayout tabIndicator1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_host_item, null);
        TextView tvTab1 = (TextView) tabIndicator1.findViewById(R.id.tv_title);
        tvTab1.setText("文件");
        RelativeLayout tabIndicator2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_host_item, null);
        TextView tvTab2 = (TextView) tabIndicator2.findViewById(R.id.tv_title);
        tvTab2.setText("遥控");
        RelativeLayout tabIndicator3 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_host_item, null);
        TextView tvTab3 = (TextView) tabIndicator3.findViewById(R.id.tv_title);
        tvTab3.setText("设置");
        Intent intent = new Intent(context, EmptyActivity.class);
        tabHost.addTab(tabHost.newTabSpec("A").setIndicator(tabIndicator1).setContent(intent));
        tabHost.addTab(tabHost.newTabSpec("B").setIndicator(tabIndicator2).setContent(intent));
        tabHost.addTab(tabHost.newTabSpec("C").setIndicator(tabIndicator3).setContent(intent));
        pager.setAdapter(new tabhostAdapter(viewList));
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                tabHost.setCurrentTab(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                if ("A".equals(tabId)) {
                    pager.setCurrentItem(0);
                }
                if ("B".equals(tabId)) {
                    pager.setCurrentItem(1);
                }
                if ("C".equals(tabId)) {
                    pager.setCurrentItem(2);
                }
                if ("D".equals(tabId)) {
                    pager.setCurrentItem(3);
                }
            }
        });

        fabLink = (FloatingActionButton) findViewById(R.id.fab_link);
        fabLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LinkActivity.class);
                startActivity(intent);
            }
        });
    }

    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }

    private class tabhostAdapter extends PagerAdapter {

        private List<View> list;

        private tabhostAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public void destroyItem(View view, int position, Object arg2) {
            ViewPager pViewPager = ((ViewPager) view);
            pViewPager.removeView(list.get(position));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(View view, int position) {
            ViewPager pViewPager = ((ViewPager) view);
            pViewPager.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

    }

    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ShowToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                app.off();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                        return ipaddress = ip.getHostAddress();
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
