package reseausocial;

import java.io.BufferedReader;

public class ServeurRequeteHandler extends Thread {
    
        private Serveur serveur;
        private BufferedReader inputServeur;
    
        public ServeurRequeteHandler(Serveur serveur, BufferedReader inputServeur) {
            this.serveur = serveur;
            this.inputServeur = inputServeur;
        }
    
        @Override
        public void run() {
            try {
                String requete;
                while (true) {
                    requete = this.inputServeur.readLine();
                    switch (requete.split(" ", 2)[0]) {
                        case "/delete":
                            if (Session.warningContenuManquant(requete)) break;
                            String uuidMessage = requete.split(" ", 1)[1];
                            this.serveur.deleteMessage(uuidMessage);
                            break;
                        case "/remove":
                            if (Session.warningContenuManquant(requete)) break;
                            String nomUtilisateur = requete.split(" ", 1)[1];
                            this.serveur.deleteUtilisateur(nomUtilisateur);
                            break;
                        case "/help":
                            this.serveur.afficheCommandesServeur();
                            break;
                        default:
                            System.out.println("Commande non reconnue");
                            break;
                    }
                }
    
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
    
}
