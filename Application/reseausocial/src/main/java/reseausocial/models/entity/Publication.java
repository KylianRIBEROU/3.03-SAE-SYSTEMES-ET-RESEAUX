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
    Long id;

    @Column(name = "contenu", nullable = false)
    String contenu;

    @Column(name = "dateheure", nullable = false, columnDefinition = "DATETIME")
    String date;

    @Column(name = "nb_likes")
    @ColumnDefault("0")
    @Builder.Default
    int nbLikes = 0;

    @ManyToMany(mappedBy = "publicationsLikees")
    @Builder.Default
    private Set<Utilisateur> utilisateursQuiOntLike = new HashSet<>();
}
