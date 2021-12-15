package protocol;

import utils.TextIO;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
	/**port number which the server listens to.*/
    private int port;
    /**
     * Construct a server with a port number.
     * @param port
     */
    public Server(int port) {
        this.port = port;
    }
    /**
	 * Opens a new socket and starts a new ClientHandler of the game for every connecting client.
	 */
    public void run() {
        try (ServerSocket ssock = new ServerSocket(port,0, InetAddress.getByName("127.0.0.1"))) {
            // If you want external hosts (i.e., not this computer) to connect to
            // this server you can use the following line instea
            // d. However, be careful
            // with that as this recipe server has some security issues!
            // ServerSocket ssock = new ServerSocket(port);
            while (true) {
                Socket sock = ssock.accept();
                TextIO.putln("Client connected!");
                ClientHandler handler = new ClientHandler(sock);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**Message of asking port*/
    private static final String USAGE = "Expected parameter: <port>";
    
    /**Starts the server and creates multithreaded ClientHandlers*/
    public static void main(String[] args) {
        if (args.length != 1) {
            TextIO.putln(USAGE);
            System.exit(0);
        }

        String portString = args[0];
        int port = 0;

        // parse portnumber
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            TextIO.putln(USAGE);
            TextIO.putln("ERROR: port " + portString + " is not an integer");
            System.exit(0);
        }

        // And start the server
        Server s = new Server(port);
        TextIO.putln("Server starting.");
        new Thread(s).start();
    }
}
