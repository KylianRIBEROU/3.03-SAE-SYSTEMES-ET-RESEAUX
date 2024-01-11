package reseausocial.server;

public interface CommandesServeur {
    
    public boolean deletePublication(Long idPublication);

    public boolean deleteUtilisateur(String nomUtilisateur);

}
