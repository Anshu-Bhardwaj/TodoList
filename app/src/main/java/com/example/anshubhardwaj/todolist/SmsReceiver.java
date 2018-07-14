package com.example.anshubhardwaj.todolist;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {

    String name;
    long timeStamp;
    String description;
    String time;
    String date;

    @RequiresApi(api = Build.VERSION_CODES.M)


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle data = intent.getExtras();


        if(data != null ) {
            Object[] pdus = (Object[]) data.get("pdus");

            for (int i = 0; i < pdus.length; ++i) {

                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i], "3gpp");
                name = smsMessage.getDisplayOriginatingAddress();
                description =smsMessage.getDisplayMessageBody();
                timeStamp = smsMessage.getTimestampMillis();

                Calendar cl = Calendar.getInstance();

                int day=cl.get(Calendar.DAY_OF_MONTH);
                int month=cl.get(Calendar.MONTH)+1;

                int hour=cl.get(Calendar.HOUR_OF_DAY);
                int min=cl.get(Calendar.MINUTE);

                date = "" + day + "/" + month + "/" + cl.get(Calendar.YEAR);
                time = "" + hour + ":" + min;

            }

            ToDo todo = new ToDo(name,description,date,time);
            todo.setTimeInEpochs(timeStamp);

            TodosOpenHelper expensesOpenHelper = TodosOpenHelper.getInstance(context);
            SQLiteDatabase database = expensesOpenHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Todo.COLUMN_NAME, name);
            contentValues.put(Contract.Todo.COLUMN_DESCRIPTION, description);
            contentValues.put(Contract.Todo.DATE_TIME, todo.getTimeInEpochs());
            contentValues.put(Contract.Todo.COLUMN_DATE,date);
            contentValues.put(Contract.Todo.COLUMN_TIME,time);

            long id = database.insert(Contract.Todo.TABLE_NAME, null, contentValues);
            if (id > -1L) {
                Toast.makeText(context, "Todo Added!", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
