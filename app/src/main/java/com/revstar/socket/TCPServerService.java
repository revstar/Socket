package com.revstar.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Create on 2019/1/11 9:48
 * author revstar
 * Email 1967919189@qq.com
 */
public class TCPServerService extends Service {

    private Boolean mIsServiceDestoryed=false;

    private String[]mDefinedMessages=new String[]{
      "你好啊，哈哈",
      "请问你叫什么名字呀？",
      "你知道吗？我可是可以个多个人同时聊天的哦！",
      "给你讲个笑话吧:据说爱笑的人运气不会太差，不知道真假。"
    };

    @androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        new Thread(new TcpServer()).start();
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed=true;
        super.onDestroy();
    }

    private class TcpServer implements Runnable{


        @Override
        public void run() {
            ServerSocket serverSocket=null;

            try {
                //监听本地8688端口
                serverSocket=new ServerSocket(8688);
            } catch (IOException e) {
                System.err.println("establish tcp server filed,port:8688");
                e.printStackTrace();
                return;
            }
            while (!mIsServiceDestoryed){
                //接收客户端请求
                try {
                    final Socket client=serverSocket.accept();
                    System.out.println("accept");
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws IOException {

        //用于接收客户端消息
        BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));
        //用于向客户端发送消息
        PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
        out.println("欢迎来到聊天室！");
        while (!mIsServiceDestoryed){
            String str=in.readLine();
            System.out.println("msg from client"+str);
            if (str==null){
                //客户端断开
                break;
            }
            int i=new Random().nextInt(mDefinedMessages.length);
            String msg=mDefinedMessages[i];
            out.println("send:"+msg);
        }
        System.out.println("client quit.");
        //关闭流
        MyUtils.close(out);
        MyUtils.close(in);
        client.close();


    }
}
