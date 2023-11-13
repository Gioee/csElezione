//Cantoni Gioele 5BIN
package com.company;

        import java.io.*;
        import java.net.Socket;
        import java.net.UnknownHostException;
        import java.util.Scanner;

public class Main {

    final static String nomeServer = "localhost";
    final static int portaServer = 4500;

    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);

        System.out.format("ELEZIONE DEL RAPPRESENTANTE DEI LAVORATORI%n----------------------------------------------------------------------%nCognome: ");
        String cognome = s.next();
        System.out.print("Nome: ");
        String nome = s.next();

        String consenso = "";

        do{
            System.out.print("Desidera candidarsi al ruolo di rappresentante (S/N)? ");
            consenso = s.next();

        }while (consenso.equalsIgnoreCase("S")==false && consenso.equalsIgnoreCase("N")==false);

        try (Socket sck = new Socket(nomeServer, portaServer)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(sck.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(sck.getOutputStream(), "UTF-8"), true);

            out.println(String.format("%s %s %s", nome, cognome, consenso));
            System.out.format("%nDati inviati. In attesa di ricevere la lista dei candidati...");

            String risposta = in.readLine();
            risposta = risposta.substring(1, risposta.length()-1);
            risposta = risposta.replace("=", ". ");

            int preferenza;

            System.out.format("%n%nLISTA DEI CANDIDATI%n======================================");

            do{

                for (int i = 0; i < risposta.split(", ").length; i++)
                    System.out.format("%n%s", risposta.split(", ")[i].toUpperCase());

                System.out.format("%n%nEsprimere la propria preferenza: ");
                preferenza = s.nextInt();
            }while (!risposta.contains(Integer.toString(preferenza)));

            out.println(preferenza);

            System.out.format("%nPreferenza inviata al sistema centrale.%nIn attesa di conoscere il nome del rappresentante dei lavoratori...%n");

            String rappresentante = in.readLine();

            System.out.format("%nIl nuovo rappresentante dei lavoratori è: " + rappresentante.toUpperCase());

            System.out.format("%n%nApplicazione terminata.");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
