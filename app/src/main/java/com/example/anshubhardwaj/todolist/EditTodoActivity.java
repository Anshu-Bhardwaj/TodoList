package com.example.anshubhardwaj.todolist;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class EditTodoActivity extends AppCompatActivity {

    EditText titleEditText,descriptionEditText,dateEditText,timeEditText;
    String titleTodo,descriptionTodo,dateTodo,timeTodo;
    Bundle bundle;
    long id;
    int year,month,day,hour,min;


    public static final int EDIT_TODO_RESULT_CODE = 1013;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);


        titleEditText = findViewById(R.id.addExpense_TitleEditText);
        descriptionEditText = findViewById(R.id.addExpense_DescriptionEditText);
        dateEditText = findViewById(R.id.addExpense_DateEditText);
        timeEditText = findViewById(R.id.addExpense_TimeEditText);


        Intent intent = getIntent();

        id = intent.getLongExtra("id",-1);

        if(id != -1) {

            TodosOpenHelper openHelper = TodosOpenHelper.getInstance(getApplicationContext());
            SQLiteDatabase database = openHelper.getReadableDatabase();
            String[] selectionArgs = {id + ""};

            String[] columns = {Contract.Todo.COLUMN_ID,Contract.Todo.COLUMN_NAME, Contract.Todo.COLUMN_DESCRIPTION, Contract.Todo.DATE_TIME,Contract.Todo.COLUMN_DATE,Contract.Todo.COLUMN_TIME};
            Cursor cursor = database.query(Contract.Todo.TABLE_NAME, columns, Contract.Todo.COLUMN_ID + " = ?", selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DATE));
                String time = cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_TIME));


                ToDo todo = new ToDo(name,description,date,time);
                todo.setTimeInEpochs(cursor.getLong(cursor.getColumnIndex(Contract.Todo.DATE_TIME)));

                titleEditText.setText(name);
                descriptionEditText.setText(description);
                dateEditText.setText(date);
                timeEditText.setText(time);

                String[] dateComp = todo.getDate().split("/");
                String[] timeComp = todo.getTime().split(":");

                day = Integer.parseInt(dateComp[0]);
                month = Integer.parseInt(dateComp[1]);
                year = Integer.parseInt(dateComp[2]);

                hour = Integer.parseInt(timeComp[0]);
                min = Integer.parseInt(timeComp[1]);
            }
            cursor.close();

        }else{

            String action = intent.getAction();

            if(action == Intent.ACTION_SEND){

                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                titleEditText.setText(text);

            }
        }

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDate();

            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTime();
            }
        });

    }

    public void discard(View view){
        finish();
    }

    public void save(View view) {
        if(validateFields()){

                Intent intent = new Intent();

                TodosOpenHelper openHelper = TodosOpenHelper.getInstance(getApplicationContext());
                SQLiteDatabase database = openHelper.getWritableDatabase();

                String[] selectionArgs = {id + ""};

                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Todo.COLUMN_NAME,titleTodo);
                contentValues.put(Contract.Todo.COLUMN_DESCRIPTION,descriptionTodo);
                contentValues.put(Contract.Todo.COLUMN_DATE,dateTodo);
                contentValues.put(Contract.Todo.COLUMN_TIME,timeTodo);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,day,hour,min);

                contentValues.put(Contract.Todo.DATE_TIME,calendar.getTimeInMillis());

                database.update(Contract.Todo.TABLE_NAME,contentValues,Contract.Todo.COLUMN_ID + " = ?",selectionArgs);

                intent.putExtra("id",id);

                setResult(EDIT_TODO_RESULT_CODE,intent);


                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

                Intent intent1 = new Intent(getApplicationContext(),AlarmReceiver.class);
                intent1.putExtra("id",id);
                PendingIntent pendingIntent =  PendingIntent.getBroadcast(getApplicationContext(),1,intent1,PendingIntent.FLAG_UPDATE_CURRENT);

                long currentTime = calendar.getTimeInMillis();
                alarmManager.set(AlarmManager.RTC_WAKEUP,currentTime,pendingIntent);

                finish();



            }else{

                Toast.makeText(EditTodoActivity.this, "Incomplete details", Toast.LENGTH_SHORT).show();
            }

    }

    private boolean validateFields() {

        titleTodo = titleEditText.getText().toString().trim();
        descriptionTodo = descriptionEditText.getText().toString().trim();
        dateTodo = dateEditText.getText().toString();
        timeTodo = timeEditText.getText().toString();

        if(titleTodo.isEmpty()){

            titleEditText.setError("Enter title");
            return false;
        }

        if(descriptionTodo.isEmpty()){

            descriptionEditText.setError("Enter description");
            return false;
        }

        if(dateTodo.isEmpty()){

            dateEditText.setError("Select date");
            return false;
        }

        if(timeTodo.isEmpty()){

            timeEditText.setError("Select time");
            return false;
        }

        return true;
    }

    private void setTime() {

        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(EditTodoActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                timeTodo = hourOfDay + ":" + minute;
                timeEditText.setText(timeTodo);

                EditTodoActivity.this.hour = hourOfDay;
                EditTodoActivity.this.min = minute;

            }
        },hour,min,false);

        timePickerDialog.show();
    }

    private void setDate() {


        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditTodoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                int tempMonth = month + 1;
                dateTodo = dayOfMonth + "/" + tempMonth + "/" + year;
                dateEditText.setText(dateTodo);

                EditTodoActivity.this.day = dayOfMonth;
                EditTodoActivity.this.month  = month;
                EditTodoActivity.this.year  = year;

            }
        },year,month,day);

        datePickerDialog.show();
    }
}
