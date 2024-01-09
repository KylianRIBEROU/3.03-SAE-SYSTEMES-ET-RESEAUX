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

    public void creerUtilisateur(String pseudonyme, String motDePasse) {
        utilisateurService.creerUtilisateur(pseudonyme, motDePasse);
    }

    public void supprimerUtilisateur(long id) {
        utilisateurService.supprimerUtilisateur(id);
    }

    public void supprimerUtilisateur(String nom) {
        utilisateurService.supprimerUtilisateur(nom);
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

    public Publication creerPublication(String contenu, String pseudonymeAuteur){
        Publication publi = publicationService.creerPublication(contenu, pseudonymeAuteur);
        utilisateurService.ajoutePublication(pseudonymeAuteur, publi);
        return publi;
    }

    public boolean unfollowUtilisateur(String pseudoUtilisateur, String pseudoUtilisateurSuivi) {
        Utilisateur utilisateur = utilisateurService.findByPseudonyme(pseudoUtilisateur);
        Utilisateur utilisateurSuivi = utilisateurService.findByPseudonyme(pseudoUtilisateurSuivi);
        if (utilisateur != null && utilisateurSuivi != null) {
            if (utilisateur.getAbonnements().contains(utilisateurSuivi)) {
                utilisateur.getAbonnements().remove(utilisateurSuivi);
                return true;
            }
        }
        return false;
    }

    public boolean suivreUtilisateur(String pseudoUtilisateur, String pseudoUtilisateurASuivre) {
        Utilisateur utilisateur = utilisateurService.findByPseudonyme(pseudoUtilisateur);
        Utilisateur utilisateurSuivi = utilisateurService.findByPseudonyme(pseudoUtilisateurASuivre);
        if (utilisateur != null && utilisateurSuivi != null) {
            if (!utilisateur.getAbonnements().contains(utilisateurSuivi)) {
                utilisateur.getAbonnements().add(utilisateurSuivi);
                return true;
            }
        }
        return false;
    }

    public boolean  suivreUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurSuivi) {
        return this.utilisateurService.suivreUtilisateur(utilisateur, utilisateurSuivi);
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

    public void supprimerPublicationsUtilisateur(Utilisateur utilisateur) {
        publicationService.supprimerPublicationsDunUtilisateur(utilisateur);
    }
    
    public List<Publication> findAllPublications() {
        return publicationService.findAll();
    }

    public Publication utilisateurLikePublication(String pseudoUtilisateur, Long idPublication) {
        Publication publication = publicationService.findById(idPublication);

        if (publication != null) {
        
        publicationService.ajouteLikePublication(publication);
        publicationService.ajouteUtilisateurAyantLike(publication, pseudoUtilisateur);
        utilisateurService.ajoutePublicationLikee(pseudoUtilisateur, publication);
        }
        return publication;
    }

    public List<Publication> getPublicationsByUtilisateurPseudo(String pseudoUtilisateur) {
        return publicationService.findByAuteurPseudonyme(pseudoUtilisateur);
    }

    public Set<Publication> getPublicationsUtilisateur(Utilisateur utilisateur) {
        return utilisateur.getPublications();
    }

}
