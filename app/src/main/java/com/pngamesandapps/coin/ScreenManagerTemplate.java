package com.pngamesandapps.coin;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class ScreenManagerTemplate {

	Context pActivityContext;
	DrawUtils pDrawUtil;
	BitmapManager pBitmap_mgr;
	
	/* Replace with your own game manager class */
	
	public ScreenManagerTemplate(Context context)
	{
		pActivityContext = context;
		
	}
	
	public void initialize()
	{
		
		
	}
	
	public void setLevelData()
	{
		
		
	}
	
	/* Touch handler for any updates */
	
	public void handleEvent(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
			
		}

		if (event.getAction() == MotionEvent.ACTION_MOVE) {

			

		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
	

		}

	}
	
	/* All updated drawing go here */
	
	public void updateDrawBuffer(Canvas canvas, float startFrame,
			long elapsedTime) {
		
		
	}
	
	


}
