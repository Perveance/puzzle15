package mszhidko.games.puzzle15;

import java.util.Random;

import mszhidko.games.movingtile.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class BoardActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board);

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
		
		protected int mBoardWidth;
		protected int mBoardHeight;
		protected int mBoardTop;
		protected int mBoardLeft;
		private boolean isUiInited;
		private int N;
		
		public PlaceholderFragment() {
			//int initial_board[][] = {{6, 1, 3, 12}, {4, 2, 5, 10}, {7, 8, 0, 9 }, {1, 2, 3, 4}};
			
			N = 3;
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
		
		public class MultiTouchListener implements OnTouchListener
		{

			private float mPrevX;
			private float mPrevY;
			private int mLeft;
			private int mTop;
			private boolean mMoving;
			private PlaceholderFragment hostFragment;
			private TileButton[][] mTileButtons= new TileButton[N][N];
			private TileButton mCurButton; 
			Direction mDirection;
			
			public MultiTouchListener(PlaceholderFragment boardFragment) {
			    hostFragment = boardFragment;
			    mTileButtons = (TileButton[][]) hostFragment.getButtons();
			}
			
			private boolean isInsideButton(MotionEvent e, Button b) {
				
				float x = e.getRawX();
				float y = e.getRawY();
				
				int[] location = new int[2];
				b.getLocationOnScreen(location);
				
				int b_x_min = location[0];
				int b_x_max = b_x_min + b.getWidth();
				int b_y_min = location[1];
				int b_y_max = b_y_min + b.getHeight();
				
				if ((x < b_x_max) && (x > b_x_min) && (y > b_y_min) && (y < b_y_max)) {
					return true;
				}
				
				return false;
			}
			
			private Button getTouchedButton(MotionEvent e) {
				for (int i = 0; i < mTileButtons.length; i++) {
					for (int j = 0; j < mTileButtons.length; j++) {
						if (mTileButtons[i][j] != null) {
							if (isInsideButton(e, mTileButtons[i][j])) {
								return mTileButtons[i][j];
							}
						}
					}
				}
				
				return null;
			}
			
			private Direction getPotentialDirection() {
				
				Direction d = mBoard.getDirection(mCurButton.getI(), mCurButton.getJ());
				
				return d;
			
			}
			
			private float limitDx(float dX) {
				
				switch (mDirection) {
				case RIGHT:
					if (dX > mCurButton.getWidth()) {
	                	dX = mCurButton.getWidth() + 4;
	                }
					
					if (dX < 0)
						dX = 0;
					
					break;
				case LEFT:
					if (dX < ((-1) * mCurButton.getWidth())) {
						dX = -mCurButton.getWidth() - 4;
					}
					
					if (dX > 0)
						dX = 0;
					
					break;
					
				case UP:
				case DOWN:
					dX = 0;
					break;
				default:
					assert false;
				}
				
				return dX;
			}
			
			private float limitDy(float dY) {
				
				switch (mDirection) {
				case LEFT:
				case RIGHT:
					dY = 0;
					break;
					
				case UP:
					if (dY < -mCurButton.getHeight())
						dY = -mCurButton.getHeight() - 4;
					
					if (dY > 0)
						dY = 0;
					
					break;
					
				case DOWN:
					if (dY > mCurButton.getHeight())
						dY = mCurButton.getHeight() + 4;
					
					if (dY < 0)
						dY = 0;
					
					break;
					
				}
				
				return dY;
			}
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
			    float dX, dY;
			    
			    int action = event.getAction();
			    switch (action ) {
			        case MotionEvent.ACTION_DOWN: {
		
			        	mCurButton = (TileButton) getTouchedButton(event);
			        	
			        	if (mCurButton != null) { // Button has been touched
			        		
			        		mDirection = getPotentialDirection();
			        		Log.i("Mikhail", "Direction = " + mDirection);
			        		if (mDirection != Direction.NONE) { // TODO: merge this if with if (mCurButton != null)
			        			mMoving = true;
			        			mPrevX = event.getX();
					        	mPrevY = event.getY();
	
					            mLeft = mCurButton.getLeft();
				                mTop = mCurButton.getTop();
			        		} else {
			        			mMoving = false;
			        		}

			        	} else { // No button has been touched
			        		Log.i("Mikhail", "No button has been touched!");
			        		mMoving = false;
			        	}
			        	
			            break;
			        }
		
			        case MotionEvent.ACTION_MOVE:
			        {
			        	
			        	if (mMoving == true) {
			        		int leftMargin = 0; /* Of a touched button inside the RelativeLayout */
			        		int topMargin = 0; /* Of a touched button inside the RelativeLayout */
			                
			        		dX = (event.getX() - mPrevX); // dx of a button
			                dY = (event.getY() - mPrevY);
			                
			                dX = limitDx(dX);
			                dY = limitDy(dY);
			                
			                leftMargin = mLeft + (int) dX; // change only x if we are moving horizontally
			                topMargin = mTop + (int) dY; // change only y if we are moving vertically
			                
			                int left = view.getLeft(); // get X margin of a Frame view
			                int top = view.getTop();  // get Y margin of a Frame view
			                
			                // Make sure the button doesn't leave the Frame's borders
			                leftMargin = leftMargin < left ? left : leftMargin;
			                topMargin = topMargin < top ? top : topMargin;
			                int bWidth = mCurButton.getWidth();
			                int bHeight = mCurButton.getHeight();
			                
			                if (leftMargin + bWidth > left + view.getWidth()) {
			                	leftMargin = left + view.getWidth() - bWidth;
			                }
			                
			                if (topMargin + bHeight > top + view.getHeight()) {
			                	topMargin = top + view.getHeight() - bHeight;
			                }
			                
			                // Update button's position
			                MarginLayoutParams marginParams = new MarginLayoutParams(mCurButton.getWidth(), mCurButton.getHeight());
			                marginParams.setMargins(leftMargin, topMargin, 0, 0);
			                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
			                
			                mCurButton.setLayoutParams(layoutParams);
			                
			        	}
			        	
			            break;
			        }
		
			        case MotionEvent.ACTION_CANCEL:

			        	mCurButton = null;
			        	
			            break;
		
			        case MotionEvent.ACTION_UP:

			        	if (mMoving == true) {
				        	int left = mCurButton.getLeft();
				        	int top = mCurButton.getTop();
				        	
				        	switch (mDirection) {
				        	case DOWN:
				        		
				        		if ((top - mTop) > mCurButton.getHeight()/4) {
				        			mBoard.move(mCurButton.getI(), mCurButton.getJ());
				        			mCurButton.setI(mCurButton.getI() + 1);
				        			top = mTop + mCurButton.getHeight() + 4;
				        		} else {
				        			top = mTop;
				        		}
				                
				        		break;
				        	case UP:
				        		
				        		if ((mTop - top) > mCurButton.getHeight()/4) {
				        			mBoard.move(mCurButton.getI(), mCurButton.getJ());
				        			mCurButton.setI(mCurButton.getI() - 1);
				        			top = mTop - mCurButton.getHeight() - 4;
				        		} else {
				        			top = mTop;
				        		}
				        		
				        		break;
				        	case LEFT:
				        		
				        		if ((mLeft - left) > mCurButton.getWidth()/4) {
				        			mBoard.move(mCurButton.getI(), mCurButton.getJ());
				        			mCurButton.setJ(mCurButton.getJ() - 1);
				        			left = mLeft - mCurButton.getWidth() - 4;
				        		} else {
				        			left = mLeft;
				        		}
				        		break;
				        	case RIGHT:
				        		
				        		if ((left - mLeft) > mCurButton.getWidth()/4){
				        			mBoard.move(mCurButton.getI(), mCurButton.getJ());
				        			mCurButton.setJ(mCurButton.getJ() + 1);
				        			left = mLeft + mCurButton.getWidth() + 4;
				        		} else {
				        			left = mLeft;
				        		}
				        		break;
				        	}
				        	
				        	MarginLayoutParams marginParams = new MarginLayoutParams(mCurButton.getWidth(), mCurButton.getHeight());
			                marginParams.setMargins(left, top, 0, 0);
			                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
			                
			                mCurButton.setLayoutParams(layoutParams);
			                mMoving = false;
			        	}
			        	
			        	mCurButton = null;
			        	
			        	
			            break;
			    }
		
			    return true;
			}

		}

	}

}

