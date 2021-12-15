package protocol;

import abalone.Game;

import java.net.Socket;

public class GameEntity {
	/**lobby ID which represents the specific game.*/
    public int lobby;
    /**Abalone game instance.*/
    public Game game;
    /**number of players in the game.*/
    public int playerNumber;
    /**current number of plays who have already joined the game.*/
    public int currentNumber;
    /**multiple clients(players) in the game.*/
    public Socket[] sockets;
    /**Ready states of all the players in the game entity.*/
    public boolean[] readies;
    /**state of the game.*/
    public int state; //0.not start 1.start 2.end
    
    /**
     * Construct a game entity with lobbyID, game instance and number of players
     * @param lobby ID of lobby
     * @param game game instance 
     * @param numPlayers number of players in this game
     */
    public GameEntity(int lobby, Game game, int numPlayers) {
        this.lobby = lobby;
        this.game = game;
        this.playerNumber = numPlayers;
        this.sockets = new Socket[numPlayers];
        this.readies = new boolean[numPlayers];
        this.state = 0;
    }
}
