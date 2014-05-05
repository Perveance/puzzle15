package mszhidko.games.puzzle15;

import mszhidko.games.movingtile.R;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class BoardActivity extends ActionBarActivity {

	static int mDim;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board);
		mDim = getIntent().getIntExtra(GameMenuActivity.PUZZLE_DIMENTION, 3);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.board, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private RelativeLayout mLayout;
		private TileButton[][] mButtons;
		private Board mBoard;
		
		public Board getBoard() {
			return mBoard;
		}

		protected int mBoardWidth;
		protected int mBoardHeight;
		protected int mBoardTop;
		protected int mBoardLeft;
		private boolean isUiInited;
		private int N;
		
		public PlaceholderFragment() {
			//int initial_board[][] = {{6, 1, 3, 12}, {4, 2, 5, 10}, {7, 8, 0, 9 }, {1, 2, 3, 4}};
			
			N = mDim;
			mBoard = new Board(Board.generate_board(N));
			mButtons = new TileButton[N][N];
			
			final Handler toastHandler = new Handler();
			
			new Thread( new Runnable() {
				
				@Override
				public void run() {
					Solver solver = new Solver(mBoard);
					
					final StringBuffer body = new StringBuffer("");
					
				    // print solution to standard output
				    if (!solver.isSolvable()) {
				        body.append("No solution possible");
				    } else {
				        body.append("Minimum number of moves = " + solver.moves() + "\n");
				        //for (Board board : solver.solution())
				        //    body += board.toString() + "\n";
				    }
				    
				    Runnable anotherRunnable = new Runnable() {
						
						public void run() {
							Toast.makeText(	getActivity(), 
											body, 
											Toast.LENGTH_LONG).show();
							}
					};
					
				    toastHandler.post(anotherRunnable);
					
				}
			}).start();
				
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_board,
					container, false);
			
			MultiTouchListener touchListener = new MultiTouchListener(this);
		    rootView.setOnTouchListener(touchListener);
			    
			mLayout = (RelativeLayout) rootView.findViewById(R.id.boardFragment);
			if (mLayout != null) {
				mLayout.setBackgroundColor(0xFF111111);
			}
			
			mLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            @Override
	            public void onGlobalLayout() {
	            	
	            	if (!isUiInited) {
	            		Log.i("Mikhail", "onGlobalLayout called");
	                	initializeUI();
	            	} else {
	            		mLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
	            	}
	            	
	            }
	        });
			
			return rootView;
		}
		
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
		}
		
		protected void initializeUI() {
			if (!isUiInited) {
				
				View v = this.getView();
				mBoardHeight = v.getMeasuredHeight();
		        mBoardWidth = v.getMeasuredWidth();
		        Log.i("Mikhail", "initializeUI; mBoardHeight=" + mBoardHeight + "; mBoardWidth=" + mBoardWidth);
		        
		        int buttonWidth = mBoardWidth / N;
				int buttonHeight = mBoardHeight / N;
				mBoardLeft = v.getLeft();
				mBoardTop = v.getTop();
				
				for (int i = 0; i < N; i++) {
					for (int j = 0; j < N; j++) {
						if(mBoard.get(i, j) == 0) {
							continue;
						}
						
						mButtons[i][j] = new TileButton(getActivity());
						mButtons[i][j].setFrame(v);
						int[] tag = {i, j};
						mButtons[i][j].setTag(tag);
						mButtons[i][j].setText(String.valueOf(mBoard.get(i, j)));
						mButtons[i][j].setBackgroundColor(0xAAFFB10B);
						
						MarginLayoutParams marginParams = new MarginLayoutParams(buttonWidth - 4, buttonHeight - 4);
		                marginParams.setMargins(mBoardLeft + j * buttonWidth, mBoardTop + i * buttonHeight, 0, 0);
		                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
		                
		                mButtons[i][j].setLayoutParams(layoutParams);
		                mLayout.addView(mButtons[i][j]);
			                
					}
				}
				isUiInited = true;
			}
			
		}
		
		public Button[][] getButtons() {
			return mButtons;
		}
		
		public enum Direction {
		    LEFT, RIGHT, UP, DOWN, NONE
		}

	}

}

