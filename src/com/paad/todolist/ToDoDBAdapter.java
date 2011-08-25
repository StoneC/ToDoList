package com.paad.todolist;

import java.sql.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ToDoDBAdapter {
	private static final String DATABASE_NAME="todoList.db";
	private static final String DATABASE_TABLE="todoItems";
	private static final int DATABASE_VERSION=1;
	public static final int TASK_COLUMN = 1;
	public static final int PRIORITY_COLUMN = 2;
	public static final int CREATION_DATE_COLUMN = 3;
	public static final int MODIFY_DATE_COLUMN = 4;
	
	private SQLiteDatabase db;
	private final Context context;
	
	private toDoDBOpenHelper dbHelper;
	
	public ToDoDBAdapter(Context _context)
	{
		this.context = _context;
		dbHelper = new toDoDBOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	public void close()
	{
		db.close();
	}
	
	public void open() throws SQLiteException
	{
		try{
			db = dbHelper.getWritableDatabase();
		}
		catch(SQLiteException ex)
		{
			db = dbHelper.getReadableDatabase();
		}
	}
	
	public long insertTask(ToDoItem _task)
	{
		ContentValues newTaskValues = new ContentValues();
		newTaskValues.put(KEY_TASK, _task.getTask());
		newTaskValues.put(KEY_PRIORITY, _task.getPriority());
		newTaskValues.put(KEY_CREATION_DATE, _task.getCreated().getTime());
		newTaskValues.put(KEY_MODIFY_DATE, _task.getModified().getTime());
		
		return db.insert(DATABASE_TABLE, null, newTaskValues);
	}
	
	public boolean removeTask(long _rowIndex)
	{
		return db.delete(DATABASE_TABLE, KEY_ID+"="+_rowIndex, null)>0;
	}
	
	public boolean updateTask(long _rowIndex, String _task, int _priority)
	{
		ContentValues newValue = new ContentValues();
		newValue.put(KEY_TASK, _task);
		newValue.put(KEY_PRIORITY, _priority);
		newValue.put(KEY_MODIFY_DATE, new Date(java.lang.System.currentTimeMillis()).getTime());
		return db.update(DATABASE_TABLE, newValue, KEY_ID+"="+_rowIndex, null)>0;
	}
	
	public Cursor getAllToDoItemsCursor(){
		return db.query(DATABASE_TABLE, new String[]{ KEY_ID,KEY_TASK,KEY_PRIORITY,KEY_CREATION_DATE,KEY_MODIFY_DATE}, null, null, null, null, null);
	}
	
	public Cursor setCursorToDoItem(long _rowIndex) throws SQLException{
		Cursor result = db.query(true, DATABASE_TABLE, new String[]{KEY_ID,KEY_TASK,KEY_PRIORITY,KEY_CREATION_DATE,KEY_MODIFY_DATE}, KEY_ID+"="+_rowIndex, 
				null, null, null,null,null);
		if((result.getCount()==0)||!result.moveToFirst()){
			throw new SQLException("No to do items found for now: "+_rowIndex);
		}
		return result;
	}
	
	public ToDoItem getToDoItem(long _rowIndex) throws SQLException {
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[]{KEY_ID,KEY_TASK,KEY_PRIORITY,KEY_CREATION_DATE,KEY_MODIFY_DATE}, KEY_ID+"="+_rowIndex, 
				null, null, null,null,null);
		if((cursor.getCount() ==0)||!cursor.moveToFirst()){
			throw new SQLException("No to do item found for now: "+_rowIndex);
		}
		
		String task = cursor.getString(TASK_COLUMN);
		int priority = cursor.getInt(PRIORITY_COLUMN); 
		long created = cursor.getLong(CREATION_DATE_COLUMN);
		long modified = cursor.getLong(MODIFY_DATE_COLUMN);
		
		ToDoItem result = new ToDoItem(task,priority,new Date(created),new Date(modified));
		return result;
	}

	private static final String KEY_ID="_id";
	private static final String KEY_TASK="task";
	private static final String KEY_PRIORITY="priority";
	private static final String KEY_CREATION_DATE="creation_date";
	private static final String KEY_MODIFY_DATE="modify_date";
	
	private static class toDoDBOpenHelper extends SQLiteOpenHelper{
		
		public toDoDBOpenHelper(Context context, String name, CursorFactory factory, int version){
			super(context,name,factory,version);
		}

		private static final String DATABASE_CREATE ="create table " +DATABASE_TABLE
		+"("+KEY_ID+" integer primary key autoincrement," +
		KEY_TASK +" text not null, "+KEY_PRIORITY + " float,"+
		KEY_CREATION_DATE+ " long, "+KEY_MODIFY_DATE+ " long);";
		
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			Log.w("TaskDBAdapter","Upgrading from version "+
					_oldVersion +" to " +
					_newVersion + ",which will destroy all old data");
			_db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE);
			onCreate(_db);
		}
	}
}
