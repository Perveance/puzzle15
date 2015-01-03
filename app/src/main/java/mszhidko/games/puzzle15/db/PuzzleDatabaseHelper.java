package mszhidko.games.puzzle15.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import mszhidko.games.puzzle15.Board;
import mszhidko.games.puzzle15.Solution;


/**
 * Created by mikhail on 12/14/14.
 */
public class PuzzleDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "puzzle.sqlite";
    private static final int DB_VERSION = 1;

    private static final String TABLE_PUZZLE  = "puzzle";
    private static final String COLUMN_PUZZLE = "puzzle";
    private static final String COLUMN_SOLUTION_ID = "solution_id";

    private static final String TABLE_SOLUTION     = "solution";
    private static final String COLUMN_SOLUTION    = "solution";
    private static final String COLUMN_MOVES       = "moves";
    private static final String COLUMN_START_BOARD = "start_board";

    public PuzzleDatabaseHelper(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("drop table if exists solution");
        db.execSQL("drop table if exists puzzle");

        db.execSQL("create table solution (" +
                " _id integer primary key autoincrement, start_board varchar(100)," +
                " solution varchar(100), moves integer)");

        db.execSQL("create table puzzle (" +
            "_id integer primary key autoincrement, puzzle varchar(100), " +
            "solution_id integer references solution(_id))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: implement upgrade if needed
    }

    public long insertPuzzle(Board b, long solutionId) {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PUZZLE, b.toString());
        cv.put(COLUMN_SOLUTION_ID, solutionId);
        return getWritableDatabase().insert(TABLE_PUZZLE, null, cv);
    }

    public long insertSolution(Solution s) {


        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SOLUTION, s.toString());
        cv.put(COLUMN_MOVES, String.valueOf(s.getOptMoves()));
        cv.put(COLUMN_START_BOARD, s.getStartBoard().toString());

        return getWritableDatabase().insert(TABLE_SOLUTION, null, cv);
    }

    public PuzzleCursor queryPuzzle() {

        Cursor c = getReadableDatabase().query(TABLE_PUZZLE, null, null, null, null, null, null);
        // query puzzle by

        return new PuzzleCursor(c);
    }

    public static class PuzzleCursor extends CursorWrapper {

        public PuzzleCursor(Cursor c) {
            super(c);
        }

        public Board getPuzzle() {

            int c = getCount();
            Log.i("Mikhail", "--> Count = " + String.valueOf(c));
            if (isBeforeFirst() || isAfterLast())
                return null;

            String boardStr = getString(getColumnIndex(COLUMN_PUZZLE));
            Board b = new Board(boardStr);
            return b;
        }
    }

}
