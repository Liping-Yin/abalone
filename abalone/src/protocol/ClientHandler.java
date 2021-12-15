package protocol;

import abalone.Game;
import abalone.Marble;
import abalone.Player;
import utils.Direction;
import utils.ProtocolCode;
import utils.TextIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientHandler implements Runnable {
	/**Game entity which players are playing.*/
    private final static List<GameEntity> gameEntities = new ArrayList<>();
    /**the ID of the lobby created by players.*/
    private static int lobbyId = 1;

    private BufferedReader in;
    private BufferedWriter out;
    private Socket sock;
    private String playerName = "";

 /**
  * Construct a ClientHandler with socket.
  * @param sock which belongs to the client 
  * @throws IOException if error happens
  */
    public ClientHandler(Socket sock) throws IOException {
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        this.sock = sock;
    }
    /**
     * The thread persistently listen to messages from the client.
     * Deal with the messages from client.
     */
    public void run() {
        String msg;
        try {
            msg = in.readLine();
            while (msg != null) {
                handleCommand(msg);
                msg = in.readLine();
            }
            shutdown();
        } catch (IOException e) {
            // For now, ignore and let thread stop.
        }
    }


    /**
     * Handle server commands
     * @param msg command from client
     * @throws IOException
     */
    private void handleCommand(String msg) throws IOException {

        String[] strs = msg.split(";");
        if(strs.length<2){
            write(ProtocolCode.ERROR_COMMAND + ";" + playerName+ ";" + ProtocolCode.UNRECOGNIZECOMMAND_ERR);
            return;
        }
        TextIO.putln(msg);
        String command = strs[0];
        if (command.equals(ProtocolCode.CREATE_COMMAND)) {
            dealCreate(strs[1], Integer.parseInt(strs[2]));
        } else if (command.equals(ProtocolCode.JOIN_COMMAND)){
            dealJoin(strs[1], Integer.parseInt(strs[2]));
        } else if (command.equals(ProtocolCode.READY_COMMAND)){
            dealReady(strs[1]);
        } else if (command.equals(ProtocolCode.MOVE_COMMAND)){
            dealMove(strs[1], strs[2], strs[3]);
        } else if (command.equals(ProtocolCode.EXIT_COMMAND)){
            dealExit(strs[1]);
        } else if (command.equals(ProtocolCode.RECONNECT_COMMAND)){
            dealReconnect(strs[1]);
        } else {
            write(ProtocolCode.ERROR_COMMAND + ";"+playerName+ ";"+ProtocolCode.UNRECOGNIZECOMMAND_ERR);
        }
    }
    
 // ------------------ ClientHandler Methods --------------------------

    /**
     *Deal with 'create ' message according to the protocol.
     *for example, if the player send messages of ready, 
     *then a new game entity will be created by lobbyId, game instance and players number
     * @throws IOException if error occurs, 
     * if invalid player's name or name has exited,
     * or player's number out range of the game rules
     */
    private void dealCreate(String playerName, int numPlayers) throws IOException {
        if(!validName(playerName)){
            write(ProtocolCode.ERROR_COMMAND + ";" + playerName+ ";" + ProtocolCode.INVALIDNAME_ERR);
            return;
        }
        if(nameExists(playerName)){
            write(ProtocolCode.ERROR_COMMAND + ";" + playerName+ ";" + ProtocolCode.NAMEDEPULICATE_ERR);
            return;
        }
        if(numPlayers<2 || numPlayers >4){
            write(ProtocolCode.ERROR_COMMAND + ";" + playerName+ ";" + ProtocolCode.INVALIDARGU_ERR);
            return;
        }
        String[] names = new String[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            names[i] = "";
        }
        names[0] = playerName;
        Game game = new Game(numPlayers, names);
        synchronized (gameEntities) {
            GameEntity entity = new GameEntity(lobbyId++, game, numPlayers);
            entity.sockets[0] = this.sock;
            entity.currentNumber = 1;
            gameEntities.add(entity);
            String msg = ProtocolCode.LOBBY_COMMAND + ";" + playerName+";" + numPlayers;
            printToAllPlayer(entity, msg);
        }
    }

    /**
     * Deal with 'join' message according to the protocol.
     * for example if a player wants to join a game,
     * if the game exists then the player will be added into that game entity 
     * @throws IOException if player name is not valid, 
     * or name has exited,or player's number out range of the game rules
     */
    private void dealJoin(String playerName, int numPlayers) throws IOException {
        if(!validName(playerName)){
            write(ProtocolCode.ERROR_COMMAND + ";" + playerName+ ";" + ProtocolCode.INVALIDNAME_ERR);
            return;
        }
        if(nameExists(playerName)){
            write(ProtocolCode.ERROR_COMMAND + ";" + playerName+ ";" + ProtocolCode.NAMEDEPULICATE_ERR);
            return;
        }
        if(numPlayers<2 || numPlayers >4){
            write(ProtocolCode.ERROR_COMMAND + ";" + playerName+ ";" + ProtocolCode.INVALIDARGU_ERR);
            return;
        }
        GameEntity entity = null;
        for(GameEntity e: gameEntities){
            if(e.playerNumber == numPlayers && e.currentNumber<numPlayers){
                entity = e;
                break;
            }
        }
        if(entity == null){
            for(GameEntity e: gameEntities){
                if(e.currentNumber<numPlayers){
                    entity = e;
                    break;
                }
            }
            if(entity == null){
                write(ProtocolCode.ERROR_COMMAND + ";" + playerName+ ";" + ProtocolCode.UNKNOWN_ERR);
            }
        }
        if(entity != null){
            entity.sockets[entity.currentNumber] = this.sock;
            entity.game.getPlayers()[entity.currentNumber].setName(playerName);
            entity.currentNumber ++;
            String msg = ProtocolCode.LOBBY_COMMAND + ";";
            for (int i = 0; i < entity.currentNumber; i++) {
                msg += entity.game.getPlayers()[i].getName() + ";";
            }
            msg += entity.playerNumber;
            printToAllPlayer(entity, msg);
        }
    }

    /**
     * Deal with 'ready' message according to the protocol.
     * for example, if all the players in the same entity send'ready' to the client,
     * then the game can start.
     * @throws IOException if valid player name
     */
    private void dealReady(String playerName) throws IOException {
        if(!nameExists(playerName)){
            write(ProtocolCode.ERROR_COMMAND + ";" + playerName+ ";" + ProtocolCode.NOTLOBBY_ERR);
            return;
        }
        for(GameEntity ge: gameEntities){
            Player[] players = ge.game.getPlayers();
            for (int i = 0; i < ge.currentNumber; i++) {
                if(players[i].getName().equals(playerName)){
                    ge.readies[i] = true;

                    if(ge.currentNumber == ge.playerNumber){
                    	// full, start
                        for (int j = 0; j < ge.playerNumber; j++) {
                            if(ge.readies[j]==false){
                                return;
                            }
                        }
                        ge.state = 1;
                        String msg1 = ProtocolCode.START_COMMAND + ";";
                        for (int j = 0; j < ge.currentNumber; j++) {
                            msg1 += ge.game.getPlayers()[j].getName() + ";";
                        }
                        printToAllPlayer(ge, msg1);
                    }
                    return;
                }
            }
        }
    }

    /**
     * Deal with 'exit' message according to the protocol.
     * for examples if player send 'exit',
     * then all the player in the game entity will get the end messages,
     * and the game will be ended
     * @throws IOException
     */
    private void dealExit(String playerName) throws IOException {
        if(!nameExists(playerName)){
            write(ProtocolCode.ERROR_COMMAND + ";" + playerName+ ";" + ProtocolCode.NOTLOBBY_ERR);
            return;
        }
        for(GameEntity ge: gameEntities){
            Player[] players = ge.game.getPlayers();
            for (int i = 0; i < ge.currentNumber; i++) {
                if(players[i].getName().equals(playerName)){
                    if(ge.state == 2){
                        players[i].setName("");
                        ge.readies[i] = false;
                        ge.sockets[i] = null;
                    }else{
                        write(ProtocolCode.ERROR_COMMAND + ";" + playerName+ ";" + ProtocolCode.UNSUPPORTED_ERR);
                    }
                    return;
                }
            }
        }
    }

    /**
     * Deal with 'move' message according to the protocol.
     * for example, according to the protocol, the player send a move command:
     * MOVE;LB;45,46,then the clientHandLer will know which player is making the move,
     * then the game entity will make the move, and then ask the next player to move,
     * until the game is over
     * @throws IOException if errors occur
     */
    private void dealMove(String playerName, String dir, String moves) throws IOException {
        if (!nameExists(playerName)) {
            write(ProtocolCode.ERROR_COMMAND + ";" + playerName + ";" + ProtocolCode.NOTLOBBY_ERR);
            return;
        }
        for (GameEntity ge : gameEntities) {
            Player[] players = ge.game.getPlayers();
            for (int i = 0; i < ge.currentNumber; i++) {
                if (players[i].getName().equals(playerName)) {
                    if(ge.state!=1){
                        write(ProtocolCode.ERROR_COMMAND + ";" + playerName + ";" + ProtocolCode.NOTGAME_ERR);
                        return;
                    }
                    if(ge.game.getCurrentPlayer() != players[i]){
                        write(ProtocolCode.ERROR_COMMAND + ";" + playerName + ";" + ProtocolCode.NOTTURN_ERR);
                        return;
                    }

                    Game game = ge.game;
                    Direction direction = game.getDirection(dir);
                    if(direction==null){
                        write(ProtocolCode.WARNING_COMMAND + ";" + playerName + ";" + ProtocolCode.INVALIDMOVE_ERR);
                        return;
                    }
                    List<Marble> marbles = game.getMarblesToPlay(moves);
                    if(marbles==null){
                        write(ProtocolCode.WARNING_COMMAND + ";" + playerName + ";" + ProtocolCode.INVALIDMOVE_ERR);
                        return;
                    }
                    if(game.canMove(marbles, direction)){
                        game.move();
                        String msg = ProtocolCode.UPDATE_COMMAND + ";" +
                                playerName + ";" + dir + ";" + moves;
                        printToAllPlayer(ge, msg);
                        //After move, judge whether game over or not
                        if(!game.gameOver()){
                            game.changeToNextPlayer();
                        }else{
                            //Game over
                            String scoreMsg = ProtocolCode.SCORE_COMMAND + ";" + game.getCurrentPlayer().getName();
                            if(game.getPlayers().length==4){
                                scoreMsg += ";" + game.getCurrentPlayer().getTeamMember().getName();
                            }
                            try {
                                Thread.currentThread().sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            printToAllPlayer(ge, scoreMsg);
                        }
                        return;
                    }else{
                        write(ProtocolCode.WARNING_COMMAND + ";" + playerName + ";" + ProtocolCode.INVALIDMOVE_ERR);
                        return;
                    }
                }
            }
        }
    }


    /**
     * Deal with the 'reconnect' message according to the protocol.
     * for example, if the player disconnect to the the game suddenly, 
     * then the client will ask the player to reconnect the game 
     * @throws IOException
     */
    private void dealReconnect(String playerName) throws IOException {
        //TODO
    }



    /**
     * Send messages to all the players in the same game entity.
     * @param msg messages sent to the player
     * @throws IOException if errors occur
     */
    private void printToAllPlayer(GameEntity entity, String msg) throws IOException {
        for (int i = 0; i < entity.currentNumber; i++) {
            Socket sock = entity.sockets[i];
            BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            out1.write(msg);
            out1.newLine();
            out1.flush();
        }
    }
    /**Shut down the ClientHandler.*/
    private void shutdown() {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**Check if the format of a player's name is valid.*/
    private boolean validName(String name){
        if(name==null || name.equals("")){
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if(!Character.isLetterOrDigit(ch)){
                return false;
            }
        }
        return true;
    }
    /**Check if the player's name has already existed.*/
    private boolean nameExists(String name){
        for(GameEntity ge: gameEntities){
            Player[] players = ge.game.getPlayers();
            for (int i = 0; i < ge.currentNumber; i++) {
                if(players[i].getName().equals(name)){
                    return true;
                }
            }
        }
        return false;
    }
    /**Send messages to Client(player).*/
    private void write(String msg) throws IOException {
        out.write(msg);
        out.newLine();
        out.flush();
    }
}
