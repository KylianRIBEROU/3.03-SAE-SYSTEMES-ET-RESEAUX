package reseausocial.models.service.impl;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reseausocial.models.entity.Publication;
import reseausocial.models.entity.Utilisateur;
import reseausocial.models.repository.UtilisateurRepository;
import reseausocial.models.service.UtilisateurService;

@Service
public class UtilisateurServiceImpl implements UtilisateurService{
    
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }


    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur findById(long id) {
        return utilisateurRepository.findById(id);
    }

    public Utilisateur findByPseudonyme(String pseudonyme) {
        return utilisateurRepository.findByPseudonyme(pseudonyme);
    }

    public void creerUtilisateur(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
    }

    public Utilisateur creerUtilisateur(String pseudonyme, String motDePasse) {
        Utilisateur utilisateur = Utilisateur.builder()
            .pseudonyme(pseudonyme)
            .motDePasse(motDePasse)
            .build();
        utilisateurRepository.save(utilisateur);
        return utilisateur;
    }

    @Transactional
    public void supprimerUtilisateur(Utilisateur utilisateur) {
        utilisateurRepository.delete(utilisateur);
    }

    @Transactional
    public void supprimerUtilisateur(long id) {
        utilisateurRepository.deleteById(id);
    }

    @Transactional
    public void supprimerUtilisateur(String pseudonyme) {
        utilisateurRepository.deleteByPseudonyme(pseudonyme);
    }

    @Override
    public void ajoutePublication(Utilisateur utilisateur, Publication publication) {
        utilisateur.getPublications().add(publication);
        utilisateurRepository.save(utilisateur);
    }

    @Override
    public void ajoutePublication(String pseudonymeUtilisateur, Publication publication) {
        Utilisateur utilisateur = utilisateurRepository.findByPseudonyme(pseudonymeUtilisateur);
        utilisateur.getPublications().add(publication);
        utilisateurRepository.save(utilisateur);
    }

    @Override
    public void ajoutePublicationLikee(String pseudoUtilisateur, Publication publication) {
        Utilisateur utilisateur = utilisateurRepository.findByPseudonyme(pseudoUtilisateur);
        utilisateur.getPublicationsLikees().add(publication);
        utilisateurRepository.save(utilisateur);
    }

    @Override
    public void supprimerPublicationLikee(Utilisateur utilisateur, Publication publication) {
        utilisateur.getPublicationsLikees().remove(publication);
        utilisateurRepository.save(utilisateur);
    }

    @Override
    public boolean suivreUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurSuivi) {
        System.out.println(utilisateur.getId() + " " + utilisateurSuivi.getId());
        System.out.println(this.utilisateurRepository.utilisateurSuitUtilisateur(utilisateur.getId(), utilisateurSuivi.getId()));

        System.out.println(this.utilisateurRepository.utilisateurSuitUtilisateur(utilisateurSuivi.getId(), utilisateur.getId()));

        if (this.utilisateurRepository.utilisateurSuitUtilisateur(utilisateurSuivi.getId(), utilisateur.getId()) == 1) {
            System.out.println("TU LE SUIS DEJA FRERE");
            return false;
        }
        utilisateur.getAbonnements().add(utilisateurSuivi);
        utilisateurSuivi.getAbonnes().add(utilisateur); // a vérifier, l'ajout des deux cotés est il nécessaire ?
        utilisateurRepository.save(utilisateur);
        utilisateurRepository.save(utilisateurSuivi);
        return true;
   
    }

    @Override
    public boolean unfollowUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurSuivi) {
        if (this.utilisateurRepository.utilisateurSuitUtilisateur(utilisateur.getId(), utilisateurSuivi.getId()) == 0) {
            return false;
        }
        utilisateur.getAbonnements().remove(utilisateurSuivi);
        utilisateurSuivi.getAbonnes().remove(utilisateur); // a vérifier, l'ajout des deux cotés est il nécessaire ?
        utilisateurRepository.save(utilisateur);
        utilisateurRepository.save(utilisateurSuivi);
        return true;
    }

    @Override
    public List<Utilisateur> findRandomUtilisateurs(String pseudonyme, int limite) {
        return utilisateurRepository.findRandomUtilisateurs(pseudonyme, limite);
    }

    @Override
    public Set<Utilisateur> findUtilisateursAyantLike(long idPublication) {
        return utilisateurRepository.findUtilisateursAyantLike(idPublication);
    }

    @Override
    public boolean utilisateurALikePublication(long idPublication, String pseudonymeUtilisateur) {
        Utilisateur utilisateur = utilisateurRepository.findByPseudonyme(pseudonymeUtilisateur);
        int valeur = utilisateurRepository.utilisateurALikePublication(utilisateur.getId(), idPublication);
        return valeur == 1;
    }

}
