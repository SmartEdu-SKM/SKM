package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dell on 10/7/2015.
 */
public class NewTask extends BaseActivity {

    private Toolbar mToolbar;
    String myTitle;
    String myDesc;
    TextView DATE;
    Button addTaskButton;
    int Year;
    int Month;
    int Day;
    CalendarView calendar;
    ImageButton test;
    Date date1;
    MyDBHandler dbHandler;
    Task task;
    EditText taskTitle;
    EditText taskDescription;
    String role;
    Notification_bar noti_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

       /* mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Task");*/

        taskTitle = (EditText) findViewById(R.id.taskTitle);
        taskDescription = (EditText) findViewById(R.id.taskDescription);
        addTaskButton = (Button) findViewById(R.id.addTaskButton);

        DATE= (TextView) findViewById(R.id.dateText);
        test=(ImageButton)findViewById(R.id.test);

        Log.i("abcd", "tasktitle is......" + taskTitle);
        Bundle fromrole = getIntent().getExtras();
        role = fromrole.getString("role");
        noti_bar = (Notification_bar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role);
        final Date date2 = new Date(Year - 1900, Month, Day + 1);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTitle = taskTitle.getText().toString().trim();
                myDesc = taskDescription.getText().toString();


                if (myTitle.equals("") || myDesc.equals("") || ((DATE.getText().equals("Select Due Date")))) {
                    Toast.makeText(getApplicationContext(), "Event details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    ParseObject task = new ParseObject("Task");
                    task.put("createdBy", ParseUser.getCurrentUser());
                    task.put("addedByRole", role);
                    task.put("TaskName", myTitle);
                    task.put("TaskDescription", myDesc);
                    //String id = task.getObjectId();
                    String string_date = String.valueOf(Day) + "-" + String.valueOf(Month+1) + "-" + String.valueOf(Year);

                    SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                    Date d = null;
                    try {
                        d = f.parse(string_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long milliseconds = d.getTime();
                    task.put("dueDate", milliseconds);
                    task.saveInBackground();
                    Toast.makeText(getApplicationContext(), "Event details successfully stored", Toast.LENGTH_LONG).show();

                    Intent to_tasks = new Intent(NewTask.this, Tasks.class);
                    to_tasks.putExtra("role", role);
                    startActivity(to_tasks);
                    finish();

                   /* ParseQuery<ParseObject> taskQuery = ParseQuery.getQuery("Task");
                    taskQuery.whereEqualTo("TaskName", myTitle);
                    taskQuery.whereEqualTo("createdBy", ParseUser.getCurrentUser());
                    taskQuery.whereEqualTo("addedByRole", role);
                    taskQuery.whereEqualTo("dueDate", milliseconds);
                    taskQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> taskListRet, com.parse.ParseException e) {
                            if (e == null) {
                                ParseObject u = (ParseObject) taskListRet.get(0);
                                String id = u.getObjectId();
                                Toast.makeText(NewTask.this, "id of task selected is = " + id, Toast.LENGTH_LONG).show();
                                Intent to_tasks = new Intent(NewTask.this, Tasks.class);
                                to_tasks.putExtra("id", id);
                                startActivity(to_tasks);
                                finish();
                            } else {
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    }); */

                }
            }
        });


    }

    public void open(View view)
    {

        final Dialog dialog = new Dialog(NewTask.this);
        dialog.setContentView(R.layout.activity_calendar2);
        dialog.setTitle("Select Date");
        calendar= (CalendarView)dialog.findViewById(R.id.calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                Year = year;
                Month = month;
                Day = dayOfMonth;
                date1 = new Date(Year - 1900, Month, Day);
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                DATE.setText(dateFormat.format(date1), TextView.BufferType.EDITABLE);
                Toast.makeText(getApplicationContext(), dayOfMonth + "/" + (Month+1) + "/" + year, Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });
        dialog.show();



    }
}
