package reseausocial.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Setter;
import reseausocial.Serveur;
import reseausocial.models.Message;
import reseausocial.models.Utilisateur;
import reseausocial.resources.Constantes;
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
            String username = traiterRequeteConnexion();
        
            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                // clientMessage.split(" ", 2) // va split seulement sur le premier espace
                // [0] pour 1ere partie, [1 pour le reste]
                switch (clientMessage.split(" ", 2)[0]) {
                    case "/post":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String contenu = clientMessage.split(" ", 2)[1];
                        Message msgUtil = creerMessage(this.utilisateur, contenu); 
                        output.println("Message posté : " + msgUtil.toString());
                        partagerMessage(this.utilisateur, msgUtil); 
                        break;
                    
                    case "/show-my-posts":
                        output.println("Liste de vos messages postés:");
                        List<Message> messages = this.utilisateur.getMessages();
                        if (messages.isEmpty()) {
                            output.println("Vous n'avez posté aucun message");
                        }
                        for (Message message : messages) {
                            output.println(message.toString());
                        }
                        break;

                    case "/show-all-posts":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String nomUtil = clientMessage.split(" ", 2)[1];
                        Utilisateur utilisateur = checkUtilisateurExiste(nomUtil);
                        if (utilisateur == null) {
                            output.println("L'utilisateur '" + nomUtil + "' n'existe pas");
                        } else {
                            output.println("Liste des messages de " + nomUtil + " :");
                            List<Message> messagesUtilisateur = utilisateur.getMessages();
                            if (messagesUtilisateur.isEmpty()) {
                                output.println("Cet utilisateur n'a posté aucun message");
                            }
                            for (Message message : messagesUtilisateur) {
                                output.println(message.toString());
                            }
                        }
                        break;

                    case "/show":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String uuidMessage = clientMessage.split(" ", 2)[1];
                        Message message = serveur.getMessage(uuidMessage);
                        if (message == null) {
                            output.println("Aucun message avec l'id '" + uuidMessage + "' existe sur le serveur");
                        } else {
                            output.println(message.toString());
                        }
                        break;

                    case "/like":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String uuid = clientMessage.split(" ", 2)[1];
                        Message msg = serveur.likeMessage(uuid);
                        if (msg == null) {
                            output.println("Aucun message avec l'id '" + uuid + "' n'existe sur le serveur");
                        } else {
                            output.println("Message liké avec succès ! ( id : " + uuid + " )");
                        }
                        break;

                    case "/delete":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String uuidDelete = clientMessage.split(" ", 2)[1];
                        if (this.utilisateur.supprimeMessage(uuidDelete)) {
                            output.println("Message supprimé avec succès ! ( id : " + uuidDelete + " )");
                        } else {
                            output.println("Vous n'avez posté aucun message avec cet ID.");
                        }
                        break;

                    case "/follow":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String nomUtilisateur = clientMessage.split(" ", 2)[1];
                        Utilisateur utilisateurSuivi = checkUtilisateurExiste(nomUtilisateur);
                        if (utilisateurSuivi == null) {
                            output.println("L'utilisateur " + nomUtilisateur + " n'existe pas");
                        } else {
                            if (this.utilisateur.getAbonnements().contains(utilisateurSuivi)) {
                                output.println("Vous suivez déjà " + nomUtilisateur);
                                break;
                            }
                            this.utilisateur.ajouteAbonnement(utilisateurSuivi);
                            output.println("Vous suivez maintenant " + nomUtilisateur);
                        }
                        break;
                    
                    case "/unfollow":
                        if (warningContenuManquant(clientMessage, output)) break;
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
                        afficherMenuAideClient(output);
                        break;

                    default:
                        Thread.sleep(100); // au cas ou on sait jamais
                        output.println("Pas de requête valide spécifiée");
                }
                if (this.utilisateur == null){
                    output.println("Utilisateur supprimé par un administrateur. Déconnexion");
                    output.println("shutdown");
                    break;
                }
            }
            System.out.println(username + " s'est déconnecté");
            fermerSession();
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

    /**
     * Méthode qui vérifie si un utilisateur existe déjà dans la liste des utilisateurs du serveur
     * @param nomUtilisateur
     * @return l'utilisateur s'il existe, null sinon
     */
    private Utilisateur checkUtilisateurExiste(String nomUtilisateur) {
        for (Utilisateur utilisateur : serveur.getUtilisateurs()) {
            if (utilisateur.getNom().equals(nomUtilisateur)) {
                return utilisateur;
            }
        }
        return null;
    }

    /**
     *  Méthode qui crée un utilisateur et l'ajoute à la liste des utilisateurs du serveur
     * @param nomUtilisateur
     * @return l'utilisateur créé
     */
    private Utilisateur creerUtilisateur(String nomUtilisateur) {
        Utilisateur utilisateur = new Utilisateur(nomUtilisateur);
        serveur.ajouteUtilisateur(utilisateur);
        return utilisateur;
    }

    /**
     * Méthode qui crée un message et l'ajoute à la liste des messages de l'utilisateur
     * @param utilisateur
     * @param contenu
     * @return le message créé
     */
    private Message creerMessage(Utilisateur utilisateur, String contenu){
        Message message = Message.builder()
            .uuid(UUID.randomUUID().toString())
            .auteur(utilisateur)
            .contenu(contenu)
            .date(LocalDateTime.now())
            .nbLikes(0)
            .build();
        utilisateur.ajouteMessage(message);
        return message;
    }

    private void partagerMessage(Utilisateur utilisateur, Message message){
        this.serveur.redirigerMessage(utilisateur, message);
    }

    /**
     * Méthode qui affiche au client la liste des commandes disponibles
     * @param output
     */
    private void afficherMenuAideClient(PrintWriter output) {
        output.println("----------------------------------------------");
        output.println("Liste des commandes disponibles pour le client:");
        output.println("/post <contenu> : poster un message");
        output.println("/show-my-posts : afficher la liste de vos messages postés");
        output.println("/show-all-posts <nom_utilisateur> : afficher la liste des messages postés par un utilisateur");
        output.println("/show <uuid> : afficher un message");
        output.println("/like <uuid> : liker un message");
        output.println("/delete <uuid> : supprimer un de vos messages");
        output.println("/follow <nom_utilisateur> : suivre un utilisateur");
        output.println("/unfollow <nom_utilisateur> : ne plus suivre un utilisateur");
        output.println("/help : afficher la liste des commandes disponibles");
        output.println("----------------------------------------------");
    }

    public void recevoirMessage(Message message) {
        output.println("-------------------------------------");
        output.println("Message posté par une personne que vous suivez");
        output.println(message.toString());
        output.println("-------------------------------------");
    }

    private String traiterRequeteConnexion() throws IOException{
         // recevoir nom utilisateur rentre par client
            String inputUsername = input.readLine();
            this.utilisateur = checkUtilisateurExiste(inputUsername);
            boolean nouveauCompte = false;

            while (this.utilisateur == null) {
                output.println("notregistered");
                output.println("L'utilisateur '" + inputUsername + "' n'existe pas");
                output.println("Voulez-vous créer un compte avec ce nom ? (y/n)");
                String reponse = input.readLine();
                if (reponse.equalsIgnoreCase("y") || reponse.equalsIgnoreCase("yes")) {
                    this.utilisateur = creerUtilisateur(inputUsername);
                    nouveauCompte = true;
                } else {
                    output.println("Veuillez entrer un autre nom d'utilisateur : ");
                    inputUsername = input.readLine();
                    this.utilisateur = checkUtilisateurExiste(inputUsername);
                }
            }
            String username = this.utilisateur.getNom();
            System.out.println(username + " s'est connecté" );
            output.println("Bienvenue " + username + " !");
            if (nouveauCompte) {
                this.afficherSuggestionsAbonnements();
            }
            return username;
    }

    public void afficherSuggestionsAbonnements(){
        output.println("Vous venez de créer un compte ! Voici une liste d'utilisateurs que vous pourriez suivre :");
        int cpt = 0;
        for (Utilisateur utilisateur : serveur.getUtilisateurs()) {
            if (!utilisateur.equals(this.utilisateur) && !this.utilisateur.getAbonnements().contains(utilisateur)) {
                output.println("- "+utilisateur.toString());
                cpt++;
            }
            
            if (cpt >= Constantes.LIMITE_NB_UTILISATEURS_SUGGERES) {
                break;
            }
        }
        if (cpt == 0) {
            output.println("Aucun utilisateur à suivre pour le moment");
        }
    }

    /**
     * Méthode qui affiche un warning si le contenu de la requete est manquant
     * @param requete
     * @param output
     * @return true si le contenu est manquant, false sinon
     */
    public static boolean warningContenuManquant(String requete, PrintWriter output){
        if (requete.split(" ", 2).length < 2){
            output.println(Constantes.MESSAGE_ARGUMENT_COMMANDE_MANQUANT);
            return true;
        }   
        return false;
    }

    /**
     * Méthode qui affiche un warning si le contenu de la requete est manquant
     * @param requete
     * @return true si le contenu est manquant, false sinon
     */
    public static boolean warningContenuManquant(String requete){
        if (requete.split(" ", 2).length < 2){
            System.out.println(Constantes.MESSAGE_ARGUMENT_COMMANDE_MANQUANT);
            return true;
        }
        return false;
    }

    /**
     * Méthode qui ferme la session en fermant les flux et le socket
     * @throws IOException
     */
    public void fermerSession() throws IOException{
        this.input.close();
        this.output.close();
        this.clientSocket.close();
    }

    /**
     * Méthode qui renvoie une représentation de la session en String
     */
    @Override
    public String toString(){
        return "Session de "+this.utilisateur.getNom();
    }
}