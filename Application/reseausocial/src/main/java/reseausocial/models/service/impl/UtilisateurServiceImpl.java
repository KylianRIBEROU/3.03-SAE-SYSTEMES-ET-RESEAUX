package reseausocial.models.service.impl;

import java.util.List;

import javax.transaction.Transactional;

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

    public Utilisateur findById(long id) {
        return utilisateurRepository.findById(id);
    }

    public Utilisateur findByPseudonyme(String pseudonyme) {
        return utilisateurRepository.findByPseudonyme(pseudonyme);
    }

    public void creerUtilisateur(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
    }

    public void creerUtilisateur(String pseudonyme, String motDePasse) {
        Utilisateur utilisateur = Utilisateur.builder()
            .pseudonyme(pseudonyme)
            .motDePasse(motDePasse)
            .build();
        utilisateurRepository.save(utilisateur);
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
}
