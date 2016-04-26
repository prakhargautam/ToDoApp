package com.example.prakhargautam.todoapp;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prakhargautam on 24/04/16.
 */
public class TaskAdapter extends ArrayAdapter<Task> {

    Context context;
    ArrayList<Task> tasks;

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
        this.context=context;
        this.tasks= tasks;
    }

    public static class ViewHolder
    {
        ImageView priority;
        TextView description;
        TextView tag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=convertView.inflate(context,R.layout.task, null);
            ViewHolder vh= new ViewHolder();
            vh.priority= (ImageView) convertView.findViewById(R.id.priority);
            vh.tag= (TextView) convertView.findViewById(R.id.tag);
            vh.description= (TextView) convertView.findViewById(R.id.description);
            convertView.setTag(vh);
        }
        ViewHolder vh= (ViewHolder) convertView.getTag();
        switch(tasks.get(position).priority){
            case 0:
                vh.priority.setBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            case 1:
                vh.priority.setBackgroundColor(context.getResources().getColor(R.color.orange));
                break;
            case 2:
                vh.priority.setBackgroundColor(context.getResources().getColor(R.color.green));
        }
        vh.description.setText(tasks.get(position).description);
        vh.tag.setText(tasks.get(position).getTag().getName());

        return convertView;
    }
}
