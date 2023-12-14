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

    public static void main(String[] args) {
        Serveur serveur = new Serveur();
        serveur.lancerServeur(5555);
    }

    // Le serveur sera Threadisé pour répondre a plusieurs clients en même temps

    private void lancerServeur(int port) {
        try {
            ServerSocket serveurSocket = new ServerSocket(port);

            while (true) {


                Socket clientSocket = serveurSocket.accept(); // commande bloquante ? vérifier si plusieurs  clients peuvent se connecter en même temps
                System.out.println("test que l'attente d'un client est bloquant"); 

                // TODO: condition selon le nombre de processeurs availables
                // int nbProcesseurs = Runtime.getRuntime().availableProcessors();

                Session clientSession = new Session(this, serveurSocket, clientSocket);
                Thread clientThread = new Thread(clientSession);
                clientThread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
