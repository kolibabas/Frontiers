package com.memeworks.frontiers;

import android.graphics.Canvas;
import android.graphics.Paint;

public class FloatText {
	
	public final StringBuilder Text = new StringBuilder(100);
	
	public int Position_World_X = 0;
	public int Position_World_Y = 0;
	public boolean Visible = false;
	
	private int opacity = 255;
	private int opacity_decay_rate = 250;
	private float text_size_init = 12;
	private float text_size_current = 12;
	
	private Paint text_paint = new Paint();
	private Paint outline_paint = new Paint();
	private final char chars[] = new char[100];
	
	public FloatText() {}
	
	public void Show(int x, int y, String txt, float text_size, int decay_rate)
	{
		Position_World_X = x;
		Position_World_Y = y;
		
		Text.setLength(0);
		Text.append(txt);
		
		opacity = 255;
		opacity_decay_rate = decay_rate;
		Visible = true;
		
		text_paint.setARGB(opacity, 255, 255, 255);
		text_paint.setTypeface(Frontiers.FONT);
		text_paint.setTextSize(text_size);
		text_paint.setTextAlign(Paint.Align.CENTER);
		text_paint.setAntiAlias(true);
		text_size_current = text_size;
		
	    outline_paint.setARGB(opacity, 255, 102, 0);
	    outline_paint.setTextAlign(Paint.Align.CENTER);
	    outline_paint.setTextSize(text_size);
	    outline_paint.setTypeface(Frontiers.FONT);
	    outline_paint.setStyle(Paint.Style.STROKE);
	    outline_paint.setAntiAlias(true);
	    outline_paint.setStrokeWidth(2);
	}
	
	public void Hide()
	{
		Visible = false;
	}
	
	public void Draw(Canvas c)
	{
		if (this.Visible)
		{
			Text.getChars(0, Text.length(), chars, 0);
			c.drawText(chars, 0, Text.length(), Gameworld.worldXCoordToScreen(Position_World_X, 0), Gameworld.worldYCoordToScreen(Position_World_Y, 0), outline_paint);
			c.drawText(chars, 0, Text.length(), Gameworld.worldXCoordToScreen(Position_World_X, 0), Gameworld.worldYCoordToScreen(Position_World_Y, 0), text_paint);
		}
	}
	
	public void Frame_Started(float frame_time)
	{
		opacity -= frame_time * opacity_decay_rate;
		text_size_current += frame_time * 10;
		
		if (opacity <= 0)
		{
			opacity = 0;
			text_size_current = text_size_init;
			this.Visible = false;
		}
		else
		{
			text_paint.setTextSize(text_size_current);
			text_paint.setARGB(opacity, 255, 255, 255);
			
			outline_paint.setTextSize(text_size_current);
			outline_paint.setARGB(opacity, 255, 102, 0);
		}
	}
}
