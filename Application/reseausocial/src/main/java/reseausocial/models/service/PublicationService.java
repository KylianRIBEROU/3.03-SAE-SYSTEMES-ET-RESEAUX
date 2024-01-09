package reseausocial.models.service;

import java.util.List;

import org.springframework.stereotype.Service;

import reseausocial.models.entity.Publication;
import reseausocial.models.entity.Utilisateur;

@Service
public interface PublicationService {


    public Publication creerPublication(String contenu, long auteurId);

    public Publication creerPublication(String contenu, Utilisateur auteur);

    public Publication creerPublication(String contenu, String pseudonymeAuteur);

    public void supprimerPublication(long id);

    public void supprimerPublicationsDunUtilisateur(Utilisateur utilisateur);

    public Publication findById(long id);

    public List<Publication> findByAuteurId(long id);

    public List<Publication> findByAuteurPseudonyme(String pseudonyme);

    public List<Publication> findAll();

    public void ajouteLikePublication(long idPublication);

    public void ajouteLikePublication(Publication publication);

    public void ajouteUtilisateurAyantLike(Publication publication, String pseudonymeUtilisateur);



}
