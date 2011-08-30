//TODO: Future Ideas
// Animation Manager class - for polishing
// Weapon upgrades
// Multiple backgrounds

package com.memeworks.frontiers;

import com.memeworks.frontiersdemo.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class Frontiers extends Activity {
 
	//Enums
	public static final long SPLASH_TIME = 2000;
	
	public static final int GAMETYPE_COUNTDOWN = 1;
	public static final int GAMETYPE_SURVIVAL = 2;
	public static final int GAMETYPE_ELIMINATION = 3;
	public static final int GAMETYPE_UNARMED = 4;
	
	public static final String GAMESTRING_COUNTDOWN = "COUNTDOWN ";
	public static final String GAMESTRING_SURVIVAL = "SURVIVAL ";
	public static final String GAMESTRING_ELIMINATION = "ELIMINATION ";
	public static final String GAMESTRING_UNARMED = "UNARMED ";

	public static final int GAMESTATE_GAMEOVER = 1;
	public static final int GAMESTATE_PAUSE = 2;
	public static final int GAMESTATE_READY = 3;
	public static final int GAMESTATE_RUNNING = 4;

	public static final int MOVABLESTATE_INVINCIBLE = 1;
	public static final int MOVABLESTATE_SPAWNING = 2;
	public static final int MOVABLESTATE_ALIVE = 3;
	public static final int MOVABLESTATE_DYING = 4;
	public static final int MOVABLESTATE_DEAD = 5;

	public static final int AI_PLAYER = 0;
	public static final int AI_CHASE = 1;
	public static final int AI_RANDOM = 2;
	public static final int AI_SQUARE_CW = 3;
	public static final int AI_SQUARE_CCW = 4;
	public static final int AI_BOUNCE_CW = 5;
	public static final int AI_BOUNCE_CCW = 6;
	public static final int AI_PATROL_UD = 7;
	public static final int AI_PATROL_LR = 8;
	
	public static final int POWER_UP_BOMB = 9;
	public static final int POWER_UP_FREEZE = 10;
	public static final int POWER_UP_SHIELD = 11;
	
	public static final int SOUND_MUSIC = 0;
	public static final int SOUND_SHOOT = 1;
	public static final int SOUND_PLAYERSPAWN = 2;
	public static final int SOUND_ENEMYSPAWN = 3;
	public static final int SOUND_DEATH = 4;
	public static final int SOUND_PLAYERDEATH = 5;
	public static final int SOUND_SHIELDS = 6;
	public static final int SOUND_FREEZE = 7;
	public static final int SOUND_BOMB = 8;
	
	public static int SCREEN_WIDTH = 0;
	public static int SCREEN_HEIGHT = 0;
	public static int SCREEN_CENTER_X = 0;
	public static int SCREEN_CENTER_Y = 0;
	
	public static final int SCREENSIZE_LARGE = 3;
	public static final int SCREENSIZE_MEDIUM = 2;
	public static final int SCREENSIZE_SMALL = 1;
	
	//Static inter-activity variables
	public static boolean firstRun = true;
	public static boolean Loaded = false;
	public static int SelectedGametype = -1;
	public static boolean MultiTouchSupported = false;
	public static int ScreenSize = 2;
	public static int LastScore = 0;
	public static String LastScoreInitials = "Anon";
	public static boolean SilentMode = true;
	public static String GameOverMessage = "";
	
	public static String[] CountdownNames;
	public static String[] EliminationNames;
	public static String[] SurvivalNames;
	public static String[] UnarmedNames;
	public static int[] CountdownScores;
	public static int[] EliminationScores;
	public static int[] SurvivalScores;
	public static int[] UnarmedScores;
	
	public static SoundManager SOUND_MANAGER;
	public static Typeface FONT;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.splash);
         
         Frontiers.FONT = Typeface.createFromAsset(getAssets(), "fonts/rexlia.ttf");
         SOUND_MANAGER = new SoundManager();

         // Restore preferences and scores
         SharedPreferences settings = getSharedPreferences("prefs", 0);
         SilentMode = settings.getBoolean("silentMode", true);
         firstRun = settings.getBoolean("firstRun", true);
         LastScoreInitials = settings.getString("LastInitials", "Anon");
         
         CountdownNames = new String[10];
     	 EliminationNames = new String[10];
     	 SurvivalNames = new String[10];
     	 UnarmedNames = new String[10];
     	 CountdownScores = new int[10];
     	 EliminationScores = new int[10];
     	 SurvivalScores = new int[10];
     	 UnarmedScores = new int[10];
     	 
         for (int i = 1; i <= 10; i ++) {
        	 CountdownNames[i - 1] = settings.getString("CountdownName" + i, "Anon");
        	 CountdownScores[i - 1] = settings.getInt("CountdownScore" + i, 0);
        	 
        	 EliminationNames[i - 1] = settings.getString("EliminationName" + i, "Anon");
        	 EliminationScores[i - 1] = settings.getInt("EliminationScore" + i, 1000);
        	 
        	 SurvivalNames[i - 1] = settings.getString("SurvivalName" + i, "Anon");
        	 SurvivalScores[i - 1] = settings.getInt("SurvivalScore" + i, 0);
        	 
        	 UnarmedNames[i - 1] = settings.getString("UnarmedName" + i, "Anon");
        	 UnarmedScores[i - 1] = settings.getInt("UnarmedScore" + i, 0);
         }

         /* New Handler to start the Menu-Activity
          * and close this Splash-Screen after some seconds.*/
         new Handler().postDelayed(new Runnable(){

              public void run() {
            	   ImageView splash = (ImageView) findViewById(R.id.splashscreen);
            	   splash.startAnimation(AnimationUtils.loadAnimation(Frontiers.this, android.R.anim.fade_out));
                   /* Create an Intent that will start the Menu-Activity. */
                   Intent mainMenuIntent = new Intent(Frontiers.this, MainMenu.class);
                   Frontiers.this.startActivity(mainMenuIntent);
                   Frontiers.this.finish();
              }
         }, SPLASH_TIME);

    }
    
    @Override
    protected void onStop() {
    	super.onStop();
        Frontiers.this.finish();
    }
    
}