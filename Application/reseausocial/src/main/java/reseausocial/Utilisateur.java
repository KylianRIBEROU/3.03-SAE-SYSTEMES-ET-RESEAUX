package reseausocial;

import java.util.List;

import lombok.*;

@Getter
@Setter
public class Utilisateur {
    private String nom;

    private List<Utilisateur> abonnes;
    //TODO : Liste de messages, classe Message obligée pour leur donner une date, tout ca ...
}
