package com.pngamesandapps.coin;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

	private SurfaceHolder pSurfaceHolder;
	private GameSurfaceView pGameSurfaceView;
	private boolean bRunning = false;
	

	public GameThread(SurfaceHolder pSurfHold, GameSurfaceView pGameSurfView) 
	{
		this.pSurfaceHolder = pSurfHold;
		this.pGameSurfaceView = pGameSurfView;

	}

	public void setRunning(boolean run) {
		bRunning = run;

	}

	public void run() {
		Canvas pCanvas = null;
		
		//System.out.println("running thread.."+bRunning);
		
		while (bRunning) {
			
		    
			
			        try
			            {
			                pCanvas = pSurfaceHolder.lockCanvas();
			                synchronized (pSurfaceHolder) {
			                    if (pCanvas != null) {
			                        pGameSurfaceView.onDraw(pCanvas);
			                    }

			                }
			            }
			            
			            finally
			            {
			                if (pCanvas != null) {
			                    pSurfaceHolder.unlockCanvasAndPost(pCanvas);
			                }
			            }
			    }
		   
			
		

	}

}
