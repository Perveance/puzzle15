package mszhidko.games.puzzle15;

import mszhidko.games.puzzle15.BoardActivity.PuzzleFragment;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class TileButton extends Button {

	private PuzzleFragment mView;
	
	public TileButton(Context context) {
		super(context);
	}
	
	public TileButton(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}   
	
	public TileButton(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	
	
	public void setFrame(PuzzleFragment v) {
		mView = v;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (mView != null) {
			View v = mView.getView();
			return v.onTouchEvent(event);
		} else {
			return super.onTouchEvent(event);
		}
		
	}
	
	public int getI() {
		int[] ij = (int[]) getTag();
		return ij[0];
	}
	
	public void setI(int i) {
		int[] ij = (int[]) getTag();
		ij[0] = i;
		setTag(ij);
	}
	
	public int getJ() {
		int[] ij = (int[]) getTag();
		return ij[1];
	}
	
	public void setJ(int j) {
		int[] ij = (int[]) getTag();
		ij[1] = j;
		setTag(ij);
	}
	
	private boolean mResetFlag = false;
	
	public void setResetButton() {
		mResetFlag = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	
		if (mResetFlag) {
		
			mView.resetButton();
			mResetFlag = false;
		}
	}

}
