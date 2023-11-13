//Cantoni Gioele 5BIN
package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    static HashMap <Integer, String> candidati = new HashMap<>();
    static final int portaServer = 4500;
    static final int N = 4; //numero dipendenti di ABC Costruzioni
    static int c = 0;

    static CountDownLatch cd = new CountDownLatch(N);
    static CountDownLatch cd2 = new CountDownLatch(N);

    static boolean controllo = false;

    static ExecutorService esecutore = Executors.newFixedThreadPool(N);

    static Lock lc = new ReentrantLock();

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(portaServer)) {

            while (true){
                Socket temp;

                try{
                    temp = server.accept();

                    esecutore.execute(() -> {
                        Socket client = temp;
                        try(client){

                            String rem = client.getRemoteSocketAddress().toString();
                            Thread t = Thread.currentThread();
                            System.out.format("Thread: %d, client (remoto): %s, ", t.getId(), rem);


                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
                            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"), true);

                            String r = in.readLine();
                            Scanner s = new Scanner(r);

                            String nome = s.next();
                            String cognome = s.next();
                            String consenso = s.next();

                            System.out.format("nome: %s, cognome: %s, consenso: %s, n client collegati: %d.%n", nome,cognome,consenso, N - cd.getCount() + 1);

                            if(consenso.equalsIgnoreCase("S")){
                                lc.lock();
                                try{
                                    candidati.put(c, cognome + " " + nome);
                                    c++;
                                }finally {
                                    lc.unlock();
                                }

                            }

                            cd.countDown();

                            try{
                                cd.await();

                                out.println(candidati.toString());

                                lc.lock();

                                try {
                                    if(controllo==false)
                                        System.out.println(candidati.toString());
                                } finally {
                                    lc.unlock();
                                    controllo = true;
                                }


                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            int voti[] = new int[candidati.size()];

                            String preferenza = in.readLine();
                            Scanner s2 = new Scanner(preferenza);
                            int voto = s2.nextInt();

                            lc.lock();

                            try{
                                voti[voto]++;
                            }finally {
                                lc.unlock();
                            }

                            cd2.countDown();

                            int max=0;

                            try{

                                cd2.await();

                                lc.lock();

                                try {
                                    for (int i = 0; i < voti.length; i++)
                                        if(voti[i]>max)
                                            max = i;
                                } finally {
                                    lc.unlock();
                                }

                                out.println(candidati.get(max));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            controllo = false;
                            lc.lock();

                            try {
                                if(controllo==false){
                                    for (int i = 0; i < candidati.size(); i++)
                                        System.out.format("Candidato: %s - Voti: %d%n", candidati.get(i), voti[i]);

                                    System.out.format("%n%nIl nuovo rappresentante dei lavoratori è: %s%n", candidati.get(max));
                                }
                            } finally {
                                lc.unlock();
                                controllo = true;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}