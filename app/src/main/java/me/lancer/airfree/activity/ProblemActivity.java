package me.lancer.airfree.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.lancer.airfree.adapter.ProblemAdapter;
import me.lancer.airfree.bean.ProblemBean;
import me.lancer.distance.R;

public class ProblemActivity extends BaseActivity implements View.OnClickListener {

    private Button btnBack;
    private ListView lvProblem;

    private ProblemAdapter adapter;
    private List<ProblemBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        init();
    }

    private void init() {
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        list.clear();
        list.add(new ProblemBean("Q", "Q：我在哪里可以获取服务端？"));
        String downloadLink = "http://o7gy5l0ax.bkt.clouddn.com/AirFree-Server.zip";
        list.add(new ProblemBean("A", "A："+downloadLink+"\n用浏览器打开链接即可下载，下载后解压运行exe文件"));
        list.add(new ProblemBean("Q", "Q：我应该如何使用客户端连接服务端？"));
        list.add(new ProblemBean("A", "A：你可以点击主界面左上角按钮进入连接界面，输入服务端IP或者扫描二维码连接。" +
                "连接前请先确认客户端和服务端均连接在同一局域网内。"));
        list.add(new ProblemBean("Q", "Q：我在无网络或未连接状态下可以使用客户端吗？"));
        list.add(new ProblemBean("A", "A：可以，在无网络或未连接状态下文件管理功能是可以使用的，" +
                "你可以对客户端所有类型文件（夹）进行删除、移动、复制操作，还可以查看内、外部存储，查看图片、" +
                "文档，播放音乐、视频.etc."));
        list.add(new ProblemBean("Q", "Q：我应该如何上传文件到服务端或从服务端下载文件？"));
        list.add(new ProblemBean("A", "A：上传文件：在内、外部存储、图片、音乐、视频、文档、" +
                "下载中选择你希望上传的文件，点击上传。" + "\n" + "下载文件：在电脑磁盘中选择你希望下载的文件，" +
                "点击下载。" + "\n" + "上传、下载过程中请耐心等待，本应用暂不支持断点续传。"));
        list.add(new ProblemBean("Q", "Q：我可以如何控制服务端？"));
        list.add(new ProblemBean("A", "A：你可以在键鼠控制中控制鼠标和输入文字，通过手势/语音控制快捷地打开远程设备程序，"
                + "\n" + "在电源选项中选择关机、重启、注销，在远程桌面中选择屏幕抓取和实时桌面，调节音量和亮度，" + "\n" +
                "获取远程设备信息以及多用户聊天共享。"));
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
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            setResult(RESULT_OK, null);
            finish();
        }
    }
}
