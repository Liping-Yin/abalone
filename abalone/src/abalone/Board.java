package abalone;

import utils.Direction;
import utils.MarbleColor;
import utils.TextIO;

import java.util.List;
/**
 * Board for the abalone game.
 * @author liping
 *
 */
public class Board {
    /**
     * Fields of the abalone game.
     */
    private Field[][] fields;
 // -- Constructors -----------------------------------------------
    /**
     * Creates empty board.
     * @ensures all fields are empty
     */
    public Board() {
        int[] sizes = {5, 6, 7, 8, 9, 8, 7, 6, 5};
        fields = new Field[sizes.length][];
        for (int i = 0; i < sizes.length; i++) {
            fields[i] = new Field[sizes[i]];
            for (int j = 0; j < fields[i].length; j++) {
                fields[i][j] = new Field();
            }
        }
    }
    /**
     * Creates a board with marbles by call.
     * @param marbles on the board
     */
    public Board(List<Marble> marbles){
        this();
        for (int i = 0; i < marbles.size(); i++) {
            Marble m = marbles.get(i);
            fields[m.getRow()][m.getCol()].marble = m;
        }
    }
    /**
     * get the next position where the current marble moved to.
     * @param p1 position of the marble to be moved
     * @param dir direction of the marble to be moved to
     * @return new position after the marble is moved
     */
    public static Position getNextPos(Position p1, Direction dir) {
        int r1=p1.row,c1=p1.col,r2=r1,c2=c1;
        if(dir==Direction.LM){
            c2--;
        }else if(dir==Direction.RM){
            c2++;
        }else if(dir==Direction.LT){
            r2--;
            if(r1<=4){
                c2--;
            }
        }else if(dir==Direction.RT){
            r2--;
            if(r1>=5){
                c2++;
            }
        }else if(dir==Direction.LB){
            r2++;
            if(r1>=4){
                c2--;
            }
        }else if(dir==Direction.RB){
            r2++;
            if(r1<4){
                c2++;
            }
        }
        if(r2<0 || r2>8 || c2<0 || c2>8 ||
                ((r2==0 || r2==8) && c2>4) ||
                ((r2==1 || r2==7) && c2>5) ||
                ((r2==2 || r2==6) && c2>6) ||
                ((r2==3 || r2==5) && c2>7)
        ){
            return null;
        }
        return new Position(r2, c2);
    }
    /**
     * Moves the marble to a new position.
     * moves the marble of this position to a new place
     * according the direction and update the position of this marble
     * @param p1 position of the marble
     * @param dir direction of the marble to be moved to
     */
    public void moveMarble(Position p1, Direction dir) {
        int r1 = p1.row;
        int c1 = p1.col;
        Marble m = fields[r1][c1].marble;
        Position p2 = getNextPos(p1, dir);
        if (p2 == null) {
            //out of range
            m.setPos(-1, -1);
            m.isAlive = false;
        } else {
            //normal move
            int r2 = p2.row;
            int c2 = p2.col;
            fields[r2][c2].marble = m;
            m.setPos(r2, c2);
        }
        fields[r1][c1].marble = null;
    }
    /**
     * Gets the marble by its position.
     * @param p position of the marble
     * @return marble of which the position belongs to
     */
    public Marble getByPos(Position p) {
        Field f = fields[p.row][p.col];
        return f.marble;
    }
    /**
     * Print out the board of the game.
     */
    public void printBoard() {
        for (int i = 0; i < fields.length; i++) {
            for (int j = 0; j < fields.length - fields[i].length; j++) {
                TextIO.put(" ");
            }
            for (int j = 0; j < fields[i].length; j++) {
                if (fields[i][j].marble == null) {
                    TextIO.put("\u001b[30m○\u001b[0m");
                } else {
                    if (fields[i][j].marble.color == MarbleColor.BLACK) {
                        TextIO.put("\u001b[30m●\u001b[0m");
                    } else if (fields[i][j].marble.color == MarbleColor.BLUE) {
                        TextIO.put("\u001b[44m●\u001b[0mm");
                    } else if (fields[i][j].marble.color == MarbleColor.RED) {
                        TextIO.put("\u001b[31m●\u001b[0m");
                    } else if (fields[i][j].marble.color == MarbleColor.WHITE) {
                        TextIO.put("\u001b[37m●\u001b[0m");
                    }
                }
                TextIO.put(" ");
            }
            TextIO.putln();
        }
    }

}
