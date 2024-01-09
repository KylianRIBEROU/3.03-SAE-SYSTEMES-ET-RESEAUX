package reseausocial;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reseausocial.models.entity.Publication;
import reseausocial.models.entity.Utilisateur;

// import reseausocial.models.Message;
// import reseausocial.models.Utilisateur;
import reseausocial.server.CommandesServeur;
import reseausocial.server.DatabaseManager;
import reseausocial.server.ServeurRequeteHandler;
import reseausocial.server.Session;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
public class Serveur implements CommandesServeur, CommandLineRunner{

    private List<Utilisateur> utilisateurs;
    private List<Session> sessions;
    private BufferedReader inputServeur;

    private final DatabaseManager databaseManager;

    @Autowired
    public Serveur(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;


        this.utilisateurs = this.databaseManager.getUtilisateurs();
        this.sessions = new ArrayList<>();
        this.inputServeur = new BufferedReader(new InputStreamReader(System.in));

        

       //  System.out.println(databaseManager.getUtilisateurs());
       //   databaseManager.creerUtilisateur("admin", "admin");
    }

    @PostConstruct
    public void init() {
        // System.out.println(databaseManager.getUtilisateurs()); // []
        // System.out.println(databaseManager.findUtilisateurByPseudo("Kylian")); // null
        // databaseManager.creerUtilisateur("admin", "admin123"); 
        // System.out.println(databaseManager.getUtilisateurs());

        // //Utilisateur a = databaseManager.findUtilisateurByPseudo("admin");

        // //  databaseManager.creerPublication("Un test 1", a);

        // this.databaseManager.creerPublicationTest("Un test de message", "admin");

        // System.out.println(databaseManager.findAllPublications());
        // Publication publi = databaseManager.findPublicationById(1);
        
        // System.out.println(publi.toString()); 

        // this.databaseManager.supprimerPublication(1);

        // System.out.println(databaseManager.getUtilisateurs());
        // System.out.println(databaseManager.findPublicationById(1));

        // this.databaseManager.creerPublicationTest("TEST SUPPRESSION CASCADE", "admin");

        // System.out.println(databaseManager.findPublicationById(2));

        // this.databaseManager.supprimerUtilisateur("admin");

        // System.out.println(databaseManager.findPublicationById(2));

    }

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void ajouteUtilisateur(Utilisateur utilisateur) {
        this.utilisateurs.add(utilisateur);
    }

    // public static void main(String[] args) {
    //     Serveur serveur = new Serveur();
    //     serveur.lancerServeur(5555);
    // }

    public static void main(String[] args) {
        SpringApplication.run(Serveur.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Serveur serveur = new Serveur(databaseManager);
        serveur.lancerServeur(5555);
    }

    private void lancerServeur(int port) {
        LocalDateTime dateInitServ = LocalDateTime.now();
        try {
            ServerSocket serveurSocket = new ServerSocket(port);
            System.out.println("Serveur lancé à : "+dateInitServ.toString());


            while (true) {

                ServeurRequeteHandler serveurRequeteHandler = new ServeurRequeteHandler(this, this.inputServeur);
                serveurRequeteHandler.start();

                Socket clientSocket = serveurSocket.accept();
                Session clientSession = new Session(this, clientSocket);
                this.sessions.add(clientSession);
                Thread clientThread = new Thread(clientSession);
                clientThread.start();
            }


        } catch (Exception e) {
            displayUpTime(dateInitServ);
            e.printStackTrace();
        }
    }

    public void redirigerMessage(Utilisateur utilisateur, Message message) {
        for (Session session : this.sessions) {
            if (session.getUtilisateur().getAbonnements().contains(utilisateur)) {
                session.recevoirMessage(message);
            }
        }
    }

    public void partagerPublication(Utilisateur utilisateur, Publication publication) {
        for (Session session : this.sessions) {
            if (utilisateur.getAbonnes().contains(session.getUtilisateur())) { //TODO: fix
                session.recevoirPublication(publication);
            }
        }
    }

    public Publication getPublicationById(Long idPublication) {
        return this.databaseManager.findPublicationById(idPublication);
    }

    public Utilisateur getUtilisateurByPseudo(String pseudonyme){
        return this.databaseManager.findUtilisateurByPseudonyme(pseudonyme);
    }

    public List<Publication> getPublicationsUtilisateur(String pseudoUtilisateur) {
        return this.databaseManager.getPublicationsByUtilisateurPseudo(pseudoUtilisateur);
    }

    public List<Publication> getPublicationsUtilisateur(Utilisateur utilisateur){
        return this.databaseManager.getPublicationsUtilisateur(utilisateur);
    }

    public boolean checkUtilisateurCredentials(String pseudo, String motDePasse){
        return this.databaseManager.checkUtilisateurCredentials(pseudo, motDePasse);
    }

    public Publication creerPublication(String contenu, String pseudoAuteur) {
        Publication publication = this.databaseManager.creerPublication(contenu, pseudoAuteur);
        Utilisateur auteur = this.databaseManager.findUtilisateurByPseudonyme(pseudoAuteur);
        this.partagerPublication(auteur, publication);
        return publication;
    }

    public boolean utilisateurExiste(String pseudo) {
        return this.databaseManager.findUtilisateurByPseudonyme(pseudo) != null;
    }

    public boolean unfollowUtilisateur(String pseudoUtilisateur, String pseudoUtilisateurSuivi) {
        return this.databaseManager.unfollowUtilisateur(pseudoUtilisateur, pseudoUtilisateurSuivi);
    }

    public boolean suivreUtilisateur(String pseudoUtilisateur, String pseudoUtilisateurASuivre ){
        return this.databaseManager.suivreUtilisateur(pseudoUtilisateur, pseudoUtilisateurASuivre);
    }

    public boolean suivreUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurASuivre ){
        return this.databaseManager.suivreUtilisateur(utilisateur, utilisateurASuivre);
    }

