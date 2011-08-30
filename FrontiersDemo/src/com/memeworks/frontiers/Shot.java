package com.memeworks.frontiers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Shot {
	
	public int mWorldPositionX;
	public int mWorldPositionY;
	
	public int mScreenPosX;
	public int mScreenPosY;
	
	public int mDirection;
	public int mMoveSpeed = 300;
	
	public boolean mVisible = true;
	
	private float dX = 0;
	private float dY = 0;
	
	private final int mWidth = 12;
	private final int mHeight = 12;
	
	private Paint mPaint = null;
	private Rect mBounds;
	
	//public Shot(float directionX, float directionY, int originX, int originY)  {
	public Shot() {
		
		mPaint = new Paint();
		mPaint.setARGB(255, 245, 184, 0);
		
		switch (Frontiers.ScreenSize) {
		case Frontiers.SCREENSIZE_LARGE:
			mMoveSpeed = 750;
			break;
		case Frontiers.SCREENSIZE_MEDIUM:
			mMoveSpeed = 450;
			break;
		case Frontiers.SCREENSIZE_SMALL:
			mMoveSpeed = 360;
			break;
		}
		
		mBounds = new Rect();
	}
	
	public void fire(float directionX, float directionY, int originX, int originY) {

		mVisible = true;
		mWorldPositionX = originX;
		mWorldPositionY = originY;
		
		mScreenPosX = Gameworld.worldXCoordToScreen((int)mWorldPositionX, mWidth);
		mScreenPosY = Gameworld.worldYCoordToScreen((int)mWorldPositionY, mHeight);
		
		dX = directionX;
		dY = directionY;
	}
	
	public void move(float timeSinceLastFrame) {
		mWorldPositionX += dX * mMoveSpeed * timeSinceLastFrame;
		mWorldPositionY += dY * mMoveSpeed * timeSinceLastFrame;
		
		mScreenPosX = Gameworld.worldXCoordToScreen((int)mWorldPositionX, mWidth);
		mScreenPosY = Gameworld.worldYCoordToScreen((int)mWorldPositionY, mHeight);
		
		mVisible = Gameworld.isInBoundsX(mScreenPosX, mWidth) && Gameworld.isInBoundsY(mScreenPosY, mHeight); 
	}
	
	public void draw(Canvas c) {
		if (mVisible) {
			c.drawCircle(mScreenPosX + 6, mScreenPosY + 6, 6, mPaint);
		}
	}
	
	public Rect getBounds() {
		mBounds.set(mScreenPosX, mScreenPosY, mScreenPosX + mWidth, mScreenPosY + mHeight);
		return mBounds;
	}
}
