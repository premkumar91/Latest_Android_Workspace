package com.pngamesandapps.coin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.Random;

import com.pngamesandapps.coin.CustomButton.ButtonClickListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.MotionEvent;
import android.widget.Toast;

public class ChainReactionManager {

    // up space - 40 down space - 40

    Context pActivityContext;
    DrawUtils pDrawUtil;
    BitmapManager pBitmap_mgr;
    float angle = 0;
    int NO_OF_CELL = 8;
    int CANVAS_X = 10,
            CANVAS_Y = 50;
    float CELL_WIDTH = 30,
            CELL_HEIGHT = 30;
    float ROW_PLUS = 7,
            COL_PLUS = 12;

    int nTotalCellWidth = 300,
            nTotalCellHeight = 380;

    float nBallWidth, nBallHeight;
    int nImageType = -1;
    int nNoofPlayers = 2;
    int nPlayerId = -1;
    int nCpuId = -1;
    int nTouchCount = -1;
    int nMaxPlayer = 8;
    int mMaxImageType = 4;
    int nNoOfTrigCell = 0;
    int MAXPLAYER = 8;

    boolean bPlayerIdTaken = false;
    boolean bGameOver = false;

    ChainCellData[][] pCellData;
    ChainCellData[][] pCellDataCPUTurn;
    ChainCellData[][] pCellDataPlayerTurn;

    int nPlayerImage[][] = new int[nMaxPlayer][mMaxImageType];
    int nSelectedArr[][] = new int[500][2];

    //TriggerCell[] pTriggerCell;

    private int nCurrentPlayerID = -1,
            nPreviousPlayerID = -1;
    private boolean bSplit;
    private boolean bCPU = false;
//    private int nCornerCellCount;
//    private int nCornerCells[][];

    private int TOP_LEFT = 1,
            TOP_RIGHT = 2, LEFT = 3, RIGHT = 4,
            BOTTOM_LEFT = 5, BOTTOM_RIGHT = 6, TOP = 7,
            BOTTOM = 8;

    private CustomButton pBtn_Back, pBtn_Pause;

    private boolean bLoaded = false;

    private int nNextPlayerId = -1;

    MediaPlayer pMedia_Sound, pMedia_Split, pMedia_BoardTouch;

    private boolean bSplitSoundOn = false;

    private float fTime = 0;
    private int nMoves = 0;
    private int nMins = 0;
    private int nSecs = 0;
    private int nTotalTime = 1;                                 // minutes

    private int nPlayerRGB[] = new int[MAXPLAYER];
    private boolean bGameOverbyCpu = false;
    private boolean bGameOverbyPlayer = false;

    private int CELL_CURRENT_TRIGGER = 1;
    private int CELL_BEFOREOPPONENTEXCEED = 2;
    private int CELL_MINMATCHNOTEFFECTIVE = 3;
    private int CELL_NOTEFFECTIVE = 4;
    private int CELL_RANDOM = 5;
    private int nCurrentCPUState = -1;

    private boolean bNextIterationPlayerGameOverCheck = false;
    private boolean bPaused = false;
    private boolean bSettingEnabled = false;

    private int nTaps = 0;
    private long lSystemThinkingTime = 0;
    private CustomButton pBtn_Reload;
    private CustomButton pBtn_Sound;
    private CustomButton pBtn_Settings;
    SharedPreferences pPreference;
    Editor pEditor;

    private float fPlayerTurnTime = 0;
    private float fCpuTurnTime = 0;
    private float MAX_TURN_TIME = 700;

    private int nChoosenCell[] = new int[2];
    private boolean bShowDescription = false;

    private float fBallCropX = 0f, fBallCropY = 0f, fBallCropWid = 1f, fBallCropHgt = 1f;
    private float fTestFrames[][], nTestFrameIndex = 0;




    public ChainReactionManager(Context context) {
        pActivityContext = context;
        pDrawUtil = new DrawUtils(pActivityContext);
        allocateObjects();
        initMedia(context);
    }

    public void setPreferences(SharedPreferences pPreference) {
        this.pPreference = pPreference;
    }

    public void initMedia(Context context) {

        pMedia_Sound = MediaPlayer.create(context, R.raw.btn);
        pMedia_Split = MediaPlayer.create(context, R.raw.split);
        pMedia_BoardTouch = MediaPlayer.create(context,
                R.raw.boardtouch);
    }

    public void allocateObjects() {
        initBitmap();
        initButton();

        addButtonListener();

    }

    public void initButton() {
        pBtn_Back = new CustomButton(pActivityContext,
                pBitmap_mgr.getBitmap(R.drawable.back), 0,
                0, 512, 512, 280, 12, 30, 30);
        pBtn_Pause = new CustomButton(pActivityContext,
                pBitmap_mgr.getBitmap(R.drawable.pause), 0,
                0, 512, 512, 220, 442, 20, 20);
        pBtn_Sound = new CustomButton(
                pActivityContext,
                (Constants.bSound) ? pBitmap_mgr
                        .getBitmap(R.drawable.sound)
                        : pBitmap_mgr.getBitmap(R.drawable.mute),
                0, 0, 512, 512, 270, 442, 20, 20);

        pBtn_Reload = new CustomButton(pActivityContext,
                pBitmap_mgr.getBitmap(R.drawable.reload),
                0, 0, 512, 512, 140, 442, 30, 30);

        pBtn_Settings = new CustomButton(pActivityContext,
                pBitmap_mgr.getBitmap(R.drawable.settings),
                0, 0, 512, 512, 280, 442, 30, 30);

    }

    public void initBitmap() {

        pBitmap_mgr = new BitmapManager();

        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.about);

        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type1_1);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type1_2);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type1_3);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type1_4);

        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type2_1);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type2_2);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type2_3);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type2_4);

        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type3_1);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type3_2);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type3_3);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type3_4);

        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type4_1);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type4_2);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type4_3);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type4_4);

        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type5_1);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type5_2);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type5_3);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type5_4);

        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type6_1);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type6_2);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type6_3);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type6_4);

        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type7_1);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type7_2);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type7_3);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type7_4);

        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.play_but);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.back);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.pause);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.reload);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.sound);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.mute);
        pBitmap_mgr.addBitmap(pActivityContext, R.drawable.settings);

    }

    public void initialize() {

    }

    public void addButtonListener() {
        pBtn_Settings.setOnClickListener(new ButtonClickListener() {

            public void onButtonClick(CustomButton bt) {
                float cx, cy, rad, ang = 0;

                cx = pBtn_Settings.fBtnX;
                cy = pBtn_Settings.fBtnY + 5;
                rad = 50;

                if (Constants.bSound)
                    pMedia_Sound.start();

                bSettingEnabled = !bSettingEnabled;

                if (bSettingEnabled) {
                    pBtn_Pause.setButtonAnim(
                            (float) cx,
                            (float) cy,
                            (float) (cx + 30 * Math
                                    .cos(180 * Math.PI / 180)),
                            (float) (cy + 30 * Math
                                    .sin(180 * Math.PI / 180)),
                            500.0f);

                    pBtn_Sound.setButtonAnim(
                            (float) cx,
                            (float) cy,
                            (float) (cx + 60 * Math
                                    .cos(180 * Math.PI / 180)),
                            (float) (cy + 60 * Math
                                    .sin(180 * Math.PI / 180)),
                            500.0f);
                }

            }
        });

        pBtn_Back.setOnClickListener(new ButtonClickListener() {

            public void onButtonClick(CustomButton bt) {

                if (Constants.bSound)
                    pMedia_Sound.start();

                Constants.nScreen = Constants.SUBMODE_SCREEN;

            }
        });

        pBtn_Reload.setOnClickListener(new ButtonClickListener() {

            public void onButtonClick(CustomButton bt) {

                if (Constants.bSound)
                    pMedia_Sound.start();

                setLevelData();

            }
        });

        pBtn_Sound.setOnClickListener(new ButtonClickListener() {

            public void onButtonClick(CustomButton bt) {

                if (Constants.bSound)
                    pMedia_Sound.start();

                Constants.bSound = !Constants.bSound;

            }
        });

        pBtn_Pause.setOnClickListener(new ButtonClickListener() {

            public void onButtonClick(CustomButton bt) {

                if (Constants.bSound)
                    pMedia_Sound.start();

                bPaused = !bPaused;

            }
        });

    }




        /*
         * Common initialisation for all modes
         */

    public void setLevelData() {





        nChoosenCell[0] = -1;
        nChoosenCell[1] = -1;

        fCpuTurnTime = 0;
        fPlayerTurnTime = 0;

        lSystemThinkingTime = 0;
        bSettingEnabled = false;

        nTaps = 0;
        bPaused = false;
        bNextIterationPlayerGameOverCheck = false;

        bSplitSoundOn = false;

        bSplit = false;

        nNextPlayerId = -1;
        bLoaded = false;

        bGameOver = false;
        nNoOfTrigCell = 0;
        // nPlayerId = -1;
        nCurrentPlayerID = -1;
        nPreviousPlayerID = -1;
        // nCpuId = -1;

        nTouchCount = -1;
        nTotalTime = 1;

        fTime = 0;
        nMoves = 0;
        nMins = 0;
        nSecs = 0;

        bGameOverbyCpu = false;
        bGameOverbyPlayer = false;

        nPlayerRGB[0] = Color.rgb(206, 206, 206);
        nPlayerRGB[1] = Color.rgb(212, 54, 254);
        nPlayerRGB[2] = Color.rgb(103, 103, 103);
        nPlayerRGB[3] = Color.rgb(48, 177, 0);
        nPlayerRGB[4] = Color.rgb(97, 46, 27);
        nPlayerRGB[5] = Color.rgb(0, 0, 0);
        nPlayerRGB[6] = Color.rgb(0, 0, 0);
        nPlayerRGB[7] = Color.rgb(0, 0, 0);

        if (Constants.nMode == Constants.MODE_SINGLEPLAYER || Constants.nMode == Constants.MODE_DEMO) {
            nNoofPlayers = 2;
        } else {
            nNoofPlayers = Constants.nPlayers[Constants.nCurrentPlayerIndex];
        }

        NO_OF_CELL = Constants.nBoards[Constants.nCurrentBoardIndex];

        if (Constants.nMode == Constants.MODE_DEMO) {
            NO_OF_CELL = Constants.nBoards[0];
            CANVAS_X = 20;
            CANVAS_Y = 80;
            nTotalCellWidth = 280;
            nTotalCellHeight = 280;
        } else {
            CANVAS_X = 10;
            CANVAS_Y = 50;
            nTotalCellWidth = 300;
            nTotalCellHeight = 380;
        }

        CELL_WIDTH = nTotalCellWidth / NO_OF_CELL;
        CELL_HEIGHT = nTotalCellHeight / NO_OF_CELL;

        nBallWidth = CELL_WIDTH * 0.7f;
        nBallHeight = CELL_HEIGHT * 0.7f;

        ROW_PLUS = CELL_WIDTH * 0.16f;
        COL_PLUS = CELL_HEIGHT * 0.14f;

        nMoves = (NO_OF_CELL * NO_OF_CELL) + 20;

        nCurrentCPUState = -1;

        System.out.println("mode---" + Constants.nMode);
        System.out.println("submode---" + Constants.nSubMode);

        nPlayerId = 0;
        nCpuId = 1;
        setCPUTurn(false);

        switch (Constants.nSubMode) {
            case Constants.MODE_MOVES:

                // nMoves = 10000;

                break;

            case Constants.MODE_INFINITE:

                break;

            case Constants.MODE_TIMED:

                nTotalTime = 2;

                break;

            default:
                break;
        }

        initializeArray();




        bShowDescription = Constants.nMode == Constants.MODE_DEMO ? false : true;
        bLoaded = true;

        //difficulty testing
//        if(Constants.nMode!=Constants.MODE_DEMO)
//        {
        //Testing
//        int nTestPlayerCell[][]={{0,0,1},{2,3,3},{2,4,2},{3,3,3},{3,4,1},{4,0,1},{4,3,2}};
//        int nTestCpuCell[][]={{0,2,2},{0,3,1},{0,4,1},{1,3,1},{2,0,1},{3,2,1},{4,2,2}};
//        int testrow,testcol,testcount;

//            for(int i=0;i<nTestPlayerCell.length;i++)
//            {
//                testrow=nTestPlayerCell[i][0];
//                testcol=nTestPlayerCell[i][1];
//                testcount=nTestPlayerCell[i][2];
//                pCellData[testrow][testcol].bEmpty=false;
//                pCellData[testrow][testcol].nTouchCount=testcount;
//                pCellData[testrow][testcol].nPlayerID=nPlayerId;
//                pCellData[testrow][testcol].nImageId= nPlayerImage[nPlayerId][testcount - 1];
//                System.out.println("testplayer"+testrow+","+testcol);
//            }
//
//            for(int i=0;i<nTestCpuCell.length;i++)
//            {
//                testrow=nTestCpuCell[i][0];
//                testcol=nTestCpuCell[i][1];
//                testcount=nTestCpuCell[i][2];
//                pCellData[testrow][testcol].bEmpty=false;
//                pCellData[testrow][testcol].nTouchCount=testcount;
//                pCellData[testrow][testcol].nPlayerID=nCpuId;
//                pCellData[testrow][testcol].nImageId= nPlayerImage[nCpuId][testcount - 1];
//                System.out.println("testcpu"+testrow+","+testcol);
//            }
//
//        }


    }

    public void initializeArray() {
        // int nCount = 0;
        //int nCornerCount = 0;

        pCellData = new ChainCellData[NO_OF_CELL][NO_OF_CELL];
        pCellDataCPUTurn = new ChainCellData[NO_OF_CELL][NO_OF_CELL];
        pCellDataPlayerTurn = new ChainCellData[NO_OF_CELL][NO_OF_CELL];
        //pTriggerCell = new TriggerCell[NO_OF_CELL * NO_OF_CELL];

//        nCornerCellCount = (2 * NO_OF_CELL) + (2 * (NO_OF_CELL - 2));
//        nCornerCells = new int[nCornerCellCount][2];

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {

                pCellData[i][j] = new ChainCellData();
                pCellDataCPUTurn[i][j] = new ChainCellData();
                pCellDataPlayerTurn[i][j] = new ChainCellData();
                //pTriggerCell[nCount] = new TriggerCell();

                // nCount++;
            }
        }

        boolean bCornerCells = false;

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {

                bCornerCells = false;

//                if((i==0&&j==0)||(i==0&&j==NO_OF_CELL-1)||(i==NO_OF_CELL-1&&j==0)||(i==NO_OF_CELL-1&&j==NO_OF_CELL-1))
//                {
//                    pCellData[i][j].nMatchCount = pCellDataCPUTurn[i][j].nMatchCount=pCellDataPlayerTurn[i][j].nMatchCount=2;
//                    pCellData[i][j].bCornerCells = pCellDataCPUTurn[i][j].bCornerCells=pCellDataPlayerTurn[i][j].bCornerCells=true;
//                    bCornerCells=true;
//                }
//                if(i==0||i==NO_OF_CELL-1||j==0||j==NO_OF_CELL-1)
//                {

                if ((i == 0 && j == 0) || (i == 0 && j == NO_OF_CELL - 1) || (i == NO_OF_CELL - 1 && j == 0) || (i == NO_OF_CELL - 1 && j == NO_OF_CELL - 1)) {
                    pCellData[i][j].nMatchCount = pCellDataCPUTurn[i][j].nMatchCount = pCellDataPlayerTurn[i][j].nMatchCount = 2;
                    pCellData[i][j].bCornerCells = pCellDataCPUTurn[i][j].bCornerCells = pCellDataPlayerTurn[i][j].bCornerCells = true;
                    bCornerCells = true;
                } else if ((j == 0 && i != 0 && i != NO_OF_CELL - 1) || (j == NO_OF_CELL - 1 && i != 0 && i != NO_OF_CELL - 1) || (i == 0 && j != 0 && j != NO_OF_CELL - 1) || (i == NO_OF_CELL - 1 && j != 0 && j != NO_OF_CELL - 1)) {
                    pCellData[i][j].nMatchCount = pCellDataCPUTurn[i][j].nMatchCount = pCellDataPlayerTurn[i][j].nMatchCount = 3;
                    pCellData[i][j].bBorderCells = pCellDataCPUTurn[i][j].bBorderCells = pCellDataPlayerTurn[i][j].bBorderCells = true;
                    bCornerCells = true;
                } else {
                    pCellData[i][j].nMatchCount = pCellDataCPUTurn[i][j].nMatchCount = pCellDataPlayerTurn[i][j].nMatchCount = 4;
                }
//                }

//                if(!bCornerCells)
//                {
//                    pCellData[i][j].nMatchCount = pCellDataCPUTurn[i][j].nMatchCount=pCellDataPlayerTurn[i][j].nMatchCount=4;

//                }

               // System.out.println("i,j " + i + "," + j + " mcount " + pCellData[i][j].nMatchCount);


                                /* Four Corners Cells */

//                if ((i == 0 || i == NO_OF_CELL - 1)
//                        && (j == 0 || j == NO_OF_CELL - 1)) {
//
//                    if (i == 0 && j == 0) {
//                        pCellData[i][j].nMatchCount = 2;
//                        pCellData[i][j].bTop = true;
//                        pCellData[i][j].bLeft = true;
//
//                        pCellDataCPUTurn[i][j].nMatchCount = 2;
//                        pCellDataCPUTurn[i][j].bTop = true;
//                        pCellDataCPUTurn[i][j].bLeft = true;
//
//                        pCellDataPlayerTurn[i][j].nMatchCount = 2;
//                        pCellDataPlayerTurn[i][j].bTop = true;
//                        pCellDataPlayerTurn[i][j].bLeft = true;
//
////                        nCornerCells[nCornerCount][0] = i;
////                        nCornerCells[nCornerCount][1] = j;
////                        nCornerCount++;
//
//                    }
//
//                    if (i == 0 && j == NO_OF_CELL - 1) {
//                        pCellData[i][j].nMatchCount = 2;
//                        pCellData[i][j].bTop = true;
//                        pCellData[i][j].bRight = true;
//
//                        pCellDataCPUTurn[i][j].nMatchCount = 2;
//                        pCellDataCPUTurn[i][j].bTop = true;
//                        pCellDataCPUTurn[i][j].bRight = true;
//
//                        pCellDataPlayerTurn[i][j].nMatchCount = 2;
//                        pCellDataPlayerTurn[i][j].bTop = true;
//                        pCellDataPlayerTurn[i][j].bRight = true;
////
////                        nCornerCells[nCornerCount][0] = i;
////                        nCornerCells[nCornerCount][1] = j;
////                        nCornerCount++;
//                    }
//
//                    if (i == NO_OF_CELL - 1 && j == 0) {
//                        pCellData[i][j].nMatchCount = 2;
//                        pCellData[i][j].bBottom = true;
//                        pCellData[i][j].bLeft = true;
//
//                        pCellDataCPUTurn[i][j].nMatchCount = 2;
//                        pCellDataCPUTurn[i][j].bBottom = true;
//                        pCellDataCPUTurn[i][j].bLeft = true;
//
//                        pCellDataPlayerTurn[i][j].nMatchCount = 2;
//                        pCellDataPlayerTurn[i][j].bBottom = true;
//                        pCellDataPlayerTurn[i][j].bLeft = true;
//
////                        nCornerCells[nCornerCount][0] = i;
////                        nCornerCells[nCornerCount][1] = j;
////                        nCornerCount++;
//                    }
//
//                    if (i == NO_OF_CELL - 1
//                            && j == NO_OF_CELL - 1) {
//                        pCellData[i][j].nMatchCount = 2;
//                        pCellData[i][j].bBottom = true;
//                        pCellData[i][j].bRight = true;
//
//                        pCellDataCPUTurn[i][j].nMatchCount = 2;
//                        pCellDataCPUTurn[i][j].bBottom = true;
//                        pCellDataCPUTurn[i][j].bRight = true;
//
//                        pCellDataPlayerTurn[i][j].nMatchCount = 2;
//                        pCellDataPlayerTurn[i][j].bBottom = true;
//                        pCellDataPlayerTurn[i][j].bRight = true;
//
////                        nCornerCells[nCornerCount][0] = i;
////                        nCornerCells[nCornerCount][1] = j;
////                        nCornerCount++;
//
//                    }
//
//                }
//
//                                /* ------------- */
//
//                                /* Top,bottom row and other Cell matches */
//
//                else {
//
//                    if (i == 0) {
//                        pCellData[i][j].nMatchCount = 3;
//                        pCellData[i][j].bTop = true;
//
//                        pCellDataCPUTurn[i][j].nMatchCount = 3;
//                        pCellDataCPUTurn[i][j].bTop = true;
//
//                        pCellDataPlayerTurn[i][j].nMatchCount = 3;
//                        pCellDataPlayerTurn[i][j].bTop = true;
//
////                        nCornerCells[nCornerCount][0] = i;
////                        nCornerCells[nCornerCount][1] = j;
////                        nCornerCount++;
//
//                    } else if (i == NO_OF_CELL - 1) {
//                        pCellData[i][j].nMatchCount = 3;
//                        pCellData[i][j].bBottom = true;
//
//                        pCellDataCPUTurn[i][j].nMatchCount = 3;
//                        pCellDataCPUTurn[i][j].bBottom = true;
//
//                        pCellDataPlayerTurn[i][j].nMatchCount = 3;
//                        pCellDataPlayerTurn[i][j].bBottom = true;
//
////                        nCornerCells[nCornerCount][0] = i;
////                        nCornerCells[nCornerCount][1] = j;
////                        nCornerCount++;
//
//                    } else {
//                        if (j == 0
//                                || j == NO_OF_CELL - 1) {
//                            if (j == 0) {
//                                pCellData[i][j].bLeft = true;
//                                pCellDataCPUTurn[i][j].bLeft = true;
//                                pCellDataPlayerTurn[i][j].bLeft = true;
//
////                                nCornerCells[nCornerCount][0] = i;
////                                nCornerCells[nCornerCount][1] = j;
////                                nCornerCount++;
//
//                            } else {
//                                pCellData[i][j].bRight = true;
//                                pCellDataCPUTurn[i][j].bRight = true;
//                                pCellDataPlayerTurn[i][j].bRight = true;
////
////                                nCornerCells[nCornerCount][0] = i;
////                                nCornerCells[nCornerCount][1] = j;
////                                nCornerCount++;
//                            }
//
//                            pCellData[i][j].nMatchCount = 3;
//                            pCellDataCPUTurn[i][j].nMatchCount = 3;
//                            pCellDataPlayerTurn[i][j].nMatchCount = 3;
//
//                        } else {
//                            pCellData[i][j].nMatchCount = 4;
//                            pCellDataCPUTurn[i][j].nMatchCount = 4;
//                            pCellDataPlayerTurn[i][j].nMatchCount = 4;
//
//                        }
//
//                    }
//
//                }

                                /* ------------- */

                // System.out.println("i->"+i+" j->"+j+" "+pCellData[i][j].nMatchCount);

                pCellData[i][j].PosX = CANVAS_X
                        + (j * CELL_WIDTH);
                pCellData[i][j].PosY = CANVAS_Y
                        + (i * CELL_HEIGHT);

                pCellDataPlayerTurn[i][j].bSound = pCellDataCPUTurn[i][j].bSound = pCellData[i][j].bSound = false;
                pCellDataPlayerTurn[i][j].bAnim = pCellDataCPUTurn[i][j].bAnim = pCellData[i][j].bAnim = false;
                pCellDataPlayerTurn[i][j].bClicked = pCellDataCPUTurn[i][j].bClicked = pCellData[i][j].bClicked = false;
                pCellDataPlayerTurn[i][j].bEmpty = pCellDataCPUTurn[i][j].bEmpty = pCellData[i][j].bEmpty = true;
                pCellDataPlayerTurn[i][j].bExceeded = pCellDataCPUTurn[i][j].bExceeded = pCellData[i][j].bExceeded = false;
                pCellDataPlayerTurn[i][j].bExtraAnim1 = pCellDataCPUTurn[i][j].bExtraAnim1 = pCellData[i][j].bExtraAnim1 = false;
                pCellDataPlayerTurn[i][j].bExtraAnim2 = pCellDataCPUTurn[i][j].bExtraAnim2 = pCellData[i][j].bExtraAnim2 = false;
                pCellDataPlayerTurn[i][j].bExtraAnim3 = pCellDataCPUTurn[i][j].bExtraAnim3 = pCellData[i][j].bExtraAnim3 = false;
                pCellDataPlayerTurn[i][j].bExtraAnim4 = pCellDataCPUTurn[i][j].bExtraAnim4 = pCellData[i][j].bExtraAnim4 = false;
                pCellDataPlayerTurn[i][j].bSplit = pCellDataCPUTurn[i][j].bSplit = pCellData[i][j].bSplit = false;
                pCellDataPlayerTurn[i][j].bTile = pCellDataCPUTurn[i][j].bTile = pCellData[i][j].bTile = false;
                pCellDataPlayerTurn[i][j].AnimX1 = pCellDataCPUTurn[i][j].AnimX1 = pCellData[i][j].AnimX1 = 0;
                pCellDataPlayerTurn[i][j].AnimX2 = pCellDataCPUTurn[i][j].AnimX2 = pCellData[i][j].AnimX2 = 0;
                pCellDataPlayerTurn[i][j].AnimX3 = pCellDataCPUTurn[i][j].AnimX3 = pCellData[i][j].AnimX3 = 0;
                pCellDataPlayerTurn[i][j].AnimX4 = pCellDataCPUTurn[i][j].AnimX4 = pCellData[i][j].AnimX4 = 0;
                pCellDataPlayerTurn[i][j].AnimY1 = pCellDataCPUTurn[i][j].AnimY1 = pCellData[i][j].AnimY1 = 0;
                pCellDataPlayerTurn[i][j].AnimY2 = pCellDataCPUTurn[i][j].AnimY2 = pCellData[i][j].AnimY2 = 0;
                pCellDataPlayerTurn[i][j].AnimY3 = pCellDataCPUTurn[i][j].AnimY3 = pCellData[i][j].AnimY3 = 0;
                pCellDataPlayerTurn[i][j].AnimY4 = pCellDataCPUTurn[i][j].AnimY4 = pCellData[i][j].AnimY4 = 0;
                pCellDataPlayerTurn[i][j].fUnitVectX1 = pCellDataCPUTurn[i][j].fUnitVectX1 = pCellData[i][j].fUnitVectX1 = 0;
                pCellDataPlayerTurn[i][j].fUnitVectX2 = pCellDataCPUTurn[i][j].fUnitVectX2 = pCellData[i][j].fUnitVectX2 = 0;
                pCellDataPlayerTurn[i][j].fUnitVectX3 = pCellDataCPUTurn[i][j].fUnitVectX3 = pCellData[i][j].fUnitVectX3 = 0;
                pCellDataPlayerTurn[i][j].fUnitVectX4 = pCellDataCPUTurn[i][j].fUnitVectX4 = pCellData[i][j].fUnitVectX4 = 0;
                pCellDataPlayerTurn[i][j].fUnitVectY1 = pCellDataCPUTurn[i][j].fUnitVectY1 = pCellData[i][j].fUnitVectY1 = 0;
                pCellDataPlayerTurn[i][j].fUnitVectY2 = pCellDataCPUTurn[i][j].fUnitVectY2 = pCellData[i][j].fUnitVectY2 = 0;
                pCellDataPlayerTurn[i][j].fUnitVectY3 = pCellDataCPUTurn[i][j].fUnitVectY3 = pCellData[i][j].fUnitVectY3 = 0;
                pCellDataPlayerTurn[i][j].fUnitVectY4 = pCellDataCPUTurn[i][j].fUnitVectY4 = pCellData[i][j].fUnitVectY4 = 0;
                pCellDataPlayerTurn[i][j].nAnimImageId = pCellDataCPUTurn[i][j].nAnimImageId = pCellData[i][j].nAnimImageId = -1;
                pCellDataPlayerTurn[i][j].nImageId = pCellDataCPUTurn[i][j].nImageId = pCellData[i][j].nImageId = -1;
                pCellDataPlayerTurn[i][j].nCellType = pCellDataCPUTurn[i][j].nCellType = pCellData[i][j].nCellType = -1;
                pCellDataPlayerTurn[i][j].nTouchCount = pCellDataCPUTurn[i][j].nTouchCount = pCellData[i][j].nTouchCount = 0;
                pCellDataPlayerTurn[i][j].nOldTouchCount = pCellDataCPUTurn[i][j].nOldTouchCount = pCellData[i][j].nOldTouchCount = 0;
                pCellDataPlayerTurn[i][j].nPlayerID = pCellDataCPUTurn[i][j].nPlayerID = pCellData[i][j].nPlayerID = -1;
                pCellDataPlayerTurn[i][j].nPlayerOldID = pCellDataCPUTurn[i][j].nPlayerOldID = pCellData[i][j].nPlayerOldID = -1;
                pCellDataPlayerTurn[i][j].StartX1 = pCellDataCPUTurn[i][j].StartX1 = pCellData[i][j].StartX1 = 0;
                pCellDataPlayerTurn[i][j].StartX2 = pCellDataCPUTurn[i][j].StartX2 = pCellData[i][j].StartX2 = 0;
                pCellDataPlayerTurn[i][j].StartX3 = pCellDataCPUTurn[i][j].StartX3 = pCellData[i][j].StartX3 = 0;
                pCellDataPlayerTurn[i][j].StartX4 = pCellDataCPUTurn[i][j].StartX4 = pCellData[i][j].StartX4 = 0;
                pCellDataPlayerTurn[i][j].StartY1 = pCellDataCPUTurn[i][j].StartY1 = pCellData[i][j].StartY1 = 0;
                pCellDataPlayerTurn[i][j].StartY2 = pCellDataCPUTurn[i][j].StartY2 = pCellData[i][j].StartY2 = 0;
                pCellDataPlayerTurn[i][j].StartY3 = pCellDataCPUTurn[i][j].StartY3 = pCellData[i][j].StartY3 = 0;
                pCellDataPlayerTurn[i][j].StartY4 = pCellDataCPUTurn[i][j].StartY4 = pCellData[i][j].StartY4 = 0;
                pCellDataPlayerTurn[i][j].TargetX1 = pCellDataCPUTurn[i][j].TargetX1 = pCellData[i][j].TargetX1 = 0;
                pCellDataPlayerTurn[i][j].TargetX2 = pCellDataCPUTurn[i][j].TargetX2 = pCellData[i][j].TargetX2 = 0;
                pCellDataPlayerTurn[i][j].TargetX3 = pCellDataCPUTurn[i][j].TargetX3 = pCellData[i][j].TargetX3 = 0;
                pCellDataPlayerTurn[i][j].TargetX4 = pCellDataCPUTurn[i][j].TargetX4 = pCellData[i][j].TargetX4 = 0;
                pCellDataPlayerTurn[i][j].TargetY1 = pCellDataCPUTurn[i][j].TargetY1 = pCellData[i][j].TargetY1 = 0;
                pCellDataPlayerTurn[i][j].TargetY2 = pCellDataCPUTurn[i][j].TargetY2 = pCellData[i][j].TargetY2 = 0;
                pCellDataPlayerTurn[i][j].TargetY3 = pCellDataCPUTurn[i][j].TargetY3 = pCellData[i][j].TargetY3 = 0;
                pCellDataPlayerTurn[i][j].TargetY4 = pCellDataCPUTurn[i][j].TargetY4 = pCellData[i][j].TargetY4 = 0;

                pCellDataCPUTurn[i][j].PosX = CANVAS_X
                        + (j * CELL_WIDTH);
                pCellDataCPUTurn[i][j].PosY = CANVAS_Y
                        + (i * CELL_HEIGHT);

                pCellDataPlayerTurn[i][j].PosX = CANVAS_X
                        + (j * CELL_WIDTH);
                pCellDataPlayerTurn[i][j].PosY = CANVAS_Y
                        + (i * CELL_HEIGHT);

                pCellData[i][j].bIgnoredCells = false;

            }
        }

        //System.out.println("Corner Cell Count" + nCornerCount);

        resetSelectedArray();
        // resetTriggerArray();

        // player image initialize

        nPlayerImage[0][0] = R.drawable.type1_1;
        nPlayerImage[0][1] = R.drawable.type1_2;
        nPlayerImage[0][2] = R.drawable.type1_3;
        nPlayerImage[0][3] = R.drawable.type1_4;

        nPlayerImage[1][0] = R.drawable.type2_1;
        nPlayerImage[1][1] = R.drawable.type2_2;
        nPlayerImage[1][2] = R.drawable.type2_3;
        nPlayerImage[1][3] = R.drawable.type2_4;

        nPlayerImage[2][0] = R.drawable.type3_1;
        nPlayerImage[2][1] = R.drawable.type3_2;
        nPlayerImage[2][2] = R.drawable.type3_3;
        nPlayerImage[2][3] = R.drawable.type3_4;

        nPlayerImage[3][0] = R.drawable.type4_1;
        nPlayerImage[3][1] = R.drawable.type4_2;
        nPlayerImage[3][2] = R.drawable.type4_3;
        nPlayerImage[3][3] = R.drawable.type4_4;

        nPlayerImage[4][0] = R.drawable.type5_1;
        nPlayerImage[4][1] = R.drawable.type5_2;
        nPlayerImage[4][2] = R.drawable.type5_3;
        nPlayerImage[4][3] = R.drawable.type5_4;

        nPlayerImage[5][0] = R.drawable.type6_1;
        nPlayerImage[5][1] = R.drawable.type6_2;
        nPlayerImage[5][2] = R.drawable.type6_3;
        nPlayerImage[5][3] = R.drawable.type6_4;

        nPlayerImage[6][0] = R.drawable.type7_1;
        nPlayerImage[6][1] = R.drawable.type7_2;
        nPlayerImage[6][2] = R.drawable.type7_3;
        nPlayerImage[6][3] = R.drawable.type7_4;

        bPlayerIdTaken = false;

    }

    public void setCPUTurn(boolean bValue) {
        bCPU = bValue;
    }

    public boolean getCPUTurn() {
        return bCPU;
    }

    public boolean isCPUTurn() {
        return bCPU;
    }

    public void handleButton(float x, float y) {

    }


        /* Touch handler for any updates */

    public void handleEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

                /*
                 * Single Player Validation
                 */
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (bShowDescription) {
                bShowDescription = false;
                return;
            }

        }

        if (!bLoaded || Constants.nMode == Constants.MODE_DEMO || bShowDescription)
            return;

        if (!bPaused) {
            if (bSettingEnabled)
                pBtn_Sound.handleEvent(event);

            pBtn_Reload.handleEvent(event);
            pBtn_Back.handleEvent(event);
            pBtn_Settings.handleEvent(event);
        }

        if (Constants.nMode == Constants.MODE_SINGLEPLAYER) {
            if (isCPUTurn())
                return;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {

        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (bPaused) {
                if (isPointInsideScreen(x, y))
                    bPaused = false;

                return;
            }

            checkCombinations(x, y);

        }

        if (!bPaused && bSettingEnabled)
            pBtn_Pause.handleEvent(event);

    }


    //new
    private int[] findBestCell() {
        int nCell[] = new int[2], nReturnData[] = new int[3];

        int tCpuCellCount = 0, tPlayerCellCount = 0, tPlayerDiffonCpuTurn = 0;
        int tCpuDiffonPlayerTurn = 0;
        boolean bValidCell = false, bEmptyCell = false;
        int nValidCells[][] = new int[100][3], nValidCellsCount = 0;
        int nCpuCells[][] = new int[NO_OF_CELL*NO_OF_CELL][2], nCpuCellCount = 0;
        int  nEmptyCellCount = 0,nTotalCells=0;
        int tUpdatedCpuCount,tUpdatedPlayerCount=0;

        nCell[0]=-1;
        nCell[1]=-1;


        System.out.println("findBestCell ");

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {
                if (!pCellData[i][j].bEmpty && pCellData[i][j].nPlayerID == nCpuId) {

                    tCpuCellCount++;
                }

                if (pCellData[i][j].bEmpty || pCellData[i][j].nPlayerID == nCpuId) {
                    nCpuCells[nCpuCellCount][0] = i;
                    nCpuCells[nCpuCellCount][1] = j;
                    nCpuCellCount++;
                }

                if (!pCellData[i][j].bEmpty && pCellData[i][j].nPlayerID == nPlayerId)
                    tPlayerCellCount++;

                if(pCellData[i][j].bEmpty)
                    nEmptyCellCount++;

                nTotalCells++;

                copyCellDatatoCpuData(i, j);
                pCellData[i][j].bIgnoredCells = false;
            }
        }

        if(tCpuCellCount>0)
        {
            for (int i = 0; i < NO_OF_CELL; i++) {
                for (int j = 0; j < NO_OF_CELL; j++) {

                    if (pCellData[i][j].bEmpty || pCellData[i][j].nPlayerID == nCpuId) {
                        copyCellDatatoCpuData();
                        nReturnData = checkNextCpuEffectiveCell(i, j);
                        tUpdatedCpuCount=nReturnData[2];
                        if (nReturnData[0] == 1) {
                            nCell[0] = i;
                            nCell[1] = j;
                            System.out.println("gameover  by cpu "+i+","+j);
                            return nCell;
                        }

                        tPlayerDiffonCpuTurn = tPlayerCellCount-nReturnData[1];
                        System.out.println("tPlayerDiffonCpuTurn "+tPlayerDiffonCpuTurn+" "+i+","+j);
                        System.out.println("updated cpu count"+tUpdatedCpuCount);

                        if(tPlayerDiffonCpuTurn==0&&pCellData[i][j].nPlayerID==nCpuId&&pCellData[i][j].nTouchCount+1>=pCellData[i][j].nMatchCount)
                            pCellData[i][j].bIgnoredCells=true;

                        if (tPlayerDiffonCpuTurn > 0) {

                            for (int r = 0; r < NO_OF_CELL; r++) {
                                bValidCell = true;

                                for (int c = 0; c < NO_OF_CELL; c++) {
                                    copyCpuDatatoPlayerData();
                                    if (!pCellDataPlayerTurn[r][c].bEmpty&&pCellDataPlayerTurn[r][c].nPlayerID == nPlayerId)
                                    {
                                        nReturnData = checkNextPlayerEffectiveCell(r, c);
                                        tCpuDiffonPlayerTurn = tUpdatedCpuCount-nReturnData[1];
                                        System.out.println("player cells"+r+","+c);
                                        System.out.println("tCpuDiffonPlayerTurn "+tCpuDiffonPlayerTurn);

                                        if (tCpuDiffonPlayerTurn > 0 && tCpuDiffonPlayerTurn > tPlayerDiffonCpuTurn) {
                                            bValidCell = false;
                                            break;
                                        }
                                        if (nReturnData[0] == 1) {
                                            bValidCell = false;
                                            break;
                                        }
                                    }
                                }
                                if (!bValidCell) {

                                    pCellData[i][j].bIgnoredCells = true;
                                    break;
                                }

                            }
                            if (bValidCell) {
                                nValidCells[nValidCellsCount][0] = i;
                                nValidCells[nValidCellsCount][1] = j;
                                nValidCells[nValidCellsCount][2] = tPlayerDiffonCpuTurn;
                                nValidCellsCount++;
                            }

                        }
                    }


                }
            }

        }



        System.out.println("CpuCount "+tCpuCellCount);
        System.out.println("PlayerCount "+tPlayerCellCount);

        System.out.println("Valid Cells on Analysis "+nValidCellsCount);

