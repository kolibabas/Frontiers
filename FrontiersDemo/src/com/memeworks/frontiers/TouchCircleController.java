package com.memeworks.frontiers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.FloatMath;

public class TouchCircleController {
	
	public float mTouchX = -1;
	public float mTouchY = -1;
	public float mLastDist = 0;
	public final int mTouchCircleRadius = 10;
	public static Paint mTouchCirclePaint = null;
	
	public float mCenterX = -1;
	public float mCenterY = -1;
	public static int mAllowedCircleRadius = 39;
	public static int mCircleBorderRadius = 40;
	public static int mWarningCircleRadius = 50;
	public static Paint mAllowedCirclePaint = null;
	
	public static final int mDeadZoneCircleRadius = 7;
	public static Paint mDeadZoneCirclePaint = null;
	
	public boolean mVisible = false;
	public boolean mContainsEnemies = false;
	public Paint mWarningPaint = null;
	
	private float mAngle = 0;
	private int mAlpha = 255;
	private int mRed = 255;
	private int mGreen = 255;
	private int mBlue = 255;
	
	private Rect mBounds;

	/**
	 * Creates a new Touch Circle Controller for moving or firing
	 * @param screenWidth The width of this device's screen
	 * @param screenHeight The height of this device's screen
	 * @param isMovementControl 
	 * @param left If true, place circle on the left, if false, the right
	 */
	public TouchCircleController(boolean isMovementControl) {
		
		if (Frontiers.SCREEN_WIDTH >= 800) {
			mAllowedCircleRadius = 99;
			mCircleBorderRadius = 100;
			mWarningCircleRadius = 125;
		}
		
		mAllowedCirclePaint = new Paint();
		mAllowedCirclePaint.setAntiAlias(true);
		if (!isMovementControl) {
			mGreen = 0;
			mBlue = 0;
		}
		mAllowedCirclePaint.setStyle(Style.STROKE);
		mAllowedCirclePaint.setStrokeWidth(2);
		
		mDeadZoneCirclePaint = new Paint();
		mDeadZoneCirclePaint.setARGB(255, 255, 255, 255);
		
		mTouchCirclePaint = new Paint();
		mTouchCirclePaint.setAntiAlias(false);
		mTouchCirclePaint.setARGB(255, 255, 255, 255);
		mTouchCirclePaint.setStyle(Style.FILL);
		
		mWarningPaint = new Paint();
		mWarningPaint.setARGB(125, 255, 0, 0);
		mWarningPaint.setStyle(Style.FILL);
		
		mBounds = new Rect();
	}
	
	public void draw(Canvas c) {
		if (mTouchX >= 0 && mTouchY >= 0 && mVisible) {
			if (mLastDist < mAllowedCircleRadius) {
				c.drawCircle(mTouchX, mTouchY, mTouchCircleRadius, mTouchCirclePaint);
			}
		}
		else if (mVisible == false && mAlpha > 0) {
			mAlpha -= 25;
		}
		
		if (mAlpha > 0) {	
			mAllowedCirclePaint.setARGB(mAlpha, mRed, mGreen, mBlue);
			c.drawCircle(mCenterX, mCenterY, mCircleBorderRadius, mAllowedCirclePaint);
			c.drawCircle(mCenterX, mCenterY, mDeadZoneCircleRadius, mDeadZoneCirclePaint);	
			
			if (mContainsEnemies) {
				c.drawCircle(mCenterX, mCenterY, mWarningCircleRadius, mWarningPaint);
			}
		}		
	}
	
	/**
	 * Calculates movement for a player object, use calculateFireDirection()
	 * for firing.
	 * @return
	 */
	public float[] getMoveDirection() {
		float x = (int)mTouchX - mCenterX;
		float y = (int)mTouchY - mCenterY;
		mLastDist = FloatMath.sqrt(x * x + y * y);
		
		float mRadianAngle = (float) Math.atan2(x,y);
		mAngle = (-mRadianAngle * 57.295f) + 180; //57.295 ~= 180/pi, +180 because up is down for android
		
		//Normalize distance for better game play
		x = x / mLastDist;
		y = y / mLastDist;
		
		//If inside the circle, return
		if (mLastDist < mAllowedCircleRadius) {
			return new float[] {x, y, mAngle};
		}
		else {
			float diff = mLastDist - mAllowedCircleRadius;
			mCenterY += FloatMath.cos(mRadianAngle) * (diff * 1.1f);
			mCenterX += FloatMath.sin(mRadianAngle) * (diff * 1.1f);
			
			//Retry, don't recurse because it can cause stack overflows
			x = (int)mTouchX - mCenterX;
			y = (int)mTouchY - mCenterY;
			mLastDist = FloatMath.sqrt(x * x + y * y);
			
			mRadianAngle = (float) Math.atan2(x,y);
			mAngle = (-mRadianAngle * 57.295f) + 180; //57.295 ~= 180/pi, +180 because up is down for android
			
			//Normalize distance for better game play
			x = x / mLastDist;
			y = y / mLastDist;
			
			return new float[] {x, y, mAngle};
		}
	}
	
	public float[] getFireDirection() {
		if (mTouchX > 0 || mTouchY > 0) {
			float x = (int)mTouchX - mCenterX;
			float y = (int)mTouchY - mCenterY;
			mLastDist = FloatMath.sqrt(x * x + y * y);	
			
			//Normalize distance for better game play
			x = x / mLastDist;
			y = y / mLastDist;
			
			float mRadianAngle = (float) Math.atan2(x,y);
			
			//If inside the circle, return
			if (mLastDist < mAllowedCircleRadius) {
				return new float[] {x, y};
			}
			else {
				float diff = mLastDist - mAllowedCircleRadius;
				mCenterY += FloatMath.cos(mRadianAngle) * (diff * 1.1f);
				mCenterX += FloatMath.sin(mRadianAngle) * (diff * 1.1f);
				
				//Retry, don't recurse because it can cause stack overflows
				x = (int)mTouchX - mCenterX;
				y = (int)mTouchY - mCenterY;
				mLastDist = FloatMath.sqrt(x * x + y * y);

				//Normalize distance for better game play
				x = x / mLastDist;
				y = y / mLastDist;
				
				return new float[] {x, y};
			}
		}
		
		return new float[] {0,0};
	}
	
	public void goInvisible() {
		mVisible = false;
		mTouchX = -1;
		mTouchY = -1;
	}
	
	public void goVisible() {
		mVisible = true;
		mAlpha = 255;
		mTouchX = mCenterX;
		mTouchY = mCenterY;
	}
	
	public Rect getBounds() {
		mBounds.set((int)mCenterX - mWarningCircleRadius, (int)mCenterY - mWarningCircleRadius, (int)mCenterX + mWarningCircleRadius, (int)mCenterY + mWarningCircleRadius);
		return mBounds;
	}
}
