package com.memeworks.frontiers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class CircleTimer {
	
	public Bitmap image = null;
	
	//Background circle
	private Paint circle_paint = new Paint();
	private Paint arc_paint = new Paint();
	private Paint outline_paint = new Paint();
	private int circle_radius = 25;
	private RectF arc_rect = null;
	
	//Text
	public StringBuilder Text = new StringBuilder();
	private boolean use_text = false;
	private char text_chars[] = new char[20];
	private Paint text_paint = new Paint();
	private Paint text_outline_paint = new Paint();
	private float text_size = 15;
	
	//Timer properties
	public int TIER = 500;
	private float DECAY_RATE = TIER / 5;
	public float current_time = 0;
	private float percent = 0.0f;
	private int centerX = 0;
	private int centerY = 0;
	
	//Combat multiplier specific
	public int Current_Multiplier = 1;
	
	public CircleTimer(int centerX, int centerY, boolean text, int tier, int decay_rate)
	{
		this.centerX = centerX;
		this.centerY = centerY;
		this.use_text = text;
		this.TIER = tier;
		this.DECAY_RATE = decay_rate;
		
		text_paint.setARGB(255, 255, 255, 255);
		text_paint.setTypeface(Frontiers.FONT);
		text_paint.setTextSize(text_size);
		text_paint.setTextAlign(Paint.Align.CENTER);
		text_paint.setAntiAlias(true);
		
		text_outline_paint.setARGB(255, 255, 102, 0);
		text_outline_paint.setTextAlign(Paint.Align.CENTER);
		text_outline_paint.setTextSize(text_size);
		text_outline_paint.setTypeface(Frontiers.FONT);
		text_outline_paint.setStyle(Paint.Style.STROKE);
		text_outline_paint.setStrokeWidth(2);
		text_outline_paint.setAntiAlias(true);
		
		circle_paint.setARGB(255, 0, 0, 0);
		circle_paint.setAntiAlias(true);
		circle_paint.setStrokeWidth(circle_radius);
		
		arc_paint.setARGB(255, 255, 102, 0);
		arc_paint.setAntiAlias(true);
		arc_paint.setStrokeWidth(circle_radius);
		
		outline_paint.setARGB(255, 255, 255, 255);
		outline_paint.setAntiAlias(true);
		outline_paint.setStyle(Paint.Style.STROKE);
		outline_paint.setStrokeWidth(2);
		
		arc_rect = new RectF(centerX - circle_radius, centerY - circle_radius, centerX + circle_radius, centerY + circle_radius);
	}
	
	public void Draw(Canvas c, int offsetY)
	{
		arc_rect.offset(0, offsetY);
		if (current_time > 0) {
			c.drawCircle(centerX, centerY + offsetY, circle_radius, outline_paint);
			c.drawArc(arc_rect, 0, (int)(360.0 * percent), true, arc_paint);
			c.drawCircle(centerX, centerY + offsetY, circle_radius / 1.5f, circle_paint);

			if (use_text) {
				Text.getChars(0, Text.length(), text_chars, 0);
				c.drawText(text_chars, 0, Text.length(), centerX, centerY + offsetY + 5, text_outline_paint);
				c.drawText(text_chars, 0, Text.length(), centerX, centerY + offsetY + 5, text_paint);
			}
			else if (image != null){
				c.drawBitmap(image, centerX - image.getWidth() / 2, centerY + offsetY - image.getHeight() / 2, null);
			}
		}
		arc_rect.offset(0, -offsetY);
	}
	
	public void Frame_Started(float timeSinceLastFrame)
	{
		current_time -= DECAY_RATE * timeSinceLastFrame * Current_Multiplier / 1.5;
		if (current_time < 0)
		{
			current_time = 0;
		}
		
		if (use_text)
		{
			Current_Multiplier = (int) (current_time / TIER); 
			if (Current_Multiplier <= 1)
			{
				Current_Multiplier = 1;
			}
			
			Text.setLength(0);
			Text.append("x" + Current_Multiplier);
		}
		
		percent = ((int)(current_time) % TIER) / (float)TIER;
	}
	
	public void add(int time)
	{
		current_time += time;
	}
	
	public void reset()
	{
		Current_Multiplier = 1;
		current_time = 0;
	}
	
}
