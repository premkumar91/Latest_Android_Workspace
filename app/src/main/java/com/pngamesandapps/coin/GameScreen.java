package com.pngamesandapps.coin;

import com.pngamesandapps.coin.CustomButton.ButtonClickListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.widget.Toast;
import android.graphics.Bitmap;

public class GameScreen
    {

        /**
         * @param args
         */

        private Context      pActivityContext;
        SharedPreferences    pPreference;

        Editor               pEditor;
        ChainReactionManager pGameManager;
        BitmapManager        pBitmap_mgr;
        DrawUtils            pDrawUtil;

        CustomButton         pBtn_Play, pBtn_SinglePlayer, pBtn_MultiPlayer,
                            pBtn_Back, pBtn_Settings, pBtn_Help, pBtn_Share;
        CustomButton         pBtn_MoveMode, pBtn_TimeMode, pBtn_InfiniteMode;
        CustomButton         pBtn_Sound, pBtn_About;

        CustomButton         pBoardButton[]    = new CustomButton[6];
        CustomButton         pPlayerButton[]   = new CustomButton[6];

        boolean              bGameLoadFinished = false;

        MediaPlayer          pMedia_Sound;
        private long         fTimeSum;
        private int          nFpsCount;
        private int          FPS;
        private CustomButton pBtn_DiffDecrease;
        private CustomButton pBtn_DiffIncrease;
        private int          nDifficultyCount  = 0;
        private int          MIN_DIFF          = 1, MAX_DIFF = 10;
        private float pTitleBallFrames[][],fFrameId=0;
        private float fTitleFramelen=0;

        public GameScreen(Context context)
            {
                pActivityContext = context;
                pPreference = pActivityContext.getSharedPreferences(
                                    Constants.MY_PREFERENCES,
                                    Context.MODE_PRIVATE);
                pBitmap_mgr = new BitmapManager();

                initMedia(context);
                initialize();

                pGameManager = new ChainReactionManager(pActivityContext);
                pDrawUtil = new DrawUtils(pActivityContext);
                pGameManager.setPreferences(pPreference);

                pGameManager.setLevelData();
                setGameLoadStatus(true);

            }

        public void initMedia(Context context)
            {
                pMedia_Sound = MediaPlayer.create(context, R.raw.btn);

            }

        public void initialize()
            {

                Constants.nDifficultyCount = pPreference.getInt(
                                    Constants.KEY_DIFFICULTY, MIN_DIFF);
                Constants.nCurrentBoardIndex = pPreference.getInt(
                                    Constants.KEY_BOARDS, 3);
                Constants.nCurrentPlayerIndex = pPreference.getInt(
                                    Constants.KEY_PLAYERS, 0);
                Constants.bSound = pPreference.getBoolean(Constants.KEY_SOUND,
                                    true);

                /* ------ Button Image Initialize ----- */

                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.bg);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.play_but);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.singleplayer);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.multiplayer);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.back);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.movemode);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.timemode);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.settings);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.help);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.share);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.sound);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.close);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type3_1);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.board);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.about_icon);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.mute);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.board);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.type1_3);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.circle);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.infinite);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.about);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.select);
                pBitmap_mgr.addBitmap(pActivityContext, R.drawable.unselect);

                pBtn_Settings = new CustomButton(pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.settings),
                                    0, 0, 512, 512, 270, 420, 30, 30);
                pBtn_Share = new CustomButton(pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.share), 0,
                                    0, 512, 512, 30, 420, 30, 30);
                pBtn_Help = new CustomButton(pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.help), 0,
                                    0, 128, 128, 100, 220, 40, 40);

                pBtn_Play = new CustomButton(pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.play_but),
                                    0, 0, 128, 128, 140, 400, 40, 40);
                pBtn_Back = new CustomButton(pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.back), 0,
                                    0, 512, 512, 270, 8, 25, 25);

                pBtn_SinglePlayer = new CustomButton(
                                    pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.singleplayer),
                                    0, 0, 512, 512, 110, 110, 90, 90);
                pBtn_MultiPlayer = new CustomButton(
                                    pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.multiplayer),
                                    0, 0, 512, 512, 110, 260, 90, 90);
                pBtn_MoveMode = new CustomButton(pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.movemode),
                                    0, 0, 512, 512, 110, 90, 80, 80);
                pBtn_TimeMode = new CustomButton(pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.timemode),
                                    0, 0, 512, 512, 110, 200, 80, 80);

                pBtn_InfiniteMode = new CustomButton(pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.infinite),
                                    0, 0, 512, 512, 110, 310, 80, 80);

                pBtn_About = new CustomButton(
                                    pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.about_icon),
                                    0, 0, 512, 512, 29, 10, 50, 50);
                pBtn_Sound = new CustomButton(
                                    pActivityContext,
                                    (Constants.bSound) ? pBitmap_mgr
                                                        .getBitmap(R.drawable.sound)
                                                        : pBitmap_mgr.getBitmap(R.drawable.mute),
                                    0, 0, 512, 512, 145, 445, 25, 25);

                pBtn_DiffDecrease = new CustomButton(pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.back), 0,
                                    0, 512, 512, 127, 407, 20, 20);

                pBtn_DiffIncrease = new CustomButton(pActivityContext,
                                    pBitmap_mgr.getBitmap(R.drawable.back), 0,
                                    0, 512, 512, 179, 407, 20, 20);

                int boardx = 60, boardy = 95, xspace = 72, yspace = 60;
                int x, y, y2;
                for (int i = 0, l = pBoardButton.length; i < l; i++)
                    {
                        if (i <= l / 2 - 1)
                            {
                                x = 70 + (i * 72);
                                y = 92;
                                y2 = 257;
                            } else
                            {
                                x = 70 + ((i - l / 2) * 72);
                                y = 152;
                                y2 = 312;
                            }

                        pBoardButton[i] = new CustomButton(
                                            pActivityContext,
                                            pBitmap_mgr.getBitmap(R.drawable.unselect),
                                            0, 0, 512, 512, x, y, 40, 40);
                        pBoardButton[i].setCustomValue(i);

                        pPlayerButton[i] = new CustomButton(
                                            pActivityContext,
                                            pBitmap_mgr.getBitmap(R.drawable.unselect),
                                            0, 0, 512, 512, x, y2, 40, 40);
                        pPlayerButton[i].setCustomValue(i);

                    }

                // Constants.setScreen(Constants.MAIN_SCREEN);

                addButtonListener();

                // Constants.nScreen = Constants.MODE_SCREEN;
                Constants.nScreen = Constants.MAIN_SCREEN;
                Constants.nMode = Constants.MODE_DEMO;
                Constants.nSubMode = -1;

                pTitleBallFrames=new float[8][4];
                pTitleBallFrames=getFrames(pBitmap_mgr.getBitmap(R.drawable.type1_3),256,256);
                fFrameId=0;
                fTitleFramelen=pTitleBallFrames.length;
            }

        public float[][] getFrames(Bitmap bitmap,int framewid,int framehgt)
        {
            int length=(bitmap.getWidth()/framewid)*(bitmap.getHeight()/framehgt);
            float fFramesData[][]=new float[length][4];
            float xFrameLen=(bitmap.getWidth()/framewid),yFrameLen=(bitmap.getHeight()/framehgt);
            float Xframe=0,frames=0;
            int frameindex=0;
            while(frames<yFrameLen)
            {
                Xframe=0;
                while(Xframe<xFrameLen)
                {
                    fFramesData[frameindex][0]=(Xframe/xFrameLen);
                    fFramesData[frameindex][1]=(frames/yFrameLen);
                    fFramesData[frameindex][2]=(1/xFrameLen);
                    fFramesData[frameindex][3]=(1/yFrameLen);
                    //System.out.println(fFramesData[frameindex][0]+","+fFramesData[frameindex][1]+","+fFramesData[frameindex][2]+","+fFramesData[frameindex][3]);

                    Xframe++;
                    frameindex++;


                }
                frames++;
            }

            return fFramesData;
        }

        public void addButtonListener()
            {

                pBtn_Play.setOnClickListener(new ButtonClickListener()
                    {

                        public void onButtonClick(CustomButton bt)
                            {

                                // Constants.setScreen(Constants.MODE_SCREEN);

                                if (Constants.bSound)
                                    pMedia_Sound.start();

                                Constants.bChangeScreen = true;
                                Constants.nSwitchScreen = Constants.MODE_SCREEN;
                                Constants.nSwitchMode = -1;
                                Constants.nSwitchMode = -1;

                            }
                    });

                for (int i = 0; i < pBoardButton.length; i++)
                    {

                        pBoardButton[i].setOnClickListener(new ButtonClickListener()
                            {

                                public void onButtonClick(CustomButton bt)
                                    {
                                        if (Constants.bSound)
                                            {
                                                pMedia_Sound.start();
                                            }

                                        Constants.nCurrentBoardIndex = bt
                                                            .getCustomValue();
                                        pEditor = pPreference.edit();
                                        pEditor.putInt(Constants.KEY_BOARDS,
                                                            Constants.nCurrentBoardIndex);
                                        pEditor.commit();

                                    }
                            });

                        pPlayerButton[i].setOnClickListener(new ButtonClickListener()
                            {

                                public void onButtonClick(CustomButton bt)
                                    {
                                        if (Constants.bSound)
                                            {
                                                pMedia_Sound.start();
                                            }

                                        Constants.nCurrentPlayerIndex = bt
                                                            .getCustomValue();

                                        pEditor = pPreference.edit();
                                        pEditor.putInt(Constants.KEY_PLAYERS,
                                                            Constants.nCurrentPlayerIndex);
                                        pEditor.commit();
                                    }
                            });

                    }

                pBtn_Sound.setOnClickListener(new ButtonClickListener()
                    {

                        public void onButtonClick(CustomButton bt)
                            {

                                Constants.bSound = !Constants.bSound;

                                pEditor = pPreference.edit();
                                pEditor.putBoolean(Constants.KEY_SOUND,
                                                    Constants.bSound);
                                pEditor.commit();

                            }
                    });

                pBtn_DiffDecrease.setOnClickListener(new ButtonClickListener()
                    {

                        public void onButtonClick(CustomButton bt)
                            {

                                if (Constants.bSound)
                                    pMedia_Sound.start();

                                Constants.nDifficultyCount--;
                                if (Constants.nDifficultyCount <= MIN_DIFF)
                                    Constants.nDifficultyCount = MIN_DIFF;

                                pEditor = pPreference.edit();
                                pEditor.putInt(Constants.KEY_DIFFICULTY,
                                                    Constants.nDifficultyCount);
                                pEditor.commit();

                            }
                    });

                pBtn_DiffIncrease.setOnClickListener(new ButtonClickListener()
                    {

                        public void onButtonClick(CustomButton bt)
                            {

                                if (Constants.bSound)
                                    pMedia_Sound.start();

                                Constants.nDifficultyCount++;
                                if (Constants.nDifficultyCount >= MAX_DIFF)
                                    Constants.nDifficultyCount = MAX_DIFF;

                                pEditor = pPreference.edit();
                                pEditor.putInt(Constants.KEY_DIFFICULTY,
                                                    Constants.nDifficultyCount);
                                pEditor.commit();
                            }
                    });

                pBtn_Settings.setOnClickListener(new ButtonClickListener()
                    {

                        public void onButtonClick(CustomButton bt)
                            {

                                Constants.nScreen = Constants.SETTINGS_SCREEN;

                                if (Constants.bSound)
                                    pMedia_Sound.start();

                            }
                    });

                // pBtn_Share.setOnClickListener(new ButtonClickListener() {
                //
                // public void onButtonClick(CustomButton bt) {
                //
                // Constants.setScreen(Constants.SHARE_SCREEN);
                //
                // if(Constants.isSoundON())
                // pMedia_Sound.start();
                //
                // }
                // });

                pBtn_About.setOnClickListener(new ButtonClickListener()
                    {

                        public void onButtonClick(CustomButton bt)
                            {

                                if (Constants.bSound)
                                    pMedia_Sound.start();
                                Constants.nScreen = Constants.ABOUT_SCREEN;

                            }
                    });

                pBtn_Back.setOnClickListener(new ButtonClickListener()
                    {

                        public void onButtonClick(CustomButton bt)
                            {

                                if (Constants.bSound)
                                    pMedia_Sound.start();

                                switch (Constants.nScreen)
                                    {
                                    case Constants.MODE_SCREEN:

                                        Constants.bChangeScreen = true;
                                        Constants.nSwitchScreen = Constants.MAIN_SCREEN;
                                        Constants.nSwitchMode = Constants.MODE_DEMO;
                                        Constants.nSwitchSubMode = -1;
                                        
                                        break;
                                    
                                    case Constants.SUBMODE_SCREEN:
                                        
                                        Constants.bChangeScreen=true;
                                        Constants.nSwitchScreen=Constants.MODE_SCREEN;
                                        Constants.nSwitchMode=-1;
                                        Constants.nSwitchSubMode=-1;
                                        
                                    break; 
                                    
                                    case Constants.SETTINGS_SCREEN:
                                        Constants.bChangeScreen=true;
                                        Constants.nSwitchScreen=Constants.MAIN_SCREEN;
                                        Constants.nSwitchMode=Constants.MODE_DEMO;
                                        Constants.nSwitchSubMode=-1;
                                        
                                    break; 
                                    
                                    
                                        
                                    }

                                //Constants.nScreen = Constants.MODE_SCREEN;

                            }
                    });

                pBtn_SinglePlayer.setOnClickListener(new ButtonClickListener()
                    {

                        public void onButtonClick(CustomButton bt)
                            {

                                if (Constants.bSound)
                                    pMedia_Sound.start();

                                // Constants.nScreen = Constants.GAMESCREEN;
                                Constants.nScreen = Constants.SUBMODE_SCREEN;
                                Constants.nMode = Constants.MODE_SINGLEPLAYER;

                                // pGameManager.setLevelData();
                                // setGameLoadStatus(true);

                            }
                    });

                pBtn_MultiPlayer.setOnClickListener(new ButtonClickListener()
                    {

                        public void onButtonClick(CustomButton bt)
                            {

                                if (Constants.bSound)
                                    pMedia_Sound.start();

                                // Constants.nScreen = Constants.GAMESCREEN;
                                Constants.nScreen = Constants.SUBMODE_SCREEN;
                                Constants.nMode = Constants.MODE_MUTIPLAYER;

                                // pGameManager.setLevelData();
                                //
                                setGameLoadStatus(true);

                            }
                    });

                pBtn_InfiniteMode.setOnClickListener(new ButtonClickListener()
                    {

                        @Override
                        public void onButtonClick(CustomButton bt)
                            {
                                // TODO Auto-generated method stub
                                if (Constants.bSound)
                                    pMedia_Sound.start();

                                Constants.nScreen = Constants.GAMESCREEN;
                                Constants.nSubMode = Constants.MODE_INFINITE;

                                pGameManager.setLevelData();
                                setGameLoadStatus(true);

                            }

                    });

                pBtn_MoveMode.setOnClickListener(new ButtonClickListener()
                    {

                        public void onButtonClick(CustomButton bt)
                            {

                                if (Constants.bSound)
                                    pMedia_Sound.start();

                                Constants.nScreen = Constants.GAMESCREEN;
                                Constants.nSubMode = Constants.MODE_MOVES;

                                pGameManager.setLevelData();
                                setGameLoadStatus(true);

                            }
                    });

                pBtn_TimeMode.setOnClickListener(new ButtonClickListener()
                    {

                        public void onButtonClick(CustomButton bt)
                            {

                                if (Constants.bSound)
                                    pMedia_Sound.start();

                                Constants.nScreen = Constants.GAMESCREEN;
                                Constants.nSubMode = Constants.MODE_TIMED;
                                pGameManager.setLevelData();

                                setGameLoadStatus(true);

                            }
                    });

            }

        public void setGameLoadStatus(boolean bValue)
            {
                bGameLoadFinished = bValue;
            }

        public boolean getGameLoadStatus(boolean bValue)
            {
                return bGameLoadFinished;
            }

        public boolean isGameLoaded()
            {

                return bGameLoadFinished;

            }

        public void handleEvent(MotionEvent event)
            {

                switch (Constants.nScreen)
                    {
                    case Constants.MAIN_SCREEN:

                        // pBtn_Play.handleEvent(event);
                        pBtn_Settings.handleEvent(event);
                        // pBtn_Share.handleEvent(event);
                        // pBtn_Help.handleEvent(event);
                        if (isGameLoaded())
                            {
                                pGameManager.handleEvent(event);
                                pBtn_Play.handleEvent(event);
                            }

                        break;

                    case Constants.SETTINGS_SCREEN:

                        pBtn_Sound.handleEvent(event);
                        pBtn_Back.handleEvent(event);
                        pBtn_About.handleEvent(event);
                        pBtn_DiffDecrease.handleEvent(event);
                        pBtn_DiffIncrease.handleEvent(event);

                        //

                        for (int i = 0, l = pBoardButton.length; i < l; i++)
                            {
                                pBoardButton[i].handleEvent(event);
                                pPlayerButton[i].handleEvent(event);

                            }

                        break;

                    case Constants.ABOUT_SCREEN:

                        pBtn_Back.handleEvent(event);

                        break;

                    case Constants.SHARE_SCREEN:

                        pBtn_Back.handleEvent(event);
                        break;

                    case Constants.SUBMODE_SCREEN:

                        pBtn_MoveMode.handleEvent(event);
                        pBtn_TimeMode.handleEvent(event);
                        pBtn_InfiniteMode.handleEvent(event);
                        pBtn_Back.handleEvent(event);

                        break;

                    case Constants.MODE_SCREEN:

                        pBtn_SinglePlayer.handleEvent(event);
                        pBtn_MultiPlayer.handleEvent(event);
                        pBtn_Back.handleEvent(event);
                        
                        //pBtn_Settings.handleEvent(event);

                        break;

                    case Constants.GAMESCREEN:

                        if (isGameLoaded())
                            {
                                pGameManager.handleEvent(event);
                            }

                        break;

                    }

            }

        public void drawFPS(Canvas canvas, long fElapsedTime)
            {

                fTimeSum += fElapsedTime;
                if (fTimeSum >= 1000)
                    {

                        fTimeSum = 0;
                        FPS = nFpsCount;
                        nFpsCount = 0;

                    }

                // System.out.println("fps"+nFpsCount);
                pDrawUtil.drawText(canvas, "FPS : " + FPS+" E.T "+fElapsedTime, 10, 465, 12,
                                    Color.BLACK);
                nFpsCount++;

            }

        public  void drawTitle(Canvas canvas,long elapsedTime)
        {
            int frameId=0;
            float cx,cy,cw,ch;

            pDrawUtil.drawStrokeText(canvas, "CAPTURE", 150, 40,
                    20, Color.BLACK, Color.WHITE, 2);

            fFrameId+=(fTitleFramelen/1000)*elapsedTime;

            //System.out.println(fFrameId);
            if(fFrameId>=fTitleFramelen)
                fFrameId=0;

            frameId=(int)fFrameId;
            cx=pTitleBallFrames[frameId][0];
            cy=pTitleBallFrames[frameId][1];
            cw=pTitleBallFrames[frameId][2];
            ch=pTitleBallFrames[frameId][3];


            pDrawUtil.drawImage(canvas, pBitmap_mgr
                            .getBitmap(R.drawable.type1_3), cx,
                    cy,cw, ch, 40, 20, 25, 25, false);

        }

        public void draw(Canvas canvas, float startFrame, long elapsedTime)
            {
//                pDrawUtil.clearRect(canvas, 0, 0, 320, 480,
//                                    Color.rgb(255, 240, 245));

                pDrawUtil.drawImage(canvas,pBitmap_mgr.getBitmap(R.drawable.bg),0,0,1,1,0,0,320,480,false);

                drawFPS(canvas, elapsedTime);

                if (Constants.bChangeScreen)
                    {
                        Constants.nScreen = Constants.nSwitchScreen;
                        Constants.nMode = Constants.nSwitchMode;
                        Constants.nSubMode = Constants.nSwitchSubMode;
                        Constants.bChangeScreen = false;
                        
                        if(Constants.nScreen==Constants.MAIN_SCREEN)
                            {
                                pGameManager.setLevelData();
                                setGameLoadStatus(true);

                            }
                    }

                switch (Constants.nScreen)
                    {
                    case Constants.MAIN_SCREEN:


                        pBtn_Play.drawButton(canvas);
                        if (isGameLoaded())
                            {
                                pGameManager.updateDrawBuffer(canvas,
                                                    startFrame, elapsedTime);
                            }



                        drawTitle(canvas,elapsedTime);


                        pBtn_Settings.drawButton(canvas);
                        pBtn_Share.drawButton(canvas);

                        break;

                    case Constants.SETTINGS_SCREEN:

                        pDrawUtil.drawStrokeText(canvas, "Settings", 150, 30,
                                            20, Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawLine(canvas, 20, 40, 300, 40,
                                            Color.BLACK, 1);

                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.board), 0, 0,
                                            512, 512, 110, 55, 20, 20, false);
                        pDrawUtil.drawStrokeText(canvas, "BOARD", 167, 70, 15,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawLine(canvas, 117, 80, 207, 80,
                                            Color.BLACK, 1);
                        for (int i = 0, l = pBoardButton.length; i < l; i++)
                            {
                                if (i == Constants.nCurrentBoardIndex)
                                    {
                                        pBoardButton[i].changeBitmap(pBitmap_mgr
                                                            .getBitmap(R.drawable.select));
                                    } else
                                    {
                                        pBoardButton[i].changeBitmap(pBitmap_mgr
                                                            .getBitmap(R.drawable.unselect));
                                    }

                                if (i == Constants.nCurrentPlayerIndex)
                                    {
                                        pPlayerButton[i].changeBitmap(pBitmap_mgr
                                                            .getBitmap(R.drawable.select));
                                    } else
                                    {
                                        pPlayerButton[i].changeBitmap(pBitmap_mgr
                                                            .getBitmap(R.drawable.unselect));
                                    }
                                pBoardButton[i].drawButton(canvas);
                                pPlayerButton[i].drawButton(canvas);

                            }

                        pDrawUtil.drawStrokeText(canvas, "5x5 ", 91, 115, 10,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawStrokeText(canvas, "6x6 ", 161, 115, 10,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawStrokeText(canvas, "7x7 ", 236, 115, 10,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawStrokeText(canvas, "8x8 ", 91, 175, 10,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawStrokeText(canvas, "9x9 ", 163, 175, 10,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawStrokeText(canvas, "10x10 ", 234, 175,
                                            10, Color.BLACK, Color.WHITE, 2);

                        pDrawUtil.drawAnimSprite(
                                            canvas,
                                            pBitmap_mgr.getBitmap(R.drawable.singleplayer),
                                            0, 0, 512, 512, 110, 215, 20, 20,
                                            false);
                        pDrawUtil.drawStrokeText(canvas, "PLAYERS", 167, 228,
                                            15, Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawLine(canvas, 117, 238, 207, 238,
                                            Color.BLACK, 1);
                        pDrawUtil.drawStrokeText(canvas, "2 ", 92, 277, 10,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawStrokeText(canvas, "3 ", 164, 277, 10,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawStrokeText(canvas, "4 ", 237, 277, 10,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawStrokeText(canvas, "5 ", 92, 334, 10,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawStrokeText(canvas, "6 ", 164, 334, 10,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawStrokeText(canvas, "7 ", 237, 334, 10,
                                            Color.BLACK, Color.WHITE, 2);

                        pDrawUtil.drawStrokeText(canvas, "Difficulty", 167,
                                            380, 15, Color.BLACK, Color.WHITE,
                                            2);
                        pDrawUtil.drawLine(canvas, 117, 390, 207, 390,
                                            Color.BLACK, 1);
                        pDrawUtil.drawStrokeText(canvas, ""
                                            + Constants.nDifficultyCount, 164,
                                            422, 20, Color.BLACK, Color.WHITE,
                                            2);

                        pBtn_About.drawButton(canvas);

                        pBtn_Sound.changeBitmap(Constants.bSound ? pBitmap_mgr
                                            .getBitmap(R.drawable.sound)
                                            : pBitmap_mgr.getBitmap(R.drawable.mute));
                        pBtn_Sound.drawButton(canvas);
                        pBtn_Back.drawButton(canvas);
                        pBtn_DiffDecrease.drawButton(canvas);
                        pBtn_DiffIncrease.drawRotateButton(canvas, 180);

                        break;

                    case Constants.ABOUT_SCREEN:

                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.about), 0, 0,
                                            640, 960, 0, 0, 320, 480, false);

                        pBtn_Back.drawButton(canvas);

                        break;

                    case Constants.SHARE_SCREEN:

                        pBtn_Back.drawButton(canvas);

                        pDrawUtil.drawText(canvas, "BOARD", 90, 20, 16,
                                            Color.BLUE);
                        pDrawUtil.drawText(canvas, "PLAYER", 90, 110, 16,
                                            Color.BLUE);
                        pDrawUtil.drawText(canvas, "MODE", 90, 200, 16,
                                            Color.BLUE);

                        pDrawUtil.drawRoundRect(canvas, 87, 23, 56, 2, 2,
                                            Color.MAGENTA);

                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 10, 40, 50, 50, false);
                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 70, 40, 50, 50, false);
                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 130, 40, 50, 50, false);
                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 190, 40, 50, 50, false);

                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 10, 130, 50, 50, false);
                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 70, 130, 50, 50, false);
                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 130, 130, 50, 50,
                                            false);
                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 190, 130, 50, 50,
                                            false);

                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 10, 200, 50, 50, false);
                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 70, 200, 50, 50, false);
                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 130, 200, 50, 50,
                                            false);
                        pDrawUtil.drawAnimSprite(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type3_1), 0,
                                            0, 512, 512, 190, 200, 50, 50,
                                            false);

                        break;

                    case Constants.SUBMODE_SCREEN:

                        pBtn_MoveMode.drawButton(canvas);
                        pBtn_TimeMode.drawButton(canvas);
                        pBtn_InfiniteMode.drawButton(canvas);
                        pBtn_Back.drawButton(canvas);

                        pDrawUtil.drawStrokeText(canvas, "Modes", 150, 30, 20,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawLine(canvas, 20, 40, 300, 40,
                                            Color.BLACK, 1);

                        pDrawUtil.drawStrokeText(canvas, "Moves", 150, 185, 12,
                                            Color.BLACK, Color.WHITE, 2);

                        pDrawUtil.drawStrokeText(canvas, "Timed", 150, 295, 12,
                                            Color.BLACK, Color.WHITE, 2);
                        pDrawUtil.drawStrokeText(canvas, "Endless", 150, 405,
                                            12, Color.BLACK, Color.WHITE, 2);

                        if (Constants.nMode == Constants.MODE_SINGLEPLAYER)
                            pDrawUtil.drawAnimSprite(
                                                canvas,
                                                pBitmap_mgr.getBitmap(R.drawable.singleplayer),
                                                0, 0, 512, 512, 90, 15, 20, 20,
                                                true);
                        else
                            pDrawUtil.drawAnimSprite(
                                                canvas,
                                                pBitmap_mgr.getBitmap(R.drawable.multiplayer),
                                                0, 0, 512, 512, 90, 15, 20, 20,
                                                true);

                        break;

                    case Constants.MODE_SCREEN:

                        pBtn_SinglePlayer.drawButton(canvas);
                        pBtn_MultiPlayer.drawButton(canvas);

                        // pBtn_Settings.drawButton(canvas);
                        // pBtn_Share.drawButton(canvas);

                        pDrawUtil.drawStrokeText(canvas, "Modes", 150, 30, 20,
                                            Color.BLACK, Color.WHITE, 2);
                        
                        pDrawUtil.drawImage(canvas, pBitmap_mgr
                                            .getBitmap(R.drawable.type1_3), 0,
                                            0, 0.125f,1, 40, 20, 25, 25, false);

                        pDrawUtil.drawStrokeText(canvas, "SinglePlayer", 160,
                                            220, 12, Color.BLACK, Color.WHITE,
                                            2);
                        pDrawUtil.drawStrokeText(canvas, "MultiPlayer", 160,
                                            370, 12, Color.BLACK, Color.WHITE,
                                            2);

                        pBtn_Back.drawButton(canvas);

                        //

                        break;

                    case Constants.GAMESCREEN:

                        if (isGameLoaded())
                            {
                                pGameManager.updateDrawBuffer(canvas,
                                                    startFrame, elapsedTime);
                            }

                        break;

                    }

                // pGameManager.updateDrawBuffer(canvas, startFrame,
                // elapsedTime);

            }

    }
