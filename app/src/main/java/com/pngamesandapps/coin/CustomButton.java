package com.pngamesandapps.coin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.widget.Toast;

public class CustomButton {

	Context pContext;
	SparseArray<Bitmap> pBitmapCache = new SparseArray<Bitmap>();
	int nImgid, nCropX, nCropY, nCropWid, nCropHeig;

	float fBtnX, fBtnY, fBtnWid, fBtnHgt;
	float fDefaultWidth = 320;
    float fDefaultHeight = 480;
	Bitmap pBitmap;
	boolean bButtonClicked = false;
	boolean bChecked = false;
	int nCustomintValue=-1;
	
	Paint   pButtonPaint     = new Paint();
    private float fAnimX;
    private float fAnimY;
    private float fTarX;
    private float fTarY;
    private boolean bAnim=false;
    private float fAnimTime=0;
    private float fTotAnimTime=0;
	

	public CustomButton(Context context, Bitmap bitmap, int nCropX, int nCropY,
			int nCropWid, int nCropHeig, float nDrawX, float nDrawY, float nDrawWid,
			float nDrawHgt) 
	{
		pContext = context;
		this.pBitmap = bitmap;
		this.nCropX = nCropX;
		this.nCropY = nCropY;
		this.nCropWid = nCropWid;
		this.nCropHeig = nCropHeig;

		this.fBtnX = (nDrawX);
        this.fBtnY = (nDrawY);
        this.fBtnWid = (nDrawWid);
        this.fBtnHgt=(nDrawHgt); 
        
       
        
        pButtonPaint.setFilterBitmap(true);
	}
	
	public void setChecked(boolean bValue)
	    {
	        bChecked = bValue;
	    }
	
	
	public boolean getChecked()
	    {
	        
	        return bChecked;
	    }
	
	public void setButtonAnim(float sx,float sy,float tx,float ty,float tottime)
        {
          fAnimX=sx;
          fAnimY=sy;
          fTarX=tx;
          fTarY=ty;
          bAnim=true;
          fAnimTime=0;
          fTotAnimTime=tottime;
        }
    
	
	public void setCustomValue(int value)
	    {
	        nCustomintValue=value;
	    }
	public int getCustomValue()
	    {
	        return nCustomintValue;
	    }
	
	public void changeBitmap(Bitmap bitmap)
	    {
	        this.pBitmap = bitmap;
	    }

	public void adjustPos(float x1, float y1, float w1, float h1) {

		fBtnX = (x1);
		fBtnY = (y1);
		fBtnWid = (w1);
		fBtnHgt = (h1);

	}

	public void adjustPos(float x1, float y1) {

		fBtnX = (x1);
		fBtnY = (y1);

	}

	public void adjustPosX(float x1) {

		fBtnX = (x1);

	}

	public void adjustPosY(float y1) {

		fBtnY = (y1);

	}

	public float scaleX(float x) {
		return (float) ((x / fDefaultWidth) * Coins.fScreenWidth);
	}

	public float pointscale(float pt) {
		float val = 0;
		val = (float) (scaleX(pt) * 0.5 + scaleY(pt) * 0.5);
		return val;
	}
 
	public float scaleY(float y) {
		return (float) ((y / fDefaultHeight) * Coins.fScreenHeight);
	}
	
	   
    public void drawRotateButton(Canvas canvas,float degrees) 
        {

        Rect src = new Rect(this.nCropX, this.nCropY, this.nCropX
                + this.nCropWid, this.nCropY + this.nCropHeig);
        RectF dst = new RectF(scaleX(-this.fBtnWid/2), scaleY(-this.fBtnHgt/2),scaleX(-(this.fBtnWid/2)+this.fBtnWid) ,
                scaleY(-(this.fBtnHgt/2)+this.fBtnHgt));
        
        if(bButtonClicked)
            pButtonPaint.setAlpha(128);
        
        canvas.save();
        canvas.translate(scaleX(this.fBtnX+this.fBtnWid/2), scaleY(this.fBtnY+this.fBtnHgt/2));
        canvas.rotate(degrees);
        canvas.drawBitmap(this.pBitmap, src, dst, pButtonPaint);
        canvas.restore();
        pButtonPaint.setAlpha(255);
        
                
    }
	
    public void drawAnimButton(Canvas canvas,long elapsedTime) 
        {

           if(bAnim)
               {
                   fAnimTime+=(1/fTotAnimTime)*elapsedTime;
                   if(fAnimTime>=1)
                       {
                           fAnimTime=1;
                           bAnim=false;
                       }
                  this.fBtnX=this.fAnimX+(fTarX-fAnimX)* fAnimTime;
                  this.fBtnY=this.fAnimY+(fTarY-fAnimY)* fAnimTime;
                   
               }
        
        Rect src = new Rect(this.nCropX, this.nCropY, this.nCropX
                + this.nCropWid, this.nCropY + this.nCropHeig);
        RectF dst = new RectF(scaleX(this.fBtnX), scaleY(this.fBtnY),scaleX(this.fBtnX + this.fBtnWid) ,
                scaleY(this.fBtnY + this.fBtnHgt));
        
        if(bButtonClicked)
            pButtonPaint.setAlpha(128);
        
        
        canvas.drawBitmap(this.pBitmap, src, dst, pButtonPaint);
        pButtonPaint.setAlpha(255);
        
        
    }

    
	public void drawButton(Canvas canvas) 
	    {
	        
		
	    Rect src = new Rect(this.nCropX, this.nCropY, this.nCropX
				+ this.nCropWid, this.nCropY + this.nCropHeig);
		RectF dst = new RectF(scaleX(this.fBtnX), scaleY(this.fBtnY),scaleX(this.fBtnX + this.fBtnWid) ,
				scaleY(this.fBtnY + this.fBtnHgt));
		
		if(bButtonClicked)
		    pButtonPaint.setAlpha(128);
		
		
		canvas.drawBitmap(this.pBitmap, src, dst, pButtonPaint);
		pButtonPaint.setAlpha(255);
		
		
	}

	public interface ButtonClickListener {
		public void onButtonClick(CustomButton bt);
	}

	protected ButtonClickListener pButtonclicklistener;

	public void setOnClickListener(ButtonClickListener bt) {
		pButtonclicklistener = bt;
	}

	public void handleEvent(MotionEvent event) {
		int eventX = (int) event.getX();
		int eventY = (int) event.getY();

		
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			if ((eventX < scaleX(fBtnX + fBtnWid) ) && (eventY < scaleY(fBtnY + fBtnHgt))
					&& (eventX > scaleX(fBtnX)) && (eventY > scaleY(fBtnY))) {

				// textpaint.setColor(Color.RED);
				// border.setColor(Color.RED);
				// buttonclick=true;
				bButtonClicked = true;
				
				

			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// textpaint.setColor(Color.WHITE);
			// border.setColor(Color.WHITE);

			// Toast.makeText(
			// pContext,
			// "" + eventX + "," + eventY + "--" + fBtnX + "," + fBtnY
			// + "," + fBtnWid + "," + fBtnHgt, Toast.LENGTH_LONG)
			// .show();

			if ((eventX < scaleX(fBtnX + fBtnWid)) && (eventY < scaleY(fBtnY + fBtnHgt))
					&& (eventX > scaleX(fBtnX)) && (eventY > scaleY(fBtnY))) {

				if (bButtonClicked&&!bAnim) {
					
					pButtonclicklistener.onButtonClick(this);
		
			

				}

			}
			bButtonClicked = false;
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {

		}

	}

}
