package reseausocial.models.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import reseausocial.models.entity.Publication;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long>{

    public Publication findById(long id);

    public List<Publication> findByAuteurId(long id);

    // supprimer toutes les publications d'un utilisateur
    public void deleteByAuteurId(long id);

    // get toutes les publications d'un utilisateur selon son nom

    public List<Publication> findByAuteurPseudonyme(String pseudonyme);
    
}
