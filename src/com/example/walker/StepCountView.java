package com.example.walker;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class StepCountView extends View {

	private static int myGreen, myAlphaGreen, myBlue, myGray; 

	private static float centerX = 0, centerY = 0, radius = 0;
	private static final float MARGIN = 100;
	private static final float CYCLE_ANGLE = 360;
	private static final float START_ANGLE = -90;
	private static final float FONT_SIZE = 270;
	private static final String GOAL_STRING = "今日目标  ";
	private static final String STEP_STRING = "  步";
	
	private static final int FLASH_FREQ = 30;
	private static float flashR[] = new float[FLASH_FREQ];
	
	public StepCountView(Context context) {
		super(context);
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		centerX = size.x / 2;
		centerY = centerX;
		radius = centerX - MARGIN;
		myGreen = Color.rgb(50, 200, 80);
		myBlue = Color.rgb(40, 180, 250);
		myGray = Color.rgb(215, 215, 215);
		myAlphaGreen = Color.argb(85, 160, 250, 180);
		
		float rangR = radius / 2;
		rangR /= FLASH_FREQ;
		for (int i=0; i < FLASH_FREQ; i++) {
			flashR[i] = radius / 2 + rangR * i;
		}
			
	}
	
	@Override  
    public void onDraw(Canvas canvas) {  
        
        Paint paint = new Paint();  
        
        paint.setColor(myAlphaGreen);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(35); 
        canvas.drawCircle(centerX, centerY, 
        		flashR[StepCountFragment.flashCount % FLASH_FREQ], paint);
        
        paint.setColor(myGray);      
        paint.setStrokeWidth(35);  
        canvas.drawCircle(centerX, centerY, radius, paint);
        
        RectF oval = new RectF(MARGIN, MARGIN, centerX + radius, centerY + radius);
        
        paint.setColor(myGreen); 
        
        float angle;
        if (StepCountFragment.total_step < StepCountFragment.user_goal) {
        	angle = CYCLE_ANGLE * StepCountFragment.total_step / StepCountFragment.user_goal;
        } else {
        	angle = CYCLE_ANGLE;
        }
        canvas.drawArc(oval, START_ANGLE, angle, false, paint);
        
        //画出字符串 drawText(String text, float x, float y, Paint paint)   
        // y 是 基准线 ，不是 字符串的 底部
        
        //设置字体大小  
        paint.setTextSize(FONT_SIZE); 
        //让画出的图形是实心的  
        paint.setStyle(Paint.Style.FILL); 
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(StepCountFragment.total_step + "", centerX, centerY + FONT_SIZE / 3, paint);
        
        paint.setColor(myBlue);
        paint.setTextSize(FONT_SIZE / 3);
        canvas.drawText(GOAL_STRING + StepCountFragment.user_goal + STEP_STRING, centerX, 
        		centerY + centerY + MARGIN + MARGIN, paint);
        
        super.onDraw(canvas);  
    }  
  
}
