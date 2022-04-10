package com.hw.server0410;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread implements Runnable, MsgType {
    private Socket s;
    private HashMap<Socket, Integer> socketList;
    public ServerThread(Socket s, HashMap<Socket, Integer> socketList) {
        this.s = s;
        this.socketList = socketList;
    }
    //把当前在线客户端个数群发给所有在的用户
    public void sendUser(OutputStream os){

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
                int len = socketList.size();
                output.write(len);
                for(int i=0;i<len;i++) {
                    System.out.println("ServerThread发送用户上线消息协议:" + USER);
                    String userInfo = "死党" + socketList.get(socket) + "加入聊天!";
                    output.write(userInfo.getBytes());
                    output.flush();
                }
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
