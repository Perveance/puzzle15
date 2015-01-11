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
}
