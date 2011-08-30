package com.memeworks.frontiers;

import java.util.ArrayList;

import com.memeworks.frontiersdemo.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Build.VERSION;

class MainView extends SurfaceView implements SurfaceHolder.Callback {

	class MainThread extends Thread {
		
        /** State-tracking constants */
		public int mState;
        
        // Android Pointers
	
		private Game mGamePointer;

        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;
        
        /** Current dimensions of the surface/canvas. */
        private int mCanvasHeight = 1;
        private int mCanvasWidth = 1;
        
        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRunning = false;
        
        //private SoundManager mSoundManager;
        
        // UI Items
        private TouchCircleController mMoveControl = null;
        private TouchCircleController mFireControl = null;
        private int mFireTouchIndex = -1;
        private int mMoveTouchIndex = -1;

        // Game Variables
        private Gameworld mWorld = null;
        private PlayerShip pShip = null;
        private Bitmap mPlayerShipImage;        
        private float[] pShipMovement = {0,0,0};
        private float pShootDirectionX = 0;
        private float pShootDirectionY = 0;
        private float pShootTimer = 0; 
        private float pRespawnTimer = 0;
        
        /** The drawable to use as the background of the animation canvas */
        public Bitmap mBackgroundImage;
        public Bitmap mBackgroundGrid;
        
        private Bitmap mChaserImage;
        private Bitmap mRandomerImage;
        private Bitmap mSquarerImage;
        private Bitmap mBouncerImage;
        private Bitmap mPatrollerUDImage;
        private Bitmap mPatrollerLRImage;
        
        private Bitmap mPowerupFreezeImage;
        private Bitmap mPowerupBombImage;
        private Bitmap mPowerupShieldImage;
        private Paint mPowerupShieldPaint = new Paint();
        
        /** Last time updatePhysics was called */
        private long mLastTime;
        private float mTimeSinceLastFrame;
        private ArrayList<Enemy> mCurrentEnemies;
        private int mCurrentEnemyCount = 0;
        private Enemy mCurrentEnemyHolder;
        
        private Shot[] mShots;
        private int mShotCount = 0;
        private Shot mShotHolder;
        
        /** The gametype to be played */
        private Gametype mGame;
        private int mGametype = Frontiers.GAMETYPE_COUNTDOWN;
        private float mGameTimer = 1.0f;
		private float mGameOverTimer;

		private Bitmap mPausedBitmap;

 
		public MainThread(SurfaceHolder holder, Context context, Handler handler) {
            mSurfaceHolder = holder;

            // Parse SDK string as SDK_INT not supported on < 1.5
            // If >= 5 (2.0) then we have multitouch
            if (Integer.parseInt(VERSION.SDK) >= 5) {
            	Frontiers.MultiTouchSupported = true;
            }
      
            // Load Resources here
            Resources res = context.getResources();
            
            mPlayerShipImage = BitmapFactory.decodeResource(res, R.drawable.playership);
            mChaserImage = BitmapFactory.decodeResource(res, R.drawable.chasers);
            mRandomerImage = BitmapFactory.decodeResource(res, R.drawable.randomers);
            mSquarerImage = BitmapFactory.decodeResource(res, R.drawable.squarers);
            mBouncerImage = BitmapFactory.decodeResource(res, R.drawable.bouncers);
            mPatrollerUDImage = BitmapFactory.decodeResource(res, R.drawable.patrollerud);
            mPatrollerLRImage = BitmapFactory.decodeResource(res, R.drawable.patrollerlr);
            
            mPowerupFreezeImage = BitmapFactory.decodeResource(res, R.drawable.powerupfreeze);
            mPowerupBombImage = BitmapFactory.decodeResource(res, R.drawable.powerupbomb);
            mPowerupShieldImage = BitmapFactory.decodeResource(res, R.drawable.powerupshield);
            
            mPowerupShieldPaint.setARGB(255, 255, 255, 255);
            mPowerupShieldPaint.setStyle(Paint.Style.STROKE);
            mPowerupShieldPaint.setAntiAlias(true);
            mPowerupShieldPaint.setStrokeWidth(3);
            
            mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.gametypescreenbg);
            mBackgroundGrid = BitmapFactory.decodeResource(res, R.drawable.background);
            mPausedBitmap = BitmapFactory.decodeResource(res, R.drawable.paused);
            
