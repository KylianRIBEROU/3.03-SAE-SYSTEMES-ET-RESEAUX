package reseausocial.models.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reseausocial.models.entity.Publication;
import reseausocial.models.entity.Utilisateur;

import java.time.LocalDateTime;
import java.util.Set;

import javax.transaction.Transactional;

import java.util.List;

import reseausocial.models.repository.PublicationRepository;
import reseausocial.models.service.PublicationService;
import reseausocial.models.service.UtilisateurService;

@Service
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;

    private final UtilisateurService utilisateurService;

    @Autowired
    public PublicationServiceImpl(PublicationRepository publicationRepository, UtilisateurService utilisateurService) {
        this.publicationRepository = publicationRepository;

        this.utilisateurService = utilisateurService;
    }

    public List<Publication> findByAuteurId(long id) {
        return publicationRepository.findByAuteurId(id);
    }

    public Publication findById(long id) {
        return publicationRepository.findById(id);
    }

    @Override
    public Publication creerPublication(String contenu, long auteurId) {
        Publication publication = Publication.builder()
            .contenu(contenu)
            .dateheure(LocalDateTime.now())
            .auteur(utilisateurService.findById(auteurId))
            .build();
        publicationRepository.save(publication);
        return publication;

    }

    @Override
    public Publication creerPublication(String contenu, Utilisateur auteur) {
        Publication publication = Publication.builder()
            .contenu(contenu)
            .dateheure(LocalDateTime.now())
            .auteur(auteur)
            .build();
        publicationRepository.save(publication);
        return publication;
    }

    @Override
    public Publication creerPublication(String contenu, String pseudonymeAuteur) {
        Publication publication = Publication.builder()
            .contenu(contenu)
            .dateheure(LocalDateTime.now())
            .auteur(utilisateurService.findByPseudonyme(pseudonymeAuteur))
            .build();
        publicationRepository.save(publication);
        return publication;
    }

    @Override
    @Transactional
    public void supprimerPublication(long id) {
        publicationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void supprimerPublicationsDunUtilisateur(Utilisateur utilisateur) {
        publicationRepository.deleteByAuteurId(utilisateur.getId());
    }

    @Override
    public List<Publication> findByAuteurPseudonyme(String pseudonyme) {
        return publicationRepository.findByAuteurPseudonyme(pseudonyme);
    }

    @Override   
    public List<Publication> findAll() {
        return publicationRepository.findAll();
    }

    // @Transactional ? 
    @Override
    public void ajouteLikePublication(long idPublication) {
        Publication publication = publicationRepository.findPublicationById(idPublication);
        publication.setNbLikes(publication.getNbLikes() + 1);
        publicationRepository.save(publication);
    }

    @Override
    public void ajouteLikePublication(Publication publication) {
        publication.setNbLikes(publication.getNbLikes() + 1);
        publicationRepository.save(publication);
    }

    @Override
    public void ajouteUtilisateurAyantLike(Publication publication, String pseudonymeUtilisateur) {
        publication.getUtilisateursQuiOntLike().add(utilisateurService.findByPseudonyme(pseudonymeUtilisateur));
        publicationRepository.save(publication);
    }
}
