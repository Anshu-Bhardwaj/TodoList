package com.example.anshubhardwaj.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener {

    public static final String TITLE_KEY = "title";
    public static final String DESCRIPTION_KEY = "description";
    public static final String DATE_KEY = "date";
    public static final String TIME_KEY = "time";
    public static final String ID_KEY = "id";


    public int pos;

    ArrayList<ToDo> todos = new ArrayList<>();
    ToDo todo;
    TodoAdapter adapter;
    Cursor cursor;
    BroadcastReceiver receiver;
    ListView listview;


    public static final int DETAILS_REQUEST_CODE = 1011;
    public static final int ADD_TODO_REQUEST_CODE=3010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.listview);

        adapter = new TodoAdapter(this, todos);
        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(this);
        listview.setOnItemClickListener(this);

        fetchData();

//        for (int i = 0; i < 20; i++) {
//
//            ToDo todo = new ToDo("Expense " + i, i * 100 + "", "27/06/2018", "12:08");
//            todos.add(todo);
//        }




    }

    public void fetchData(){
        TodosOpenHelper openHelper = TodosOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database = openHelper.getWritableDatabase();

        cursor = database.query(Contract.Todo.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext() && cursor!=null) {
            String title = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DATE));
            String time = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_TIME));

            long id = cursor.getLong(cursor.getColumnIndex(Contract.Todo.COLUMN_ID));

            todo = new ToDo(title, description,date,time);
            todo.setId(id);
            todos.add(todo);

        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idMenu=item.getItemId();
      if(idMenu==R.id.permissionGrant){
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }

        else if(idMenu==R.id.sendFeedback){
            Intent intent= new Intent();
            intent.setAction(Intent.ACTION_SENDTO);
            Uri uri= Uri.parse("mailto:anshu.msit@gmail.com");
            intent.setData(uri);
            startActivity(intent);
        }

        else if(idMenu==R.id.aboutUs){
            Intent intent= new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri= Uri.parse("https://en.todoist.com/");
            intent.setData(uri);
            startActivity(intent);
        }

        else {

            todos.clear();
            TodosOpenHelper openHelper = TodosOpenHelper.getInstance(getApplicationContext());
            SQLiteDatabase database = openHelper.getReadableDatabase();

            if (idMenu == R.id.byName) {
                cursor = database.query(Contract.Todo.TABLE_NAME, null, null, null, null, null, Contract.Todo.COLUMN_NAME);

                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_NAME));
                    String description = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DESCRIPTION));
                    String date = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DATE));
                    String time = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_TIME));


                    long id = cursor.getLong(cursor.getColumnIndex(Contract.Todo.COLUMN_ID));

                    todo = new ToDo(title, description,date,time);
                    todo.setId(id);
                    todos.add(todo);
                    adapter.notifyDataSetChanged();

                }
                cursor.close();

            } else if (idMenu == R.id.byDate) {
                cursor = database.query(Contract.Todo.TABLE_NAME, null, null, null, null, null, Contract.Todo.COLUMN_DATE);

                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_NAME));
                    String description = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DESCRIPTION));
                    String date = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DATE));
                    String time = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_TIME));


                    long id = cursor.getLong(cursor.getColumnIndex(Contract.Todo.COLUMN_ID));

                    todo = new ToDo(title, description,date,time);
                    todo.setId(id);
                    todos.add(todo);
                    adapter.notifyDataSetChanged();

                }
                cursor.close();

            }
            else if (idMenu == R.id.byTime) {
                cursor = database.query(Contract.Todo.TABLE_NAME, null, null, null, null, null, Contract.Todo.COLUMN_TIME);

                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_NAME));
                    String description = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DESCRIPTION));
                    String date = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DATE));
                    String time = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_TIME));


                    long id = cursor.getLong(cursor.getColumnIndex(Contract.Todo.COLUMN_ID));

                    todo = new ToDo(title, description,date,time);
                    todo.setId(id);
                    todos.add(todo);
                    adapter.notifyDataSetChanged();

                }
                cursor.close();

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETAILS_REQUEST_CODE && resultCode == ShowDetailsActivity.DETAILS_RESULT_CODE ) {

            todos.clear();
            fetchData();

        } else if (requestCode == ADD_TODO_REQUEST_CODE && resultCode == AddTodoActivity.ADD_TODO_RESULT_CODE) {


            TodosOpenHelper openHelper = TodosOpenHelper.getInstance(getApplicationContext());
            SQLiteDatabase database = openHelper.getWritableDatabase();

            todos.clear();
            cursor = database.query(Contract.Todo.TABLE_NAME, null, null, null, null, null, null);

            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DATE));
                String time = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_TIME));

                long id = cursor.getLong(cursor.getColumnIndex(Contract.Todo.COLUMN_ID));

                todo = new ToDo(title, description, date, time);
                todo.setId(id);
                todos.add(todo);
                adapter.notifyDataSetChanged();
            }
            cursor.close();

        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {

        final ToDo todo = todos.get(i);


        final int position = i;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setCancelable(false);
        builder.setMessage("Do you really want to delete " + todo.getName() + "?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Removing from db
                TodosOpenHelper openHelper= TodosOpenHelper.getInstance(getApplicationContext());
                SQLiteDatabase database= openHelper.getWritableDatabase();
                long id=todo.getId();
                String[] selectionArgs={id + ""};
                database.delete(Contract.Todo.TABLE_NAME, Contract.Todo.COLUMN_ID + " = ? ", selectionArgs);

                //Removing from list
                todos.remove(position);
                adapter.notifyDataSetChanged();
                //Toast.makeText(MainActivity.this,todo.getName()+" removed.", Toast.LENGTH_LONG).show();
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent1 = new Intent(MainActivity.this,AlarmReceiver.class);
                PendingIntent pendingIntent =  PendingIntent.getBroadcast(getApplicationContext(),1,intent1,0);
                alarmManager.cancel(pendingIntent);

                Snackbar.make(listview,"Todo removed..",Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       String name = todo.getName();
                       String description = todo.getDescription();
                       String date = todo.getDate();
                       String time = todo.getTime();
                       long id= todo.getId();

                        TodosOpenHelper openHelper= TodosOpenHelper.getInstance(getApplicationContext());
                        SQLiteDatabase database= openHelper.getWritableDatabase();

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Todo.COLUMN_NAME, name);
                        contentValues.put(Contract.Todo.COLUMN_DESCRIPTION, description);
                        contentValues.put(Contract.Todo.COLUMN_DATE,date);
                        contentValues.put(Contract.Todo.COLUMN_TIME,time);
                        contentValues.put(Contract.Todo.COLUMN_ID,todo.getId());
                        contentValues.put(Contract.Todo.DATE_TIME,todo.getTimeInEpochs());

                        long id1 = database.insert(Contract.Todo.TABLE_NAME, null, contentValues);
                        ToDo todo= new ToDo(name,description,date,time);
                        todo.setId(id);
                        if(position>= todos.size()){
                            todos.add(todo);
                        }
                        else{
                            todos.add(todo);
                            for(int i=todos.size()-1; i>position;i--){
                                todos.set(i,todos.get(i-1));
                            }
                            todos.set(position,todo);
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this,todo.getName()+" restored.", Toast.LENGTH_LONG).show();
                    }
                }).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        pos=i;
        final ToDo todo = todos.get(i);
        Intent intent = new Intent(getApplicationContext(), ShowDetailsActivity.class);
        intent.setAction("view from list");
        Bundle b = new Bundle();

        b.putString(TITLE_KEY, todo.getName());
        b.putString(DESCRIPTION_KEY, todo.getDescription());
        b.putString(TIME_KEY,todo.getTime());
        b.putString(DATE_KEY,todo.getDate());
        b.putLong(ID_KEY,todo.getId());

        intent.putExtras(b);
        startActivityForResult(intent,DETAILS_REQUEST_CODE);

    }

    public  void addData(View view){
        Intent intent=new Intent(this, AddTodoActivity.class);
        startActivityForResult(intent,ADD_TODO_REQUEST_CODE);
    }

}

