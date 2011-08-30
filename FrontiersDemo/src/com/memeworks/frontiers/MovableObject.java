package com.memeworks.frontiers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

/**
 * Base class for all movables in the game, should be extended for specific purposes
 * @author glitch
 *
 */
public class MovableObject {
	
	/** Current state */
	public int mState;
	
	/** Type of Object (Player, CircleEnemy, etc) */
	public int mType;
	
	public Bitmap mImage;
	public Paint mColor;
	public Paint mOpacity;
	
	public float mMoveSpeed;
	
	/** Ship orientation, face direction of travel */
	public float mOrientation = 0;
	
	public int mWidth;
	public int mHeight;
	public int mWidthOffset;
	public int mHeightOffset;
	public int mScreenPosX;
	public int mScreenPosY;
	
	//Track Position in the Game World
	public int mWorldPositionX;
	public int mWorldPositionY;
	
	//Explosion
	public int[] burstN = new int[] {-1,0};
	public int[] burstNE = new int[] {0,0};
	public int[] burstE = new int[] {0,0};
	public int[] burstSE = new int[] {0,0};
	public int[] burstS = new int[] {0,0};
	public int[] burstSW = new int[] {0,0};
	public int[] burstW = new int[] {0,0};
	public int[] burstNW = new int[] {0,0};
	
	public Rect mBounds;
	
	public float mDeathTimer = 1.0f;
	public float mSpawnTimer = 0.5f;
	
	private int mSpawnRadius = 0;
	private final int DEATH_LINE_LENGTH = 10;
	
	public MovableObject(int type, int x, int y, Bitmap d)
	{
		mType = type;
		mImage = d;
		
		mColor = new Paint();
		mColor.setStyle(Style.STROKE);
		mColor.setStrokeWidth(3);
		
		mOpacity = new Paint();
		
		switch (type) {
		case Frontiers.AI_BOUNCE_CW:
		case Frontiers.AI_BOUNCE_CCW:
			mColor.setARGB(255, 0, 204, 204); //Teal
			break;
		case Frontiers.AI_CHASE:
			mColor.setARGB(255, 204, 0, 0); //Red
			break;
		case Frontiers.AI_PATROL_LR:
		case Frontiers.AI_PATROL_UD:
			mColor.setARGB(255, 255, 153, 0); //Orange
			break;
		case Frontiers.AI_RANDOM:
			mColor.setARGB(255, 153, 0, 153); //Purple
			break;
		case Frontiers.AI_SQUARE_CW:
		case Frontiers.AI_SQUARE_CCW:
			mColor.setARGB(255, 0, 0, 255); //Blue
			break;
		case Frontiers.AI_PLAYER:
		default:
			mColor.setARGB(255, 255, 255, 255); //White
			break;
		}
		
		mWidth = d.getWidth();
		mHeight = d.getHeight();
		mWidthOffset = (int) (mWidth * 0.05f);
		mHeightOffset = (int) (mHeight * 0.05f);
		
		mSpawnRadius = mWidth / 2;
		if (mHeight > mWidth) {
			mSpawnRadius = mHeight / 2;
		}
		
		mWorldPositionX = x;
		mWorldPositionY = y;
		
		mBounds = new Rect();
		spawn();
	}
	
	public void spawn() {
		this.mState = Frontiers.MOVABLESTATE_SPAWNING;
		this.mOrientation = 0;
		this.mOpacity.setAlpha(0);
		
		this.updateScreenPosition();
	}
	
	public void doSpawnAnimation(float timeSinceLastFrame) {
		this.mSpawnTimer -= timeSinceLastFrame;
		this.mOpacity.setAlpha(this.mOpacity.getAlpha() + (int)(512 * timeSinceLastFrame));

		if (mSpawnTimer <= 0) {
			this.mState = Frontiers.MOVABLESTATE_ALIVE;
			this.mSpawnTimer = 0.5f;
		}
	}
	
