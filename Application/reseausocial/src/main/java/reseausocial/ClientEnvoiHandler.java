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
                    client.getOutput().println(commande);
                    Thread.sleep(400); // pour éviter flood 
                }

            } catch (Exception e) {
                System.out.println("Problème d'envoi de message au serveur");
            }
        }
    
}
