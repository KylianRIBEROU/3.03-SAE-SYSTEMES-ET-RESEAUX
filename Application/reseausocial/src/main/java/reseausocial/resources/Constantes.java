package reseausocial.resources;

import java.util.Arrays;
import java.util.List;

public class Constantes {
    
    public static final int BUFFSIZE = 1024;
    public static final int PORT = 5555;
    public static final int LIMITE_NB_MESSAGES = 10;
    public static final int LIMITE_NB_UTILISATEURS_SUGGERES = 10;
    public static final int LONGUEUR_MAX_PSEUDO_CLIENT = 100;

    public static final String MESSAGE_ARGUMENT_COMMANDE_MANQUANT = "Il manque un contenu à la requête. Si vous avez besoin de précision sur comment la structurer, tapez /help";

    public static final List<String> COMMANDES_FIN_SESSION = Arrays.asList("/exit", "/quit", "/logout");
    
}
