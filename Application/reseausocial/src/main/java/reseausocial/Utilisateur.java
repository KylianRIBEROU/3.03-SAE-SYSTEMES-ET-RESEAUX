package reseausocial;

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


    /**
     * Méthode qui renvoie une représentation de l'utilisateur en String
     */
    @Override
    public String toString(){
        return "Utilisateur : " + this.nom+", Nb messages envoyés : "+this.messages.size();
    }

    /**
     * Méthode qui affiche une représentation de l'utilisateur en String
     */
    public void afficheUtilisateur(){
        System.out.println(this.toString());
    }
}
