package reseausocial.models.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reseausocial.models.entity.Publication;
import reseausocial.models.entity.Utilisateur;

import java.time.LocalDateTime;
import java.util.Set;

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

    public Set<Publication> findByAuteurId(long id) {
        return publicationRepository.findByAuteurId(id);
    }

    public Publication findById(long id) {
        return publicationRepository.findById(id);
    }

    @Override
    public void creerPublication(String contenu, long auteurId) {
        Publication publication = Publication.builder()
            .contenu(contenu)
            .date(LocalDateTime.now().toString())
            .auteur(utilisateurService.findById(auteurId))
            .build();
        publicationRepository.save(publication);
    }

    @Override
    public void supprimerPublication(long id) {
        publicationRepository.deleteById(id);
    }

    @Override
    public void supprimerPublication(Utilisateur utilisateur) {
        publicationRepository.deleteByAuteurId(utilisateur.getId());
    }
}