            //Instantiate holder objects
            mCurrentEnemies = new ArrayList<Enemy>();
            mShots = new Shot[15];
		}
		

		public void setRunning(boolean b) {
			mRunning = b;
		}
		
		public void pause() {
			synchronized (mSurfaceHolder) {
                if (mState == Frontiers.GAMESTATE_RUNNING) {
                	mState = Frontiers.GAMESTATE_PAUSE;
                }
            }
		}
		
		public void unPause() {
			synchronized (mSurfaceHolder) {
                if (mState == Frontiers.GAMESTATE_PAUSE) {
                	mState = Frontiers.GAMESTATE_RUNNING;
                	mLastTime = System.currentTimeMillis();
                }
            }
		}
        
        public void setGametype(int type) {
            synchronized (mSurfaceHolder) {
                mGametype = type;
            }
        }
        
        public void setGamePointer(Game game) {
        	mGamePointer = game;
        }
        
        @Override
        public void run() {
            while (mRunning) {
            	
                Canvas c = null;
                try {
                	synchronized(TouchEventMutex) {
                		TouchEventMutex.notify();
                	}
                	Thread.yield();
                	
                    c = mSurfaceHolder.lockCanvas();
                    synchronized (mSurfaceHolder) {
                        if (mState == Frontiers.GAMESTATE_RUNNING) {

                        	frameStarted();
                        	
                        	//Everything is ready to be drawn
                        	doDraw(c);
                        	updateState();
                        }
                        else if (mState == Frontiers.GAMESTATE_PAUSE){
                        	drawPaused(c);
                        }
                        
                    }
                }
                catch (Exception e) {
                	Log.e("Exception", e.getStackTrace().toString());
                } 
                finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
        
        private void updateState() {
			if (mGame.mGameOver == true && mGameOverTimer <= 0.0f){
				this.mState = Frontiers.GAMESTATE_GAMEOVER;
				this.mBackgroundImage = null;
				
				if (mGame.mType == Frontiers.GAMETYPE_COUNTDOWN || mGame.mType == Frontiers.GAMETYPE_SURVIVAL) {
					Frontiers.LastScore = mGame.mScore;
					Frontiers.GameOverMessage = "You scored " + Frontiers.LastScore + " points";
				}
				else if (mGame.mType == Frontiers.GAMETYPE_ELIMINATION){
					Frontiers.LastScore = mGame.mSeconds;
					Frontiers.GameOverMessage = "You mopped them up in " + Frontiers.LastScore + " seconds";
				}
				else if (mGame.mType == Frontiers.GAMETYPE_UNARMED){
					Frontiers.LastScore = mGame.mSeconds;
					Frontiers.GameOverMessage = "You survived for " + Frontiers.LastScore + " seconds";
				}
				
				this.pause();
	        	this.mBackgroundImage = null;
	        	Intent gameIntent = new Intent(mGamePointer, MainMenu.class);
	        	mGamePointer.startActivity(gameIntent);
	        	mGamePointer.finish();
			}
		}
        
        private void drawPaused(Canvas c) {
        	c.drawBitmap(mPausedBitmap, (mCanvasWidth / 2) - (mPausedBitmap.getWidth() / 2), (mCanvasHeight / 2) - (mPausedBitmap.getHeight() / 2), null);
        }

		private void doDraw(Canvas c) {
			c.drawColor(0, PorterDuff.Mode.CLEAR);
        	mWorld.draw(c); //Draw background

        	//Draw player's shots
        	for (int i = 0; i < pShip.mShots.length; i++) {
            	pShip.mShots[i].draw(c);
            }

        	c.save();
        	c.rotate(pShip.mOrientation, pShip.mScreenPosX + (pShip.mWidth / 2), pShip.mScreenPosY + (pShip.mHeight / 2));
			pShip.draw(c);
			c.restore();
			if (mGame.mPShipHasShield) {
				c.drawCircle(pShip.mScreenPosX + (pShip.mWidth / 2), pShip.mScreenPosY + (pShip.mHeight / 2), (pShip.mHeight / 2) + 5, mPowerupShieldPaint);
			}
			
			//Draw all enemies
			for (int i = 0; i < mGame.mCurrentEnemies.size(); i++) {
				mGame.mCurrentEnemies.get(i).updateScreenPosition();
				mGame.mCurrentEnemies.get(i).draw(c);	
			}
			
			//Draw UI Last (On Top)
			mMoveControl.draw(c);
			mFireControl.draw(c);
			mGame.draw(c);
		}

		/**
         * Starts the game, sets parameters for the current game type.
         */
        public void doStart() {
        	if (mGametype == -1) {
        		Log.e("Gametype", "Gametype was passed -1");
        		try {
					throw new Exception();
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        	mGameOverTimer = 2.5f;
        	mWorld = new Gameworld(mBackgroundImage, mBackgroundGrid);
        	mGame = new Gametype(mGametype, 25,
        			mChaserImage, mRandomerImage, mSquarerImage, mBouncerImage, mPatrollerUDImage, mPatrollerLRImage,
        			mPowerupBombImage, mPowerupFreezeImage, mPowerupShieldImage);
        	mMoveControl = new TouchCircleController(true);
        	mFireControl = new TouchCircleController(false);
        	
        	pShip = new PlayerShip(mWorld.mWorldCenterX, mWorld.mWorldCenterY, mPlayerShipImage);
        	
        	mLastTime = System.currentTimeMillis() + 100;
        	mState = Frontiers.GAMESTATE_RUNNING;
        }
        
        /**
         * Handles pretty much all of the game play 
         * Optimized to reduce object allocation where possible, but it's ugly
         */
        private void frameStarted() {
        	//Calculate frame time
            long now = System.currentTimeMillis();
            if (mLastTime > now) return;
            mTimeSinceLastFrame = (now - mLastTime) / 1000.0f;
            mLastTime = now;
            
            //Handle player respawning
            if (pRespawnTimer > 0.0f) {
            	pRespawnTimer -= mTimeSinceLastFrame;
            }
            else if (pShip.mState != Frontiers.MOVABLESTATE_ALIVE && pRespawnTimer <= 0.0f && !mGame.mGameOver) {
            	pShip.spawn();
            	SoundManager.playSound(Frontiers.SOUND_PLAYERSPAWN);
            	if (pShip.mState == Frontiers.MOVABLESTATE_SPAWNING) {
            		pShip.doSpawnAnimation(mTimeSinceLastFrame);
            	}
            	
            	if (pShip.mState == Frontiers.MOVABLESTATE_ALIVE) {
            		mGame.mWaveTimer = 0.0f;
                	pRespawnTimer = 0;
            	}	
            }
            
            //Handle shooting action
            if (mGame.mType != Frontiers.GAMETYPE_UNARMED) {
	            if (mFireControl.mVisible) {
	            	float[] d = mFireControl.getFireDirection();
	            	pShootDirectionX = d[0];
	            	pShootDirectionY = d[1];
	            }
	            
	            if ((pShootDirectionX != 0 || pShootDirectionY != 0) && pShootTimer <= 0.0f &&
	            	pShip.mState == Frontiers.MOVABLESTATE_ALIVE) {
	            	pShootTimer = pShip.mFireRate;
	            	pShip.shoot(pShootDirectionX, pShootDirectionY);
	            	SoundManager.playSound(Frontiers.SOUND_SHOOT);
	            }
	            else if (pShootTimer >= 0.0f) {
	            	pShootTimer -= mTimeSinceLastFrame;
	            }
            }

            //Move Player and scroll map
            if (mMoveControl.mVisible && pShip.mState == Frontiers.MOVABLESTATE_ALIVE) {
	            pShipMovement = mMoveControl.getMoveDirection();
	            if (pShipMovement[0] != 0.0f || pShipMovement[1] != 0.0f) {
	            	pShip.mOrientation = pShipMovement[2];
	            	pShip.move(pShipMovement[0], pShipMovement[1], mTimeSinceLastFrame);
	            	mWorld.scroll(pShip.mScreenPosX, pShip.mScreenPosY, pShip.mWidth, pShip.mHeight);  
	            }
            }
            
            //Do ArrayList lookups;
        	mCurrentEnemies = mGame.mCurrentEnemies;
        	mCurrentEnemyCount = mCurrentEnemies.size();
        	mShots = pShip.mShots;
        	mShotCount = pShip.mShots.length;
            
            // Process enemy AI, spawning, dying animations
            for (int i = 0; i < mCurrentEnemyCount; i++) {
            	mCurrentEnemyHolder = mCurrentEnemies.get(i);
            	
            	if (mCurrentEnemyHolder.mState == Frontiers.MOVABLESTATE_ALIVE && !mGame.mIsFrozen) {
            		mCurrentEnemyHolder.moveTowardDestination(mTimeSinceLastFrame, pShip.mWorldPositionX, pShip.mWorldPositionY);
            	}
            	else if (mCurrentEnemyHolder.mState == Frontiers.MOVABLESTATE_SPAWNING) {
            		mCurrentEnemyHolder.doSpawnAnimation(mTimeSinceLastFrame);
            	}
            	else if (mCurrentEnemyHolder.mState == Frontiers.MOVABLESTATE_DYING) {
            		mCurrentEnemyHolder.doDeathAnimation(mTimeSinceLastFrame);
            		
            		if (mCurrentEnemyHolder.mState == Frontiers.MOVABLESTATE_DEAD) {
            			mGame.mDeadEnemies.add(mCurrentEnemyHolder);
            		}
            	}
            } 
            
            // Move shots + remove used ones from last frame
            for (int i = 0; i < mShotCount; i++) {
            	mShotHolder = mShots[i];
            	if (mShotHolder.mVisible == true) {
            		mShotHolder.move(mTimeSinceLastFrame);
            	}
            }
            
            //Check shot collisions with living enemies
            boolean enemyKilled = false;
            for (int i = 0; i < mShotCount; i++) {
            	if (mShots[i].mVisible) {
	            	mShotHolder = mShots[i];
	            	for (int j = 0; j < mCurrentEnemyCount; j++) {
	            		mCurrentEnemyHolder = mCurrentEnemies.get(j);
	            		if (mCurrentEnemyHolder.mState == Frontiers.MOVABLESTATE_ALIVE && Rect.intersects(mShotHolder.getBounds(), mCurrentEnemyHolder.mBounds)) {
	            			mCurrentEnemyHolder.mState = Frontiers.MOVABLESTATE_DYING;
	
	            			mShotHolder.mVisible = false;
	            			mGame.onEnemyKilled(mCurrentEnemyHolder.mWorldPositionX, mCurrentEnemyHolder.mWorldPositionY, mCurrentEnemyHolder.mScore);
	            			enemyKilled = true;
	            			break;
	            		}
	            	}
            	}
            }
            
            if (enemyKilled) {
            	SoundManager.playSound(Frontiers.SOUND_DEATH);
            }
            
            //Remove dead enemies
            for (int i = 0; i < mGame.mDeadEnemies.size(); i++) {
            	mGame.mCurrentEnemies.remove(mGame.mDeadEnemies.get(i));
            }
            mCurrentEnemies = mGame.mCurrentEnemies;
            mCurrentEnemyCount = mGame.mCurrentEnemies.size();
            
            //Check for player collision with powerup
            for (int i = 0; i < mGame.mPowerups.length; i++) {
            	if (mGame.mPowerups[i].mState == Frontiers.MOVABLESTATE_ALIVE && Rect.intersects(pShip.mBounds, mGame.mPowerups[i].mBounds)) {
            		mGame.onPowerup(i);
            		mGame.mPowerups[i].get();
            	}            	
            }
            
            //Check for player collision with living enemy (i.e. death)
            boolean moveWarning = false;
            boolean fireWarning = false;
            for (int i = 0; i < mCurrentEnemyCount; i++) {
            	mCurrentEnemyHolder = mCurrentEnemies.get(i);
            	if (mCurrentEnemyHolder.mState == Frontiers.MOVABLESTATE_ALIVE && Rect.intersects(pShip.mBounds, mCurrentEnemyHolder.mBounds)) {
            		if (mGame.mPShipHasShield) {
            			mCurrentEnemyHolder.mState = Frontiers.MOVABLESTATE_DYING;
            			
            			mGame.onEnemyKilled(mCurrentEnemyHolder.mWorldPositionX, mCurrentEnemyHolder.mWorldPositionY, mCurrentEnemyHolder.mScore);
            			enemyKilled = true;
            		}
            		else {
                		mGame.onEnemyKilled(mCurrentEnemyHolder.mScreenPosX, mCurrentEnemyHolder.mScreenPosY, mCurrentEnemyHolder.mScore);
                		pShip.death();
                		mGame.onPlayerDeath();
                		mMoveControl.goInvisible();
                		mFireControl.goInvisible();
                		
                		//If it's a gametype where you can respawn, reset scroll
                		if (mGame.mType == Frontiers.GAMETYPE_COUNTDOWN || mGame.mType == Frontiers.GAMETYPE_ELIMINATION) {
                			mWorld.resetScroll();
                		}
                		pRespawnTimer = 2.0f;
                		pShootTimer = 0;
                		pShootDirectionX = 0;
                		pShootDirectionY = 0;
                		SoundManager.playSound(Frontiers.SOUND_PLAYERDEATH);	
            		}
            		break;
            	}
            	else if (moveWarning == false && mCurrentEnemyHolder.mState == Frontiers.MOVABLESTATE_ALIVE && Rect.intersects(mMoveControl.getBounds(), mCurrentEnemyHolder.mBounds)) {
            		moveWarning = true;
            	}
            	else if (fireWarning == false && mCurrentEnemyHolder.mState == Frontiers.MOVABLESTATE_ALIVE && Rect.intersects(mMoveControl.getBounds(), mCurrentEnemyHolder.mBounds)) {
            		fireWarning = true;
            	}
            } 
            mMoveControl.mContainsEnemies = moveWarning;
            mFireControl.mContainsEnemies = fireWarning;
            
            //Update game
            if (!mGame.mGameOver) {
            	if (((mCurrentEnemyCount <= mGame.mEnemyThreshold && mGame.mCurrentWave != 1) || mGame.mWaveTimer <= 0.0f) && pShip.mState == Frontiers.MOVABLESTATE_ALIVE) {
            		mGame.advance(pShip.mWorldPositionX, pShip.mWorldPositionY);
            		SoundManager.playSound(Frontiers.SOUND_ENEMYSPAWN);
            		mGame.mWaveTimer = 8.0f;
            	}
            	else {
            		mGame.mWaveTimer -= mTimeSinceLastFrame;
            	}
            	
            	mGame.FrameStarted(mTimeSinceLastFrame);
	            //Update game timer
	            if (mGameTimer <= 0.0f){
	            	mGame.clockTick();
	            	mGameTimer = 1.0f;
	            }
	            else {
	            	mGameTimer -= mTimeSinceLastFrame;
	            }
            }
            else {
            	mGameOverTimer -= mTimeSinceLastFrame;
            }
        }

		//Input Handlers	
		public boolean doKeyDown(int keyCode, KeyEvent msg) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				if (mState == Frontiers.GAMESTATE_RUNNING) {
					this.pause();
				}
				else if (mState == Frontiers.GAMESTATE_PAUSE) {
					this.unPause();
				}
				break;
				case KeyEvent.KEYCODE_DPAD_UP:
					pShootDirectionX = 0;
					pShootDirectionY = -1;
					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					pShootDirectionX = 0;
					pShootDirectionY = 1;
					break;
				case KeyEvent.KEYCODE_DPAD_LEFT:
					pShootDirectionX = -1;
					pShootDirectionY = 0;
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					pShootDirectionX = 1;
					pShootDirectionY = 0;
					break;
				case KeyEvent.KEYCODE_VOLUME_UP :
					SoundManager.mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
		                                             AudioManager.ADJUST_RAISE, 
		                                             AudioManager.FLAG_SHOW_UI);
		            break;
		        case KeyEvent.KEYCODE_VOLUME_DOWN:
		        	SoundManager.mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, 
		                                             AudioManager.ADJUST_LOWER, 
		                                             AudioManager.FLAG_SHOW_UI);
		            break;
				default: 
					break;
			}
			return false;
		}

