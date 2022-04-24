package com.hw.server0410;

import java.io.InputStream;
import java.io.InputStreamReader;
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
        System.out.println("服务器收到一条群聊消息:" + message);
        for (Socket socket : socketList.keySet()) {
            if (socket != this.s) {
                output = socket.getOutputStream();
                //先发送消息头
                output.write(GROUP);
                output.write(("死党" + socketList.get(s) + ":" + message + "\r\n").getBytes());
                output.flush();
            }
        }
    }

    private String getMessage(InputStreamReader input) throws Exception {
        StringBuffer message = new StringBuffer();
        int i = 0;
        while ((i = input.read()) != 13) {
            char c = (char) i;
            message.append(c);
            System.out.println("getMessage方法现在得到的消息是message = " + message);
        }
        return new String(message);
    }

    private String getUserId(InputStreamReader input) throws Exception {
        StringBuffer id = new StringBuffer();
        int i = 0;
        while ((i = input.read()) != 13) {
            char c = (char) i;
            id.append(c);
        }
        return new String(id);
    }

    //接收到客户端发送来的私聊消息，并找到私聊对象转发之
    private void handlePrivateMessage(InputStreamReader input) throws Exception {
        OutputStream output = null;
        String userId = getUserId(input);
        int id = Integer.parseInt(userId);
        System.out.println("ServerThread:获取到的私聊对象id是id = " + id);
        String message = getMessage(input);
        System.out.println("服务器收到一条私聊消息:" + message);
        //在哈希表中找出该id对应的客户，并取得该客户的输出流
        for (Socket socket : socketList.keySet()) {
            if (socketList.get(socket) == id) {
                output = socket.getOutputStream();
                break;
            }
        }
        if (output != null) {
            //首先发送消息头
            output.write(PRIVATE);
            output.write((message + "\r\n").getBytes());
            output.flush();
        } else {
            //socket为空就打印
            System.out.println("Output is null!");
        }
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
                InputStreamReader inputReader = new InputStreamReader(s.getInputStream());
                int head = input.read();
                System.out.println("ServerThread:当前读到的消息头是head = " + head);
                switch (head) {
                    case GROUP:
                        handleGroupMessage(input);
                        break;
                    case PRIVATE:
                        handlePrivateMessage(inputReader);
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
