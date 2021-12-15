package abalone;

import utils.Direction;

import java.util.List;

public abstract class Player {
    /**player's name.*/
    private String name;
    /**Marbles belonging to a player.*/
    Marble[] marbles;
    /**Team member of a player.*/
    Player teamMember;
    /**Check if a player is online.*/
    private boolean online;
    /**
     * Construct a player with the name and marbles.
     * @param name name of the player
     * @param marbles marbles belonging to a player
     */
    public Player(String name, Marble[] marbles) {
        this.name = name;
        this.marbles = marbles;
    }
    /**
     * Get the alive marbles belonging to a player.
     * @return the number of available marbles which is alive on the board
     */
    public int availableMarbles(){
        int i = 0;
        for (Marble m: marbles) {
            if(m.isAlive){
                i++;
            }
        }
        return i;
    }
    /**
     * Gets the team player of this player.
     * @return the team player
     */
    public Player getTeamMember() {
        return teamMember;
    }
    /**
     * Set team player to this player.
     * @param teamMember
     */
    public void setTeamMember(Player teamMember) {
        this.teamMember = teamMember;
    }
    /**
     * Get the player's name.
     * @return player's name
     */
    public String getName() {
        return name;
    }
    /**
     * Set the player's name.
     * @param name of the player
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Get marbles of the player.
     * @return
     */
    public Marble[] getMarbles() {
        return marbles;
    }
    /**
     * Set the marbles.
     * @param marbles to be set 
     */
    public void setMarbles(Marble[] marbles) {
        this.marbles = marbles;
    }
    /**
     * Player's online state.
     * @return true if the player is online else false
     */
    public boolean isOnline() {
        return online;
    }
    /**
     * Change the state of the player.
     * @param onlineState state of the player
     */
    public void setOnline(boolean onlineState) {
        this.online = onlineState;
    }
    /**
     * Get marbles which the player wants to move.
     * @param str command which shows positions of which marbles to moved
     * @return marbles to be moved
     */
    public abstract List<Marble> getMarblesToPlay(String str);
    /**
     * Get directions where the marbles to be moved to.
     * @param str command which shows the marbles direction
     * @return the Direction of marbles moved to
     */
    public abstract Direction getDirection(String str);
}
