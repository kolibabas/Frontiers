package com.memeworks.frontiers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

public class Gameworld {
	
	public int mWorldCenterX = 0;
	public int mWorldCenterY = 0;
	
	/** The currently visible area */
	private static Rect mVisibleArea;
	
	/** The screen area that triggers scrolling */
	public static Rect mScrollArea;
	
	/** The player accessible coordinate area, should not be modified */
	public static Rect mPlayArea;
	public static Rect mBackgroundArea;

	public Bitmap mBackgroundImage;
	public Bitmap mBackgroundGrid;
	
	private Paint mBorderPaint = null;
	private Paint mGridPaint = null;
	
	//Scroll calculation variables
	private int mScrollX;
	private int mScrollY;
	private int mScrollPosX = 0;
	private int mScrollPosY = 0;
	
	//World coordinate to screen calculation variables
	private static int mOffset;
	private static int mResult;
	
	public Gameworld(Bitmap backgroundImage, Bitmap backgroundGrid) {

		mVisibleArea = new Rect(0, 0, Frontiers.SCREEN_WIDTH - 1, Frontiers.SCREEN_HEIGHT - 1);
		
		mPlayArea = new Rect((int)(mVisibleArea.centerX() - Frontiers.SCREEN_WIDTH * 0.75f),
							 (int)(mVisibleArea.centerY() - Frontiers.SCREEN_HEIGHT * 0.75f),
							 (int)(mVisibleArea.centerX() + Frontiers.SCREEN_WIDTH * 0.75f),
							 (int)(mVisibleArea.centerY() + Frontiers.SCREEN_HEIGHT * 0.75f));
		
		mBackgroundArea = new Rect(mPlayArea);
		
		int scrollOffsetX = Frontiers.SCREEN_WIDTH / 3;
		int scrollOffsetY = Frontiers.SCREEN_HEIGHT / 3;
		
		mScrollArea = new Rect(scrollOffsetX, 
							   scrollOffsetY, 
							   Frontiers.SCREEN_WIDTH - scrollOffsetX, 
							   Frontiers.SCREEN_HEIGHT - scrollOffsetY);
		
		mWorldCenterX = mPlayArea.width() / 2;
		mWorldCenterY = mPlayArea.height() / 2;
		
		mBorderPaint = new Paint();
		mBorderPaint.setARGB(255, 255, 255, 255);
		mBorderPaint.setStyle(Style.STROKE);
		
		mGridPaint = new Paint();
		mGridPaint.setARGB(255, 0, 255, 0);
		
		mBackgroundImage = backgroundImage;
		mBackgroundGrid = backgroundGrid;
	}
	
	public void draw(Canvas c) {
		c.drawBitmap(mBackgroundImage, mBackgroundArea.left, mBackgroundArea.top, null);
		c.drawBitmap(mBackgroundGrid, mPlayArea.left, mPlayArea.top, null);
		
		if (mPlayArea.left >= 0) {
			c.drawLine(mPlayArea.left, mPlayArea.top, mPlayArea.left, mPlayArea.bottom, mBorderPaint);
		}
		else if (mPlayArea.right <= Frontiers.SCREEN_WIDTH) {
			c.drawLine(mPlayArea.right - 1, mPlayArea.top, mPlayArea.right - 1, mPlayArea.bottom, mBorderPaint);
		}
		
		if (mPlayArea.top >= 0) {
			c.drawLine(mPlayArea.left, mPlayArea.top, mPlayArea.right, mPlayArea.top, mBorderPaint);
		}
		else if (mPlayArea.bottom <= Frontiers.SCREEN_HEIGHT) {
			c.drawLine(mPlayArea.left, mPlayArea.bottom - 1, mPlayArea.right, mPlayArea.bottom - 1, mBorderPaint);
		}

	}
	
	/**
	 * Scrolls the visible area based on player ship location
	 * @param x Player ship's current screenPosX
	 * @param y Player ship's current screenPosY
	 */
	public void scroll(int pShipX, int pShipY, int width, int height) {
		mScrollX = 0;
		mScrollY = 0;
		
		if (pShipX < mScrollArea.left && mPlayArea.left < 0) {
			mScrollX = mScrollArea.left - pShipX;
		} 
		else if (pShipX + width > mScrollArea.right && mPlayArea.right > Frontiers.SCREEN_WIDTH) {
			mScrollX = mScrollArea.right - (pShipX + width);
		}
		
		if (pShipY < mScrollArea.top && mPlayArea.top < 0) {
			mScrollY = mScrollArea.top - pShipY;
		}
		else if (pShipY + height > mScrollArea.bottom && mPlayArea.bottom > Frontiers.SCREEN_HEIGHT) {
			mScrollY = mScrollArea.bottom - (pShipY + height);
		}
		
		if (mScrollX != 0 || mScrollY != 0) {
			mScrollPosX += mScrollX;
			mScrollPosY += mScrollY;
			mPlayArea.offset(mScrollX, mScrollY);
			mBackgroundArea.offset(mScrollX / 2, mScrollY / 2);
		}
	}
	
	public void resetScroll() {
		mPlayArea = new Rect((int)(mVisibleArea.centerX() - Frontiers.SCREEN_WIDTH * 0.75f),
				 (int)(mVisibleArea.centerY() - Frontiers.SCREEN_HEIGHT * 0.75f),
				 (int)(mVisibleArea.centerX() + Frontiers.SCREEN_WIDTH * 0.75f),
				 (int)(mVisibleArea.centerY() + Frontiers.SCREEN_HEIGHT * 0.75f));
	}
	
	public static boolean isInBoundsX(int screenX, int width) {
		return (screenX > mPlayArea.left &&
				screenX + width < mPlayArea.right);
	}
	
	public static boolean isInBoundsY(int screenY, int height) {
		return (screenY > mPlayArea.top &&
				screenY + height < mPlayArea.bottom);
	}
	
	/**
	 * Determines on screen X coordinate from World X Coordinate.
	 * Negative values mean it is off screen
	 * @param x The world X coordinate
	 * @return
	 */
	public static int worldXCoordToScreen(int x, int width) {
		
		mOffset = Math.abs(mVisibleArea.left - mPlayArea.left);
		mResult = x - mOffset;
		
		if (mResult < mPlayArea.left) {
			mResult = mPlayArea.left;
		}
		else if (mResult + width > mPlayArea.right) {
			mResult = mPlayArea.right - width;
		}

		return mResult;
	}
	
	/**
	 * Determines on screen Y coordinate from World Y Coordinate.
	 * Negative values mean it is off screen
	 * @param y The world Y coordinate
	 * @return
	 */
	public static int worldYCoordToScreen(int y, int height) {
		
		mOffset = Math.abs(mVisibleArea.top - mPlayArea.top);
		mResult = y - mOffset;
		
		if (mResult < mPlayArea.top) {
			mResult = mPlayArea.top;
		}
		else if (mResult + height > mPlayArea.bottom) {
			mResult = mPlayArea.bottom - height;
		}
		
		return mResult;
	}
	
}
