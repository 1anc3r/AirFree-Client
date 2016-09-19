package me.lancer.airfree.util;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.SpeechUtility;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import me.lancer.distance.R;

public class ApplicationUtil extends Application {

    private mRunnable runnable;
    public boolean connected = false;
    public boolean reachable = false;
    public Bitmap mBitmap;
    private Handler mHandler = null;

    private boolean isExplosing;
    private boolean isConnecting;
    private Socket mSocketClient = null;
    private BufferedReader mBufferedReaderClient = null;
    private PrintWriter mPrintWriterClient = null;

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(ApplicationUtil.this, "appid=" + getString(R.string.app_id));
        isExplosing = false;
        isConnecting = false;
        mSocketClient = null;
        mBufferedReaderClient = null;
        mPrintWriterClient = null;
    }

    public int init(String ip, String port){
        int port_ = Integer.parseInt(port);
        try {
            mSocketClient = new Socket();
            SocketAddress mSocketAddress = new InetSocketAddress(ip, port_);
            mSocketClient.connect(mSocketAddress, 1500);
            mBufferedReaderClient = new BufferedReader(
                    new InputStreamReader(mSocketClient.getInputStream()));
            mPrintWriterClient = new PrintWriter(
                    mSocketClient.getOutputStream(), true);

            Log.e("IP & PORT", " 连接成功!");
            Toast.makeText(this, "连接成功!", Toast.LENGTH_SHORT).show();

            if (mSocketClient != null) {
                return 1;
            } else {
                Log.e("IP & PORT", " 没有连接!");
                Toast.makeText(this, "没有连接!", Toast.LENGTH_SHORT).show();
                return 0;
            }
        } catch (Exception e) {
            Log.e("IP & PORT", " 连接失败!");
            Toast.makeText(this, "连接失败!", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    public void off(){
        try {
            if (mSocketClient != null) {
                isConnecting = false;
                mBufferedReaderClient.close();
                mBufferedReaderClient = null;
                mPrintWriterClient.close();
                mPrintWriterClient = null;
                mSocketClient.close();
                mSocketClient = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIsConnecting(boolean isConnecting) {
        this.isConnecting = isConnecting;
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public Socket getmSocketClient() {
        return mSocketClient;
    }

    public void setmSocketClient(Socket mSocketClient) {
        this.mSocketClient = mSocketClient;
    }

    public BufferedReader getmBufferedReaderClient() {
        return mBufferedReaderClient;
    }

    public void setmBufferedReaderClient(BufferedReader mBufferedReaderClient) {
        this.mBufferedReaderClient = mBufferedReaderClient;
    }

    public PrintWriter getmPrintWriterClient() {
        return mPrintWriterClient;
    }

    public void setmPrintWriterClient(PrintWriter mPrintWriterClient) {
        this.mPrintWriterClient = mPrintWriterClient;
    }

    public boolean isExplosing() {
        return isExplosing;
    }

    public void setIsExplosing(boolean isExplosing) {
        this.isExplosing = isExplosing;
    }

    public void sendTxtMessage(String message){
        runnable.sendTxtMessage(message);
    }

    public void sendImgMessage(String message) {
        runnable.sendImgMessage(message);
    }

    public void startThread(String ip){
        runnable = new mRunnable(ip);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void stopThread(){
        if(connected){
            runnable.closeSocket();
        }
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void sendMessage(String command, String parameter) {
        if (getmPrintWriterClient() != null) {
            try {
                JSONObject jb = new JSONObject();
                jb.put("command", command);
                jb.put("parameter", parameter);
                if (jb.toString() != null) {
                    getmPrintWriterClient().print(jb.toString() + "\n");
                    getmPrintWriterClient().flush();
                    Log.e("IP & PORT", "发送成功:" + jb.toString());
                }
            } catch (Exception e) {
                Log.e("IP & PORT", "发送异常:" + e.getMessage());
            }
        }
    }

    public class mRunnable implements Runnable {

        private InetAddress mInetAddress;
        private final int txtPort = 59673;
        private final int imgPort = 59674;
        private DatagramSocket txtSocket, imgSocket;
        byte[] txtBuf = new byte[1000];
        byte[] imgBuf = new byte[8192];
        public static final String msgInit = "Init", msgBegin = "Begin", msgEnd = "End";

        public mRunnable(String ip) {
            try {
                mInetAddress = InetAddress.getByName(ip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                txtSocket = new DatagramSocket();
                imgSocket = new DatagramSocket();
                connected = testConnection();
                if (connected) {
                    msgListener();
                    surveyConnection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void msgListener() {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (connected) {
                        boolean iprepared = false;
                        DatagramPacket imgPacket = new DatagramPacket(imgBuf, 0, imgBuf.length);
                        try {
                            imgSocket.receive(imgPacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String imgRecPkgContent = new String(imgPacket.getData(), 0, imgPacket.getLength());
                        if (imgRecPkgContent.equals(msgInit)) {
                            sendImgMessage(msgBegin);//echo msg "begin"
                        } else if (imgRecPkgContent.equals(msgBegin)) {
                            iprepared = true;
                        }
                        if (!iprepared) {
                            continue;
                        }
                        imgBuf = new byte[8192];
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        while (iprepared) {
                            try {
                                iprepared = connected;
                                imgPacket = new DatagramPacket(imgBuf, imgBuf.length);
                                imgSocket.receive(imgPacket);
                                String msg = new String(imgPacket.getData(), 0, imgPacket.getLength());
                                if (msg.startsWith(mRunnable.msgEnd)) {
                                    iprepared = false;
                                    break;
                                }
                                byteArrayOutputStream.write(imgPacket.getData(), 0, imgPacket.getLength());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        byte[] imgReceived = byteArrayOutputStream.toByteArray();
                        try {
                            byteArrayOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mBitmap = BitmapFactory.decodeByteArray(imgReceived, 0, imgReceived.length);
                        mHandler.obtainMessage(EnumMessageInfo.MsgBitmapGenerated).sendToTarget();
                    }
                }
            }).start();
        }

        public void sendImgMessage(String message) {
            try {
                imgBuf = message.getBytes();
                DatagramPacket outPacket = new DatagramPacket(imgBuf, imgBuf.length, mInetAddress, imgPort);
                imgSocket.send(outPacket);
            } catch (IOException e) {
                if (e.getMessage().equals("Network unreachable")) {
                    reachable = false;
                }
                closeSocketNoMessge();
            }
        }

        public void sendTxtMessage(String message) {
            try {
                txtBuf = message.getBytes();
                DatagramPacket outPacket = new DatagramPacket(txtBuf, txtBuf.length, mInetAddress, txtPort);
                txtSocket.send(outPacket);
                reachable = true;
            } catch (Exception e) {
                if (e.getMessage().equals("Network unreachable")) {
                    reachable = false;
                }
                closeSocketNoMessge();
            }
        }

        public void closeSocketNoMessge() {
            txtSocket.close();
            imgSocket.close();
            connected = false;
        }

        public void closeSocket() {
            sendTxtMessage(new String("Close"));
            txtSocket.close();
            imgSocket.close();
            connected = false;
        }

        private boolean testConnection() {
            try {
                if (!connected) {
                    txtBuf = new String("Connectivity").getBytes();
                } else {
                    txtBuf = new String("Connected").getBytes();
                }
                DatagramPacket outPacket = new DatagramPacket(txtBuf, txtBuf.length, mInetAddress, txtPort);
                txtSocket.send(outPacket);
            } catch (Exception e) {
                return false;
            }

            try {
                DatagramPacket inPacket = new DatagramPacket(txtBuf, txtBuf.length);
                txtSocket.receive(inPacket);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private void surveyConnection() {
            int count = 0;
            while (connected) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!testConnection()) {
                    count++;
                } else {
                    count = 0;
                }
                if (count == 3) {
                    closeSocket();
                    return;
                }
            }
        }
    }
}
