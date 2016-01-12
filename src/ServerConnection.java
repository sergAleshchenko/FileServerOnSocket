import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sergey on 07.12.15.
 */
public class ServerConnection implements Runnable {
    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(4444);
            System.out.println("ServerConnection started.");
        } catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
        }

        while (true) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection : " + clientSocket);
                System.out.println();
                Thread t = new Thread(new ServerSide(clientSocket));

                t.start();

            } catch (Exception e) {
                System.err.println("Error in connection attempt.");
            }
        }

    }
}