    public Message likeMessage(String uuidMessage){
        Message message = this.getMessage(uuidMessage);
        if (message!=null){
            message.likeMessage();
        }
        return message;
    }

    public Publication utilisateurLikePublication(String pseudoUtilisateur, Long idPublication){
        return this.databaseManager.utilisateurLikePublication(pseudoUtilisateur, idPublication); 
    }

    private void displayUpTime(LocalDateTime dateInitServ){
        LocalDateTime dateFinServ = LocalDateTime.now();
        System.out.println("Serveur fermé à : "+dateFinServ.toString());
        System.out.println("Uptime : "+dateInitServ.until(dateFinServ, java.time.temporal.ChronoUnit.MINUTES));
    }


    public Message getMessage(String uuidMessage) {
        for (Utilisateur utilisateur : this.utilisateurs){
                Message message =utilisateur.getMessage(uuidMessage);
                if (message!=null) return message;
            }
        return null;
    }

    public boolean deletePublicationById(Long idPublication){
        return this.databaseManager.supprimerPublication(idPublication);
    }

    @Override
    public void deleteMessage(String uuid) {
       for ( Utilisateur utilisateur : this.utilisateurs){
                Message message = utilisateur.getMessage(uuid);
                if (message!=null) {
                    utilisateur.supprimeMessage(message);
                    System.out.println("Message supprimé");
                };
        }
    }

    @Override
    public void deleteUtilisateur(String nomUtilisateur) {
        for (Utilisateur utilisateur : this.utilisateurs){
            if (utilisateur.getNom().equals(nomUtilisateur)){
                    utilisateur.supprimeMessages();
                    System.out.println("Messages de "+nomUtilisateur+" supprimés");
                    this.utilisateurs.remove(utilisateur);
                    System.out.println(nomUtilisateur+" supprimé");
                    for (Session session : this.sessions){
                        if (session.getUtilisateur().equals(utilisateur)){
                            session.setUtilisateur(null);
                            this.sessions.remove(session);
                            System.out.println("Session de "+nomUtilisateur+" fermée");
                            break;
                        }
                    }
                    break;
            }
        }
    }

    public void afficheUtilisateurs(){
        System.out.println("----------------------------------------------");
        System.out.println("Utilisateurs :");
        for (Utilisateur utilisateur : this.utilisateurs){
            utilisateur.afficheUtilisateur();
        }
        System.out.println("----------------------------------------------");
    }

    public void afficheCommandesServeur(){
        System.out.println("----------------------------------------------");
        System.out.println("Commandes serveur ( administrateur ) :");
        System.out.println("/delete <uuid> : supprime le message correspondant à l'uuid");
        System.out.println("/remove <nomUtilisateur> : supprime l'utilisateur et ses messages");
        System.out.println("/help : affiche les commandes serveur");
        System.out.println("----------------------------------------------");
    }


}