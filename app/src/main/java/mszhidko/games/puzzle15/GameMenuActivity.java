package mszhidko.games.puzzle15;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
	
	private ArrayList<Board> board2l = new ArrayList<Board>();
	private ArrayList<Board> board3l = new ArrayList<Board>();
	private ArrayList<Board> board4l = new ArrayList<Board>();
	private ArrayList<Board> board5l = new ArrayList<Board>();

    private PuzzleDatabaseHelper mHelper;

	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_menu);
		
		Button easyB = (Button) findViewById(R.id.easyPuzzleButton);
		
		easyB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent puzzleIntent = new Intent(GameMenuActivity.this,	BoardActivity.class);
				
				puzzleIntent.putExtra(PUZZLE_DIMENTION, 2);
				
				int ind = (int) (Math.random() * board2l.size());
				Log.i("Mikhail", "Length = " + board2l.size());
				puzzleIntent.putExtra(PUZZLE, board2l.get(ind));
				
				startActivity(puzzleIntent);
				
			}
		});
		
		Button normalB = (Button) findViewById(R.id.normalPuzzleButton);
		
		normalB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent puzzleIntent = new Intent(GameMenuActivity.this,	BoardActivity.class);
				
				puzzleIntent.putExtra(PUZZLE_DIMENTION, 3);
				//int ind = (int) (Math.random() * board3l.size());
                Board b = testLoadBoard();
				puzzleIntent.putExtra(PUZZLE, b);
				
				startActivity(puzzleIntent);
				
			}
		});
		
		Button difB = (Button) findViewById(R.id.difficultPuzzleButton);
		
		difB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent puzzleIntent = new Intent(GameMenuActivity.this,	BoardActivity.class);
				
				puzzleIntent.putExtra(PUZZLE_DIMENTION, 4);
				int ind = (int) (Math.random() * board4l.size());
				puzzleIntent.putExtra(PUZZLE, board4l.get(ind));
				
				startActivity(puzzleIntent);
				
			}
		});
		
		Button veryDifB = (Button) findViewById(R.id.veryDifPuzzleButton);
		
		veryDifB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent puzzleIntent = new Intent(GameMenuActivity.this,	BoardActivity.class);
				
				puzzleIntent.putExtra(PUZZLE_DIMENTION, 5);
				int ind = (int) (Math.random() * board5l.size());
				puzzleIntent.putExtra(PUZZLE, board5l.get(ind));
				
				startActivity(puzzleIntent);
				
			}
		});

		// Read boards. This should be done in background thread
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
				int optimalSolution = b.getInt("solution");
				Log.i("Mikhail", "\nboard = " + boardStr + "solution = " + optimalSolution);

				Board newBoard = new Board(boardStr);
				newBoard.setOptimalSolution( (int) optimalSolution);

				switch (newBoard.dimension()) {
				case 2:
					board2l.add(newBoard);
					break;
				case 3:
					board3l.add(newBoard);
					break;
				case 4:
					board4l.add(newBoard);
					break;
				case 5:
					board5l.add(newBoard);
					break;
				default:

				}

			}

		} catch (IOException e) {
			
			Log.i("Mikhail", "READ: error");
			
		} catch (JSONException e) {
			
			Log.i("Mikhail", "JSON exception");
			
		}

        mHelper = new PuzzleDatabaseHelper(getApplicationContext());
        testStoreBoards();
        testLoadBoard();
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

    void testStoreBoards() {

        int[][] blocks = {{1, 2, 3}, {4, 5, 6}, {7, 0, 8}};
        Board b = new Board(blocks);
        Solution s = new Solution(b);

        long sId = mHelper.insertSolution(s);
        Log.i("Mikhail", "solutionId = " + sId);

        long bId = mHelper.insertPuzzle(b, sId);
        Log.i("Mikhail", "boardId = " + bId);
    }

    Board testLoadBoard() {
        PuzzleDatabaseHelper.PuzzleCursor pc = mHelper.queryPuzzle();
        pc.moveToFirst();
        Board b = pc.getPuzzle();
        return b;
    }
}