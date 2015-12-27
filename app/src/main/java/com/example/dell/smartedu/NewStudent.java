package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class NewStudent extends BaseActivity {

    private Toolbar mToolbar;
    String name;
    int age;
    int rollno= -1;

    MyDBHandler dbHandler;
    Task task;
    EditText studentName;
    EditText studentAge;
    EditText studentRno;
    Notification_bar noti_bar;
    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Student");

        Intent from_students = getIntent();
        classId = from_students.getStringExtra("id");

        studentName = (EditText) findViewById(R.id.studentName);
        studentAge = (EditText) findViewById(R.id.studentAge);
        studentRno= (EditText) findViewById(R.id.rollno_desc);
        Button addStudentButton = (Button) findViewById(R.id.addStudentButton);
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher");
        Log.i("abcd", "studentName is......" + studentName);

        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = studentName.getText().toString().trim();
                age = Integer.parseInt(studentAge.getText().toString().trim());
                rollno = Integer.parseInt(studentRno.getText().toString().trim());

                if (name.equals(null) || (age==0) || (rollno== -1)){
                    Toast.makeText(getApplicationContext(), "Student details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {

                    final String sessionToken = ParseUser.getCurrentUser().getSessionToken();

                    addParentUser(name, age, rollno, sessionToken);
                    addStudentUser(name, age, rollno, sessionToken);

                            }
                        }
                    });
                }




    protected void addStudentUser(String Name,int Age,int Rollno, final String presession)
    {
        final ParseUser[] userRef = {new ParseUser()};
        // Set up a new Parse user
        final ParseUser user = new ParseUser();
        user.setUsername(Name + Rollno);
        user.setPassword(Rollno + Name + Rollno);



                   /* Parse.User.signUp(username, password).then(function(newUser) {
                        Parse.User.become(sessionToken);
                    });*/


        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {// Handle the response

                if (e != null) {
                    // Show the error message
                    Toast.makeText(NewStudent.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    userRef[0] = user;
                    Log.d("role", "added Student role by " + ParseUser.getCurrentUser().getObjectId());
                    ParseObject roleobject = new ParseObject("Role");
                    roleobject.put("createdBy",ParseUser.getCurrentUser());
                    roleobject.put("roleName", "Student");
                    roleobject.saveInBackground();

                    try {

                        Toast.makeText(NewStudent.this, "Student User made "+ " "+ParseUser.getCurrentUser().getObjectId(),
                                Toast.LENGTH_LONG).show();
                        ParseUser.become(presession);
                        addStudent(userRef[0]);
                    } catch (ParseException e1) {
                        Toast.makeText(NewStudent.this, "cant add student",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

    }





    protected void addParentUser(String Name,int Age,int Rollno, final String presession)
    {
        final ParseUser[] userRef = {new ParseUser()};
        // Set up a new Parse user
        final ParseUser user = new ParseUser();
        user.setUsername("parent_" + Name + Rollno);
        user.setPassword(Rollno + Name + Rollno);



                   /* Parse.User.signUp(username, password).then(function(newUser) {
                        Parse.User.become(sessionToken);
                    });*/


        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {// Handle the response

                if (e != null) {
                    // Show the error message
                    Toast.makeText(NewStudent.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    userRef[0] = user;
                    Toast.makeText(NewStudent.this, "Parent User made "+ " "+ParseUser.getCurrentUser().getObjectId(),
                            Toast.LENGTH_LONG).show();
                   // addRole("Parent", ParseUser.getCurrentUser().getObjectId());
                    Log.d("role", "added Parent role by " + ParseUser.getCurrentUser().getObjectId());
                    ParseObject roleobject = new ParseObject("Role");
                    roleobject.put("createdBy",ParseUser.getCurrentUser());
                    roleobject.put("roleName", "Parent");
                    roleobject.saveInBackground();
                    try {

                        ParseUser.become(presession);
                    } catch (ParseException e1) {
                        Toast.makeText(NewStudent.this,"cant add parent",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

    }



    protected void addStudent(final ParseUser userRef){


        Toast.makeText(NewStudent.this, "Student User made "+ userRef+" "+ParseUser.getCurrentUser().getObjectId(),Toast.LENGTH_LONG).show();
        final ParseObject[] classRef = new ParseObject[1];
        final ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
        classQuery.whereEqualTo("objectId",classId);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                    classRef[0] = studentListRet.get(0);

                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                    studentQuery.whereEqualTo("class", classRef[0]);
                    studentQuery.whereEqualTo("rollNumber", rollno);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, ParseException e) {
                            if (e == null) {

                                if(studentListRet.size()==0){
                                    ParseObject student = new ParseObject("Student");
                                    student.put("addedBy", ParseUser.getCurrentUser());
                                    student.put("name", name);
                                    student.put("age", age);
                                    student.put("rollNumber", rollno);
                                    student.put("class",classRef[0]);
                                    student.put("userId",userRef);
                                    student.saveInBackground();

                                    Toast.makeText(getApplicationContext(), "Student details successfully stored", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(NewStudent.this, Students.class);
                                    i.putExtra("id",classId);
                                    startActivity(i);
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Unique Roll Number in a class Constraint Violated", Toast.LENGTH_LONG).show();
                                }

                            }

                            else {
                                //Toast.makeText(NewStudent.this, "errorInner", Toast.LENGTH_LONG).show();
                                Log.d("user", "ErrorInner: " + e.getMessage());
                            }
                        }
                    });


                } else {
                    //Toast.makeText(NewStudent.this, "errorOuter", Toast.LENGTH_LONG).show();
                    Log.d("user", "ErrorOuter: " + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(NewStudent.this,login.class);
            startActivity(nouser);
        }
    }

            }
