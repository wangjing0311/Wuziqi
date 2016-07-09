package com.ylw.wuziqi.net;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;


public class MQRecive {
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

    Executor executor;

    public MQRecive() {
        executor = TaskUtil.createSerialExecutor();
    }

    public interface OnMsgListener {

        boolean onMsg(String msg);

    }

    private OnMsgListener onMsgListener;

    public void setOnMsgListener(OnMsgListener onMsgListener) {
        // TODO Auto-generated method stub
        this.onMsgListener = onMsgListener;
    }

    public void start() {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    startR();
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    start();
                }
            }
        });
    }

    public void startR() throws Exception {
        // TODO Auto-generated method stub
        String topic = "test_0011"; // topic
        String url = "http://publictest-rest.ons.aliyun.com/"; // 公测集群配置为http://publictest-rest.ons.aliyun.com/
        String ak = AliKey.ak(); // accesskey
        String sk = AliKey.sk(); // secretkey
        String cid = "CID_recive";// consumerId

        String date = String.valueOf(new Date().getTime());
        String sign = null;
        String NEWLINE = "\n";
        String signString;
        System.out.println(NEWLINE + NEWLINE);

        while (true) {
            URL uri = new URL(url + "message/?topic=" + topic + "&time=" + date + "&num=" + 32);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            signString = topic + NEWLINE + cid + NEWLINE + date;

            sign = HmacSHA1Signature.computeSignature(sk, signString).trim();

            conn.setRequestProperty(SIGNATURE, sign);
            conn.setRequestProperty(AK, ak);
            conn.setRequestProperty(CONSUMERID, cid);
            long start = System.currentTimeMillis();
            conn.connect();
            String msg = getMsg(conn);
            System.out.println("get cost:" + (System.currentTimeMillis() - start) / 1000 + "    "
                    + conn.getResponseCode() + "    " + msg);
            List<SimpleMessage> list = null;
            if (msg != null && msg.length() > 0) {
                list = JSON.parseArray(msg, SimpleMessage.class);
            }
            if (list == null || list.size() == 0) {
                Thread.sleep(500);
                continue;
            }
            System.out.println("size is :" + list.size());
            for (SimpleMessage simpleMessage : list) {
                date = String.valueOf(new Date().getTime());
                System.out.println(
                        "receive msg:" + simpleMessage.getBody() + "   born time " + simpleMessage.getBornTime());

                boolean isConsumer = true;
                if (onMsgListener != null) {
                    isConsumer = onMsgListener.onMsg(simpleMessage.getBody());
                }

                if (!isConsumer)
                    continue;

                uri = new URL(url + "message/?msgHandle=" + simpleMessage.getMsgHandle() + "&topic=" + topic + "&time="
                        + date);
                conn = (HttpURLConnection) uri.openConnection();
                conn.setRequestMethod("DELETE");

                signString = topic + NEWLINE + cid + NEWLINE + simpleMessage.getMsgHandle() + NEWLINE + date;
                sign = HmacSHA1Signature.computeSignature(sk, signString).trim();
                conn.setRequestProperty(SIGNATURE, sign);
                conn.setRequestProperty(AK, ak);
                conn.setRequestProperty(CONSUMERID, cid);
                conn.connect();
                System.out.println("delete msg:" + getMsg(conn));
            }
            Thread.sleep(500);
        }

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
