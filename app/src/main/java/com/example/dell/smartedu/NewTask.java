package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseRole;
import com.parse.ParseUser;

/**
 * Created by Dell on 10/7/2015.
 */
public class NewTask extends ActionBarActivity {

    private Toolbar mToolbar;
    String myTitle;
    String myDesc;

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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Task");

        taskTitle = (EditText) findViewById(R.id.taskTitle);
        taskDescription = (EditText) findViewById(R.id.taskDescription);
        Button addTaskButton = (Button) findViewById(R.id.addTaskButton);

        Log.i("abcd", "tasktitle is......" + taskTitle);
        Bundle fromrole=getIntent().getExtras();
        role= fromrole.getString("role");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role);
                addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTitle = taskTitle.getText().toString().trim();
                myDesc = taskDescription.getText().toString();

                if (myTitle.equals(null) || myDesc.equals(null)) {
                    Toast.makeText(getApplicationContext(), "Task details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    ParseObject task = new ParseObject("Task");
                    task.put("createdBy", ParseUser.getCurrentUser());
                    task.put("addedByRole",role);
                    task.put("TaskName", myTitle);
                    task.put("TaskDescription", myDesc);
                    task.saveInBackground();
                    Toast.makeText(getApplicationContext(), "Task details successfully stored", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(NewTask.this, Tasks.class);
                    i.putExtra("role",role);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
