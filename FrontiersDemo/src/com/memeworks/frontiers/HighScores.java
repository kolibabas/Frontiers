package com.memeworks.frontiers;

import com.memeworks.frontiersdemo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class HighScores extends Activity {
	
	private Spinner gametypeSelector;
	private ListView scoreList;
	private ArrayAdapter<String> scoreArray;
	private ArrayAdapter<String> gametypeArray;
	private boolean finishing = false;

	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.highscores);
        
        TextView t = (TextView) findViewById(R.id.highscorestext);
        t.setTypeface(Frontiers.FONT);        
        
        scoreList = (ListView) findViewById(R.id.highscorelist);
        scoreArray = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Frontiers.CountdownNames);
        scoreList.setAdapter(scoreArray);
        
        finishing = false;
        Button backButton = (Button) findViewById(R.id.highscorebackbutton);
        backButton.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				if (finishing == false) {
					finishing = true;
					Intent gameIntent = new Intent(HighScores.this, MainMenu.class);
		        	HighScores.this.startActivity(gameIntent);
		        	HighScores.this.finish();
				}
				return true;
			}
		});

        gametypeSelector = (Spinner) findViewById(R.id.highscoregametypespinner);
        gametypeArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
        		new String[] { "Countdown", "Elimination", "Survival", "Unarmed"});
        gametypeArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gametypeSelector.setAdapter(gametypeArray);
        gametypeSelector.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				try{
				String[] listItems = new String[10];
				switch (position) {
				case 0: //Countdown
					for (int i = 0; i < 10; i++) {
						listItems[i] = (i + 1) + ": " + Frontiers.CountdownNames[i] + "   -   " + Frontiers.CountdownScores[i];
					}
					break;
				case 1: //Elimination
					for (int i = 0; i < 10; i++) {
						listItems[i] = (i + 1) + ": " + Frontiers.EliminationNames[i] + "   -   " + Frontiers.EliminationScores[i] + " seconds";
					}
					break;
				case 2: //Survival
					for (int i = 0; i < 10; i++) {
						listItems[i] = (i + 1) + ": " + Frontiers.SurvivalNames[i] + "   -   " + Frontiers.SurvivalScores[i];
					}
					break;
				case 3: //Unarmed
					for (int i = 0; i < 10; i++) {
						listItems[i] = (i + 1) + ": " + Frontiers.UnarmedNames[i] + "   -   " + Frontiers.UnarmedScores[i] + " seconds";
					}
					break;
				}
				
				scoreArray = new ArrayAdapter<String>(HighScores.this, android.R.layout.simple_list_item_1, listItems);
		        scoreList.setAdapter(scoreArray);
				}
				catch (Exception ex) {
					Log.e("Highscores", ex.toString());
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				scoreList.setSelection(0);
			}

        });
    }
    
    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Intent gameIntent = new Intent(HighScores.this, MainMenu.class);
        	HighScores.this.startActivity(gameIntent);
        	HighScores.this.finish();
        }
        
        return true;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent evt) {
    	return true;
    }
    

}
