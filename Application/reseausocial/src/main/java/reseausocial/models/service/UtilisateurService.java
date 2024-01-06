package reseausocial.models.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import reseausocial.models.entity.Utilisateur;

@Service
public interface UtilisateurService {

    public void creerUtilisateur(String pseudonyme, String motDePasse);

    public void creerUtilisateur(Utilisateur utilisateur);

    public void supprimerUtilisateur(long id);

    public void supprimerUtilisateur(String nom);

    public void supprimerUtilisateur(Utilisateur utilisateur);

    public Utilisateur findById(long id);

    public Utilisateur findByPseudonyme(String pseudonyme);

    public List<Utilisateur> findAll();
    
}
