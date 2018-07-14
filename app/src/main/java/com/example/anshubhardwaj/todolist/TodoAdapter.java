package com.example.anshubhardwaj.todolist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TodoAdapter extends ArrayAdapter {

    ArrayList<ToDo> items;
    LayoutInflater inflater;

    public TodoAdapter(@NonNull Context context, ArrayList<ToDo> items) {
        super(context, 0, items);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View output = convertView;
        if (output == null) {
            output = inflater.inflate(R.layout.todo_row_layout, parent, false);
            TextView titleTextView = output.findViewById(R.id.todoTitle);
            TodoViewHolder viewHolder = new TodoViewHolder();
            viewHolder.title = titleTextView;
            output.setTag(viewHolder);
        }
        TodoViewHolder viewHolder = (TodoViewHolder) output.getTag();
        ToDo todo = items.get(position);
        viewHolder.title.setText(todo.getName());
        return output;

    }
}



