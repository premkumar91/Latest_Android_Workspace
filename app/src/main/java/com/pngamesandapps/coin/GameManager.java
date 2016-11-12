package com.pngamesandapps.coin;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.widget.Toast;

public class GameManager {

	int[] nCellId = { 1, 2, 5, 10, 15, 20, 25, 50 };
	int nTarget = 50;
	int[][] nCoinList = new int[100][5];
	int nMaxCoinComb = 100;
	int[][][] nCombList = new int[nMaxCoinComb][nCellId.length][2];
	int nMaxComb = 50;
	int nCoinSelectId = -1;
	int nCoinPosX = 0, nCoinPosY = 1, nCoinID = 2, nCoinProcessed = 3,nCoinCount = 4;
	int nComb_i = 0 ,nComb_j = 0,nComb_k = 0;
	int nDefCoinWid = 40, nDefCoinHgt = 40;
	
	CellData[] pCellData = new CellData[nCellId.length];

	ArrayList<ArrayList<Integer>> nCombRow = new ArrayList<ArrayList<Integer>>();
	ArrayList<Integer> nCombCol = new ArrayList<Integer>();
	ArrayList<Integer> nTempCombArray = new ArrayList<Integer>();
	
    Context pActivityContext;
	DrawUtils pDrawUtil;
	BitmapManager pBitmap_mgr;
	
	
	public GameManager(Context context)
	{
		pActivityContext = context;
		
	}
	
	public void initialize()
	{
		pDrawUtil = new DrawUtils(pActivityContext);
		pBitmap_mgr = new BitmapManager();
		pBitmap_mgr.addBitmap(pActivityContext, R.drawable.about);
		pBitmap_mgr.addBitmap(pActivityContext, R.drawable.coin);

		
	}
	
	
	public void setLevelData() {

		int nStartX = 20;
		int nStartY = 20;
		int nCell_HSpace = 10;
		int nCell_VSpace = 10;
		int nCellWidth = 40, nCellHeight = 40;

		for (int i = 0; i < nCellId.length; i++) {
			pCellData[i] = new CellData();
			pCellData[i].nCellID = nCellId[i];
			pCellData[i].nCellWidth = nCellWidth;
			pCellData[i].nCellHeight = nCellHeight;
			pCellData[i].nPosX = nStartX;
			pCellData[i].nPosY = nStartY;
			pCellData[i].nImageId = R.drawable.coin;
			pCellData[i].nTextX = (nStartX + nCellWidth / 2);
			pCellData[i].nTextY = (nStartY + nCellHeight / 2);
			pCellData[i].pText = "" + nCellId[i];

			nStartX = nStartX + nCellWidth + nCell_HSpace;

		}

		for (int i = 0; i < nCoinList.length; i++) {
			nCoinList[i][0] = -1;
			nCoinList[i][1] = -1;
			nCoinList[i][2] = -1;
			nCoinList[i][3] = -1;
			nCoinList[i][4] = 0;
		}
		
		for(int i=0;i<nMaxCoinComb;i++)
		{
			for(int j=0;j < nCellId.length;j++)
			{
				nCombList[i][j][0] = -1;
				nCombList[i][j][1] = -1;
				
				
			}
		}
		

	}
	
	public void handleEvent(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// checkCoinBounds(x, y);
		}

