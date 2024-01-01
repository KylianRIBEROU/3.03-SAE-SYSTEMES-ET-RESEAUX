package reseausocial.models.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import reseausocial.models.entity.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    public Utilisateur findByNom(String nom);

    public Utilisateur findById(long id);

    public void deleteByNom(String nom);

    


}
