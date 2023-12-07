package reseausocial;

public class Client implements Runnable{
    private socket socket;
    public Client(socket socket){
        this.socket = socket;
    }
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String line;
            while((line = in.readLine()) != null){
                System.out.println("Message reçu : " + line);
                System.out.println(line);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}