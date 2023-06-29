import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String clientName;

    private Socket socket;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Client(String clientName, Socket socket) {
        this.clientName = clientName;
        this.socket = socket;
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Введіть повідомлення до сервера: ");
            String message = scanner.nextLine();
            writer.println(message);
            System.out.println("Повідомлення надіслано на сервер: " + message);

            String name = scanner.nextLine();
            Client client = new Client(name,socket);
            System.out.print("Enter your name: ");
            String clientName = scanner.nextLine();
            client.setClientName(clientName);

            Thread responseThread = new Thread(() -> {
                try {
                    String response = reader.readLine();
                    System.out.println("Відповідь від сервера: " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            responseThread.start();

            while (true) {
                String userInput = scanner.nextLine();
                writer.println(userInput);

                if ("close".equalsIgnoreCase(userInput)) {
                    break;
                }
            }

            responseThread.join();

            reader.close();
            writer.close();
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
