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
    String parentId;
    String studentId;

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

                if (name.equals(null) || (age == 0) || (rollno == -1)) {
                    Toast.makeText(getApplicationContext(), "Student details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {

                    final String sessionToken = ParseUser.getCurrentUser().getSessionToken();
                    addParentUser(name, age, rollno, sessionToken);
                    addStudentUser(name, age, rollno, sessionToken);
                    addParent(name,rollno);

                }
            }
        });
                }





    protected void addStudentUser(final String Name,int Age, final int Rollno, final String presession)
    {
        final ParseUser[] userRef = {new ParseUser()};
        // Set up a new Parse user
        final ParseUser user_student = new ParseUser();
        user_student.setUsername(Name + Rollno);
        user_student.setPassword(Rollno + Name + Rollno);


        user_student.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {// Handle the response

                if (e != null) {
                    // Show the error message
                    Toast.makeText(NewStudent.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    userRef[0] = user_student;
                    Toast.makeText(NewStudent.this, "Student User made " + " " + user_student.getObjectId(),
                            Toast.LENGTH_LONG).show();
                    Log.d("role", "added Student role of " + user_student.getObjectId());
                    ParseObject roleobject = new ParseObject("Role");
                    roleobject.put("createdBy", user_student);
                    roleobject.put("roleName", "Student");
                    roleobject.saveInBackground();

                    try {

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
        final ParseUser user_parent = new ParseUser();
        user_parent.setUsername("parent_" + Name + Rollno);
        user_parent.setPassword(Rollno + Name + Rollno);


        user_parent.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {// Handle the response

                if (e != null) {
                    // Show the error message
                    Toast.makeText(NewStudent.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    userRef[0] = user_parent;
                    Toast.makeText(NewStudent.this, "Parent User made "+ " "+user_parent.getObjectId(),
                            Toast.LENGTH_LONG).show();
                    Log.d("role", "added Parent role of " + user_parent.getObjectId());
                    ParseObject roleobject = new ParseObject("Role");
                    roleobject.put("createdBy",user_parent);
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
        classQuery.whereEqualTo("objectId", classId);
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

                                if (studentListRet.size() == 0) {
                                    ParseObject student = new ParseObject("Student");
                                    student.put("addedBy", ParseUser.getCurrentUser());
                                    student.put("name", name);
                                    student.put("age", age);
                                    student.put("rollNumber", rollno);
                                    student.put("class", classRef[0]);
                                    student.put("userId", userRef);
                                    student.saveInBackground();

                                    Toast.makeText(getApplicationContext(), "Student details successfully stored", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(NewStudent.this, Students.class);
                                    i.putExtra("id", classId);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Unique Roll Number in a class Constraint Violated", Toast.LENGTH_LONG).show();
                                }

                            } else {
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



    protected void addParent(final String Name, final int Rollno)
    {
        ParseQuery<ParseUser> user=ParseUser.getQuery();
        user.whereEqualTo("username","parent_"+ Name + Rollno);
        user.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parentobjects, ParseException e) {
                if(e==null) {
                    if(parentobjects.size()!=0) {
                        Log.d("user", "parent found");
                        final ParseUser user_parent =parentobjects.get(0);
                        ParseQuery<ParseUser> user=ParseUser.getQuery();
                        user.whereEqualTo("username",Name+Rollno);
                        user.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                if(e==null) {
                                    if(objects.size()!=0) {
                                        Log.d("user", "student found");
                                        ParseUser user_student =objects.get(0);
                                        ParseObject parent=new ParseObject("Parent");
                                        parent.put("userId",user_parent);
                                        parent.put("child",user_student);
                                        parent.saveEventually();
                                    }
                                }else
                                {
                                    Log.d("user", "no student");
                                }
                            }
                        });
                    }
                }else
                {
                    Log.d("user", "no parent");
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