		if (event.getAction() == MotionEvent.ACTION_MOVE) {

			// checkValid(x,y);

		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// checkValid(x,y);
			checkCoinBounds(x, y);

		}

	}
	
	public void updateDrawBuffer(Canvas canvas, float startFrame,
			long elapsedTime) {

		pDrawUtil.clearRect(canvas, 0, 0, 480, 800, Color.rgb(238, 48, 167));

		drawCoinAnim(canvas, startFrame, elapsedTime);
		drawCoinCombinations(canvas, startFrame, elapsedTime);

		pDrawUtil.drawLine(canvas, 0, 600, 480, 0, Color.BLACK, 5);
		pDrawUtil.drawLine(canvas, 400, 0, 0, 800, Color.BLACK, 5);

	}
	
	public void drawCoinAnim(Canvas canvas, float startFrame, long elapsedTime) {
		for (int i = 0; i < nCellId.length; i++) {

			pDrawUtil.drawAnimSprite(canvas,
					pBitmap_mgr.getBitmap(pCellData[i].nImageId), 0, 0, 512,
					512, pCellData[i].nPosX, pCellData[i].nPosY,
					pCellData[i].nCellWidth, pCellData[i].nCellHeight, true);
			pDrawUtil.drawText(canvas, pCellData[i].pText, pCellData[i].nTextX,
					pCellData[i].nTextY, 10, Color.BLACK);

			if (pCellData[i].bAnim) {

				float fTimeDiff = elapsedTime;

				pCellData[i].nAnimX += (pCellData[i].nUnitVectX * fTimeDiff);
				pCellData[i].nAnimY += (pCellData[i].nUnitVectY * fTimeDiff);

				float nDeltaX = pCellData[i].nTargetX - pCellData[i].nAnimX;
				float nDeltaY = pCellData[i].nTargetY - pCellData[i].nTargetY;

				pCellData[i].nVectB_X = nDeltaX;
				pCellData[i].nVectB_Y = nDeltaY;

				float distance = (float) Math.sqrt((nDeltaX * nDeltaX)
						+ (nDeltaY * nDeltaY));

				pDrawUtil
						.drawAnimSprite(canvas,
								pBitmap_mgr.getBitmap(pCellData[i].nImageId),
								0, 0, 512, 512, pCellData[i].nAnimX,
								pCellData[i].nAnimY, pCellData[i].nCellWidth,
								pCellData[i].nCellHeight, true);
				pDrawUtil.drawText(canvas, pCellData[i].pText,
						pCellData[i].nAnimX + (pCellData[i].nCellWidth / 2),
						pCellData[i].nAnimY + (pCellData[i].nCellHeight / 2),
						10, Color.BLACK);

				float nVec_DotProduct = pCellData[i].nVectA_X
						* pCellData[i].nVectB_X + pCellData[i].nVectA_Y
						* pCellData[i].nVectB_Y;

				

				if (distance < 5 || nVec_DotProduct < 0) {
					pCellData[i].bAnim = false;
				}

			}

		}

	}
	
	public void drawCoinCombinations(Canvas canvas, float startFrame,
			long elapsedTime) {

		for (int i = 0; i < nCoinList.length; i++) {
			if (nCoinList[i][3] >= 0) {
				pDrawUtil.drawAnimSprite(canvas,
						pBitmap_mgr.getBitmap(R.drawable.coin), 0, 0, 512, 512,
						nCoinList[i][nCoinPosX], nCoinList[i][nCoinPosY], 40,
						40, true);
				pDrawUtil.drawText(canvas, "" + nCoinList[i][nCoinID],
						(float) nCoinList[i][nCoinPosX] + (nDefCoinWid / 2),
						(float) nCoinList[i][nCoinPosY] + (nDefCoinHgt / 2),
						10, Color.RED);
				pDrawUtil.drawText(canvas, "" + nCoinList[i][nCoinCount],
						(float) nCoinList[i][nCoinPosX] + (nDefCoinWid / 2),
						(float) nCoinList[i][nCoinPosY] + nDefCoinHgt + 15, 12,
						Color.BLUE);
			} else {
				break;
			}

		}

	}


	
	
	
	public void checkCoinBounds(float x, float y) {

		for (int i = 0; i < nCellId.length; i++) {
			if (x >= pCellData[i].nPosX
					&& x <= pCellData[i].nPosX + pCellData[i].nCellWidth
					&& y >= pCellData[i].nPosY
					&& y <= pCellData[i].nPosY + pCellData[i].nCellHeight
					&& !pCellData[i].bAnim) {

				pCellData[i].bAnim = true;
				pCellData[i].bClicked = true;
				pCellData[i].nAnimX = pCellData[i].nPosX;
				pCellData[i].nAnimY = pCellData[i].nPosY;

				nCoinSelectId = pCellData[i].nCellID;

				int[] nCoinArr = new int[4];
				nCoinArr = computeCoinTargetPosition();

				pCellData[i].nTargetX = nCoinArr[nCoinPosX];
				pCellData[i].nTargetY = nCoinArr[nCoinPosY];
				pCellData[i].nCoinCount = nCoinArr[nCoinCount];

				float nDeltaX = pCellData[i].nTargetX - pCellData[i].nPosX;
				float nDeltaY = pCellData[i].nTargetY - pCellData[i].nPosY;

				pCellData[i].nVectA_X = nDeltaX;
				pCellData[i].nVectA_Y = nDeltaY;

				float distance = (float) Math.sqrt((nDeltaX * nDeltaX)
						+ (nDeltaY * nDeltaY));

				float nUnitVectX = nDeltaX / distance;
				float nUnitVectY = nDeltaY / distance;

				pCellData[i].nUnitVectX = nUnitVectX;
				pCellData[i].nUnitVectY = nUnitVectY;

				checkCoinCombinations(pCellData[i].nCellID);

				

				// Toast.makeText(pActivityContext,
				// "test EVENT "+pCellData[i].pText, Toast.LENGTH_SHORT).show();
			}

		}

	}
	
	
	public int[] computeCoinTargetPosition() {
		boolean bCoinAlready = false;
		int nCoinPos = -1;
		int nCellSpace = 10;
		int nCellLimit = 5;

		int nMaxRow = 3, nMaxcol = 5;

		int nCoinStartX = 20, nCoinStartY = 560, nCoinXSpace = 20, nCoinYSpace = 30, nCoinWid = 40;
		int nColY = 0;

		for (int i = 0; i < nCoinList.length; i++) {

			if (i % nMaxcol == 0 && i != 0) {
				nColY++;

			}

			if (nCoinList[i][3] == -1) {

				nCoinList[i][nCoinPosX] = nCoinStartX
						+ (nCoinWid * (i % nMaxcol))
						+ (nCoinXSpace * (i % nMaxcol));
				nCoinList[i][nCoinPosY] = nCoinStartY + (nCoinWid * nColY)
						+ (nCoinYSpace * nColY);

				nCoinList[i][nCoinID] = nCoinSelectId;
				nCoinList[i][nCoinProcessed] = 1;
				nCoinList[i][nCoinCount] = nCoinList[i][nCoinCount] + 1;

				nCoinPos = i;

				break;
			} else if (nCoinList[i][2] == nCoinSelectId && nCoinSelectId >= 0) {

				nCoinList[i][nCoinPosX] = nCoinStartX
						+ (nCoinWid * (i % nMaxcol))
						+ (nCoinXSpace * (i % nMaxcol));
				nCoinList[i][nCoinPosY] = nCoinStartY + (nCoinWid * nColY)
						+ (nCoinYSpace * nColY);

				nCoinPos = i;
				nCoinList[i][nCoinCount] = nCoinList[i][nCoinCount] + 1;
				break;
			}

		}


		return nCoinList[nCoinPos];

	}


	public void checkCoinCombinations(int nCellID) {
		boolean bValidCombination = false;
		boolean bExceeded = false;
		int nTargetSum = 0;

		nCombCol.add(nCellID);

		for (int i = 0; i < nCombCol.size(); i++) {
			nTargetSum += nCombCol.get(i);

			if (nTargetSum == nTarget) {
				bValidCombination = true;
				break;
			} else if (nTargetSum >= nTarget) {
				bExceeded = true;
				break;
			}

		}

		if (bValidCombination) {
			// nCombRow.add(nCombCol);
			Toast.makeText(pActivityContext, "rigth comb", Toast.LENGTH_SHORT)
					.show();
			//checkDuplicateCombination();
			nCombCol.clear();

		} else if (bExceeded) {
			Toast.makeText(pActivityContext, "False Combination ",
					Toast.LENGTH_SHORT).show();
			nCombCol.clear();

		}

		//System.out.println(nTargetSum);

	}
	

	/*     coding to be done              */ 
	
	public void checkDuplicateCombination()
	{
		
		
		int[][][] nTempCombList = new int[1][nCellId.length][2];
		int t1=0,t2=0;
		boolean bDuplicate = false;
		int nCount = 0;
		
		for(int i = 0 ;i < nCellId.length; i++)
		{
			
			if(pCellData[i].bClicked)
			{
				
				nTempCombList[0][t2][0] = pCellData[i].nCellID;
				nTempCombList[0][t2][1] = pCellData[i].nCoinCount;
				t2++;
							
				System.out.println("CombExist");
				System.out.println(pCellData[i].nCellID+" -> "+pCellData[i].nCoinCount);
				
			}
		}
		
		
			if(nComb_i == 0)
			{
				for(int a = 0;a < t2;a++)
				{
					
					nCombList[nComb_i][a][0] = nTempCombList[nComb_i][a][0];
					nCombList[nComb_i][a][1] = nTempCombList[nComb_i][a][1];
					
				}
				nComb_i++;
			}
			
			else
			{
				for(int i=0;i<nComb_i;i++)
				{
					for(int j=0;j<nCellId.length;j++)
					{
						
						if(nCombList[i][j][0] < 0 || nCombList[i][j][1] < 0)
						{
							break;
						}
						
						nCount = 0;
						
						for(int k =0 ;k < t2;k++)
						{
							if(nCombList[i][j][0] == nTempCombList[0][k][0] && nCombList[i][j][1]  == nTempCombList[0][k][1])
							{
								nCount++;
							}
						}
						
						if(nCount == t2-1)
						{
							bDuplicate = true;
							break;
						}
							
			
						
					}
					
					if(bDuplicate)
						break;
					
				}
				
				if(!bDuplicate)
				{
					for(int k =0 ;k < t2;k++)
					{

						nCombList[nComb_i][k][0] = nTempCombList[nComb_i][k][0];
						nCombList[nComb_i][k][1] = nTempCombList[nComb_i][k][1];
						
					}
					nComb_i++;
				}
				
					
			}
			
		
		
		System.out.println("duplicate"+bDuplicate);
		
		
	}


	  
	  
	
}
