package com.pngamesandapps.coin;


public class ChainCellData {

	float PosX = - 1;
	float PosY = - 1;
	float StartX1 = -1;
	float StartY1 = -1;
	float StartX2 = -1;
	float StartY2 = -1;
	float StartX3 = -1;
	float StartY3 = -1;
	float StartX4 = -1;
	float StartY4 = -1;
	float AnimX1 = -1;
	float AnimY1 = -1;
	float TargetX1 = -1;
	float TargetY1 = -1;
	float AnimX2 = -1;
	float AnimY2 = -1;
	float TargetX2 = -1;
	float TargetY2 = -1;
	float AnimX3 = -1;
	float AnimY3 = -1;
	float TargetX3 = -1;
	float TargetY3 = -1;
	float AnimX4 = -1;
	float AnimY4 = -1;
	float TargetX4 = -1;
	float TargetY4 = -1;

	float fUnitVectX1 = -1;
	float fUnitVectY1 = -1;
	
	float fUnitVectX2 = -1;
	float fUnitVectY2 = -1;
	
	float fUnitVectX3 = -1;
	float fUnitVectY3 = -1;
	
	float fUnitVectX4 = -1;
	float fUnitVectY4 = -1;

	
	int nImageId = -1;
	int nAnimImageId = -1;
	int nPlayerID = -1;
	int nPlayerOldID = -1;
	int nMatchCount = -1;
	int nCellType = -1;
	int nTouchCount = 0;
	int nOldTouchCount = 0;
	
	boolean bTop = false;
	boolean bBottom = false;
	boolean bLeft = false;
	boolean bRight = false; 
	boolean bTile = false;
	boolean bSplit = false;
    boolean bExceeded = false;	

	
	boolean bEmpty = true;
	boolean bAnim = false;
	boolean bExtraAnim1 = false;
	boolean bExtraAnim2 = false;
	boolean bExtraAnim3 = false;
	boolean bExtraAnim4 = false;
	boolean bClicked = false;
	
	boolean bSound = false;
	boolean bCornerCells=false;
	boolean bBorderCells=false;
	boolean bIgnoredCells=false;
	

}


