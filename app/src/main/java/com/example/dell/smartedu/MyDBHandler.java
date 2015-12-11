package com.example.dell.smartedu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Dell on 10/7/2015.
 */
public class MyDBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smartedu.db";
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "_id";
    public static final String TITLE = "_title";
    public static final String DESCRIPTION = "description";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        Log.i("abcd", "(MyDBHandler) inside constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_TASKS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " + DESCRIPTION + " TEXT " + ");" ;
        db.execSQL(query);
        Log.i("abcd", "(MyDBHandler) inside onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        Log.i("abcd", "(MyDBHandler) inside onUpgrade");
    }

    public void addTask(Task task){
        Log.i("abcd","(MyDBHandler) inside addTask");
        Log.i("abcd", "taskTitle is: " + task.getTitle() + ", taskDescription is: " + task.getDescription());
        ContentValues values = new ContentValues();
        values.put(TITLE,task.getTitle());
        values.put(DESCRIPTION,task.getDescription());
        //    values.put(task.getTitle(),);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    public void deleteTask(String title){
        Log.i("abcd","(MyDBHandler) inside deleteTask");
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TASKS + " WHERE " + TITLE + "=\"" + title + "\";");
        db.close();///////////
    }

    public String databaseToString(){
        Log.i("abcd","(MyDBHandler) inside databaseToString");
        String dbString = "";
        SQLiteDatabase db = this.getWritableDatabase();//this was not present earlier
        String query = "SELECT * FROM " + TABLE_TASKS + " WHERE 1";

        Log.i("abcd","(MyDBHandler) inside databaseToString2");
        Cursor c = db.rawQuery(query, null);
        Log.i("abcd","(MyDBHandler) inside databaseToString3");
        c.moveToFirst();

        Log.i("abcd", "(MyDBHandler) inside databaseToString4");
        while(!c.isAfterLast()){
            //while(c.moveToFirst() && c.getCount() > 0){
            Log.i("abcd","(MyDBHandler) inside databaseToString5");
            if(c.getString(c.getColumnIndex(COLUMN_ID)) != null){
                Log.i("abcd","(MyDBHandler) inside databaseToString6");
                dbString += c.getString(c.getColumnIndex(COLUMN_ID));
                dbString += "\n";
            }
        }
        Log.i("abcd", "(MyDBHandler) dbstring().getBytes().toString() is: " + dbString.getBytes().toString());
        db.close();
        return dbString;
    }

    public ArrayList<Task> getAllTasks() {
        Log.i("abcd","(MyDBHandler) inside getAllTasks()");
        ArrayList<Task> taskList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //int x = cursor.getCount();
        // looping through all rows and adding to list

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false)
        {
            Task task = new Task();
            task.setId(Integer.parseInt(cursor.getString(0)));
            task.setTitle(cursor.getString(1));
            task.setDescription(cursor.getString(2));
            Log.i("abcd", "id, title and description are: " + cursor.getString(0) + ", " + cursor.getString(1) + "and " +
                    cursor.getString(2));
            // Adding contact to list
            taskList.add(new Task(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2)));
            cursor.moveToNext();
        }
//        Log.i("abcd","(MyDBHandler) inside getAllTasks(): taskList.toString() is: " + taskList.get(0).toString());
//        Log.i("abcd","(MyDBHandler) inside getAllTasks(): taskList.toString() is: " + taskList.get(1).toString());
//        Log.i("abcd","(MyDBHandler) inside getAllTasks(): taskList.toString() is: " + taskList.get(2).toString());

/*
        Task myTask = new Task();
        myTask.setId(0);
        myTask.setTitle("SampleTitle1111111");
        myTask.setDescription("Sample Description of the task goes here!");
        taskList.add(myTask);

        myTask.setId(1);
        myTask.setTitle("SampleTitle222222222222");
        myTask.setDescription("Sample Desc of the task goes here!");
        taskList.add(myTask);
        Log.i("abcd","ListToString: " + taskList.toArray());
*/
        // return contact list
        db.close();////////////
        return taskList;
    }

    public int getContacsCount() {
        String countQuery = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}

