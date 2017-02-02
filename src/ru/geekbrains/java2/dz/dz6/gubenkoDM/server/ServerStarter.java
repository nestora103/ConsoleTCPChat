package ru.geekbrains.java2.dz.dz6.gubenkoDM.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerStarter {
    public static void main(String[] args) {
        ServerSocket server = null;
        Socket s = null;
        try {
            server = new ServerSocket(8189);
            System.out.println("Server created. Waiting for client...");
            while(true) {
                s = server.accept();
                System.out.println("Client connected");
                Thread th=new Thread(new ClientHandler(s));
                th.start();
                try {
                    th.join();
                } catch (InterruptedException e) {
                    System.out.println("Ошибочка вышла при ожидании завершения работы сервера!");
                    e.printStackTrace();
                }
                //если работаем с одним клиентом
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                s.close();
                server.close();
                System.out.println("Server closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
