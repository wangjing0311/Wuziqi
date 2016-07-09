package com.ylw.wuziqi.net;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.Executor;


public class MQSend {

    private static final String TAG = "MQSend";
    public static String SIGNATURE = "Signature";
    public static String NUM = "num";
    public static String CONSUMERID = "ConsumerId";
    public static String PRODUCERID = "ProducerId";
    public static String TIMEOUT = "timeout";
    public static String TOPIC = "topic";
    public static String AK = "AccessKey";
    public static String BODY = "body";
    public static String MSGHANDLE = "msgHandle";
    public static String TIME = "time";

    String topic = "test_0011"; // topic
    String url = "http://publictest-rest.ons.aliyun.com/"; // 公测集群配置为http://publictest-rest.ons.aliyun.com/
    String ak = AliKey.ak(); // accesskey
    String sk = AliKey.sk(); // secretkey
    String pid = "PID_publish"; // producerId

    String sign = null;
    String body = "hello ons http";
    String NEWLINE = "\n";
    String signString;

    Executor executor;

    public MQSend() {
        executor = TaskUtil.createSerialExecutor();
    }

    public void send(final String msg) {
        if(msg!=null)return;// TODO: 2016/7/10 stop send msg
        executor.execute(new Runnable() {
            int time = 1000;

            @Override
            public void run() {
                try {
                    time = 1000;
                    sendM(msg);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    time += 100;
                    time = (time > 30000) ? 30000 : time;
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e1) {
                        Log.e(TAG, e1.getMessage(), e1);
                    }
                    send(msg);
                }
            }
        });
    }

    public void sendM(String msg) throws Exception {
        // TODO Auto-generated method stub
        body = msg;
        String date = String.valueOf(new Date().getTime());
        URL uri = new URL(url + "message?topic=" + topic + "&time=" + date + "&tag=http" + "&key=http");
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setRequestMethod("POST");
//        conn.setDoOutput(true);
        // conn.setUseCaches(false);

        signString = topic + NEWLINE + pid + NEWLINE + MD5.getInstance().getMD5String(body) + NEWLINE + date;//
//        Log.d(TAG, signString);
        sign = HmacSHA1Signature.computeSignature(sk, signString).trim();

        conn.setRequestProperty(SIGNATURE, sign);
        conn.setRequestProperty(AK, ak);
        conn.setRequestProperty(PRODUCERID, pid);
        conn.setRequestProperty("Content-Type", "text/plain;charset=UTF-8");
        conn.connect();
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        String content = body;
        out.write(content.getBytes());
        out.flush();
        out.close();

        int code = conn.getResponseCode();

        String result = getMsg(conn);
        Log.d(TAG, "send msg:" + code + " " + result);

    }

    private static String getMsg(HttpURLConnection conn) throws IOException {
        InputStream in = conn.getInputStream();
        byte[] buffer = new byte[2048];
        ByteArrayBuffer bs = new ByteArrayBuffer(2048);
        int len = 0;
        while ((len = in.read(buffer)) != -1) {
            bs.append(buffer, 0, len);
        }
        String msg = new String(bs.toByteArray());
        return msg;
    }

}
