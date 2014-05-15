package mszhidko.games.puzzle15;

import java.util.Calendar;

import mszhidko.games.puzzle15.BoardActivity.PlaceholderFragment;
import mszhidko.games.puzzle15.BoardActivity.PlaceholderFragment.Direction;
import android.app.Dialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MultiTouchListener implements OnTouchListener
{

	private float mPrevX;
	private float mPrevY;
	private int mLeft, mNewLeft;
	private int mTop, mNewTop;
	private boolean mMoving;
	private boolean mIsSolved = false;
	
	// This listener is set to a PuzzleGame fragment
	private PlaceholderFragment hostFragment;
	private TileButton[][] mTileButtons;
	private TileButton mCurButton; // Only one button can be moved at a time
	Direction mDirection; 		// Which direction the button can be moved
	private long mStartTime;	// To distinguish click from move
	final int ANIMATION_SPEED = 60;
	
	// Used to detect flings
	//private GestureDetector mGestureDetector;
	
	public MultiTouchListener(PlaceholderFragment boardFragment) {

		hostFragment = boardFragment;
	    mTileButtons = (TileButton[][]) hostFragment.getButtons();
	    
	    /*mGestureDetector = new GestureDetector(hostFragment.getActivity(),
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						
						//
						// TODO: implement proper swipe detection and handling
						//
						
						
						Log.i("Mikhail", "    <<< GestureDetector: onFling! >>>");
						return false;
					}
				});*/
	    
	}
	
	// Check whether a button was clicked
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
		
		Direction d = hostFragment.getBoard().getDirection(mCurButton.getI(), mCurButton.getJ());
		
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
		case NONE:
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
	
	private void moveTile(int left, int top, boolean isClick) {
		
    	switch (mDirection) {
    	case DOWN:
    		
    		if ((top - mTop) > mCurButton.getHeight()/6 || isClick) { // Moving tile
    			
    			boolean ret = hostFragment.getBoard().move(mCurButton.getI(), mCurButton.getJ());
    			if (ret) {
    				hostFragment.moveForward();
    				mCurButton.setI(mCurButton.getI() + 1);
    				mNewTop = mTop + mCurButton.getHeight() + 4;
    			} else {
    				mNewTop = mTop;
    			}
    			
    		} else { // Not moving the tile, just return to original position
    			
    			mNewTop = mTop;
    			
    		}
    		
    		mNewLeft = left;
            
    		break;
    	case UP:
    		
    		if ((mTop - top) > mCurButton.getHeight()/6 || isClick) {

    			boolean ret = hostFragment.getBoard().move(mCurButton.getI(), mCurButton.getJ());
    			if (ret) {
    				hostFragment.moveForward();
    				mCurButton.setI(mCurButton.getI() - 1);
    				mNewTop = mTop - mCurButton.getHeight() - 4;
    			} else {
    				mNewTop = mTop;
    			}
    			
    		} else {
    			mNewTop = mTop;
    		}
    		
    		mNewLeft = left;
    		
    		break;
    	case LEFT:
    		
    		if ((mLeft - left) > mCurButton.getWidth()/6 || isClick) {
    			
    			
    			boolean ret = hostFragment.getBoard().move(mCurButton.getI(), mCurButton.getJ());
    			if (ret) {
    				hostFragment.moveForward();
    				mCurButton.setJ(mCurButton.getJ() - 1);
    				mNewLeft = mLeft - mCurButton.getWidth() - 4;
    			} else {
    				mNewLeft = mLeft;
    			}
    			
    		} else {
    			mNewLeft = mLeft;
    		}
    		
			mNewTop = top;
			
    		break;
    	case RIGHT:
    		
    		if ((left - mLeft) > mCurButton.getWidth()/6 || isClick){
    			
    			boolean ret = hostFragment.getBoard().move(mCurButton.getI(), mCurButton.getJ());
    			if (ret) {
    				hostFragment.moveForward();
    				mCurButton.setJ(mCurButton.getJ() + 1);
    				mNewLeft = mLeft + mCurButton.getWidth() + 4;
    			} else {
    				mNewLeft = mLeft;
    			}
    		
    		} else {
    			mNewLeft = mLeft;
    		}
    		
    		mNewTop = top;
    		
    		break;
    		
    	case NONE:
    		
    		mNewLeft = left;
    		mNewTop = top;
    		
    	}
    	
	}
	
	private int getAnimationDuration(int maxDuration, int dx, int dy) {
		
		int dt = 0;
		
		if (Math.abs(dx) >= Math.abs(dy)) {
			dt = (int) ((float) maxDuration * ( (float) Math.abs(dx) / (float) mCurButton.getHeight()));
		} else {
			dt = (int) ((float) maxDuration * ((float) Math.abs(dy) / (float) mCurButton.getWidth()));
		}
		
		return dt;
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
	    float dX, dY;
	    
	    if (mIsSolved) { /* */
			return true;
		}	    
	    
	    //mGestureDetector.onTouchEvent(event);
	    
	    int action = event.getAction();
	    switch (action) {
	        case MotionEvent.ACTION_DOWN: {

	        	if (mCurButton == null) {
	        		// No animation is going on
		        	mCurButton = (TileButton) getTouchedButton(event);
		        	
		        	if (mCurButton != null) { // Button has been touched
		        		
		        		mDirection = getPotentialDirection();
		        		//Log.i("Mikhail", "Direction = " + mDirection);
		        		if (mDirection != Direction.NONE) {
		        			mMoving = true; // Flag that indicates that the button can be moved
		        			mPrevX = event.getX(); // Set original X and Y of the touch event 
				        	mPrevY = event.getY();
	
				            mLeft = mCurButton.getLeft(); // First, save starting position of the button
			                mTop = mCurButton.getTop();   // left and top 
			                
			                mStartTime = Calendar.getInstance().getTimeInMillis(); // Save timestamp, to identify the click
		        		} else { // Button cannot be moved
		        			mMoving = false;
		        			mCurButton = null;
		        		}
	
		        	} else { // No button has been touched
		        		//Log.i("Mikhail", "No button has been touched!");
		        		mMoving = false;
		        	}
	        	}
	        	
	            break; // Out of ACTION_DOWN
	        }

	        case MotionEvent.ACTION_MOVE:
	        {
	        	
	        	if (mMoving == true) {
	        		int leftMargin = 0; /* Of a touched button inside the RelativeLayout */
	        		int topMargin = 0; /* Of a touched button inside the RelativeLayout */
	                
	        		dX = event.getX() - mPrevX; // dx of a touch-event and the touched button 
	                dY = event.getY() - mPrevY; // local variable
	                
	                dX = limitDx(dX); // Limit dX by the width of the button
	                dY = limitDy(dY); // Limit dX by the height of the button
	                
	                leftMargin = mLeft + (int) dX; // change only x if we are moving horizontally
	                topMargin = mTop + (int) dY; // change only y if we are moving vertically
	                
	                //int left = view.getLeft(); // get X margin of a Frame view
	                //int top = view.getTop();  // get Y margin of a Frame view
	                
	                // Make sure the button doesn't leave the Frame's borders
	                //leftMargin = leftMargin < left ? left : leftMargin;
	                //topMargin = topMargin < top ? top : topMargin;
	                
	                // Update button's position
	                MarginLayoutParams marginParams = new MarginLayoutParams(mCurButton.getWidth(), mCurButton.getHeight());
	                marginParams.setMargins(leftMargin, topMargin, 0, 0);
	                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
	                
	                mCurButton.setLayoutParams(layoutParams);
	                
	        	}
	        	
	            break;
	        }

	        case MotionEvent.ACTION_CANCEL:

	        case MotionEvent.ACTION_UP:
	        	
	        	if (mMoving == true) {
	        		
		        	int left = mCurButton.getLeft(); // Current position
		        	int top = mCurButton.getTop(); // Current position
		        	
		        	int dx = left - mLeft; // dx that button has traveled since ACTION_DOWN
		        	int dy = top - mTop;   // dy that button has traveled since ACTION_DOWN
		        	long dt = Calendar.getInstance().getTimeInMillis() - mStartTime;
		        	boolean isClick = false;
		        	
		        	TranslateAnimation animation = null;
		        	
		        	// Identify click by duration and distance
		        	if (mDirection != Direction.NONE) {
		        		if (dx < 10 && dy < 10 && dt < 300) {
		        			isClick = true;
		        		}
		        	}
		        	
		        	moveTile(left, top, isClick); // This method will update mNewLeft & mNewTop
		        	mIsSolved = hostFragment.getBoard().isGoal();
		        	
		        	dx = mNewLeft - left; // new dx for animation 
		        	dy = mNewTop - top; // new dy for animation
		        	animation = new TranslateAnimation(0, dx, 0, dy);
		        	
	        		animation.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							
							MarginLayoutParams marginParams = new MarginLayoutParams(mCurButton.getWidth(), mCurButton.getHeight());
		                	marginParams.setMargins(mNewLeft, mNewTop, 0, 0);
		                	RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
		                
		                	mCurButton.setLayoutParams(layoutParams);
		                	mCurButton.setResetButton();
		                	
		                	if (mIsSolved) {
		                		Toast.makeText(hostFragment.getActivity(), 
									           "Solved!", 
									           Toast.LENGTH_LONG).show();
		                		
		                		MultiTouchListener.this.hostFragment.gameOver();
		                		
		                	}
						}
						
					});
	        		
	        		animation.setDuration(getAnimationDuration(calcButtonSpeed(dx, dy), dx, dy));
	        		animation.setFillAfter(false);
	        		animation.setFillEnabled(true);
	        		animation.setFillBefore(false);
	        		mCurButton.startAnimation(animation);
	        		
	                mMoving = false;
	        	}
	        	
	            break;
	    }

	    return true;
	}
	
	public void resetButton() {
		
		mCurButton = null;
		
	}
	
	private int calcButtonSpeed(int dx, int dy) {
		
		int distance = (Math.abs(dx) > Math.abs(dy)) ? Math.abs(dx) : Math.abs(dy);
		int speed = mCurButton.getWidth() / ANIMATION_SPEED;
		int duration = distance / speed;
		
		return duration;
	}

}
