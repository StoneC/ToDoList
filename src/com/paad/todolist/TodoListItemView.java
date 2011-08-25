package com.paad.todolist;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class TodoListItemView extends TextView {

	private Paint marginPaint;
	private Paint linePaint;
	private int paperColor;
	private float margin;
	
	public TodoListItemView(Context context, AttributeSet ats,int ds)
	{
		super(context,ats,ds);
		init();
	}
	
	public TodoListItemView(Context context)
	{
		super(context);
		init();
	}
	
	public TodoListItemView(Context context,AttributeSet ats)
	{
		super(context,ats);
		init();
	}
	
	private void init()
	{
		Resources myResources = getResources();
		Log.d("myevent", "ToDoListItemView init started!");
		marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		marginPaint.setColor(myResources.getColor(R.color.notepad_margin));
		
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(myResources.getColor(R.color.notepad_lines));
		
		paperColor = myResources.getColor(R.color.notepad_paper);
		margin = myResources.getDimension(R.dimen.notepad_margin);
		
		
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		Log.d("myevent", "ToDoListItemView onDraw!");
		canvas.drawColor(paperColor);
		
		canvas.drawLine(0, 0, getMeasuredHeight(), 0, linePaint);
		canvas.drawLine(0,getMeasuredHeight(),getMeasuredWidth(),getMeasuredHeight(),linePaint);
		
		canvas.drawLine(margin,0,margin,getMeasuredHeight(),marginPaint);
		
		canvas.save();
		canvas.translate(margin, 0);
		
		super.onDraw(canvas);
		canvas.restore();
	}
}
