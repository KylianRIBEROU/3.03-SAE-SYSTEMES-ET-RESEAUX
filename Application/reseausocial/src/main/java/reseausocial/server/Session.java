package reseausocial.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Set;

import lombok.Setter;
import reseausocial.Serveur;

import reseausocial.models.entity.Utilisateur;
import reseausocial.models.entity.Publication;
import reseausocial.resources.Constantes;
import lombok.Getter;


@Getter
@Setter
public class Session implements Runnable {

    private Serveur serveur;
    private Socket clientSocket;
    private Utilisateur utilisateur;
    private String pseudoUtilConnecte;

    private BufferedReader input;
    private PrintWriter output;

    public Session(Serveur serveur, Socket clientSocket) {
        this.serveur = serveur;
        this.clientSocket = clientSocket;
        this.utilisateur = null;
        this.pseudoUtilConnecte = "N/A";

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
            this.utilisateur =  traiterRequeteConnexion();
            if (this.utilisateur == null){
                fermerSession();
                return; //TODO : check ca et enelver celui a la fin du while
            }
            this.pseudoUtilConnecte = this.utilisateur.getPseudonyme();
            output.println(String.format("Connexion réussie. Bienvenue %s !", this.pseudoUtilConnecte));
            afficherSuggestionsAbonnements();
            output.println("Tapez /help pour afficher la liste des commandes disponibles");

            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                // clientMessage.split(" ", 2) // va split seulement sur le premier espace"
                // [0] pour 1ere partie, [1 pour le reste]
                switch (clientMessage.split(" ", 2)[0]) {
                    case "/post":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String contenu = clientMessage.split(" ", 2)[1];
                        Publication publi = this.serveur.creerPublication(pseudoUtilConnecte, contenu);
                        output.println("Publication postée : " + publi.toString());
                        this.serveur.partagerPublication(this.pseudoUtilConnecte, publi);
                        break;
                    
                    case "/show-my-posts":
                        List<Publication> publications = this.serveur.getPublicationsUtilisateur(pseudoUtilConnecte);
                        if (publications.isEmpty()) {
                            output.println("Vous n'avez posté aucunes publications ! Utilisez la commande /post pour en poster une");
                        }
                        else {
                            output.println("Liste de vos publications postées :");
                            for (Publication pub : publications) {
                                output.println(pub.toString());
                            }
                        }
                        break;

                    case "/show-all-posts":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String pseudoUtil = clientMessage.split(" ", 2)[1];
                        Utilisateur utilisateur = this.serveur.getUtilisateurByPseudo(pseudoUtil);
                        if (utilisateur == null) {
                            output.println("L'utilisateur '" + pseudoUtil + "' n'existe pas");
                        } else {
                            output.println("Liste des messages de " + pseudoUtil + " :");
                            Set<Publication> publicationsUtilisateur = utilisateur.getPublications();
                            if (publicationsUtilisateur.isEmpty()) {
                                output.println("Cet utilisateur n'a posté aucune publication");
                            }
                            for (Publication p : publicationsUtilisateur) {
                                output.println(p.toString());
                            }
                        }
                        break;

                    case "/show":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String idPublication = clientMessage.split(" ", 2)[1];
                        Long id = Long.parseLong(idPublication);
                        Publication publication = this.serveur.getPublicationById(id);
                        if (publication == null) {
                            output.println("Aucun message avec l'id '" + idPublication + "' existe sur le serveur");
                        } else {
                            output.println(publication.toString());
                        }
                        break;

                    case "/like":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String idPubli= clientMessage.split(" ", 2)[1];
                        Long idPubliLong = Long.parseLong(idPubli);  //TODO: check idPubli est nombre avant, pour éviter que ParseLong lève une exception
                        Publication publiLike = this.serveur.getPublicationById(idPubliLong);
                        if (publiLike == null) {
                            output.println("Aucun message avec l'id '" + idPubli + "' n'existe sur le serveur");
                        } else {
                             if (this.serveur.utilisateurLikePublication(pseudoUtilConnecte, idPubliLong)){
                                output.println("Message liké avec succès ! ( id : " + idPubli + " )");
                             }
                             else{
                                output.println("Vous avez déjà liké ce message ! ( id : " + idPubli + " )");
                             }
                        }
                        break;

                    case "/delete":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String idMessageADelete = clientMessage.split(" ", 2)[1];
                        Long idPubliADeleteLong = Long.parseLong(idMessageADelete);
                        if (this.serveur.deletePublication(idPubliADeleteLong, this.pseudoUtilConnecte)) {
                            output.println("Message supprimé avec succès ! ( id : " + idMessageADelete + " )");
                        } else {
                            output.println("Vous n'avez posté aucun message avec cet ID.");
                        }
                        break;

                    case "/follow":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String nomUtilisateur = clientMessage.split(" ", 2)[1];
                        if (nomUtilisateur.equals(this.pseudoUtilConnecte)){
                            output.println("Vous ne pouvez pas vous suivre vous même");
                            break;
                        }
                        Utilisateur utilisateurASuivre = this.serveur.getUtilisateurByPseudo(nomUtilisateur);
                        if (utilisateurASuivre == null) {
                            output.println("L'utilisateur " + nomUtilisateur + " n'existe pas");
                        } else {
                            boolean suivre = this.serveur.suivreUtilisateur(this.pseudoUtilConnecte, utilisateurASuivre.getPseudonyme());
                            System.out.println(suivre);
                            if (!suivre){
                                output.println("Vous suivez déjà " + nomUtilisateur);
                                break;
                            }
                            else {
                            output.println("Vous suivez maintenant " + nomUtilisateur);
                            }
                        }
                        break;
                    
                    case "/unfollow":
                        if (warningContenuManquant(clientMessage, output)) break;
                        String nomUtilisateurAUnfollow = clientMessage.split(" ", 2)[1];
                        Utilisateur utilisateurUnfollow = this.serveur.getUtilisateurByPseudo(nomUtilisateurAUnfollow);
                        if (utilisateurUnfollow == null) {
                            output.println("L'utilisateur à unfollow '" + nomUtilisateurAUnfollow + "' n'existe pas");//TODo: changer comme pour le follow
                        } else {
                           if (this.serveur.unfollowUtilisateur(this.utilisateur, utilisateurUnfollow)){ //TODO: fix
                                output.println("Vous ne suivez plus " + nomUtilisateurAUnfollow);
                           }
                            else{
                                output.println("Vous ne suivez pas " + nomUtilisateurAUnfollow);
                            }
                        }
                        break;

                    case "/followers":
                        output.println("-------------------------------------");
                        output.println("Liste de vos abonnés :");
                        Set<Utilisateur> abonnes = this.serveur.getAbonnesUtilisateur(this.pseudoUtilConnecte);
                        if (abonnes.isEmpty()) {
                            output.println(" /!\\ Vous n'avez aucun abonné");
                        }
                        for (Utilisateur abonne : abonnes) {
                            output.println(abonne.toString());
                        }
                        output.println("-------------------------------------");
                        break;

                    case "/following":
                        output.println("-------------------------------------");
                        output.println("Liste des utilisateurs que vous suivez :");
                        Set<Utilisateur> abonnements = this.serveur.getAbonnementsUtilisateur(this.pseudoUtilConnecte);
                        if (abonnements.isEmpty()) {
                            output.println("/!\\ Vous ne suivez aucun utilisateur");
                        }
                        for (Utilisateur abonnement : abonnements) {
                            output.println(abonnement.toString());
                        }
                        output.println("-------------------------------------");
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
                    output.println("shutdown"); //TODO: changer comportement pour forcer déconnexion
                    break;
                }
            }
            System.out.println(pseudoUtilConnecte + " s'est déconnecté.");
            fermerSession();
        }
        catch (SocketException e){
            if (pseudoUtilConnecte.equals("N/A")){
                System.out.println("Session d'un utilisateur en cours de connexion interrompue");
            }
            else{
                System.out.println("Session de "+ pseudoUtilConnecte + " interrompue");
            }
        }
        catch (IOException e) {
            System.out.println("Flux de donnée d'un utilisateur en cours de connexion interrompu");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui affiche au client la liste des commandes disponibles
     * @param output
     */
    private void afficherMenuAideClient(PrintWriter output) {
        output.println("----------------------------------------------");
        output.println("Liste des commandes disponibles pour le client:");
        output.println("/post <contenu> : poster une publication");
        output.println("/show-my-posts : afficher la liste des publicatiokns que vous avez postées");
        output.println("/show-all-posts <nom_utilisateur> : afficher la liste des publications postées par un utilisateur");
        output.println("/show <id_publication> : afficher une publication");
        output.println("/like <id_publication> : liker une publication");
        output.println("/delete <id_publication> : supprimer une de vos publications");
        output.println("/followers : afficher la liste de vos abonnés");
        output.println("/following : afficher la liste des utilisateurs que vous suivez");
        output.println("/follow <nom_utilisateur> : suivre un utilisateur");
        output.println("/unfollow <nom_utilisateur> : ne plus suivre un utilisateur");
        output.println("/help : afficher la liste des commandes disponibles");
        output.println("----------------------------------------------");
    }

    public void recevoirPublication(Publication publication){
        output.println("-------------------------------------");
        output.println("Publication postée par une personne que vous suivez");
        output.println(publication.toString());
        output.println("-------------------------------------");
    }


    private Utilisateur traiterRequeteConnexion() throws IOException{
        // recevoir nom utilisateur rentre par client

        output.println("Vous êtes connecté au serveur sur l'ip " + this.clientSocket.getInetAddress() + ", port " + Constantes.PORT+" !");
        afficherOptionsDeConnexion();
    
        String requeteClient;
        boolean connecte = false;
        while (((requeteClient = input.readLine()) != null) || connecte) {
            
            switch (requeteClient) {
                case "1":
                    output.println("Nom d'utilisateur :");
                    String inputPseudonyme = input.readLine();
                    output.println("Mot de passe :");
                    // TODO: trouver truc pour que la saisie du mdp seulement soit
                    // cachée dans le terminal 
                    //output.println("hideinput"); et le client réagirait a cet output jsp
                    String inputMotDePasse = input.readLine();
                    if (this.serveur.checkUtilisateurCredentials(inputPseudonyme, inputMotDePasse)){
                        connecte = true;
                        return this.serveur.getUtilisateurByPseudo(inputPseudonyme);
                    }
                    else{
                        output.println("Nom d'utilisateur ou mot de passe incorrect");
                        afficherOptionsDeConnexion();
                    }
                    
                    break;
                case "2":
                    String nouveauPseudo ="";
                    while (nouveauPseudo.equals("")){
                        output.println("Veuillez choisir un nom d'utilisateur :");
                        nouveauPseudo = input.readLine();
                        // pseudo sans caractères spéciaux
                        if (!Serveur.pseudonymeUtilisateurValide(nouveauPseudo)){
                            output.println("Veuillez entrer un nom d'utilisateur valide. Max 100 caractères. Pas de caractères spéciaux.");
                            nouveauPseudo = "";
                        }
                        else if (this.serveur.UtilisateurExiste(nouveauPseudo)){
                            output.println("Ce nom d'utilisateur est déjà pris");
                            nouveauPseudo = "";
                        }
                        else {
                            break;
                        }
                    }
                    String nouveauMotDePasse = "";
                    String confirmationMotDePasse = "";
                    boolean mdpValides = false;
                    while (!mdpValides){
                        output.println("Veuillez choisir un mot de passe :");
                                        
                    // TODO: trouver truc pour que la saisie du mdp seulement soit
                    // cachée dans le terminal 
                    //output.println("hideinput"); et le client réagirait a cet output
                        nouveauMotDePasse = input.readLine();

                        if (nouveauMotDePasse.length() >= 100 || nouveauMotDePasse.length() < 1){
                            output.println("Veuillez entrer un mot de passe valide. Max 100 caractères.");
                        }
                        else{
                            output.println("Veuillez confirmer votre mot de passe :");
                            confirmationMotDePasse = input.readLine();
                            if (nouveauMotDePasse.equals(confirmationMotDePasse)){
                                mdpValides = true;
                            }
                            else{
                                output.println("Les mots de passe ne correspondent pas.");
                            }
                        }
                    } 

                    output.println("Création du compte en cours...");
                    return this.serveur.creerUtilisateur(nouveauPseudo, nouveauMotDePasse);
                case "3":
                    output.println("shutdown");
                    fermerSession(); //TODO: VERIFIER QUE TOUT EST BIEN FERME
                    break;
                default:
                    output.println("Veuillez choisir une option valide (1, 2 ou 3)");
                    break;
            }
        }
        return null;
    }

    /**
     * Méthode qui affiche une liste d'utilisateurs que l'utilisateur pourrait suivre
     */
    public void afficherSuggestionsAbonnements(){
        output.println("--------------------------------------------------");
        output.println("Voici une liste d'utilisateurs que vous pourriez suivre :");
        List<Utilisateur> utilisateursSuggeres = this.serveur.getListeSuggestionUtilisateurs(this.utilisateur.getPseudonyme(), Constantes.LIMITE_NB_UTILISATEURS_SUGGERES);
        if (utilisateursSuggeres.isEmpty()){
            output.println("Aucun utilisateur à suivre pour le moment");
        }
        else{
        for (Utilisateur util: utilisateursSuggeres){
                output.println(util.toString());
            }
        }
        output.println("--------------------------------------------------");
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

    public void afficherOptionsDeConnexion(){
        output.println("Veuillez choisir une option (1-3) :");
        output.println("1. Se connecter");
        output.println("2. Créer un compte");
        output.println("3. Quitter");
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

    // /**
    //  * Méthode qui renvoie une représentation de la session en String
    //  */
    // @Override
    // public String toString(){
    //     return "Session de "+this.utilisateur.getNom();
    // }


    /**
     * Méthode qui renvoie une représentation de la session en String
     */
    @Override
    public String toString(){
        return "Session de "+this.utilisateur.getPseudonyme();
    }

}
