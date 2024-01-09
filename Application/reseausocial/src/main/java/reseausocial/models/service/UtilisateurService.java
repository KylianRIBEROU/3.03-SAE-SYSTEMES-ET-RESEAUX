package reseausocial.models.service;

import java.util.List;

import org.springframework.stereotype.Service;

import reseausocial.models.entity.Publication;
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

    public boolean suivreUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurSuivi);

    public List<Utilisateur> findAll();

    public void ajoutePublication(String pseudonyme, Publication publication);

    public void ajoutePublication(Utilisateur utilisateur, Publication publication);

    public void ajoutePublicationLikee(String pseudoUtilisateur, Publication publication);
    
}
