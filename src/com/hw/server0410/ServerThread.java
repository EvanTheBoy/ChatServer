package com.hw.server0410;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread implements Runnable {
    private Socket s;
    private HashMap<Socket, Integer> socketList;
    public ServerThread(Socket s, HashMap<Socket, Integer> socketList) {
        this.s = s;
        this.socketList = socketList;
    }
    

    @Override
    public void run() {
        try {
            InputStream input = s.getInputStream();
            OutputStream output;

            for (Socket socket : socketList.keySet()) {
                output = socket.getOutputStream();
                String userInfo = "死党" + socketList.get(s) + "加入聊天!";
                output.write(userInfo.getBytes());
                output.flush();
            }

            while (true) {
                byte[] bytes = new byte[1024];
                int length = input.read(bytes);
                String message = new String(bytes, 0, length);
                for (Socket socket : socketList.keySet()) {
                    if (socket != this.s) {
                        output = socket.getOutputStream();
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
