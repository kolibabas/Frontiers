package com.memeworks.frontiers;

import com.memeworks.frontiersdemo.R;
import com.scoreninja.adapter.ScoreNinjaAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class MainMenu extends Activity {
	private ScoreNinjaAdapter scoreNinjaAdapter;

	private ImageButton optionsButton;
	private ImageButton playButton;
	private boolean finishing = false;
	private boolean highscoreDialogShown = false;
	AlertDialog mGameOverDialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        
        finishing = false;
        highscoreDialogShown = false;
        scoreNinjaAdapter = new ScoreNinjaAdapter(this, "frontiersscores", "C4541A600266B8E96B42C1BC952C982C");
        
        if (Frontiers.firstRun) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
    		builder.setMessage("Since this is your first flight, please take a moment to look over your controls and combat briefing.")
    		       .setCancelable(false)
    		       .setTitle("Welcome, Cadet")
    		       .setPositiveButton("Teach me!", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		        	   instructionsButtonClicked();
    		        	   dialog.dismiss();
    		           }
    		       })
    		       .setNegativeButton("Nope.", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		        	   dialog.dismiss();
    		           }
    		       }).create().show();
    		
    		Frontiers.firstRun = false;
    		SharedPreferences settings = getSharedPreferences("prefs", 0);
    		SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstRun", false);
            editor.commit();
        }
        
        try{
    	if (Frontiers.SelectedGametype != -1 && Frontiers.LastScore != 0) {
           	switch (Frontiers.SelectedGametype) {
           	case Frontiers.GAMETYPE_COUNTDOWN:
           		if (Frontiers.LastScore > Frontiers.CountdownScores[9]) {
           			showHighScoreDialog();
           		}
           		break;
           	case Frontiers.GAMETYPE_ELIMINATION:
           		if (Frontiers.LastScore < Frontiers.EliminationScores[9]) {
           			showHighScoreDialog();
           		}
           		break;
           	case Frontiers.GAMETYPE_SURVIVAL:
           		if (Frontiers.LastScore > Frontiers.SurvivalScores[9]) {
           			showHighScoreDialog();
           		}
           		break;
           	case Frontiers.GAMETYPE_UNARMED:
           		if (Frontiers.LastScore > Frontiers.UnarmedScores[9]) {
           			showHighScoreDialog();
           		}
           		break;
           }
        }
    	
    	if (Frontiers.GameOverMessage.length() > 1 && highscoreDialogShown == false) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
    		builder.setMessage(Frontiers.GameOverMessage)
    		       .setCancelable(false)
    		       .setTitle("Game Over")
    		       .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		        	   dialog.dismiss();
    		           }
    		       });
    		       
    		mGameOverDialog = builder.create();
    		mGameOverDialog.show();
    		Frontiers.GameOverMessage = "";
        }
        
        }
        catch (Exception ex) {
        	Log.e("Return dialog", ex.getStackTrace().toString());
        }
        
        playButton = (ImageButton) findViewById(R.id.playbutton);
        playButton.setFocusable(false);
        playButton.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				playButton.setPressed(true);
				playButtonClicked();
				return true;
			}
		});
        
        final ImageButton instructionsButton = (ImageButton) findViewById(R.id.instructionsbutton);
        instructionsButton.setFocusable(false);
        instructionsButton.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				instructionsButton.setPressed(true);
				instructionsButtonClicked();
				return true;
			}
		});
        
        optionsButton = (ImageButton) findViewById(R.id.optionsbutton);
        optionsButton.setFocusable(false);
        if (Frontiers.SilentMode) {
        	optionsButton.setImageResource(R.drawable.soundoff);
        }
        else {
        	optionsButton.setImageResource(R.drawable.soundon);
        }
        optionsButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				optionsButtonClicked();
			}
		});
        
        final ImageButton highscoreButton = (ImageButton) findViewById(R.id.highscorebutton);
        highscoreButton.setFocusable(false);
        highscoreButton.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				highscoreButton.setPressed(true);
				highscoreButtonClicked();
				return true;
			}
		});
    }

	protected void instructionsButtonClicked() {
		if (!finishing) {
			finishing = true;
			/* Create an Intent that will start the Game-Activity. */
	        Intent gameIntent = new Intent(MainMenu.this, Instructions.class);
	        MainMenu.this.startActivity(gameIntent);
	        MainMenu.this.finish();
		}
	}

	protected void highscoreButtonClicked() {
		if (!finishing) {
			finishing = true;
			
			/* Create an Intent that will start the Game-Activity. */
	        Intent gameIntent = new Intent(MainMenu.this, HighScores.class);
	        MainMenu.this.startActivity(gameIntent);
	        MainMenu.this.finish();
		}
	}

	protected void playButtonClicked() {
		if (!finishing) {
			finishing = true;
			/* Create an Intent that will start the Game-Activity. */
	        Intent gameIntent = new Intent(MainMenu.this, GametypeSelector.class);
	        MainMenu.this.startActivity(gameIntent);
	        MainMenu.this.finish();
		}
	}

	protected void optionsButtonClicked() {
    	Frontiers.SilentMode = !Frontiers.SilentMode;
    	if (Frontiers.SilentMode) {
        	optionsButton.setImageResource(R.drawable.soundoff);
        }
        else {
        	optionsButton.setImageResource(R.drawable.soundon);
        }
    	// Save user preferences.
        SharedPreferences settings = getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("silentMode", Frontiers.SilentMode);
        editor.commit();
	}
	
	private void showHighScoreDialog() {
		highscoreDialogShown = true;
		final FrameLayout fl = new FrameLayout(this);
		final EditText input = new EditText(this);
		input.setGravity(Gravity.CENTER);

		fl.addView(input, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

		input.setText(Frontiers.LastScoreInitials);
		new AlertDialog.Builder(this)
		     .setView(fl)
		     .setTitle(Frontiers.GameOverMessage + "! Enter Initials:")
		     .setPositiveButton("Save", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					String text = input.getText().toString();
					if (text.length() > 0) {
						Frontiers.LastScoreInitials = text;
					}
					
					switch (Frontiers.SelectedGametype) {
		        	case Frontiers.GAMETYPE_COUNTDOWN:
		        		for (int i = 1; i <= 10; i++) {
		        			if (Frontiers.LastScore > Frontiers.CountdownScores[i - 1]) {
		            			saveHighScore(Frontiers.GAMETYPE_COUNTDOWN, i);
		            			break;
		            		}
		        		}
		        		break;
		        	case Frontiers.GAMETYPE_ELIMINATION:
		        		for (int i = 1; i <= 10; i++) {
		        			if (Frontiers.LastScore < Frontiers.EliminationScores[i - 1]) {
		            			saveHighScore(Frontiers.GAMETYPE_ELIMINATION, i);
		            			break;
		            		}
		        		}
		        		break;
		        	case Frontiers.GAMETYPE_SURVIVAL:
		        		for (int i = 1; i <= 10; i++) {
		        			if (Frontiers.LastScore > Frontiers.SurvivalScores[i - 1]) {
		            			saveHighScore(Frontiers.GAMETYPE_SURVIVAL, i);
		            			break;
		            		}
		        		}
		        		break;
		        	case Frontiers.GAMETYPE_UNARMED:
		        		for (int i = 1; i <= 10; i++) {
		        			if (Frontiers.LastScore > Frontiers.UnarmedScores[i - 1]) {
		            			saveHighScore(Frontiers.GAMETYPE_UNARMED, i);
		            			break;
		            		}
		        		}
		        		break;
		        	}
					Frontiers.LastScore = 0;
					dialog.dismiss();
				}
		     })
		     .setNeutralButton("Online", new DialogInterface.OnClickListener() {
		    	 public void onClick(DialogInterface dialog, int which) {
	    			String gametypeString = "Countdown";
	    			switch(Frontiers.SelectedGametype)
	    			{
	    			case Frontiers.GAMETYPE_SURVIVAL:
	    				gametypeString = "Survival";
	    				break;
	    				
	    			case Frontiers.GAMETYPE_ELIMINATION:
	    				gametypeString = "Elimination";
	    				break;
	    				
	    			case Frontiers.GAMETYPE_UNARMED:
	    				gametypeString = "Unarmed";
	    				break;
	    			}
	    			
	    			scoreNinjaAdapter.show(Frontiers.LastScore, null, gametypeString);
		    	 }
		     })
		     .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					Frontiers.LastScore = 0;
					dialog.dismiss();
				}
			}).create().show();
		Frontiers.GameOverMessage = "";
	}

	protected void saveHighScore(int gametype, int position) {
		SharedPreferences settings = getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        
		switch(gametype) {
		case Frontiers.GAMETYPE_COUNTDOWN:
			if (position != 10) {
				System.arraycopy(Frontiers.CountdownScores, position - 1, Frontiers.CountdownScores, position, 10 - position); 
				System.arraycopy(Frontiers.CountdownNames, position - 1, Frontiers.CountdownNames, position, 10 - position); 
				Frontiers.CountdownScores[position - 1] = Frontiers.LastScore;
				Frontiers.CountdownNames[position - 1] = Frontiers.LastScoreInitials;
			}
			else {
				Frontiers.CountdownScores[9] = Frontiers.LastScore;
				Frontiers.CountdownNames[9] = Frontiers.LastScoreInitials;
			}
			
            editor.putInt("CountdownScore" + position, Frontiers.LastScore);
            editor.putString("CountdownName" + position, Frontiers.LastScoreInitials);
			break;
    	case Frontiers.GAMETYPE_ELIMINATION:
    		if (position != 10) {
				System.arraycopy(Frontiers.EliminationScores, position - 1, Frontiers.EliminationScores, position, 10 - position); 
				System.arraycopy(Frontiers.EliminationNames, position - 1, Frontiers.EliminationNames, position, 10 - position);
				Frontiers.EliminationScores[position - 1] = Frontiers.LastScore;
				Frontiers.EliminationNames[position - 1] = Frontiers.LastScoreInitials;
			}
			else {
				Frontiers.EliminationScores[9] = Frontiers.LastScore;
				Frontiers.EliminationNames[9] = Frontiers.LastScoreInitials;
			}
			
            editor.putInt("EliminationScore" + position, Frontiers.LastScore);
            editor.putString("EliminationName" + position, Frontiers.LastScoreInitials);
    		break;
    	case Frontiers.GAMETYPE_SURVIVAL:
    		if (position != 10) {
				System.arraycopy(Frontiers.SurvivalScores, position - 1, Frontiers.SurvivalScores, position, 10 - position); 
				System.arraycopy(Frontiers.SurvivalNames, position - 1, Frontiers.SurvivalNames, position, 10 - position);
				Frontiers.SurvivalScores[position - 1] = Frontiers.LastScore;
				Frontiers.SurvivalNames[position - 1] = Frontiers.LastScoreInitials;
			}
			else {
				Frontiers.SurvivalScores[9] = Frontiers.LastScore;
				Frontiers.SurvivalNames[9] = Frontiers.LastScoreInitials;
			}
			
            editor.putInt("SurvivalScore" + position, Frontiers.LastScore);
            editor.putString("SurvivalName" + position, Frontiers.LastScoreInitials);
    		break;
    	case Frontiers.GAMETYPE_UNARMED:
    		if (position != 10) {
				System.arraycopy(Frontiers.UnarmedScores, position - 1, Frontiers.UnarmedScores, position, 10 - position); 
				System.arraycopy(Frontiers.UnarmedNames, position - 1, Frontiers.UnarmedNames, position, 10 - position);
				Frontiers.UnarmedScores[position - 1] = Frontiers.LastScore;
				Frontiers.UnarmedNames[position - 1] = Frontiers.LastScoreInitials;
			}
			else {
				Frontiers.UnarmedScores[9] = Frontiers.LastScore;
				Frontiers.UnarmedNames[9] = Frontiers.LastScoreInitials;
			}
			
            editor.putInt("UnarmedScore" + position, Frontiers.LastScore);
            editor.putString("UnarmedName" + position, Frontiers.LastScoreInitials);
    		break;
    	}
		
		editor.putString("LastInitials", Frontiers.LastScoreInitials);
		editor.commit();
	}

	/**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MainMenu.this.finish();
        }
        
        return true;
    }
    
    @Override
    protected void onActivityResult(
        int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      scoreNinjaAdapter.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
        MainMenu.this.finish();
    }

}
