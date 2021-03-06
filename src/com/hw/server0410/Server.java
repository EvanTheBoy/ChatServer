package com.hw.server0410;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {
    private HashMap<Socket, Integer> socketList = new HashMap<>();
    private List<Integer> idList = new ArrayList<>();
    public void createServer(int port) {
        int i = 1;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                //有一个客户端连上了服务器
                Socket client = serverSocket.accept();
                idList.add(i);
                socketList.put(client, i++);
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