	public void doDeathAnimation(float timeSinceLastFrame) {
		mDeathTimer -= timeSinceLastFrame;
		
		if (mDeathTimer <= 0) {
			this.mState = Frontiers.MOVABLESTATE_DEAD;
		}
		else {
			int mOffset = (int) ((1 - mDeathTimer) * 300);

			updateScreenPosition();
			
			burstN[0] = mScreenPosX + mWidth / 2;
			burstN[1] = mScreenPosY - mOffset + mHeight / 2;
			
			burstNE[0] = mScreenPosX + mOffset + mWidth / 2;
			burstNE[1] = mScreenPosY - mOffset + mHeight / 2;
			
			burstE[0] = mScreenPosX + mOffset + mWidth / 2;
			burstE[1] = mScreenPosY + mHeight / 2;
			
			burstSE[0] = mScreenPosX + mOffset + mWidth / 2;
			burstSE[1] = mScreenPosY + mOffset + mHeight / 2;
			
			burstS[0] = mScreenPosX + mWidth / 2;
			burstS[1] = mScreenPosY + mOffset + mHeight / 2;
			
			burstSW[0] = mScreenPosX - mOffset + mWidth / 2;
			burstSW[1] = mScreenPosY + mOffset + mHeight / 2;
			
			burstW[0] = mScreenPosX - mOffset + mWidth / 2;
			burstW[1] = mScreenPosY + mHeight / 2;
			
			burstNW[0] = mScreenPosX - mOffset + mWidth / 2;
			burstNW[1] = mScreenPosY - mOffset + mHeight / 2;
			
			mColor.setAlpha((int)(255 * mDeathTimer));
		}
	}
	
	public void draw(Canvas c) {
		if (mState == Frontiers.MOVABLESTATE_ALIVE) {
			c.drawBitmap(mImage, mScreenPosX, mScreenPosY, null);
		}
		else if (mState == Frontiers.MOVABLESTATE_SPAWNING) {
			c.drawBitmap(mImage, mScreenPosX, mScreenPosY, mOpacity);
			c.drawCircle(mScreenPosX + mWidth / 2, mScreenPosY + mHeight / 2, (1 - mSpawnTimer) * mSpawnRadius, mColor);
		}
		else if (mState == Frontiers.MOVABLESTATE_DYING && mType != Frontiers.AI_PLAYER) {
			if (burstN[0] != -1) { //Compute before drawing
				c.drawLine(burstN[0], burstN[1], burstN[0], burstN[1] + DEATH_LINE_LENGTH, mColor);
				c.drawLine(burstNE[0], burstNE[1], burstNE[0] - DEATH_LINE_LENGTH, burstNE[1] + DEATH_LINE_LENGTH, mColor);
				c.drawLine(burstE[0], burstE[1], burstE[0] - DEATH_LINE_LENGTH, burstE[1], mColor);
				c.drawLine(burstSE[0], burstSE[1], burstSE[0] - DEATH_LINE_LENGTH, burstSE[1] - DEATH_LINE_LENGTH, mColor);
				c.drawLine(burstS[0], burstS[1], burstS[0], burstS[1] - DEATH_LINE_LENGTH, mColor);
				c.drawLine(burstSW[0], burstSW[1], burstSW[0] + DEATH_LINE_LENGTH, burstSW[1] - DEATH_LINE_LENGTH, mColor);
				c.drawLine(burstW[0], burstW[1], burstW[0] + DEATH_LINE_LENGTH, burstW[1], mColor);
				c.drawLine(burstNW[0], burstNW[1], burstNW[0] + DEATH_LINE_LENGTH, burstNW[1] + DEATH_LINE_LENGTH, mColor);
			}
		}
	}
	
	public void updateScreenPosition() {
		this.mScreenPosX = Gameworld.worldXCoordToScreen((int)mWorldPositionX, mWidth);
		this.mScreenPosY = Gameworld.worldYCoordToScreen((int)mWorldPositionY, mHeight);
		mBounds.set(mScreenPosX + mWidthOffset, mScreenPosY + mHeightOffset, mScreenPosX + mWidth - mWidthOffset, mScreenPosY + mHeight - mHeightOffset);
	}

}
