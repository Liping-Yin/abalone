package abalone;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import abalone.Player;
import java.util.Arrays;

class Game2Test {

    static Game game;

    @BeforeEach
    void setUp() {
        game = new Game(2,new String[]{"Tom","Jack"});
    }



    @Test
    void getMarblesToPlay() {

    }

    @Test
    void getDirection() {

    }

    @Test
    void printBoard() {
    }

    @Test
    void getCurrentPlayer() {

    	
    }

    @Test
    void move() {
        String[] dirs = {
                "LB",
                "LT",
                "RB",
                "LT",
                "LM",
                "RM",
                "LB",
                "LB",
                "LM",
                "RT",
                "RT",
                "LB",
                "RT",
                "RT",
                "RB",
                "LB",
                "RB",
                "RT",
                "RB",
                "LB",
                "LT",
                "LM",
                "RB",
                "RM",
                "LT",
                "RM",
                "RB",
                "RT",
                "LT",
                "RM",
                "RB",

        };
        String[] marbles = {
                "13,14,15",
                "45,46,47",
                "20,21,22",
                "37",
                "29,30,31",
                "38,39",
                "28,29,30",
                "27",
                "36,37,38",
                "55,60",
                "35",
                "49,55",
                "27,36",
                "55,60",
                "19,28,37",
                "49,55",
                "45,28,37",
                "55,60",
                "52,45",
                "49,55",
                "52,58",
                "59,60",
                "37,45,52",
                "59",
                "45,58,52",
                "56,57",
                "37,45,52",
                "40",
                "45,58,52",
                "57",
                "37,45,52",

        };
        System.out.println("Game start");
        game.printBoard();
        for (int i = 0; i < dirs.length; i++) {
            game.move(dirs[i], marbles[i]);
            System.out.println(game.getCurrentPlayer().getName() + " move: " + dirs[i] + " " + marbles[i]);
            game.printBoard();
            if(game.gameOver()){
                System.out.println("Winner is: " + game.getCurrentPlayer().getName());
                break;
            }else {
                game.changeToNextPlayer();
            }
        }

    }

    @Test
    void move1() {
    }

    @Test
    void canMove() {
    }

    @Test
    void getPlayers() {
    }
}