//        int nLastValue = 0;
//        for (int i = 0; i < nValidCellsCount; i++) {
//            if (tPlayerDiffonCpuTurn > 0 && nValidCells[i][2] > nLastValue) {
//                nLastValue = nValidCells[i][2];
//                nCell[0] = nValidCells[i][0];
//                nCell[1] = nValidCells[i][1];
//            }
//
//            System.out.println(nValidCells[i][0]+","+nValidCells[i][1]+","+nValidCells[i][2]);
//        }
///

        if(nValidCellsCount>0)
        {
            Arrays.sort(nValidCells, new Comparator<int[]>() {

                @Override
                public int compare(int[] o1, int[] o2) {
                    return Integer.valueOf(o2[2]).compareTo(Integer.valueOf(o1[2]));
                }

            });

            System.out.println("sorted vaild cells "+nValidCells[0][0]+","+nValidCells[0][1]+","+nValidCells[0][2]);

            nCell[0]=nValidCells[0][0];
            nCell[1]=nValidCells[0][1];
        }

        if (this.checkFitArray(nCell[0], nCell[1]))
            return  nCell;

        System.out.println("cells"+nCell[0]+","+nCell[1]);

        int nCornerCells[][] = new int[4][2], nBorderCells[][] = new int[(4 * NO_OF_CELL) - 4][2];
        int nNotEffectiveCells[][] = new int[NO_OF_CELL * NO_OF_CELL][2];
        int nCornerCellsCount = 0, nBorderCellsCount = 0, nNotEffectiveCellsCount = 0;

        if (!this.checkFitArray(nCell[0], nCell[1])) {
            for (int i = 0; i < NO_OF_CELL; i++) {
                for (int j = 0; j < NO_OF_CELL; j++) {

                    System.out.println("cell info"+i+","+j+pCellData[i][j].bIgnoredCells);
                    if (pCellData[i][j].bIgnoredCells)
                        continue;
                    if (pCellData[i][j].bCornerCells) {

                        if ((pCellData[i][j].bEmpty || pCellData[i][j].nPlayerID == nCpuId) && isCPUCellisMoreEffectivethanAdjacents(i, j)) {
                            nCornerCells[nCornerCellsCount][0] = i;
                            nCornerCells[nCornerCellsCount][1] = j;
                            nCornerCellsCount++;
                        }

                        System.out.println("cornercell"+i+","+j+" , "+nCornerCellsCount);

                    } else if (pCellData[i][j].bBorderCells) {

                        if ((pCellData[i][j].bEmpty || pCellData[i][j].nPlayerID == nCpuId) && isCPUCellisMoreEffectivethanAdjacents(i, j)) {
                            nBorderCells[nBorderCellsCount][0] = i;
                            nBorderCells[nBorderCellsCount][1] = j;
                            nBorderCellsCount++;
                        }

                        System.out.println("bordercells"+i+","+j+" ,"+nBorderCellsCount);
                    } else {

                        if ((pCellData[i][j].bEmpty || pCellData[i][j].nPlayerID == nCpuId) && isCPUCellisMoreEffectivethanAdjacents(i, j)) {
                            nNotEffectiveCells[nNotEffectiveCellsCount][0] = i;
                            nNotEffectiveCells[nNotEffectiveCellsCount][1] = j;
                            nNotEffectiveCellsCount++;
                        }

                        System.out.println("noteffectivecells"+i+","+j+" ,"+nNotEffectiveCellsCount);
                    }


                }
            }
        }

        Random ran = new Random();
        int nRanNum = ran.nextInt(10), nItertaionCount = 0, nRanIndex = 0;

        System.out.println("cornercells"+nCornerCellsCount);
        System.out.println("bordercells"+nBorderCellsCount);
        System.out.println("noteffectivecells"+nNotEffectiveCellsCount);

        if (nCornerCellsCount > 0)
        {
            nRanIndex = nRanNum % nCornerCellsCount;
            nCell[0] = nCornerCells[nRanIndex][0];
            nCell[1] = nCornerCells[nRanIndex][1];
            System.out.println("random cornercells "+nCell[0]+","+nCell[1]);
            return nCell;
        }

        if (nBorderCellsCount > 0) {
            nRanIndex = nRanNum % nBorderCellsCount;
            nCell[0] = nBorderCells[nRanIndex][0];
            nCell[1] = nBorderCells[nRanIndex][1];
            System.out.println("random Bordercells "+nCell[0]+","+nCell[1]);
            return nCell;
        }

        if (nNotEffectiveCellsCount > 0) {
            nRanIndex = nRanNum % nNotEffectiveCellsCount;
            nCell[0] = nNotEffectiveCells[nRanIndex][0];
            nCell[1] = nNotEffectiveCells[nRanIndex][1];
            System.out.println("random noteffectivecells "+nCell[0]+","+nCell[1]);
            return nCell;
        }



        nRanIndex = nRanNum % tCpuCellCount;
        nCell[0] = nCpuCells[nRanIndex][0];
        nCell[1] = nCpuCells[nRanIndex][1];

        System.out.println("random cpucells "+nCell[0]+","+nCell[1]);

        return nCell;
    }


