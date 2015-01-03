package mszhidko.games.puzzle15;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikhail on 12/14/14.
 */
public class Solution {

    Board startBoard;
    List<Integer> moves = new ArrayList<Integer>();
    int optMoves = 0;

    Solution(Board b) {
        startBoard = b;

        // TODO: solution should be passed to constructor as well
        moves.add(1); moves.add(2); moves.add(3); // Now just some fake solution
    }

    public String toString() {
        return moves.toString();
    }

    public Board getStartBoard() {
        return startBoard;
    }

    public int getOptMoves() {
        return optMoves;
    }
}
