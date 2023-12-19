package reseausocial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Setter;
import reseausocial.Exception.CreationCompteRefuseeException;
import lombok.Getter;


@Getter
@Setter
public class Session implements Runnable {

    private Serveur serveur;
    private Socket clientSocket;
    private Utilisateur utilisateur;

    private BufferedReader input;
    private PrintWriter output;

    public Session(Serveur serveur, Socket clientSocket) {
        this.serveur = serveur;
        this.clientSocket = clientSocket;
        this.utilisateur = null;

        try {
            this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.output = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
    
        try {
            // recevoir nom utilisateur rentre par client
            String inputUsername = input.readLine();

            this.utilisateur = checkUtilisateurExiste(inputUsername);
            if (this.utilisateur == null) {
                output.println("notregistered");
                output.println("L'utilisateur '" + inputUsername + "' n'existe pas");
                output.println("Voulez vous créér un compte avec ce nom  ? (y/n)");
                String reponse = input.readLine();
                if (reponse.equals("y") || reponse.equals("Y") || reponse.equals("yes") || reponse.equals("Yes")) {
                    this.utilisateur = creerUtilisateur(inputUsername);
                } else {
                    output.println("Veuillez vous connecter avec un autre nom d'utilisateur");
                    throw new CreationCompteRefuseeException();
                }
            }
            else {
            System.out.println("Utilisateur " + this.utilisateur.getNom() + " connecté");
            }
            output.println("Bienvenue " + this.utilisateur.getNom() + " !");

            // le traitement de la requete du client c'est ici je pense

        
            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                // clientMessage.split(" ", 2) // va split seulement sur le premier espace
                // [0] pour 1ere partie, [1 pour le reste]
                switch (clientMessage.split(" ", 2)[0]) {
                    case "/post":
                        String contenu = clientMessage.split(" ", 2)[1];
                        Message msgUtil = creerMessage(this.utilisateur, contenu); 
                        output.println("Message posté : " + msgUtil.toString());
                        partagerMessage(this.utilisateur, msgUtil); 
                        break;

                    case "/like":
                        String uuid = clientMessage.split(" ", 2)[1];
                        serveur.likeMessage(uuid);
                        break;

                    case "/delete":
                        String uuidDelete = clientMessage.split(" ", 2)[1];
                        this.utilisateur.supprimeMessage(uuidDelete);
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
                            output.println("L'utilisateur à unfollow '" + nomUtilisateurUnfollow + "' n'existe pas");
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
        catch (CreationCompteRefuseeException e) {
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
        utilisateur.ajouteMessage(message); //TODO cr"r"r 
        return message;
    }

    private void partagerMessage(Utilisateur utilisateur, Message message){
        this.serveur.redirigerMessage(utilisateur, message);
    }

    private void afficherMenuAide(PrintWriter output) {
        output.println("Liste des commandes disponibles :");
        output.println("date : affiche la date du serveur");
        output.println("user : affiche le nom de l'utilisateur du serveur"); // un truc dans le genre a modifier
        output.println("quit : ferme la connexion");
    }

    public void recevoirMessage(Message message) {
        output.println(message.toString());
    }
}
