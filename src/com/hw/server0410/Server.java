package com.hw.server0410;

import com.hw.login_register0427.Login_Register;
import com.hw.login_register0427.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {
    private HashMap<Socket, Integer> socketList = new HashMap<>();
    private HashMap<User, Socket> userSocketHashMap = new HashMap<>();
    private List<Integer> idList = new ArrayList<>();
    public void createServer(int port) {
        int i = 1;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                //有一个客户端连上了服务器
                Socket client = serverSocket.accept();
                //为这个客户端创造一个User对象，该对象保存客户端的id,用户名和登录密码
                User user = new User();
                Login_Register lg = new Login_Register();
                //获取客户的用户名和登录密码
                lg.initEnterUI();
                user.setId(i);
                idList.add(i);
                //把它们组成键值对存入表中
                socketList.put(client, i++);
                userSocketHashMap.put(user, client);
                //启动服务器端线程开始工作
                ServerThread st = new ServerThread(client, socketList, idList);
                Thread t1 = new Thread(st);
                t1.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server().createServer(8888);
    }
}
