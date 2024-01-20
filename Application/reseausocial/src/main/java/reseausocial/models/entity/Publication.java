package reseausocial.models.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "publication")
public class Publication {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publi_id")
    Long id;

    @Column(name = "publi_contenu", nullable = false)
    String contenu;

    @Column(name = "publi_dateheure", nullable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime dateheure;

    @Column(name = "nb_likes")
    @ColumnDefault("0")
    @Builder.Default
    int nbLikes = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id") // Nom de la colonne dans la table "Publication" qui stocke l'ID de l'utilisateur*
   //   @JoinColumn(name = "utilisateur_id", referencedColumnName = "id")
    private Utilisateur auteur;

    public Publication(String contenu, Utilisateur auteur) {
        this.contenu = contenu;
        this.auteur = auteur;
        this.dateheure = LocalDateTime.now();
    }

    @ManyToMany(mappedBy = "publicationsLikees", fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Utilisateur> utilisateursQuiOntLike = new HashSet<>();

     /**
     * Méthode qui renvoie une répresentation de la publication, en string sous la forme d'un JSON
     */
    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + this.getId() + ",\n" +
                "  \"auteur\": \"" + auteur.getPseudonyme()+ "\",\n" +
                "  \"contenu\": \"" + this.getContenu() + "\",\n" +
                "  \"date\": \"" + this.getDateheure().toString() + "\",\n" +
                "  \"nbLikes\": " + this.getNbLikes() + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Publication)) {
            return false;
        }
        Publication publication = (Publication) o;
        return id == publication.id;
    }


    /**
     * Méthode qui affiche la publication, sous la forme d'un JSON
     */
    public void afficheMessage(){
        System.out.println(this.toString());
    }
}
