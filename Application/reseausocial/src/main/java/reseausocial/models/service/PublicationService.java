package reseausocial.models.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import reseausocial.models.entity.Publication;
import reseausocial.models.entity.Utilisateur;

@Service
public interface PublicationService {


    public void creerPublication(String contenu, long auteurId);

    public void supprimerPublication(long id);

    public void supprimerPublication(Utilisateur utilisateur);

    public Publication findById(long id);

    public Set<Publication> findByAuteurId(long id);

}
