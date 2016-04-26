package com.example.prakhargautam.todoapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {


    static int REQUEST_CODE=1;
    static int EDIT_REQUEST_CODE=2;
    TextView textView;

    ArrayList<Task> tasks;
    TaskAdapter taskAdapter;
    ListView taskList;
    Boolean openDetail=false;
    LinearLayout previousDetails;
    View previousChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if(openDetail){
                    previousDetails.removeView(previousChild);
                }
                Intent i = new Intent();
                i.setClass(MainActivity.this,AddActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });

        tasks= new ArrayList<>();
        tasks=getTaskList();
        taskList= (ListView) findViewById(R.id.task_list);
        taskAdapter= new TaskAdapter(this,tasks);

        taskList.setAdapter(taskAdapter);

        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if(openDetail){
                    previousDetails.removeView(previousChild);
                }else{
                    openDetail=true;
                }

                final LinearLayout rl_inflate = (LinearLayout)view.findViewById(R.id.wrapper);
                final View child = getLayoutInflater().inflate(R.layout.details,null);
                rl_inflate.addView(child);

                previousDetails= rl_inflate;
                previousChild= child;

                TextView dueDate= (TextView) child.findViewById(R.id.due_date);
                String s=tasks.get(position).getDueDate().getTime().toString().substring(0,9);
                dueDate.setText(s);

                TextView comments= (TextView)child.findViewById(R.id.comments);

                if(tasks.get(position).getComment()!=null){
                    comments.setText(tasks.get(position).getComment());
                }

                FloatingActionButton edit = (FloatingActionButton)child.findViewById(R.id.edit);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rl_inflate.removeView(child);
                        openDetail=false;
                        Intent intent= new Intent();
                        intent.putExtra("edit", tasks.get(position));
                        intent.putExtra("position",position);
                        intent.setClass(MainActivity.this,AddActivity.class);
                        startActivityForResult(intent, EDIT_REQUEST_CODE);
                    }
                });
                Button close= (Button)child.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rl_inflate.removeView(child);
                        openDetail=false;
                    }
                });
                FloatingActionButton delete=(FloatingActionButton)child.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TaskSQLHelper taskSQLHelper= new TaskSQLHelper(MainActivity.this,1);
                        SQLiteDatabase db= taskSQLHelper.getWritableDatabase();
                        db.delete(taskSQLHelper.TASK_TABLE_NAME,taskSQLHelper.TASK_TABLE_DESCRIPTION+" =?",
                                new String[]{tasks.get(position).getDescription()});
                        db.close();
                        tasks.remove(position);
                        taskAdapter.notifyDataSetChanged();
                        rl_inflate.removeView(child);
                        openDetail=false;

                    }
                });

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            switch(requestCode){
                case 1:
                    tasks.add(0, (Task) data.getSerializableExtra("task"));
                    taskAdapter.notifyDataSetChanged();
                    TaskSQLHelper mDbHelper= new TaskSQLHelper(this,1);
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(mDbHelper.TASK_TABLE_DESCRIPTION, tasks.get(0).getDescription());
                    values.put(mDbHelper.TASK_TABLE_PRIORITY, tasks.get(0).getPriority());
                    values.put(mDbHelper.TASK_TABLE_TAG_NAME, tasks.get(0).getTag().getName());
                    values.put(mDbHelper.TASK_TABLE_DUE_DATE, tasks.get(0).getDueDate().getTimeInMillis());
                    values.put(mDbHelper.TASK_COMMENT, tasks.get(0).getComment());

                    db.insert(
                            mDbHelper.TASK_TABLE_NAME, null,
                            values);

                    db.close();

                    break;

                case 2:

                    Task newTask= (Task) data.getSerializableExtra("task");
                    int position= data.getIntExtra("position", 0);
                    TaskSQLHelper meditDbHelper= new TaskSQLHelper(this,1);
                    SQLiteDatabase dbedit = meditDbHelper.getWritableDatabase();

                    ContentValues newValues = new ContentValues();
                    newValues.put(meditDbHelper.TASK_TABLE_DESCRIPTION, newTask.getDescription());
                    newValues.put(meditDbHelper.TASK_TABLE_PRIORITY, newTask.getPriority());
                    newValues.put(meditDbHelper.TASK_TABLE_TAG_NAME, newTask.getTag().getName());
                    newValues.put(meditDbHelper.TASK_TABLE_DUE_DATE, newTask.getDueDate().getTimeInMillis());
                    newValues.put(meditDbHelper.TASK_COMMENT, newTask.getComment());

                    dbedit.update(TaskSQLHelper.TASK_TABLE_NAME, newValues, TaskSQLHelper.TASK_TABLE_DESCRIPTION + " = ?",
                            new String[]{tasks.get(position).getDescription()});

                    dbedit.close();

                    tasks.set(position, newTask);
                    taskAdapter.notifyDataSetChanged();

                    break;

                case 3:
            }
        }
        else {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        TaskSQLHelper sqlHelper = new TaskSQLHelper(this, 1);
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        String[] columns = {TaskSQLHelper.TASK_TABLE_PRIORITY, TaskSQLHelper.TASK_TABLE_DESCRIPTION, TaskSQLHelper.TASK_TABLE_TAG_NAME
                ,TaskSQLHelper.TASK_TABLE_DUE_DATE, TaskSQLHelper.TASK_COMMENT };

        Cursor c = db.query(true, TaskSQLHelper.TASK_TABLE_NAME,columns,null,null,null,null,null,null);

        if(id== R.id.priority_sort){
            c = db.query(true, TaskSQLHelper.TASK_TABLE_NAME,columns,null,null,null,null,TaskSQLHelper.TASK_TABLE_PRIORITY,null);
        }
        else if(id==R.id.due_date_sort){
            c=db.query(true, TaskSQLHelper.TASK_TABLE_NAME,columns, null, null, null, null, TaskSQLHelper.TASK_TABLE_DUE_DATE,null);
        }

        int i=0;
        while(c.moveToNext()){
            Task t=new Task();
            t.setPriority(c.getInt(c.getColumnIndexOrThrow(TaskSQLHelper.TASK_TABLE_PRIORITY)));
            t.setDescription(c.getString(c.getColumnIndexOrThrow(TaskSQLHelper.TASK_TABLE_DESCRIPTION)));
            long date= c.getLong(c.getColumnIndex(TaskSQLHelper.TASK_TABLE_DUE_DATE));
            GregorianCalendar gDate= new GregorianCalendar();
            gDate.setTimeInMillis(date);
            t.setDueDate(gDate);
            t.setComment(c.getString(c.getColumnIndex(TaskSQLHelper.TASK_COMMENT)));
            Tag tag= new Tag();
            tag.setName(c.getString(c.getColumnIndexOrThrow(TaskSQLHelper.TASK_TABLE_TAG_NAME)));
            t.setTag(tag);
            tasks.set(i,t);
            i++;
        }

        c.close();
        db.close();
        taskAdapter.notifyDataSetChanged();


        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Task> getTaskList(){
        TaskSQLHelper sqlHelper = new TaskSQLHelper(this, 1);
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        String[] columns = {TaskSQLHelper.TASK_TABLE_PRIORITY, TaskSQLHelper.TASK_TABLE_DESCRIPTION, TaskSQLHelper.TASK_TABLE_TAG_NAME
                ,TaskSQLHelper.TASK_TABLE_DUE_DATE, TaskSQLHelper.TASK_COMMENT };
        Cursor c = db.query(true, TaskSQLHelper.TASK_TABLE_NAME,columns,null,null,null,null,null,null);
        ArrayList<Task> tasks=new ArrayList<>();
        int i=0;
        while(c.moveToNext()){
            Task t=new Task();
            t.setPriority(c.getInt(c.getColumnIndexOrThrow(TaskSQLHelper.TASK_TABLE_PRIORITY)));
            t.setDescription(c.getString(c.getColumnIndexOrThrow(TaskSQLHelper.TASK_TABLE_DESCRIPTION)));
            long date= c.getLong(c.getColumnIndex(TaskSQLHelper.TASK_TABLE_DUE_DATE));
            GregorianCalendar gDate= new GregorianCalendar();
            gDate.setTimeInMillis(date);
            t.setDueDate(gDate);
            t.setComment(c.getString(c.getColumnIndex(TaskSQLHelper.TASK_COMMENT)));
            Tag tag= new Tag();
            tag.setName(c.getString(c.getColumnIndexOrThrow(TaskSQLHelper.TASK_TABLE_TAG_NAME)));
            t.setTag(tag);
//            t.dueDate=c.getString(c.getColumnIndexOrThrow(TaskSQLHelper.TASK_TABLE_DATE_ADDED));
            tasks.add(t);
            i++;
        }

        c.close();
        db.close();
        Collections.reverse(tasks);
        return tasks;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
