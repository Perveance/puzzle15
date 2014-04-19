package mszhidko.games.movingtile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.FrameLayout;

public class TileButton extends Button {

	private FrameLayout mFrame;
	
	public TileButton(Context context) {
		super(context);
	}
	
	public TileButton(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}   
	
	public TileButton(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	
	
	public void setFrame(FrameLayout frame) {
		mFrame = frame;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (mFrame != null) {
			return mFrame.onTouchEvent(event);
		} else {
			return super.onTouchEvent(event);
		}
		
	}

}
