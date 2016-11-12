package com.pngamesandapps.coin;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class Coins extends Activity {

  static float fScreenWidth =0,fScreenHeight = 0;
	GameSurfaceView pSurfaceView;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
//	    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//        .detectDiskReads()
//        .detectDiskWrites()
//        .detectNetwork()   // or .detectAll() for all detectable problems
//        .penaltyLog()
//        .build());
//	    
//	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//        .detectLeakedSqlLiteObjects()
//        .detectLeakedClosableObjects()
//        .penaltyLog()
//        .penaltyDeath()
//        .build());
	    
	    
	    
	    super.onCreate(savedInstanceState);
	
	    
	    
	    DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    fScreenWidth = metrics.widthPixels;
	    fScreenHeight = metrics.heightPixels;
	    
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
	    
	    System.out.println(fScreenWidth+","+fScreenHeight);
	    pSurfaceView = new GameSurfaceView(this);
	    setContentView(pSurfaceView);
		//setContentView(R.layout.activity_coins);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.coins, menu);
		return true;
	}

    @Override
    protected void onPause()
        {
            // TODO Auto-generated method stub
            //pSurfaceView.shutDown();
            super.onPause();
        }

    @Override
    protected void onResume()
        {
            // TODO Auto-generated method stub
            //pSurfaceView.startUP();
            super.onResume();
         
        }

}
