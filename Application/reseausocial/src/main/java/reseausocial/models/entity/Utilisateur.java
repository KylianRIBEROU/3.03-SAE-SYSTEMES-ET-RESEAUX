package reseausocial.models.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "utilisateur_id")
    Long id;

    @Column(name = "pseudonyme", nullable = false, unique = true, length = 100)
    String pseudonyme;

    @Column(name = "motdepasse", nullable = false , length = 100)
    String motDePasse;

    @OneToMany(mappedBy = "auteur", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @Builder.Default
    private Set<Publication> publications = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "utilisateur_like_publication",
        joinColumns = @JoinColumn(name = "utilisateur_id"),
        inverseJoinColumns = @JoinColumn(name = "publication_id")
    )
    @Builder.Default
    private Set<Publication> publicationsLikees = new HashSet<>();


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "suivre",
        joinColumns = @JoinColumn(name = "utilisateur_id"),
        inverseJoinColumns = @JoinColumn(name = "abonne_id")
    )
    @Builder.Default
    private Set<Utilisateur> abonnements = new HashSet<>();

    @ManyToMany(mappedBy = "abonnements", fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Utilisateur> abonnes = new HashSet<>();


    public Utilisateur(String pseudonyme, String motDePasse) {
        this.pseudonyme = pseudonyme;
        this.motDePasse = motDePasse;
    }

    @Override
    public String toString(){
        return "{" +
            "\"pseudonyme\":\"" + pseudonyme + "\"," +
            "\"nb abonnes\":\"" + this.abonnes.size() + "\"," +
            "\"nb abonnements\":\"" + this.abonnements.size() +
             "\"" +
            "}";
    }

    public void affichageInformationsUtilisateur(){
        System.out.println(this.toString()) ;
    }

    public void affichageUtilisateurSimple(){
        System.out.println("Utilisateur: " + this.pseudonyme+", nbAbo: "+this.abonnes.size()+", nbAbonnements: "+this.abonnements.size() +", nbP: "+this.publications.size());;
    }

    @Override
    public boolean equals(Object o){
        if(o == this) return true;
        if(!(o instanceof Utilisateur)) return false;
        Utilisateur u = (Utilisateur) o;
        return this.pseudonyme.equals(u.pseudonyme);
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.pseudonyme);
    }

}