		public boolean doKeyUp(int keyCode, KeyEvent msg) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				pShootDirectionX = 0;
				pShootDirectionY = 0;
				break;
			case KeyEvent.KEYCODE_VOLUME_UP :
				SoundManager.mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, 
	                                             AudioManager.ADJUST_SAME, 
	                                             AudioManager.FLAG_SHOW_UI);
	            break;
	        case KeyEvent.KEYCODE_VOLUME_DOWN:
	        	SoundManager.mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, 
	                                             AudioManager.ADJUST_SAME, 
	                                             AudioManager.FLAG_SHOW_UI);
	            break;
			}
				
			return true;
		}

		public boolean doTouchEvent(MotionEvent evt) {
			if (!Frontiers.MultiTouchSupported) {	
				switch (evt.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mMoveControl.mCenterX = evt.getX();
					mMoveControl.mCenterY = evt.getY();
					mMoveControl.goVisible();
				case MotionEvent.ACTION_MOVE:
					mMoveControl.mTouchX = evt.getX();
					mMoveControl.mTouchY = evt.getY();					
					return true;
					
				case MotionEvent.ACTION_UP:
					mMoveControl.goInvisible();
					return true;

				default:
					break;
				}
			}
			else { //Handle Multitouch
				
				if (Reflect.getPointerCount(evt) == 2) {
					// Get finger ids
					int finger1Id = Reflect.getPointerId(evt, 0);
					int finger2Id = Reflect.getPointerId(evt, 1);
			    			    	
			   		// Collect information about finger 1
		    		if(Reflect.getX(evt, finger1Id) < Reflect.getX(evt, finger2Id)) {
		    			//Pointer 1 is Move Control
		    			mMoveTouchIndex = finger1Id;
		    			mFireTouchIndex = finger2Id;
		    		}
		    		else {
		    			//Pointer 1 is Fire Control
		    			mFireTouchIndex = finger1Id;
		    			mMoveTouchIndex = finger2Id;
		    		}	
		    		
		    		if (!mMoveControl.mVisible)
		    		{
			    		mMoveControl.mCenterX = Reflect.getX(evt, mMoveTouchIndex);
						mMoveControl.mCenterY = Reflect.getY(evt, mMoveTouchIndex);	
						mMoveControl.goVisible();
		    		}
		    		    		
		    		if (!mFireControl.mVisible)
		    		{
						mFireControl.mCenterX = Reflect.getX(evt, mFireTouchIndex);
						mFireControl.mCenterY = Reflect.getY(evt, mFireTouchIndex);
						mFireControl.goVisible();
		    		}
		
		    		int action = Reflect.getAction(evt);
		    		switch (action) {

					case MotionEvent.ACTION_MOVE:
						mMoveControl.mTouchX = Reflect.getX(evt, mMoveTouchIndex);
						mMoveControl.mTouchY = Reflect.getY(evt, mMoveTouchIndex);
					
						mFireControl.mTouchX = Reflect.getX(evt, mFireTouchIndex);
						mFireControl.mTouchY = Reflect.getY(evt, mFireTouchIndex);	
						break;
					case 0x6: //Pointer 1 up
						if (mMoveTouchIndex == finger1Id) {
							mMoveControl.goInvisible();
						}
						else if (mFireTouchIndex == finger1Id) {
							mFireControl.goInvisible();
							pShootDirectionX = 0;
							pShootDirectionY = 0;
						}
						break;
					case 0x106: //Pointer 2 up
						if (mMoveTouchIndex == finger2Id) {
							mMoveControl.goInvisible();
						}
						else if (mFireTouchIndex == finger2Id) {
							mFireControl.goInvisible();
							pShootDirectionX = 0;
							pShootDirectionY = 0;
						}
						break;
		    		}
			    }
				else if (Reflect.getPointerCount(evt) == 1) {
					switch (Reflect.getAction(evt)) {
					case MotionEvent.ACTION_DOWN:
						if (Reflect.getX(evt, 0) < mCanvasWidth / 2) {
							mMoveControl.mCenterX = Reflect.getX(evt, 0);
							mMoveControl.mCenterY = Reflect.getY(evt, 0);
							mMoveControl.goVisible();
						}
						else {
							mFireControl.mCenterX = Reflect.getX(evt, 0);
							mFireControl.mCenterY = Reflect.getY(evt, 0);
							mFireControl.goVisible();
						}
						break;
					case MotionEvent.ACTION_MOVE:
						if (Reflect.getX(evt, 0) < mCanvasWidth / 2) {
							mMoveControl.mTouchX = Reflect.getX(evt, 0);
							mMoveControl.mTouchY = Reflect.getY(evt, 0);
						}
						else {
							mFireControl.mTouchX = Reflect.getX(evt, 0);
							mFireControl.mTouchY = Reflect.getY(evt, 0);
						}
						break;
					case MotionEvent.ACTION_UP:
						mMoveControl.goInvisible();
						mFireControl.goInvisible();
						pShootDirectionX = 0;
						pShootDirectionY = 0;
						break;
					}
				}
			}
			return true;
		}
		
		public boolean doTrackballEvent(MotionEvent evt) {
			if (evt.getAction() == MotionEvent.ACTION_MOVE) {
				float x = evt.getX();
				float y = evt.getY();
	
				float mag = x * x + y * y;
				mag = FloatMath.sqrt(mag);
				if (mag == 0) {
					mag = 0.1f;
				}
				
				pShootDirectionX = x / mag;
				pShootDirectionY = y / mag;
			}

			return true;
		} 

		/* Callback invoked when the surface dimensions change. */
		public void setSurfaceSize(int width, int height) {
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;
                
                Frontiers.SCREEN_WIDTH = width;
                Frontiers.SCREEN_HEIGHT = height;
                Frontiers.SCREEN_CENTER_X = width / 2;
                Frontiers.SCREEN_CENTER_Y = height / 2;
                
                if (width >= 800) {
                	Frontiers.ScreenSize = Frontiers.SCREENSIZE_LARGE;
                }
                else if (width <= 320) {
                	Frontiers.ScreenSize = Frontiers.SCREENSIZE_SMALL;
                }
                
                if (mBackgroundImage.getWidth() < (width * 1.5) || mBackgroundImage.getHeight() < (height * 1.5)) {
                	int bWidth = mBackgroundImage.getWidth();
                	int bHeight = mBackgroundImage.getHeight();
                	int newWidth = (int) (width * 2);
                	int newHeight = (int) (height * 2);
                	
                	float scaleWidth = ((float) newWidth) / bWidth;
                    float scaleHeight = ((float) newHeight) / bHeight;
                    
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    mBackgroundImage = Bitmap.createBitmap(mBackgroundImage, 0, 0, bWidth, bHeight, matrix, true);
                }

                mState = Frontiers.GAMESTATE_READY;
                doStart();
            }
		}

    };
    
    /** The thread that actually draws the animation */
    private MainThread thread;
    
    /** Mutex object for touch thread */
    private Object TouchEventMutex = new Object();
    
    public MainView(Context context, AttributeSet attrs) {
		super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new MainThread(holder, context, new Handler() {
        	@Override
            public void handleMessage(Message m) {
        		
            }
        });

        // make sure we get key events
        setFocusableInTouchMode(true);
        setFocusable(true); 
	}
 
    
    /**
     * Fetches the animation thread corresponding to this MainView.
     * 
     * @return the animation thread
     */
    public MainThread getThread() {
        return thread;
    }
    
    /** 
     * Pass Gametype information along to the thread
     * @param type
     */
    public void setGametype(int type) {
    	thread.setGametype(type);
    }
 
    
    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        return thread.doKeyDown(keyCode, msg);
    }

    
    /**
     * Standard override for key-up.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        return thread.doKeyUp(keyCode, msg);
    }
    
    /** Standard override for trackball events
     * 
     */
    @Override
    public boolean onTrackballEvent(MotionEvent evt) {
    	return thread.doTrackballEvent(evt);
    } 
    
    /**
     * Standard override for touch events
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent evt) {
    	thread.doTouchEvent(evt);
    	
    	synchronized(TouchEventMutex) {
    		try {
    			TouchEventMutex.wait(1000L);
    		}
    		catch (InterruptedException e) {}
    	}
    	return true;
    }

    
    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) {
        	thread.pause();
        	
        	if (thread.mState != Frontiers.GAMESTATE_GAMEOVER) {
	        	thread.mBackgroundImage = null;
	        	Intent gameIntent = new Intent(thread.mGamePointer, MainMenu.class);
	        	thread.mGamePointer.startActivity(gameIntent);
	        	thread.mGamePointer.finish();
        	}
        }
    }

    
    /* Callback invoked when the surface dimensions change. */
	public void surfaceChanged(SurfaceHolder arg0, int format, int width, int height) {
		thread.setSurfaceSize(width, height);
	}

	
	/*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
	public void surfaceCreated(SurfaceHolder holder) {
		// Start the thread here so that we don't busy-wait in run()
        // Waiting for the surface to be created
		if (thread.mState != Frontiers.GAMESTATE_PAUSE) {
			thread.setRunning(true);
    		thread.start();
		}
	}

	
    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Tell thread to shut down & wait for it to finish
        boolean retry = true;
        thread.setRunning(false);
        
        while (retry) {
            try {
                thread.join();
                retry = false;
            } 
            catch (InterruptedException e) {
            }
        }
	}
}

