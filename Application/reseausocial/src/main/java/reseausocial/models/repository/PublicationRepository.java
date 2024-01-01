package reseausocial.models.repository;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import reseausocial.models.entity.Publication;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long>{

    public Publication findById(long id);

    public Set<Publication> findByAuteurId(long id);

    // supprimer toutes les publications d'un utilisateur
    public void deleteByAuteurId(long id);

    
}
