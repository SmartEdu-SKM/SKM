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


                  //  final String[] userRef = new String[1];
                    final ParseObject[] users = {new ParseObject("User")};
                    //sa Set up a new Parse user
                   /* final ParseUser user = new ParseUser();
                    user.setUsername(name + rollno);
                    user.setPassword(rollno + name + rollno);*/
                   // user.saveInBackground();
                    ParseUser user=new ParseUser();
                    user.put("username",name+rollno);
                    user.put("password",rollno + name + rollno);
                    user.saveInBackground();

                    Toast.makeText(NewStudent.this, "Student User made ",
                            Toast.LENGTH_LONG).show();

                   /* Parse.User.signUp(username, password).then(function(newUser) {
                        Parse.User.become(sessionToken);
                    });*/


                    ParseQuery<ParseUser> useraddedinfo=ParseQuery.getQuery("User");
                    useraddedinfo.whereEqualTo("username", name + rollno);
                    useraddedinfo.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> usersList, ParseException e) {
                            if (e == null) {
                                if(usersList.size()!=0) {
                                    users[0] = usersList.get(0);
                                    addStudent(users[0]);
                                }else
                                {
                                    Toast.makeText(NewStudent.this, "no user added so no retrieved",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(NewStudent.this, "here",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                    /*user.signUpInBackground(new SignUpCallback() {
                       @Override
                       public void done(ParseException e) {// Handle the response

                           if (e != null) {
                               // Show the error message
                               Toast.makeText(NewStudent.this, e.getMessage(),
                                       Toast.LENGTH_LONG).show();
                           } else {

                               userRef[0] = user.getObjectId();
                               ParseUser.becomeInBackground(sessionToken, new LogInCallback() {
                                   public void done(ParseUser user1, ParseException e) {
                                       if (user1 != null) {
                                           // The current user is now set to user.
                                       } else {
                                           // The token could not be validated.
                                       }
                                   }
                               });
                               // Toast.makeText(NewStudent.this, "Student User made "+ userRef[0]+" "+ParseUser.getCurrentUser().getObjectId(),Toast.LENGTH_LONG).show();
                               //doesnt change back the current user to original here
                               /*Intent refresh=new Intent(NewStudent.this,NewStudent.class);
                                startActivity(refresh);
                               addStudent(userRef[0]);
                           }

                       }
                   });*/

                   /* Intent refresh=new Intent(NewStudent.this,Students.class);
                    refresh.putExtra("id", classId);
                    startActivity(refresh);*/

                            }
                        }
                    });
                }


    protected void addStudent(final ParseObject userRef){


        Toast.makeText(NewStudent.this, "Student User made "+ userRef.getObjectId(),Toast.LENGTH_LONG).show();
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
