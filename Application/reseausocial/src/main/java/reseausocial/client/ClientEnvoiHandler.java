package reseausocial.client;

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
                    client.getOutput().println(commande); 
                    Thread.sleep(300); // pour éviter flood 
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Problème d'envoi de message au serveur");
            }
        }
    
}
