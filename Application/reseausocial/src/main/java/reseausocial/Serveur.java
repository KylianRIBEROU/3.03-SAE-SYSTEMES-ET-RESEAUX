package reseausocial;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;

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

    //TODO: avoir une classe avec des méthodes pour gérer les commandes .Comme ca on pourrait partager les commandes entre le client et le serveur
    private List<Utilisateur> utilisateurs;
    private List<Session> sessions;
    private BufferedReader inputServeur;

    private final DatabaseManager databaseManager;

    @Autowired
    public Serveur(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;


        this.utilisateurs = this.databaseManager.getUtilisateurs();
        //TODO: avoir aussi une liste avec seulement les utilisateurs actuellement connectés
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

    /**
     * Méthode qui vérifie si le pseudonyme est valide. Un pseudonyme est valide s'il
     * ne contient pas de caractères spéciaux, s'il n'est pas vide et s'il fait moins
     * de 100 caractères.
     * @param pseudonyme
     * @return true si pseudonyme valide, false sinon
     */
    public static boolean pseudonymeUtilisateurValide(String pseudonyme) {
        return pseudonyme.matches("[a-zA-Z0-9]+") && pseudonyme.length() > 0 && pseudonyme.length() <= 100;
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

    public boolean UtilisateurExiste(String pseudonyme){
        return this.databaseManager.findUtilisateurByPseudonyme(pseudonyme) != null;
    }

    public List<Publication> getPublicationsUtilisateur(String pseudoUtilisateur) {
        return this.databaseManager.getPublicationsByUtilisateurPseudo(pseudoUtilisateur);
    }

    public Set<Publication> getPublicationsUtilisateur(Utilisateur utilisateur){
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

    public Utilisateur creerUtilisateur(String pseudo, String motDePasse) {

        Utilisateur u = this.databaseManager.creerUtilisateur(pseudo, motDePasse);
        System.out.println("Un utilisateur vient de créer un compte ! Pseudo : "+pseudo);
        return u;
    }

    public boolean utilisateurExiste(String pseudo) {
        return this.databaseManager.findUtilisateurByPseudonyme(pseudo) != null;
    }

    public boolean unfollowUtilisateur(String pseudoUtilisateur, String pseudoUtilisateurSuivi) {
        return this.databaseManager.unfollowUtilisateur(pseudoUtilisateur, pseudoUtilisateurSuivi);
    }

    public boolean unfollowUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurSuivi) {
        return this.databaseManager.unfollowUtilisateur(utilisateur, utilisateurSuivi);
    }

    public boolean suivreUtilisateur(String pseudoUtilisateur, String pseudoUtilisateurASuivre ){
        return this.databaseManager.suivreUtilisateur(pseudoUtilisateur, pseudoUtilisateurASuivre);
    }

    public boolean suivreUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurASuivre ){
        return this.databaseManager.suivreUtilisateur(utilisateur, utilisateurASuivre);
    }

    public List<Utilisateur> getListeSuggestionUtilisateurs(String pseudoUtilisateurAExclure, int limite){
        return this.databaseManager.findRandomUtilisateurs(pseudoUtilisateurAExclure, limite);
    }

    public Publication utilisateurLikePublication(String pseudoUtilisateur, Long idPublication){
        return this.databaseManager.utilisateurLikePublication(pseudoUtilisateur, idPublication); 
    }

    private void displayUpTime(LocalDateTime dateInitServ){
        LocalDateTime dateFinServ = LocalDateTime.now();
        System.out.println("Serveur fermé à : "+dateFinServ.toString());
        System.out.println("Uptime : "+dateInitServ.until(dateFinServ, java.time.temporal.ChronoUnit.MINUTES));
    }

    public boolean deletePublication(Long idPublication){
        return this.databaseManager.supprimerPublication(idPublication);
    }

    @Override
    public boolean deleteUtilisateur(String nomUtilisateur) {
      return this.databaseManager.supprimerUtilisateur(nomUtilisateur);
    }

    public void afficheUtilisateurs(){
        System.out.println("----------------------------------------------");
        System.out.println("Utilisateurs :");
        for (Utilisateur utilisateur : this.utilisateurs){
            utilisateur.affichageUtilisateurSimple();
        }
        System.out.println("----------------------------------------------");
    }

    public void afficheUtilisateursConnectes(){
        //TODO: afficher les utilisateurs connectés
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