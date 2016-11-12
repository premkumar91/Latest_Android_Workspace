package com.pngamesandapps.coin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

public class BitmapManager {
	
	SparseArray<Bitmap> pBitmapCache = new SparseArray<Bitmap>();


	public void addBitmap(Context context,int id) {
		BitmapFactory.Options ops = new BitmapFactory.Options();
		ops.inPurgeable = true;
		ops.inSampleSize = 1;
		ops.inScaled = false;
		ops.inDensity = 0;
		Bitmap pSprite = BitmapFactory.decodeResource(context.getResources(),
				id, ops);
	
		//pBitmapCache.put(id, Bitmap.createBitmap(pSprite));
		  pBitmapCache.put(id,pSprite);

	}
	
	public void addBitmapWithBounds(Context context,int id, int x, int y, int wid, int hgt) {
		BitmapFactory.Options ops = new BitmapFactory.Options();
		ops.inPurgeable = true;
		ops.inSampleSize = 1;
		ops.inScaled = false;
		ops.inDensity = 0;
		Bitmap sprite = BitmapFactory.decodeResource(context.getResources(),
				id, ops);
		 
		pBitmapCache.put(id, Bitmap.createBitmap(sprite, x, y, x + wid, y + hgt));

	}
	
	public void addScaledBitmap(Context context,int id,int width,int height, boolean filter) {

		BitmapFactory.Options ops = new BitmapFactory.Options();
		ops.inPurgeable = true;
		ops.inSampleSize = 1;
		ops.inScaled = false;
		ops.inDensity = 0;
		Bitmap sprite = BitmapFactory.decodeResource(context.getResources(),
				id, ops);
		
		pBitmapCache.put(id, Bitmap.createScaledBitmap(sprite,width /*sprite.getWidth()*/,height/*sprite.getHeight()*/, filter));
		
	}
	
	public Bitmap getBitmap(int id) {
		return pBitmapCache.get(id);

	}
	
	public void removeBitmap(int id) {
		pBitmapCache.delete(id);

	}
	
	
}
