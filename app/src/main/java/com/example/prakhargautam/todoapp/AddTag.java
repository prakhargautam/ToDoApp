package com.example.prakhargautam.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddTag extends AppCompatActivity {

    Tag tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        tag= new Tag();
        final EditText editText= (EditText) findViewById(R.id.tag_name);
        Button submit= (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().equals("")){
                    tag.setName(editText.getText().toString());
                    Intent data= new Intent();
                    data.putExtra("tag",tag);
                    setResult(RESULT_OK,data);
                    finish();
                }
            }
        });
    }
}
