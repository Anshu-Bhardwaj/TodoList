package com.example.anshubhardwaj.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class TodosOpenHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "expenses_db";
    public static final int VERSION = 1;


    private static TodosOpenHelper instance;

    public static TodosOpenHelper getInstance(Context context){
        if(instance == null){
            instance = new TodosOpenHelper(context.getApplicationContext());
        }
        return instance;
    }


    private TodosOpenHelper(Context context) {

        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String expensesSql = "CREATE TABLE " + Contract.Todo.TABLE_NAME  + " (  " +
                Contract.Todo.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                Contract.Todo.COLUMN_NAME  + " TEXT , " +
                Contract.Todo.COLUMN_DESCRIPTION + " TEXT , " +
                Contract.Todo.DATE_TIME  + " NUMERIC , " +
                Contract.Todo.COLUMN_DATE + " TEXT , " +
                Contract.Todo.COLUMN_TIME + " TEXT ) ";

        sqLiteDatabase.execSQL(expensesSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<ToDo> getAll(){

        ArrayList<ToDo> toDos = new ArrayList<>();
        String[] columns = {Contract.Todo.COLUMN_ID,Contract.Todo.COLUMN_NAME, Contract.Todo.COLUMN_DESCRIPTION, Contract.Todo.DATE_TIME,Contract.Todo.COLUMN_DATE,Contract.Todo.COLUMN_TIME};
        Cursor cursor = this.getReadableDatabase().query(Contract.Todo.TABLE_NAME, columns, null, null, null, null, Contract.Todo.DATE_TIME);


        if(cursor.moveToNext()){
            do{
                ToDo todo = new ToDo(cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(Contract.Todo.COLUMN_TIME)));


                todo.setTimeInEpochs(cursor.getLong(cursor.getColumnIndex(Contract.Todo.DATE_TIME)));

                long id = cursor.getInt(cursor.getColumnIndex(Contract.Todo.COLUMN_ID));
                todo.setId(id);

                toDos.add(todo);
                Log.d("chekkkk",todo.getDate() + " " + todo.getName());

            }while(cursor.moveToNext());

        }

        cursor.close();
        return toDos;
    }

    public static void addToDatabase(SQLiteDatabase db ,String tableName , ToDo item){
        ContentValues values = new ContentValues();
        values.put(Contract.Todo.COLUMN_NAME, item.getName());
        values.put(Contract.Todo.COLUMN_DESCRIPTION,item.getDescription());
        values.put(Contract.Todo.COLUMN_DATE, item.getDate());
        values.put(Contract.Todo.COLUMN_TIME,item.getTime());
        values.put(Contract.Todo.DATE_TIME,item.getTimeInEpochs());
        // values.put(Contract.Todo.COLUMN_ID,item.getId());
        db.insert(tableName, null, values);
    }
}
