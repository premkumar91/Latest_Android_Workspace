package com.pngamesandapps.coin;

public class Constants
    {

        public static final int GAMESCREEN        = 0;

        /*
         * Game Screens
         */
        public static final int MAIN_SCREEN       = 1;
        public static final int MODE_SCREEN       = 2;

        /*
         * Game Mode
         */
        public static final int MODE_SINGLEPLAYER = 3;
        public static final int MODE_MUTIPLAYER   = 4;

        public static final int MODE_INFINITE     = 5;
        public static final int MODE_MOVES        = 6;
        public static final int MODE_TIMED        = 7;
        public static final int MODE_DEMO        = 8;
        

        // RESERVED

        public static final int SETTINGS_SCREEN   = 40;
        public static final int HELP_SCREEN       = 41;
        public static final int SHARE_SCREEN      = 42;
        public static final int SUBMODE_SCREEN    = 43;
        public static final int ABOUT_SCREEN      = 44;
        
        
        public static final String MY_PREFERENCES    = "MyPrefs";
        public static final String KEY_DIFFICULTY    = "difficulty";
        public static final String KEY_SOUND = "SOUND";
        public static final String KEY_FIRSTINSTALL = "firstinstall";
        public static final String KEY_BOARDS = "boards";
        public static final String KEY_PLAYERS = "players";
        
       public static int[] nBoards={5,6,7,8,9,10};
       public static int[] nPlayers={2,3,4,5,6,7};
       public static int nCurrentBoardIndex=0;
       public static int nCurrentPlayerIndex=0;
       public static int nDifficultyCount=1;
       
                           
       

        /*
         * Screen,Mode getter & setter
         */
        static boolean bChangeScreen=false;
        static int nSwitchScreen =  -1;
        static int nSwitchMode=-1;
        static int nSwitchSubMode=-1;
        
        
        
        static int              nScreen           = -1;
        static int              nMode             = -1;
        static int              nSubMode          = -1;
        static int              nLevelUpState     = -1;

        static boolean          bSound            = true;
        static boolean          bThreadState      = false;

        public static final int DEFAULTWIDTH      = 320;
        public static final int DEFAULTHEIGHT     = 480;

    }
