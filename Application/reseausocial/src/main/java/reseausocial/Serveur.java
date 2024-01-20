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

    }

    /**
     * Méthode qui est appelée après la création de l'objet Serveur
     * utilisée à des fins de tests et débuggage
     */
    @PostConstruct
    public void init() {

    }

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void ajouteUtilisateur(Utilisateur utilisateur) {
        this.utilisateurs.add(utilisateur);
    }

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

    public void fermerSessionAvecNomUtilisateur(String nomUtilisateur) {
        for (Session session : this.sessions) {
            if (session.getUtilisateur().getPseudonyme().equals(nomUtilisateur)) {
                try {
                    session.fermerSession();            
                }
                catch (Exception e) {
                    System.out.println("Erreur lors de la fermeture de la session de l'utilisateur "+nomUtilisateur);
                }
            }
        }
    }

    public void partagerPublication(String pseudoUtil , Publication publication) {
        Utilisateur utilisateur = this.databaseManager.findUtilisateurByPseudonyme(pseudoUtil);
        Set<Utilisateur> abonnes = utilisateur.getAbonnes();
        for (Session session : this.sessions) {
            if (abonnes.contains(session.getUtilisateur()) ) { 
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

    public Set<Utilisateur> getAbonnesUtilisateur(String pseudoUtilisateur){
        return this.databaseManager.getAbonnesUtilisateur(pseudoUtilisateur);
    }

    public Set<Utilisateur> getAbonnementsUtilisateur(String pseudoUtilisateur){
        return this.databaseManager.getAbonnementsUtilisateur(pseudoUtilisateur);
    }
    public boolean checkUtilisateurCredentials(String pseudo, String motDePasse){
        return this.databaseManager.checkUtilisateurCredentials(pseudo, motDePasse);
    }

    public Publication creerPublication(String pseudoAuteur, String contenu) {
        Publication publication = this.databaseManager.creerPublication(pseudoAuteur, contenu);
        Utilisateur auteur = this.databaseManager.findUtilisateurByPseudonyme(pseudoAuteur);
        this.partagerPublication(auteur.getPseudonyme(), publication);
        return publication;
    }

    public Publication creerPublication(Utilisateur auteur, String contenu) {
        Publication publication = this.databaseManager.creerPublication(auteur, contenu);
        this.partagerPublication(auteur.getPseudonyme(), publication);
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

    public List<Utilisateur> getListeSuggestionUtilisateurs(String pseudoUtilisateurAExclure, int limite){
        return this.databaseManager.findRandomUtilisateurs(pseudoUtilisateurAExclure, limite);
    }

    public boolean utilisateurLikePublication(String pseudoUtilisateur, Long idPublication){
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

    public boolean deletePublication(Long idPublication, String pseudoUtilisateur){
        return this.databaseManager.supprimerPublication(idPublication, pseudoUtilisateur);
    }

    @Override
    public boolean deleteUtilisateur(String nomUtilisateur) {
      return this.databaseManager.supprimerUtilisateur(nomUtilisateur);
    }

    public void supprimerSuivreRelations(String nomUtilisateur){
        this.databaseManager.supprimerSuivreRelations(nomUtilisateur);
    }


    public void afficheUtilisateurs(){
        List<Utilisateur> utilisateurs = this.databaseManager.getUtilisateurs();
        System.out.println("----------------------------------------------");
        System.out.println("Utilisateurs :");
        for (Utilisateur utilisateur : utilisateurs) {
            utilisateur.affichageUtilisateurSimple();        }
        System.out.println("----------------------------------------------");
    }

    public void afficheCommandesServeur(){
        System.out.println("----------------------------------------------");
        System.out.println("Commandes serveur ( administrateur ) :");
        System.out.println("/show-all-users : affiche tous les utilisateurs");
        System.out.println("/delete <id_publication> : supprime la publication avec l'id spécifié");
        System.out.println("/remove <nomUtilisateur> : supprime l'utilisateur et ses publications");
        System.out.println("/help : affiche les commandes serveur");
        System.out.println("----------------------------------------------");
    }


}