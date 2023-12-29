package reseausocial;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Message {
 
    private String uuid;
    private Utilisateur auteur;
    private String contenu;
    private LocalDateTime date;
    private int nbLikes; // réfléchir a un Atomic Integer pour rendre ça Thread safe
    
    public Message(String uuid, Utilisateur auteur, String contenu, LocalDateTime date, int nbLikes) {
        this.uuid = uuid;
        this.auteur = auteur;
        this.contenu = contenu;
        this.date = date;
        this.nbLikes = nbLikes;
    }

    public Message(String uuid, Utilisateur auteur, String contenu, LocalDateTime date) {
        this.uuid = uuid;
        this.auteur = auteur;
        this.contenu = contenu;
        this.date = date;
        this.nbLikes = 0;
    }
    
 
    public void likeMessage(){
        this.nbLikes++;
    }

    /**
     * Méthode qui renvoie vrai si le message est égal à un autre message, faux sinon
     */
    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
            }
        if (!(o instanceof Message)){
             return false;
            }
        Message message = (Message) o;
        return message.getUuid().equals(this.uuid);
    }

    /**
     * Méthode qui renvoie une répresentation du message, en string sous la forme d'un JSON
     */
    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + this.getUuid() + ",\n" +
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
