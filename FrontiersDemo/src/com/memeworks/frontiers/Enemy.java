package com.memeworks.frontiers;

import java.util.ArrayList;
import android.graphics.Bitmap;
import android.util.FloatMath;

public class Enemy extends MovableObject {
	public int mScore;
	
	public ArrayList<int[]> mDestList = null;
	private int[] mCurrentDest = null;
	private int mDestIndex = 0;
	
	public Enemy(int type, int x, int y, Bitmap d, int score, ArrayList<int[]> destList) {
		super(type, x, y, d);
		
		mScore = score;
		mSpawnTimer = 1.0f;
		
		switch (Frontiers.ScreenSize) {
		case Frontiers.SCREENSIZE_LARGE:
			mMoveSpeed = 190.0f;
			break;
		case Frontiers.SCREENSIZE_MEDIUM:
			mMoveSpeed = 100.0f;
			break;
		case Frontiers.SCREENSIZE_SMALL:
			mMoveSpeed = 75.0f;
			break;
		}
		
		if (mType == Frontiers.AI_PATROL_LR || mType == Frontiers.AI_PATROL_UD) {
			mMoveSpeed = mMoveSpeed * 1.2f;
		}
		
		if (mType == Frontiers.AI_CHASE) { //Chasers don't have destinations
			mCurrentDest = new int[] {0,0};
			mDestList = new ArrayList<int[]>();
			mDestList.add(new int[] { mWorldPositionX, mWorldPositionY});
		}
		else {
			mDestList = destList;
		}
	}
	
	public void moveTowardDestination(float timeSinceLastFrame, int playerX, int playerY) {
		if (mType == Frontiers.AI_CHASE) {
			mCurrentDest[0] = playerX;
			mCurrentDest[1] = playerY;
			moveTowardDestination(timeSinceLastFrame);
		}
		else if (mDestList != null) {
			moveTowardDestination(timeSinceLastFrame);
		}
	}
	
	private void moveTowardDestination(float timeSinceLastFrame) {
		if (mCurrentDest == null){
			mCurrentDest = mDestList.get(1);
			mDestIndex = 1;
		}
		
		float moveAmount = mMoveSpeed * timeSinceLastFrame;
		
		//Find potential position to be moved to
		float[] potentialPos = new float[] {mCurrentDest[0], mCurrentDest[1]};
		
		//Center Potential Position on origin
		potentialPos[0] = mCurrentDest[0] - mWorldPositionX;
		potentialPos[1] = mCurrentDest[1] - mWorldPositionY;
		
		//Normalize Potential Position
		float tempMag = potentialPos[0] * potentialPos[0] + potentialPos[1] * potentialPos[1];
		tempMag = FloatMath.sqrt(tempMag);
		if (tempMag == 0) { 
			tempMag = 0.1f;
		}
		
		potentialPos[0] = potentialPos[0] / tempMag;
		potentialPos[1] = potentialPos[1] / tempMag;
		
		//Find movement amount vector
		potentialPos[0] = potentialPos[0] * moveAmount;
		potentialPos[1] = potentialPos[1] * moveAmount;
		
		//Add to movement amount vector to current world position
		potentialPos[0] = potentialPos[0] + mWorldPositionX;
		potentialPos[1] = potentialPos[1] + mWorldPositionY;
		
		//Find distance from mPosition to currentDest and potentialPos
		float[] tempDist = new float[] { mWorldPositionX - mCurrentDest[0], mWorldPositionY - mCurrentDest[1] };
		float distanceToDest =  FloatMath.sqrt(tempDist[0] * tempDist[0] + tempDist[1] * tempDist[1]);
		
		tempDist[0] = mWorldPositionX - potentialPos[0];
		tempDist[1] = mWorldPositionY - potentialPos[1];
		float distanceToPotential = FloatMath.sqrt(tempDist[0] * tempDist[0] + tempDist[1] * tempDist[1]);
		
		//If we are about to overshoot, move to destination, advance currentDest to next waypoint for next frame
		//Chasers don't need a next destination, if they've arrived, the player dies
		if (distanceToDest <= distanceToPotential && mType != Frontiers.AI_CHASE) {
			mWorldPositionX = mCurrentDest[0];
			mWorldPositionY = mCurrentDest[1];
			mDestIndex++;
			
			if (mDestIndex >= mDestList.size()){
				mCurrentDest = mDestList.get(0);
				mDestIndex = 0;
			}
			else {
				mCurrentDest = mDestList.get(mDestIndex);
			}
		}
		else {
			mWorldPositionX = (int) potentialPos[0];
			mWorldPositionY = (int) potentialPos[1];
		}
	} 

}
