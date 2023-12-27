package reseausocial;

public class ClientEnvoiHandler extends Thread {
    
        private Client client;
    
        public ClientEnvoiHandler(Client client) {
            this.client = client;

        }
    
        @Override
        public void run() {
            try {
                String commande; 
                while (true){
                    System.out.print("> ");
                    commande = client.getInputClient().readLine();
                    if (commande.equals("quit")  || commande.equals("exit") || commande.equals("quitter")) {
                        break;
                    }
                    System.out.println(("la requête du client : " + commande));
                    System.out.println("Envoi de la requête au serveur...");
                    client.getOutput().println(commande); //TODO: jsp ou mais faut gerer l'erreur si une commande est envoyee sans contenu
                    // TODO:par exemple si le client doit faire "/show uuid" et qu'il ne met pas d'uuid ( genre juste  "/show" ) ca plante du  cote serveur psk OutOtBoundsException
                    Thread.sleep(400); // pour éviter flood 
                }

            } catch (Exception e) {
                System.out.println("Problème d'envoi de message au serveur");
            }
        }
    
}
