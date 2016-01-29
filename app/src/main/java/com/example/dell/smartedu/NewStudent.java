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
        institution_code=from_students.getStringExtra("institution_code");
        institution_name=from_students.getStringExtra("institution_name");

        studentName = (EditText) findViewById(R.id.studentName);
        studentAge = (EditText) findViewById(R.id.studentAge);
        studentRno= (EditText) findViewById(R.id.rollno_desc);
        Button addStudentButton = (Button) findViewById(R.id.addStudentButton);
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher",institution_name);
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
                    addStudentUser(name, age, rollno, sessionToken);
                    sleep(3000);
                    addParentUser(name, age, rollno, sessionToken);

                   // addParent(name, rollno);

                }
            }
        });
                }




    protected void sleep(int time)
    {
        for(int x=0;x<time;x++)
        {

        }
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
                  //  Toast.makeText(NewStudent.this, "Student User made " + " " + user_student.getObjectId(), Toast.LENGTH_LONG).show();
                    Log.d("role", "added Student role of " + user_student.getObjectId());
                    ParseObject roleobject = new ParseObject(RoleTable.TABLE_NAME);
                    roleobject.put(RoleTable.OF_USER_REF, user_student);
                    roleobject.put(RoleTable.ROLE, "Student");
                    roleobject.put(RoleTable.ENROLLED_WITH,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                    roleobject.saveInBackground();

                    ParseObject parent=new ParseObject(ParentTable.TABLE_NAME);
                    parent.put(ParentTable.CHILD_USER_REF,user_student);
                    parent.saveEventually();

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





    protected void addParentUser(final String Name,int Age, final int Rollno, final String presession)
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
                   // Toast.makeText(NewStudent.this, "Parent User made "+ " "+user_parent.getObjectId(), Toast.LENGTH_LONG).show();
                    Log.d("role", "added Parent role of " + user_parent.getObjectId());
                    ParseObject roleobject = new ParseObject(RoleTable.TABLE_NAME);
                    roleobject.put(RoleTable.OF_USER_REF, user_parent);
                    roleobject.put(RoleTable.ROLE, "Parent");
                    roleobject.put(RoleTable.ENROLLED_WITH,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                    roleobject.saveInBackground();




                    ParseQuery<ParseUser> user=ParseUser.getQuery();
                    user.whereEqualTo("username",Name +String.valueOf(Rollno));
                    user.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                if (objects.size() != 0) {
                                    Log.d("user", "student user found");
                                    ParseUser student_user = objects.get(0);
                                    ParseQuery<ParseObject> parent_relation = ParseQuery.getQuery(ParentTable.TABLE_NAME);
                                    parent_relation.whereEqualTo(ParentTable.CHILD_USER_REF, student_user);
                                    parent_relation.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e == null) {
                                                if (objects.size() != 0) {
                                                    objects.get(0).put(ParentTable.PARENT_USER_REF, user_parent);
                                                    objects.get(0).saveEventually();
                                                } else {

                                                }

                                            } else {
                                                Log.d("user", "relation not found");
                                            }
                                        }
                                    });
                                } else {
                                    Log.d("user", "query logic error in student...size = " + objects.size());
                                }
                            } else {
                                Log.d("user", "no student");
                            }
                        }
                    });





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
       // Toast.makeText(NewStudent.this, "Student User made "+ userRef+" "+ParseUser.getCurrentUser().getObjectId(),Toast.LENGTH_LONG).show();
        final ParseObject[] classRef = new ParseObject[1];
        final ParseQuery<ParseObject> classQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        classQuery.whereEqualTo(ClassTable.OBJECT_ID, classId);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                    classRef[0] = studentListRet.get(0);

                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(StudentTable.TABLE_NAME);
                    studentQuery.whereEqualTo(StudentTable.CLASS_REF, classRef[0]);
                    studentQuery.whereEqualTo(StudentTable.ROLL_NUMBER, rollno);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, ParseException e) {
                            if (e == null) {

                                if (studentListRet.size() == 0) {
                                    ParseObject student = new ParseObject(StudentTable.TABLE_NAME);
                                    student.put(StudentTable.ADDED_BY_USER_REF, ParseUser.getCurrentUser());
                                    student.put(StudentTable.STUDENT_NAME, name);
                                    student.put(StudentTable.STUDENT_AGE, age);
                                    student.put(StudentTable.ROLL_NUMBER, rollno);
                                    student.put(StudentTable.CLASS_REF, classRef[0]);
                                    student.put(StudentTable.STUDENT_USER_REF, userRef);
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


/*
    protected void addParent(final String Name, final int Rollno)
    {
        Log.d("user", "parent child realtion");
        ParseQuery<ParseUser> user=ParseUser.getQuery();
        user.whereEqualTo("username", "parent_" + Name + Rollno);
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
                                    }else
                                    {
                                        Log.d("user", "query logic error in student");
                                    }
                                }else
                                {
                                    Log.d("user", "no student");
                                }
                            }
                        });
                    }else
                    {
                        Log.d("user", "query logic error in parent");
                    }
                }else
                {
                    Log.d("user", "no parent");
                }
            }
        });
    }
*/
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
