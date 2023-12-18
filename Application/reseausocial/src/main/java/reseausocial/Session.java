package reseausocial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.UUID;

public class Session implements Runnable {

    private Serveur serveur;
    private ServerSocket serveurSocket;
    private Socket clientSocket;
    private Utilisateur utilisateur;

    public Session(Serveur serveur, ServerSocket serveurSocket ,Socket clientSocket) {
        this.serveur = serveur;
        this.serveurSocket = serveurSocket;
        this.clientSocket = clientSocket;
        this.utilisateur = null;
    }

    @Override
    public void run() {
    
        try {

            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

             String inputUsername = input.readLine();
            this.utilisateur = checkUtilisateurExiste(inputUsername);
            if (this.utilisateur == null) {
                this.utilisateur = creerUtilisateur(inputUsername);
            }
            System.out.println("Utilisateur " + this.utilisateur.getNom() + " connecté");
            output.println("Bienvenue " + this.utilisateur.getNom() + " !");

            // le traitement de la requete du client c'est ici je pense

        
            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                // clientMessage.split(" ", 2) // va split seulement sur le premier espace
                // [0] pour 1ere partie, [1 pour le reste]
                switch (clientMessage.split(" ", 2)[0]) {
                    case "/post":
                        String contenu = clientMessage.split(" ", 2)[1];
                        Message msgUtil = creerMessage(this.utilisateur, contenu); //TODO
                        break;
                    case "/follow":
                        String nomUtilisateur = clientMessage.split(" ", 2)[1];
                        Utilisateur utilisateurSuivi = checkUtilisateurExiste(nomUtilisateur);
                        if (utilisateurSuivi == null) {
                            output.println("L'utilisateur " + nomUtilisateur + " n'existe pas");
                        } else {
                            this.utilisateur.ajouteAbonnement(utilisateurSuivi);
                            output.println("Vous suivez maintenant " + nomUtilisateur);
                        }
                        break;
                    
                    case "/unfollow":
                        String nomUtilisateurUnfollow = clientMessage.split(" ", 2)[1];
                        Utilisateur utilisateurUnfollow = checkUtilisateurExiste(nomUtilisateurUnfollow);
                        if (utilisateurUnfollow == null) {
                            output.println("L'utilisateur a unfollow '" + nomUtilisateurUnfollow + "' n'existe pas");
                        } else {
                            this.utilisateur.supprimeAbonnement(utilisateurUnfollow);
                            output.println("Vous ne suivez plus " + nomUtilisateurUnfollow);
                        }
                        break;
                    case "/help":
                        afficherMenuAide(output);
                        break;

                    default:
                        Thread.sleep(500); 
                        output.println(clientMessage);
                        output.println("Pas de requête valide spécifiée");
                }
            }

            input.close();
            output.close();
            clientSocket.close();
        }
        catch (SocketException e){
            System.out.println("Session de "+ this.utilisateur.getNom() + " interrompue");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Utilisateur checkUtilisateurExiste(String nomUtilisateur) {
        for (Utilisateur utilisateur : serveur.getUtilisateurs()) {
            if (utilisateur.getNom().equals(nomUtilisateur)) {
                return utilisateur;
            }
        }
        return null;
    }

    private Utilisateur creerUtilisateur(String nomUtilisateur) {
        Utilisateur utilisateur = new Utilisateur(nomUtilisateur);
        serveur.ajouteUtilisateur(utilisateur);
        return utilisateur;
    }

    private Message creerMessage(Utilisateur utilisateur, String contenu){
        Message message = Message.builder()
            .uuid(UUID.randomUUID().toString())
            .auteur(utilisateur)
            .contenu(contenu)
            .date(LocalDateTime.now())
            .nbLikes(0)
            .build();
        utilisateur.ajouteMessage(message); //TODO
        return message;
    }

    private void partagerMessage(Utilisateur utilisateur, Message message){
        //TODO
    }

    private void afficherMenuAide(PrintWriter output) {
        output.println("Liste des commandes disponibles :");
        output.println("date : affiche la date du serveur");
        output.println("user : affiche le nom de l'utilisateur du serveur"); // un truc dans le genre a modifier
        output.println("quit : ferme la connexion");
    }
}
