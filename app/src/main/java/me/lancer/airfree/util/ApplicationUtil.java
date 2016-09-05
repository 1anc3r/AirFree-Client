package me.lancer.airfree.util;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.SpeechUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import me.lancer.distance.R;

public class ApplicationUtil extends Application {

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
}
