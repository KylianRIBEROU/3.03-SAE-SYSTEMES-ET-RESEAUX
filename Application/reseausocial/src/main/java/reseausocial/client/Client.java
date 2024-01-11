package reseausocial.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import lombok.Getter;
import lombok.Setter;
import reseausocial.resources.Constantes;

import java.io.Console;


@Getter
@Setter
public class Client {
    private Socket socket;

    private BufferedReader input;
    private PrintWriter output;

    private BufferedReader inputClient;
    private Console inputMotDePasseClient;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(args);
            System.out.println("Usage: java Client <nom_serveur>");
            System.out.println("Exemple: java Client localhost");;
            System.exit(1);
        }

        String host = args[0];

        Client client = new Client(host, Constantes.PORT);
        client.client();
    }

   // https://stackoverflow.com/questions/41409670/is-socket-close-considered-a-clean-way-to-end-the-connection
   // bonne source de documentation ça 

   public Client(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
            this.inputClient = new BufferedReader(new InputStreamReader(System.in));

            this.inputMotDePasseClient = System.console();

        }
        catch (IOException  e) {
            System.out.println("Le serveur est hors ligne ou n'existe pas");
            System.exit(1);
        }

    }

    private void client() {

         try {
           
            ClientReceptionHandler receptionMsgServHandler = new ClientReceptionHandler(this);
            receptionMsgServHandler.start(); // réception message serveur threadisé
            
            ClientEnvoiHandler envoiMsgServHandler = new ClientEnvoiHandler(this);
            envoiMsgServHandler.start(); // envoi message serveur threadis
            

//             //TODO:  apres s'etre connecté ou créé compte Il voit ensuite la liste des différents messages postés par les utilisateurs
// auxquels il est abonné, dans l’ordre chronologique (une limite du nombre de messages affichés
// devra être implantée).

            receptionMsgServHandler.join(); // dans la logique à implémenter, si serveur down, on ferme le client
            // envoiMsgServHandler.join();
            fermetureClient();
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void fermetureClient() throws IOException{
            input.close();
            output.close();
            socket.close();
            System.out.println("Fermeture du client");
            System.exit(1);
    }
}
