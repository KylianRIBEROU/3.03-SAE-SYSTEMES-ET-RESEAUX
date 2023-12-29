package reseausocial.Exception;

public class CreationCompteRefuseeException extends Exception{
    public CreationCompteRefuseeException(){
        super("Le client n'a pas souhaité poursuivre la création de compte");
    }
}
