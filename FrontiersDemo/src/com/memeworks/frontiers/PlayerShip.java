package com.memeworks.frontiers;

import android.graphics.Bitmap;


public class PlayerShip extends MovableObject {
	
	public float mFireRate;
	public int mFireDirection;
	
	public Shot[] mShots;
	public int mShotIndex = 0;
	
	private int mWorldCenterX;
	private int mWorldCenterY;
	
	public PlayerShip(int x, int y, Bitmap d) {
		super(Frontiers.AI_PLAYER, x, y, d);

		mFireRate = 0.125f;
		mFireDirection = 0;
		
		switch (Frontiers.ScreenSize) {
		case Frontiers.SCREENSIZE_LARGE:
			mMoveSpeed = 225.0f;
			break;
		case Frontiers.SCREENSIZE_MEDIUM:
			mMoveSpeed = 175.0f;
			break;
		case Frontiers.SCREENSIZE_SMALL:
			mMoveSpeed = 115.0f;
			break;
		}
		
		mWorldCenterX = x;
		mWorldCenterY = y;
		
		//Start in visible center of world
		mWorldPositionX = x - mWidth / 2;
		mWorldPositionY = y - mHeight / 2;

		mShots = new Shot[15];
		for (int i = 0; i < 15; i++) {
			mShots[i] = new Shot();
		}
		this.updateScreenPosition();
	}
	
	/**
	 * Moves the Player Ship dx and dx multiplied by its moveSpeed
	 * Stays inside the play area boundaries
	 * @param dx Percent of moveSpeedX
	 * @param dy Percent of moveSpeedY
	 * @param timeSinceLastFrame
	 */
	public void move(float dx, float dy, float timeSinceLastFrame) {
		int tempX = mWorldPositionX;
		int tempY = mWorldPositionY;
		
		if (dx > 0) {
			dx = dx * 1.10f;
		}
		if (dy > 0) {
			dy = dy * 1.10f;
		}
		
		mWorldPositionX += dx * this.mMoveSpeed * timeSinceLastFrame;
		mWorldPositionY += dy * this.mMoveSpeed * timeSinceLastFrame;
		
		this.updateScreenPosition();
		
		if (!Gameworld.isInBoundsX(mScreenPosX, mWidth)) {
			mWorldPositionX = tempX;
			
			this.updateScreenPosition();
		} 
		if (!Gameworld.isInBoundsY(mScreenPosY, mHeight)) {
			mWorldPositionY = tempY;
			
			this.updateScreenPosition();
		} 
		
	}
	
	public void shoot(float directionX, float directionY) {
		mShots[mShotIndex].fire(directionX, directionY, mWorldPositionX + mWidth/2, mWorldPositionY + mHeight/2);
		mShotIndex++;
		
		if (mShotIndex > 14) {
			mShotIndex = 0;
		}
	}
	
	public void death() {
		mState = Frontiers.MOVABLESTATE_DYING;
		mWorldPositionX = mWorldCenterX - mWidth / 2;
		mWorldPositionY = mWorldCenterY - mHeight / 2;
		updateScreenPosition();
	}
	
}
