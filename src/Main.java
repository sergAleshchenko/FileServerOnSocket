import java.io.IOException;

/**
 * Created by sergey on 07.12.15.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        new Thread(new ServerConnection()).start();
        new Thread(new ClientSide()).start();
    }
}
