package abalone;

import utils.Direction;

import java.util.ArrayList;
import java.util.List;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, Marble[] marbles) {
        super(name, marbles);
    }

    @Override
    public List<Marble> getMarblesToPlay(String str) {
        List<Marble> marbles;
        marbles = new ArrayList<>();
//        TextIO.put("Player " + name + ", enter marbles position (split by ','):");
        str = str.toLowerCase();
        String[] strs = str.split(",");
        if(strs.length > 3){
//            TextIO.putln("No more than 3 marbles, please try again");
            return null;
        }else{
            for (int i = 0; i < strs.length; i++) {
                Marble m = validMarble(strs[i], 1);
                if(m == null){
//                    TextIO.putln(strs[i] + " is not a valid marble, please try again");
                    return null;
                }else{
                    marbles.add(m);
                }
            }
        }
        return marbles;
    }

    @Override
    public Direction getDirection(String dir) {
        String[] directions = {"LT","RT","LM","RM","LB","RB"};
        for (int i = 0; i < directions.length; i++) {
            if(directions[i].equalsIgnoreCase(dir)){
                Direction direction = null;
                switch (directions[i]){
                    case "LT": direction = Direction.LT;break;
                    case "RT": direction = Direction.RT;break;
                    case "LM": direction = Direction.LM;break;
                    case "RM": direction = Direction.RM;break;
                    case "LB": direction = Direction.LB;break;
                    case "RB": direction = Direction.RB;break;
                }
                return direction;
            }
        }
        return null;
    }


    /**
     *
     * @param str marble input
     * @param type BOARD REPRESENTATION type.
     *             1.Naive approach
     *             2.Counting per row
     *             3.Coordinate-like
     * @return
     */
    private Marble validMarble(String str, int type) {
        if(str==null){
            return null;
        }
        if(str.length()!=2 && str.length()!=1){
            return null;
        }
        if(str.length()==1){
            str = "0" + str;
        }
        Position p = null;
        try {
            switch (type) {
                case 1:
                    int type1 = Integer.parseInt(str);
                    if(type1<0 || type1>60){
                        return null;
                    }else{
                        int type2 = naive2counting(type1);
                        p = counting2pos(type2);
                    }
                    break;
                case 2:
                    p = validCounting(str);
                    break;
                case 3:
                    p = validCoordinate(str);
                    break;
                default:
                    return null;
            }
        }catch (Exception e){
            return null;
        }
        if(p!=null){
            return getMarbleByPosition(p);
        }else{
            return null;
        }
    }

    private Marble getMarbleByPosition(Position pos){
        for (int i = 0; i < marbles.length; i++) {
            Marble m = marbles[i];
            if(m.getRow() == pos.row && m.getCol() == pos.col){
                return m;
            }
        }
        if(teamMember!=null){
            for (int i = 0; i < teamMember.marbles.length; i++) {
                Marble m = teamMember.marbles[i];
                if(m.getRow() == pos.row && m.getCol() == pos.col){
                    return m;
                }
            }
        }
        return null;
    }

    private Position counting2pos(int counting){
        int row = counting/10;
        int col = counting%10;
        return new Position(row, col);
    }

    private int naive2counting(int naive){
        int[] nums = {0,5,11,18,26,35,43,50,56,61};
        int row = 0;
        int col = 0;
        for (int i = 0; i < nums.length-1; i++) {
            if(naive>=nums[i] && naive<nums[i+1]){
                row = i;
                break;
            }
        }
        col = naive - nums[row];
        return row*10 + col;
    }

    private int coordinate2naive(String coordinate){
        return ('i' - coordinate.charAt(0))*10 + (coordinate.charAt(1)-'0');
    }

    private Position validCoordinate(String str){
        char c1 = str.charAt(0);
        char c2 = str.charAt(1);
        if(c1>='a' && c1<='i' && c2>='1' && c2<='9'){
            String str1 = "";
            str1 += ('i'-c1);
            str1 += c2-'1';
            return validCounting(str1);
        }else{
            return null;
        }
    }

    private Position validCounting(String str){
        Position res = null;
        int num = Integer.parseInt(str);
        int row = num/10;
        int col = num%10;
        if(row>=0 && row<=8 && col>=0 && col<=9){
            if((row==0 || row==8) && col>4){
                return res;
            }
            if((row==1 || row==7) && col>5){
                return res;
            }
            if((row==2 || row==6) && col>6){
                return res;
            }
            if((row==3 || row==5) && col>7){
                return res;
            }
            return new Position(row,col);
        }else{
            return res;
        }
    }


}
