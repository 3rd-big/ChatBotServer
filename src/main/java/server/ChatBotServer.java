package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatBotServer {

    final int PORT;
    private static final Logger logger = LogManager.getLogger(ChatBotServer.class);

    public ChatBotServer(int port) {
        PORT = port;
    }

    void start() {
        logger.info("ChatBotServer {} port Start", PORT);
        try {
                ServerSocket serverSocket = new ServerSocket(this.PORT);

                while (true) {
                    Socket socket = serverSocket.accept();
                    InetAddress inetAddress = socket.getInetAddress();
                    logger.info("Connect {}", inetAddress.getHostAddress());

                    ClientThread clientThread = new ClientThread(socket);
                    clientThread.start();
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
