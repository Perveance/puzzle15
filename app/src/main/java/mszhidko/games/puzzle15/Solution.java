package mszhidko.games.puzzle15;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

/**
 * Created by mikhail on 1/10/15.
 */
public class Solution implements Serializable {

    private Stack<Integer> moves = new Stack<Integer>();

    public Solution() {
    }

    public Solution(String strSolution) {
        List<String> items = Arrays.asList(strSolution.split("\\s*,\\s*"));
        for (String i : items) {
            moves.add(Integer.decode(i));
        }
    }

    public int getMove(int ind) {
        if (ind >= moves.size())
            return -1;
        return moves.get(moves.size() - 1 - ind);
    }

    public int getNumberOfMoves() {
        return moves.size();
    }

    public String toString() {
        if (moves.size() == 0)
            return "";

        Stack<String> strMoves = new Stack<String>();
        ListIterator<Integer> i = moves.listIterator(moves.size());
        while (i.hasPrevious()) {
            int val = i.previous();
            strMoves.add(String.valueOf(val));
        }

        return TextUtils.join(",", strMoves);
    }

    void pushMove(Integer m) {
        moves.push(m);
    }

    int popMove() {
        return moves.pop();
    }

    // returns solution from the first move until move N
    public Solution subSolution(int N) {
        // N is move number, since moves is stack
        int stackN = moves.size() - 1 - N;
        Solution subSolution = new Solution();
        for (int i = moves.size() - 1; i > stackN; i--) {
            subSolution.pushMove(moves.get(i));
        }

        return subSolution;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;

        if (!(o instanceof Solution))
            return false;

        Solution s = (Solution) o;

        if (s.getNumberOfMoves() != this.getNumberOfMoves())
            return false;

        for (int i = s.getNumberOfMoves() - 1; i >= 0; i--) {
            if (s.getMove(i) != this.getMove(i))
                return false;
        }

        return true;
    }

}
