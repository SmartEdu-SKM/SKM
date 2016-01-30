package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
    ImageButton imageButton;
    String role;
    Notification_bar noti_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);


        taskTitle = (EditText) findViewById(R.id.taskTitle);

        taskDescription = (EditText) findViewById(R.id.scheduleinfo);

        addTaskButton = (Button) findViewById(R.id.addTaskButton);
        imageButton= (ImageButton) findViewById(R.id.test);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });

        DATE= (TextView) findViewById(R.id.date);
        //test=(ImageButton)findViewById(R.id.test);

        Log.i("abcd", "tasktitle is......" + taskTitle);
        Bundle fromrole = getIntent().getExtras();
        role = fromrole.getString("role");
        institution_code=fromrole.getString("institution_code");
        institution_name=fromrole.getString("institution_name");
        noti_bar = (Notification_bar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institution_name);
        final Date date2 = new Date(Year - 1900, Month, Day + 1);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTitle = taskTitle.getText().toString().trim();
                myDesc = taskDescription.getText().toString();


                if (myTitle.equals("") || myDesc.equals("") || ((DATE.getText().equals("Select Due Date")))) {
                    Toast.makeText(getApplicationContext(), "Task details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    ParseObject task = new ParseObject(TaskTable.TABLE_NAME);
                    task.put(TaskTable.CREATED_BY_USER_REF, ParseUser.getCurrentUser());
                    task.put(TaskTable.BY_USER_ROLE, role);
                    task.put(TaskTable.TABLE_NAME, myTitle);
                    task.put(TaskTable.TASK_DESCRIPTION, myDesc);
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
                    task.put(TaskTable.DUE_DATE, milliseconds);
                    task.saveInBackground();
                    Toast.makeText(getApplicationContext(), "Event details successfully stored", Toast.LENGTH_LONG).show();

                    Intent to_tasks = new Intent(NewTask.this, Tasks.class);
                    to_tasks.putExtra("institution_code",institution_code);
                    to_tasks.putExtra("institution_name",institution_name);
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

    public void open()
    {

        final Dialog dialog = new Dialog(NewTask.this);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Select Date");
        dialog.setContentView(R.layout.activity_calendar2);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);


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
                Toast.makeText(getApplicationContext(), dayOfMonth + "/" + (Month + 1) + "/" + year, Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });
        dialog.show();



    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(NewTask.this,login.class);
            startActivity(nouser);
        }
    }


}
