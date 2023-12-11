package reseausocial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Date;

public class Serveur {
    private static final int BUFFSIZE = 1024;

    public static void main(String[] args) {
        server(5555);
    }

    private static void server(int port) {
        try {
            DatagramSocket socket = new DatagramSocket(port);

            while (true) {
                byte[] requeteData = new byte[BUFFSIZE];
                DatagramPacket requetePacket = new DatagramPacket(requeteData, requeteData.length);

                socket.receive(requetePacket);
                String requete = new String(requetePacket.getData(), 0, requetePacket.getLength());

                String réponse = "Pas de requête valide spécifiée";
                if (requete.toLowerCase().equals("date")) {
                    réponse = new Date().toString();
                } else if (requete.equals("user")) {
                    réponse = "Le host du serveur est : " + System.getenv("USER");
                }

                System.out.println(requete);
                
                byte[] réponseData = réponse.getBytes();
                DatagramPacket réponsePacket = new DatagramPacket(réponseData, réponseData.length, requetePacket.getAddress(), requetePacket.getPort());

                socket.send(réponsePacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
