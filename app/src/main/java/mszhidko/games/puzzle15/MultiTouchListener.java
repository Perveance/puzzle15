package mszhidko.games.puzzle15;

import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import mszhidko.games.puzzle15.BoardActivity.PuzzleFragment;
import mszhidko.games.puzzle15.BoardActivity.PuzzleFragment.Direction;
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
	private boolean mIsMoving = false;
	private boolean mIsSolved = false;
	
	// This listener is set to a PuzzleGame fragment
	private PuzzleFragment puzzleFragment;
	private TileButton[][] mPuzzleButtons; // The puzzle's board is made out of an array of buttons
	private TileButton mCurButton; // Only one button can be moved at a time
	Direction mDirection; 		// Which direction the button can be moved
	private long mStartTime;	// To distinguish click from move
    private Solution mMovesHistory = new Solution();
    private Solution solution;
	
	public MultiTouchListener(PuzzleFragment boardFragment, Solution s) {

		puzzleFragment = boardFragment;
	    mPuzzleButtons = (TileButton[][]) puzzleFragment.getButtons();
        solution = s;
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
		for (int i = 0; i < mPuzzleButtons.length; i++) {
			for (int j = 0; j < mPuzzleButtons.length; j++) {
				if (mPuzzleButtons[i][j] != null) {
					if (isInsideButton(e, mPuzzleButtons[i][j])) {
						return mPuzzleButtons[i][j];
					}
				}
			}
		}
		
		return null;
	}
	
	private Direction getPotentialDirection() {
		
		Direction d = puzzleFragment.getBoard().getDirection(mCurButton.getI(), mCurButton.getJ());
		
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
	
	private void moveTile(TileButton b, boolean isClick, boolean isBack) {

        if (!isBack) {
            mMovesHistory.pushMove(Integer.valueOf(b.getText().toString()));
        }

    	moveButton(b, isClick, mDirection, isBack);
    	puzzleFragment.updateMoves(); // Update Moves TextEdit
    	
	}
	
	private void moveButton(TileButton button, boolean isClick, Direction dir, boolean isBack) {

        int left = button.getLeft();
        int top = button.getTop();

		switch (dir) {
    	case DOWN:
    		
    		if ((top - mTop) > button.getHeight()/6 || isClick) { // Moving tile
    			
    			boolean ret = puzzleFragment.getBoard().move(button.getI(), button.getJ(), isBack);
    			if (ret) {
    				
    				button.setI(button.getI() + 1);
    				mNewTop = mTop + button.getHeight() + 4;
    				
    			} else {
    				mNewTop = mTop;
    			}
    			
    		} else { // Not moving the tile, just return to original position
    			
    			mNewTop = mTop;
    			
    		}
    		
    		mNewLeft = left;
            
    		break;
    	case UP:
    		
    		if ((mTop - top) > button.getHeight()/6 || isClick) {

    			boolean ret = puzzleFragment.getBoard().move(button.getI(), button.getJ(), isBack);
    			if (ret) {
    				
    				button.setI(button.getI() - 1);
    				mNewTop = mTop - button.getHeight() - 4;
    				
    			} else {
    				mNewTop = mTop;
    			}
    			
    		} else {
    			mNewTop = mTop;
    		}
    		
    		mNewLeft = left;
    		
    		break;
    	case LEFT:
    		
    		if ((mLeft - left) > button.getWidth()/6 || isClick) {
    			
    			
    			boolean ret = puzzleFragment.getBoard().move(button.getI(), button.getJ(), isBack);
    			if (ret) {
    				
    				button.setJ(button.getJ() - 1);
    				mNewLeft = mLeft - button.getWidth() - 4;
    				
    			} else {
    				mNewLeft = mLeft;
    			}
    			
    		} else {
    			mNewLeft = mLeft;
    		}
    		
			mNewTop = top;
			
    		break;
    	case RIGHT:
    		
    		if ((left - mLeft) > button.getWidth()/6 || isClick){
    			
    			boolean ret = puzzleFragment.getBoard().move(button.getI(), button.getJ(), isBack);
    			if (ret) {
    				
    				button.setJ(button.getJ() + 1);
    				mNewLeft = mLeft + button.getWidth() + 4;
    				
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
	
	private int getAnimationDuration(TileButton b, int maxDuration, int dx, int dy) {
		
		int dt = 0;
		
		if (Math.abs(dx) >= Math.abs(dy)) {
			dt = (int) ((float) maxDuration * ( (float) Math.abs(dx) / (float) b.getHeight()));
		} else {
			dt = (int) ((float) maxDuration * ((float) Math.abs(dy) / (float) b.getWidth()));
		}
		
		return dt;
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
	    float dX, dY;
	    
	    if (mIsSolved) {
			return true;
		}	    

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
		        			mIsMoving = true; // Flag that indicates that the button can be moved
		        			mPrevX = event.getX(); // Set original X and Y of the touch event 
				        	mPrevY = event.getY();
	
				            mLeft = mCurButton.getLeft(); // First, save starting position of the button
			                mTop = mCurButton.getTop();   // left and top 
			                
			                mStartTime = Calendar.getInstance().getTimeInMillis(); // Save timestamp, to identify the click
		        		} else { // Button cannot be moved
		        			mIsMoving = false;
		        			mCurButton = null;
		        		}
	
		        	} else { // No button has been touched
		        		//Log.i("Mikhail", "No button has been touched!");
		        		mIsMoving = false;
		        	}
	        	}
	        	
	            break; // Out of ACTION_DOWN
	        }

	        case MotionEvent.ACTION_MOVE:
	        {
	        	
	        	if (mIsMoving == true) {
	        		int leftMargin = 0; /* Of a touched button inside the RelativeLayout */
	        		int topMargin = 0; /* Of a touched button inside the RelativeLayout */
	                
	        		dX = event.getX() - mPrevX; // dx of a touch-event and the touched button 
	                dY = event.getY() - mPrevY; // local variable
	                
	                dX = limitDx(dX); // Limit dX by the width of the button
	                dY = limitDy(dY); // Limit dX by the height of the button
	                
	                leftMargin = mLeft + (int) dX; // change only x if we are moving horizontally
	                topMargin = mTop + (int) dY; // change only y if we are moving vertically
	                
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
	        	
	        	if (mIsMoving == true) {
	        		
		        	int left = mCurButton.getLeft(); // Current position
		        	int top = mCurButton.getTop(); // Current position
		        	
		        	int dx = left - mLeft; // dx that button has traveled since ACTION_DOWN
		        	int dy = top - mTop;   // dy that button has traveled since ACTION_DOWN
		        	long dt = Calendar.getInstance().getTimeInMillis() - mStartTime;
		        	boolean isClick = false;
		        	
		        	// Identify click by duration and distance
		        	if (mDirection != Direction.NONE) {
		        		if (dx < 10 && dy < 10 && dt < 300) {
		        			isClick = true;
		        		}
		        	}
		        	
		        	moveTile(mCurButton, isClick, false); // This method will update mNewLeft & mNewTop
		        	mIsSolved = puzzleFragment.getBoard().isGoal();
		        	
		        	dx = mNewLeft - left; // new dx for animation 
		        	dy = mNewTop - top; // new dy for animation

                    doTileAnimation(mCurButton, dx, dy);
	        		
	                mIsMoving = false;
	        	}
	        	
	            break;
	    }

	    return true;
	}
	
	public void resetButton() {
		
		mCurButton = null;
		
	}
	
	private int calcButtonSpeed(TileButton b, int dx, int dy) {
		
		int distance = (Math.abs(dx) > Math.abs(dy)) ? Math.abs(dx) : Math.abs(dy);
		float speed = (float) b.getWidth() / puzzleFragment.getTileSpeed();
		int duration = (int) (distance / speed);
		
		return duration;
	}
	
	public void back() {
		
		if (mMovesHistory.getNumberOfMoves() != 0 && mCurButton == null) {

			int tile = mMovesHistory.popMove();
            doTileMove(tile, true);
			
		}
		
	}

    public void hint() {

        if (mCurButton != null)
            return;

        int nMove = puzzleFragment.getBoard().getMoves();
        if (nMove == 0 || mMovesHistory.equals(solution.subSolution(nMove))) {

            int tile = solution.getMove(nMove);
            if (tile > 0)
                doTileMove(tile, false);

        } else {

            //
            // TODO: in this case new Solver should be created and new solution found
            //
            Toast.makeText(puzzleFragment.getActivity(),
                    "Your current state is not optimal. Try pressing 'Back' button first, " +
                    "and then pressing hint to show the most optimal solution.",
                    Toast.LENGTH_LONG).show();

        }

    }

    private void doTileMove(int tileIndex, boolean isBack) {

        final TileButton b = getTileButton(tileIndex);
        mCurButton = b;
        Direction d = getTileDirection(tileIndex);

        if (b == null || d.equals(Direction.NONE)) {
            Log.i("Mikhail", "getHintButton returned NULL");
            return;
        }

        Log.i("Mikhail", "Direction = " + d);

        int left = b.getLeft(); // Current position
        mLeft = b.getLeft();    // moveButton needs mLeft
        int top = b.getTop();   // Current position
        mTop = b.getTop();      // moveButton needs mTop
        mDirection = d;
        moveTile(b, true, isBack); // This method will update mNewLeft & mNewTop
        mIsSolved = puzzleFragment.getBoard().isGoal();
        puzzleFragment.updateMoves();

        int dx = mNewLeft - left; // new dx for animation
        int dy = mNewTop - top;   // new dy for animation

        Log.i("Mikhail", "dx = " + dx + "; dy = " + dy);

        doTileAnimation(b, dx, dy);

    }

    void doTileAnimation(Button button, int dx, int dy) {

        final TileButton b = (TileButton) button;
        TranslateAnimation animation = new TranslateAnimation(0, dx, 0, dy);

        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                MarginLayoutParams marginParams = new MarginLayoutParams(b.getWidth(), b.getHeight());
                marginParams.setMargins(mNewLeft, mNewTop, 0, 0);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);

                b.setLayoutParams(layoutParams);
                b.setResetButton();

                if (mIsSolved) {
                    Toast.makeText(puzzleFragment.getActivity(),
                            "Solved!",
                            Toast.LENGTH_LONG).show();

                    MultiTouchListener.this.puzzleFragment.gameOver();

                }

            }

        });

        animation.setDuration(getAnimationDuration(b, calcButtonSpeed(b, dx, dy), dx, dy));
        animation.setFillAfter(false);
        animation.setFillEnabled(true);
        animation.setFillBefore(false);
        b.startAnimation(animation);
    }

    private TileButton getTileButton(int tile2move) {

        for (int i = 0; i < mPuzzleButtons.length; i++) {
            for (int j = 0; j < mPuzzleButtons.length; j++) {
                if (mPuzzleButtons[i][j] != null) {

                    if (tile2move == Integer.valueOf(mPuzzleButtons[i][j].getText().toString())) {
                        return mPuzzleButtons[i][j];
                    }
                }
            }
        }

        return null;
    }

    private Direction getTileDirection(int tile2move) {

        int[] tag1 = new int[2]; /* Current, initial position */
        int[] tag2 = new int[2]; /* Previous position, that we need to go back to */

        final Board b = puzzleFragment.getBoard(); // current board
        for (int i = 0; i < b.dimension(); i++) {
            for (int j = 0; j < b.dimension(); j++) {

                if (b.get(i, j) == tile2move) { // tile 2 move
                    tag1[0] = i;
                    tag1[1] = j;

                }

                if (puzzleFragment.getBoard().get(i, j) == 0) { // destination tile

                    tag2[0] = i;
                    tag2[1] = j;

                }

            }
        }

        Log.i("Mikhail", "Prev: " + tag1[0] + "," + tag1[1] + " New: " + tag2[0] + "," + tag2[1]);

        if (tag1[0] < tag2[0]) {

            return Direction.DOWN;

        } else if (tag1[0] > tag2[0]) {

            return Direction.UP;

        } else if (tag1[1] < tag2[1] ) {

            return Direction.RIGHT;

        } else if (tag1[1] > tag2[1]) {

            return Direction.LEFT;
        }

        return Direction.NONE;
    }

}