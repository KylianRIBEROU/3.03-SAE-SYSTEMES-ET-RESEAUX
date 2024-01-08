package reseausocial.models.service;

import java.util.List;

import org.springframework.stereotype.Service;

import reseausocial.models.entity.Publication;
import reseausocial.models.entity.Utilisateur;

@Service
public interface PublicationService {


    public void creerPublication(String contenu, long auteurId);

    public void creerPublication(String contenu, Utilisateur auteur);

    public void creerPublication(String contenu, String pseudonymeAuteur);

    public void supprimerPublication(long id);

    public void supprimerPublicationsDunUtilisateur(Utilisateur utilisateur);

    public Publication findById(long id);

    public List<Publication> findByAuteurId(long id);

    public List<Publication> findByAuteurPseudonyme(String pseudonyme);

    public List<Publication> findAll();



}
