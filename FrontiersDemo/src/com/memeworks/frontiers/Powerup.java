package com.memeworks.frontiers;

import android.graphics.Bitmap;
import java.util.Random;

public class Powerup extends MovableObject {
	
	public String get_text = "";
	
	private Random rand = new Random();
	private Bitmap bomb_image;
	private Bitmap freeze_image;
	private Bitmap shield_image;	

	public Powerup(int type, int x, int y, Bitmap bomb, Bitmap freeze, Bitmap shield) {
		super(type, x, y, bomb);
		
		this.mState = Frontiers.MOVABLESTATE_DEAD;
		bomb_image = bomb;
		freeze_image = freeze;
		shield_image = shield;
	}
	
	public void showRandom(int worldX, int worldY)
	{
		int type = 0;
		switch(rand.nextInt(3))
		{
		case 0:
			type = Frontiers.POWER_UP_BOMB;
			break;
		case 1:
			type = Frontiers.POWER_UP_FREEZE;
			break;
		case 2:
			type = Frontiers.POWER_UP_SHIELD;
			break;
		}
		show(worldX, worldY, type);
	}
	
	public void show(int worldX, int worldY, int type)
	{
		this.mType = type;
		
		switch (type)
		{
		case Frontiers.POWER_UP_BOMB:
			this.mImage = bomb_image;
			this.get_text = "KABOOM!";
			break;
		case Frontiers.POWER_UP_FREEZE:
			this.mImage = freeze_image;
			this.get_text = "FREEZE!";
			break;
		case Frontiers.POWER_UP_SHIELD:
			this.mImage = shield_image;
			this.get_text = "SHIELDS UP!";
			break;
		}
		
		this.mState = Frontiers.MOVABLESTATE_ALIVE; //Cause powerup to be drawn
		this.mWorldPositionX = worldX;
		this.mWorldPositionY = worldY;
	}
	
	public void get()
	{
		this.mState = Frontiers.MOVABLESTATE_DEAD;
	}

}
