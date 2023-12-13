package reseausocial;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Message {
 
    private int id;
    private Utilisateur auteur;
    private String contenu;
    private LocalDateTime date;
    private int nbLikes; // réfléchir a un Atomic Integer pour rendre ça Thread safe
    
    public Message(int id, Utilisateur auteur, String contenu, LocalDateTime date, int nbLikes) {
        this.id = id;
        this.auteur = auteur;
        this.contenu = contenu;
        this.date = date;
        this.nbLikes = nbLikes;
    }

    public Message(int id, Utilisateur auteur, String contenu, LocalDateTime date) {
        this.id = id;
        this.auteur = auteur;
        this.contenu = contenu;
        this.date = date;
        this.nbLikes = 0;
    }
    

    public void likeMessage(){
        this.nbLikes++;
    }

    /**
     * Méthode qui renvoie une répresentation du message, en string sous la forme d'un JSON
     */
    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + id + ",\n" +
                "  \"auteur\": \"" + auteur.getNom() + "\",\n" +
                "  \"contenu\": \"" + this.getContenu() + "\",\n" +
                "  \"date\": \"" + this.getDate() + "\",\n" +
                "  \"nbLikes\": " + this.getNbLikes() + "\n" +
                "}";
    }

    /**
     * Méthode qui affiche le message, sous la forme d'un JSON
     */
    public void afficheMessage(){
        System.out.println(this.toString());
    }
}