//old
//        private int[] findBestCell()
//            {
//
//                int cell[] = new int[2];
//                Random ran = new Random();
//
//                int nRanNum = ran.nextInt(10);
//
//                cell[0] = -1;
//                cell[1] = -1;
//
//                nNoOfTrigCell = 0;
//                bGameOverbyCpu = false;
//                bGameOverbyPlayer = false;
//                bNextIterationPlayerGameOverCheck = false;
//
//                nCurrentCPUState = -1;
//
//                cell = find_CurrentTriggerCell();
//
//                nCurrentCPUState = CELL_CURRENT_TRIGGER;
//
//                if (!checkFitArray(cell[0], cell[1]))
//                    {
//                        cell = find_CellBeforeOpponentExceed();
//
//                        nCurrentCPUState = CELL_BEFOREOPPONENTEXCEED;
//
//                        if (!checkFitArray(cell[0], cell[1]))
//                            {
//
//                                if (nRanNum < 4)
//                                    {
//
//                                        cell = find_MinMatchNotEffectiveCell();
//                                        nCurrentCPUState = CELL_MINMATCHNOTEFFECTIVE;
//
//                                    } else if (nRanNum >= 4 && nRanNum <= 7)
//                                    {
//                                        cell = find_NotEffectiveCell();
//                                        nCurrentCPUState = CELL_NOTEFFECTIVE;
//
//                                    } else
//                                    {
//                                        cell = getRandomCell();
//                                        nCurrentCPUState = CELL_RANDOM;
//
//                                    }
//
//                                if (!checkFitArray(cell[0], cell[1]))
//                                    {
//
//                                        cell = find_MinMatchNotEffectiveCell();
//
//                                        if (!checkFitArray(cell[0], cell[1]))
//                                            {
//                                                cell = find_NotEffectiveCell();
//
//                                                if (!checkFitArray(cell[0],
//                                                                    cell[1]))
//                                                    {
//                                                        cell = getRandomCell();
//
//                                                    }
//
//                                            }
//
//                                    }
//
//                            }
//
//                    }
//
//                System.out.println("Current Cpu Analysis" + nCurrentCPUState);
//                System.out.println("Cpu Cell " + cell[0] + " , " + cell[1]);
//
//                /* Sort Trigger List */
//
//                System.out.println("Sorted list");
//
//                for (int i = 0; i < nNoOfTrigCell; i++)
//                    {
//                        for (int j = i + 1; j < nNoOfTrigCell; j++)
//                            {
//                                int tempRow, tempCol, tempPlayerCnt;
//
//                                if (pTriggerCell[j].playercountonboard < pTriggerCell[i].playercountonboard)
//                                    {
//                                        tempRow = pTriggerCell[j].row;
//                                        tempCol = pTriggerCell[j].col;
//                                        tempPlayerCnt = pTriggerCell[j].playercountonboard;
//
//                                        pTriggerCell[j].row = pTriggerCell[i].row;
//                                        pTriggerCell[j].col = pTriggerCell[i].col;
//                                        pTriggerCell[j].playercountonboard = pTriggerCell[i].playercountonboard;
//
//                                        pTriggerCell[i].row = tempRow;
//                                        pTriggerCell[i].col = tempCol;
//                                        pTriggerCell[i].playercountonboard = tempPlayerCnt;
//
//                                    }
//
//                            }
//                    }
//
//                int nSortedTrigCellCnt = nNoOfTrigCell;
//                int nSortedTriggerList[][] = new int[nNoOfTrigCell][3];
//
//                if (nNoOfTrigCell > 0)
//                    {
//
//                        for (int i = 0; i < nNoOfTrigCell; i++)
//                            {
//
//                                nSortedTriggerList[i][0] = pTriggerCell[i].row;
//                                nSortedTriggerList[i][1] = pTriggerCell[i].col;
//                                nSortedTriggerList[i][2] = pTriggerCell[i].playercountonboard;
//
//                                System.out.println(nSortedTriggerList[i][0]
//                                                    + " , "
//                                                    + nSortedTriggerList[i][1]
//                                                    + " -> "
//                                                    + nSortedTriggerList[i][2]);
//
//                            }
//
//                    }
//
//                /* Check Player Next moves for better result than Cpu Analysis */
//                if (!bGameOverbyCpu)
//                    {
//                        nNoOfTrigCell = 0;
//                        bGameOverbyPlayer = false;
//
//                        int nPrevPlayerCntonBoard = 0, nCurrPlayerCntonBoard = 0, nPlayerCntDiff = 0;
//                        int nPrevCpuCntonBoard = 0, nCurrCpuCntonBoard = 0, nCpuCntDiff = 0;
//
//                        for (int i = 0; i < NO_OF_CELL; i++)
//                            {
//                                for (int j = 0; j < NO_OF_CELL; j++)
//                                    {
//                                        if (!pCellData[i][j].bEmpty
//                                                            && pCellData[i][j].nPlayerID == nCpuId)
//                                            {
//                                                nPrevCpuCntonBoard++;
//                                            }
//
//                                    }
//                            }
//
//                        copyCellDatatoCpuData();
//
//                        if (pCellData[cell[0]][cell[1]].nTouchCount + 1 >= pCellData[cell[0]][cell[1]].nMatchCount)
//                            checkNextCpuEffectiveCell(cell[0], cell[1]);
//                        else
//                            {
//                                pCellDataCPUTurn[cell[0]][cell[1]].nPlayerID = nCpuId;
//                                pCellDataCPUTurn[cell[0]][cell[1]].nTouchCount++;ue
//                                pCellDataCPUTurn[cell[0]][cell[1]].bEmpty = false;
//                            }
//
//                        for (int i = 0; i < NO_OF_CELL; i++)
//                            {
//                                for (int j = 0; j < NO_OF_CELL; j++)
//                                    {
//                                        if (!pCellDataCPUTurn[i][j].bEmpty)
//                                            {
//
//                                                if (pCellDataCPUTurn[i][j].nPlayerID == nCpuId)
//                                                    nCurrCpuCntonBoard++;
//                                                if (pCellDataCPUTurn[i][j].nPlayerID == nPlayerId)
//                                                    nPrevPlayerCntonBoard++;
//
//                                            }
//
//                                    }
//                            }
//
//                        nCpuCntDiff = nCurrCpuCntonBoard - nPrevCpuCntonBoard;
//
//                        System.out.println("nCurrCpuCnt ,nPrevCpuCnt,nCpuDiff "
//                                            + nCurrCpuCntonBoard + ","
//                                            + nPrevCpuCntonBoard + ","
//                                            + nCpuCntDiff);
//
//                        if (nCpuCntDiff >= 0)
//                            {
//                                nNoOfTrigCell = 0;
//                                bGameOverbyPlayer = false;
//                                int nPlayerCell[] = new int[2];
//                                nPlayerCell[0] = -1;
//                                nPlayerCell[1] = -1;
//
//                                copyCpuDatatoPlayerData();
//
//                                for (int i = 0; i < NO_OF_CELL; i++)
//                                    {
//                                        for (int j = 0; j < NO_OF_CELL; j++)
//                                            {
//                                                if (!pCellDataPlayerTurn[i][j].bEmpty
//                                                                    && pCellDataPlayerTurn[i][j].nPlayerID == nPlayerId)
//                                                    {
//                                                        bGameOverbyPlayer = checkNextPlayerEffectiveCell(
//                                                                            i,
//                                                                            j);
//
//                                                    }
//                                                if (bGameOverbyPlayer)
//                                                    {
//                                                        System.out.println("Game Over by player cell "
//                                                                            + i
//                                                                            + ","
//                                                                            + j);
//                                                        nPlayerCell[0] = i;
//                                                        nPlayerCell[1] = j;
//                                                        break;
//                                                    }
//
//                                                copyCpuDatatoPlayerData();
//                                            }
//
//                                        if (bGameOverbyPlayer)
//                                            {
//
//                                                break;
//                                            }
//                                    }
//
//                                System.out.println("After Player pov ...");
//                                System.out.println("trigger cell");
//
//                                int nTempPlayerCnt = 0;
//                                int nHighestPlayerCell[] = new int[2];
//                                nHighestPlayerCell[0] = -1;
//                                nHighestPlayerCell[1] = -1;
//
//                                for (int i = 0; i < nNoOfTrigCell; i++)
//                                    {
//                                        if (pTriggerCell[i].playercountonboard > nTempPlayerCnt)
//                                            {
//                                                nTempPlayerCnt = pTriggerCell[i].playercountonboard;
//                                                nHighestPlayerCell[0] = pTriggerCell[i].row;
//                                                nHighestPlayerCell[1] = pTriggerCell[i].col;
//                                                nCurrPlayerCntonBoard = pTriggerCell[i].playercountonboard;
//                                            }
//
//                                        System.out.println(pTriggerCell[i].row
//                                                            + ","
//                                                            + pTriggerCell[i].col
//                                                            + ","
//                                                            + pTriggerCell[i].playercountonboard
//                                                            + ","
//                                                            + pTriggerCell[i].cpucountonboard);
//                                    }
//
//                                nPlayerCntDiff = nCurrPlayerCntonBoard
//                                                    - nPrevPlayerCntonBoard;
//
//                                System.out.println("Highest player cell"
//                                                    + nHighestPlayerCell[0]
//                                                    + " , "
//                                                    + nHighestPlayerCell[1]
//                                                    + " nplayerdiff "
//                                                    + nPlayerCntDiff);
//
//                                boolean bLessEffCellChosen = false;
//                                int nLesserEffCell[] = new int[4];
//                                int nTempLessPlayerCnt = 0;
//                                nLesserEffCell[0] = -1;
//                                nLesserEffCell[1] = -1;
//                                nLesserEffCell[2] = -1;
//
//                                // if (!bGameOverbyPlayer)
//                                if (nCurrentCPUState == CELL_CURRENT_TRIGGER)
//                                    {
//                                        if (nPlayerCntDiff > nCpuCntDiff
//                                                            || bGameOverbyPlayer)
//                                            {
//
//                                                nNoOfTrigCell = 0;
//
//                                                nPrevPlayerCntonBoard = 0;
//                                                nCurrCpuCntonBoard = 0;
//                                                nCurrPlayerCntonBoard = 0;
//
//                                                System.out.println("Player effective");
//                                                System.out.println("PlayCpudiff"
//                                                                    + nPlayerCntDiff
//                                                                    + " , "
//                                                                    + nCpuCntDiff);
//
//                                                System.out.println("Player game over for other possilbilties "
//                                                                    + bNextIterationPlayerGameOverCheck
//                                                                    + ","
//                                                                    + bGameOverbyPlayer);
//
//                                                /*
//                                                 * Compute highest cpu cell
//                                                 * after everycell ppov analysis
//                                                 * (get highest player cell
//                                                 * r,c,playercntonboard and
//                                                 * store in array) cpu cell must
//                                                 * follow the condition (ignore
//                                                 * sortedcpu list as its
//                                                 * considered)
//                                                 * (ismoreeffectivethanadjacents
//                                                 * ) (next player moves does not
//                                                 * causes game over)
//                                                 *
//                                                 *
//                                                 * finally find the lowest
//                                                 * playercntboard cell in stored
//                                                 * array
//                                                 */
//
//                                                int[][] nHighestOrderPlayerCell = new int[1][6];
//                                                int nHighestOrderCpuCellcnt = 0;
//                                                int nHighestCpuCnt = 0;
//
//                                                boolean bSkip = false;
//
//                                                bNextIterationPlayerGameOverCheck = false;
//                                                nNoOfTrigCell = 0;
//
//                                                int[][] nHighCpuCell = new int[1][6];
//                                                nHighCpuCell[0][0] = -1;
//                                                nHighCpuCell[0][1] = -1;
//                                                nHighestOrderPlayerCell[0][0] = -1;
//                                                nHighestOrderPlayerCell[0][1] = -1;
//
//                                                for (int i = 0; i < NO_OF_CELL; i++)
//                                                    {
//                                                        for (int j = 0; j < NO_OF_CELL; j++)
//                                                            {
//
//                                                                if (pCellData[i][j].bEmpty
//                                                                                    || pCellData[i][j].nPlayerID == nCpuId)
//                                                                    {
//
//                                                                        if (isCPUCellisMoreEffectivethanAdjacents(
//                                                                                            i,
//                                                                                            j))
//                                                                            {
//
//                                                                                System.out.println(" cell "
//                                                                                                    + i
//                                                                                                    + " , "
//                                                                                                    + j);
//
//                                                                                nHighestCpuCnt = 0;
//                                                                                nHighCpuCell[0][0] = -1;
//                                                                                nHighCpuCell[0][1] = -1;
//                                                                                nHighCpuCell[0][5] = -1;
//
//                                                                                copyCellDatatoCpuData();
//
//                                                                                if (pCellDataCPUTurn[i][j].nPlayerID == nCpuId
//                                                                                                    && pCellDataCPUTurn[i][j].nTouchCount + 1 >= pCellDataCPUTurn[i][j].nMatchCount)
//                                                                                    {
//                                                                                        checkNextCpuEffectiveCell(
//                                                                                                            i,
//                                                                                                            j);
//
//                                                                                    } else if (pCellDataCPUTurn[i][j].bEmpty
//                                                                                                    || (!pCellDataCPUTurn[i][j].bEmpty
//                                                                                                                        && pCellDataCPUTurn[i][j].nTouchCount + 1 < pCellDataCPUTurn[i][j].nMatchCount && pCellDataCPUTurn[i][j].nPlayerID == nCpuId))
//                                                                                    {
//                                                                                        pCellDataCPUTurn[i][j].nPlayerID = nCpuId;
//                                                                                        pCellDataCPUTurn[i][j].nTouchCount++;
//                                                                                        pCellDataCPUTurn[i][j].bEmpty = false;
//
//                                                                                    }
//
//                                                                                bNextIterationPlayerGameOverCheck = false;
//
//                                                                                for (int r = 0; r < NO_OF_CELL; r++)
//                                                                                    {
//                                                                                        for (int c = 0; c < NO_OF_CELL; c++)
//                                                                                            {
//
//                                                                                                copyCpuDatatoPlayerData();
//
//                                                                                                nNoOfTrigCell = 0;
//
//                                                                                                if (pCellDataPlayerTurn[r][c].nPlayerID == nPlayerId
//                                                                                                                    && pCellDataPlayerTurn[r][c].nTouchCount + 1 >= pCellDataPlayerTurn[r][c].nMatchCount)
//                                                                                                    {
//
//                                                                                                        bNextIterationPlayerGameOverCheck = checkNextPlayerEffectiveCell(
//                                                                                                                            r,
//                                                                                                                            c);
//
//                                                                                                        System.out.println("additional gameovercheck");
//                                                                                                        System.out.println(bNextIterationPlayerGameOverCheck
//                                                                                                                            + " "
//                                                                                                                            + r
//                                                                                                                            + ","
//                                                                                                                            + c);
//                                                                                                    } else if (pCellDataPlayerTurn[r][c].bEmpty
//                                                                                                                    || (!pCellDataPlayerTurn[r][c].bEmpty
//                                                                                                                                        && pCellDataPlayerTurn[r][c].nTouchCount + 1 < pCellDataPlayerTurn[r][c].nMatchCount && pCellDataPlayerTurn[r][c].nPlayerID == nPlayerId))
//                                                                                                    {
//                                                                                                        pCellDataPlayerTurn[r][c].nPlayerID = nPlayerId;
//                                                                                                        pCellDataPlayerTurn[r][c].nTouchCount++;
//                                                                                                        pCellDataPlayerTurn[r][c].bEmpty = false;
//
//                                                                                                        int[] nTmpUserCount = new int[2];
//
//                                                                                                        nTmpUserCount = getPlayerBoardUserCount();
//                                                                                                        System.out.println(r
//                                                                                                                            + ","
//                                                                                                                            + c
//                                                                                                                            + "  "
//                                                                                                                            + "tmpUserCount"
//                                                                                                                            + nTmpUserCount[0]
//                                                                                                                            + ","
//                                                                                                                            + nTmpUserCount[1]
//                                                                                                                            + " tc "
//                                                                                                                            + pCellDataPlayerTurn[r][c].nTouchCount);
//                                                                                                        nNoOfTrigCell = 1;
//                                                                                                        pTriggerCell[0].row = r;
//                                                                                                        pTriggerCell[0].col = c;
//                                                                                                        pTriggerCell[0].cpucountonboard = nTmpUserCount[0];
//                                                                                                        pTriggerCell[0].playercountonboard = nTmpUserCount[1];
//
//                                                                                                    }
//
//                                                                                                if (bNextIterationPlayerGameOverCheck)
//                                                                                                    break;
//
//                                                                                                if (nNoOfTrigCell > 0
//                                                                                                                    && !bNextIterationPlayerGameOverCheck)
//                                                                                                    {
//                                                                                                        System.out.println("cpu cent after"
//                                                                                                                            + pTriggerCell[0].cpucountonboard);
//
//                                                                                                        if (pTriggerCell[0].cpucountonboard > nHighestCpuCnt)
//                                                                                                            {
//                                                                                                                nHighestCpuCnt = pTriggerCell[0].cpucountonboard;
//
//                                                                                                                nHighCpuCell[0][0] = i;
//                                                                                                                nHighCpuCell[0][1] = j;
//                                                                                                                nHighCpuCell[0][2] = pTriggerCell[0].row;
//                                                                                                                nHighCpuCell[0][3] = pTriggerCell[0].col;
//                                                                                                                nHighCpuCell[0][4] = pTriggerCell[0].playercountonboard;
//                                                                                                                nHighCpuCell[0][5] = pTriggerCell[0].cpucountonboard;
//
//                                                                                                            }
//
//                                                                                                    }
//
//                                                                                            }
//
//                                                                                        if (bNextIterationPlayerGameOverCheck)
//                                                                                            {
//
//                                                                                                break;
//                                                                                            }
//
//                                                                                    }
//
//                                                                                if (!bNextIterationPlayerGameOverCheck
//                                                                                                    && checkFitArray(nHighCpuCell[0][0],
//                                                                                                                        nHighCpuCell[0][1]))
//                                                                                    {
//                                                                                        System.out.println("cpucount...."
//                                                                                                            + nHighCpuCell[0][5]);
//
//                                                                                        if (nHighCpuCell[0][5] > nHighestOrderCpuCellcnt)
//                                                                                            {
//                                                                                                nHighestOrderPlayerCell[0][0] = nHighCpuCell[0][0];
//                                                                                                nHighestOrderPlayerCell[0][1] = nHighCpuCell[0][1];
//                                                                                                nHighestOrderPlayerCell[0][2] = nHighCpuCell[0][2];
//                                                                                                nHighestOrderPlayerCell[0][3] = nHighCpuCell[0][3];
//                                                                                                nHighestOrderPlayerCell[0][4] = nHighCpuCell[0][4];
//                                                                                                nHighestOrderPlayerCell[0][5] = nHighCpuCell[0][5];
//                                                                                                nHighestOrderCpuCellcnt = nHighCpuCell[0][5];
//                                                                                            }
//
//                                                                                    }
//
//                                                                            }
//
//                                                                    }
//
//                                                            }
//
//                                                        /*
//                                                         * for one cpu cell
//                                                         * checkmade for all
//                                                         * ppov cell and stored
//                                                         * in array take avg cpu
//                                                         * cell which is having
//                                                         * higher cpu in all
//                                                         * possibilities
//                                                         */
//
//                                                    }
//
//                                                System.out.println("nHighCpuCell"
//                                                                    + nHighestOrderPlayerCell[0][0]
//                                                                    + ","
//                                                                    + nHighestOrderPlayerCell[0][1]
//                                                                    + ","
//                                                                    + nHighestOrderPlayerCell[0][5]);
//
//                                                if (this.checkFitArray(
//                                                                    nHighestOrderPlayerCell[0][0],
//                                                                    nHighestOrderPlayerCell[0][1]))
//                                                    {
//                                                        cell[0] = nHighestOrderPlayerCell[0][0];
//                                                        cell[1] = nHighestOrderPlayerCell[0][1];
//                                                    }
//
//                                                /*
//                                                 *
//                                                 */
//
//                                                // System.out.println("Lesser cell ");
//                                                // System.out.println(nLesserEffCell[0]
//                                                // + " , "
//                                                // + nLesserEffCell[1]
//                                                // + " -> "
//                                                // + nLesserEffCell[3]);
//                                                //
//                                                //
//                                                // if (this.checkFitArray(
//                                                // nLesserEffCell[0],
//                                                // nLesserEffCell[1]))
//                                                // {
//                                                // cell[0] = nLesserEffCell[0];
//                                                // cell[1] = nLesserEffCell[1];
//                                                // }
//
//                                            }
//
//                                    }
//
//                            }
//
//                    }
//
//                return cell;
//            }

    public int[] getPlayerBoardUserCount() {
        int[] nUserCount = new int[2];

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {
                if (!pCellDataPlayerTurn[i][j].bEmpty) {
                    if (pCellDataPlayerTurn[i][j].nPlayerID == nCpuId)
                        nUserCount[0]++;
                    else
                        nUserCount[1]++;
                }

            }
        }

        return nUserCount;

    }

