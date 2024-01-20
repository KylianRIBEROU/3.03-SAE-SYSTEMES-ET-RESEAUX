package reseausocial.models.service;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

import reseausocial.models.entity.Publication;
import reseausocial.models.entity.Utilisateur;

@Service
public interface UtilisateurService {

    public Utilisateur creerUtilisateur(String pseudonyme, String motDePasse);

    public void creerUtilisateur(Utilisateur utilisateur);

    public void supprimerUtilisateur(long id);

    public void supprimerUtilisateur(String nom);

    public void supprimerUtilisateur(Utilisateur utilisateur);

    public Utilisateur findById(long id);

    public Utilisateur findByPseudonyme(String pseudonyme);

    public boolean unfollowUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurSuivi);

    public boolean suivreUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurSuivi);

    public List<Utilisateur> findAll();

    public void ajoutePublication(String pseudonyme, Publication publication);

    public void ajoutePublication(Utilisateur utilisateur, Publication publication);

    public void ajoutePublicationLikee(String pseudoUtilisateur, Publication publication);

    public void supprimerPublicationLikee(Utilisateur utilisateur, Publication publication);
    
    public List<Utilisateur> findRandomUtilisateurs(String pseudonyme, int limite);

    public Set<Utilisateur> findUtilisateursAyantLike(long idPublication);

    public boolean utilisateurALikePublication(long idPublication, String pseudonymeUtilisateur);

    public void supprimerSuivreRelations(Utilisateur utilisateur);
}
