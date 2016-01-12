
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sergey on 07.12.15.
 */
public class ServerSide implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
//    private Map<Integer, String> idWithName = new HashMap<>();

    public ServerSide(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            String clientSelection;
            while ((clientSelection = bufferedReader.readLine()) != null) {
                switch (clientSelection) {
                    case "1":
                        String clientPath1 = bufferedReader.readLine();
                        String serverPath1 = bufferedReader.readLine();
                        String name1 = bufferedReader.readLine();
                        receiveFile(name1, serverPath1);
                        break;
                    case "2":
                        String clientPath2 = bufferedReader.readLine();
                        String serverPath2 = bufferedReader.readLine();
                        String name2 = bufferedReader.readLine();
//                        int id2 = Integer.parseInt(bufferedReader.readLine());
                        sendFile(name2, serverPath2);
                        break;
                    case "3":
                        String path3 = bufferedReader.readLine();
                        String name3 = bufferedReader.readLine();
                        break;
                    case "4":
                        String path4 = bufferedReader.readLine();
                        String name4 = bufferedReader.readLine();
                        break;
                    default:
                        System.out.println("Incorrect command received.");
                        break;
                }
                bufferedReader.close();
                break;
            }

        } catch (IOException ex) {
            Logger.getLogger(ServerSide.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendFile(String fileName, String clientPath2) {
        try {
//            String[] partsOfName = fileName.split("_");
//            int id = Integer.parseInt(partsOfName[partsOfName.length - 1]);

            File myFile = new File(clientPath2 + "/" + fileName);
            byte[] byteArray = new byte[(int) myFile.length()];

            FileInputStream fileInputStream = new FileInputStream(myFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            dataInputStream.readFully(byteArray, 0, byteArray.length);

            OutputStream outputStream = socket.getOutputStream();

            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(myFile.getName());
            dataOutputStream.writeLong(byteArray.length);
            dataOutputStream.write(byteArray, 0, byteArray.length);
            dataOutputStream.flush();
            System.out.println("File " + fileName + " sent to client.");
        } catch (Exception e) {
            System.err.println("File does not exist!");
        }
    }

    public void receiveFile(String name1, String path1) {
        try {
            int bytesRead;
            int id = (int)(Math.random()*100);


            DataInputStream clientData = new DataInputStream(socket.getInputStream());

            String fileName = clientData.readUTF();

//            String newName = "received_from_client_" + name1;
            String nameWithId = "received_from_client_" +  name1 + "_" + Integer.toString(id);

            File myFile = new File(path1 + "/" + nameWithId);

            OutputStream output = new FileOutputStream(myFile);
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            clientData.close();

            System.out.println("File " + fileName + " received from client.");
        } catch (IOException ex) {
            System.err.println("ClientSide error. ServerSide closed.");
        }
    }
}