//    public int[] find_CellBeforeOpponentExceed() {
//        int row = -1;
//        int col = -1;
//        boolean bValid = false, bProcessed = false;
//        int nOppenentCellDiff = -1;
//        int nCpuCellDiff = -1;
//
//        int nPriority = 0;
//        int cellDiffArr[][] = new int[4][1];
//        int nCellAdjCnt = 0;
//        boolean bHighPriorityCell = false;
//        int nNoOfOpponent = 0;
//
//        nNoOfTrigCell = 0;
//        copyCellDatatoCpuData();
//
//        for (int i = 0; i < NO_OF_CELL; i++) {
//            for (int j = 0; j < NO_OF_CELL; j++) {
//
//                for (int k = 0, len = cellDiffArr.length; k < len; k++) {
//                    cellDiffArr[k][0] = -1;
//
//                }
//                nCellAdjCnt = 0;
//                nNoOfOpponent = 0;
//
//                row = i;
//                col = j;
//
//                if (!pCellDataCPUTurn[i][j].bEmpty
//                        && pCellDataCPUTurn[i][j].nPlayerID == nCpuId) {
//                    bValid = true;
//                    bProcessed = false;
//
//                    nCpuCellDiff = pCellDataCPUTurn[i][j].nMatchCount
//                            - pCellDataCPUTurn[i][j].nTouchCount;
//
//                    for (int r = row - 1; r <= row + 1; r++) {
//                        for (int c = col - 1; c <= col + 1; c++) {
//
//                            if (!checkFitArray(r, c))
//                                continue;
//
//                            if ((r == row - 1 && c != col)
//                                    || (r == row + 1 && c != col)
//                                    || (r == row && c == col))
//                                continue;
//
//                            if (pCellDataCPUTurn[r][c].nPlayerID == nCpuId
//                                    || pCellDataCPUTurn[r][c].bEmpty)
//                                continue;
//
//                            nOppenentCellDiff = pCellDataCPUTurn[r][c].nMatchCount
//                                    - pCellDataCPUTurn[r][c].nTouchCount;
//
//                            if (nOppenentCellDiff < nCpuCellDiff)
//                                bValid = false;
//
//                            else if (nOppenentCellDiff == nCpuCellDiff) {
//                                nPriority = 2;
//                                cellDiffArr[nCellAdjCnt][0] = nOppenentCellDiff;
//                                nCellAdjCnt++;
//                                nNoOfOpponent++;
//
//                                bHighPriorityCell = true;
//
//                            } else {
//                                nPriority = 1;
//                                nNoOfOpponent++;
//
//                            }
//
//                            bProcessed = true;
//
//                        }
//                    }
//
//                    if (bValid && bProcessed) {
//
//                        pTriggerCell[nNoOfTrigCell].row = i;
//                        pTriggerCell[nNoOfTrigCell].col = j;
//                        pTriggerCell[nNoOfTrigCell].playercountonboard = -1;
//                        pTriggerCell[nNoOfTrigCell].priority = nPriority;
//                        pTriggerCell[nNoOfTrigCell].noOfOpponent = nNoOfOpponent;
//
//                        if (nPriority == 2) {
//
//                            int nPrevCellDiff = 0;
//                            int nCurrCellDiff = 0;
//
//                            for (int k = 0; k < nCellAdjCnt; k++) {
//                                nCurrCellDiff = cellDiffArr[k][0];
//
//                                if (nCurrCellDiff < nPrevCellDiff
//                                        || k == 0) {
//                                    nPrevCellDiff = nCurrCellDiff;
//                                }
//
//                            }
//
//                            pTriggerCell[nNoOfTrigCell].triggerdiff = nPrevCellDiff;
//
//                        }
//
//                        nNoOfTrigCell++;
//
//                    }
//
//                }
//
//            }
//        }
//
//        int nBestCell[] = new int[2];
//        nBestCell[0] = -1;
//        nBestCell[1] = -1;
//
//        if (nNoOfTrigCell > 0) {
//
//            int nCurrVal = -1, nPrevVal = -1;
//            int nCurrOpponent = -1, nPrevOpponent = -1;
//
//            boolean bHighPriorityCellTieBreak = false;
//            boolean bHighestOpponentTieBreak = false;
//
//            int nCheckCnt = 0;
//
//            if (bHighPriorityCell) {
//                // In Case of HighPriority Tie or Low Priority
//                // Tie Take
//                // the most effective cell
//                // Most Effective Cell is chosen from most of
//                // opponent
//                // adjacent count cell logic
//
//                for (int i = 0; i < nNoOfTrigCell; i++) {
//
//                    if (pTriggerCell[i].priority == pTriggerCell[i].HIGHPRIORITY) {
//
//                        nCurrVal = pTriggerCell[i].triggerdiff;
//
//                        if (nCurrVal < nPrevVal
//                                || nPrevVal == -1) {
//                            nPrevVal = nCurrVal;
//                            nBestCell[0] = pTriggerCell[i].row;
//                            nBestCell[1] = pTriggerCell[i].col;
//
//                            if (nCheckCnt > 0)
//                                bHighPriorityCellTieBreak = true;
//
//                        } else if (nCurrVal == nPrevVal) {
//                            nCurrOpponent = pTriggerCell[i].noOfOpponent;
//                            if (nCurrOpponent > nPrevOpponent
//                                    || nPrevOpponent == -1) {
//                                nCurrOpponent = nPrevOpponent;
//                                nBestCell[0] = pTriggerCell[i].row;
//                                nBestCell[1] = pTriggerCell[i].col;
//                                bHighestOpponentTieBreak = true;
//                            }
//
//                        }
//
//                        nCheckCnt++;
//
//                    }
//                }
//
//                if (bHighPriorityCellTieBreak
//                        || bHighestOpponentTieBreak) {
//                    if (bHighPriorityCellTieBreak)
//                        return nBestCell;
//
//                    return nBestCell;
//                }
//
//            } else {
//                nCurrOpponent = -1;
//                nPrevOpponent = -1;
//                nCurrVal = -1;
//                nPrevVal = -1;
//
//                // Take most opponent cell in low priority case
//
//                for (int i = 0; i < nNoOfTrigCell; i++) {
//
//                    if (pTriggerCell[i].priority == pTriggerCell[i].LOWPRIORTITY) {
//                        nCurrOpponent = pTriggerCell[i].noOfOpponent;
//
//                        if (nCurrOpponent > nPrevOpponent
//                                || nPrevOpponent == -1) {
//                            nCurrOpponent = nPrevOpponent;
//                            nBestCell[0] = pTriggerCell[i].row;
//                            nBestCell[1] = pTriggerCell[i].col;
//                        }
//
//                    }
//
//                }
//
//                return nBestCell;
//
//            }
//
//        }
//
//        return nBestCell;
//
//    }

    private float getEmptyCellsinBoard() {

        float nNoofEmptyCell = 0;

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {
                if (pCellData[i][j].bEmpty)
                    nNoofEmptyCell++;

            }

        }

        return nNoofEmptyCell;

    }

    private boolean isAnyPlayerinAdjacent(int i, int j) {

        int row = -1, col = -1;

        row = i - 1;
        col = j;

        if (checkFitArray(row, col)
                && pCellData[row][col].nPlayerID == nPlayerId) {
            return true;
        }

        row = i;
        col = j - 1;

        if (checkFitArray(row, col)
                && pCellData[row][col].nPlayerID == nPlayerId) {
            return true;
        }
        row = i;
        col = j + 1;

        if (checkFitArray(row, col)
                && pCellData[row][col].nPlayerID == nPlayerId) {
            return true;
        }
        row = i + 1;
        col = j;

        if (checkFitArray(row, col)
                && pCellData[row][col].nPlayerID == nPlayerId) {
            return true;
        }

        return false;

    }

    private boolean isCPUCellisMoreEffectivethanAdjacents(int i, int j) {


        int row = -1, col = -1;
        int nCurrCellDiff, nAdjCellDiff;

        row = i - 1;
        col = j;

        if (checkFitArray(row, col)) {

            if (pCellData[row][col].nPlayerID == nPlayerId) {
                nCurrCellDiff = pCellData[i][j].nMatchCount
                        - pCellData[i][j].nTouchCount;
                nAdjCellDiff = pCellData[row][col].nMatchCount
                        - pCellData[row][col].nTouchCount;

                if (nAdjCellDiff < nCurrCellDiff) {

                    return false;
                }
            }

        }

        row = i;
        col = j - 1;

        if (checkFitArray(row, col)) {

            if (pCellData[row][col].nPlayerID == nPlayerId) {
                nCurrCellDiff = pCellData[i][j].nMatchCount
                        - pCellData[i][j].nTouchCount;
                nAdjCellDiff = pCellData[row][col].nMatchCount
                        - pCellData[row][col].nTouchCount;

                if (nAdjCellDiff < nCurrCellDiff) {

                    return  false;
                }
            }

        }

        row = i;
        col = j + 1;

        if (checkFitArray(row, col)) {

            if (pCellData[row][col].nPlayerID == nPlayerId) {
                nCurrCellDiff = pCellData[i][j].nMatchCount
                        - pCellData[i][j].nTouchCount;
                nAdjCellDiff = pCellData[row][col].nMatchCount
                        - pCellData[row][col].nTouchCount;

                if (nAdjCellDiff < nCurrCellDiff) {

                    return false;
                }
            }

        }

        row = i + 1;
        col = j;

        if (checkFitArray(row, col)) {

            if (pCellData[row][col].nPlayerID == nPlayerId) {
                nCurrCellDiff = pCellData[i][j].nMatchCount
                        - pCellData[i][j].nTouchCount;
                nAdjCellDiff = pCellData[row][col].nMatchCount
                        - pCellData[row][col].nTouchCount;

                if (nAdjCellDiff < nCurrCellDiff) {

                    return false;
                }
            }

        }

        return true;

    }

    private int[] find_NotEffectiveCell() {
        int cell[] = new int[2];
        cell[0] = -1;
        cell[1] = -1;
        int row = -1, col = -1;
        int nCurrCellDiff = 0, nAdjCellDiff = 0;

        Random ran = new Random();
        int nRanNum = ran.nextInt(10);

        boolean bChoosen = false;
        int iteration = 0;

        while (nRanNum <= 6 && !bChoosen && iteration <= 100) {
            row = ran.nextInt(NO_OF_CELL);
            col = ran.nextInt(NO_OF_CELL);

            if (checkFitArray(row, col)
                    && isAnyPlayerinAdjacent(row, col)) {
                if ((pCellData[row][col].bEmpty || pCellData[row][col].nPlayerID == nCpuId)
                        && isCPUCellisMoreEffectivethanAdjacents(
                        row,
                        col)) {
                    bChoosen = true;
                }
            }

            iteration++;
        }

        if (bChoosen) {
            cell[0] = row;
            cell[1] = col;
        }

        if (!bChoosen) {
            boolean bValid = false;

            for (int i = 0; i < NO_OF_CELL; i++) {
                for (int j = 0; j < NO_OF_CELL; j++) {

                    if (isAnyPlayerinAdjacent(row, col)
                            && (pCellData[i][j].nPlayerID == nCpuId || pCellData[i][j].bEmpty)) {

                        bValid = true;

                        row = i - 1;
                        col = j;

                        if (checkFitArray(row, col)) {

                            if (pCellData[row][col].nPlayerID == nPlayerId) {
                                nCurrCellDiff = pCellData[i][j].nMatchCount
                                        - pCellData[i][j].nTouchCount;
                                nAdjCellDiff = pCellData[row][col].nMatchCount
                                        - pCellData[row][col].nTouchCount;

                                if (nAdjCellDiff < nCurrCellDiff) {
                                    bValid = false;
                                }
                            }

                        }

                        row = i;
                        col = j - 1;

                        if (checkFitArray(row, col)) {

                            if (pCellData[row][col].nPlayerID == nPlayerId) {
                                nCurrCellDiff = pCellData[i][j].nMatchCount
                                        - pCellData[i][j].nTouchCount;
                                nAdjCellDiff = pCellData[row][col].nMatchCount
                                        - pCellData[row][col].nTouchCount;

                                if (nAdjCellDiff < nCurrCellDiff) {
                                    bValid = false;
                                }
                            }

                        }

                        row = i;
                        col = j + 1;

                        if (checkFitArray(row, col)) {

                            if (pCellData[row][col].nPlayerID == nPlayerId) {
                                nCurrCellDiff = pCellData[i][j].nMatchCount
                                        - pCellData[i][j].nTouchCount;
                                nAdjCellDiff = pCellData[row][col].nMatchCount
                                        - pCellData[row][col].nTouchCount;

                                if (nAdjCellDiff < nCurrCellDiff) {
                                    bValid = false;
                                }
                            }

                        }

                        row = i + 1;
                        col = j;

                        if (checkFitArray(row, col)) {

                            if (pCellData[row][col].nPlayerID == nPlayerId) {
                                nCurrCellDiff = pCellData[i][j].nMatchCount
                                        - pCellData[i][j].nTouchCount;
                                nAdjCellDiff = pCellData[row][col].nMatchCount
                                        - pCellData[row][col].nTouchCount;

                                if (nAdjCellDiff < nCurrCellDiff) {
                                    bValid = false;
                                }
                            }

                        }

                        if (bValid) {

                            cell[0] = i;
                            cell[1] = j;

                            System.out.println("findnoteffectivecell"
                                    + cell[0]
                                    + " , "
                                    + cell[1]);

                            return cell;
                        }

                    }

                }
            }
        }

        System.out.println("findnoteffectivecell" + cell[0] + " , "
                + cell[1]);

        return cell;

    }

    private int[] find_leastEffectiveCell() {
        int cell[] = new int[2];
        int nCpuPrevBoardCount = 0;

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {
                if (!pCellData[i][j].bEmpty
                        && pCellData[i][j].nPlayerID == nPlayerId) {
                    nCpuPrevBoardCount++;
                }

            }
        }

        System.out.println("prev player vnt on board "
                + nCpuPrevBoardCount);

        return cell;

    }

    private int[] find_ClosestTriggerNotEffectiveCell() {
        int cell[] = new int[2];
        int row = -1, col = -1;
        int nCellDiff = -1;
        boolean bValid = false;

        cell[0] = -1;
        cell[1] = -1;

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {
                if (pCellData[i][j].nPlayerID == nCpuId) {

                    bValid = true;

                    row = i - 1;
                    col = j;

                    if (checkFitArray(row, col)) {

                        if (pCellData[row][col].nPlayerID == nPlayerId)
                            bValid = false;

                    }

                    row = i;
                    col = j - 1;

                    if (checkFitArray(row, col)) {

                        if (pCellData[row][col].nPlayerID == nPlayerId)
                            bValid = false;

                    }

                    row = i;
                    col = j + 1;

                    if (checkFitArray(row, col)) {

                        if (pCellData[row][col].nPlayerID == nPlayerId)
                            bValid = false;

                    }

                    row = i + 1;
                    col = j;

                    if (checkFitArray(row, col)) {

                        if (pCellData[row][col].nPlayerID == nPlayerId)
                            bValid = false;

                    }

                    if (bValid) {
                        nCellDiff = pCellData[i][j].nMatchCount
                                - (pCellData[i][j].nTouchCount + 1);

                        if (nCellDiff == 1) {
                            cell[0] = i;
                            cell[1] = j;

                            System.out.println("closest trig cell "
                                    + cell[0]
                                    + " , "
                                    + cell[1]);

                            return cell;
                        }
                    }

                }

            }
        }

        System.out.println("Closest trig cell " + cell[0] + " , "
                + cell[1]);

        return cell;

    }

//    private int[] find_MinMatchNotEffectiveCell() {
//        int row = -1, col = -1;
//        boolean bValid = false;
//        int iteration = 0;
//        int r, c;
//
//        Random ran = new Random();
//        int cell[] = new int[2];
//        cell[0] = -1;
//        cell[1] = -1;
//        int nCurrCellDiff, nAdjCellDiff;
//
//        // sequential search from top to bottom on board for min match
//        int nRanNum = ran.nextInt(10);
//
//        int nCornerCellInfo[][] = new int[4][2];
//        nCornerCellInfo[0][0] = 0;
//        nCornerCellInfo[0][1] = 0;
//
//        nCornerCellInfo[1][0] = 0;
//        nCornerCellInfo[1][1] = NO_OF_CELL - 1;
//
//        nCornerCellInfo[2][0] = NO_OF_CELL - 1;
//        nCornerCellInfo[2][1] = 0;
//
//        nCornerCellInfo[3][0] = NO_OF_CELL - 1;
//        nCornerCellInfo[3][1] = NO_OF_CELL - 1;
//
//        boolean bChoosenCornerCell = false;
//
//        System.out.println("ran num is " + nRanNum);
//
//        while (nRanNum < 6 && !bChoosenCornerCell && iteration <= 100) {
//
//            int nIndex = ran.nextInt(nCornerCellInfo.length);
//            row = nCornerCellInfo[nIndex][0];
//            col = nCornerCellInfo[nIndex][1];
//
//            iteration++;
//
//            if (pCellData[row][col].bEmpty) {
//
//                bValid = true;
//
//                bValid = isCPUCellisMoreEffectivethanAdjacents(
//                        row, col);
//
//                if (!bValid)
//                    continue;
//
//                if (bValid
//                        && (pCellData[row][col].nTouchCount + 1) < pCellData[row][col].nMatchCount
//                        || bNextIterationPlayerGameOverCheck
//                        || bGameOverbyPlayer)
//                {
//
//                    cell[0] = row;
//                    cell[1] = col;
//
//                    bChoosenCornerCell = true;
//
//                    System.out.println("corner cell chooden"
//                            + cell[0]
//                            + " , "
//                            + cell[1]);
//
//                    return cell;
//                }
//
//            }
//
//        }
//
//        if (!bChoosenCornerCell) {
//
//            for (int i = 0; i < nCornerCellCount; i++) {
//                row = nCornerCells[i][0];
//                col = nCornerCells[i][1];
//
//                if (pCellData[row][col].nPlayerID == nCpuId
//                        || pCellData[row][col].bEmpty) {
//
//                    bValid = true;
//
//                    r = row - 1;
//                    c = col;
//
//                    if (checkFitArray(r, c)) {
//
//                        if (pCellData[r][c].nPlayerID == nPlayerId) {
//                            nCurrCellDiff = pCellData[row][col].nMatchCount
//                                    - pCellData[row][col].nTouchCount;
//                            nAdjCellDiff = pCellData[r][c].nMatchCount
//                                    - pCellData[r][c].nTouchCount;
//
//                            if (nAdjCellDiff < nCurrCellDiff) {
//                                bValid = false;
//                            }
//
//                        }
//
//                    }
//
//                    r = row;
//                    c = col - 1;
//
//                    if (checkFitArray(r, c)) {
//
//                        if (pCellData[r][c].nPlayerID == nPlayerId) {
//                            nCurrCellDiff = pCellData[row][col].nMatchCount
//                                    - pCellData[row][col].nTouchCount;
//                            nAdjCellDiff = pCellData[r][c].nMatchCount
//                                    - pCellData[r][c].nTouchCount;
//
//                            if (nAdjCellDiff < nCurrCellDiff) {
//                                bValid = false;
//                            }
//
//                        }
//
//                    }
//
//                    r = row;
//                    c = col + 1;
//
//                    if (checkFitArray(r, c)) {
//
//                        if (pCellData[r][c].nPlayerID == nPlayerId) {
//                            nCurrCellDiff = pCellData[row][col].nMatchCount
//                                    - pCellData[row][col].nTouchCount;
//                            nAdjCellDiff = pCellData[r][c].nMatchCount
//                                    - pCellData[r][c].nTouchCount;
//
//                            if (nAdjCellDiff < nCurrCellDiff) {
//                                bValid = false;
//                            }
//
//                        }
//
//                    }
//
//                    r = row + 1;
//                    c = col;
//
//                    if (checkFitArray(r, c)) {
//
//                        if (pCellData[r][c].nPlayerID == nPlayerId) {
//                            nCurrCellDiff = pCellData[row][col].nMatchCount
//                                    - pCellData[row][col].nTouchCount;
//                            nAdjCellDiff = pCellData[r][c].nMatchCount
//                                    - pCellData[r][c].nTouchCount;
//
//                            if (nAdjCellDiff < nCurrCellDiff) {
//
//                                bValid = false;
//                            }
//
//                        }
//
//                    }
//
//                    if (bValid
//                            && (pCellData[row][col].nTouchCount + 1) < pCellData[row][col].nMatchCount
//                            || bNextIterationPlayerGameOverCheck
//                            || bGameOverbyPlayer) {
//
//                        cell[0] = row;
//                        cell[1] = col;
//
//                        System.out.println("minmatch analysis cell "
//                                + cell[0]
//                                + " , "
//                                + cell[1]);
//
//                        if (pCellData[row][col].nTouchCount + 1 < pCellData[row][col].nMatchCount)
//                            return cell;
//                    }
//
//                }
//
//            }
//
//        }
//
//        System.out.println("minmatch analysis failed cell " + cell[0]
//                + " , " + cell[1]);
//
//        return cell;
//
//    }

    private int getAdjacentCellInfo(int r, int c) {
        return c;

    }

//    private int getCornerState(int r, int c) {
//        // TODO Auto-generated method stub
//
//        if (!checkFitArray(r, c))
//            return -1;
//
//        // left top cell
//        if (pCellData[r][c].bEmpty && pCellData[r][c].bTop
//                && pCellData[r][c].bLeft
//                && !pCellData[r][c].bRight
//                && !pCellData[r][c].bBottom) {
//            return TOP_LEFT;
//        }
//
//        // right top cell
//        if (pCellData[r][c].bEmpty && pCellData[r][c].bTop
//                && pCellData[r][c].bRight
//                && !pCellData[r][c].bLeft
//                && !pCellData[r][c].bBottom) {
//            return TOP_RIGHT;
//        }
//
//        // top only excluded 2 edges
//        if (pCellData[r][c].bEmpty && pCellData[r][c].bTop
//                && !pCellData[r][c].bLeft
//                && !pCellData[r][c].bRight
//                && !pCellData[r][c].bBottom) {
//            return TOP;
//        }
//
//        // left only excluded 2 edges
//        if (pCellData[r][c].bEmpty && pCellData[r][c].bLeft
//                && !pCellData[r][c].bTop
//                && !pCellData[r][c].bRight
//                && !pCellData[r][c].bBottom) {
//            return LEFT;
//        }
//
//        // right only excluded 2 edges
//        if (pCellData[r][c].bEmpty && pCellData[r][c].bRight
//                && !pCellData[r][c].bLeft
//                && !pCellData[r][c].bTop
//                && !pCellData[r][c].bBottom) {
//            return RIGHT;
//        }
//
//        // bottom left
//
//        if (pCellData[r][c].bEmpty && pCellData[r][c].bBottom
//                && pCellData[r][c].bLeft
//                && !pCellData[r][c].bTop
//                && !pCellData[r][c].bRight) {
//            return BOTTOM_LEFT;
//        }
//
//        // bottom right
//        if (pCellData[r][c].bEmpty && pCellData[r][c].bBottom
//                && pCellData[r][c].bRight
//                & !pCellData[r][c].bTop
//                && !pCellData[r][c].bLeft) {
//            return BOTTOM_RIGHT;
//        }
//
//        // bottom only exluded 2 edges
//        if (pCellData[r][c].bEmpty && pCellData[r][c].bBottom
//                && !pCellData[r][c].bRight
//                & !pCellData[r][c].bTop
//                && !pCellData[r][c].bLeft) {
//            return BOTTOM;
//        }
//
//        return -1;
//
//    }

    private void resetSelectedArray() {

        for (int i = 0, len = nSelectedArr.length; i < len; i++) {
            nSelectedArr[i][0] = -1;
            nSelectedArr[i][1] = -1;

        }

    }

        /*
         * It finds cell which triggers now that makes more effect to player
         */

//    private int[] find_CurrentTriggerCell() {
//
//        int nPlayerPrevBoardCount = 0;
//        int cell[] = new int[2];
//        cell[0] = -1;
//        cell[1] = -1;
//
//        nNoOfTrigCell = 0;
//
//        for (int i = 0; i < NO_OF_CELL; i++) {
//            for (int j = 0; j < NO_OF_CELL; j++) {
//                if (!pCellData[i][j].bEmpty
//                        && pCellData[i][j].nPlayerID == nPlayerId) {
//                    nPlayerPrevBoardCount++;
//                }
//
//                copyCellDatatoCpuData(i, j);
//
//            }
//        }
//
//        System.out.println("prev player count on board "
//                + nPlayerPrevBoardCount);
//
//        boolean bFound = false;
//
//        for (int i = 0; i < NO_OF_CELL; i++) {
//            for (int j = 0; j < NO_OF_CELL; j++) {
//
//                bFound = false;
//
//                bFound = checkNextCpuEffectiveCell(i, j);
//
//                copyCellDatatoCpuData();
//
//                if (bFound) {
//                    cell[0] = i;
//                    cell[1] = j;
//
//                    System.out.println("gameover by cpu cell"
//                            + i + " , " + j);
//
//                    bGameOverbyCpu = true;
//
//                    return cell;
//                }
//
//                // copyCellDatatoCpuData(i,j);
//
//            }
//        }
//
//        // Tezting
//        System.out.println("Trigger Cell " + nNoOfTrigCell);
//        for (int i = 0; i < nNoOfTrigCell; i++) {
//            System.out.println(pTriggerCell[i].row
//                    + " "
//                    + pTriggerCell[i].col
//                    + " "
//                    + pTriggerCell[i].playercountonboard
//                    + " " + pTriggerCell[i].priority
//                    + " ");
//
//        }
//
//        //
//        int nTempPrevPlayerBoardCnt = 0;
//        int nBestCell = -1;
//        if (nNoOfTrigCell > 0) {
//
//            for (int a = 0; a < nNoOfTrigCell; a++) {
//
//                if (pTriggerCell[a].playercountonboard < nPlayerPrevBoardCount) {
//                    if (pTriggerCell[a].playercountonboard < nTempPrevPlayerBoardCnt
//                            || nTempPrevPlayerBoardCnt == 0) {
//                        nBestCell = a;
//                        nTempPrevPlayerBoardCnt = pTriggerCell[a].playercountonboard;
//                    }
//
//                }
//
//            }
//
//        }
//
//        if (nBestCell != -1) {
//            cell[0] = pTriggerCell[nBestCell].row;
//            cell[1] = pTriggerCell[nBestCell].col;
//
//        }
//
//        return cell;
//
//    }

    // Create Interest for user

