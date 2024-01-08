package reseausocial.server;

import java.util.List;

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

    public Utilisateur findUtilisateurByPseudo(String pseudo) {
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

    public void creerPublication(String contenu, Utilisateur auteur) {
        publicationService.creerPublication(contenu, auteur);
    }

    public void creerPublication(String contenu, String pseudonymeAuteur){
        publicationService.creerPublication(contenu, pseudonymeAuteur);
    }

    public Publication findPublicationById(long id) {
        return publicationService.findById(id);
    }

    public List<Publication> findPublicationsByAuteurId(long id) {
        return publicationService.findByAuteurId(id);
    }

    public void creerPublicationTest(String contenu, String pseudoAuteur){

        Utilisateur auteur = this.findUtilisateurByPseudo(pseudoAuteur);
        System.out.println(auteur);
        this.creerPublication(contenu, auteur);
    }

    public void supprimerPublication(long id) {
        publicationService.supprimerPublication(id);
    }

    public void supprimerPublicationsUtilisateur(Utilisateur utilisateur) {
        publicationService.supprimerPublicationsDunUtilisateur(utilisateur);
    }
    
    public List<Publication> findAllPublications() {
        return publicationService.findAll();
    }
}
