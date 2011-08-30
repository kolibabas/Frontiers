package com.memeworks.frontiers;

import com.memeworks.frontiers.MainView.MainThread;
import com.memeworks.frontiersdemo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class Game extends Activity {

    /** A handle to the thread that's actually running the animation. */
    private MainThread mMainThread;

    /** A handle to the View in which the game is running. */
    private MainView mMainView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // tell system to use the layout defined in our XML file
        setContentView(R.layout.main);
        
        // Use volume buttons to control sound
        //setVolumeControlStream(AudioManager.STREAM_MUSIC);
        SoundManager.initSounds(getBaseContext());

        // get handles to the MainView from XML, and its MainThread
        mMainView = (MainView) findViewById(R.id.game);
        mMainView.setGametype(Frontiers.SelectedGametype);
        mMainThread = mMainView.getThread(); 
        mMainThread.setGamePointer(Game.this);
        
        if (mMainThread.mState == Frontiers.GAMESTATE_GAMEOVER) {
        	mMainThread.doStart();
        }
        Frontiers.LastScore = 0;
    } 
	
    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMainThread.setRunning(false);
    }
    
    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	mMainThread.pause();
        	mMainThread.mBackgroundImage = null;
        	Intent gameIntent = new Intent(Game.this, MainMenu.class);
            Game.this.startActivity(gameIntent);
            Game.this.finish();
        }
        
        return true;
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	SoundManager.release();
    }

}
