package reseausocial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;

    private BufferedReader input;
    private PrintWriter output;

    private BufferedReader inputClient;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(args);
            System.out.println("Usage: java Client <nom_serveur> <nom_utilisateur>");
            System.out.println("Exemple: java Client localhost SNIVEAU");
            System.exit(1);
        }

        String host = args[0];
        String user = args[1];

        Client client = new Client(host, Constantes.PORT);

        client.client(user);
    }

   // https://stackoverflow.com/questions/41409670/is-socket-close-considered-a-clean-way-to-end-the-connection
   // bonne source de documentation ça 

   public Client(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
            this.inputClient = new BufferedReader(new InputStreamReader(System.in));

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void client(String user) {

         try {
           
           requeteConnexion(user);

            String commande; 
            while (true){
                System.out.print("> ");
                commande = inputClient.readLine();
                if (commande.equals("quit")  || commande.equals("exit") || commande.equals("quitter")) {
                    break;
                }
                System.out.println(("la requête du client : " + commande));
                System.out.println("Envoi de la requête au serveur...");


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

    private void requeteConnexion(String user) throws IOException {
         // Envoi du nom d'utilisateur au serveur
            output.println(user);

            // Réception de la réponse du serveur
            String serverResponse = input.readLine();

            // Si le serveur propose la création d'un compte
            while (serverResponse.contains("notregistered")) {
                System.out.println(input.readLine());
                System.out.println(input.readLine());
                System.out.print("> ");
                String reponse = inputClient.readLine();

                output.println(reponse);

                // Si le client souhaite créer un compte avec ce nom
                if (!reponse.equalsIgnoreCase("y") && !reponse.equalsIgnoreCase("yes")) {
                    
                    System.out.println(input.readLine());
                    System.out.print("> ");
                    user = inputClient.readLine();
                    output.println(user);
                }
                else if (reponse.equalsIgnoreCase("y") || reponse.equalsIgnoreCase("yes")){
                    serverResponse = input.readLine();
                    break;
                }
                else {
                    System.out.println("Choix invalide");
                    System.exit(1);
                }

                // Réception de la réponse du serveur
                serverResponse = input.readLine();
            }

            System.out.println(serverResponse);
    }
}
