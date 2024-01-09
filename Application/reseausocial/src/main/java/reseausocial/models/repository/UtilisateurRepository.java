package reseausocial.models.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import reseausocial.models.entity.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    public Utilisateur findByPseudonyme(String pseudonyme); // null si pas d'utilisateur correspondant

    public Utilisateur findById(long id);

    public void deleteByPseudonyme(String pseudonyme);

    public boolean existsByPseudonyme(String pseudonyme);

    public void deleteById(long id);

}
