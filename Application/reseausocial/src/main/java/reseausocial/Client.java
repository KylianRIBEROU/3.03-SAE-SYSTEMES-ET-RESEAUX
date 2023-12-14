package reseausocial;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(args);
            System.out.println("Usage: java Client <nom_serveur> <nom_utilisateur>");
            System.out.println("Exemple: java Client localhost SNIVEAU");
            System.exit(1);
        }

        String host = args[0];
        String request = args[1];

        System.out.println(host);
        System.out.println(request);
        client(host, Constantes.PORT, request);
    }

    private static void client(String host, int port, String request) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(host);

            byte[] requestData = request.getBytes();
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, address, port);

            socket.send(requestPacket);

            byte[] responseData = new byte[Constantes.BUFFSIZE];
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);

            socket.receive(responsePacket);
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());

            System.out.println(response);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
