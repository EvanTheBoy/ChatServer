package com.hw.server0410;

public interface MsgType {
    public final byte GROUP = 1;  //群聊
    public final byte PRIVATE = 2;  //私聊
    public final byte USER = 3;  //用户上线
    public final byte BROADCAST = 4; //广播
}
