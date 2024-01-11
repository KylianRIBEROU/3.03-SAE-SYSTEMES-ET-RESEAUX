package reseausocial.models.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import reseausocial.models.entity.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    public Utilisateur findByPseudonyme(String pseudonyme); // null si pas d'utilisateur correspondant

    public Utilisateur findById(long id);

    public void deleteByPseudonyme(String pseudonyme);

    public boolean existsByPseudonyme(String pseudonyme);

    public void deleteById(long id);

    // get X utilisateurs aléatoires avec un pseudonyme différent de celui passé en paramètre, qui ne sont pas dans les utilisateurs suivis par l'utilisateur
    @Query(value = "SELECT * FROM utilisateur"
    +" WHERE pseudonyme != ?1"
    +" AND id NOT IN (SELECT abonne_id FROM suivre WHERE utilisateur_id = (SELECT id FROM utilisateur WHERE pseudonyme = ?1))"
    +" ORDER BY RAND() LIMIT ?2", nativeQuery = true)
    public List<Utilisateur> findRandomUtilisateurs(String pseudonyme, int limite);

}
