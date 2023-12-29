package reseausocial;

import java.io.IOException;

public class ClientReceptionHandler extends Thread{

    private Client client;

    public ClientReceptionHandler(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
        String serverMessage;
        while ((serverMessage = client.getInput().readLine()) != null) {
            System.out.println(serverMessage);
        }
        } catch (IOException e) {
            System.out.println("Problème de réception de message du serveur");
        }
    }
}
