package com.example.anshubhardwaj.todolist;

import java.util.Calendar;

public class ToDo {

    private String name;
    private String description;
    private String date;
    private String time;
    private long timeInEpochs;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ToDo(String name, String description,String date, String time) {
        this.name = name;
        this.description = description;
        this.date=date;
        this.time=time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimeInEpochs() {
        return timeInEpochs;
    }

    public void setTimeInEpochs(long timeInEpochs) {
        this.timeInEpochs = timeInEpochs;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInEpochs);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        ++month;

        this.date = day + "/" + month + "/" + year;

        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);

        this.time = hour + ":" + min;

    }
}

