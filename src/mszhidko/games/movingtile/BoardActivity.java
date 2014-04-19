package mszhidko.games.movingtile;

import java.util.Random;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.os.Build;

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

		private FrameLayout mFrame;
		private RelativeLayout mLayout;
		private TileButton mButton;
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_board,
					container, false);
			
			mFrame = (FrameLayout) rootView.findViewById(R.id.frame);
			if (mFrame != null) {
				
				mFrame.setBackgroundColor(0xFFAAAAAA);
				MultiTouchListener touchListener = new MultiTouchListener(this);
			    mFrame.setOnTouchListener(touchListener);
			    
			    /*
			    MarginLayoutParams marginParamsFrame = new MarginLayoutParams(225*3, 100*3);
			    marginParamsFrame.setMargins(50, 50, 0, 0);
			    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParamsFrame);
			    mFrame.setLayoutParams(layoutParams);
			    */

			}
			
			mLayout = (RelativeLayout) rootView.findViewById(R.id.boardFragment);
			if (mLayout != null) {
				mLayout.setBackgroundColor(0xFF111111);
			}
			
			mButton = (TileButton) rootView.findViewById(R.id.tileButton1);
			mButton.setFrame(mFrame);
			/*mButton = new TileButton(getActivity(), mFrame);
			MarginLayoutParams marginParams = new MarginLayoutParams(255, 100);
			int left = mFrame.getLeft();
			int top = mFrame.getTop();
			marginParams.setMargins(left, top, 0, 0);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
            mButton.setLayoutParams(layoutParams);*/
			
		    //mButton.setText("TileButton");
		    //mLayout.addView(mButton);
			
			return rootView;
		}
		
		public Button getButton() {
			return mButton;
		}
		
		public FrameLayout getBoard() {
			return mFrame;
		}
		
		public class MultiTouchListener implements OnTouchListener
		{

			private float mPrevX;
			private float mPrevY;
			private int mLeft;
			private int mTop;
			private boolean mMoving, mMovingHor, mMovingVert;
			public PlaceholderFragment hostFragment;
			
			private int mXmin, mXmax, mYmin, mYmax;
			
			public MultiTouchListener(PlaceholderFragment boardFragment) {
			    hostFragment = boardFragment;
			}
			
			private boolean isInsideButton(MotionEvent e, Button b) {
				
				float x = e.getRawX();
				float y = e.getRawY();
				
				int[] location = new int[2];
				b.getLocationOnScreen(location);
				
				int b_x_min = location[0];
				int b_x_max = b_x_min + b.getWidth();
				int b_y_min = location[1]/*b.getTop()*/;
				int b_y_max = b_y_min + b.getHeight();
				
				if ((x < b_x_max) && (x > b_x_min) && (y > b_y_min) && (y < b_y_max)) {
					return true;
				}
				
				return false;
			}
			
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
			    float dX, dY;
			    
			    int action = event.getAction();
			    switch (action ) {
			        case MotionEvent.ACTION_DOWN: {
		
			        	//Log.i("Mikhail", "ACTION_DOWN: getX="+event.getX() + "; getY=" + event.getY());
			        	//Log.i("Mikhail", "ACTION_DOWN: getRawX="+event.getRawX() + "; getRawY=" + event.getRawY());
			        	
			        	Button b = hostFragment.getButton();
			        	
			        	if (isInsideButton(event, b) == true) {
			        		
			        		//Log.i("Mikhail", "INSIDE");
			        		//Log.i("Mikhail", "left=" + left + "; right=" + right + "; top=" + top + "bottom=" + bottom + "; h="+height+"; w="+width);
			        		mMoving = true;
			        		mPrevX = event.getX();
				        	mPrevY = event.getY();

				            mLeft = b.getLeft();
			                mTop = b.getTop();
			        	} else {
			        		mMoving = false;
			        	}
			        	
			        	mMovingHor = false;
			        	mMovingVert = false;
			        	
			            break;
			        }
		
			        case MotionEvent.ACTION_MOVE:
			        {
		
			        	//Log.i("Mikhail", "ACTION_MOVE: getX="+event.getX() + "; getY=" + event.getY());
			        	//Log.i("Mikhail", "ACTION_MOVE: getRawX="+event.getRawX() + "; getRawY=" + event.getRawY());
			        	
			        	if (mMoving == true) {
			        		int leftMargin = 0; /* Of a button inside a RelativeLayout */
			        		int topMargin = 0; /* Of a button inside a RelativeLayout */
			                
			        		dX = (event.getX() - mPrevX); // dx of a button
			                dY = (event.getY() - mPrevY);
			                
			                // Need to decide if we are moving horizontally or vertically 
			                if ((mMovingHor == false) && (mMovingVert == false)) {
			                	if (Math.abs(dX) >= Math.abs(dY)) { // TODO: what is dX == dY
			                		mMovingHor = true;
			                	} else {
			                		mMovingVert = true;
			                	}
			                }
			                
			                Button b = hostFragment.getButton();
			                
			                MarginLayoutParams marginParams = new MarginLayoutParams(b.getWidth(), b.getHeight());
			                
			                if (mMovingHor) {
			                  leftMargin = mLeft + (int) dX; // change only x if we are moving horizontally
			                  topMargin = mTop;
			                } else if (mMovingVert) {
			                  leftMargin = mLeft;
			                  topMargin = mTop + (int) dY; // change only y if we are moving vertically
			                }
			                
			                int left = view.getLeft(); // get X margin of a Frame view
			                int top = view.getTop();  // get Y margin of a Frame view
			                
			                // Make sure the button doesn't leave the Frame's borders
			                leftMargin = leftMargin < left ? left : leftMargin;
			                topMargin = topMargin < top ? top : topMargin;
			                int bWidth = hostFragment.getButton().getWidth();
			                int bHeight = hostFragment.getButton().getHeight();
			                
			                if (leftMargin + bWidth > left + view.getWidth()) {
			                	leftMargin = left + view.getWidth() - bWidth;
			                }
			                
			                if (topMargin + bHeight > top + view.getHeight()) {
			                	topMargin = top + view.getHeight() - bHeight;
			                }
			                
			                
			                /*
			                Log.i("Mikhail", "x=" + boardLocation[0] + "px; y=" + boardLocation[1] + "px; l=" + leftMargin + 
			                		"; r = " + topMargin);
			                */
			                
			                // Update button's position 
			                marginParams.setMargins(leftMargin, topMargin, 0, 0);
			                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
			                
			                b.setLayoutParams(layoutParams);
			                
			        	}
			        	
			            break;
			        }
		
			        case MotionEvent.ACTION_CANCEL:
			            break;
		
			        case MotionEvent.ACTION_UP:

			        	mMovingHor = false;
			        	mMovingVert = false;
			        	
			            break;
			    }
		
			    return true;
			}

		}

	}
	
	

}
