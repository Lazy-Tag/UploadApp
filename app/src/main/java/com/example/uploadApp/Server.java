package com.example.uploadApp;

import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    private static final String HOST = "39.105.200.134";
    private static final int PORT = 5000;
    public void upload(String account, JSONObject data) {
        Thread thread = new Thread(() -> {
            try {
                Socket socket = new Socket(HOST, PORT);
                OutputStream outputStream = socket.getOutputStream();

                outputStream.write(("post" + "\n" + account + "\n" + data.toString()).getBytes(StandardCharsets.UTF_8));
                socket.shutdownOutput();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
