import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sergey on 07.12.15.
 */
public class ClientSide implements Runnable {
    private static Socket socket;
    private static String fileName;
//    private static int fileId;
    private static String clientPath;
    private static String serverPath;

    //    private static Map<Integer, String> idWithName = new HashMap<>();
    private static BufferedReader bufferedReader;
    private static PrintStream printStream;


    @Override
    public void run() {
        try {
            socket = new Socket("localhost", 4444);
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }

        try {
            printStream = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            switch (Integer.parseInt(selectAction())) {
                case 1:
                    printStream.println("1");

                    System.err.print("Input the client directory (for example /home/userName/IdeaProjects/FileServerExample/ClientDirectory): ");
                    clientPath = bufferedReader.readLine();
                    printStream.println(clientPath);

                    System.err.print("Input the server directory (for example /home/userName/IdeaProjects/FileServerExample/ServerDirectory): ");
                    serverPath = bufferedReader.readLine();
                    printStream.println(serverPath);

                    System.err.print("Enter the file name on the client side: ");
                    fileName = bufferedReader.readLine();
                    printStream.println(fileName);

                    sendFile(fileName, clientPath);
                    break;
                case 2:
                    printStream.println("2");

                    System.err.print("Input the client directory (for example /home/userName/IdeaProjects/FileServerExample/ClientDirectory): ");
                    clientPath = bufferedReader.readLine();
                    printStream.println(clientPath);

                    System.err.print("Input the server directory (for example /home/userName/IdeaProjects/FileServerExample/ServerDirectory): ");
                    serverPath = bufferedReader.readLine();
                    printStream.println(serverPath);

                    System.err.print("Enter the file name on the server side: ");
                    fileName = bufferedReader.readLine();
                    printStream.println(fileName);

//                    System.err.print("Enter the file id on the server side: ");
//                    fileId = Integer.parseInt(bufferedReader.readLine());
//                    printStream.println(fileId);

                    receiveFile(fileName, clientPath);
                    break;
                case 3:
                    printStream.println("3");

                    System.err.print("Input the server directory (for example /home/userName/IdeaProjects/FileServerExample/ServerDirectory): ");
                    serverPath = bufferedReader.readLine();
                    printStream.println(serverPath);

                    System.err.print("Input the file name on the server side: ");
                    fileName = bufferedReader.readLine();
                    printStream.println(fileName);

                    searchFile(fileName, serverPath);
                    break;
                case 4:
                    printStream.println("4");

                    System.err.print("Input the server directory (for example /home/userName/IdeaProjects/FileServerExample/ServerDirectory): ");
                    serverPath = bufferedReader.readLine();
                    printStream.println(serverPath);

                    System.err.print("Enter the file name on the server side: ");
                    fileName = bufferedReader.readLine();
                    printStream.println(fileName);

                    removeFile(fileName, serverPath);
                    break;
            }

            socket.close();
        } catch (Exception e) {
            System.err.println("Your input is not valid");
            System.exit(1);
        }
    }



    public static String selectAction() throws IOException {
        System.out.println("1. Send file from the client to the server.");
        System.out.println("2. Receive file from the server to the client.");
        System.out.println("3. Search file on the server.");
        System.out.println("4. Remove file on the server.");
        System.out.println();
        System.out.print("Make a choice: ");

        String answer = bufferedReader.readLine();

        if (Integer.parseInt(answer) < 1 || Integer.parseInt(answer) > 4) {
            throw new IOException();
        }
        return answer;
    }

    public static void sendFile(String fileName, String sendPath) {
        try {

            File myFile = new File(sendPath + "/" + fileName);
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
            System.out.println();
            System.out.println("File "+ ClientSide.fileName +" sent to ServerConnection.");
        } catch (Exception e) {
            System.err.println("File does not exist!");
        }
    }

    public static void receiveFile(String fileName, String receivePath) {
        try {

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

            DataInputStream clientData = new DataInputStream(inputStream);

            fileName = clientData.readUTF();

            String newName = "received_from_server_" + fileName;
            File myFile = new File(receivePath + "/" + newName);

            OutputStream outputStream = new FileOutputStream(myFile);
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            outputStream.close();
            inputStream.close();
            System.out.println();
            System.out.println("File "+fileName+" received from ServerConnection.");
        } catch (IOException ex) {
            Logger.getLogger(ServerSide.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void searchFile(String fileName, String path) {
        try {
            File myFile = new File(path);
            File[] matchingFiles = myFile.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.contains(fileName);
                }
            });

            for (int i = 0; i < matchingFiles.length; i++) {
                System.out.println(matchingFiles[i].getName());
            }
        } catch (Exception e) {
            System.out.println("The problem is in searchFile() function.");
        }
    }

    private void removeFile(String fileName, String path) {
        try {

            File myFile = new File(path + "/" + fileName);

            if(myFile.delete()){
                System.out.println(myFile.getName() + " is deleted!");
            } else{
                System.out.println("Delete operation is failed.");
            }
        } catch (Exception e) {
            System.out.println("The problem is in removeFile() function.");
        }
    }
}
