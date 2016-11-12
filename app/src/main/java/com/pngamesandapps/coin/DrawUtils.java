package com.pngamesandapps.coin;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.SparseArray;

public class DrawUtils
    {

        Context pContext;
        Paint   pFilter, pNoFilter;

        Paint   pTextPaint     = new Paint();
        Paint   pLinePaint     = new Paint();

        float   fDefaultWidth  = 320;
        float   fDefaultHeight = 480;
        int     nCropX, nCropY, nCropWid, nCropHgt, nDrawX, nDrawY, nDrawWid,
                            nDrawHgt;
        int     nImgId;
        
        Typeface typeface;
        

        public DrawUtils(Context pContext)
            {
                this.pContext = pContext;
                typeface = Typeface.createFromAsset(
                                    pContext.getAssets(), "fonts/Simply Rounded Bold.ttf");
                pFilter = new Paint();
                pFilter.setFilterBitmap(true);
                pNoFilter = new Paint(Color.BLACK);
                
       
                
                

            }

        public float scaleX(float x)
            {
                return (float) ((x / fDefaultWidth) * Coins.fScreenWidth);
            }

        public float scaleX(float x, boolean bUpscale)
            {

                if (bUpscale)
                    {
                        return (float) Math.ceil((x / fDefaultWidth)
                                            * Coins.fScreenWidth);
                    }

                return (float) ((x / fDefaultWidth) * Coins.fScreenWidth);
            }

        public float pointscale(float pt)
            {
                float val = 0;
                val = (float) (scaleX(pt) * 0.5 + scaleY(pt) * 0.5);
                return val;
            }

        public float scaleY(float y, boolean bUpScale)
            {

                if (bUpScale)
                    {
                        return (float) Math.ceil((y / fDefaultHeight)
                                            * Coins.fScreenHeight);
                    }
                return (float) ((y / fDefaultHeight) * Coins.fScreenHeight);
            }

        public float scaleY(float y)
            {
                return (float) ((y / fDefaultHeight) * Coins.fScreenHeight);
            }



        public void drawImage(Canvas canvas, Bitmap bitmap, float cropX,
                                   float cropY, float cropWid, float cropHgt, float dstX,
                                   float dstY, float dstWid, float dstHgt,
                                   boolean filter)
        {
            int nCropX,nCropY,nCropWid,nCropHgt;
            nCropX=(int)(cropX*bitmap.getWidth());
            nCropY=(int)(cropY*bitmap.getHeight());
            nCropWid=(int)(cropWid*bitmap.getWidth());
            nCropHgt=(int)(cropHgt*bitmap.getHeight());


            Rect src = new Rect(nCropX, nCropY, nCropX + nCropWid, nCropY + nCropHgt);
            RectF dst = new RectF(scaleX(dstX), scaleY(dstY), scaleX(dstX
                    + dstWid), scaleY(dstY + dstHgt));
            if (bitmap != null)
            {


                if (filter)
                    canvas.drawBitmap(bitmap, src, dst, pFilter);
                else
                    canvas.drawBitmap(bitmap, src, dst, pNoFilter);

            }


        }
        

        public void drawAnimSprite(Canvas canvas, Bitmap bitmap, int srcX,
                            int srcY, int srcWid, int srcHgt, float dstX,
                            float dstY, float dstWid, float dstHgt,
                            boolean filter)
            {

                Rect src = new Rect(srcX, srcY, srcX + srcWid, srcY + srcHgt);
                RectF dst = new RectF(scaleX(dstX), scaleY(dstY), scaleX(dstX
                                    + dstWid), scaleY(dstY + dstHgt));

                if (bitmap != null)
                    {
                        if (filter)
                            canvas.drawBitmap(bitmap, src, dst, pFilter);
                        else
                            canvas.drawBitmap(bitmap, src, dst, pNoFilter);

                    }

            }

        public void drawBit(Canvas canvas, Bitmap bitmap)
            {
                canvas.drawBitmap(bitmap, 50, 50, null);
            }

        public void drawLine(Canvas canvas, float startX, float startY,
                            float stopX, float stopY, int color, float linewidth)
            {

                pLinePaint.setColor(color);
                pLinePaint.setStrokeWidth(scaleX(linewidth));
                pLinePaint.setStyle(Paint.Style.FILL);
                canvas.drawLine(scaleX(startX), scaleY(startY), scaleX(stopX),
                                    scaleY(stopY), pLinePaint);

            }
        
        
        public void drawText(Canvas canvas, String text, float x, float y,
                            int strwidth, int color)
            {
                
                
                pTextPaint.setColor(color);
                pTextPaint.setStyle(Paint.Style.FILL);
                pTextPaint.setTextSize(scaleX(strwidth));
                pTextPaint.setTextAlign(Paint.Align.CENTER);
                pTextPaint.setTypeface(typeface);
                canvas.drawText(text, scaleX(x), scaleY(y), pTextPaint);

            }

        public void drawCircle(Canvas canvas, float x, float y, float radius,
                            int fillcolor,int strokecolor,float strokewidth)
            {
                
                pTextPaint.setColor(fillcolor);
                pTextPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(scaleX(x), scaleY(y), scaleX(radius),
                                    pTextPaint);
                
                pTextPaint.setColor(strokecolor);
                pTextPaint.setStyle(Paint.Style.STROKE);
                pTextPaint.setStrokeWidth(strokewidth);
                canvas.drawCircle(scaleX(x), scaleY(y), scaleX(radius),
                                    pTextPaint);
                

            }
        


        public void drawRect(Canvas canvas, float x, float y, float width,
                            float height, int strokeWidth, int color)
            {

                RectF rect = new RectF(scaleX(x), scaleY(y), scaleX(x + width),
                                    scaleY(y + height));
                pTextPaint.setStrokeWidth(strokeWidth);
                pTextPaint.setColor(color);
                canvas.drawColor(color);
                canvas.drawRect(rect, pTextPaint);

            }

       

        public void drawRect(Canvas canvas, float x, float y, float width,
                            float height, int strokeWidth, int strokecolor,
                            int fillcolor)
            {

               

                RectF rect = new RectF(scaleX(x), scaleY(y), scaleX(x + width),
                                    scaleY(y + height));
                
                pTextPaint.setColor(fillcolor);
                pTextPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(rect, pTextPaint);

                pTextPaint.setColor(strokecolor);
                pTextPaint.setStyle(Paint.Style.STROKE);
                pTextPaint.setStrokeWidth(scaleX(strokeWidth));
                canvas.drawRect(rect, pTextPaint);

            }

        public void drawStrokeRect(Canvas canvas, float x, float y,
                            float width, float height, int strokeWidth,
                            int color)
            {
                pTextPaint.setStrokeWidth(strokeWidth);
                pTextPaint.setColor(color);
                pTextPaint.setStyle(Paint.Style.STROKE);
                RectF rect = new RectF(scaleX(x), scaleY(y), scaleX(x + width),
                                    scaleY(y + height));
                canvas.drawRect(rect, pTextPaint);

            }

        public void drawRotateImage(Canvas canvas, Bitmap bitmap, int srcX,
                            int srcY, int srcWid, int srcHgt, float dstX,
                            float dstY, int dstWid, int dstHgt, float angle,
                            boolean filter)
            {

                canvas.save();
                canvas.translate(scaleX(dstX + dstWid / 2), scaleY(dstY
                                    + dstHgt / 2));
                canvas.rotate(angle);

                this.drawAnimSprite(canvas, bitmap, srcX, srcY, srcWid, srcHgt,
                                    -dstWid / 2, -dstHgt / 2, dstWid, dstHgt,
                                    filter);

                canvas.restore();

            }

        public void clearRect(Canvas canvas, int x, int y, int width,
                            int height, int color)
            {
                pTextPaint.setColor(color);
                RectF rect = new RectF(scaleX(x), scaleY(y), scaleX(x + width),
                                    scaleY(y + height));
                canvas.drawRect(rect, pTextPaint);

            }

        public void drawRoundRect(Canvas canvas, int x, int y, int width,
                            int height, int strokeWidth, int color)
            {
                pTextPaint.setStrokeWidth(strokeWidth);
                pTextPaint.setColor(color);
                RectF rect = new RectF(scaleX(x), scaleY(y), scaleX(x + width),
                                    scaleY(y + height));
                canvas.drawRoundRect(rect, scaleX(width / 4),
                                    scaleY(height / 4), pTextPaint);

            }

        public void drawStrokeText(Canvas canvas, String text, int x, int y,
                            int strwidth ,int textColor, int strokeColor,int strokewidth)
            {
                // pTextPaint.setAntiAlias(true);

                pTextPaint.setColor(strokeColor);
                pTextPaint.setStyle(Style.STROKE);
                pTextPaint.setTextSize(scaleX(strwidth));
                pTextPaint.setStrokeWidth(scaleX(strokewidth));
                pTextPaint.setTypeface(typeface);
                pTextPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(text, scaleX(x), scaleY(y), pTextPaint);

                // pTextPaint.setAntiAlias(true);
                pTextPaint.setColor(textColor);
                pTextPaint.setStyle(Paint.Style.FILL);
                pTextPaint.setTextSize(scaleX(strwidth));
                pTextPaint.setTypeface(typeface);
                pTextPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(text, scaleX(x), scaleY(y), pTextPaint);

            }

    }
