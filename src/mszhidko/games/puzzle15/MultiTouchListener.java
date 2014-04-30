package mszhidko.games.puzzle15;

import java.util.Calendar;

import mszhidko.games.puzzle15.BoardActivity.PlaceholderFragment;
import mszhidko.games.puzzle15.BoardActivity.PlaceholderFragment.Direction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MultiTouchListener implements OnTouchListener
{

	private float mPrevX;
	private float mPrevY;
	private int mLeft, mNewLeft;
	private int mTop, mNewTop;
	private boolean mMoving;
	private PlaceholderFragment hostFragment;
	private TileButton[][] mTileButtons;
	private TileButton mCurButton; 
	Direction mDirection;
	private long mStartTime;
	
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
    		
    		if ((top - mTop) > mCurButton.getHeight()/4 || isClick) { // Moving tile
    			
    			hostFragment.getBoard().move(mCurButton.getI(), mCurButton.getJ());
    			mCurButton.setI(mCurButton.getI() + 1);
    			mNewTop = mTop + mCurButton.getHeight() + 4;
    			
    		} else { // Not moving the tile, just return to original position
    			
    			mNewTop = mTop;
    			
    		}
    		
    		mNewLeft = left;
            
    		break;
    	case UP:
    		
    		if ((mTop - top) > mCurButton.getHeight()/4 || isClick) {

    			hostFragment.getBoard().move(mCurButton.getI(), mCurButton.getJ());
    			mCurButton.setI(mCurButton.getI() - 1);
    			mNewTop = mTop - mCurButton.getHeight() - 4;
    			
    		} else {
    			mNewTop = mTop;
    		}
    		
    		mNewLeft = left;
    		
    		break;
    	case LEFT:
    		
    		if ((mLeft - left) > mCurButton.getWidth()/4 || isClick) {
    			
    			hostFragment.getBoard().move(mCurButton.getI(), mCurButton.getJ());
    			mCurButton.setJ(mCurButton.getJ() - 1);
    			mNewLeft = mLeft - mCurButton.getWidth() - 4;
    			
    		} else {
    			mNewLeft = mLeft;
    		}
    		
			mNewTop = top;
			
    		break;
    	case RIGHT:
    		
    		if ((left - mLeft) > mCurButton.getWidth()/4 || isClick){
    			
    			hostFragment.getBoard().move(mCurButton.getI(), mCurButton.getJ());
    			mCurButton.setJ(mCurButton.getJ() + 1);
    			mNewLeft = mLeft + mCurButton.getWidth() + 4;
    		
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
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
	    float dX, dY;
	    
	    int action = event.getAction();
	    switch (action ) {
	        case MotionEvent.ACTION_DOWN: {

	        	if (mCurButton == null) {
	        		// No animation is going on
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
			                
			                mStartTime = Calendar.getInstance().getTimeInMillis();
		        		} else {
		        			mMoving = false;
		        		}
	
		        	} else { // No button has been touched
		        		Log.i("Mikhail", "No button has been touched!");
		        		mMoving = false;
		        	}
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
		        	//int startX = left;
		        	
		        	int top = mCurButton.getTop();
		        	//int startY = top;
		        	
		        	int dx = left - mLeft;
		        	int dy = top - mTop;
		        	long dt = Calendar.getInstance().getTimeInMillis() - mStartTime;
		        	boolean isClick = false;
		        	
		        	TranslateAnimation animation = null;
		        	
		        	// Identify click by duration and distance
		        	if (mDirection != Direction.NONE) {
		        		if (dx < 5 && dy < 5 && dt < 400) {
		        			isClick = true;
		        		}
		        	}
		        	
		        	moveTile(left, top, isClick);
		        	
		        	animation = new TranslateAnimation(0, mNewLeft - left, 0, mNewTop - top);
		        	
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
		                
		                	Log.i("Mikhail", "onAnimationEnd! mTop=" + mTop + "; mNewTop=" + mNewTop);
		                	mCurButton.setLayoutParams(layoutParams);
		                	mCurButton = null;
						}
					});
	        		
	        		animation.setDuration(200); // duartion in ms TODO: Duration should be proportional to distance
	        		animation.setFillAfter(false);
	        		animation.setFillEnabled(true);
	        		animation.setFillBefore(false);
	        		mCurButton.startAnimation(animation);
		        		
	                mMoving = false;
	        	}
	        	
	        	//mCurButton = null;
	        	Log.i("Mikhail", "onTouch finished; mTop=" + mTop + "; mNewTop=" + mNewTop);
	       
	            break;
	    }

	    return true;
	}

}
