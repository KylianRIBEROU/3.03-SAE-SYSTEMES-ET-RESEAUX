package reseausocial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(args);
            System.out.println("Usage: java Client <nom_serveur> <nom_utilisateur>");
            System.out.println("Exemple: java Client localhost SNIVEAU");
            System.exit(1);
        }

        String host = args[0];
        String user = args[1];

        client(host, Constantes.PORT, user);
    }

   // https://stackoverflow.com/questions/41409670/is-socket-close-considered-a-clean-way-to-end-the-connection
   // bonne source de documentation ça 

    private static void client(String host, int port, String user) {

         try {
            // tunnel entre serveur et client
            Socket socket = new Socket(host, port);
            // pour lire ce que serveur envoie
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // pour envoyer a serveur
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            output.println(user);
            System.out.println(input.readLine());

            BufferedReader inputClient = new BufferedReader(new InputStreamReader(System.in)); // jsp si faut le mettre dans le while ou pas
            String commande; 
            while (true){
                System.out.print("> ");
                commande = inputClient.readLine();
                if (commande.equals("quit")  || commande.equals("exit") || commande.equals("quitter")) {
                    break;
                }
                System.out.println(("la requête du client :" + commande));
                System.out.println("Envoi de la requête au serveur");


                output.println(commande);
                String reponseServ;
                while ((reponseServ = input.readLine()) != null) {
                System.out.println(reponseServ);
                if (!input.ready()) {
                    break;
                   } 
                }
            }

            // si serv ferme la connexion ou qu'on "quit". Exceptions a gérer plus tard 
            input.close();
            output.close();
            socket.close();
            System.out.println("tout est fermé");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
