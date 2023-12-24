package reseausocial;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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
        try {
            ServerSocket serveurSocket = new ServerSocket(port);

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
}