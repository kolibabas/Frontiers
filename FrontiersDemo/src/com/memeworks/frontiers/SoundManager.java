package com.memeworks.frontiers;

import java.util.HashMap;

import com.memeworks.frontiersdemo.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundManager {
	private static SoundPool mSoundPool;
	private static HashMap<Integer, Integer> mSoundPoolMap;
	public static AudioManager  mAudioManager;
	private static Context mContext;
	
	public SoundManager() {
    
	}
	
	public static void initSounds(Context context) {
	    mContext = context;
	    mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
	    mSoundPoolMap = new HashMap<Integer, Integer>();
	    mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

	    mSoundPoolMap.put(Frontiers.SOUND_PLAYERSPAWN, mSoundPool.load(mContext, R.raw.playerspawn, 1));
	    mSoundPoolMap.put(Frontiers.SOUND_ENEMYSPAWN, mSoundPool.load(mContext, R.raw.enemyspawn, 1));
	    mSoundPoolMap.put(Frontiers.SOUND_DEATH, mSoundPool.load(mContext, R.raw.enemydeath, 1));
	    mSoundPoolMap.put(Frontiers.SOUND_PLAYERDEATH, mSoundPool.load(mContext, R.raw.playerdeath, 1));
	    //mSoundPoolMap.put(Frontiers.SOUND_SHOOT, mSoundPool.load(mContext, R.raw.shootbass, 1));
	    mSoundPoolMap.put(Frontiers.SOUND_SHIELDS, mSoundPool.load(mContext, R.raw.shieldsup, 1));
	    mSoundPoolMap.put(Frontiers.SOUND_FREEZE, mSoundPool.load(mContext, R.raw.freeze, 1));
	    mSoundPoolMap.put(Frontiers.SOUND_BOMB, mSoundPool.load(mContext, R.raw.bomb, 1));
	}
	
	public static void playSound(int index)
	{
		if (!Frontiers.SilentMode && mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
			
			if (mSoundPoolMap.containsKey(index)) {
				int soundID = mSoundPoolMap.get(index);
				mSoundPool.play(soundID, 1, 1, 1, 0, 1f);
			}
		}
	}
	
	public static final void release() {
		mSoundPool.release();
	}
}
