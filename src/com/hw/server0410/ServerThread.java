package com.hw.server0410;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
    public void sendUser(OutputStream os) throws Exception {
        //在线好友个数
        int len = idList.size();
        System.out.println("server = "+len);
        os.write(len);
        os.flush();
        for (int i = 0; i < len; ++i) {
            String userInfo = "死党" + idList.get(i);
            System.out.println("userInfo = "+userInfo+"  "+i);
            sendString(os, userInfo);
            os.flush();
        }
    }

    //用于循环内部以#对消息进行分割
    public void sendString(OutputStream os, String msg) throws Exception {
        String str = msg + "#";
        os.write(str.getBytes());
        os.flush();
    }

    //读取群聊消息，并转发
    private void handleGroupMessage(InputStream input) throws Exception {
        OutputStream output;
        byte[] bytes = new byte[1024];
        int length = input.read(bytes);
        String message = new String(bytes, 0, length);
        System.out.println("服务器收到一条消息:" + message);
        for (Socket socket : socketList.keySet()) {
            if (socket != this.s) {
                output = socket.getOutputStream();
                output.write(GROUP);
                output.write(("死党" + socketList.get(s) + ":" + message + "\r\n").getBytes());
                output.flush();
            }
        }
    }

    private void handlePrivateMessage(InputStream input) throws Exception {
        OutputStream output = null;
        int id = input.read();
        byte[] bytes = new byte[1024];
        int length = input.read(bytes);
        String message = new String(bytes, 0, length);
        System.out.println("服务器收到一条消息:" + message);
        //在哈希表中找出该id对应的客户，并取得该客户的输出流
        for (Socket socket : socketList.keySet()) {
            Socket s = socket;
            if (socketList.get(s) == id) {
                output = s.getOutputStream();
                break;
            }
        }
        output.write(message.getBytes());
        output.flush();
    }

    //向客户端转发上线用户的信息
    private void transferUserInfo() throws Exception {
        OutputStream output;
        for (Socket socket : socketList.keySet()) {
            System.out.println("用户数量:" + socketList.keySet().size());
            output = socket.getOutputStream();
            output.write(USER);
            sendUser(output);
            System.out.println("用户上线提示消息发送完毕，未确认是否收到...");
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                InputStream input = s.getInputStream();
                int head = input.read();
                switch (head) {
                    case GROUP:
                        handleGroupMessage(input);
                        break;
                    case PRIVATE:

                        break;
                    case USER:
                        transferUserInfo();
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
