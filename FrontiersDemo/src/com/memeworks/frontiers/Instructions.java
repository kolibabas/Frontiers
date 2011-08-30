package com.memeworks.frontiers;

import com.memeworks.frontiersdemo.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Instructions extends Activity {

	private boolean finishing = false;
	private RelativeLayout layout;
	private int current_index = 0;
	private Resources resources;
	private int[] instructions = new int[6];
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.instructions);
         resources = getResources();
         
         current_index = 0;
         layout = (RelativeLayout) findViewById(R.id.instructionsbackground);
         layout.setBackgroundDrawable(resources.getDrawable(R.drawable.instruct1));
         
         instructions[0] = R.drawable.instruct1;
         instructions[1] = R.drawable.instruct2;
         instructions[2] = R.drawable.instruct3;
         instructions[3] = R.drawable.instruct4;
         instructions[4] = R.drawable.instruct5;
         instructions[5] = R.drawable.instruct6;
         
         finishing = false;
         Button backButton = (Button) findViewById(R.id.instructionsbackbutton);
         backButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if (finishing == false) {
					finishing = true;
					current_index--;
					
					if (current_index < 0) {
						Intent gameIntent = new Intent(Instructions.this, MainMenu.class);
			        	Instructions.this.startActivity(gameIntent);
			        	Instructions.this.finish();
					}
					else {
						layout.setBackgroundDrawable(resources.getDrawable(instructions[current_index]));
						finishing = false;
					}
				}
			}
		});
         
         final Button nextButton = (Button) findViewById(R.id.instructionsnextbutton);
         nextButton.setText("Next");
         nextButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if (finishing == false) {
					finishing = true;
					current_index++;
					
					if (current_index > 5) {
						Intent gameIntent = new Intent(Instructions.this, MainMenu.class);
			        	Instructions.this.startActivity(gameIntent);
			        	Instructions.this.finish();
			        	
			        	return;
					}
					else if (current_index == 5) {
						nextButton.setText("Done");
					}
					
					layout.setBackgroundDrawable(resources.getDrawable(instructions[current_index]));
					finishing = false;
				}
			}
		});
    } 
    
    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Intent gameIntent = new Intent(Instructions.this, MainMenu.class);
        	Instructions.this.startActivity(gameIntent);
        	Instructions.this.finish();
        }
        
        return true;
    }

}
