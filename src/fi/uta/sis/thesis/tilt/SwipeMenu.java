package fi.uta.sis.thesis.tilt;

import fi.uta.sis.thesis.tilt.MenuActivity.Alignment;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

public class SwipeMenu extends LinearLayout {
	private boolean visible;
	private Alignment align;
	
	SwipeMenu(Alignment a, Context context) {
		super(context);
		setVisible(true);
		this.setAlign(a);
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
		if(!isVisible()) {
			TranslateAnimation anim;
			int fromX = this.getWidth();
			if(getAlign() == Alignment.LEFT) {
				anim = new TranslateAnimation(Animation.ABSOLUTE, -(fromX), Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
			} else {
				anim = new TranslateAnimation(Animation.ABSOLUTE, fromX, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
			}
			anim.setDuration(1000);
		    anim.setFillAfter( true );
		    this.startAnimation(anim);
		    setVisible(true);
		}
	}
	
	public void hide() {
		if(isVisible()) {
			TranslateAnimation anim;
			int fromX = this.getWidth();
			if(getAlign() == Alignment.LEFT) {
				anim = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, -(fromX), Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
			} else {
				anim = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, fromX, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
			}
			anim.setDuration(1000);
		    anim.setFillAfter( true );
		    this.startAnimation(anim);
		    setVisible(false);
		}
	}
}
