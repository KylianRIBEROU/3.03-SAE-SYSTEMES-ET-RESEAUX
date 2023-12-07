package reseausocial;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        int nb = Runtime.getRuntime().availableProcessors();
        FixedThreadPool pool = new FixedThreadPool(nb);
        int port = 4444;
        try{
            ServerSocket server = new ServerSocket(port);
            while(true){
                Socket socket = server.accept();
                System.out.println("Connexion établie");
                pool.fork();
            }
        }
    }
}
