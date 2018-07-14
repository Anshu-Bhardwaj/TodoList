package com.example.anshubhardwaj.todolist;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AddTodoActivity extends AppCompatActivity {

    EditText titleEditText;
    EditText descriptionEditText;
    EditText dateEditText;
    EditText timeEditText;
    Bundle bundle;
    int year,month,day,hour,min;

    String titleTodo, descriptionTodo, dateTodo, timeTodo;

    public static final int ADD_TODO_RESULT_CODE = 3030;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);


        Intent intent = getIntent();

        bundle = intent.getExtras();

        String action = intent.getAction();
        if (action == intent.ACTION_SEND) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            titleEditText.setText(text);
        }



        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setDate();

            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setTime();
            }
        });


    }

    public boolean save(View view){


        if(validateFields()){

            Intent intent = new Intent();
            intent.putExtra(MainActivity.TITLE_KEY,titleTodo);
            intent.putExtra(MainActivity.DESCRIPTION_KEY,descriptionTodo);
            intent.putExtra(MainActivity.TIME_KEY,timeTodo);
            intent.putExtra(MainActivity.DATE_KEY,dateTodo);
            setResult(ADD_TODO_RESULT_CODE,intent);

            ToDo todo = new ToDo(titleTodo, descriptionTodo, dateTodo, timeTodo);

            TodosOpenHelper openHelper = TodosOpenHelper.getInstance(getApplicationContext());
            SQLiteDatabase database = openHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Todo.COLUMN_NAME, todo.getName());
            contentValues.put(Contract.Todo.COLUMN_DESCRIPTION, todo.getDescription());
            contentValues.put(Contract.Todo.COLUMN_DATE,todo.getDate());
            contentValues.put(Contract.Todo.COLUMN_TIME,todo.getTime());
            contentValues.put(Contract.Todo.DATE_TIME,todo.getTimeInEpochs());

            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,day,hour,min);

            long id = database.insert(Contract.Todo.TABLE_NAME, null, contentValues);


            if (id > -1) {
                todo.setId(id);

            }

            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

            Intent intent1 = new Intent(getApplicationContext(),AlarmReceiver.class);
            intent1.putExtra("id",id);
            PendingIntent pendingIntent =  PendingIntent.getBroadcast(getApplicationContext(),1,intent1,PendingIntent.FLAG_UPDATE_CURRENT);

            long currentTime = calendar.getTimeInMillis();
            alarmManager.set(AlarmManager.RTC_WAKEUP,currentTime,pendingIntent);

            finish();

        }else{

            Toast.makeText(AddTodoActivity.this, "Incomplete details", Toast.LENGTH_SHORT).show();
        }

        return true;


    }


    public void discard(View view) {
        finish();
    }

    private boolean validateFields() {

        titleTodo = titleEditText.getText().toString();
        descriptionTodo = descriptionEditText.getText().toString();
        dateTodo = dateEditText.getText().toString();
        timeTodo = timeEditText.getText().toString();

        if (titleTodo.isEmpty()) {

            titleEditText.setError("Enter title");
            return false;
        }

        if (descriptionTodo.isEmpty()) {

            descriptionEditText.setError("Enter description");
            return false;
        }

        if (dateTodo.isEmpty()) {

            dateEditText.setError("Select date");
            return false;
        }

        if (timeTodo.isEmpty()) {

            timeEditText.setError("Select time");
            return false;
        }

        return true;
    }

    private void setTime() {

        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddTodoActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                timeTodo = hourOfDay + ":" + minute;
                timeEditText.setText(timeTodo);

                AddTodoActivity.this.hour = hourOfDay;
                AddTodoActivity.this.min = minute;

            }
        },hour,min,false);

        timePickerDialog.show();
    }


    private void setDate() {


        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddTodoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                int tempMonth = month + 1;
                dateTodo = dayOfMonth + "/" + tempMonth + "/" + year;
                dateEditText.setText(dateTodo);

                AddTodoActivity.this.day = dayOfMonth;
                AddTodoActivity.this.month  = month;
                AddTodoActivity.this.year  = year;


            }
        },year,month,day);

        datePickerDialog.show();
    }

}

