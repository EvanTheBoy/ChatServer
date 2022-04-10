package com.hw.server0410;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerThread implements Runnable, MsgType {
    private Socket s;
    private HashMap<Socket, Integer> socketList;
    private List<Integer> idList;
    public ServerThread(Socket s, HashMap<Socket, Integer> socketList, List<Integer> idList) {
        this.s = s;
        this.socketList = socketList;
        this.idList = idList;
    }

    //把当前在线客户端个数群发给所有在的用户
    public void sendUser(OutputStream os) throws Exception{
        //在线好友个数
        int len = idList.size();
        os.write(len);
        for (int i = 0; i < len; ++i) {
            String userInfo = "死党" + idList.get(i) + "加入聊天!";
            os.write(userInfo.getBytes());
        }
        os.flush();
    }

    @Override
    public void run() {
        try {
            InputStream input = s.getInputStream();
            OutputStream output;

            for (Socket socket : socketList.keySet()) {
                System.out.println("用户数量:" + socketList.keySet().size());
                output = socket.getOutputStream();
                output.write(USER);
                sendUser(output);
                System.out.println("用户上线提示消息发送完毕，未确认是否收到...");
            }

            while (true) {
                byte[] bytes = new byte[1024];
                int length = input.read(bytes);
                String message = new String(bytes, 0, length);
                for (Socket socket : socketList.keySet()) {
                    if (socket != this.s) {
                        output = socket.getOutputStream();
                        output.write(GROUP);
                        output.write(("死党" + socketList.get(s) + ":" + message).getBytes());
                        output.flush();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
