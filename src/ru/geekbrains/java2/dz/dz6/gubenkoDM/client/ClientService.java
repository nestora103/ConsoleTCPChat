package ru.geekbrains.java2.dz.dz6.gubenkoDM.client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientService{

    JTextField jtf;
    JTextArea jta;

    final String SERVER_ADDR = "localhost";
    final int SERVER_PORT = 8189;
    Socket sock;
    Scanner in;
    PrintWriter out;
    Scanner sc=new Scanner(System.in);
    boolean flag;

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

    public ClientService() {
        try {
            flag=true;
            sock = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new Scanner(sock.getInputStream());
            out = new PrintWriter(sock.getOutputStream());
        } catch (IOException e) {
            System.out.println("Проблемы с доступом к серверу! Проверьте настройки и повторите позже!");
            e.printStackTrace();
        }

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
                            sock.close();
                            System.out.println("Disconect Client!");
                            break;
                        }
                   }
                }
            } catch (Exception e) {
                System.out.println("Возникли проблемы при получении сообщения клиентом!");
            }
       }).start();
    }
}
