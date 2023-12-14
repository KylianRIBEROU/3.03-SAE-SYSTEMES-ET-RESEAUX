package reseausocial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Session implements Runnable {

    private Serveur serveur;
    private ServerSocket serveurSocket;
    private Socket clientSocket;

    public Session(Serveur serveur, ServerSocket serveurSocket ,Socket clientSocket) {
        this.serveur = serveur;
        this.serveurSocket = serveurSocket;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {

            // le traitement de la requete du client c'est ici je pense




            // Initialisation des flux de données
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Le input.readline(): ( ce qu'on recoit du client)");
            System.out.println(input.readLine());
            System.out.println("b");
            System.out.println("le client peut pas renvoyer commande tant que serv a pas rép a sa requête");
            System.out.println("soit mettre systeme de timeout , soit threadiser l'input/output du client");
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);



            while (true ){
                System.out.println("c");
                System.out.println(input.readLine()); // bloquant, le a se print pas psk y'a le readline de au dessus qui a deja pris la premiere entree du client
                System.out.println("a");
            

            // Logique de communication
            // String clientMessage;
            // while ((clientMessage = input.readLine()) != null) {
            //     // Traitement de la requête du client
            //     if (clientMessage.equals("quit")) {
            //         break;
            //     }
            //     String response = processClientRequest(clientMessage);
            //     System.out.println("la réponse du serv :");
            //     System.out.println(response);
            //     // Envoi de la réponse au client
            //     output.println(response);
            // }
        }


            
                // String requete = new String(requetePacket.getData(), 0, requetePacket.getLength());

                // String réponse = "Pas de requête valide spécifiée";
                // if (requete.toLowerCase().equals("date")) {
                //     réponse = new Date().toString();
                // } else if (requete.equals("user")) {
                //     réponse = "Le host du serveur est : " + System.getenv("USER");
                // }

                // System.out.println(requete);
                
                // byte[] réponseData = réponse.getBytes();
                // DatagramPacket réponsePacket = new DatagramPacket(réponseData, réponseData.length, requetePacket.getAddress(), requetePacket.getPort());

                // socket.send(réponsePacket);
            // // Fermeture des flux et de la connexion
            // input.close();
            // output.close();
            // clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processClientRequest(String request) {
        // Logique de traitement de la requête du client
        // ...

        // Exemple : renvoyer la requête en majuscules
        return request.toUpperCase();
    }
}
