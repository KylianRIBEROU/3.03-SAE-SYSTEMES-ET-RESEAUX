package reseausocial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

            //TODO: envoyer nom utilisateur au serveur pour qu'il l'enregistre avant de lui proposer de rentrer des commandes

            BufferedReader inputClient = new BufferedReader(new InputStreamReader(System.in)); // jsp si faut le mettre dans le while ou pas
            String commande; 
            while (true){
                System.out.print("> ");
                commande = inputClient.readLine();
                if (commande.equals("quit")) {
                    break;
                }
                System.out.println(("la requête du client :" + commande));
                System.out.println("Envoi de la requête au serveur");
                output.println(commande);
                String reponseServ = input.readLine();
                System.out.println("la réponse du serv :");
                System.out.println(reponseServ);

            }

            // si serv ferme la connexion ou qu'on "quit". Exceptions a gérer plus tard 
            input.close();
            output.close();
            socket.close();
            System.out.println("tout est fermé");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // try {
        //     DatagramSocket socket = new DatagramSocket();
        //     InetAddress address = InetAddress.getByName(host);

        //     byte[] requestData = request.getBytes();
        //     DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, address, port);

        //     socket.send(requestPacket);

        //     byte[] responseData = new byte[Constantes.BUFFSIZE];
        //     DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);

        //     socket.receive(responsePacket);
        //     String response = new String(responsePacket.getData(), 0, responsePacket.getLength());

        //     System.out.println(response);

        //     socket.close();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }
}
