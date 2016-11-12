package com.pngamesandapps.coin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pngamesandapps.coin.CustomButton;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import com.pngamesandapps.coin.CustomButton.ButtonClickListener;

public class GameSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	
	GameThread pGameThread;
	ChainReactionManager pGameManager;
	GameScreen pGameScreen;
	
	
	long fStartFrame = 0, fLastFrame = 0;
	

	public GameSurfaceView(Context context) {
		super(context);		
		
		pGameScreen = new GameScreen(context);
		
		
	
		getHolder().addCallback(this);

		fLastFrame = fStartFrame = System.currentTimeMillis();

//		pGameThread = new GameThread(getHolder(), this);
//		pGameThread.start();
//		pGameThread.setRunning(true);
//		 

		
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
	
		long fCurrentFrameTime = System.currentTimeMillis();
		long fElapsedTime = fCurrentFrameTime - fLastFrame;
			
		pGameScreen.draw(canvas, fCurrentFrameTime, fElapsedTime);
		fLastFrame = fCurrentFrameTime;

	}
	
	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
			    
			   // System.out.println("surface changed...");

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		// TODO Auto-generated method stub
	    pGameThread = new GameThread(getHolder(), this);
        pGameThread.start();
        pGameThread.setRunning(true);
        
	    //System.out.println("surface created..."+(pGameThread == null));

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	    pGameThread.setRunning(false);
	    //System.out.println("surface destroyed");
	    
	    
	}
	
	public void startUP()
	    {
	        
	        if(pGameThread != null)
	            {
	                pGameThread.setRunning(true);
	                System.out.println("not null");
	            }
	        
	        fLastFrame = fStartFrame = System.currentTimeMillis();
	   	    System.out.println("onresume..");
	    }
	public void shutDown()
	    {
	        if(pGameThread != null)
	            {
	                pGameThread.setRunning(false);
	                System.out.println("not null");
	            }
	               
	        System.out.println("onpause");
	    }

	public float getStartFrame() {
		return fStartFrame;
	}

	public void setStartFrame(long fFrameTime) {
		fStartFrame = fFrameTime;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		pGameScreen.handleEvent(event);

		return true;

		// return super.onTouchEvent(event);
	}

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


//	public void addlistener() {
//		pCustomButton.setOnClickListener(new ButtonClickListener() {
//
//			public void onButtonClick(CustomButton bt) {
//				// TODO Auto-generated method stub
//				Toast.makeText(pActivityContext, "test ckij",
//						Toast.LENGTH_SHORT).show();
//
//			}
//		});
//	}

}
