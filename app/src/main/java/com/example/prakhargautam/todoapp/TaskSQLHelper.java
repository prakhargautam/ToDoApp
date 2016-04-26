package com.example.prakhargautam.todoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by prakhargautam on 12/03/16.
 */
public class TaskSQLHelper extends SQLiteOpenHelper {

    final static String DATABASE_NAME="task_manager";
    final static String TASK_TABLE_NAME="tasks";
    final static String TASK_TABLE_PRIORITY="priority";
    final static String TASK_TABLE_DESCRIPTION="description";
    final static String TASK_TABLE_TAG_NAME="tag";
    final static String TASK_COMMENT="comments";
    final static String TASK_TABLE_DUE_DATE="due_date";
    final static String KEY_TAG_ID="key_id";
    final static String KEY_TASK_ID="task_id";
    final static String TAG_TABLE_NAME="tags";
    final static String TAG_NAME="name";
    final static String TABLE_TASK_TAG="";

    static final String CREATE_TASK_TABLE="CREATE TABLE "+TASK_TABLE_NAME+" ("+KEY_TASK_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            TASK_TABLE_PRIORITY+" INTEGER,"+TASK_TABLE_DESCRIPTION+" TEXT, "+TASK_COMMENT+" TEXT, "
            +TASK_TABLE_DUE_DATE+" INTEGER, "+TASK_TABLE_TAG_NAME+" TEXT);";

    static final String CREATE_TAG_TABLE = "CREATE TABLE " + TAG_TABLE_NAME
            + "(" + KEY_TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + TAG_NAME + " TEXT)";


    public TaskSQLHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_TAG_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}