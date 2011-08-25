package com.paad.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoItem {
	String task;
	float priority;
	Date created;
	Date modified;
	
	
	public String getTask()
	{
		return task;
	}
	
	public float getPriority()
	{
		return priority;
	}
	
	public Date getCreated()
	{
		return created;
	}
	
	public Date getModified()
	{
		return modified;
	}

	public ToDoItem(String _task, float _priority)
	{
		this(_task, _priority,
				new Date(java.lang.System.currentTimeMillis()),
				new Date(java.lang.System.currentTimeMillis()));
	}

	public ToDoItem(String _task, float _priority, Date _created,Date _modified) {
		task=_task;
		priority=_priority;
		created = _created;
		modified=_modified;
	}
	
	@Override
	public String toString()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yy");
		String dateString = sdf.format(created);
		return "("+dateString+")"+task;	
	}
}
