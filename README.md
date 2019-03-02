# AirFree远程协助(Android)
<img src="https://github.com/1anc3r/AirFree-Client/blob/master/app/src/main/ic_launcher-web.png?raw=true" width = "96" height = "96" alt="icon"/>

## 应用简介:
AirFree是一款通过Android手机轻松访问、控制电脑的远程协助应用，能够为用户提供 Android 和 Windows 之间远程资源共享和设备控制功能，你不仅可以远程管理电脑中的资源，实现无线双向传输，还可以让 Android 手机摇身一变成为无线遥控器远程控制电脑。 

AirFree远程协助(Desktop)在[这里](https://github.com/1anc3r/AirFree-Server).

## 相关链接:
[<img src="https://camo.githubusercontent.com/b908ff6651d5ec11d504a37d2f441de9bc1362bc/68747470733a2f2f706c61792e676f6f676c652e636f6d2f696e746c2f656e5f75732f6261646765732f696d616765732f617070732f656e2d706c61792d62616467652e706e67" width = "245.1" height = "72.6" alt="icon"/>](https://play.google.com/store/apps/details?id=me.lancer.airfree)
* Github: [![](https://img.shields.io/badge/github-1anc3r-yellowgreen.svg)](https://github.com/1anc3r)
* 我的博客: [![](https://img.shields.io/badge/blog-1anc3r-green.svg)](http://1anc3r.github.io/)
* 实验室主页: [![](https://img.shields.io/badge/wiki-xiyoumobile-brightgreen.svg)](http://www.xiyoumobile.com/)
* AirFree远程协助(Android)下载链接:: [AirFree-Client.apk](http://fir.im/airfree)
* AirFree远程协助(Desktop)下载链接: [AirFree-Server.exe](http://pan.baidu.com/s/1skI7QFF)

## 使用方法:
1. 手机和电脑连接相同的Wi-Fi，或者手机/电脑开放热点给电脑/手机连接，然后打开Windows Server（WS）端程序和Android Client（AC）端
2. 点击AC端主界面左上角的连接按钮，WS端会将IP地址以文字和二维码的形式呈现，可以输入或者扫码连接
3. 然后就可以放心食用了。如果不连接的话，还是可以使用文件管理功能，浏览本机的图片、音乐、视频、文档、应用。对了，Download是放置WS端给AC端传输文件的目录，DCIM是放置截图的目录

## 功能介绍:
1. 远程设备：远程浏览WS端目录
2. 内外部存储.etc：管理AC端内外部存储.etc
3. 键鼠控制：模拟鼠标和键盘的操作
4. 手势控制：快速启动远程设备程序
5. 语音控制，功能同手势需联网使用
6. 音量亮度调节：调节远程设备音量亮度
7. 电源选项：远程关机、重启、注销
8. 远程桌面：屏幕抓取以及实时桌面
9. 聊天共享：多用户聊天
11. 常见问题，应用使用说明
12. 意见反馈，欢迎大家吐槽

## 应用界面:

<img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/主页_文件.png?raw=true" width = "288" height = "369" alt="" /><img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/主页_遥控.png?raw=true" width = "288" height = "369" alt="" /><img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/主页_设置.png?raw=true" width = "288" height = "369" alt="" />

<img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/文件_远程设备.png?raw=true" width = "288" height = "369" alt="" /><img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/文件_内部存储.png?raw=true" width = "288" height = "369" alt="" /><img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/文件_搜索.png?raw=true" width = "288" height = "369" alt="" />

<img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/文件_图片.png?raw=true" width = "288" height = "369" alt="" /><img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/文件_视频.png?raw=true" width = "288" height = "369" alt="" /><img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/文件_音乐.png?raw=true" width = "288" height = "369" alt="" />

<img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/遥控_键鼠.png?raw=true" width = "288" height = "369" alt="" /><img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/遥控_手势.png?raw=true" width = "288" height = "369" alt="" /><img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/遥控_远程桌面.png?raw=true" width = "288" height = "369" alt="" />

<img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/遥控_语音.png?raw=true" width = "288" height = "369" alt="" /><img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/遥控_聊天.png?raw=true" width = "288" height = "369" alt="" /><img src="https://github.com/1anc3r/AirFree-Client/blob/master/Screenshots/设置_语言.png?raw=true" width = "288" height = "369" alt="" />

## 技术要点
项目中文件管理功能通过 ContentProvider 获取/处理本机资源。远程控制功能以 Socket 作为传输手段，以 Json 作为传输格式，以 AsyncTask、IntentService 完成远程控制/ 传输操作。通过此项目熟悉了 Android 四大组件的工作过程，掌握了基于 Socket 的网络编程， 掌握了Android的多线程编程，培养了使用第三方SDK的能力。 

