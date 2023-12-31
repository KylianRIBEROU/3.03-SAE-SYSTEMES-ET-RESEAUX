package reseausocial.models.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "publication")
public class Publication {
    
    int id;

    String contenu;

    String date;

    int nbLikes;
}
