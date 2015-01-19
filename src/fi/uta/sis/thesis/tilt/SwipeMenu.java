package fi.uta.sis.thesis.tilt;

import fi.uta.sis.thesis.tilt.MenuActivity.Alignment;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

public class SwipeMenu extends LinearLayout {
	private boolean visible;
	private Boolean hideable = true;
	private Alignment align;
	private TranslateAnimation anim;
	
	SwipeMenu(Alignment a, Context context) {
		super(context);
		setVisible(true);
		this.setAlign(a);
		anim = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
	}
	
	public boolean isHideable() {
		return hideable;
	}
	
	public void setHideable(boolean hideable) {
		this.hideable = hideable;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public Alignment getAlign() {
		return align;
	}

	public void setAlign(Alignment align) {
		this.align = align;
	}
	
	public void show() {
		if(!isVisible() && (anim.hasStarted() == anim.hasEnded()) ) {
			int fromX = this.getWidth();
			
			if(getAlign() == Alignment.LEFT) {
				anim = new TranslateAnimation(Animation.ABSOLUTE, -(fromX), Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
			} else {
				anim = new TranslateAnimation(Animation.ABSOLUTE, fromX, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
			}
			anim.setDuration(200);
		    anim.setFillAfter( true );
		    this.startAnimation(anim);
		    setVisible(true);
		}
	}
	
	public void hide() {
		if(isVisible() && (anim.hasStarted() == anim.hasEnded()) && isHideable() ) {
			int fromX = this.getWidth();
			
			if(getAlign() == Alignment.LEFT) {
				anim = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, -(fromX), Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
			} else {
				anim = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, fromX, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
			}
			anim.setDuration(200);
		    anim.setFillAfter( true );
		    this.startAnimation(anim);
		    setVisible(false);
		}
	}
}