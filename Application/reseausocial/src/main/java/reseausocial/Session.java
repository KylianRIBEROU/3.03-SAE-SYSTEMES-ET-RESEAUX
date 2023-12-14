package reseausocial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Session implements Runnable {

    private Socket clientSocket;

    public Session(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            // Initialisation des flux de données
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

            // Logique de communication
            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                // Traitement de la requête du client
                String response = processClientRequest(clientMessage);

                // Envoi de la réponse au client
                output.println(response);
            }

            // Fermeture des flux et de la connexion
            input.close();
            output.close();
            clientSocket.close();
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
