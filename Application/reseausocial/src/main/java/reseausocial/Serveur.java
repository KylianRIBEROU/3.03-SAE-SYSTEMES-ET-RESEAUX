package reseausocial;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Serveur implements CommandesServeur {

    private List<Utilisateur> utilisateurs;
    private List<Session> sessions;
    private BufferedReader inputServeur;

    public Serveur() {
        this.utilisateurs = new ArrayList<>();
        this.sessions = new ArrayList<>();
        this.inputServeur = new BufferedReader(new InputStreamReader(System.in));
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
                ServeurRequeteHandler serveurRequeteHandler = new ServeurRequeteHandler(this, this.inputServeur);
                serveurRequeteHandler.start();

                Socket clientSocket = serveurSocket.accept();

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

    public Message likeMessage(String uuidMessage){
        Message message = this.getMessage(uuidMessage);
        if (message!=null){
            message.likeMessage();
        }
        return message;
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
                    break;
            }
        }
    }

    public void afficheCommandesServeur(){
        System.out.println("Commandes serveur ( administrateur ) :");
        System.out.println("/delete <uuid> : supprime le message correspondant à l'uuid");
        System.out.println("/remove <nomUtilisateur> : supprime l'utilisateur et ses messages");
        System.out.println("/help : affiche les commandes serveur");
    }
}