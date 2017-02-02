package ru.geekbrains.java2.dz.dz6.gubenkoDM.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.TreeMap;

public class ClientHandler implements Runnable {
    private Socket s;
    private PrintWriter out;
    private Scanner in;
    //private static int CLIENTS_COUNT = 0;
    private String name;
    boolean flag;
    Scanner sc=new Scanner(System.in);

    public ClientHandler(Socket s) {
        try {
            this.s = s;
            in = new Scanner(s.getInputStream());
            out = new PrintWriter(s.getOutputStream());
            flag=true;
            //CLIENTS_COUNT++;
            //name = "Client #" + CLIENTS_COUNT;
        } catch (IOException e) {
        }
    }

    /**
     *  Отправка сообщения от клиента
     *  в выходной поток
     */

    public void sendMsg(String msg) {
        out.println(msg);
        out.flush();
    }

    /**
     *  Получение сообщения клиентом
     *  из входной потока
     */
    public boolean reciveMsg() {
        String w = in.nextLine();
        if (w.equalsIgnoreCase("end session")){
            return true;
        }
        System.out.println(w);
        return false;
    }



    @Override
    public void run() {
        new Thread(() -> {
            try {
                //System.out.println("1");
                while (flag) {
                    // System.out.println("2");
                    if (sc.hasNext()){
                        sendMsg(sc.nextLine());
                    }
                }
            } catch (Exception e) {
                System.out.println("Возникли проблемы при отсылке сообщения серверу!");
            }
        }).start();

        //запустим отдельный поток для мониторинга пришло ли сообщение клиенту
        //реализация на lambda
        new Thread(() -> {
            try {
                while (true) {
                    if (in.hasNext()) {
                        if (reciveMsg()){
                            //если мы тут то заканчиваем работу
                            flag=false;
                            out.println("end session");
                            out.flush();
                            out.close();
                            in.close();
                            //sock.close();
                            try {
                                System.out.println("Client disconnected");
                                s.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Возникли проблемы при получении сообщения клиентом!");
            }
        }).start();

        while (flag){};
    }
}
