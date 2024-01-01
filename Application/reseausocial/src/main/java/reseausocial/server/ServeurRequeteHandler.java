package reseausocial.server;

import java.io.BufferedReader;

import reseausocial.Serveur;

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
                            System.out.println(requete);
                            System.out.println(requete.split(" ", 2)[0]);
                            System.out.println(requete.split(" ", 2)[1]);
                            String uuidMessage = requete.split(" ", 2)[1];
                            this.serveur.deleteMessage(uuidMessage);
                            break;
                        case "/remove":
                            if (Session.warningContenuManquant(requete)) break;
                            String nomUtilisateur = requete.split(" ", 2)[1];
                            this.serveur.deleteUtilisateur(nomUtilisateur);
                            break;
                        case "/show-all-users":
                            this.serveur.afficheUtilisateurs();
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
