package com.memeworks.frontiers;

import com.memeworks.frontiersdemo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

public class GametypeSelector extends Activity {
	
	public int mDisplayWidth;
	public int mDisplayHeight;
	private boolean finishing = false;
	public static ImageButton countdownButton;
	public static ImageButton eliminationButton;
	public static ImageButton survivalButton;
	public static ImageButton unarmedButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.gametypeselect);

         finishing = false;
         WindowManager w = getWindowManager();
         Display d = w.getDefaultDisplay();
         mDisplayWidth = d.getWidth();
         mDisplayHeight = d.getHeight(); 
         
         Frontiers.LastScore = 0;
         
         countdownButton = (ImageButton)findViewById(R.id.countdownbutton);
         countdownButton.setFocusable(false);
         countdownButton.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				Frontiers.SelectedGametype = Frontiers.GAMETYPE_COUNTDOWN;
				GametypeSelector.countdownButton.setPressed(true);
				if (Frontiers.SelectedGametype != -1 && !finishing) {
					finishing = true;
			    	/* Create an Intent that will start the Game-Activity. */
			        Intent gameIntent = new Intent(GametypeSelector.this, Game.class);
			        GametypeSelector.this.startActivity(gameIntent);
			        GametypeSelector.this.finish();
				}
				return true;
			}
		});
         
        eliminationButton = (ImageButton)findViewById(R.id.eliminationbutton);
        eliminationButton.setFocusable(false);
        eliminationButton.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				Frontiers.SelectedGametype = Frontiers.GAMETYPE_ELIMINATION;
				GametypeSelector.eliminationButton.setPressed(true);
				if (Frontiers.SelectedGametype != -1 && !finishing) {
					finishing = true;
			    	/* Create an Intent that will start the Game-Activity. */
			        Intent gameIntent = new Intent(GametypeSelector.this, Game.class);
			        GametypeSelector.this.startActivity(gameIntent);
			        GametypeSelector.this.finish();
				}
				return true;
			}
		});
        
        survivalButton = (ImageButton)findViewById(R.id.survivalbutton);
        survivalButton.setFocusable(false);
        survivalButton.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				Frontiers.SelectedGametype = Frontiers.GAMETYPE_SURVIVAL;
				GametypeSelector.survivalButton.setPressed(true);
				if (Frontiers.SelectedGametype != -1 && !finishing) {
					finishing = true;
			    	/* Create an Intent that will start the Game-Activity. */
			        Intent gameIntent = new Intent(GametypeSelector.this, Game.class);
			        GametypeSelector.this.startActivity(gameIntent);
			        GametypeSelector.this.finish();
				}
				return true;
			}
		});
        
        unarmedButton = (ImageButton)findViewById(R.id.unarmedbutton);
        unarmedButton.setFocusable(false);
        unarmedButton.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				Frontiers.SelectedGametype = Frontiers.GAMETYPE_UNARMED;
				GametypeSelector.unarmedButton.setPressed(true);
				if (Frontiers.SelectedGametype != -1 && !finishing) {
					finishing = true;
			    	/* Create an Intent that will start the Game-Activity. */
			        Intent gameIntent = new Intent(GametypeSelector.this, Game.class);
			        GametypeSelector.this.startActivity(gameIntent);
			        GametypeSelector.this.finish();
				}
				return true;
			}
		});
    } 
    
    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Intent gameIntent = new Intent(GametypeSelector.this, MainMenu.class);
            GametypeSelector.this.startActivity(gameIntent);
            GametypeSelector.this.finish();
        }
        
        return true;
    }

}
