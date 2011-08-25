package com.paad.todolist;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;

import java.sql.Date;
import java.util.ArrayList;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.widget.AdapterView;

public class ToDoList extends Activity {
    /** Called when the activity is first created. */
	
	static final private int ADD_NEW_TODO=Menu.FIRST;
	static final private int REMOVE_TODO=Menu.FIRST+1;
	
	private ArrayList<ToDoItem> todoItems;
	private ListView myListView;
	private EditText myEditText;
	private RatingBar myRating;
	private ToDoItemAdapter aa;
	
	ToDoDBAdapter toDoDBAdapter;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //获取对UI小组件的引用
        myListView =(ListView)findViewById(R.id.myListView);
        myEditText=(EditText)findViewById(R.id.myEditText);
        myRating = (RatingBar)findViewById(R.id.myRating);
       
        todoItems = new ArrayList<ToDoItem>();
        
        int resID= R.layout.todolist_item;
        
        aa = new ToDoItemAdapter(this,resID,todoItems);
        
        //将ArrayAdapter绑定到ListView
        myListView.setAdapter(aa);
        
        myEditText.setOnKeyListener(
        		new OnKeyListener()
        		{
        	public boolean onKey(View v,int keyCode, KeyEvent event) 
        	{
        		if(event.getAction()==KeyEvent.ACTION_DOWN)
        			if(keyCode==KeyEvent.KEYCODE_DPAD_CENTER){
        				ToDoItem newItem = new ToDoItem(myEditText.getText().toString(),myRating.getRating());
        				toDoDBAdapter.insertTask(newItem);
        				updateArray();
        				Log.d("myevent", "new ToDoItem created in Database!");
        				myEditText.setText("");
        				aa.notifyDataSetChanged();
        				cancelAdd();
        				Log.d("myevent", "return ture!");
        				return true;
        			}
        		Log.d("myevent", "return false!");
        		return false;
        	}
        }
        		);
        
        registerForContextMenu(myListView);
        
        restoreUIState();
        
        toDoDBAdapter = new ToDoDBAdapter(this);
        toDoDBAdapter.open();
        populateTodoList();
        
    }
    
    Cursor toDoListCursor;
    
    private void populateTodoList() {
		toDoListCursor = toDoDBAdapter.getAllToDoItemsCursor();
		startManagingCursor(toDoListCursor);
		
		updateArray();
		
	}

	private void updateArray() {
		toDoListCursor.requery();
		todoItems.clear();
		
		if(toDoListCursor.moveToFirst())
			do {
				String task = toDoListCursor.getString(ToDoDBAdapter.TASK_COLUMN);
				float priority = toDoListCursor.getFloat(ToDoDBAdapter.PRIORITY_COLUMN);
				long created = toDoListCursor.getLong(ToDoDBAdapter.CREATION_DATE_COLUMN);
				long modified = toDoListCursor.getLong(ToDoDBAdapter.MODIFY_DATE_COLUMN);
				ToDoItem newItem = new ToDoItem(task,priority,
						new Date(created),new Date(modified));
				todoItems.add(0,newItem);
				
			}while(toDoListCursor.moveToNext());
		
		aa.notifyDataSetChanged();
		
	}

	private void restoreUIState() {
		// TODO Auto-generated method stub
		SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
		
		String text = settings.getString(TEXT_ENTRY_KEY, "");
		Boolean adding = settings.getBoolean(ADDING_ITEM_KEY, false);
		
		if(adding)
		{
			addNewItem();
			myEditText.setText(text);
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	super.onCreateOptionsMenu(menu);
    	
    	MenuItem itemAdd = menu.add(0,ADD_NEW_TODO,Menu.NONE,R.string.add_new);
    	MenuItem itemRem = menu.add(0,REMOVE_TODO,Menu.NONE,R.string.remove);
    	
    	itemAdd.setIcon(R.drawable.add);
    	itemRem.setIcon(R.drawable.del);
    	
    	itemAdd.setShortcut('0', 'a');
    	itemRem.setShortcut('1', 'r');
    	
    	return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	
    	menu.setHeaderTitle("Selected To Do Item");
    	menu.add(0,REMOVE_TODO,Menu.NONE,R.string.remove);
    }
    
    private boolean addingNew =false;
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	super.onPrepareOptionsMenu(menu);
    	
    	int idx = myListView.getSelectedItemPosition();
    	
    	String removeTitle = getString(addingNew?R.string.cancel:R.string.remove);
    	
    	MenuItem removeItem = menu.findItem(REMOVE_TODO);
    	removeItem.setTitle(removeTitle);
    	removeItem.setVisible(addingNew||idx>-1);
    	
    	return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	super.onOptionsItemSelected(item);
    	
    	int index = myListView.getSelectedItemPosition();
    	
    	switch(item.getItemId())
    	{
    	case(REMOVE_TODO):
    	{
    		if(addingNew)
    		{
    			cancelAdd();
    		}
    		else
    		{
    			removeItem(index);
    		}
    		return true;
    	}
    	case(ADD_NEW_TODO):
    	{
    		addNewItem();
    		return true;
    	}
    	
    	}
    	return false;
    }
  
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
    	super.onContentChanged();
    	switch(item.getItemId())
    	{
    	case(REMOVE_TODO):
    	{    
    		AdapterView.AdapterContextMenuInfo menuInfo;
    		menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    		
    		int index =menuInfo.position;
    		removeItem(index);
    		return true;
    	}
    	}
    	return false;
    }
    private void cancelAdd()
    {
    	addingNew= false;
    	myEditText.setVisibility(View.GONE);
    	myRating.setVisibility(View.GONE);
    }
    
    private void addNewItem()
    {
    	addingNew= true;
    	myEditText.setVisibility(View.VISIBLE);
    	myRating.setVisibility(View.VISIBLE);
    	
    	
    }
    
    private void removeItem(int _index)
    {
    	toDoDBAdapter.removeTask(todoItems.size()-_index);
    	updateArray();
    }
    
    private static final String TEXT_ENTRY_KEY ="TEXT_ENTRY_KEY";
    private static final String ADDING_ITEM_KEY ="ADDING_ITEM_KEY";
    private static final String SELECTED_INDEX_KEY="SELECTED_INDEX_KEY";
    
    @Override
    protected void onPause()
    {
    	super.onPause();
    	
    	SharedPreferences uiState = getPreferences(0);
    	SharedPreferences.Editor editor = uiState.edit();
    	
    	editor.putString(TEXT_ENTRY_KEY, myEditText.getText().toString());
    	editor.putBoolean(ADDING_ITEM_KEY, addingNew);
    
    	editor.commit();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
    	savedInstanceState.putInt(SELECTED_INDEX_KEY, myListView.getSelectedItemPosition());
    	
    	super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
    	int pos= -1;
    	
    	if(savedInstanceState != null)
    		if(savedInstanceState.containsKey(SELECTED_INDEX_KEY))
    		{
    			pos = savedInstanceState.getInt(SELECTED_INDEX_KEY,-1);
    		}
    	myListView.setSelection(pos);
    }
    
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    	toDoDBAdapter.close();
    }
}
