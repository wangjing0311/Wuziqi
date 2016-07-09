package com.ylw.wuziqi;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.ylw.wuziqi.net.MQRecive;
import com.ylw.wuziqi.net.MQSend;
import com.ylw.wuziqi.net.MessageBody;
import com.ylw.wuziqi.net.TaskUtil;

/**
 * author ylw
 * Created on 2016/7/9.
 * description : Wuziqi
 */
public class Controller {

    private static final int WAIT = 0;
    private static final int ACCECPT = 1;

    public static String myId;
    private final Context context;
    private FiveBackground fiveBackground;
    MQRecive recive;
    MQSend send;

    String friendId = "";
    static Controller c;

    public Controller(Context context, FiveBackground fiveBackground) {
        this.context = context;
        this.fiveBackground = fiveBackground;
        recive = new MQRecive();
        recive.setOnMsgListener(new MQRecive.OnMsgListener() {
            @Override
            public boolean onMsg(String msg) {
                boolean result = handleMsg(JSON.parseObject(msg, MessageBody.class));
                if (!result) {
                    send.send(msg);
                }
                return true;
            }
        });
        recive.start();
        send = new MQSend();
        myId = DeviceUtil.getUniquePsuedoID();
        c = this;
        connect();
    }

    private boolean handleMsg(MessageBody msg) {
        if (msg.isMe())
            return false;
        if (msg.isStep()) {
            if (!friendId.equals(msg.getUid())) {
                return false;
            }
            draw(msg.getX(), msg.getY());
        } else {
            switch (msg.getCommandCode()) {
                case WAIT:
                    friendId = msg.getUid();
                    send(ACCECPT);
                    viewMsg("收到 friendId ： " + friendId);
                    break;
                case ACCECPT:
                    friendId = msg.getUid();
                    beginGame();
                    viewMsg("收到 ACCECPT ： 开始游戏");
                    break;
            }
        }
        return true;
    }

    private void draw(final int x, final int y) {
        TaskUtil.postMainTask(new Runnable() {
            @Override
            public void run() {
                fiveBackground.step(x, y);
                ((MainActivity) context).draw();
            }
        });
    }

    public static void draw() {
        TaskUtil.postMainTask(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) c.context).draw();
            }
        });
    }

    private void viewMsg(final String msg) {
        TaskUtil.postMainTask(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) context).setMsg(msg);
            }
        });
    }

    private void beginGame() {
        fiveBackground.start();
    }

    private void connect() {
        send(WAIT);
    }


    public static void send(int x, int y) {
        MessageBody body = new MessageBody(x, y);
        c.send.send(body.toString());
    }

    public static void send(int code) {
        MessageBody body = new MessageBody(code);
        c.send.send(body.toString());
    }

    public void setView(FiveBackground fiveBackground) {
        this.fiveBackground = fiveBackground;
    }

    public static void gameOver() {

    }

    public void restart() {
        fiveBackground.restart();
    }
}
