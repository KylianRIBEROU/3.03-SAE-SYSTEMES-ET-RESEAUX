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
        
                            String idPublication = requete.split(" ", 2)[1];
                            if (Session.warningParseLongException(requete)) break;
                            Long idPublicationLong = Long.parseLong(idPublication);
                            if (this.serveur.deletePublication(idPublicationLong)){
                                System.out.println("Publication supprimée avec succès.");
                            } else {
                                System.out.println("Aucune publication n'existe avec cet ID");
                            }
                            break;
                        case "/remove":
                            if (Session.warningContenuManquant(requete)) break;
                            String nomUtilisateur = requete.split(" ", 2)[1];
                            if (this.serveur.deleteUtilisateur(nomUtilisateur)) {
                                this.serveur.fermerSessionAvecNomUtilisateur(nomUtilisateur);
                                System.out.println("Utilisateur supprimé avec succès.");
                            } else {
                                System.out.println("Aucun utilisateur n'existe avec ce nom");
                            }; //TODO: terminer session des utilisateurs actuellement connectés qui viennent d'être supprimés
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
