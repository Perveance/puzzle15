package mszhidko.games.puzzle15;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikhail on 12/14/14.
 */
public class Puzzle {

    private Board startBoard;

    private Solution solution;
    private int optMoves = 0;

    public Puzzle(Board b, Solution s) {
        startBoard = b;
        solution = s;
        optMoves = b.getOptimalSolutionMoves();
    }

    public Board getStartBoard() {
        return startBoard;
    }

    public int getOptMoves() {
        return optMoves;
    }

    public Solution getSolution() {
        return solution;
    }

    static public class Solution {

        List<Integer> moves = new ArrayList<Integer>();

        public Solution() {
            // TODO: solution should be passed to constructor as well
            moves.add(1); moves.add(2); moves.add(3); // Now just some fake solution
        }

        public String toString() {
            return moves.toString();
        }

    }
}
