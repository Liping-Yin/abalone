package abalone;

import utils.Direction;

import java.util.List;

public class ComputerPlayer extends Player {
    /**
     * Construct a computer player with name and its marbles.
     * @param name
     * @param marbles
     */
    public ComputerPlayer(String name, Marble[] marbles) {
        super(name, marbles);
    }

    @Override
    public List<Marble> getMarblesToPlay(String str) {
        return null;
    }

    @Override
    public Direction getDirection(String dir) {
        return null;
    }
}
