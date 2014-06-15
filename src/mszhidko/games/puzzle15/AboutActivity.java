package mszhidko.games.puzzle15;

import mszhidko.games.movingtile.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_screen);
		
		Log.i("Mikhail", "AboutActivity:onCreate2");
		
		TextView aboutPuzzle = (TextView) findViewById(R.id.AboutPuzzleTB);
		aboutPuzzle.setMovementMethod(new ScrollingMovementMethod());
		
		
		
	}

}
