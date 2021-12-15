package protocol;

import abalone.Game;
import abalone.Player;
import utils.ProtocolCode;
import utils.TextIO;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    /**
     * Message for IP address and port number.
     */
    private static final String USAGE = "usage: <address> <port>";
    /**
	 * States of the game.
	 */
    private static int STATE_ERROR = -1;
    private static int STATE_INITIAL = 0;
    private static int STATE_CONNECT = 1;
    private static int STATE_LOBBY = 2;
    private static int STATE_START = 3;
    private static int STATE_OVER = 4;
    private static int STATE_EXIT = 5;

    /**IP address*/
    private String ip;
    /**port number*/
    private int port;
    /**Socket of the client*/
    private Socket sock = null;
    /**output stream to the server*/
    private BufferedWriter out;
    /**input stream from the server*/
    private BufferedReader in;
    /**
     * -1 cannot connect server
     * 0 initial
     * 1 connect to server
     * 2 in a lobby
     * 3 game start
     * 4 game over
     * 5 exit
     */
    public int state = STATE_INITIAL;
    public String errorReceive;
    private Game game;
    /**current player of the game.*/
    private Player player;
    /**game created by which player.*/
    private String clientName;
    /**state of whether the client has received commands from the server. */
    private volatile boolean hasReceive = false;
    /**command received from the server.*/
    private volatile int handleResult = 0;

 /**
  * Construct a Client with IP and Port number.
  * @param ip IP address
  * @param port port number
  * @requires ip!=null && port != null;
  */
    public Client(String ip, String port){
        // check args[1] - the IP-adress
        try {
            InetAddress.getByName(ip);
            this.ip = ip;
        } catch (UnknownHostException e) {
            TextIO.putln(USAGE);
            TextIO.putln("ERROR: host " + ip + " unknown");
            System.exit(0);
        }

        // parse args[2] - the port
        try {
            this.port = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            TextIO.putln(USAGE);
            TextIO.putln("ERROR: port " + ip + " is not an integer");
            System.exit(0);
        }
    }

    /**
     * Create a new Client. 
     * persistently ask for the player to input message then send the messages to the server
     * when the connection to the server is made and the player hasn't exited the game
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            TextIO.putln(USAGE);
            System.exit(0);
        }
        Client client = new Client(args[0], args[1]);
        if(client.connect()){
            while(!client.createOrJoin());
            while(client.state != STATE_EXIT){
                String msg = TextIO.getln();
                msg = client.change(msg.toLowerCase());
                client.sendToServer(msg);
            }
        }
    }

    /**
     * Create connection to the server.
     * State of the connection between the client and server will be returned,
     * and input and output stream to the client will be created,
     * threaded listening to the commands from server will be created
     * 
     * @return state of the connection to server.
     */
    public boolean connect(){
        // try to open a Socket to the server
        try {
            this.sock = new Socket(ip, port);
            TextIO.putln("Connect to Abalone game server successfully");
            in = new BufferedReader(new InputStreamReader(
                    sock.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(
                    sock.getOutputStream()));
            state = STATE_CONNECT;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true){
                            String msg = in.readLine();
                            handleResult = handleCommand(msg);
                            hasReceive = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            return true;
        } catch (IOException e) {
            TextIO.putln("ERROR: could not create a socket on " + ip
                    + " and port " + port);
            state = STATE_ERROR;
            return false;
        }
    }
 /**
  * Send the message of the player to the server and return the result.
  * @param msg messages send to the server
  * @return the result of whether the message has been sent successfully
  * @requires msg != null;
  */
    public boolean sendToServer(String msg){
        try {
            out.write(msg);
            out.newLine();
            out.flush();
            return true;
        } catch (IOException e) {
            TextIO.putln("ERROR: unable to communicate to server");
            e.printStackTrace();
            return false;
        }
    }
 /**
  * Ask player to create or join a lobby.
  * 
  * @return true if the client has received command from the server
  */
    public boolean createOrJoin(){
        String command = null;
        int choose = 0;
        while(true){
            TextIO.put("Please Create(1) or Join(2) a abalone lobby: ");
            String str = TextIO.getln();
            try{
                choose = Integer.parseInt(str);
                if(choose==1 || choose==2){
                    break;
                }
            }catch (Exception ex){
            }
        }
        String name = null;
        int num = 0;
        while(state < STATE_LOBBY) {
            while (true) {
                TextIO.put("Please enter your name and player numbers(2-4) in the lobby(eg. Tom 4): ");
                String[] str = TextIO.getln().split(" ");
                if (str.length != 2) {
                    continue;
                }
                name = str[0];
                if (name.equals("")) {
                    continue;
                }
                try {
                    num = Integer.parseInt(str[1]);
                    if (num >= 2 && num <= 4) {
                        break;
                    }
                } catch (Exception ex) {
                }
            }
            if (choose == 1) {
                command = "CREATE";
            } else if (choose == 2) {
                command = "JOIN";
            }
            String msg = command + ";" + name + ";" + num;
            sendToServer(msg);
            while(hasReceive == false);
            hasReceive = false;
            if(handleResult<0 && errorReceive.equals(ProtocolCode.UNKNOWN_ERR)){
                return false;
            }
        }
        this.clientName = name;
        return true;
    }
 /**
  * change the messages received from the player to protocol format.
  * 
  * @param msg messages received from the player
  * @return the messages format according to the protocol
  */
    private String change(String msg) {
        if(msg.equals("ready")){
            return ProtocolCode.READY_COMMAND + ";" + clientName;
        }else if(msg.equals("exit")){
            return ProtocolCode.EXIT_COMMAND + ";" + clientName;
        }else if(msg.equals("reconnect")){
            return ProtocolCode.RECONNECT_COMMAND + ";" + clientName;
        }else if(msg.split(";")[0].equals("move")){
            String[] args = msg.split(";");
            return ProtocolCode.MOVE_COMMAND + ";" + clientName + ";" + args[1] + ";" + args[2];
        }
        return msg;
    }


    /**
     * Handle server commands
     * @param msg command from client
     * @throws IOException
     */
    private int handleCommand(String msg) throws IOException {
        String[] strs = msg.split(";");
        if(strs.length<2){
            return -1;
        }
        String command = strs[0];
        if (command.equals(ProtocolCode.ERROR_COMMAND)) {
            errorReceive = strs[2];
            dealError(errorReceive);
            return -1;
        } else if (command.equals(ProtocolCode.LOBBY_COMMAND)){
            dealLobby(strs);
        } else if (command.equals(ProtocolCode.START_COMMAND)){
            dealStart(strs);
        } else if (command.equals(ProtocolCode.WARNING_COMMAND)){
            TextIO.putln(strs[2]);
            return -1;
        } else if (command.equals(ProtocolCode.RANDOM_COMMAND)){
            //TODO
        } else if (command.equals(ProtocolCode.UPDATE_COMMAND)){
            dealUpdate(strs);
        } else if (command.equals(ProtocolCode.SCORE_COMMAND)){
            dealScore(strs);
        } else if (command.equals(ProtocolCode.DISCONNECTED_COMMAND)){
            //TODO
        }
        return 1;
    }
 // ------------------ Client Methods --------------------------
    /**
     * Deal with the error messages from the player according to the protocol.
     * for example , if the there is no lobby for two players' game, but the player wants to join the game,
     * then error message will be output
     * @param str messages sent from the player
     */
    private void dealError(String str) {
        if(str.equals(ProtocolCode.UNKNOWN_ERR)){
            if(state==STATE_CONNECT){
                TextIO.putln("No lobby, please create one");
            }else{

            }
        }else{
            TextIO.putln(str);
        }
    }
 /**
  * Deal with messages of update according to the protocol.
  * for example, if the updated message received,
  * then the client will ask the current player to enter a move,
  * then take turn to the next player and so on
  * 
  * @param strs messages of how to move a marble
  */
    private void dealUpdate(String[] strs) {
        String name = strs[1];
        String dir = strs[2];
        String marbles = strs[3];
        game.move(dir, marbles);
        game.changeToNextPlayer();
        TextIO.putln("Player "+ name + " move.");
        player = game.getCurrentPlayer();
        printBoard();
    }
    /**
     * Deal with the end of the game.
     * if there is a winner, then client sends a message about who wins the game,
     * and ask player to exit the game
     * @param strs message about the player
     */
    private void dealScore(String[] strs) {
        String msg = "Game over. Winner ";
        if(game.getPlayers().length!=4){
            msg += "is: " + strs[1];
        }else{
            msg += "are: " + strs[1] + " and " +strs[2];
        }
        TextIO.putln(msg);
        state = STATE_OVER;
        TextIO.putln("If you want to exit the lobby, enter EXIT");
    }
    /**
     * Deal with the start of the game.
     * if a 
     * @param strs
     */
    private void dealStart(String[] strs) {
        int len = strs.length;
        String msg = "Game start. Players are: ";
        String[] names = new String[len-1];
        for (int i = 1; i < strs.length; i++) {
            msg += strs[i] +",";
            names[i-1] = strs[i];
        }
        TextIO.putln(msg);
        game = new Game(len-1, names);
        player = game.getCurrentPlayer();
        state = STATE_START;
        printBoard();
    }
 /**
  * Deal with lobby message according to the protocol.
  * For example, if the player created the game,
  *  then the client will send the lobby messages to the players
  *  about how many players are playing the game and which players 
  * @param strs message about the players who play the game
  */
    private void dealLobby(String[] strs) {
        int len = strs.length;
        int amount = Integer.parseInt(strs[len-1]);
        String msg = "This is a " + amount + " players lobby: ";
        for (int i = 1; i < len-2; i++) {
            msg += strs[i] +",";
        }
        msg += strs[len-2] +". If you are ready to play, please send 'READY'";
        TextIO.putln(msg);
        state = STATE_LOBBY;
    }
    /**Print the board of the game.
     * After the players are ready, the client will ask the current player to make a move
     */
    private void printBoard(){
        game.printBoard();
        if(player.getName().equals(clientName)){
            TextIO.putln("Your turn. Please enter direction and marbles to move(e.g. MOVE;LB;7,13): ");
        }else{
            TextIO.putln("Wait for " + player.getName() + " move...");
        }
    }
}
