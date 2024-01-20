package reseausocial.server;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reseausocial.models.entity.Publication;
import reseausocial.models.entity.Utilisateur;
import reseausocial.models.service.PublicationService;
import reseausocial.models.service.UtilisateurService;

@Service
public class DatabaseManager  {

    @Autowired
    private final UtilisateurService utilisateurService;

    @Autowired
    private final PublicationService publicationService;

    public DatabaseManager(UtilisateurService utilisateurService, PublicationService publicationService) {
        this.utilisateurService = utilisateurService;
        this.publicationService = publicationService;
    } 

    public Utilisateur findUtilisateurByPseudonyme(String pseudo) {
        return utilisateurService.findByPseudonyme(pseudo);
    }

    public void supprimerUtilisateur(long id) {
        utilisateurService.supprimerUtilisateur(id);
    }

    public boolean supprimerUtilisateur(String pseudonyme) {
        if (utilisateurService.findByPseudonyme(pseudonyme) != null){
            publicationService.supprimerPublicationsDunUtilisateur(this.utilisateurService.findByPseudonyme(pseudonyme));
            utilisateurService.supprimerUtilisateur(pseudonyme);
            return true;
        }
        return false;
    }

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurService.findAll();
    }

    public boolean checkUtilisateurCredentials(String pseudo, String motDePasse) {
        Utilisateur utilisateur = utilisateurService.findByPseudonyme(pseudo);
        if (utilisateur != null) {
            return utilisateur.getMotDePasse().equals(motDePasse);
        }
        return false;
    }

    public Publication creerPublication(String pseudonymeAuteur, String contenu){
        Publication publi = publicationService.creerPublication(contenu, pseudonymeAuteur);
        utilisateurService.ajoutePublication(pseudonymeAuteur, publi);
        return publi;
    }

    public Publication creerPublication(Utilisateur auteur, String contenu){
        Publication publi = publicationService.creerPublication(contenu, auteur);
        utilisateurService.ajoutePublication(auteur, publi);
        return publi;
    }

    public Utilisateur creerUtilisateur(String pseudonyme, String motDePasse) {
        return utilisateurService.creerUtilisateur(pseudonyme, motDePasse);
    }

    public boolean unfollowUtilisateur(String pseudoUtilisateur, String pseudoUtilisateurAUnfollow) {
        Utilisateur utilisateur = utilisateurService.findByPseudonyme(pseudoUtilisateur);
        Utilisateur utilisateurAUnfollow = utilisateurService.findByPseudonyme(pseudoUtilisateurAUnfollow);
       
        return utilisateurService.unfollowUtilisateur(utilisateur, utilisateurAUnfollow); // true si unfollow a fonctionné, false sinon
    }

    public boolean unfollowUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurSuivi) {
        return this.utilisateurService.unfollowUtilisateur(utilisateur, utilisateurSuivi);
    }

    public boolean suivreUtilisateur(String pseudoUtilisateur, String pseudoUtilisateurASuivre) {
        Utilisateur utilisateur = utilisateurService.findByPseudonyme(pseudoUtilisateur);
        Utilisateur utilisateurASuivre = utilisateurService.findByPseudonyme(pseudoUtilisateurASuivre);
    
        return utilisateurService.suivreUtilisateur(utilisateur, utilisateurASuivre); 
    }


    public Publication findPublicationById(long id) {
        return publicationService.findById(id);
    }

    public List<Publication> findPublicationsByAuteurId(long id) {
        return publicationService.findByAuteurId(id);
    }

    public boolean supprimerPublication(long id) {
        if (publicationService.findById(id) != null){
            publicationService.supprimerPublication(id);
            return true;
        }
        return false;
    }

    public Set<Utilisateur> getAbonnesUtilisateur(String pseudoUtilisateur) {
        Utilisateur utilisateur = utilisateurService.findByPseudonyme(pseudoUtilisateur);
        if (utilisateur != null) {
            return utilisateur.getAbonnes();
        }
        return null;
    }

    public Set<Utilisateur> getAbonnementsUtilisateur(String pseudoUtilisateur) {
        Utilisateur utilisateur = utilisateurService.findByPseudonyme(pseudoUtilisateur);
        if (utilisateur != null) {
            return utilisateur.getAbonnements();
        }
        return null;
    }

    public boolean supprimerPublication(long id, String pseudoUtilisateur) {
        Utilisateur utilisateur = utilisateurService.findByPseudonyme(pseudoUtilisateur);
        if (utilisateur != null) {
            Publication publication = publicationService.findById(id);
            if (publication != null) {
                if (publication.getAuteur().equals(utilisateur)) {
                    publicationService.supprimerPublication(id);
                    return true;
                }
            }
        }
        return false;
    }

    public void supprimerPublicationsUtilisateur(Utilisateur utilisateur) {
        publicationService.supprimerPublicationsDunUtilisateur(utilisateur);
    }
    
    public List<Publication> findAllPublications() {
        return publicationService.findAll();
    }


    public boolean utilisateurLikePublication(String pseudoUtilisateur, Long idPublication) {
        if (utilisateurService.utilisateurALikePublication(idPublication, pseudoUtilisateur)){
            return false;
        }
        else {
            Publication publication = publicationService.findById(idPublication);

            publicationService.ajouteLikePublication(publication);
            publicationService.ajouteUtilisateurAyantLike(publication, pseudoUtilisateur);
            utilisateurService.ajoutePublicationLikee(pseudoUtilisateur, publication);
            return true;
            }
    }


    public List<Publication> getPublicationsByUtilisateurPseudo(String pseudoUtilisateur) {
        return publicationService.findByAuteurPseudonyme(pseudoUtilisateur);
    }

    public Set<Publication> getPublicationsUtilisateur(Utilisateur utilisateur) {
        return utilisateur.getPublications();
    }

    public List<Utilisateur> findRandomUtilisateurs(String pseudonymeUtilisateurExclu, int limite) {
        return utilisateurService.findRandomUtilisateurs(pseudonymeUtilisateurExclu, limite);
    }

}
