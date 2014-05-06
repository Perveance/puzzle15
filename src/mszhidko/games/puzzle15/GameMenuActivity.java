package mszhidko.games.puzzle15;

import mszhidko.games.movingtile.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GameMenuActivity extends Activity {
	
	public static final String  PUZZLE_DIMENTION = "mszhidko.games.puzzle15.puzzle_dimention";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_menu);
		
		Button easyB = (Button) findViewById(R.id.easyPuzzleButton);
		
		easyB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent puzzleIntent = new Intent(GameMenuActivity.this,	BoardActivity.class);
				
				puzzleIntent.putExtra(PUZZLE_DIMENTION, 2);
				
				startActivity(puzzleIntent);
				
			}
		});
		
		Button normalB = (Button) findViewById(R.id.normalPuzzleButton);
		
		normalB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent puzzleIntent = new Intent(GameMenuActivity.this,	BoardActivity.class);
				
				puzzleIntent.putExtra(PUZZLE_DIMENTION, 3);
				
				startActivity(puzzleIntent);
				
			}
		});
		
		Button difB = (Button) findViewById(R.id.difficultPuzzleButton);
		
		difB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent puzzleIntent = new Intent(GameMenuActivity.this,	BoardActivity.class);
				
				puzzleIntent.putExtra(PUZZLE_DIMENTION, 4);
				
				startActivity(puzzleIntent);
				
			}
		});
		
		Button veryDifB = (Button) findViewById(R.id.veryDifPuzzleButton);
		
		veryDifB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent puzzleIntent = new Intent(GameMenuActivity.this,	BoardActivity.class);
				
				puzzleIntent.putExtra(PUZZLE_DIMENTION, 5);
				
				startActivity(puzzleIntent);
				
			}
		});
		
		Button exit = (Button) findViewById(R.id.exitButton);
		
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Log.i("Mikhail", "onClick: calling finish()");
				finish();
				
			}
		});
	}

}
