package mszhidko.games.puzzle15;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mikhail on 12/14/14.
 */
public class Puzzle  {

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

    static public class Solution implements Serializable {

        List<Integer> moves = new ArrayList<Integer>();

        public Solution(String strSolution) {
            List<String> items = Arrays.asList(strSolution.split("\\s*,\\s*"));
            for (String i : items) {
                moves.add(Integer.decode(i));
            }
        }

        public List<Integer> getMoves() {
            return moves;
        }

        public String toString() {
            if (moves.size() == 0)
                return "";

            List<String> strMoves = new ArrayList<String>(moves.size());
            for (Integer move : moves) {
                strMoves.add(String.valueOf(move));
            }

            return TextUtils.join(",", strMoves);
        }

    }
}
