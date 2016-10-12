package me.lancer.airfree.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import me.lancer.airfree.adapter.ProblemAdapter;
import me.lancer.airfree.bean.ProblemBean;
import me.lancer.distance.R;

public class GuideActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvShow;
    private Button btnBack;
    private ListView lvProblem;

    private ProblemAdapter adapter;
    private List<ProblemBean> list = new ArrayList<>();

    private SharedPreferences pref;
    private String language = "zn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        init();
    }

    private void init() {
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        list.clear();
        tvShow = (TextView) findViewById(R.id.tv_show);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString(getString(R.string.language_choice ), "zn");
        if (language.equals("zn")) {
            tvShow.setText("使用说明");
            list.add(new ProblemBean("Q", "Q：我在哪里可以获取AirFree服务端？"));
            String downloadLink = "http://o7gy5l0ax.bkt.clouddn.com/AirFree-Server.zip";
            list.add(new ProblemBean("A", "A：" + downloadLink + "\n用浏览器打开链接即可下载，下载后解压运行exe文件"));
            list.add(new ProblemBean("Q", "Q：我应该如何使用客户端连接服务器"));
            list.add(new ProblemBean("A", "A：你可以点击主界面左上角按钮进入连接界面，输入服务端IP或者扫描服务端二维码连接。连接前请先确认客户端和服务端均连接在同一WLAN内。"));
            list.add(new ProblemBean("Q", "Q：我可以在无网络或者未连接服务端的情况下使用应用吗？"));
            list.add(new ProblemBean("A", "A：在无网络或者未连接服务端的情况下手机文件管理功能是可以使用的，你可以对客户端所有类型文件（夹）进行删除、剪切、复制操作，还可以浏览内外部存储，查看图片、文档，播放音乐、视频。"));
            list.add(new ProblemBean("Q", "Q：我应该如何上传文件到服务端或者从服务端下载文件？"));
            list.add(new ProblemBean("A", "A：上传文件：在内、外部存储、图片、音乐、视频、文档中选择李希望上传的文件，点击上传。\n下载文件：在远程设备目录中选择李希望下载的文件，点击下载。\n上传、下载过程中请耐心等待，该版本暂不支持断点续传。"));
            list.add(new ProblemBean("Q", "Q：我可以如何控制服务端？"));
            list.add(new ProblemBean("A", "A：你可以在键鼠控制中模拟远程设备上键盘、鼠标的操作，甚至放映PPT、播放视频。通过手势/语音控制可以快捷地打开远程设备程序。也可以远程调节音量和亮度。在电源选项中可以远程关机、重启、注销。在远程桌面中可以抓取屏幕和实时桌面。点击远程设备信息以获取电脑配置信息。多用户连接的情况下可以在聊天室中互传消息。"));
            list.add(new ProblemBean("Q", "Q：语音控制可以接受什么命令？"));
            list.add(new ProblemBean("A", "A：\"关机\"、\"重启\"、\"注销\"、\"截屏\"、\"关闭窗口\"、\"切换窗口\"、\"打开命令行\"、" +
                "\"打开任务管理器\"、\"打开资源管理器\"、\"打开我的电脑\"、\"打开[c/d/e/f...]盘\"、" +
                "\"打开设备管理器\"、\"打开磁盘管理器\"、\"打开注册表编辑器\"、\"打开计算器\"、" +
                "\"打开记事本\"、\"打开画图板\"、\"打开写字本\"、\"播放\"、\"暂停\"、\"快进\"、\"快退\"、\"升高音量\"、\"降低音量\"、\"全屏\"、\"结束放映\"、\"上一页\"、\"下一页\"、\"[...]唱支歌\"、\"[...]卖个萌\"、" +
                "\"搜索[...]\"..."));
            lvProblem = (ListView) findViewById(R.id.lv_problem);
            lvProblem.setDividerHeight(0);
            adapter = new ProblemAdapter(this, list);
            lvProblem.setAdapter(adapter);
        }else if (language.equals("en")) {
            tvShow.setText("Guide");
            list.add(new ProblemBean("Q", "Q：Where can I get AirFree-Server"));
            String downloadLink = "http://o7gy5l0ax.bkt.clouddn.com/AirFree-Server.zip";
            list.add(new ProblemBean("A", "A：" + downloadLink + "\nUse PC Browser to open the link to download zip file, extract the zip file, run the exe file."));
            list.add(new ProblemBean("Q", "Q：How can I use the client to connect to the server?"));
            list.add(new ProblemBean("A", "A：You can click the button in the upper-left corner of the main interface to enter the connection interface, input the IP address or scan QR code to connection.\n" +
                    "Ps. Make sure that both the client and the server are connected to the same WLAN."));
            list.add(new ProblemBean("Q", "Q：Can I use AirFree in the absence of network environment or without connection?"));
            list.add(new ProblemBean("A", "A：Yes, the file managemer function can be used," +
                    "You can delete, move, copy all types of files(folders) on the client, you can also view the internal and external storage, view pictures and " +
                    "document, play music and video, etc."));
            list.add(new ProblemBean("Q", "Q：How can I upload files to the server or download files from the server?"));
            list.add(new ProblemBean("A", "A：Upload：select the file you want to upload in the Internal and External storage, Pictures, Music, Video, Documents, Download, then click Upload." +
                    "\n" + "Download：select the file you want to download in the Directory, then click Download." +
                    "\n" + "Please be patient when you are uploading and downloading, AirFree can not support HTTP for now."));
            list.add(new ProblemBean("Q", "Q：How can I control the server?"));
            list.add(new ProblemBean("A", "A：You can through Keyboard/Mouse, Gesture, Voice to achieve some operations, such as typing, clicking, scrolling wheel, playing, pauseing, forwarding, rewinding, "
                    + "you can shutdown, restart, logout in Power Option, you can adjust the volume and brightness, you can also get Screen Capture and Real-time Desktop in Desktop, " +
                    "and you can get Remote Device Information and join in chatroom."));
//        list.add(new ProblemBean("Q", "Q：语音控制可以接受什么命令？"));
//        list.add(new ProblemBean("A", "A：\"关机\"、\"重启\"、\"注销\"、\"截屏\"、\"关闭窗口\"、\"切换窗口\"、\"打开命令行\"、" +
//                "\"打开任务管理器\"、\"打开资源管理器\"、\"打开我的电脑\"、\"打开[c/d/e/f...]盘\"、" +
//                "\"打开设备管理器\"、\"打开磁盘管理器\"、\"打开注册表编辑器\"、\"打开计算器\"、" +
//                "\"打开记事本\"、\"打开画图板\"、\"打开写字本\"、\"播放\"、\"暂停\"、\"快进\"、\"快退\"、\"升高音量\"、\"降低音量\"、\"全屏\"、\"结束放映\"、\"上一页\"、\"下一页\"、\"[...]唱支歌\"、\"[...]卖个萌\"、" +
//                "\"搜索[...]\"..."));
            lvProblem = (ListView) findViewById(R.id.lv_problem);
            lvProblem.setDividerHeight(0);
            adapter = new ProblemAdapter(this, list);
            lvProblem.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            setResult(RESULT_OK, null);
            finish();
        }
    }
}
