package mszhidko.games.puzzle15;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import mszhidko.games.movingtile.R;
import mszhidko.games.puzzle15.db.PuzzleDatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GameMenuActivity extends Activity {
	
	public static final String  PUZZLE_DIMENTION = "mszhidko.games.puzzle15.puzzle_dimention";
	public static final String  PUZZLE = "mszhidko.games.puzzle15.puzzle";

    private PuzzleDatabaseHelper mHelper;



	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_menu);

        final Map difficultyMap = new HashMap<Integer, Integer>();

        difficultyMap.put(R.id.easyPuzzleButton, 2);
        difficultyMap.put(R.id.normalPuzzleButton, 3);
        difficultyMap.put(R.id.difficultPuzzleButton, 4);
        difficultyMap.put(R.id.veryDifPuzzleButton, 5);

        Set<Integer> idSet = difficultyMap.keySet();
        for (final Integer id : idSet) {
            Button b = (Button) findViewById(id);
            b.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent puzzleIntent = new Intent(GameMenuActivity.this,	BoardActivity.class);

                    puzzleIntent.putExtra(PUZZLE_DIMENTION, (int) difficultyMap.get(id));
                    Puzzle p = loadPuzzle((int) difficultyMap.get(id));
                    puzzleIntent.putExtra(PUZZLE, p.getStartBoard());

                    startActivity(puzzleIntent);

                }
            });
        }

        mHelper = new PuzzleDatabaseHelper(getApplicationContext());
        populatePuzzlesDB();  // Read puzzles from JSON file and create SQLite DB
        mHelper.createDataBase(); // Copy SQLite DB from assets to app's databases folder
	}
	
	public void onAbout(View v) {
		
		Log.i("Mikhail", "About button pressed");
		Intent i = new Intent(this, AboutActivity.class);
		startActivity(i);
		
	}
	
	public void onSettings(View v) {
		
		Log.i("Mikhail", "Settings button pressed");
		Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
		
	}
	
	public void onExit(View v) {
				finish();
	}

    Puzzle loadPuzzle(int dimension) {
        PuzzleDatabaseHelper.PuzzleCursor pc = mHelper.queryPuzzle(dimension);
        int nOfPuzzles = pc.getCount();
        int position = (int)(Math.random() * nOfPuzzles);
        Log.i("Mikhail", "position=" + position + "; nOfPuzzles=" + nOfPuzzles);
        pc.moveToPosition(position);
        //pc.moveToLast();
        Puzzle p = pc.getPuzzle();
        return p;
    }

    void populatePuzzlesDB() {

        mHelper.createNewDataBase();

        // Read boards from JSON file and save to sqlite db.
        // TODO: This shouldn't be done in shipped app. It should be done by helper application once
        // to create db. App will be shipped with sqlite db.
        BufferedReader reader = null;

        try {

            AssetManager am = getResources().getAssets();
            InputStream in = am.open("puzzles/boards.json", MODE_PRIVATE);

            reader = new BufferedReader(new InputStreamReader(in));

            StringBuilder jsonString = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONObject obj = (JSONObject) new JSONTokener(jsonString.toString()).nextValue();
            JSONArray boardsJSON = (JSONArray) obj.get("boards");

            for (int i = 0; i < boardsJSON.length(); i++) {

                JSONObject b = (JSONObject) boardsJSON.get(i);
                String boardStr = (String) b.get("tiles");
                int optimalSolution = b.getInt("moves");
                Log.i("Mikhail", "\nboard = " + boardStr + "solution = " + optimalSolution);

                Board newBoard = new Board(boardStr);
                newBoard.setOptimalSolution( (int) optimalSolution);
                Puzzle.Solution s = new Puzzle.Solution();

                Puzzle p = new Puzzle(newBoard, s);
                long bId = mHelper.insertPuzzle(newBoard, p);
                Log.i("Mikhail", "boardId = " + bId);

            }

        } catch (IOException e) {

            Log.i("Mikhail", "READ: error");

        } catch (JSONException e) {

            Log.i("Mikhail", "JSON exception");

        }
    }
}