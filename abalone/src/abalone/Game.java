package abalone;

import java.util.ArrayList;
import java.util.List;

import utils.Direction;
import utils.MarbleColor;

public class Game {

	/**Board of the game.*/
    private Board board;
    /**players in the game.*/
    private Player[] players;
    private int currentPlayerIndex;
    private AMove aMove;

    public Game(int playerNumber, String[] names) {
        if(playerNumber <2 || playerNumber > 4){
            throw new IllegalArgumentException();
        }
        players = generatePlayers(playerNumber, names);
        board = new Board(getAllMarbles());
        currentPlayerIndex = 0;
    }

    public List<Marble> getMarblesToPlay(String str) {
        List<Marble> marbles = players[currentPlayerIndex].getMarblesToPlay(str);
        return marbles;
    }

    public Direction getDirection(String dir) {
        return players[currentPlayerIndex].getDirection(dir);
    }

    public void printBoard(){
        board.printBoard();
    }

    public Player getCurrentPlayer(){
        return players[currentPlayerIndex];
    }

    public void move(){
        move(aMove);
    }

    public void move(String dir, String marble) {
        List<Marble> marbles = getMarblesToPlay(marble);
        Direction direction = getDirection(dir);
        if(canMove(marbles, direction)){
            move(aMove);
        }
    }

    public boolean canMove(List<Marble> marbles, Direction direction) {
        AMove res = null;
        res = inALine(marbles, direction);
        if(res == null){
            return false;
        }
        if(hasEnemyNext(res)){
            if(!canPush(res)){
                return false;
            }
        }
        if(canReach(res)){
            aMove = res;
            return true;
        }
        return false;
    }

