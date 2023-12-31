package reseausocial.models.entity;

import javax.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder

@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "pseudonyme", nullable = false, unique = true, length = 100)
    String nom;

    @Column(name = "motdepasse", nullable = false , length = 100)
    String motDePasse;

    @ManyToMany
    @JoinTable(
        name = "utilisateur_like_publication",
        joinColumns = @JoinColumn(name = "utilisateur_id"),
        inverseJoinColumns = @JoinColumn(name = "publication_id")
    )
    @Builder.Default
    private Set<Publication> publicationsLikees = new HashSet<>();


    @ManyToMany
    @JoinTable(
        name = "suivre",
        joinColumns = @JoinColumn(name = "utilisateur_id"),
        inverseJoinColumns = @JoinColumn(name = "abonne_id")
    )
    @Builder.Default
    private Set<Utilisateur> abonnes = new HashSet<>();

    @ManyToMany(mappedBy = "abonnes")
    @Builder.Default
    private Set<Utilisateur> abonnements = new HashSet<>();

}