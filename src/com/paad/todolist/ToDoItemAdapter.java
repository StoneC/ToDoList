package com.paad.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class ToDoItemAdapter extends ArrayAdapter<ToDoItem> {
	int resource;
	
	public ToDoItemAdapter(Context _context, int _resource, List<ToDoItem> _items)
	{
		super(_context,_resource,_items);
		resource=_resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout todoView;
		Log.d("myevent", "getView started!");
		ToDoItem item = getItem(position);
		
		String taskString = item.getTask();
		Float priority = item.getPriority();
		Date createdDate = item.getCreated();
		Date modifiedDate = item.getModified();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
		String cdateString = sdf.format(createdDate);
		String mdateString = sdf.format(modifiedDate);
		
		if(convertView==null)
		{
			todoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi =(LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, todoView,true);
		}
		else
		{
			todoView = (LinearLayout) convertView;
		}
		
		TextView cdateView = (TextView) todoView.findViewById(R.id.cdate);
		TextView mdateView = (TextView) todoView.findViewById(R.id.mdate);
		TextView taskView = (TextView) todoView.findViewById(R.id.task);
		RatingBar ratingBar = (RatingBar) todoView.findViewById(R.id.priority);
		
		cdateView.setText(cdateString);
		mdateView.setText("modified at : " +mdateString);
		taskView.setText(taskString);
		ratingBar.setRating(priority.floatValue());
		
		return todoView;
		
	}
}
