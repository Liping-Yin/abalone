package abalone;

import utils.MarbleColor;

import java.util.ArrayList;
import java.util.List;

public class Marble {
	/**marble's position*/
    private Position pos;
    /**marble's color*/
    MarbleColor color;
    /**whether marble is pushed down from the board */
    boolean isAlive;
    /**
     * Construct a marble with color and its position by row and column.
     * @param color marble's color
     * @param row marble's position belongs to which row in the board
     * @param col marble's position belongs to which column in the board
     */
    public Marble(MarbleColor color, int row, int col) {
        this.color = color;
        this.pos = new Position(row, col);
        this.isAlive = true;
    }
    /**
     * Set marble's position by row and column.
     * @param row marble's position belongs to which row in the board
     * @param col marble's position belongs to which column in the board 
     */
    public void setPos(int row, int col) {
        if(pos == null){
            pos = new Position(row, col);
        }else {
            this.pos.row = row;
            this.pos.col = col;
        }
    }
    /**
     * set marble's position by an instance of Position.
     * @param pos marble's position
     */
    public void setPos(Position pos) {
        this.pos = pos;
    }
    /**Get the position of the marble*/
    public Position getPos() {
        return pos;
    }
    /**Get the row in the position of the marble*/
    public int getRow(){
        return pos.row;
    }
    /**Get the column in the position of the marble*/
    public int getCol(){
        return pos.col;
    }

}