//    private int[] getNotEffectiveClosestCelltoPlayer() {
//
//        int cell[] = new int[2];
//        cell[0] = -1;
//        cell[1] = -1;
//
//        int row = -1, col = -1;
//
//        for (int i = 0; i < NO_OF_CELL; i++) {
//            for (int j = 0; j < NO_OF_CELL; j++) {
//
//                if (!pCellData[i][j].bEmpty) {
//                    if (pCellData[i][j].nPlayerID == nPlayerId) {
//
//                        row = i - 1;
//                        col = j - 1;
//
//                        if (checkFitArray(row, col)
//                                && pCellData[row][col].bEmpty) {
//                            cell[0] = row;
//                            cell[1] = col;
//
//                            return cell;
//                        }
//
//                        row = i - 1;
//                        col = j + 1;
//
//                        if (checkFitArray(row, col)
//                                && pCellData[row][col].bEmpty) {
//                            cell[0] = row;
//                            cell[1] = col;
//
//                            return cell;
//                        }
//
//                        row = i + 1;
//                        col = j - 1;
//
//                        if (checkFitArray(row, col)
//                                && pCellData[row][col].bEmpty) {
//                            cell[0] = row;
//                            cell[1] = col;
//
//                            return cell;
//                        }
//
//                        row = i + 1;
//                        col = j + 1;
//
//                        if (checkFitArray(row, col)
//                                && pCellData[row][col].bEmpty) {
//                            cell[0] = row;
//                            cell[1] = col;
//
//                            return cell;
//                        }
//
//                    }
//                }
//
//            }
//        }
//
//        return cell;
//    }

    private int getCellAdjacentChainSum(int row, int col) {

        int nAdjSum = 0;

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col; c++) {
                if ((r == row && c == col)
                        && (r == row - 1 && c == col - 1)
                        && (r == row - 1 && c == col + 1)
                        && (r == row + 1 && c == col - 1)
                        && (r == row + 1 && c == col + 1)) {
                    continue;
                }

                if (!checkFitArray(r, c)
                        || pCellData[r][c].bEmpty)
                    continue;

                nAdjSum += pCellData[r][c].nTouchCount;

            }
        }

        return nAdjSum;
    }


    //new player cellanalysis
    /*

        checkNextPlayerEffectiveCell() returns array
        array[0]=0 or 1 , 1->CpuWin 0->Not CpuWin
        array[1]= CpuCount after PlayerTurn
        array[2]= CpuCount after CpuTurn


     */
    private int[] checkNextPlayerEffectiveCell(int i, int j) {

        int nNoofSel = 0;
        int row = -1, col = -1;
        int nPlayerBoardCnt = 0;
        boolean bValidCell = false;
        int nReturnData[] = new int[3];
        nReturnData[0] = 0;
        nReturnData[1] = 0;
        nReturnData[2] =0;


        resetSelectedArray();

        if (pCellDataPlayerTurn[i][j].nPlayerID == nPlayerId || pCellDataPlayerTurn[i][j].bEmpty) {


            if(pCellDataPlayerTurn[i][j].nTouchCount+1<pCellDataPlayerTurn[i][j].nMatchCount)
            {
                pCellDataPlayerTurn[i][j].bEmpty=false;
                pCellDataPlayerTurn[i][j].nTouchCount++;
                pCellDataPlayerTurn[i][j].nPlayerID=nPlayerId;

                for(int r=0;r<NO_OF_CELL;r++)
                {
                    for(int c=0;c<NO_OF_CELL;c++)
                    {
                        if(!pCellDataPlayerTurn[r][c].bEmpty&&pCellDataPlayerTurn[r][c].nPlayerID==nCpuId)
                        {
                            nReturnData[1]++;
                        }
                        if(!pCellDataPlayerTurn[r][c].bEmpty&&pCellDataPlayerTurn[r][c].nPlayerID==nPlayerId)
                        {
                            nReturnData[2]++;
                        }

                    }
                }

                return nReturnData;
            }

            if ((pCellDataPlayerTurn[i][j].nTouchCount + 1)
                    % pCellDataPlayerTurn[i][j].nMatchCount == 0) {
                pCellDataPlayerTurn[i][j].nTouchCount = 0;
                pCellDataPlayerTurn[i][j].bEmpty = true;

            } else {
                pCellDataPlayerTurn[i][j].nTouchCount = (pCellDataPlayerTurn[i][j].nTouchCount + 1)
                        % pCellDataPlayerTurn[i][j].nMatchCount;
            }

            // pCellDataCPUTurn[i][j].nTouchCount = 0;
            // pCellDataCPUTurn[i][j].bEmpty = true;

            row = i - 1;
            col = j;

            if (checkFitArray(row, col)) {

                pCellDataPlayerTurn[row][col].nPlayerID = nPlayerId;
                pCellDataPlayerTurn[row][col].nTouchCount++;
                pCellDataPlayerTurn[row][col].bEmpty = false;


                if (pCellDataPlayerTurn[row][col].nTouchCount >= pCellDataPlayerTurn[row][col].nMatchCount) {
                    nSelectedArr[nNoofSel][0] = row;
                    nSelectedArr[nNoofSel][1] = col;
                    nNoofSel++;

                }

            }

            row = i;
            col = j - 1;

            if (checkFitArray(row, col)) {

                pCellDataPlayerTurn[row][col].nPlayerID = nPlayerId;
                pCellDataPlayerTurn[row][col].nTouchCount++;
                pCellDataPlayerTurn[row][col].bEmpty = false;


                if (pCellDataPlayerTurn[row][col].nTouchCount >= pCellDataPlayerTurn[row][col].nMatchCount) {
                    nSelectedArr[nNoofSel][0] = row;
                    nSelectedArr[nNoofSel][1] = col;
                    nNoofSel++;

                }

            }

            row = i;
            col = j + 1;

            if (checkFitArray(row, col)) {

                pCellDataPlayerTurn[row][col].nPlayerID = nPlayerId;
                pCellDataPlayerTurn[row][col].nTouchCount++;
                pCellDataPlayerTurn[row][col].bEmpty = false;

                if (pCellDataPlayerTurn[row][col].nTouchCount >= pCellDataPlayerTurn[row][col].nMatchCount) {
                    nSelectedArr[nNoofSel][0] = row;
                    nSelectedArr[nNoofSel][1] = col;
                    nNoofSel++;

                }

            }

            row = i + 1;
            col = j;

            if (checkFitArray(row, col)) {

                pCellDataPlayerTurn[row][col].nPlayerID = nPlayerId;
                pCellDataPlayerTurn[row][col].nTouchCount++;
                pCellDataPlayerTurn[row][col].bEmpty = false;

                if (pCellDataPlayerTurn[row][col].nTouchCount >= pCellDataPlayerTurn[row][col].nMatchCount) {
                    nSelectedArr[nNoofSel][0] = row;
                    nSelectedArr[nNoofSel][1] = col;
                    nNoofSel++;

                }

            }

            if (checkGameOverforPlayer()) {
                nReturnData[0] = 1;
                return nReturnData;

            }


            int r, c;

            for (int k = 0; k < nNoofSel; k++) {

                row = nSelectedArr[k][0];
                col = nSelectedArr[k][1];

                if (checkFitArray(row, col)
                        && pCellDataPlayerTurn[row][col].nTouchCount >= pCellDataPlayerTurn[row][col].nMatchCount) {

                    if (pCellDataPlayerTurn[row][col].nTouchCount
                            % pCellDataPlayerTurn[row][col].nMatchCount == 0) {
                        pCellDataPlayerTurn[row][col].nTouchCount = 0;
                        pCellDataPlayerTurn[row][col].bEmpty = true;
                    } else {
                        pCellDataPlayerTurn[row][col].nTouchCount = pCellDataPlayerTurn[row][col].nTouchCount
                                % pCellDataPlayerTurn[row][col].nMatchCount;
                    }

                    r = row - 1;
                    c = col;

                    if (checkFitArray(r, c)) {

                        pCellDataPlayerTurn[r][c].nPlayerID = nPlayerId;
                        pCellDataPlayerTurn[r][c].nTouchCount++;

                        pCellDataPlayerTurn[r][c].bEmpty = false;

                        if (pCellDataPlayerTurn[r][c].nTouchCount >= pCellDataPlayerTurn[r][c].nMatchCount) {
                            nSelectedArr[nNoofSel][0] = r;
                            nSelectedArr[nNoofSel][1] = c;
                            nNoofSel++;
                        }


                        if (nNoofSel >= nSelectedArr.length) {
                            //System.out.println("arr limit excedded");
                            //return true;
                            nReturnData[0] = 1;
                            return nReturnData;
                        }

                    }

                    r = row;
                    c = col - 1;

                    if (checkFitArray(r, c)) {

                        pCellDataPlayerTurn[r][c].nPlayerID = nPlayerId;
                        pCellDataPlayerTurn[r][c].nTouchCount++;
                        pCellDataPlayerTurn[r][c].bEmpty = false;

                        if (pCellDataPlayerTurn[r][c].nTouchCount >= pCellDataPlayerTurn[r][c].nMatchCount) {
                            nSelectedArr[nNoofSel][0] = r;
                            nSelectedArr[nNoofSel][1] = c;
                            nNoofSel++;
                        }


                        if (nNoofSel >= nSelectedArr.length) {
//                                System.out.println("arr limit excedded");
//                                return true;
                            nReturnData[0] = 1;
                            return nReturnData;
                        }

                    }

                    r = row;
                    c = col + 1;

                    if (checkFitArray(r, c)) {

                        pCellDataPlayerTurn[r][c].nPlayerID = nPlayerId;
                        pCellDataPlayerTurn[r][c].nTouchCount++;
                        pCellDataPlayerTurn[r][c].bEmpty = false;

                        if (pCellDataPlayerTurn[r][c].nTouchCount >= pCellDataPlayerTurn[r][c].nMatchCount) {
                            nSelectedArr[nNoofSel][0] = r;
                            nSelectedArr[nNoofSel][1] = c;
                            nNoofSel++;
                        }

                        if (nNoofSel >= nSelectedArr.length) {
                            //System.out.println("arr limit excedded");
                            //return true;
                            nReturnData[0] = 1;
                            return nReturnData;
                        }

                    }

                    r = row + 1;
                    c = col;

                    if (checkFitArray(r, c)) {

                        pCellDataPlayerTurn[r][c].nPlayerID = nPlayerId;
                        pCellDataPlayerTurn[r][c].nTouchCount++;
                        pCellDataPlayerTurn[r][c].bEmpty = false;

                        if (pCellDataPlayerTurn[r][c].nTouchCount >= pCellDataPlayerTurn[r][c].nMatchCount) {
                            nSelectedArr[nNoofSel][0] = r;
                            nSelectedArr[nNoofSel][1] = c;
                            nNoofSel++;
                        }


                        if (nNoofSel >= nSelectedArr.length) {
                            //System.out.println("arr limit excedded");
                            //return true;
                            nReturnData[0] = 1;
                            return nReturnData;
                        }

                    }

                }

                if (checkGameOverforPlayer()) {
                    nReturnData[0] = 1;
                    return nReturnData;

                }

            }

        }

//            if (bValidCell)
//            {
//                // player on board after split
//
//                int nCpuBoardCnt = 0;
//
//                for (int r = 0; r < NO_OF_CELL; r++)
//                {
//                    for (int c = 0; c < NO_OF_CELL; c++)
//                    {
//                        if (!pCellDataPlayerTurn[r][c].bEmpty)
//
//                        {
//                            if (pCellDataPlayerTurn[r][c].nPlayerID == nPlayerId)
//                                nPlayerBoardCnt++;
//                            else
//                                nCpuBoardCnt++;
//                        }
//
//                    }
//                }
//
//                // copyCellDatatoCpuData();
//                pTriggerCell[nNoOfTrigCell].row = i;
//                pTriggerCell[nNoOfTrigCell].col = j;
//                pTriggerCell[nNoOfTrigCell].playercountonboard = nPlayerBoardCnt;
//                pTriggerCell[nNoOfTrigCell].cpucountonboard = nCpuBoardCnt;
//                pTriggerCell[nNoOfTrigCell].priority = 1;
//                nNoOfTrigCell++;
//
//                if (nCpuBoardCnt == 0)
//                {
//                    return true;
//                }
//
//            }

        // }

        nReturnData[1] = 0;
        nReturnData[2]=0;

        for (int r = 0; r < NO_OF_CELL; r++) {
            for (int c = 0; c < NO_OF_CELL; c++) {
                if (!pCellDataPlayerTurn[r][c].bEmpty && pCellDataPlayerTurn[r][c].nPlayerID == nCpuId)
                    nReturnData[1]++;
                if (!pCellDataPlayerTurn[r][c].bEmpty && pCellDataPlayerTurn[r][c].nPlayerID == nPlayerId)
                    nReturnData[2]++;
            }

        }


        return nReturnData;

    }


        /*
         * Check Effectiveness for Player Cell *
         */

//        private boolean checkNextPlayerEffectiveCell(int i, int j)
//            {
//
//                int nNoofSel = 0;
//                int row = -1, col = -1;
//                int nPlayerBoardCnt = 0;
//                boolean bValidCell = false;
//
//                resetSelectedArray();
//
//                if (pCellDataPlayerTurn[i][j].nPlayerID == nPlayerId)
//                    {
//
//                        if (pCellDataPlayerTurn[i][j].nTouchCount + 1 >= pCellDataPlayerTurn[i][j].nMatchCount)
//                            {
//                                bValidCell = true;
//                            }
//
//                        if (!bValidCell)
//                            return false;
//
//                        // nSelectedArr[0][0] = i;
//                        // nSelectedArr[0][1] = j;
//
//                        if ((pCellDataPlayerTurn[i][j].nTouchCount + 1)
//                                            % pCellDataPlayerTurn[i][j].nMatchCount == 0)
//                            {
//                                pCellDataPlayerTurn[i][j].nTouchCount = 0;
//                                pCellDataPlayerTurn[i][j].bEmpty = true;
//
//                            }
//
//                        else
//                            {
//                                pCellDataPlayerTurn[i][j].nTouchCount = (pCellDataPlayerTurn[i][j].nTouchCount + 1)
//                                                    % pCellDataPlayerTurn[i][j].nMatchCount;
//                            }
//
//                        // pCellDataCPUTurn[i][j].nTouchCount = 0;
//                        // pCellDataCPUTurn[i][j].bEmpty = true;
//
//                        row = i - 1;
//                        col = j;
//
//                        if (checkFitArray(row, col))
//                            {
//
//                                pCellDataPlayerTurn[row][col].nPlayerID = nPlayerId;
//                                pCellDataPlayerTurn[row][col].nTouchCount++;
//                                pCellDataPlayerTurn[row][col].bEmpty = false;
//
//                                nSelectedArr[nNoofSel][0] = row;
//                                nSelectedArr[nNoofSel][1] = col;
//                                nNoofSel++;
//
//                            }
//
//                        row = i;
//                        col = j - 1;
//
//                        if (checkFitArray(row, col))
//                            {
//
//                                pCellDataPlayerTurn[row][col].nPlayerID = nPlayerId;
//                                pCellDataPlayerTurn[row][col].nTouchCount++;
//                                pCellDataPlayerTurn[row][col].bEmpty = false;
//
//                                nSelectedArr[nNoofSel][0] = row;
//                                nSelectedArr[nNoofSel][1] = col;
//                                nNoofSel++;
//
//                            }
//
//                        row = i;
//                        col = j + 1;
//
//                        if (checkFitArray(row, col))
//                            {
//
//                                pCellDataPlayerTurn[row][col].nPlayerID = nPlayerId;
//                                pCellDataPlayerTurn[row][col].nTouchCount++;
//                                pCellDataPlayerTurn[row][col].bEmpty = false;
//
//                                nSelectedArr[nNoofSel][0] = row;
//                                nSelectedArr[nNoofSel][1] = col;
//                                nNoofSel++;
//
//                            }
//
//                        row = i + 1;
//                        col = j;
//
//                        if (checkFitArray(row, col))
//                            {
//
//                                pCellDataPlayerTurn[row][col].nPlayerID = nPlayerId;
//                                pCellDataPlayerTurn[row][col].nTouchCount++;
//                                pCellDataPlayerTurn[row][col].bEmpty = false;
//
//                                nSelectedArr[nNoofSel][0] = row;
//                                nSelectedArr[nNoofSel][1] = col;
//                                nNoofSel++;
//
//                            }
//
//                        if (checkGameOverforPlayer())
//                            return true;
//
//                        int r, c;
//
//                        for (int k = 0; k < nNoofSel; k++)
//                            {
//
//                                row = nSelectedArr[k][0];
//                                col = nSelectedArr[k][1];
//
//                                if (checkFitArray(row, col)
//                                                    && pCellDataPlayerTurn[row][col].nTouchCount >= pCellDataPlayerTurn[row][col].nMatchCount)
//                                    {
//
//                                        if (pCellDataPlayerTurn[row][col].nTouchCount
//                                                            % pCellDataPlayerTurn[row][col].nMatchCount == 0)
//                                            {
//                                                pCellDataPlayerTurn[row][col].nTouchCount = 0;
//                                                pCellDataPlayerTurn[row][col].bEmpty = true;
//                                            } else
//                                            {
//                                                pCellDataPlayerTurn[row][col].nTouchCount = pCellDataPlayerTurn[row][col].nTouchCount
//                                                                    % pCellDataPlayerTurn[row][col].nMatchCount;
//                                            }
//
//                                        r = row - 1;
//                                        c = col;
//
//                                        if (checkFitArray(r, c))
//                                            {
//
//                                                pCellDataPlayerTurn[r][c].nPlayerID = nPlayerId;
//                                                pCellDataPlayerTurn[r][c].nTouchCount++;
//
//                                                pCellDataPlayerTurn[r][c].bEmpty = false;
//                                                nSelectedArr[nNoofSel][0] = r;
//                                                nSelectedArr[nNoofSel][1] = c;
//                                                nNoofSel++;
//
//                                                if (nNoofSel >= nSelectedArr.length)
//                                                    {
//                                                        System.out.println("arr limit excedded");
//                                                        return true;
//                                                    }
//
//                                            }
//
//                                        r = row;
//                                        c = col - 1;
//
//                                        if (checkFitArray(r, c))
//                                            {
//
//                                                pCellDataPlayerTurn[r][c].nPlayerID = nPlayerId;
//                                                pCellDataPlayerTurn[r][c].nTouchCount++;
//                                                pCellDataPlayerTurn[r][c].bEmpty = false;
//
//                                                nSelectedArr[nNoofSel][0] = r;
//                                                nSelectedArr[nNoofSel][1] = c;
//                                                nNoofSel++;
//
//                                                if (nNoofSel >= nSelectedArr.length)
//                                                    {
//                                                        System.out.println("arr limit excedded");
//                                                        return true;
//                                                    }
//
//                                            }
//
//                                        r = row;
//                                        c = col + 1;
//
//                                        if (checkFitArray(r, c))
//                                            {
//
//                                                pCellDataPlayerTurn[r][c].nPlayerID = nPlayerId;
//                                                pCellDataPlayerTurn[r][c].nTouchCount++;
//                                                pCellDataPlayerTurn[r][c].bEmpty = false;
//
//                                                nSelectedArr[nNoofSel][0] = r;
//                                                nSelectedArr[nNoofSel][1] = c;
//                                                nNoofSel++;
//
//                                                if (nNoofSel >= nSelectedArr.length)
//                                                    {
//                                                        System.out.println("arr limit excedded");
//                                                        return true;
//                                                    }
//
//                                            }
//
//                                        r = row + 1;
//                                        c = col;
//
//                                        if (checkFitArray(r, c))
//                                            {
//
//                                                pCellDataPlayerTurn[r][c].nPlayerID = nPlayerId;
//                                                pCellDataPlayerTurn[r][c].nTouchCount++;
//                                                pCellDataPlayerTurn[r][c].bEmpty = false;
//                                                nSelectedArr[nNoofSel][0] = r;
//                                                nSelectedArr[nNoofSel][1] = c;
//                                                nNoofSel++;
//
//                                                if (nNoofSel >= nSelectedArr.length)
//                                                    {
//                                                        System.out.println("arr limit excedded");
//                                                        return true;
//                                                    }
//
//                                            }
//
//                                    }
//
//                                if (checkGameOverforPlayer())
//                                    return true;
//
//                            }
//
//                    }
//
//                if (bValidCell)
//                    {
//                        // player on board after split
//
//                        int nCpuBoardCnt = 0;
//
//                        for (int r = 0; r < NO_OF_CELL; r++)
//                            {
//                                for (int c = 0; c < NO_OF_CELL; c++)
//                                    {
//                                        if (!pCellDataPlayerTurn[r][c].bEmpty)
//
//                                            {
//                                                if (pCellDataPlayerTurn[r][c].nPlayerID == nPlayerId)
//                                                    nPlayerBoardCnt++;
//                                                else
//                                                    nCpuBoardCnt++;
//                                            }
//
//                                    }
//                            }
//
//                        // copyCellDatatoCpuData();
//                        pTriggerCell[nNoOfTrigCell].row = i;
//                        pTriggerCell[nNoOfTrigCell].col = j;
//                        pTriggerCell[nNoOfTrigCell].playercountonboard = nPlayerBoardCnt;
//                        pTriggerCell[nNoOfTrigCell].cpucountonboard = nCpuBoardCnt;
//                        pTriggerCell[nNoOfTrigCell].priority = 1;
//                        nNoOfTrigCell++;
//
//                        if (nCpuBoardCnt == 0)
//                            {
//                                return true;
//                            }
//
//                    }
//
//                // }
//
//                return false;
//
//            }

        /* Check Effectiveness for CPU cell */

