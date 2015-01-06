package mszhidko.games.puzzle15.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import mszhidko.games.puzzle15.Board;
import mszhidko.games.puzzle15.Puzzle;
import mszhidko.games.puzzle15.Puzzle.Solution;


/**
 * Created by mikhail on 12/14/14.
 */
public class PuzzleDatabaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "";
    private static final String DB_NAME = "puzzle.sqlite";
    private static final int DB_VERSION = 1;

    private static final String TABLE_PUZZLE  = "puzzle";
    private static final String COLUMN_PUZZLE = "puzzle";
    private static final String COLUMN_SOLUTION    = "solution";
    private static final String COLUMN_MOVES       = "moves";
    private static final String COLUMN_DIMENSION   = "dimension";

    private final Context mContext;
    private SQLiteDatabase mDataBase;

    public PuzzleDatabaseHelper(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = c.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + c.getPackageName() + "/databases/";
        }
        this.mContext = c;
    }

    public void createNewDataBase() {

        openDataBase();
        mDataBase.execSQL("drop table if exists puzzle");

        mDataBase.execSQL("create table puzzle (" +
            "_id integer primary key autoincrement, puzzle varchar(100), " +
            "solution varchar(100), moves integer, dimension integer)");

    }

    public void createDataBase() {

        Log.i("Mikhail", " --> onCreate PuzzleDatabaseHelper ENTER");

        if(!isDataBaseExists())
        {
            this.getReadableDatabase();
            this.close();
            try {
                //Copy the database from assests
                copyDataBase();
                Log.e("Mikhail", "createDatabase database created");
            }
            catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }

        Log.i("Mikhail", " --> onCreate PuzzleDatabaseHelper EXIT");

    }

    private boolean isDataBaseExists()
    {
        File dbFile = new File(DB_PATH + DB_NAME);
        boolean isExist = dbFile.exists();
        return isExist;
    }

    // Copy the database from assets
    private void copyDataBase() throws IOException
    {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    //Open the database, so we can query it
    public boolean openDataBase() throws SQLException
    {
        String mPath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    @Override
    public synchronized void close()
    {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: implement upgrade if needed
    }

    public long insertPuzzle(Board b, Puzzle p) {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PUZZLE, b.toString());
        cv.put(COLUMN_SOLUTION, p.toString());
        cv.put(COLUMN_MOVES, p.getOptMoves());
        cv.put(COLUMN_DIMENSION, b.dimension());
        return getWritableDatabase().insert(TABLE_PUZZLE, null, cv);
    }

    public PuzzleCursor queryPuzzle(int dim) {

        // query puzzle where dimension = dim
        String where = COLUMN_DIMENSION + " = ?";
        String[] whereArg = new String[] {String.valueOf(dim)};
        Cursor c = getReadableDatabase().query(TABLE_PUZZLE, null, where, whereArg, null, null, null);

        return new PuzzleCursor(c);
    }

    public static class PuzzleCursor extends CursorWrapper {

        public PuzzleCursor(Cursor c) {
            super(c);
        }

        public Puzzle getPuzzle() {

            int c = getCount();
            Log.i("Mikhail", "--> Count = " + String.valueOf(c));
            if (isBeforeFirst() || isAfterLast())
                return null;

            String boardStr = getString(getColumnIndex(COLUMN_PUZZLE));
            Board b = new Board(boardStr);
            Solution s = new Solution();
            int N = getInt(getColumnIndex(COLUMN_MOVES));
            b.setOptimalSolution(N);

            Puzzle p = new Puzzle(b, s);

            return p;
        }
    }

}
