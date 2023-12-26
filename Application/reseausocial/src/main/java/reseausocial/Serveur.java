package reseausocial;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
public class Serveur {

    private List<Utilisateur> utilisateurs;
    private List<Session> sessions;

    public Serveur() {
        this.utilisateurs = new ArrayList<>();
        this.sessions = new ArrayList<>();
    }

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void ajouteUtilisateur(Utilisateur utilisateur) {
        this.utilisateurs.add(utilisateur);
    }

    public static void main(String[] args) {
        Serveur serveur = new Serveur();
        serveur.lancerServeur(5555);
    }

    // Le serveur sera Threadisé pour répondre a plusieurs clients en même temps

    private void lancerServeur(int port) {
        LocalDateTime dateInitServ = LocalDateTime.now();
        try {
            ServerSocket serveurSocket = new ServerSocket(port);
            System.out.println("Serveur lancé à : "+dateInitServ.toString());

            while (true) {

                // le serveur doit pouvoir effectuer des commandes aussi de son coté


                Socket clientSocket = serveurSocket.accept(); // commande bloquante ? vérifier si plusieurs  clients peuvent se connecter en même temps

                // TODO: condition selon le nombre de processeurs availables
                // int nbProcesseurs = Runtime.getRuntime().availableProcessors();

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

    public void likeMessage(String uuidMessage){
        for (Session session : this.sessions){
            Utilisateur utilisateur = session.getUtilisateur();
            if (utilisateur!=null) session.getUtilisateur().likeMessage(uuidMessage);
        }
    }

    private void displayUpTime(LocalDateTime dateInitServ){
        LocalDateTime dateFinServ = LocalDateTime.now();
        System.out.println("Serveur fermé à : "+dateFinServ.toString());
        System.out.println("Uptime : "+dateInitServ.until(dateFinServ, java.time.temporal.ChronoUnit.MINUTES));
    }
}