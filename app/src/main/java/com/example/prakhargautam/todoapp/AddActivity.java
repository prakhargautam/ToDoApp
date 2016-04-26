package com.example.prakhargautam.todoapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class AddActivity extends AppCompatActivity implements TimePickerFragment.TimeSet, DatePickerFragment.DateSet{

    final static int TAG_REQUEST_CODE=1;
    Task task;
    EditText description;
    EditText comment;
    Button addTag;
    Toast mToast;
    int year, month, day, hour, minute;
    Boolean dueDateSet=false, dueTimeSet=false;
    Spinner spinner;
    ArrayAdapter<String> adapter;
    int position;
    Boolean edit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addTag= (Button) findViewById(R.id.add_tag);
        description= (EditText) findViewById(R.id.description);
        comment= (EditText) findViewById(R.id.comment);
        spinner= (Spinner) findViewById(R.id.choose_tag);

        loadSpinnerData();

        spinner.setSelection(0);

        task= new Task();

        Intent intent= getIntent();
        if(intent.getSerializableExtra("edit")!=null){
            task= (Task) intent.getSerializableExtra("edit");
            position= intent.getIntExtra("position",0);
            edit=true;

            description.setText(task.getDescription());
            comment.setText(task.getComment());
        }

        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tagIntent= new Intent();
                tagIntent.setClass(AddActivity.this, AddTag.class);
                startActivityForResult(tagIntent,TAG_REQUEST_CODE);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data= new Intent();
                if(description.getText().toString().equals("")){
                    if(mToast==null){
                        mToast= Toast.makeText(AddActivity.this,"Please enter Description",Toast.LENGTH_SHORT);
                    }
                    mToast.show();
                    return;
                }
                if(!dueDateSet) {
                    if (mToast == null) {
                        mToast = Toast.makeText(AddActivity.this, "Please select Date", Toast.LENGTH_SHORT);
                    }
                    mToast.show();
                    return;
                }
                GregorianCalendar gregorianCalendar= new GregorianCalendar(year,month,day);
                task.setDueDate(gregorianCalendar);
                task.setDescription(description.getText().toString());
                task.setComment(comment.getText().toString());
                Tag tag= new Tag();
                tag.setName(spinner.getSelectedItem().toString());
                task.setTag(tag);
                data.putExtra("task", task);
                if(task==null){
                    Log.i("it's null", "yup");
                }
                if(edit){
                    data.putExtra("position",position);
                }
                setResult(RESULT_OK, data);
                finish();
            }
        });


    }
    public void onRadioButtonClicked(View v){
        boolean checked= ((RadioButton) v).isChecked();

        int id= v.getId();
        switch(id){
            case R.id.high:
                task.setPriority(0);
                break;
            case R.id.normal:
                task.setPriority(1);
                break;
            case R.id.low:
                task.setPriority(2);
                break;
        }

    }
    public void showTimePickerDialog(View v){
        DialogFragment newFragment= new TimePickerFragment();
        newFragment.show(getFragmentManager(),"timePicker");
    }

    public void showDatePickerDialog(View v){
        DialogFragment newFragment= new DatePickerFragment();
        newFragment.show(getFragmentManager(),"datePicker");
    }
    @Override
    public void SetTime(int hour, int minute) {
        this.hour= hour;
        this.minute= minute;
        dueTimeSet=true;
    }

    public void SetDate(int year, int month, int day){
        this.year= year;
        this.month= month;
        this.day= day;
        dueDateSet= true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==TAG_REQUEST_CODE){
                Tag tag= (Tag) data.getSerializableExtra("tag");
                TaskSQLHelper taskSQLHelper= new TaskSQLHelper(this,1);
                SQLiteDatabase db= taskSQLHelper.getWritableDatabase();
                ContentValues values= new ContentValues();
                values.put(TaskSQLHelper.TAG_NAME,tag.getName());
                db.insert(TaskSQLHelper.TAG_TABLE_NAME,null,values);
                db.close();
                loadSpinnerData();
            }
        }
    }

    private void loadSpinnerData(){
        ArrayList<String> tags= new ArrayList<>();


        TaskSQLHelper taskSQLHelper= new TaskSQLHelper(this,1);
        String selectQuery = "SELECT  * FROM " + TaskSQLHelper.TAG_TABLE_NAME;
        SQLiteDatabase db = taskSQLHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                tags.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        adapter= new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,tags);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }
}