//        private boolean checkNextCpuEffectiveCell(int i, int j)
//            {
//                int nNoofSel = 0;
//                int row = -1, col = -1;
//                int nPlayerBoardCnt = 0;
//                boolean bValidCell = false;
//
//                resetSelectedArray();
//
//                if (pCellDataCPUTurn[i][j].nPlayerID == nCpuId)
//                    {
//
//                        if (pCellDataCPUTurn[i][j].nTouchCount + 1 >= pCellDataCPUTurn[i][j].nMatchCount)
//                            {
//                                bValidCell = true;
//                            }
//
//                        if (!bValidCell)
//                            return false;
//
//                        // nSelectedArr[0][0] = i;
//                        // nSelectedArr[0][1] = j;
//
//                        if ((pCellDataCPUTurn[i][j].nTouchCount + 1)
//                                            % pCellDataCPUTurn[i][j].nMatchCount == 0)
//                            {
//                                pCellDataCPUTurn[i][j].nTouchCount = 0;
//                                pCellDataCPUTurn[i][j].bEmpty = true;
//
//                            }
//
//                        else
//                            {
//                                pCellDataCPUTurn[i][j].nTouchCount = (pCellDataCPUTurn[i][j].nTouchCount + 1)
//                                                    % pCellDataCPUTurn[i][j].nMatchCount;
//                            }
//
//                        row = i - 1;
//                        col = j;
//
//                        if (checkFitArray(row, col))
//                            {
//
//                                pCellDataCPUTurn[row][col].nPlayerID = nCpuId;
//                                pCellDataCPUTurn[row][col].nTouchCount++;
//                                pCellDataCPUTurn[row][col].bEmpty = false;
//
//                                nSelectedArr[nNoofSel][0] = row;
//                                nSelectedArr[nNoofSel][1] = col;
//                                nNoofSel++;
//
//                            }
//
//                        row = i;
//                        col = j - 1;
//
//                        if (checkFitArray(row, col))
//                            {
//
//                                pCellDataCPUTurn[row][col].nPlayerID = nCpuId;
//                                pCellDataCPUTurn[row][col].nTouchCount++;
//                                pCellDataCPUTurn[row][col].bEmpty = false;
//
//                                nSelectedArr[nNoofSel][0] = row;
//                                nSelectedArr[nNoofSel][1] = col;
//                                nNoofSel++;
//
//                            }
//
//                        row = i;
//                        col = j + 1;
//
//                        if (checkFitArray(row, col))
//                            {
//
//                                pCellDataCPUTurn[row][col].nPlayerID = nCpuId;
//                                pCellDataCPUTurn[row][col].nTouchCount++;
//                                pCellDataCPUTurn[row][col].bEmpty = false;
//
//                                nSelectedArr[nNoofSel][0] = row;
//                                nSelectedArr[nNoofSel][1] = col;
//                                nNoofSel++;
//
//                            }
//
//                        row = i + 1;
//                        col = j;
//
//                        if (checkFitArray(row, col))
//                            {
//
//                                pCellDataCPUTurn[row][col].nPlayerID = nCpuId;
//                                pCellDataCPUTurn[row][col].nTouchCount++;
//                                pCellDataCPUTurn[row][col].bEmpty = false;
//
//                                nSelectedArr[nNoofSel][0] = row;
//                                nSelectedArr[nNoofSel][1] = col;
//                                nNoofSel++;
//
//                            }
//
//                        if (checkGameOverforCPU())
//                            return true;
//
//                        int r, c;
//
//                        for (int k = 0; k < nNoofSel; k++)
//                            {
//
//                                row = nSelectedArr[k][0];
//                                col = nSelectedArr[k][1];
//
//                                if (checkFitArray(row, col)
//                                                    && pCellDataCPUTurn[row][col].nTouchCount >= pCellDataCPUTurn[row][col].nMatchCount)
//                                    {
//
//                                        if (pCellDataCPUTurn[row][col].nTouchCount
//                                                            % pCellDataCPUTurn[row][col].nMatchCount == 0)
//                                            {
//                                                pCellDataCPUTurn[row][col].nTouchCount = 0;
//                                                pCellDataCPUTurn[row][col].bEmpty = true;
//                                            } else
//                                            {
//                                                pCellDataCPUTurn[row][col].nTouchCount = pCellDataCPUTurn[row][col].nTouchCount
//                                                                    % pCellDataCPUTurn[row][col].nMatchCount;
//                                            }
//
//                                        r = row - 1;
//                                        c = col;
//
//                                        if (checkFitArray(r, c))
//                                            {
//
//                                                pCellDataCPUTurn[r][c].nPlayerID = nCpuId;
//                                                pCellDataCPUTurn[r][c].nTouchCount++;
//
//                                                pCellDataCPUTurn[r][c].bEmpty = false;
//                                                nSelectedArr[nNoofSel][0] = r;
//                                                nSelectedArr[nNoofSel][1] = c;
//                                                nNoofSel++;
//
//                                                if (nNoofSel >= nSelectedArr.length)
//                                                    {
//                                                        System.out.println("arr limit excedded");
//                                                        return true;
//                                                    }
//
//                                            }
//
//                                        r = row;
//                                        c = col - 1;
//
//                                        if (checkFitArray(r, c))
//                                            {
//
//                                                pCellDataCPUTurn[r][c].nPlayerID = nCpuId;
//                                                pCellDataCPUTurn[r][c].nTouchCount++;
//                                                pCellDataCPUTurn[r][c].bEmpty = false;
//
//                                                nSelectedArr[nNoofSel][0] = r;
//                                                nSelectedArr[nNoofSel][1] = c;
//                                                nNoofSel++;
//
//                                                if (nNoofSel >= nSelectedArr.length)
//                                                    {
//                                                        System.out.println("arr limit excedded");
//                                                        return true;
//                                                    }
//
//                                            }
//
//                                        r = row;
//                                        c = col + 1;
//
//                                        if (checkFitArray(r, c))
//                                            {
//
//                                                pCellDataCPUTurn[r][c].nPlayerID = nCpuId;
//                                                pCellDataCPUTurn[r][c].nTouchCount++;
//                                                pCellDataCPUTurn[r][c].bEmpty = false;
//
//                                                nSelectedArr[nNoofSel][0] = r;
//                                                nSelectedArr[nNoofSel][1] = c;
//                                                nNoofSel++;
//
//                                                if (nNoofSel >= nSelectedArr.length)
//                                                    {
//                                                        System.out.println("arr limit excedded");
//                                                        return true;
//                                                    }
//
//                                            }
//
//                                        r = row + 1;
//                                        c = col;
//
//                                        if (checkFitArray(r, c))
//                                            {
//
//                                                pCellDataCPUTurn[r][c].nPlayerID = nCpuId;
//                                                pCellDataCPUTurn[r][c].nTouchCount++;
//                                                pCellDataCPUTurn[r][c].bEmpty = false;
//                                                nSelectedArr[nNoofSel][0] = r;
//                                                nSelectedArr[nNoofSel][1] = c;
//                                                nNoofSel++;
//
//                                                if (nNoofSel >= nSelectedArr.length)
//                                                    {
//                                                        System.out.println("arr limit excedded");
//                                                        return true;
//                                                    }
//
//                                            }
//
//                                    }
//
//                                if (checkGameOverforCPU())
//                                    return true;
//
//                            }
//
//                    }
//
//                if (bValidCell)
//                    {
//                        // player on board after split
//
//                        int nCpuBoardCnt = 0;
//
//                        for (int r = 0; r < NO_OF_CELL; r++)
//                            {
//                                for (int c = 0; c < NO_OF_CELL; c++)
//                                    {
//                                        if (!pCellDataCPUTurn[r][c].bEmpty
//                                                            && pCellDataCPUTurn[r][c].nPlayerID == nPlayerId)
//                                            {
//                                                nPlayerBoardCnt++;
//                                            }
//                                        if (!pCellDataCPUTurn[r][c].bEmpty
//                                                            && pCellDataCPUTurn[r][c].nPlayerID == nCpuId)
//                                            {
//                                                nCpuBoardCnt++;
//                                            }
//
//                                    }
//                            }
//
//                        // copyCellDatatoCpuData();
//
//                        pTriggerCell[nNoOfTrigCell].row = i;
//                        pTriggerCell[nNoOfTrigCell].col = j;
//                        pTriggerCell[nNoOfTrigCell].playercountonboard = nPlayerBoardCnt;
//                        pTriggerCell[nNoOfTrigCell].cpucountonboard = nCpuBoardCnt;
//                        pTriggerCell[nNoOfTrigCell].priority = 1;
//                        nNoOfTrigCell++;
//
//                    }
//
//                // }
//
//                return false;
//
//            }


    /*
        checkNextCpuEffectiveCell() returns array
        array[0]=0 or 1 , 1->PlayerWin 0->No PlayerWin
        array[1]= PlayerCount after Cputurn
        array[2]= CpuCount after PlayerTurn

     */

    private int[] checkNextCpuEffectiveCell(int i, int j) {
        int nNoofSel = 0;
        int row = -1, col = -1;
        int nPlayerBoardCnt = 0;
        boolean bValidCell = false;
        int nReturnData[] = new int[3];

        nReturnData[0] = 0; //GAMEOVER STATUS 0=not gameover, 1=gameover
        nReturnData[1] = 0; //PlayerCount
        nReturnData[2] = 0; //cpu count

        resetSelectedArray();

        if (pCellDataCPUTurn[i][j].nPlayerID == nCpuId || pCellDataCPUTurn[i][j].bEmpty) {

            if(pCellDataCPUTurn[i][j].nTouchCount+1<pCellDataCPUTurn[i][j].nMatchCount)
            {
                pCellDataCPUTurn[i][j].bEmpty=false;
                pCellDataCPUTurn[i][j].nTouchCount++;
                pCellDataCPUTurn[i][j].nPlayerID=nCpuId;

                for(int r=0;r<NO_OF_CELL;r++)
                {
                    for(int c=0;c<NO_OF_CELL;c++)
                    {
                        if(!pCellDataCPUTurn[r][c].bEmpty&&pCellDataCPUTurn[r][c].nPlayerID==nPlayerId)
                        {
                            nReturnData[1]++;
                        }
                        if(!pCellDataCPUTurn[r][c].bEmpty&&pCellDataCPUTurn[r][c].nPlayerID==nCpuId)
                        {
                            nReturnData[2]++;
                        }
                    }
                }

                return  nReturnData;
            }

            if ((pCellDataCPUTurn[i][j].nTouchCount + 1)
                    % pCellDataCPUTurn[i][j].nMatchCount == 0) {
                pCellDataCPUTurn[i][j].nTouchCount = 0;
                pCellDataCPUTurn[i][j].bEmpty = true;

            } else {
                pCellDataCPUTurn[i][j].nTouchCount = (pCellDataCPUTurn[i][j].nTouchCount + 1)
                        % pCellDataCPUTurn[i][j].nMatchCount;
            }

            row = i - 1;
            col = j;

            if (checkFitArray(row, col)) {

                pCellDataCPUTurn[row][col].nPlayerID = nCpuId;
                pCellDataCPUTurn[row][col].nTouchCount++;
                pCellDataCPUTurn[row][col].bEmpty = false;

                if (pCellDataCPUTurn[row][col].nTouchCount >= pCellDataCPUTurn[row][col].nMatchCount) {
                    nSelectedArr[nNoofSel][0] = row;
                    nSelectedArr[nNoofSel][1] = col;
                    nNoofSel++;
                }


            }

            row = i;
            col = j - 1;

            if (checkFitArray(row, col)) {

                pCellDataCPUTurn[row][col].nPlayerID = nCpuId;
                pCellDataCPUTurn[row][col].nTouchCount++;
                pCellDataCPUTurn[row][col].bEmpty = false;

                if (pCellDataCPUTurn[row][col].nTouchCount >= pCellDataCPUTurn[row][col].nMatchCount) {
                    nSelectedArr[nNoofSel][0] = row;
                    nSelectedArr[nNoofSel][1] = col;
                    nNoofSel++;
                }

            }

            row = i;
            col = j + 1;

            if (checkFitArray(row, col)) {

                pCellDataCPUTurn[row][col].nPlayerID = nCpuId;
                pCellDataCPUTurn[row][col].nTouchCount++;
                pCellDataCPUTurn[row][col].bEmpty = false;

                if (pCellDataCPUTurn[row][col].nTouchCount >= pCellDataCPUTurn[row][col].nMatchCount) {
                    nSelectedArr[nNoofSel][0] = row;
                    nSelectedArr[nNoofSel][1] = col;
                    nNoofSel++;
                }

            }

            row = i + 1;
            col = j;

            if (checkFitArray(row, col)) {

                pCellDataCPUTurn[row][col].nPlayerID = nCpuId;
                pCellDataCPUTurn[row][col].nTouchCount++;
                pCellDataCPUTurn[row][col].bEmpty = false;

                if (pCellDataCPUTurn[row][col].nTouchCount >= pCellDataCPUTurn[row][col].nMatchCount) {
                    nSelectedArr[nNoofSel][0] = row;
                    nSelectedArr[nNoofSel][1] = col;
                    nNoofSel++;
                }

            }

            if (checkGameOverforCPU()) {
                nReturnData[0] = 1;
                return nReturnData;
            }


            int r, c;

            for (int k = 0; k < nNoofSel; k++) {

                row = nSelectedArr[k][0];
                col = nSelectedArr[k][1];

                if (checkFitArray(row, col)
                        && pCellDataCPUTurn[row][col].nTouchCount >= pCellDataCPUTurn[row][col].nMatchCount) {

                    if (pCellDataCPUTurn[row][col].nTouchCount
                            % pCellDataCPUTurn[row][col].nMatchCount == 0) {
                        pCellDataCPUTurn[row][col].nTouchCount = 0;
                        pCellDataCPUTurn[row][col].bEmpty = true;
                    } else {
                        pCellDataCPUTurn[row][col].nTouchCount = pCellDataCPUTurn[row][col].nTouchCount
                                % pCellDataCPUTurn[row][col].nMatchCount;
                    }

                    r = row - 1;
                    c = col;

                    if (checkFitArray(r, c)) {

                        pCellDataCPUTurn[r][c].nPlayerID = nCpuId;
                        pCellDataCPUTurn[r][c].nTouchCount++;

                        pCellDataCPUTurn[r][c].bEmpty = false;

                        if (pCellDataCPUTurn[r][c].nTouchCount >= pCellDataCPUTurn[r][c].nMatchCount) {
                            nSelectedArr[nNoofSel][0] = r;
                            nSelectedArr[nNoofSel][1] = c;
                            nNoofSel++;
                        }


                        if (nNoofSel >= nSelectedArr.length) {
                            //System.out.println("arr limit excedded");
                            //return true;
                            nReturnData[0] = 1;
                            return nReturnData;
                        }

                    }

                    r = row;
                    c = col - 1;

                    if (checkFitArray(r, c)) {

                        pCellDataCPUTurn[r][c].nPlayerID = nCpuId;
                        pCellDataCPUTurn[r][c].nTouchCount++;
                        pCellDataCPUTurn[r][c].bEmpty = false;

                        if (pCellDataCPUTurn[r][c].nTouchCount >= pCellDataCPUTurn[r][c].nMatchCount) {
                            nSelectedArr[nNoofSel][0] = r;
                            nSelectedArr[nNoofSel][1] = c;
                            nNoofSel++;

                        }

                        if (nNoofSel >= nSelectedArr.length) {
                            //System.out.println("arr limit excedded");
                            //return true;
                            nReturnData[0] = 1;
                            return nReturnData;
                        }

                    }

                    r = row;
                    c = col + 1;

                    if (checkFitArray(r, c)) {

                        pCellDataCPUTurn[r][c].nPlayerID = nCpuId;
                        pCellDataCPUTurn[r][c].nTouchCount++;
                        pCellDataCPUTurn[r][c].bEmpty = false;

                        if (pCellDataCPUTurn[r][c].nTouchCount >= pCellDataCPUTurn[r][c].nMatchCount) {
                            nSelectedArr[nNoofSel][0] = r;
                            nSelectedArr[nNoofSel][1] = c;
                            nNoofSel++;
                        }


                        if (nNoofSel >= nSelectedArr.length) {
                            System.out.println("arr limit excedded");
                            //return true;
                            nReturnData[0] = 1;
                            return nReturnData;
                        }

                    }

                    r = row + 1;
                    c = col;

                    if (checkFitArray(r, c)) {

                        pCellDataCPUTurn[r][c].nPlayerID = nCpuId;
                        pCellDataCPUTurn[r][c].nTouchCount++;
                        pCellDataCPUTurn[r][c].bEmpty = false;

                        if (pCellDataCPUTurn[r][c].nTouchCount >= pCellDataCPUTurn[r][c].nMatchCount) {
                            nSelectedArr[nNoofSel][0] = r;
                            nSelectedArr[nNoofSel][1] = c;
                            nNoofSel++;

                        }


                        if (nNoofSel >= nSelectedArr.length) {
                            //System.out.println("arr limit excedded");
                            //return true;
                            nReturnData[0] = 1;
                            return nReturnData;
                        }

                    }

                }

                if (checkGameOverforCPU()) {
                    nReturnData[0] = 1;
                    return nReturnData;
                }


            }

        }


        nReturnData[0]=0;
        nReturnData[1] = 0;
        nReturnData[2] = 0;


        for (int r = 0; r < NO_OF_CELL; r++) {
            for (int c = 0; c < NO_OF_CELL; c++) {
                if (!pCellDataCPUTurn[r][c].bEmpty && pCellDataCPUTurn[r][c].nPlayerID == nPlayerId)
                    nReturnData[1]++;
                if (!pCellDataCPUTurn[r][c].bEmpty && pCellDataCPUTurn[r][c].nPlayerID == nCpuId)
                    nReturnData[2]++;
            }
        }

        return nReturnData;

    }

    private void copyCpuDatatoPlayerData() {

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {

                // System.out.println("copycputoplayer");
                // System.out.println(i+","+j);
                // System.out.println(pCellDataCPUTurn[i][j].nPlayerID);
                // System.out.println(pCellDataCPUTurn[i][j].nImageId);
                // System.out.println(pCellDataCPUTurn[i][j].nCellType);
                // System.out.println(pCellDataCPUTurn[i][j].nTouchCount);
                // System.out.println(pCellDataCPUTurn[i][j].nPlayerOldID);
                // System.out.println(pCellDataCPUTurn[i][j].nAnimImageId);
                // System.out.println(pCellDataCPUTurn[i][j].bEmpty);
                // System.out.println(pCellDataCPUTurn[i][j].bExtraAnim1);
                //
                pCellDataPlayerTurn[i][j].nPlayerID = pCellDataCPUTurn[i][j].nPlayerID;
                pCellDataPlayerTurn[i][j].nImageId = pCellDataCPUTurn[i][j].nImageId;
                pCellDataPlayerTurn[i][j].nCellType = pCellDataCPUTurn[i][j].nCellType;
                pCellDataPlayerTurn[i][j].nTouchCount = pCellDataCPUTurn[i][j].nTouchCount;
                pCellDataPlayerTurn[i][j].nPlayerOldID = pCellDataCPUTurn[i][j].nPlayerOldID;
                pCellDataPlayerTurn[i][j].nAnimImageId = pCellDataCPUTurn[i][j].nAnimImageId;
                pCellDataPlayerTurn[i][j].bEmpty = pCellDataCPUTurn[i][j].bEmpty;
                pCellDataPlayerTurn[i][j].bExtraAnim1 = pCellDataCPUTurn[i][j].bExtraAnim1;
                pCellDataPlayerTurn[i][j].bExtraAnim2 = pCellDataCPUTurn[i][j].bExtraAnim2;
                pCellDataPlayerTurn[i][j].bExtraAnim3 = pCellDataCPUTurn[i][j].bExtraAnim3;
                pCellDataPlayerTurn[i][j].bExtraAnim4 = pCellDataCPUTurn[i][j].bExtraAnim4;

            }
        }

    }

    private void copyCpuDatatoPlayerData(int i, int j) {
        pCellDataPlayerTurn[i][j].nPlayerID = pCellDataCPUTurn[i][j].nPlayerID;
        pCellDataPlayerTurn[i][j].nImageId = pCellDataCPUTurn[i][j].nImageId;
        pCellDataPlayerTurn[i][j].nCellType = pCellDataCPUTurn[i][j].nCellType;
        pCellDataPlayerTurn[i][j].nTouchCount = pCellDataCPUTurn[i][j].nTouchCount;
        pCellDataPlayerTurn[i][j].nPlayerOldID = pCellDataCPUTurn[i][j].nPlayerOldID;
        pCellDataPlayerTurn[i][j].nAnimImageId = pCellDataCPUTurn[i][j].nAnimImageId;
        pCellDataPlayerTurn[i][j].bEmpty = pCellDataCPUTurn[i][j].bEmpty;
        pCellDataPlayerTurn[i][j].bExtraAnim1 = pCellDataCPUTurn[i][j].bExtraAnim1;
        pCellDataPlayerTurn[i][j].bExtraAnim2 = pCellDataCPUTurn[i][j].bExtraAnim2;
        pCellDataPlayerTurn[i][j].bExtraAnim3 = pCellDataCPUTurn[i][j].bExtraAnim3;
        pCellDataPlayerTurn[i][j].bExtraAnim4 = pCellDataCPUTurn[i][j].bExtraAnim4;

    }

        /*
         * It Copies Important CellData for Analysis
         */

    private void copyCellDatatoCpuData() {

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {
                pCellDataCPUTurn[i][j].nPlayerID = pCellData[i][j].nPlayerID;
                pCellDataCPUTurn[i][j].nImageId = pCellData[i][j].nImageId;
                pCellDataCPUTurn[i][j].nCellType = pCellData[i][j].nCellType;
                pCellDataCPUTurn[i][j].nTouchCount = pCellData[i][j].nTouchCount;
                pCellDataCPUTurn[i][j].nPlayerOldID = pCellData[i][j].nPlayerOldID;
                pCellDataCPUTurn[i][j].nAnimImageId = pCellData[i][j].nAnimImageId;
                pCellDataCPUTurn[i][j].bEmpty = pCellData[i][j].bEmpty;
                pCellDataCPUTurn[i][j].bExtraAnim1 = pCellData[i][j].bExtraAnim1;
                pCellDataCPUTurn[i][j].bExtraAnim2 = pCellData[i][j].bExtraAnim2;
                pCellDataCPUTurn[i][j].bExtraAnim3 = pCellData[i][j].bExtraAnim3;
                pCellDataCPUTurn[i][j].bExtraAnim4 = pCellData[i][j].bExtraAnim4;

            }
        }

    }

    private void copyCellDatatoCpuData(int i, int j) {
        pCellDataCPUTurn[i][j].nPlayerID = pCellData[i][j].nPlayerID;
        pCellDataCPUTurn[i][j].nImageId = pCellData[i][j].nImageId;
        pCellDataCPUTurn[i][j].nCellType = pCellData[i][j].nCellType;
        pCellDataCPUTurn[i][j].nTouchCount = pCellData[i][j].nTouchCount;
        pCellDataCPUTurn[i][j].nPlayerOldID = pCellData[i][j].nPlayerOldID;
        pCellDataCPUTurn[i][j].nAnimImageId = pCellData[i][j].nAnimImageId;
        pCellDataCPUTurn[i][j].bEmpty = pCellData[i][j].bEmpty;
        pCellDataCPUTurn[i][j].bExtraAnim1 = pCellData[i][j].bExtraAnim1;
        pCellDataCPUTurn[i][j].bExtraAnim2 = pCellData[i][j].bExtraAnim2;
        pCellDataCPUTurn[i][j].bExtraAnim3 = pCellData[i][j].bExtraAnim3;
        pCellDataCPUTurn[i][j].bExtraAnim4 = pCellData[i][j].bExtraAnim4;

    }

        /*
         * It checks Cell Validation for players
         */

    private void checkCombinations(float x, float y) {
        // TODO Auto-generated method stub

        int nCol = -1, nRow = -1, cell[] = new int[2];

        if (bGameOver || bSplit)
            return;

        switch (Constants.nMode) {
            case Constants.MODE_DEMO:

                nRow = (int) x;
                nCol = (int) y;

                break;

            case Constants.MODE_SINGLEPLAYER:

                if (!isCPUTurn()) {

                    if (!checkCanvasBounds(x, y))
                        return;

                    cell = getCellFromCoordinates(x, y);
                    nCol = cell[0];
                    nRow = cell[1];

                } else {
                    nRow = (int) x;
                    nCol = (int) y;
                }
                break;

            default:

                if (!checkCanvasBounds(x, y))
                    return;

                cell = getCellFromCoordinates(x, y);
                nCol = cell[0];
                nRow = cell[1];

                break;
        }

        if (checkFitArray(nRow, nCol)) {
            if (pCellData[nRow][nCol].bExtraAnim1
                    || pCellData[nRow][nCol].bExtraAnim2
                    || pCellData[nRow][nCol].bExtraAnim3
                    || pCellData[nRow][nCol].bExtraAnim3)
                return;
        }

        if (nRow >= 0 && nRow < NO_OF_CELL && nCol >= 0
                && nCol < NO_OF_CELL) {

            nTouchCount++;

            nTouchCount = nTouchCount % nNoofPlayers;

            nCurrentPlayerID = nTouchCount;

            if (pCellData[nRow][nCol].bEmpty) {
                pCellData[nRow][nCol].nPlayerID = nCurrentPlayerID;
                pCellData[nRow][nCol].bEmpty = false;
            } else {
                if (pCellData[nRow][nCol].nPlayerID != nCurrentPlayerID) {
                    nTouchCount = nPreviousPlayerID;
                    return;
                }

            }

            nCurrentPlayerID = pCellData[nRow][nCol].nPlayerID;
            nPreviousPlayerID = nTouchCount;

            pCellData[nRow][nCol].nTouchCount++;

            if (Constants.bSound) {
                pMedia_BoardTouch.start();
            }

            // always cpu turn is second

            if (pCellData[nRow][nCol].nTouchCount >= pCellData[nRow][nCol].nMatchCount) {
                // pCellData[nRow][nCol].nImageId =
                // nPlayerImage[pCellData[nRow][nCol].nPlayerID][pCellData[nRow][nCol].nTouchCount
                // - 1];

                addSelectionCombinations(nRow, nCol);

            } else {
                pCellData[nRow][nCol].nImageId = nPlayerImage[pCellData[nRow][nCol].nPlayerID][pCellData[nRow][nCol].nTouchCount - 1];

                if (Constants.nMode == Constants.MODE_SINGLEPLAYER
                        || Constants.nMode == Constants.MODE_DEMO) {
                    if (!isCPUTurn()) {
                        setCPUTurn(true);
                        System.out.println("not in split");
                        System.out.println("processing sep thread");
                        lSystemThinkingTime = System
                                .currentTimeMillis();
                        nChoosenCell[0] = -1;
                        nChoosenCell[1] = -1;

                        (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int cell[] = new int[2];
                                cell = findBestCell();

                                // checkCombinations(cell[0],
                                // cell[1]);
                                nChoosenCell = cell;
                                System.out.println("Complted sep thread");
                                System.out.println("lSystemThinkingTime"
                                        + (System.currentTimeMillis() - lSystemThinkingTime));
                            }

                        })).start();

                    } else {
                        setCPUTurn(false);
                    }

                }

            }

            nNextPlayerId = (nTouchCount + 1) % nNoofPlayers;

            if (!isCPUTurn())
                nMoves--;

            nTaps++;

            if (nMoves <= 0)
                nMoves = 0;

        }

        // Toast.makeText(pActivityContext,
        // "test EVENT "+nRow+","+nCol, Toast.LENGTH_SHORT).show();

    }

    public int[] getCellFromCoordinates(float x, float y) {
        int cell[] = new int[2];

        cell[0] = (int) ((x - pDrawUtil.scaleX(CANVAS_X)) / pDrawUtil
                .scaleX(CELL_WIDTH));
        cell[1] = (int) ((y - pDrawUtil.scaleY(CANVAS_Y)) / pDrawUtil
                .scaleY(CELL_HEIGHT));

        return cell;

    }

    public boolean isPointInsideScreen(float x, float y) {
        int nCanvasX = (int) pDrawUtil.scaleX(0);
        int nCanvasY = (int) pDrawUtil.scaleY(0);
        int nBoundX = (int) pDrawUtil.scaleX(Constants.DEFAULTWIDTH);
        int nBoundY = (int) pDrawUtil.scaleY(Constants.DEFAULTHEIGHT);

        if (x >= nCanvasX && x <= nBoundX && y >= nCanvasY
                && y <= nBoundY) {
            return true;
        }

        return false;

    }

    public boolean checkCanvasBounds(float x, float y) {

        int nCanvasX = (int) pDrawUtil.scaleX(CANVAS_X);
        int nCanvasY = (int) pDrawUtil.scaleY(CANVAS_Y);
        int nBoundX = (int) pDrawUtil.scaleX(CANVAS_X
                + (NO_OF_CELL * CELL_WIDTH));
        int nBoundY = (int) pDrawUtil.scaleY(CANVAS_Y
                + (NO_OF_CELL * CELL_HEIGHT));

        if (!(x >= nCanvasX && x <= nBoundX && y >= nCanvasY && y <= nBoundY)) {
            return false;
        }

        return true;
    }

        /*
         * It is called by checkCombination of Cell which is going to split
         */

    public void checkRecursiveSplit(int row, int col) {
        addSelectionCombinations(row, col);

    }

    public float[] calcUnitVector(float srcVectX, float srcVectY,
                                  float dstVectX, float dstVectY) {
        float fDelX = dstVectX - srcVectX;
        float fDelY = dstVectY - srcVectY;
        float fDist = (float) Math.sqrt((fDelX * fDelX)
                + (fDelY * fDelY));
        float fUnitX = fDelX / fDist;
        float fUnitY = fDelY / fDist;

        float[] unitVect = new float[2];

        unitVect[0] = fUnitX;
        unitVect[1] = fUnitY;

        return unitVect;

    }

    public float calcVectorDistance(float srcVectX, float srcVectY,
                                    float dstVectX, float dstVectY) {
        float fDelX = dstVectX - srcVectX;
        float fDelY = dstVectY - srcVectY;
        float fDist = (float) Math.sqrt((fDelX * fDelX)
                + (fDelY * fDelY));

        return fDist;

    }

    public float calcVectorDotProduct(float delX1, float delY1,
                                      float delX2, float delY2) {

        return (delX1 * delX2) + (delY1 * delY2);

    }

    private void addSelectionCombinations(int row, int col) {

        pCellData[row][col].bEmpty = true;
        pCellData[row][col].nTouchCount = 0;
        pCellData[row][col].nImageId = -1;
        bSplit = true;

        bSplitSoundOn = true;

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {

                if ((r == row - 1 && c != col)
                        || (r == row && c == col)
                        || (r == row + 1 && c != col))
                    continue;

                if (checkFitArray(r, c)) {

                    if (!pCellData[r][c].bExtraAnim1) {

                        pCellData[r][c].AnimX1 = pCellData[row][col].PosX
                                + ROW_PLUS;
                        pCellData[r][c].AnimY1 = pCellData[row][col].PosY
                                + COL_PLUS;
                        pCellData[r][c].StartX1 = pCellData[row][col].PosX
                                + ROW_PLUS;
                        pCellData[r][c].StartY1 = pCellData[row][col].PosY
                                + COL_PLUS;
                        pCellData[r][c].TargetX1 = pCellData[r][c].PosX
                                + ROW_PLUS;
                        pCellData[r][c].TargetY1 = pCellData[r][c].PosY
                                + COL_PLUS;

                        pCellData[r][c].bExtraAnim1 = true;

                        float[] nUnitVector = new float[2];
                        nUnitVector = calcUnitVector(
                                pCellData[r][c].AnimX1,
                                pCellData[r][c].AnimY1,
                                pCellData[r][c].TargetX1,
                                pCellData[r][c].TargetY1);

                        pCellData[r][c].fUnitVectX1 = nUnitVector[0];
                        pCellData[r][c].fUnitVectY1 = nUnitVector[1];

                        pCellData[r][c].nAnimImageId = nPlayerImage[pCellData[row][col].nPlayerID][0];
                        pCellData[r][c].bAnim = true;

                    } else if (!pCellData[r][c].bExtraAnim2) {

                        pCellData[r][c].AnimX2 = pCellData[row][col].PosX
                                + ROW_PLUS;
                        pCellData[r][c].AnimY2 = pCellData[row][col].PosY
                                + COL_PLUS;
                        pCellData[r][c].StartX2 = pCellData[row][col].PosX
                                + ROW_PLUS;
                        pCellData[r][c].StartY2 = pCellData[row][col].PosY
                                + COL_PLUS;
                        pCellData[r][c].TargetX2 = pCellData[r][c].PosX
                                + ROW_PLUS;
                        pCellData[r][c].TargetY2 = pCellData[r][c].PosY
                                + COL_PLUS;

                        pCellData[r][c].bExtraAnim2 = true;

                        float[] nUnitVector = new float[2];
                        nUnitVector = calcUnitVector(
                                pCellData[r][c].AnimX2,
                                pCellData[r][c].AnimY2,
                                pCellData[r][c].TargetX2,
                                pCellData[r][c].TargetY2);

                        pCellData[r][c].fUnitVectX2 = nUnitVector[0];
                        pCellData[r][c].fUnitVectY2 = nUnitVector[1];

                        pCellData[r][c].nAnimImageId = nPlayerImage[pCellData[row][col].nPlayerID][0];
                        pCellData[r][c].bAnim = true;

                    } else if (!pCellData[r][c].bExtraAnim3) {

                        pCellData[r][c].AnimX3 = pCellData[row][col].PosX
                                + ROW_PLUS;
                        pCellData[r][c].AnimY3 = pCellData[row][col].PosY
                                + COL_PLUS;
                        pCellData[r][c].StartX3 = pCellData[row][col].PosX
                                + ROW_PLUS;
                        pCellData[r][c].StartY3 = pCellData[row][col].PosY
                                + COL_PLUS;
                        pCellData[r][c].TargetX3 = pCellData[r][c].PosX
                                + ROW_PLUS;
                        pCellData[r][c].TargetY3 = pCellData[r][c].PosY
                                + COL_PLUS;

                        pCellData[r][c].bExtraAnim3 = true;

                        float[] nUnitVector = new float[2];
                        nUnitVector = calcUnitVector(
                                pCellData[r][c].AnimX3,
                                pCellData[r][c].AnimY3,
                                pCellData[r][c].TargetX3,
                                pCellData[r][c].TargetY3);

                        pCellData[r][c].fUnitVectX3 = nUnitVector[0];
                        pCellData[r][c].fUnitVectY3 = nUnitVector[1];

                        pCellData[r][c].nAnimImageId = nPlayerImage[pCellData[row][col].nPlayerID][0];

                        pCellData[r][c].bAnim = true;

                    } else {

                        pCellData[r][c].AnimX4 = pCellData[row][col].PosX
                                + ROW_PLUS;
                        pCellData[r][c].AnimY4 = pCellData[row][col].PosY
                                + COL_PLUS;
                        pCellData[r][c].StartX4 = pCellData[row][col].PosX
                                + ROW_PLUS;
                        pCellData[r][c].StartY4 = pCellData[row][col].PosY
                                + COL_PLUS;
                        pCellData[r][c].TargetX4 = pCellData[r][c].PosX
                                + ROW_PLUS;
                        pCellData[r][c].TargetY4 = pCellData[r][c].PosY
                                + COL_PLUS;

                        pCellData[r][c].bExtraAnim4 = true;

                        float[] nUnitVector = new float[2];
                        nUnitVector = calcUnitVector(
                                pCellData[r][c].AnimX4,
                                pCellData[r][c].AnimY4,
                                pCellData[r][c].TargetX4,
                                pCellData[r][c].TargetY4);

                        pCellData[r][c].fUnitVectX4 = nUnitVector[0];
                        pCellData[r][c].fUnitVectY4 = nUnitVector[1];

                        pCellData[r][c].nAnimImageId = nPlayerImage[pCellData[row][col].nPlayerID][0];
                        pCellData[r][c].bAnim = true;

                    }

                }

            }
        }

        // checkGameOver();

    }

    public boolean checkFitArray(int row, int col) {

        if (row >= 0 && row < NO_OF_CELL && col >= 0
                && col < NO_OF_CELL) {
            return true;
        }

        return false;

    }

    public void drawGameDescripion(Canvas canvas, float startFrame,
                                   long elapsedTime) {
        String sModeSpecific = Constants.nMode == Constants.MODE_SINGLEPLAYER ? "CPU"
                : "OPPONENT";
        String sModeDesc = "";

        switch (Constants.nSubMode) {
            case Constants.MODE_MOVES:
                sModeDesc = "Beat the " + sModeSpecific + " in given moves";
                break;
            case Constants.MODE_INFINITE:
                sModeDesc = "Beat the " + sModeSpecific;
                break;
            case Constants.MODE_TIMED:
                sModeDesc = "Beat " + sModeSpecific + " in given time";
                break;
        }


        pDrawUtil.drawRect(canvas, 0, 0,
                Constants.DEFAULTWIDTH,
                Constants.DEFAULTHEIGHT, 2,
                Color.BLACK,
                Color.argb(200, 80, 80, 80));

        pDrawUtil.drawStrokeText(canvas, sModeDesc, 150,
                230, 20, Color.WHITE, Color.BLACK,
                2);

        pDrawUtil.drawStrokeText(canvas, "Tap to Continue ...",
                170, 260, 18, Color.YELLOW,
                Color.BLACK, 2);


    }

    public void drawModeInfo(Canvas canvas, float startFrame,
                             long elapsedTime) {

        if (Constants.nSubMode == Constants.MODE_TIMED) {

            String sMins = "";
            String sSecs = "";

            sMins = (String) ((nMins < 10) ? "0" + nMins : ""
                    + nMins);
            sSecs = (String) ((nSecs < 10) ? "0" + nSecs : ""
                    + nSecs);

            pDrawUtil.drawStrokeText(canvas, "Time", 38, 15, 12,
                    Color.BLACK, Color.WHITE, 2);

            pDrawUtil.drawStrokeText(canvas, sMins + " : " + sSecs,
                    40, 32, 12, Color.BLACK,
                    Color.WHITE, 2);

            if (nMins >= nTotalTime && !bSplit)
                bGameOver = true;

        }

        if (Constants.nSubMode == Constants.MODE_MOVES) {

            // pDrawUtil.drawCircle(canvas, 35, 25, 20, Color.WHITE,
            // Color.BLACK, 2);
            pDrawUtil.drawStrokeText(canvas, "Moves", 32, 15, 12,
                    Color.BLACK, Color.WHITE, 2);

            pDrawUtil.drawStrokeText(canvas, "" + nMoves, 30, 32,
                    12, Color.BLACK, Color.WHITE, 2);

        }

        if (Constants.nMode == Constants.MODE_SINGLEPLAYER) {
            // pDrawUtil.drawText(canvas, "Moves " + nMoves, 20, 15,
            // 12, Color.BLACK);

            if (isCPUTurn()) {
                pDrawUtil.drawStrokeText(canvas, "CPU Turn",
                        150, 30, 12, Color.BLACK,
                        Color.WHITE, 2);
            } else {
                pDrawUtil.drawStrokeText(canvas, "PLAYER Turn",
                        150, 30, 12, Color.BLACK,
                        Color.WHITE, 2);
            }

        }

        if (Constants.nMode == Constants.MODE_MUTIPLAYER) {

            int nTempPlayerId = (nNextPlayerId == -1) ? 0
                    : nNextPlayerId;

            pDrawUtil.drawImage(
                    canvas,
                    pBitmap_mgr.getBitmap(nPlayerImage[(nTempPlayerId)
                            % nNoofPlayers][0]),
                    fBallCropX, fBallCropY, fBallCropWid, fBallCropHgt, 105, 10, 35, 35,
                    true);
            pDrawUtil.drawStrokeText(canvas, " Turn", 145, 32, 12,
                    Color.BLACK, Color.WHITE, 2);
        }

    }

    public void drawButton(Canvas canvas, float startFrame, long elapsedTime) {
        pBtn_Back.drawButton(canvas);
        pBtn_Settings.drawButton(canvas);
        pBtn_Reload.drawButton(canvas);

        if (bSettingEnabled) {
            pBtn_Sound.changeBitmap(Constants.bSound ? pBitmap_mgr
                    .getBitmap(R.drawable.sound)
                    : pBitmap_mgr.getBitmap(R.drawable.mute));
            pBtn_Sound.drawAnimButton(canvas, elapsedTime);
            pBtn_Pause.drawAnimButton(canvas, elapsedTime);
        }

    }

    public int getWinningPlayerID() {

        int[] nPlayerCountonBoard = new int[nNoofPlayers];

        // for (int i = 0; i < NO_OF_CELL; i++)
        // {
        // for (int j = 0; j < NO_OF_CELL; j++)
        // {
        // if (!pCellData[i][j].bEmpty)
        // {
        // nPlayerCountonBoard[pCellData[i][j].nPlayerID]++;
        //
        // System.out.println("i "
        // + i
        // + " j "
        // + j
        // + " pid "
        // + pCellData[i][j].nPlayerID
        // + " cnt "
        // + nPlayerCountonBoard[pCellData[i][j].nPlayerID]);
        // }
        //
        // }
        // }

        int nTopperCount = 0;
        int nTopperId = 0;

        for (int i = 0; i < nPlayerCountonBoard.length; i++) {
            if (nPlayerCountonBoard[i] > 0
                    && nPlayerCountonBoard[i] > nTopperCount) {
                nTopperCount = nPlayerCountonBoard[i];
                nTopperId = i;
            }

        }

        int nNoofEqualPoints = 0;

        for (int i = 0; i < nPlayerCountonBoard.length; i++) {
            if (nPlayerCountonBoard[i] > 0
                    && nPlayerCountonBoard[i] == nTopperCount) {
                nNoofEqualPoints++;
            }

        }

        if (nNoofEqualPoints > 1 || nTopperCount == 0) {
            return -1;
        } else {
            return nTopperId;
        }

    }

    public void drawWinningInfo(Canvas canvas, float startFrame,
                                long elapsedTime) {

        int nWinPlayerId = 0;

        // switch (Constants.nMode)
        // {
        // case Constants.MODE_SINGLEPLAYER:
        //
        // nWinPlayerId = getWinningPlayerID();
        //
        // if (nWinPlayerId >= 0)
        // {
        // if (nWinPlayerId == 0)
        // pDrawUtil.drawText(canvas, "You Won", 80,
        // 160, 20, Color.CYAN);
        // else
        // pDrawUtil.drawText(canvas, "Cpu Won", 80,
        // 160, 20, Color.CYAN);
        //
        // } else
        // {
        // pDrawUtil.drawText(canvas, "No Body Wons", 80,
        // 160, 20, Color.CYAN);
        //
        // }
        //
        // // if (nCurrentPlayerID == 0)
        // // {
        // // pDrawUtil.drawText(canvas, "You Won", 80, 160,
        // // 20, Color.CYAN);
        // //
        // // } else
        // // {
        // // pDrawUtil.drawText(canvas, "CPU Won", 80, 160,
        // // 20, Color.CYAN);
        // //
        // // }
        //
        // break;
        //
        // case Constants.MODE_MUTIPLAYER:
        //
        // pDrawUtil.drawText(canvas, "Player "
        // + (nCurrentPlayerID + 1) + "Won",
        // 80, 160, 20, Color.CYAN);
        //
        // break;
        //
        // case Constants.MODE_TIMEBASED:
        //
        // nWinPlayerId = getWinningPlayerID();
        //
        // if (nWinPlayerId >= 0)
        // {
        // pDrawUtil.drawText(canvas, " Player "
        // + (nWinPlayerId + 1)
        // + "WON", 80, 160, 20,
        // Color.CYAN);
        // } else
        // {
        // pDrawUtil.drawText(canvas,
        // "~~~ No Body Won ~~~ ", 80,
        // 160, 20, Color.CYAN);
        // }
        //
        // break;
        //
        // case Constants.MODE_MOVEBASED:
        //
        // nWinPlayerId = getWinningPlayerID();
        //
        // if (nWinPlayerId >= 0)
        // {
        // pDrawUtil.drawText(canvas, " Player "
        // + (nWinPlayerId + 1)
        // + "WON", 80, 160, 20,
        // Color.CYAN);
        // } else
        // {
        // pDrawUtil.drawText(canvas,
        // "~~~ MATCH DRAW ~~~ ", 80,
        // 160, 20, Color.CYAN);
        // }
        //
        // break;
        //
        // }

        // pDrawUtil.drawText(canvas, "Game Over !!!", 160, 200, 20,
        // Color.CYAN);

        if (bGameOver) {
            for (int i = 0; i < NO_OF_CELL; i++) {
                for (int j = 0; j < NO_OF_CELL; j++) {

                    if (Constants.nMode == Constants.MODE_SINGLEPLAYER) {
                        pDrawUtil.drawStrokeRect(
                                canvas,
                                pCellData[i][j].PosX,
                                pCellData[i][j].PosY,
                                CELL_WIDTH,
                                CELL_HEIGHT,
                                2,
                                Color.rgb(103,
                                        103,
                                        103));
                    } else {
                        pDrawUtil.drawStrokeRect(
                                canvas,
                                pCellData[i][j].PosX,
                                pCellData[i][j].PosY,
                                CELL_WIDTH,
                                CELL_HEIGHT,
                                2,
                                nPlayerRGB[nCurrentPlayerID + 1]);
                    }

                    if (!pCellData[i][j].bEmpty) {
                        pDrawUtil.drawImage(
                                canvas,
                                pBitmap_mgr.getBitmap(pCellData[i][j].nImageId),
                                fBallCropX,
                                fBallCropY,
                                fBallCropWid,
                                fBallCropHgt,
                                pCellData[i][j].PosX
                                        + ROW_PLUS,
                                pCellData[i][j].PosY
                                        + COL_PLUS,
                                nBallWidth,
                                nBallHeight,
                                false);
                    }

                    if (pCellData[i][j].bExtraAnim1) {
                        pDrawUtil.drawImage(
                                canvas,
                                pBitmap_mgr.getBitmap(pCellData[i][j].nAnimImageId),
                                fBallCropX,
                                fBallCropY,
                                fBallCropWid,
                                fBallCropHgt,
                                pCellData[i][j].AnimX1,
                                pCellData[i][j].AnimY1,
                                nBallWidth,
                                nBallHeight,
                                false);

                    }
                    if (pCellData[i][j].bExtraAnim2) {
                        pDrawUtil.drawImage(
                                canvas,
                                pBitmap_mgr.getBitmap(pCellData[i][j].nAnimImageId),
                                fBallCropX,
                                fBallCropY,
                                fBallCropWid,
                                fBallCropHgt,
                                pCellData[i][j].AnimX2,
                                pCellData[i][j].AnimY2,
                                nBallWidth,
                                nBallHeight,
                                false);

                    }
                    if (pCellData[i][j].bExtraAnim3) {

                        pDrawUtil.drawImage(
                                canvas,
                                pBitmap_mgr.getBitmap(pCellData[i][j].nAnimImageId),
                                fBallCropX,
                                fBallCropY,
                                fBallCropWid,
                                fBallCropHgt,
                                pCellData[i][j].AnimX3,
                                pCellData[i][j].AnimY3,
                                nBallWidth,
                                nBallHeight,
                                false);

                    }
                    if (pCellData[i][j].bExtraAnim4) {

                        pDrawUtil.drawImage(
                                canvas,
                                pBitmap_mgr.getBitmap(pCellData[i][j].nAnimImageId),
                                fBallCropX,
                                fBallCropY,
                                fBallCropWid,
                                fBallCropHgt,
                                pCellData[i][j].AnimX4,
                                pCellData[i][j].AnimY4,
                                nBallWidth,
                                nBallHeight,
                                false);

                    }

                }

            }

        }

        if (Constants.nMode != Constants.MODE_DEMO)
            pDrawUtil.drawStrokeText(canvas, "Game Over !!!", 160, 240,
                    20, Color.CYAN, Color.BLACK, 2);

        if (Constants.nMode == Constants.MODE_DEMO)
            setLevelData();

    }

    public void updateGame(Canvas canvas, float startFrame, long elapsedTime) {

        switch (Constants.nSubMode) {
            case Constants.MODE_MOVES:
                if (nMoves <= 0 && !bSplit)
                    bGameOver = true;
                break;
            case Constants.MODE_TIMED:

                if (nMins < nTotalTime && !bGameOver && !bPaused
                        && nTaps > 0) {
                    fTime += elapsedTime;

                    if (Math.floor(fTime / 1000) >= 1 * 60) {
                        fTime = 0;
                        nMins++;
                    }

                    nSecs = (int) ((fTime / 1000));

                }

                break;

        }

    }

    public void drawTestFrames(Canvas canvas, float startFrame,
                               long elapsedTime) {
        float nFramelength = fTestFrames.length;
        int nFidx;
        float cx, cy, cw, ch;
        nTestFrameIndex += ((nFramelength / 1000) * elapsedTime);
        if (nTestFrameIndex >= nFramelength) {
            nTestFrameIndex = 0;
        }

        nFidx = (int) (nTestFrameIndex);
        cx = fTestFrames[nFidx][0];
        cy = fTestFrames[nFidx][1];
        cw = fTestFrames[nFidx][2];
        ch = fTestFrames[nFidx][3];

        System.out.println(cx + "," + cy + "," + cw + "," + ch);
        pDrawUtil.drawImage(canvas, pBitmap_mgr.getBitmap(R.drawable.ballideal256), cx, cy, cw, ch, 100, 100, 100, 100, false);

    }

    public void drawStaticBalls(Canvas canvas,
                                long elapsedTime) {

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {

                pDrawUtil.drawStrokeRect(canvas,
                        pCellData[i][j].PosX,
                        pCellData[i][j].PosY,
                        CELL_WIDTH, CELL_HEIGHT, 2,
                        Color.rgb(103, 103, 103));

                if (!pCellData[i][j].bEmpty && pCellData[i][j].nImageId != -1

                    // && !pCellData[i][j].bExtraAnim1 &&
                    // !pCellData[i][j].bExtraAnim2 &&
                    // !pCellData[i][j].bExtraAnim3 &&
                    // !pCellData[i][j].bExtraAnim4
                        ) {
//                        pDrawUtil.drawAnimSprite(
//                                canvas,
//                                pBitmap_mgr.getBitmap(pCellData[i][j].nImageId),
//                                0,
//                                0,
//                                512,
//                                512,
//                                pCellData[i][j].PosX
//                                        + ROW_PLUS,
//                                pCellData[i][j].PosY
//                                        + COL_PLUS,
//                                nBallWidth,
//                                nBallHeight, true);

                    //System.out.println("static balls id"+pCellData[i][j].nImageId);
                    pDrawUtil.drawImage(canvas, pBitmap_mgr.getBitmap(pCellData[i][j].nImageId), fBallCropX, fBallCropY, fBallCropWid, fBallCropHgt, pCellData[i][j].PosX
                                    + ROW_PLUS,
                            pCellData[i][j].PosY
                                    + COL_PLUS,
                            nBallWidth,
                            nBallHeight, true);
                }

            }

        }


    }

    public void updateDrawBuffer(Canvas canvas, float startFrame,
                                 long elapsedTime) {

        // pDrawUtil.clearRect(canvas, 0, 0, 480 , 800, Color.rgb(238,
        // 48,
        // 167));



        if (!bLoaded)
            return;

        updateGame(canvas, startFrame, elapsedTime);

        if (Constants.nMode != Constants.MODE_DEMO) {
            drawModeInfo(canvas, startFrame, elapsedTime);
            drawButton(canvas, startFrame, elapsedTime);
        }

        if (bGameOver) {

            drawWinningInfo(canvas, startFrame, elapsedTime);

            return;
        }

        //drawTestFrames(canvas, startFrame, elapsedTime);
        //pDrawUtil.drawImage(canvas,pBitmap_mgr.getBitmap(R.drawable.ballideal256),0.125f,0,0.125f,1,10,10,100,100,false);


        drawStaticBalls(canvas, elapsedTime);
        drawSplit(canvas, elapsedTime);

        if (Constants.nMode == Constants.MODE_SINGLEPLAYER) {
            if (isCPUTurn()) {
                fCpuTurnTime += elapsedTime;
                if (fCpuTurnTime >= MAX_TURN_TIME
                        && checkFitArray(nChoosenCell[0],
                        nChoosenCell[1])) {

                    fCpuTurnTime = 0;
                    checkCombinations(nChoosenCell[0],
                            nChoosenCell[1]);

                }
            }
        }

        if (Constants.nMode == Constants.MODE_DEMO) {
            if (!isCPUTurn()) {

                fPlayerTurnTime += elapsedTime;
                if (fPlayerTurnTime >= MAX_TURN_TIME) {
                    fPlayerTurnTime = 0;
                    nChoosenCell = this.getRandomPlayerCell();
                    if (checkFitArray(nChoosenCell[0],
                            nChoosenCell[1]))
                        checkCombinations(nChoosenCell[0],
                                nChoosenCell[1]);
                }

            } else {
                fCpuTurnTime += elapsedTime;
                if (fCpuTurnTime >= MAX_TURN_TIME
                        && checkFitArray(nChoosenCell[0],
                        nChoosenCell[1])) {

                    fCpuTurnTime = 0;
                    checkCombinations(nChoosenCell[0],
                            nChoosenCell[1]);

                }
            }
        }

        if (bPaused) {
            System.out.println("bpaused");

            pDrawUtil.drawRect(canvas, 0, 0,
                    Constants.DEFAULTWIDTH,
                    Constants.DEFAULTHEIGHT, 2,
                    Color.BLACK,
                    Color.argb(110, 80, 80, 80));

            pDrawUtil.drawStrokeText(canvas, "Game Paused", 150,
                    200, 20, Color.WHITE, Color.BLACK,
                    2);
            pDrawUtil.drawStrokeText(canvas, "Tap to Continue ...",
                    170, 240, 20, Color.YELLOW,
                    Color.BLACK, 2);

        }

        if (bShowDescription)
            drawGameDescripion(canvas, startFrame, elapsedTime);


    }

    public int[] getRandomPlayerCell() {
        int cell[] = new int[2];
        boolean bValid = false;
        int iteration = 0;
        Random rand = new Random();

        while (!bValid) {
            cell[0] = rand.nextInt(NO_OF_CELL);
            cell[1] = rand.nextInt(NO_OF_CELL);

            if (pCellData[cell[0]][cell[1]].bEmpty
                    || pCellData[cell[0]][cell[1]].nPlayerID == nPlayerId) {
                bValid = true;

            }

            iteration++;
            if (iteration >= 100)
                bValid = true;
        }

        if (!bValid) {

            for (int i = 0; i < NO_OF_CELL; i++) {
                for (int j = 0; j < NO_OF_CELL; j++) {
                    if (pCellData[i][j].bEmpty
                            || pCellData[i][j].nPlayerID == nPlayerId) {
                        cell[0] = i;
                        cell[1] = j;
                        bValid = true;
                    }
                    if (bValid)
                        break;
                }
                if (bValid)
                    break;
            }
        }

        return cell;
    }

