import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<Client> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Сервер запущено. Очікування підключень...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новий клієнт підключений: " + clientSocket.getInetAddress());

                String name = null;
                Client client = new Client(name,clientSocket);
                clients.add(client);

                ClientHandler clientHandler = new ClientHandler(client);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Client client;

        public ClientHandler(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
                PrintWriter writer = new PrintWriter(client.getSocket().getOutputStream(), true);

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Повідомлення від клієнта " + client.getSocket().getInetAddress() + ": " + message);

                    writer.println("Отримано повідомлення: " + message);
                }

                clients.remove(client);
                System.out.println("Клієнт " + client.getSocket().getInetAddress() + " відключений.");

                reader.close();
                writer.close();
                client.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
