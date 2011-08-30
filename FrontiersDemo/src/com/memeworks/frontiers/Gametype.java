package com.memeworks.frontiers;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Gametype {
	
	public int mType;
	
	public int mCurrentWave = 0;
	public int mEnemyThreshold = 0;
	public float mWaveTimer = 8.0f;
	
	public ArrayList<Enemy> mCurrentEnemies;
	public ArrayList<Enemy>	mDeadEnemies;
	
	public Powerup[] mPowerups = new Powerup[10];
	public int mCurrentPowerupIndex = 0;

	public boolean mIsFrozen = false;
	public final float FREEZE_TIMER_RESET = 4.0f;
	public float mFreezeTimer = 4.0f;
	
	public boolean mPShipHasShield = false;
	public final float SHIELD_TIMER_RESET = 4.0f;
	private float mShieldTimer = 4.0f;
	
	public String mGametypeString = "";
	public String mScoreString = "";
	public int mScore = 0;
	public int mSeconds = 0;
	public boolean mGameOver = false;
	
	private final char c[] = new char[100];
	private final StringBuilder sb = new StringBuilder(100);
	private final int INITIAL_ELIMINATION_COUNT = 50;
	
	/** Location for gametext */
	private int mTextPosX;
	private int mTextPosY;
	
	public FloatText[] Texts = new FloatText[50];
	
	/** Enemy drawables */
	private Bitmap mChaserImage;
    private Bitmap mRandomerImage;
    private Bitmap mSquarerImage;
    private Bitmap mBouncerImage;
    private Bitmap mPatrollerUDImage;
    private Bitmap mPatrollerLRImage;

	private Paint mGameTextPaint;
	private Paint mGameTextOutlinePaint;
	private Random rand = new Random();
	private Rect mValidArea;

	private int Current_Text_Index = 0;
	private CircleTimer mCombatMultiplier;
	private CircleTimer mFreezeCircleTimer;
	private CircleTimer mShieldCircleTimer;

	
	
	public Gametype(int type, int textPosY, 
			Bitmap chaserImage, Bitmap randomerImage, Bitmap squarerImage, 
			Bitmap bouncerImage, Bitmap patrollerUDImage, Bitmap patrollerLRImage,
			Bitmap powerupBombImage, Bitmap powerupFreezeImage, Bitmap powerupShieldImage) {
		
		mCombatMultiplier = new CircleTimer(30, 30, true, 500, 100);
		mFreezeCircleTimer = new CircleTimer(Frontiers.SCREEN_WIDTH - 35, 30, false, 300, 100);
		mFreezeCircleTimer.image = powerupFreezeImage;
		
		mShieldCircleTimer = new CircleTimer(Frontiers.SCREEN_WIDTH - 35, 30, false, 300, 100);
		mShieldCircleTimer.image = powerupShieldImage;
		
		mGameTextPaint = new Paint();
		mGameTextPaint.setTextSize(15);
		mGameTextPaint.setTypeface(Frontiers.FONT);
		mGameTextPaint.setTextAlign(Paint.Align.CENTER);
		mGameTextPaint.setAntiAlias(true);
		mGameTextPaint.setARGB(255, 255, 255, 255);
		
		mGameTextOutlinePaint = new Paint();
		mGameTextOutlinePaint.setTextSize(15);
		mGameTextOutlinePaint.setTypeface(Frontiers.FONT);
		mGameTextOutlinePaint.setTextAlign(Paint.Align.CENTER);
		mGameTextOutlinePaint.setAntiAlias(true);
		mGameTextOutlinePaint.setARGB(255, 255, 102, 0);
		mGameTextOutlinePaint.setStyle(Paint.Style.STROKE);
		mGameTextOutlinePaint.setStrokeWidth(4);
		
		for (int i = 0; i < Texts.length; i++)
		{
			Texts[i] = new FloatText();
		}
		
		mTextPosX = Frontiers.SCREEN_CENTER_X;
		mTextPosY = textPosY;
		
		mCurrentEnemies = new ArrayList<Enemy>();
		mDeadEnemies = new ArrayList<Enemy>();
		
		mChaserImage = chaserImage;
        mRandomerImage = randomerImage;
        mSquarerImage = squarerImage;
        mBouncerImage = bouncerImage;
        mPatrollerUDImage = patrollerUDImage;
        mPatrollerLRImage = patrollerLRImage;

    	for (int i = 0; i < mPowerups.length; i++)
    	{
    		mPowerups[i] = new Powerup(Frontiers.POWER_UP_BOMB, 0, 0, powerupBombImage, powerupFreezeImage, powerupShieldImage);
    	}
    	
        mValidArea = new Rect(0,0, Gameworld.mPlayArea.width() - 50, Gameworld.mPlayArea.height() - 50);
        
		setType(type);
	}

	private void setType(int type) {
		mType = type;
		
		switch(type){
		case Frontiers.GAMETYPE_COUNTDOWN:
			mGametypeString = Frontiers.GAMESTRING_COUNTDOWN;
			mSeconds = 180;
			mEnemyThreshold = 3;
			mScoreString = " Score: ";
			break;
			
		case Frontiers.GAMETYPE_SURVIVAL:
			mGametypeString = Frontiers.GAMESTRING_SURVIVAL;
			mSeconds = 0;
			mEnemyThreshold = 3;
			mScoreString = " Score: ";
			break;
			
		case Frontiers.GAMETYPE_ELIMINATION:
			mGametypeString = Frontiers.GAMESTRING_ELIMINATION;
			mSeconds = 0;
			mEnemyThreshold = 3;
			mScoreString = " Remaining: ";
			mScore = INITIAL_ELIMINATION_COUNT;
			break;
			
		case Frontiers.GAMETYPE_UNARMED:
			mGametypeString = Frontiers.GAMESTRING_UNARMED;
			mSeconds = 0;
			mEnemyThreshold = 2;
			mScoreString = " NO WEAPONS!";
			break;
		}
	}
	
	/**
	 * Draws text with 0 allocations
	 */
	public void draw(Canvas canvas) {
		
		//Draw main game text
		sb.getChars(0, sb.length(), c, 0);
		canvas.drawText(c, 0, sb.length(), mTextPosX, mTextPosY, mGameTextOutlinePaint);
		canvas.drawText(c, 0, sb.length(), mTextPosX, mTextPosY, mGameTextPaint);
		
		//Draw floating texts
		for (int i = 0; i < Texts.length; i++)
		{
			if (Texts[i].Visible)
			{
				Texts[i].Draw(canvas);
			}
		}
		
		for (int i = 0; i < mPowerups.length; i++)
		{
			mPowerups[i].updateScreenPosition();
			mPowerups[i].draw(canvas);
		}
		
		int freeze_offset = 0;
		int shield_offset = 0;
		if (mShieldCircleTimer.current_time > 0)
		{
			freeze_offset = 60;
		}
		else if (mFreezeCircleTimer.current_time > 0)
		{
			shield_offset = 60;
		}
		
		mCombatMultiplier.Draw(canvas, 0);
		mFreezeCircleTimer.Draw(canvas, freeze_offset);
		mShieldCircleTimer.Draw(canvas, shield_offset);
	}
	
	public void FrameStarted(float frame_time)
	{
		//Update float texts
		for (int i = 0; i < Texts.length; i++)
		{
			if (Texts[i].Visible)
			{
				Texts[i].Frame_Started(frame_time);
			}
		}
		
		//Update freeze powerup status
		if (mIsFrozen) {
			mFreezeTimer -= frame_time;
			
			if (mFreezeTimer <= 0) {
				mFreezeTimer = FREEZE_TIMER_RESET;
				mIsFrozen = false;
				mFreezeCircleTimer.reset();
			}
		}
		
		if (mPShipHasShield) {
			mShieldTimer -= frame_time;
			
			if (mShieldTimer <= 0) {
				mShieldTimer = FREEZE_TIMER_RESET;
				mPShipHasShield = false;
				mShieldCircleTimer.reset();
			}			
		}

		mCombatMultiplier.Frame_Started(frame_time);
		mFreezeCircleTimer.Frame_Started(frame_time);
		mShieldCircleTimer.Frame_Started(frame_time);
	}
	
	public void clockTick() {
		switch(mType) {
			case Frontiers.GAMETYPE_COUNTDOWN:
				mSeconds--;
				if (mSeconds == 0) {
					endGame();
				}
				break;
			case Frontiers.GAMETYPE_SURVIVAL:
			case Frontiers.GAMETYPE_UNARMED:
			case Frontiers.GAMETYPE_ELIMINATION:
				mSeconds++;
				break;
		}
		
		int minutes = mSeconds / 60;
		int seconds = mSeconds % 60;
		
		sb.setLength(0);
		sb.append(mGametypeString);
		sb.append(minutes);
		
		if (seconds < 10) {
			sb.append(":0");
		}
		else {
			sb.append(":");
		}
		sb.append(seconds);
		sb.append(mScoreString);
		
		if (mType != Frontiers.GAMETYPE_UNARMED){
			sb.append(mScore);
		}
	}
	
	public void advance(int playerX, int playerY) {
		if (!mGameOver) {
			if (mType != Frontiers.GAMETYPE_UNARMED) {
				for (int i = 0; i < mCurrentWave * 1.5 + 3; i++) {
					spawnRandomEnemy(playerX, playerY);
				}
				
				if (mCurrentWave > 3) {
					for (int i = 0; i < (mCurrentWave * 1.5) / 3; i++) {
						spawnChaser();
					}
				}
			}
			else {
				for (int i = 0; i < mCurrentWave + 1; i++) {
					spawnChaser();
				}
			}
			mCurrentWave++;
		}
	}
	
	public void onEnemyKilled(int worldX, int worldY, int points) {
		switch (mType) {
		case Frontiers.GAMETYPE_SURVIVAL:
		case Frontiers.GAMETYPE_COUNTDOWN:
			mScore += points;
			Current_Text_Index ++;
			
			if (Current_Text_Index >= Texts.length) {
				Current_Text_Index = 0;
			}
			
			Texts[Current_Text_Index].Show(worldX, worldY, "+" + points * mCombatMultiplier.Current_Multiplier, 12, 400);
			mCombatMultiplier.add(points);
			
			if (rand.nextInt(100) < 8) {
				mPowerups[mCurrentPowerupIndex].showRandom(worldX, worldY);
				mCurrentPowerupIndex = ++mCurrentPowerupIndex % mPowerups.length;
			}
			break;
		case Frontiers.GAMETYPE_ELIMINATION:
			mScore--;
			if (mScore == 0) {
				endGame();
			}
			break;
		}
	}
	
	/**
	 * Call this when the player dies so the game type can react accordingly
	 */
	public void onPlayerDeath() {
		
		for(int i = 0; i < mCurrentEnemies.size(); i++) {
			mCurrentEnemies.get(i).mState = Frontiers.MOVABLESTATE_DYING;
		}
		
		switch (mType) {
		case Frontiers.GAMETYPE_SURVIVAL:
		case Frontiers.GAMETYPE_UNARMED:
			endGame();
			break;
		case Frontiers.GAMETYPE_COUNTDOWN:
		case Frontiers.GAMETYPE_ELIMINATION:
			mCurrentWave = 0;
			break;
		}
		
		for (int i = 0; i < mPowerups.length; i++)
		{
			mPowerups[i].mState = Frontiers.MOVABLESTATE_DEAD;
		}
		
		mCombatMultiplier.reset();
		mFreezeCircleTimer.reset();
		mShieldCircleTimer.reset();
	}
	
	public void onPowerup(int powerup_index)
	{
		switch (mPowerups[powerup_index].mType)
		{
		case Frontiers.POWER_UP_BOMB:
			if (mType != Frontiers.GAMETYPE_UNARMED) {
				for(int i = 0; i < mCurrentEnemies.size(); i++) {
					mCurrentEnemies.get(i).mState = Frontiers.MOVABLESTATE_DYING;
					onEnemyKilled(mCurrentEnemies.get(i).mWorldPositionX, mCurrentEnemies.get(i).mWorldPositionY, mCurrentEnemies.get(i).mScore);				
				}
			}
			SoundManager.playSound(Frontiers.SOUND_BOMB);
			break;
		case Frontiers.POWER_UP_FREEZE:
			this.mIsFrozen = true;
			this.mFreezeTimer = FREEZE_TIMER_RESET;
			this.mFreezeCircleTimer.reset();
			this.mFreezeCircleTimer.add(mFreezeCircleTimer.TIER);
			SoundManager.playSound(Frontiers.SOUND_FREEZE);
			break;
		case Frontiers.POWER_UP_SHIELD:
			this.mPShipHasShield = true;
			this.mShieldTimer = FREEZE_TIMER_RESET;
			this.mShieldCircleTimer.reset();
			this.mShieldCircleTimer.add(mShieldCircleTimer.TIER);
			SoundManager.playSound(Frontiers.SOUND_SHIELDS);
			break;
		}
		
		Current_Text_Index ++;
		
		if (Current_Text_Index >= Texts.length) {
			Current_Text_Index = 0;
		}
		
		Texts[Current_Text_Index].Show(mPowerups[powerup_index].mWorldPositionX, mPowerups[powerup_index].mWorldPositionY, mPowerups[powerup_index].get_text, 15, 200);
	}
	
	public void endGame() {
		mGameOver = true;
		
		for(int i = 0; i < mCurrentEnemies.size(); i++) {
			mCurrentEnemies.get(i).mState = Frontiers.MOVABLESTATE_DYING;
		}
		
		int minutes = mSeconds / 60;
		int seconds = mSeconds % 60;
		
		sb.setLength(0);
		sb.append(mGametypeString);
		sb.append(minutes);
		
		if (seconds < 10) {
			sb.append(":0");
		}
		else {
			sb.append(":");
		}
		sb.append(seconds);
		sb.append(mScoreString);
		
		if (mType != Frontiers.GAMETYPE_UNARMED){
			sb.append(mScore);
		}
		
		mDeadEnemies.clear();
	}
	
	
	//Spawning
	public void spawnChaser() {
		spawnChaser(rand.nextInt(mValidArea.right), rand.nextInt(mValidArea.bottom));
	}
	public void spawnChaser(int worldX, int worldY) {
		mCurrentEnemies.add(new Enemy(Frontiers.AI_CHASE, worldX, worldY, mChaserImage, 250, null));
	}
	
	public void spawnRandomer(int worldX, int worldY) {
		mCurrentEnemies.add(new Enemy(Frontiers.AI_RANDOM, worldX, worldY, mRandomerImage, 200,
				createMoveList(Frontiers.AI_RANDOM, worldX, worldY)));
	}
	
	public void spawnSquarerCW(int worldX, int worldY) {
		mCurrentEnemies.add(new Enemy(Frontiers.AI_SQUARE_CW, worldX, worldY, mSquarerImage, 150,
				createMoveList(Frontiers.AI_SQUARE_CW, worldX, worldY)));
	}
	
	public void spawnSquarerCCW(int worldX, int worldY) {
		mCurrentEnemies.add(new Enemy(Frontiers.AI_SQUARE_CCW, worldX, worldY, mSquarerImage, 150,
				createMoveList(Frontiers.AI_SQUARE_CCW, worldX, worldY)));
	}
	
	public void spawnBouncerCW(int worldX, int worldY) {
		mCurrentEnemies.add(new Enemy(Frontiers.AI_BOUNCE_CW, worldX, worldY, mBouncerImage, 150,
				createMoveList(Frontiers.AI_BOUNCE_CW, worldX, worldY)));
	}
	
	public void spawnBouncerCCW(int worldX, int worldY) {
		mCurrentEnemies.add(new Enemy(Frontiers.AI_BOUNCE_CCW, worldX, worldY, mBouncerImage, 150,
				createMoveList(Frontiers.AI_BOUNCE_CCW, worldX, worldY)));
	}
	
	public void spawnPatrollerUpDown(int worldX, int worldY) {
		mCurrentEnemies.add(new Enemy(Frontiers.AI_PATROL_UD, worldX, worldY, mPatrollerUDImage, 100,
				createMoveList(Frontiers.AI_PATROL_UD, worldX, worldY)));
	}
	
	public void spawnPatrollerLeftRight(int worldX, int worldY) {
		mCurrentEnemies.add(new Enemy(Frontiers.AI_PATROL_LR, worldX, worldY, mPatrollerLRImage, 100,
				createMoveList(Frontiers.AI_PATROL_LR, worldX, worldY)));
	}
	
	public void spawnRandomEnemy(int playerX, int playerY) {
		int choice = rand.nextInt(7);
		int[] location = getRandomValidLocation(playerX, playerY);
		
		switch (choice) {
		case 0:
			spawnRandomer(location[0], location[1]);
			break;
		case 1:
			spawnSquarerCW(location[0], location[1]);
			break;
		case 2:
			spawnSquarerCCW(location[0], location[1]);
			break;
		case 3:
			spawnBouncerCW(location[0], location[1]);
			break;
		case 4:
			spawnBouncerCCW(location[0], location[1]);
			break;
		case 5:
			spawnPatrollerUpDown(location[0], location[1]);
			break;
		case 6:
			spawnPatrollerLeftRight(location[0], location[1]);
			break;
		}
	}
	
	/** 
	 * Returns a valid spawn location that is not in a 50 unit square radius of a player
	 * e.g. not unfair
	 * @param playerX
	 * @param playerY
	 * @return
	 */
	private int[] getRandomValidLocation(int playerX, int playerY) {
		int[] location = {rand.nextInt(mValidArea.right), rand.nextInt(mValidArea.bottom)};
		
		while (location[0] < playerX + 125 && location[0] > playerX - 75 && 
				location[1] < playerY + 125 && location[1] > playerY - 75) {
			location[0] = rand.nextInt(mValidArea.right);
			location[1] = rand.nextInt(mValidArea.bottom);
		}
		return location;
	}
	
	private ArrayList<int[]> createMoveList(int AIType, int startX, int startY) {
		ArrayList<int[]> list = new ArrayList<int[]>();
		//For Bouncers
		int [] firstPoint;
		
		//For Squarers
		int endX;
		int endY;
		
		switch (AIType) {
		case Frontiers.AI_BOUNCE_CW:
			firstPoint = getRandPosOnNearestWall(startX, startY);
			list.add(new int[]{firstPoint[0], firstPoint[1]});
			
			if (firstPoint[2] == 1) { //First wall was left
				list.add(new int [] { rand.nextInt(mValidArea.right), 1, 2 }); //Add Top
				list.add(new int [] { mValidArea.right, rand.nextInt(mValidArea.bottom)}); //Add Right
				list.add(new int [] { rand.nextInt(mValidArea.right), mValidArea.bottom}); //Add Bottom
			}
			else if (firstPoint[2] == 2) { //First wall was top
				list.add(new int [] { mValidArea.right, rand.nextInt(mValidArea.bottom)}); //Add Right
				list.add(new int [] { rand.nextInt(mValidArea.right), mValidArea.bottom}); //Add Bottom
				list.add(new int [] { 1, rand.nextInt(mValidArea.bottom)}); //Add Left
			}
			else if (firstPoint[2] == 3) { //First wall was right
				list.add(new int [] { rand.nextInt(mValidArea.right), mValidArea.bottom}); //Add Bottom
				list.add(new int [] { 1, rand.nextInt(mValidArea.bottom)}); //Add Left
				list.add(new int [] { rand.nextInt(mValidArea.right), 1, 2 }); //Add Top
			}
			else if (firstPoint[2] == 4) { //First wall was bottom
				list.add(new int [] { 1, rand.nextInt(mValidArea.bottom)}); //Add Left
				list.add(new int [] { rand.nextInt(mValidArea.right), 1, 2 }); //Add Top
				list.add(new int [] { mValidArea.right, rand.nextInt(mValidArea.bottom)}); //Add Right
			}
			break;
			
		case Frontiers.AI_BOUNCE_CCW:
			firstPoint = getRandPosOnNearestWall(startX, startY);
			list.add(new int[]{firstPoint[0], firstPoint[1]});
			
			if (firstPoint[2] == 1) { //First wall was left
				list.add(new int [] { rand.nextInt(mValidArea.right), mValidArea.bottom}); //Add Bottom
				list.add(new int [] { mValidArea.right, rand.nextInt(mValidArea.bottom)}); //Add Right
				list.add(new int [] { rand.nextInt(mValidArea.right), 1, 2 }); //Add Top	
			}
			else if (firstPoint[2] == 2) { //First wall was top
				list.add(new int [] { 1, rand.nextInt(mValidArea.bottom)}); //Add Left
				list.add(new int [] { rand.nextInt(mValidArea.right), mValidArea.bottom}); //Add Bottom
				list.add(new int [] { mValidArea.right, rand.nextInt(mValidArea.bottom)}); //Add Right
			}
			else if (firstPoint[2] == 3) { //First wall was right
				list.add(new int [] { rand.nextInt(mValidArea.right), 1, 2 }); //Add Top
				list.add(new int [] { 1, rand.nextInt(mValidArea.bottom)}); //Add Left
				list.add(new int [] { rand.nextInt(mValidArea.right), mValidArea.bottom}); //Add Bottom
			}
			else if (firstPoint[2] == 4) { //First wall was bottom
				list.add(new int [] { mValidArea.right, rand.nextInt(mValidArea.bottom)}); //Add Right
				list.add(new int [] { rand.nextInt(mValidArea.right), 1, 2 }); //Add Top
				list.add(new int [] { 1, rand.nextInt(mValidArea.bottom)}); //Add Left			
			}
			break;
			
		case Frontiers.AI_PATROL_LR:
			list.add(new int[]{0, startY});
			list.add(new int[]{mValidArea.right, startY});
			break;
			
		case Frontiers.AI_PATROL_UD:
			list.add(new int[]{startX, 0});
			list.add(new int[]{startX, mValidArea.bottom});
			break;
			
		case Frontiers.AI_RANDOM:
			int points = rand.nextInt(9) + 2; //2-10
			
			for (int i = 0; i < points; i++) {
				list.add(new int[] {rand.nextInt(mValidArea.right), rand.nextInt(mValidArea.bottom)});
			}
			break;
			
		case Frontiers.AI_SQUARE_CW:
			//Determine start location
			if (startX < mValidArea.right / 2 && startY < mValidArea.bottom / 2) { //Top-Left Quad	
				endX = rand.nextInt(mValidArea.right - startX) + startX;
				endY = rand.nextInt(mValidArea.bottom - startY) + startY;
				list.add(new int[] { endX, startY}); //Top side
				list.add(new int[] { endX, endY}); //Right side
				list.add(new int[] { startX, endY}); //Bottom side
				list.add(new int[] { startX, startY}); //Left side
			}
			else if (startX >=  mValidArea.right / 2 && startY <= mValidArea.bottom / 2) { //Top-Right Quad
				endX = rand.nextInt(startX);
				endY = rand.nextInt(mValidArea.bottom - startY) + startY;
				list.add(new int[] { startX, endY}); //Right side
				list.add(new int[] { endX, endY}); //Bottom side
				list.add(new int[] { endX, startY}); //Left side
				list.add(new int[] { startX, startY}); //Top side
			}
			else if (startX > mValidArea.right / 2 && startY > mValidArea.bottom / 2) { //Bottom-Right Quad
				endX = rand.nextInt(startX);
				endY = rand.nextInt(startY);
				list.add(new int[] { endX, startY}); //Bottom side
				list.add(new int[] { endX, endY}); //Left side
				list.add(new int[] { startX, endY}); //Top side
				list.add(new int[] { startX, startY}); //Right side
			}
			else { //Bottom-Left Quad
				endX = rand.nextInt(mValidArea.right - startX) + startX;
				endY = rand.nextInt(startY);
				list.add(new int[] { startX, endY}); //Left side
				list.add(new int[] { endX, endY}); //Top side
				list.add(new int[] { endX, startY}); //Right side
				list.add(new int[] { startX, startY}); //Bottom side
			}
			break;
			
		case Frontiers.AI_SQUARE_CCW:	
			//Determine start location
			if (startX < mValidArea.right / 2 && startY < mValidArea.bottom / 2) { //Top-Left Quad	
				endX = rand.nextInt(mValidArea.right - startX) + startX;
				endY = rand.nextInt(mValidArea.bottom - startY) + startY;
				list.add(new int[] { startX, endY}); //Left side
				list.add(new int[] { endX, endY}); //Bottom side
				list.add(new int[] { endX, startY}); //Right side
				list.add(new int[] { startX, startY}); //Top side
			}
			else if (startX >=  mValidArea.right / 2 && startY <= mValidArea.bottom / 2) { //Top-Right Quad
				endX = rand.nextInt(startX);
				endY = rand.nextInt(mValidArea.bottom - startY) + startY;
				list.add(new int[] { endX, startY}); //Top side
				list.add(new int[] { endX, endY}); //Left side
				list.add(new int[] { startX, endY}); //Bottom side
				list.add(new int[] { startX, startY}); //Right side
			}
			else if (startX > mValidArea.right / 2 && startY > mValidArea.bottom / 2) { //Bottom-Right Quad
				endX = rand.nextInt(startX);
				endY = rand.nextInt(startY);
				list.add(new int[] { startX, endY}); //Right side
				list.add(new int[] { endX, endY}); //Top side
				list.add(new int[] { endX, startY}); //Left side
				list.add(new int[] { startX, startY}); //Bottom side
			}
			else { //Bottom-Left Quad
				endX = rand.nextInt(mValidArea.right - startX) + startX;
				endY = rand.nextInt(startY);
				list.add(new int[] { endX, startY}); //Bottom side
				list.add(new int[] { endX, endY}); //Right side
				list.add(new int[] { startX, endY}); //Top side
				list.add(new int[] { startX, startY}); //Left side
			}
			break;
		}
		
		return list;
	}
	
	/**
	 * Helper function. Gets a random X, Y on nearest wall to the x, y entered
	 * Used for both Bouncers
	 * @param x
	 * @param y
	 * @return A valid position to emulate bouncing off a wall + Which wall
	 *  1 = Left, 2 = top, 3 = right, 4 = bottom
	 */
	private int[] getRandPosOnNearestWall(int x, int y) {
		
		int distDown = mValidArea.bottom - y;
		int distRight = mValidArea.right - x;
		
		if (x < distRight && x < y && x < distDown) { //Left
			return new int [] { 1, rand.nextInt(mValidArea.bottom), 1 };
		}
		else if (y < distDown && y < x && y < distRight){ //Up
			return new int [] { rand.nextInt(mValidArea.right), 1, 2 };
		}
		else if (distRight < x && distRight < y && distRight < distDown) { //Right
			return new int [] { mValidArea.right, rand.nextInt(mValidArea.bottom), 3 };
		}
		
		//Else - Down
		return new int [] { rand.nextInt(mValidArea.right), mValidArea.bottom, 4 };		
	}
}