//    public int[] getRandomCell() {
//        boolean bValidCell = false;
//        Random rand = new Random();
//        int row = 0, col = 0;
//        int cell[] = new int[2];
//        cell[0] = -1;
//        cell[1] = -1;
//        int iteration = 0;
//
//        float fBoardEmptyPer = (getEmptyCellsinBoard() / (float) (NO_OF_CELL * NO_OF_CELL));
//        System.out.println("emptypercentage in board " + fBoardEmptyPer);
//
//        // Take only empty cell if board is < 50% empty
//        // else choose only empty cell which has no player adjacents
//
//        while (!bValidCell && iteration < 1000) {
//            row = rand.nextInt(NO_OF_CELL);
//            col = rand.nextInt(NO_OF_CELL);
//
//            if (fBoardEmptyPer < 0.5f) {
//                if (pCellData[row][col].bEmpty)
//                    bValidCell = true;
//            } else {
//                if (!isAnyPlayerinAdjacent(row, col)
//                        && pCellData[row][col].bEmpty)
//                    bValidCell = true;
//            }
//
//            //
//            // if (pCellData[row][col].bEmpty
//            // /* || pCellData[row][col].nPlayerID == nCpuId */)
//            // {
//            // bValidCell = true;
//            // }
//
//            iteration++;
//        }
//
//        System.out.println("Ran Empty cell iteration" + iteration);
//
//        if (!bValidCell) {
//            for (int i = 0; i < NO_OF_CELL; i++) {
//                for (int j = 0; j < NO_OF_CELL; j++) {
//                    row = i;
//                    col = j;
//
//                    if (fBoardEmptyPer < 0.5f) {
//
//                        if (pCellData[row][col].bEmpty
//                                || pCellData[row][col].nPlayerID == nCpuId) {
//                            bValidCell = true;
//                        }
//                    } else {
//                        if (!isAnyPlayerinAdjacent(row,
//                                col)
//                                && pCellData[row][col].bEmpty) {
//                            bValidCell = true;
//                        }
//                    }
//
//                    if (bValidCell)
//                        break;
//                }
//
//                if (bValidCell)
//                    break;
//            }
//
//        }
//
//        cell[0] = row;
//        cell[1] = col;
//
//        System.out.println("getrandomcell iteration " + iteration);
//
//        return cell;
//
//    }

    public boolean checkGameOverforPlayer() {

        int nPlayerCount = 0;
        int nCpuCount = 0;

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {
                if (!pCellDataPlayerTurn[i][j].bEmpty) {
                    if (pCellDataPlayerTurn[i][j].nPlayerID == nCpuId) {
                        nCpuCount++;

                    }
                    if (pCellDataPlayerTurn[i][j].nPlayerID == nPlayerId) {
                        nPlayerCount++;
                    }
                }

            }
        }

        if (nPlayerCount > 1 && nCpuCount == 0) {

            return true;
        }

        return false;

    }

    public boolean checkGameOverforCPU() {

        int nPlayerCount = 0;
        int nCpuCount = 0;

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {
                if (!pCellDataCPUTurn[i][j].bEmpty) {
                    if (pCellDataCPUTurn[i][j].nPlayerID == nCpuId) {
                        nCpuCount++;

                    } else {
                        nPlayerCount++;
                    }
                }

            }
        }

        if (nPlayerCount == 0 && nCpuCount > 1) {
            return true;
        }

        return false;

    }

    public void checkGameOver() {

        int nChainCount = 0;
        int nPlayerCount = 0;
        int nTempPlayerID = -1;

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {
                if (!pCellData[i][j].bEmpty
                        || pCellData[i][j].bExtraAnim1
                        || pCellData[i][j].bExtraAnim2
                        || pCellData[i][j].bExtraAnim3
                        || pCellData[i][j].bExtraAnim4) {
                    if (nChainCount == 0) {
                        nTempPlayerID = pCellData[i][j].nPlayerID;
                    }

                    nChainCount++;

                }
            }
        }

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {
                if (!pCellData[i][j].bEmpty
                        || pCellData[i][j].bExtraAnim1
                        || pCellData[i][j].bExtraAnim2
                        || pCellData[i][j].bExtraAnim3
                        || pCellData[i][j].bExtraAnim4) {
                    if (nTempPlayerID == pCellData[i][j].nPlayerID) {
                        nPlayerCount++;
                    }

                }
            }
        }

        if (nChainCount > 1 && nTempPlayerID != -1) {
            if (nChainCount == nPlayerCount) {
                bGameOver = true;

            }
        }

    }


    public float[][] getFrames(Bitmap bitmap, int framewid, int framehgt) {
        int length = (bitmap.getWidth() / framewid) * (bitmap.getHeight() / framehgt);
        float fFramesData[][] = new float[length][4];
        float xFrameLen = (bitmap.getWidth() / framewid), yFrameLen = (bitmap.getHeight() / framehgt);
        float Xframe = 0, frames = 0;
        int frameindex = 0;
        while (frames < yFrameLen) {
            Xframe = 0;
            while (Xframe < xFrameLen) {
                fFramesData[frameindex][0] = (Xframe / xFrameLen);
                fFramesData[frameindex][1] = (frames / yFrameLen);
                fFramesData[frameindex][2] = (1 / xFrameLen);
                fFramesData[frameindex][3] = (1 / yFrameLen);
                //System.out.println(fFramesData[frameindex][0]+","+fFramesData[frameindex][1]+","+fFramesData[frameindex][2]+","+fFramesData[frameindex][3]);

                Xframe++;
                frameindex++;


            }
            frames++;
        }

        return fFramesData;
    }

    public void drawSplit(Canvas canvas, long elapsedTime) {

        float fExtraVal = elapsedTime / 5;
        boolean bExtraAnimOver = true;
        boolean bAnySplitOver = false;

        for (int i = 0; i < NO_OF_CELL; i++) {
            for (int j = 0; j < NO_OF_CELL; j++) {

                if (bSplitSoundOn && Constants.bSound) {
                    pMedia_Split.start();
                    bSplitSoundOn = false;
                }

                if (pCellData[i][j].bExtraAnim1) {

                    bExtraAnimOver = false;

                    pCellData[i][j].AnimX1 += pCellData[i][j].fUnitVectX1
                            * fExtraVal;
                    pCellData[i][j].AnimY1 += pCellData[i][j].fUnitVectY1
                            * fExtraVal;

                    pDrawUtil.drawImage(
                            canvas,
                            pBitmap_mgr.getBitmap(pCellData[i][j].nAnimImageId),
                            fBallCropX,
                            fBallCropY,
                            fBallCropWid,
                            fBallCropHgt,
                            pCellData[i][j].AnimX1,
                            pCellData[i][j].AnimY1,
                            nBallWidth,
                            nBallHeight, false);

                    float fVectDist = calcVectorDistance(
                            pCellData[i][j].AnimX1,
                            pCellData[i][j].AnimY1,
                            pCellData[i][j].TargetX1,
                            pCellData[i][j].TargetY1);

                    float fDelX1 = pCellData[i][j].TargetX1
                            - pCellData[i][j].StartX1;
                    float fDelY1 = pCellData[i][j].TargetY1
                            - pCellData[i][j].StartY1;

                    float fDelX2 = pCellData[i][j].TargetX1
                            - pCellData[i][j].AnimX1;
                    float fDelY2 = pCellData[i][j].TargetY1
                            - pCellData[i][j].AnimY1;

                    float fVectDotProd = calcVectorDotProduct(
                            fDelX1, fDelY1,
                            fDelX2, fDelY2);

                    if (fVectDotProd <= 0 || fVectDist < 2) {
                        pCellData[i][j].bExtraAnim1 = false;
                        pCellData[i][j].nTouchCount++;
                        pCellData[i][j].bEmpty = false;
                        pCellData[i][j].nPlayerID = nCurrentPlayerID;
                        pCellData[i][j].nImageId = nPlayerImage[nCurrentPlayerID][pCellData[i][j].nTouchCount - 1];
                        pCellData[i][j].AnimX1 = pCellData[i][j].TargetX1;
                        pCellData[i][j].AnimY1 = pCellData[i][j].TargetY1;

                        bAnySplitOver = true;

                        if (pCellData[i][j].nTouchCount >= pCellData[i][j].nMatchCount) {

                            addSelectionCombinations(
                                    i,
                                    j);

                        }

                    }

                }

                if (pCellData[i][j].bExtraAnim2) {

                    bExtraAnimOver = false;

                    pCellData[i][j].AnimX2 += pCellData[i][j].fUnitVectX2
                            * fExtraVal;
                    pCellData[i][j].AnimY2 += pCellData[i][j].fUnitVectY2
                            * fExtraVal;

                    pDrawUtil.drawImage(
                            canvas,
                            pBitmap_mgr.getBitmap(pCellData[i][j].nAnimImageId),
                            fBallCropX,
                            fBallCropY,
                            fBallCropWid,
                            fBallCropHgt,
                            pCellData[i][j].AnimX2,
                            pCellData[i][j].AnimY2,
                            nBallWidth,
                            nBallHeight, false);

                    float fVectDist = calcVectorDistance(
                            pCellData[i][j].AnimX2,
                            pCellData[i][j].AnimY2,
                            pCellData[i][j].TargetX2,
                            pCellData[i][j].TargetY2);

                    float fDelX1 = pCellData[i][j].TargetX2
                            - pCellData[i][j].StartX2;
                    float fDelY1 = pCellData[i][j].TargetY2
                            - pCellData[i][j].StartY2;

                    float fDelX2 = pCellData[i][j].TargetX2
                            - pCellData[i][j].AnimX2;
                    float fDelY2 = pCellData[i][j].TargetY2
                            - pCellData[i][j].AnimY2;

                    float fVectDotProd = calcVectorDotProduct(
                            fDelX1, fDelY1,
                            fDelX2, fDelY2);

                    if (fVectDotProd <= 0 || fVectDist < 2) {
                        pCellData[i][j].bExtraAnim2 = false;
                        pCellData[i][j].nTouchCount++;
                        pCellData[i][j].bEmpty = false;
                        pCellData[i][j].nPlayerID = nCurrentPlayerID;
                        pCellData[i][j].nImageId = nPlayerImage[nCurrentPlayerID][pCellData[i][j].nTouchCount - 1];

                        pCellData[i][j].AnimX2 = pCellData[i][j].TargetX2;
                        pCellData[i][j].AnimY2 = pCellData[i][j].TargetY2;

                        bAnySplitOver = true;

                        if (pCellData[i][j].nTouchCount >= pCellData[i][j].nMatchCount) {

                            addSelectionCombinations(
                                    i,
                                    j);
                        }

                    }

                }

                if (pCellData[i][j].bExtraAnim3) {

                    bExtraAnimOver = false;

                    pCellData[i][j].AnimX3 += pCellData[i][j].fUnitVectX3
                            * fExtraVal;
                    pCellData[i][j].AnimY3 += pCellData[i][j].fUnitVectY3
                            * fExtraVal;

                    pDrawUtil.drawImage(
                            canvas,
                            pBitmap_mgr.getBitmap(pCellData[i][j].nAnimImageId),
                            fBallCropX,
                            fBallCropY,
                            fBallCropWid,
                            fBallCropHgt,
                            pCellData[i][j].AnimX3,
                            pCellData[i][j].AnimY3,
                            nBallWidth,
                            nBallHeight, false);

                    float fVectDist = calcVectorDistance(
                            pCellData[i][j].AnimX3,
                            pCellData[i][j].AnimY3,
                            pCellData[i][j].TargetX3,
                            pCellData[i][j].TargetY3);

                    float fDelX1 = pCellData[i][j].TargetX3
                            - pCellData[i][j].StartX3;
                    float fDelY1 = pCellData[i][j].TargetY3
                            - pCellData[i][j].StartY3;

                    float fDelX2 = pCellData[i][j].TargetX3
                            - pCellData[i][j].AnimX3;
                    float fDelY2 = pCellData[i][j].TargetY3
                            - pCellData[i][j].AnimY3;

                    float fVectDotProd = calcVectorDotProduct(
                            fDelX1, fDelY1,
                            fDelX2, fDelY2);

                    if (fVectDotProd <= 0 || fVectDist < 2) {
                        pCellData[i][j].bExtraAnim3 = false;
                        pCellData[i][j].nTouchCount++;
                        pCellData[i][j].bEmpty = false;
                        pCellData[i][j].nPlayerID = nCurrentPlayerID;
                        pCellData[i][j].nImageId = nPlayerImage[nCurrentPlayerID][pCellData[i][j].nTouchCount - 1];

                        pCellData[i][j].AnimX3 = pCellData[i][j].TargetX3;
                        pCellData[i][j].AnimY3 = pCellData[i][j].TargetY3;

                        bAnySplitOver = true;

                        if (pCellData[i][j].nTouchCount >= pCellData[i][j].nMatchCount) {

                            addSelectionCombinations(
                                    i,
                                    j);
                        }

                    }

                }

                if (pCellData[i][j].bExtraAnim4) {

                    bExtraAnimOver = false;

                    pCellData[i][j].AnimX4 += pCellData[i][j].fUnitVectX4
                            * fExtraVal;
                    pCellData[i][j].AnimY4 += pCellData[i][j].fUnitVectY4
                            * fExtraVal;

                    pDrawUtil.drawImage(
                            canvas,
                            pBitmap_mgr.getBitmap(pCellData[i][j].nAnimImageId),
                            fBallCropX,
                            fBallCropY,
                            fBallCropWid,
                            fBallCropHgt,
                            pCellData[i][j].AnimX4,
                            pCellData[i][j].AnimY4,
                            nBallWidth,
                            nBallHeight, false);

                    float fVectDist = calcVectorDistance(
                            pCellData[i][j].AnimX4,
                            pCellData[i][j].AnimY4,
                            pCellData[i][j].TargetX4,
                            pCellData[i][j].TargetY4);

                    float fDelX1 = pCellData[i][j].TargetX4
                            - pCellData[i][j].StartX4;
                    float fDelY1 = pCellData[i][j].TargetY4
                            - pCellData[i][j].StartY4;

                    float fDelX2 = pCellData[i][j].TargetX4
                            - pCellData[i][j].AnimX4;
                    float fDelY2 = pCellData[i][j].TargetY4
                            - pCellData[i][j].AnimY4;

                    float fVectDotProd = calcVectorDotProduct(
                            fDelX1, fDelY1,
                            fDelX2, fDelY2);

                    if (fVectDotProd <= 0 || fVectDist < 2) {
                        pCellData[i][j].bExtraAnim4 = false;
                        pCellData[i][j].nTouchCount++;
                        pCellData[i][j].bEmpty = false;
                        pCellData[i][j].nPlayerID = nCurrentPlayerID;
                        pCellData[i][j].nImageId = nPlayerImage[nCurrentPlayerID][pCellData[i][j].nTouchCount - 1];
                        pCellData[i][j].AnimX4 = pCellData[i][j].TargetX4;
                        pCellData[i][j].AnimY4 = pCellData[i][j].TargetY4;

                        bAnySplitOver = true;

                        if (pCellData[i][j].nTouchCount >= pCellData[i][j].nMatchCount) {

                            addSelectionCombinations(
                                    i,
                                    j);
                        }

                    }

                }

            }
        }

        if (bExtraAnimOver) {

            if ((Constants.nMode == Constants.MODE_SINGLEPLAYER || Constants.nMode == Constants.MODE_DEMO)
                    && bSplit) {

                bSplit = false;

                if (!isCPUTurn()) {
                    setCPUTurn(true);
                    nChoosenCell[0] = -1;
                    nChoosenCell[1] = -1;
                    System.out.println("In SPlit");
                    System.out.println("processing sep thread");
                    lSystemThinkingTime = System
                            .currentTimeMillis();
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int cell[] = new int[2];
                            cell = findBestCell();

                            // checkCombinations(cell[0],
                            // cell[1]);
                            //

                            nChoosenCell = cell;

                            System.out.println("Complted sep thread");
                            System.out.println("lSystemThinkingTime"
                                    + (System.currentTimeMillis() - lSystemThinkingTime));
                        }

                    })).start();

                } else {
                    setCPUTurn(false);
                    fPlayerTurnTime = 0;
                }

            } else {
                bSplit = false;
            }

        }

        if (bAnySplitOver)
            checkGameOver();

    }


}


class TriggerCell {
    int row = -1;
    int col = -1;
    int playercountonboard = 0;
    int cpucountonboard = 0;
    int priority = 0;
    int triggerdiff = -1; // used for checkbefore logiv tie proroty
    // avoid
    int noOfOpponent = 0;

    int HIGHPRIORITY = 2;
    int LOWPRIORTITY = 1;

    void reset() {
        row = -1;
        col = -1;
        playercountonboard = 0;
        priority = 0;
        triggerdiff = 0;
    }

}

