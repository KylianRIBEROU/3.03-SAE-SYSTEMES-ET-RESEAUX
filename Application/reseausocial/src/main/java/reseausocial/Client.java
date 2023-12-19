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
           
            output.println(user);
            if (input.readLine().equals("notregistered")){ // si l'utilisateur n'existe pas
                System.out.println(input.readLine());
                System.out.println(input.readLine());
                String reponse = inputClient.readLine();
                if (reponse.equals("y")) {
                    output.println("y");
                    System.out.println(input.readLine());
                } else {
                    output.println("n");
                    System.out.println(input.readLine());
                    System.exit(1);
                }
            }
  
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
