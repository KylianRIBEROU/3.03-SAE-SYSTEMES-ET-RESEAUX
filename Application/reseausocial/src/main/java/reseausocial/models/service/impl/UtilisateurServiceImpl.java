package reseausocial.models.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public UtilisateurRepository getUtilisateurRepository() {
        return utilisateurRepository;
    }


    public Utilisateur findById(long id) {
        return utilisateurRepository.findById(id);
    }

    public Utilisateur findByNom(String nom) {
        return utilisateurRepository.findByNom(nom);
    }

    public void creerUtilisateur(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
    }

    public void creerUtilisateur(String pseudonyme, String motDePasse) {
        Utilisateur utilisateur = Utilisateur.builder()
            .nom(pseudonyme)
            .motDePasse(motDePasse)
            .build();
        utilisateurRepository.save(utilisateur);
    }

    public void supprimerUtilisateur(Utilisateur utilisateur) {
        utilisateurRepository.delete(utilisateur);
    }

    public void supprimerUtilisateur(long id) {
        utilisateurRepository.deleteById(id);
    }

    public void supprimerUtilisateur(String nom) {
        utilisateurRepository.deleteByNom(nom);
    }
}
