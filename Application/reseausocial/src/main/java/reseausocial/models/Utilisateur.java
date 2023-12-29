package reseausocial.models;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Getter
@Setter
@Builder
public class Utilisateur {
    private String nom;

    private List<Utilisateur> abonnements;

    private List<Message> messages;

    public Utilisateur(String nom, List<Utilisateur> abonnements, List<Message> messages) {
        this.nom = nom;
        this.abonnements = abonnements;
        this.messages = messages;
    }

    public Utilisateur(String nom){ 
        this.nom = nom;
        this.abonnements = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public void ajouteAbonnement(Utilisateur utilisateur){
        this.abonnements.add(utilisateur);
    }

    public void supprimeAbonnement(Utilisateur utilisateur){
        this.abonnements.remove(utilisateur);
    }

    public void ajouteMessage(Message message){
        this.messages.add(message);
    }

    public void supprimeMessage(Message message){
        this.messages.remove(message);
    }

    public boolean supprimeMessage(String uuid){
        for (Message message : this.messages){
            if (message.getUuid().equals(uuid)){
                this.messages.remove(message);
                return true;
            }
        }
        return false;
    }

    public void supprimeMessages(){
        this.messages.clear();
    }

    public boolean likeMessage(String uuid){
        for (Message message : this.messages){
            if (message.getUuid().equals(uuid)){
                message.likeMessage();
                return true;
            }
        }
        return false;
    }

    public Message getMessage(String uuid){
        for (Message message : this.messages){
            if (message.getUuid().equals(uuid)){
                return message;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
            }
        if (!(o instanceof Utilisateur)){
             return false;
            }
        Utilisateur utilisateur = (Utilisateur) o;
        return utilisateur.getNom().equals(this.nom);
    }
    /**
     * Méthode qui renvoie une représentation de l'utilisateur en String
     */
    @Override
    public String toString(){
        return this.nom+", Nb messages envoyés : "+this.messages.size();
    }

    /**
     * Méthode qui affiche une représentation de l'utilisateur en String
     */
    public void afficheUtilisateur(){
        System.out.println(this.toString());
    }
}