    private boolean hasEnemyNext(AMove aMove) {
        if(aMove.type==1){
            Position last = aMove.getLast();
            Position next = Board.getNextPos(last, aMove.moveDir);
            if(next!=null){
                Marble m = board.getByPos(next);
                if(m!=null){
                    Player current = players[currentPlayerIndex];
                    if(m.color!=current.marbles[0].color){
                        if(current.teamMember!=null){
                            if(m.color!=current.teamMember.marbles[0].color) {
                                return true;
                            }else {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canPush(AMove aMove) {
        Position last = aMove.getLast();
        Position next = Board.getNextPos(last, aMove.moveDir);
        if(aMove.num == 1){
            return false;
        }
        Position next2 = Board.getNextPos(next, aMove.moveDir);
        if(next2==null || board.getByPos(next2)==null){
            aMove.pushNum = 1;
            return true;
        }else{
            if(aMove.num == 2){
                return false;
            }
            Marble m = board.getByPos(next2);
            Player current = players[currentPlayerIndex];
            if(m.color!=current.marbles[0].color &&
                    (current.teamMember==null || (current.teamMember!=null &&
                            m.color!=current.teamMember.marbles[0].color))){
                Position next3 = Board.getNextPos(next2, aMove.moveDir);
                if(next3==null || board.getByPos(next3)==null){
                    aMove.pushNum = 2;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canReach(AMove aMove) {
        if(aMove.type==2){
            Position p = aMove.start;
            Position next;
            int i = 0;
            do{
                next = Board.getNextPos(p, aMove.moveDir);
                if(next == null || board.getByPos(next)!=null){
                    return false;
                }
                p = Board.getNextPos(p, aMove.marbleDir);
                i++;
            }while(i < aMove.num);
        }else{
            return true;
        }
        return true;
    }


    /**
     * determine whether marbles in a line(premise for moving)
     * @param marbles all marbles player selected
     * @param direction direction player want marbles move
     * @return null if not a line, or A Move
     */
    private AMove inALine(List<Marble> marbles, Direction direction) {
        int size = marbles.size();
        AMove aMove;
        if(size == 1){
            Position p = marbles.get(0).getPos();
            Position start = new Position(p.row, p.col);
            aMove = new AMove(start,null,1, direction,0, 2);
            return aMove;
        }else if(size == 2){
            Position p1 = marbles.get(0).getPos();
            Position p2 = marbles.get(1).getPos();
            Position start = null;
            Direction dir = null;
            int type = 1;
            if(p1.row==p2.row && Math.abs(p1.col-p2.col)==1){
                if(p1.col > p2.col){
                    p1 = marbles.get(1).getPos();
                    p2 = marbles.get(0).getPos();
                }
                if(direction==Direction.LM){
                    start = new Position(p2.row, p2.col);
                    dir = Direction.LM;
                }else{
                    if(direction != Direction.RM){
                        type = 2;
                    }
                    start = new Position(p1.row, p1.col);
                    dir = Direction.RM;
                }
                aMove = new AMove(start,dir,2, direction,0, type);
                return aMove;
            }else if( Math.abs(p1.row-p2.row)==1){
                if(p1.row>p2.row){
                    p1 = marbles.get(1).getPos();
                    p2 = marbles.get(0).getPos();
                }
                boolean lb = false;
                boolean rb = false;
                if(p1.row<=3){
                    if(p1.col==p2.col){// LB
                        lb = true;
                    }else if(p2.col-p1.col==1){//RB
                        rb = true;
                    }
                }else{
                    if(p1.col-p2.col==1){// LB
                        lb = true;
                    }else if(p2.col==p1.col){//RB
                        rb = true;
                    }
                }

                if(lb || rb){
                    if(lb){
                        if(direction==Direction.RT){
                            start = new Position(p2.row, p2.col);
                            dir = Direction.RT;
                        }else{
                            if(direction!=Direction.LB){
                                type = 2;
                            }
                            start = new Position(p1.row, p1.col);
                            dir = Direction.LB;
                        }
                    }
                    if(rb){
                        if(direction==Direction.LT){
                            start = new Position(p2.row, p2.col);
                            dir = Direction.LT;
                        }else{
                            if(direction!=Direction.RB){
                                type = 2;
                            }
                            start = new Position(p1.row, p1.col);
                            dir = Direction.RB;
                        }
                    }
                    aMove = new AMove(start, dir,2, direction,0, type);
                    return aMove;
                }
            }
        }else{
            Position[] ps = new Position[3];
            ps[0] = marbles.get(0).getPos();
            ps[1] = marbles.get(1).getPos();
            ps[2] = marbles.get(2).getPos();
            Position start = null;
            Direction dir = null;
            int type = 1;
            if(ps[0].row==ps[1].row && ps[0].row==ps[2].row){
                int[] a = sortIndex(ps[0].col, ps[1].col, ps[2].col);
                if(ps[a[1]].col-ps[a[0]].col!=1 || ps[a[2]].col-ps[a[1]].col!=1){
                    return null;
                }
                ps[0] = marbles.get(a[0]).getPos();
                ps[2] = marbles.get(a[2]).getPos();
                if(direction==Direction.LM){
                    start = new Position(ps[2].row, ps[2].col);
                    dir = Direction.LM;
                }else{
                    if(direction != Direction.RM){
                        type = 2;
                    }
                    start = new Position(ps[0].row, ps[0].col);
                    dir = Direction.RM;
                }
                aMove = new AMove(start,dir,3, direction,0, type);
                return aMove;
            }else{
                int[] a = sortIndex(ps[0].row, ps[1].row, ps[2].row);
                if(ps[a[1]].row-ps[a[0]].row!=1 || ps[a[2]].row-ps[a[1]].row!=1){
                    return null;
                }
                ps[0] = marbles.get(a[0]).getPos();
                ps[1] = marbles.get(a[1]).getPos();
                ps[2] = marbles.get(a[2]).getPos();
                boolean lb = false;
                boolean rb = false;
                if(ps[0].row<=2){
                    if(ps[0].col==ps[1].col && ps[0].col==ps[2].col){// LB
                        lb = true;
                    }else if(ps[1].col-ps[0].col==1 && ps[2].col-ps[1].col==1){//RB
                        rb = true;
                    }
                }else if(ps[0].row==3){
                    if(ps[0].col==ps[1].col && ps[1].col-ps[2].col==1){// LB
                        lb = true;
                    }else if(ps[1].col-ps[0].col==1 && ps[2].col==ps[1].col){//RB
                        rb = true;
                    }
                }else if(ps[0].row>3){
                    if(ps[0].col-ps[1].col==1 && ps[1].col-ps[2].col==1){// LB
                        lb = true;
                    }else if(ps[0].col==ps[1].col && ps[1].col==ps[2].col){//RB
                        rb = true;
                    }
                }

                if(lb || rb){
                    if(lb){
                        if(direction==Direction.RT){
                            start = new Position(ps[2].row, ps[2].col);
                            dir = Direction.RT;
                        }else{
                            if(direction!=Direction.LB){
                                type = 2;
                            }
                            start = new Position(ps[0].row, ps[0].col);
                            dir = Direction.LB;
                        }
                    }
                    if(rb){
                        if(direction==Direction.LT){
                            start = new Position(ps[2].row, ps[2].col);
                            dir = Direction.LT;
                        }else{
                            if(direction!=Direction.RB){
                                type = 2;
                            }
                            start = new Position(ps[0].row, ps[0].col);
                            dir = Direction.RB;
                        }
                    }
                    aMove = new AMove(start, dir,3, direction,0, type);
                    return aMove;
                }
            }
        }
        return null;
    }

    private void move(AMove aMove){
        Position mableLast = aMove.getLast();
        if(aMove.pushNum != 0){
            Position push1 = Board.getNextPos(mableLast, aMove.marbleDir);
            if(aMove.pushNum==2){
                Position push2 = Board.getNextPos(push1, aMove.marbleDir);
                board.moveMarble(push2, aMove.moveDir);
            }
            board.moveMarble(push1, aMove.moveDir);
        }
        board.moveMarble(mableLast, aMove.moveDir);
        if(aMove.num==3){
            Position middle = Board.getNextPos(aMove.start, aMove.marbleDir);
            board.moveMarble(middle, aMove.moveDir);
        }
        if(aMove.num!=1){
            board.moveMarble(aMove.start, aMove.moveDir);
        }
    }

    public void changeToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1)%players.length;
    }

    public boolean gameOver(){
        List<Player> enemies = getEnemy(players[currentPlayerIndex]);
        int count = 0;
        for (Player p: enemies) {
            count += p.availableMarbles();
        }
        if(players.length==2 && count<=14-6){
            return true;
        }
        if(players.length==3 && count<=12*2-6){
            return true;
        }
        if(players.length==4 && count<=9*2-6){
            return true;
        }
        return false;
    }

    private List<Player> getEnemy(Player p){
        List<Player> enemies = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < players.length; i++) {
            if(players[i] == p){
                index = i;
                break;
            }
        }
        if(players.length==2){
            enemies.add(players[(index +1)%players.length]);
        }else if(players.length==3){
            enemies.add(players[(index +1)%players.length]);
            enemies.add(players[(index +2)%players.length]);
        }else{
            enemies.add(players[(index +1)%players.length]);
            enemies.add(players[(index +3)%players.length]);
        }
        return enemies;
    }

    private Player[] generatePlayers(int playerNumber, String[] names){
        Player[] ps = new Player[playerNumber];
        MarbleColor[] colors = null;
        int[][] rows = null;
        int[][] cols = null;
        if(playerNumber == 2){
            colors = new MarbleColor[]{
                    MarbleColor.BLACK,
                    MarbleColor.WHITE
            };
            rows = new int[][]{
                    {0,0,0,0,0,1,1,1,1,1,1,2,2,2},
                    {6,6,6,7,7,7,7,7,7,8,8,8,8,8}
            };
            cols = new int[][]{
                    {0,1,2,3,4,0,1,2,3,4,5,2,3,4},
                    {2,3,4,0,1,2,3,4,5,0,1,2,3,4}
            };
        }else if(playerNumber == 3){
            colors = new MarbleColor[]{
                    MarbleColor.WHITE,
                    MarbleColor.BLACK,
                    MarbleColor.BLUE
            };
            rows = new int[][]{
                    {0,0,1,1,2,2,3,3,4,4,5},
                    {0,0,1,1,2,2,3,3,4,4,5},
                    {7,7,7,7,7,7,8,8,8,8,8}
            };
            cols = new int[][]{
                    {0,1,0,1,0,1,0,1,0,1,0},
                    {3,4,4,5,5,6,6,7,7,8,7},
                    {0,1,2,3,4,5,0,1,2,3,4}
            };
        }else if(playerNumber == 4){
            colors = new MarbleColor[]{
                    MarbleColor.RED,
                    MarbleColor.BLACK,
                    MarbleColor.WHITE,
                    MarbleColor.BLUE
            };
            rows = new int[][]{
                    {0, 0, 0, 0, 1, 1, 1, 2, 2},
                    {1, 2, 2, 3, 3, 3, 4, 4, 4},
                    {4, 4, 4, 5, 5, 5, 6, 6, 7},
                    {6, 6, 7, 7, 7, 8, 8, 8, 8}
            };
            cols = new int[][]{
                    {0,1,2,3,1,2,3,2,3},
                    {5,5,6,5,6,7,6,7,8},
                    {0,1,2,0,1,2,0,1,0},
                    {3,4,2,3,4,1,2,3,4}
            };
        }
        for (int i = 0; i < playerNumber; i++) {
            Marble[] marbles = new Marble[rows[i].length];
            for (int j = 0; j < marbles.length; j++) {
                marbles[j] = new Marble(colors[i], rows[i][j], cols[i][j]);
            }
            ps[i] = new HumanPlayer(names[i], marbles);
        }
        if (playerNumber == 4) {
            for (int i = 0; i < 4; i++) {
                ps[i].setTeamMember(ps[(i+2)%4]);
            }
        }
        return ps;
    }
    /**
     * Gets the marbles of a player.
     * @ensures \result != null;
     * @return the list of marbles
     */
    private List<Marble> getAllMarbles() {
        List<Marble> marbles = new ArrayList<>();
        for (int i = 0; i < players.length; i++) {
            Player p = players[i];
            Marble[] m = p.getMarbles();
            for (int j = 0; j < m.length; j++) {
                marbles.add(m[j]);
            }
        }
        return marbles;
    }

    private static int[] sortIndex(int a, int b, int c){
        if(a<b) {
            if(a<c) {
                if(b<c){
                    return new int[]{0,1,2};
                }else{
                    return new int[]{0,2,1};
                }
            }else{
                return new int[]{2,0,1};
            }
        }else{
            if(a<c){
                return new int[]{1,0,2};
            }else{
                if(b<c){
                    return new int[]{1,2,0};
                }else{
                    return new int[]{2,1,0};
                }
            }
        }
    }
    /**
     * Inner class for checking whether marbles can be moved.
     * @author Gebruiker
     *
     */
    private class AMove {
        //start point of a line
        public Position start;
        //line direction
        public Direction marbleDir;
        //number of marbles to move
        public int num;
        //move direction
        public Direction moveDir;
        //number of enemy to push
        public int pushNum;
        //1. IN-LINE MOVE 2. SIDE-STEP MOVE
        public int type;

        public AMove(Position start, Direction marbleDir, int num, Direction moveDir, int pushNum, int type) {
            this.start = start;
            this.marbleDir = marbleDir;
            this.num = num;
            this.moveDir = moveDir;
            this.pushNum = pushNum;
            this.type = type;
        }

        public Position getLast(){
            Position last = start;
            for (int i = 0; i < num-1; i++) {
                last = Board.getNextPos(last, marbleDir);
            }
            return last;
        }
    }

    public Player[] getPlayers() {
        return players;
    }
}
