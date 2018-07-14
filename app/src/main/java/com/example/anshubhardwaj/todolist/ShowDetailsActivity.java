package com.example.anshubhardwaj.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowDetailsActivity extends AppCompatActivity {

    TextView todoTitleTextView;
    TextView todoDescriptionTextView;
    TextView todoDateTextView;
    TextView todoTimeTextView;
    long id;

    public static final int DETAILS_RESULT_CODE = 1012;
    public static final int DETAILS_REQUEST_CODE = 2012;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        todoTitleTextView = findViewById(R.id.todoTitleTextView);
        todoDescriptionTextView = findViewById(R.id.todoDescriptionTextView);
        todoDescriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        todoDateTextView = findViewById(R.id.todoDateTextView);
        todoTimeTextView = findViewById(R.id.todoTimeTextView);

        Intent intent = getIntent();

        id = intent.getLongExtra("id",-1);


        if(id != -1){

            getAndSetData();
        }
    }



    private void getAndSetData() {

        TodosOpenHelper openHelper = TodosOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database = openHelper.getReadableDatabase();
        String[] selectionArgs = {id + ""};

        String[] columns = {Contract.Todo.COLUMN_ID,Contract.Todo.COLUMN_NAME, Contract.Todo.COLUMN_DESCRIPTION, Contract.Todo.DATE_TIME,Contract.Todo.COLUMN_DATE,Contract.Todo.COLUMN_TIME};
        Cursor cursor = database.query(Contract.Todo.TABLE_NAME,columns,  Contract.Todo.COLUMN_ID + " = ?",selectionArgs,null,null,null);

        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DATE));
            String time = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_TIME));

            ToDo todo = new ToDo(name,description,date,time);
            todo.setTimeInEpochs(cursor.getLong(cursor.getColumnIndex(Contract.Todo.DATE_TIME)));

            todoTitleTextView.setText(name);
            todoDescriptionTextView.setText(description);
            todoDateTextView.setText(date);

            todoTimeTextView.setText(time);
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == DETAILS_REQUEST_CODE && resultCode == EditTodoActivity.EDIT_TODO_RESULT_CODE) {

            if (data != null) {
                id = data.getLongExtra("id",-1);

                if (id != -1) {

                    getAndSetData();
                    setResult(DETAILS_RESULT_CODE,data);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.edit){

            Intent  intent = new Intent(this,EditTodoActivity.class);
            intent.putExtra("id",id);
            startActivityForResult(intent,DETAILS_REQUEST_CODE);
        }

        return true;

    }
}

