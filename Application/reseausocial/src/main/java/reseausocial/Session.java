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

            // penser a init utilisateur ici


            // le traitement de la requete du client c'est ici je pense

            // Initialisation des flux de données
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

            System.out.println("le client peut pas renvoyer commande tant que serv a pas rép a sa requête");
            System.out.println("soit mettre systeme de timeout , soit threadiser l'input/output du client");
        
            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                switch (clientMessage) {

                    case "help":
                        System.out.println("frérot stp foncion");
                        afficherMenuAide(output); // marche pas wtf
                        break;

                    default:
                        Thread.sleep(500); 
                        output.println(clientMessage);
                        output.println("Pas de requête valide spécifiée");
                }
            }

            input.close();
            output.close();
            clientSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String processClientRequest(String request) {
        // Logique de traitement de la requête du client
        // ...

        // Exemple : renvoyer la requête en majuscules
        return request.toUpperCase();
    }


    private void afficherMenuAide(PrintWriter output) {
        output.println("Liste des commandes disponibles :");
        output.println("date : affiche la date du serveur");
        output.println("user : affiche le nom de l'utilisateur du serveur"); // un truc dans le genre a modifier
        output.println("quit : ferme la connexion");
    }
}
