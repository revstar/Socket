package com.revstar.socket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MESSAGE_RECEIV_NEW_MSG=1;
    public static final int MESSAGE_SOCKET_CONNECTED=2;
    Button mSendBUtton;
    private TextView mMessageTextView;
    private EditText mMEssageEditText;
    private PrintWriter mPrintWriter;
    private Socket mClientSOcket;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_RECEIV_NEW_MSG:
                    mMessageTextView.setText(mMessageTextView.getText()+(String)msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    mSendBUtton.setEnabled(true);
                    break;
                    default:
                        break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMessageTextView=findViewById(R.id.msg_container);
        mSendBUtton=findViewById(R.id.send);
        mSendBUtton.setOnClickListener(this);
        mMEssageEditText=findViewById(R.id.msg);
        Intent service=new Intent(this,TCPServerService.class);
        startService(service);
        new Thread(){
            @Override
            public void run() {
               connectTCPServer();
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        if (mClientSOcket!=null){
            try {
                mClientSOcket.shutdownOutput();
                mClientSOcket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.onDestroy();

        }
    }

    @Override
    public void onClick(View v) {


        if (v==mSendBUtton){
            final String msg=mMEssageEditText.getText().toString();
            if (!TextUtils.isEmpty(msg)&&mPrintWriter!=null){
                mPrintWriter.println(msg);
                mMEssageEditText.setText("");
                String time=formatDateTime(System.currentTimeMillis());
                final String showMsg="self"+time+":"+msg+"\n";
                mMessageTextView.setText(mMessageTextView.getText()+showMsg);
            }
        }
    }

    private String formatDateTime(long time){
        return new SimpleDateFormat("HH:mm:ss").format(time);
    }

    private void connectTCPServer(){
        Socket socket=null;
        while (socket==null){
            try {
                socket=new Socket("localhost",8688);
                mClientSOcket=socket;
                mPrintWriter=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                System.out.println("connect server success");

            } catch (IOException e) {
                e.printStackTrace();
                SystemClock.sleep(1000);
                System.out.println("connect tcp server failed,retry...");
            }

        }
        //接收服务端消息
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!MainActivity.this.isFinishing()){
                String msg=br.readLine();
                System.out.println("receive:"+msg);
                if (msg!=null){
                    String time=formatDateTime(System.currentTimeMillis());
                    final  String showedMsg="server"+time+":"+msg+"\n";
                    mHandler.obtainMessage(MESSAGE_RECEIV_NEW_MSG,showedMsg).sendToTarget();
                }
            }
            System.out.println("quit...");
            MyUtils.close(mPrintWriter);
            MyUtils.close(br);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
