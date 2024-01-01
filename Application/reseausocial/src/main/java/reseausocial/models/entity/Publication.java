package reseausocial.models.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


import org.hibernate.annotations.ColumnDefault;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

@Entity
@Table(name = "publication")
public class Publication {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publi_id")
    Long id;

    @Column(name = "publi_contenu", nullable = false)
    String contenu;

    @Column(name = "publi_dateheure", nullable = false, columnDefinition = "DATETIME")
    String date;

    @Column(name = "nb_likes")
    @ColumnDefault("0")
    @Builder.Default
    int nbLikes = 0;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id") // Nom de la colonne dans la table "Publication" qui stocke l'ID de l'utilisateur*
   //   @JoinColumn(name = "utilisateur_id", referencedColumnName = "id")
    private Utilisateur auteur;

    @ManyToMany(mappedBy = "publicationsLikees")
    @Builder.Default
    private Set<Utilisateur> utilisateursQuiOntLike = new HashSet<>();
}
