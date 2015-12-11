package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Dell on 10/7/2015.
 */
public class NewStudent extends BaseActivity {

    private Toolbar mToolbar;
    String name;
    int age;

    MyDBHandler dbHandler;
    Task task;
    EditText studentName;
    EditText studentAge;
    Notification_bar noti_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Student");

        studentName = (EditText) findViewById(R.id.studentName);
        studentAge = (EditText) findViewById(R.id.studentAge);
        Button addStudentButton = (Button) findViewById(R.id.addStudentButton);
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher");
        Log.i("abcd", "studentName is......" + studentName);

        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = studentName.getText().toString();
                age = Integer.parseInt(studentAge.getText().toString());

                if (name.equals(null) || (age==0)) {
                    Toast.makeText(getApplicationContext(), "Student details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    ParseObject student = new ParseObject("Student");
                    student.put("addedBy", ParseUser.getCurrentUser());
                    student.put("name", name);
                    student.put("age", age);
                  student.saveInBackground();

                    Toast.makeText(getApplicationContext(), "Student details successfully stored", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(NewStudent.this, Students.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
