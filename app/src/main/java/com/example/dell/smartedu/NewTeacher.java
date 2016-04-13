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
public class NewTeacher extends BaseActivity {

    private Toolbar mToolbar;
    String name;

    int age=0;
    int serial=-1;
    Button addTeacherButton;

    MyDBHandler dbHandler;
    Task task;
    EditText teacherName;
    EditText teacherAge;
    EditText teacherSerial;

    Notification_bar noti_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_teacher);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Student");

        Intent from_students = getIntent();
        role = from_students.getStringExtra("role");
        institution_code=from_students.getStringExtra("institution_code");
        institution_name=from_students.getStringExtra("institution_name");

        teacherName = (EditText) findViewById(R.id.teacherName);
        teacherAge = (EditText) findViewById(R.id.teacherAge);
        teacherSerial=(EditText) findViewById(R.id.teacherSerial);

        addTeacherButton = (Button) findViewById(R.id.addTeacherButton);
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institution_name);


        addTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = teacherName.getText().toString().trim();
                if(!teacherAge.getText().toString().trim().equals(""))
                age = Integer.parseInt(teacherAge.getText().toString().trim());
                if(!teacherSerial.getText().toString().trim().equals(""))
                serial = Integer.parseInt(teacherSerial.getText().toString().trim());


                if (name.equals(null) || (age == 0) || (serial == -1)) {
                    Toast.makeText(getApplicationContext(), "Teacher details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {

                    final String sessionToken = ParseUser.getCurrentUser().getSessionToken();
                    addTeacherUser(name, age, sessionToken);
                    sleep(3000);

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
    protected void addTeacherUser(final String Name,int Age, final String presession)
    {
        final ParseUser[] userRef = {new ParseUser()};
        // Set up a new Parse user
        final ParseUser user_teacher = new ParseUser();
        user_teacher.setUsername(Name + institution_name);
        user_teacher.setPassword(institution_name + Name + institution_name);


        user_teacher.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {// Handle the response

                if (e != null) {
                    // Show the error message
                    Toast.makeText(NewTeacher.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    userRef[0] = user_teacher;
                    //  Toast.makeText(NewStudent.this, "Student User made " + " " + user_student.getObjectId(), Toast.LENGTH_LONG).show();
                    Log.d("role", "added Student role of " + user_teacher.getObjectId());
                    ParseObject roleobject = new ParseObject(RoleTable.TABLE_NAME);
                    roleobject.put(RoleTable.OF_USER_REF, user_teacher);
                    roleobject.put(RoleTable.ROLE, "Teacher");
                    roleobject.put(RoleTable.ENROLLED_WITH,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                    roleobject.saveInBackground();


                    try {

                        ParseUser.become(presession);
                        addTeacher(userRef[0]);
                    } catch (ParseException e1) {
                        Toast.makeText(NewTeacher.this, "cant add teacher",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

    }




    protected void addTeacher(final ParseUser userRef){

        ParseQuery<ParseObject> teacherQuery = ParseQuery.getQuery(TeacherTable.TABLE_NAME);
        teacherQuery.whereEqualTo(TeacherTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
        teacherQuery.whereEqualTo(TeacherTable.SERIAL_NUMBER, serial);
        teacherQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> teacherListRet, ParseException e) {
                if (e == null) {

                    if (teacherListRet.size() == 0) {
                        ParseObject teacher = new ParseObject(TeacherTable.TABLE_NAME);
                        teacher.put(TeacherTable.BY_USER_REF, ParseUser.getCurrentUser());
                        teacher.put(TeacherTable.TEACHER_NAME, name);
                        teacher.put(TeacherTable.TEACHER_AGE, age);
                        teacher.put(TeacherTable.INSTITUTION_NAME, institution_name);
                        teacher.put(TeacherTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                        teacher.put(TeacherTable.TEACHER_USER_REF, userRef);
                        teacher.put(TeacherTable.SERIAL_NUMBER,serial);
                        teacher.saveInBackground();

                        Toast.makeText(getApplicationContext(), "Teacher details successfully stored", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(NewTeacher.this, Teachers.class);
                        i.putExtra("institution_code", institution_code);
                        i.putExtra("institution_name", institution_name);
                        i.putExtra("role", role);
                        startActivity(i);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Unique Serial Number Constraint Violated", Toast.LENGTH_LONG).show();
                    }

                } else {
                    //Toast.makeText(NewStudent.this, "errorInner", Toast.LENGTH_LONG).show();
                    Log.d("user", "ErrorInner: " + e.getMessage());
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
            Intent nouser=new Intent(NewTeacher.this,login.class);
            startActivity(nouser);
        }
    }

}
