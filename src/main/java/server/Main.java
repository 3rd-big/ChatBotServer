package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        if (args.length != 1) {
            logger.error("Usage: java -jar ChatBotServer.jar [port]");
            return;
        }

        int port = Integer.parseInt(args[0]);
        new ChatBotServer(port).start();
    }
}
