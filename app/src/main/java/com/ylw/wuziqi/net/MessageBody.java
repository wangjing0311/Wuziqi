package com.ylw.wuziqi.net;

import com.alibaba.fastjson.JSON;
import com.ylw.wuziqi.Controller;

/**
 * author ylw
 * Created on 2016/7/9.
 * description : Wuziqi
 */
public class MessageBody {
    public static final int COMMAND = 0;
    public static final int STEP = 1;

    private String uid;
    private int code;
    private int commandCode;
    private String msg;
    private int x;
    private int y;

    public MessageBody() {
    }

    public MessageBody(int commandCode) {
        uid = Controller.myId;
        this.code = COMMAND;
        this.commandCode = commandCode;
    }

    public MessageBody(int x, int y) {
        uid = Controller.myId;
        this.code = STEP;
        this.x = x;
        this.y = y;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(int commandCode) {
        this.commandCode = commandCode;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public boolean isStep(){
        return STEP == code;
    }

    public boolean isMe() {
        return Controller.myId.equals(uid);
    }
}
