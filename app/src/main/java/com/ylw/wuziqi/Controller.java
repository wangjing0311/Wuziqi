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
    private static final int LEAVE = 2;

    public static String myId;
    private Context context;
    private FiveBackground fiveBackground;
    MQRecive recive;
    MQSend send;

    String friendId = "";
    public static Controller c;
    private boolean isBegin = false;
    public boolean withMe = true;

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
        send = new MQSend();
        myId = DeviceUtil.getUniquePsuedoID() + Math.random();
        c = this;
    }

    private boolean handleMsg(MessageBody msg) {
        if (withMe) return false;
        if (msg.isMe())
            return false;
        if (msg.isStep()) {
            if (!friendId.equals(msg.getUid())) {
                return false;
            }
            draw(msg.getX(), msg.getY());
        } else if (!isBegin) {
            switch (msg.getCommandCode()) {
                case WAIT:
                    friendId = msg.getUid();
                    send(ACCECPT);
                    viewMsg("开始连接");
                    break;
                case ACCECPT:
                    friendId = msg.getUid();
                    beginGame();
                    viewMsg("开始游戏");
                    break;

            }
        } else {
            if (!friendId.equals(msg.getUid())) {
                return false;
            }
            switch (msg.getCommandCode()) {
                case LEAVE:
                    viewMsg("对方离开");
                    viewMsg("等待连接");
                    isBegin = false;
                    connect();
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
        isBegin = true;
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


    public static void gameOver() {
        c.isBegin = false;
    }

    public void restart() {
        c.isBegin = true;
        fiveBackground.restart();
    }

    public static void startAnim() {
        ((MainActivity) c.context).anim();
    }

    public void withMe(boolean b) {
        withMe = b;
        if (withMe) {
            c.isBegin = true;
            fiveBackground.restart();
            recive.stop();
            leave();
        } else {
            c.isBegin = false;
            fiveBackground.restart();
            fiveBackground.setWait(true);
            recive.start();
            connect();
        }
    }

    public void leave() {
        send(LEAVE);
    }

    public void destroy() {
        recive.stop();
    }
}
