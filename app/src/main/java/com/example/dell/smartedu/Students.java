package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class Students extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button addStudentButton;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView studentList;
    Notification_bar noti_bar;
    String classId;
    String name;
    Integer age;
    Integer rollno;
    Button createIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        Intent from_student = getIntent();
        classId = from_student.getStringExtra("id");
        role=from_student.getStringExtra("role");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Students");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        addStudentButton = (Button)findViewById(R.id.addButton);
        studentList = (ListView) findViewById(R.id.studentList);
        createIDs=(Button)findViewById(R.id.shareCode);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,"Teacher");
        drawerFragment.setDrawerListener(this);

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);
        Toast.makeText(Students.this, "id class selected is = " +classId, Toast.LENGTH_LONG).show();

        /*ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
        studentQuery.whereEqualTo("class",classname);
        studentQuery.whereEqualTo("teacher",ParseUser.getCurrentUser());*/
        final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
        classQuery.whereEqualTo("objectId",classId);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                    classRef[0] = studentListRet.get(0);

                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                    studentQuery.whereEqualTo("class", classRef[0]);
                    studentQuery.addAscendingOrder("rollNumber");
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, ParseException e) {
                            if (e == null) {

                                ArrayList<String> studentLt = new ArrayList<String>();
                                ArrayAdapter adapter = new ArrayAdapter(Students.this, android.R.layout.simple_list_item_1, studentLt);
                                //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                Log.d("user", "Retrieved " + studentListRet.size() + " students");
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < studentListRet.size(); i++) {
                                    ParseObject u = (ParseObject) studentListRet.get(i);
                                    //  if(u.getString("class").equals(id)) {
                                    int rollnumber=u.getInt("rollNumber");
                                    String name = u.getString("name");
                                    name= String.valueOf(rollnumber) + ". " + name;
                                    //name += "\n";
                                    // name += u.getInt("age");

                                    adapter.add(name);
                                    // }

                                }


                                studentList.setAdapter(adapter);
                                createIDs.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        shareCode();
                                    }
                                });


                                studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String item = ((TextView) view).getText().toString();
                                        Log.d("user", item);

                                        //item  = item.replaceAll("[\n\r\\s]", "");
                                        String[] itemValues = item.split("\\. ");

                                        final String[] details = new String[2];
                                        int j = 0;
                                        for(int i=0; i<=1; i++){
                                            Log.d("user", itemValues[i]);
                                        }

                                        for (String x : itemValues) {
                                            details[j++] = x;
                                        }

                                        Log.d("user", "rno: " + details[0].trim()+"name "+details[1]);  //extracts Chit as Chi and query fails???

                                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                                        studentQuery.whereEqualTo("rollNumber", Integer.parseInt(details[0].trim()));
                                        studentQuery.whereEqualTo("name", details[1].trim());
                                        studentQuery.whereEqualTo("class", classRef[0]);
                                        studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                                if (e == null) {
                                                    if(studentListRet.size()!=0) {
                                                        ParseObject u = (ParseObject) studentListRet.get(0);
                                                        String id = u.getObjectId();
                                                        //Toast.makeText(Students.this,"id of student selected is = " + id, Toast.LENGTH_LONG).show();
                                                        Intent to_student_info = new Intent(Students.this, StudentInfo.class);
                                                        to_student_info.putExtra("id", id);
                                                        to_student_info.putExtra("classId", classId);
                                                        startActivity(to_student_info);
                                                    }
                                                } else {
                                                    Log.d("user", "Error: " + e.getMessage());
                                                }
                                            }
                                        });


                                    }
                                });


                            } else {
                                Toast.makeText(Students.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });


                } else {
                    Toast.makeText(Students.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });



        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();



        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Students.this, NewStudent.class);
                i.putExtra("id",classId);
                startActivity(i);
            }
        });




    }

    public void shareCode(){


                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                    studentQuery.whereEqualTo("class", ParseObject.createWithoutData("Class",classId));
                    studentQuery.whereEqualTo("addedBy", null);
                    studentQuery.whereEqualTo("userId", null);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, ParseException e) {
                            if (e == null) {
                                if (studentListRet.size() != 0) {

                                    for(int i=0; i<studentListRet.size(); i++) {
                                        ParseObject u = studentListRet.get(i);
                                        name = u.getString("name");
                                        age = u.getInt("age");
                                        rollno = u.getInt("rollNumber");

                                        final String sessionToken = ParseUser.getCurrentUser().getSessionToken();
                                        addStudentUser(name, age, rollno, sessionToken, u);
                                        sleep(10000);
                                        addParentUser(name, age, rollno, sessionToken);
                                        Log.d("shareCode", "Main: ");
                                       // u.put("addedBy", ParseUser.getCurrentUser());
                                        //u.saveEventually();
                                    }
                                }


                            } else {
                                //Toast.makeText(NewStudent.this, "errorInner", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
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
    protected void addStudentUser(final String Name, int Age, final int Rollno, final String presession, final ParseObject u)
    {
        final ParseUser[] userRef = new ParseUser[1];
        // Set up a new Parse user
        final ParseUser user_student = new ParseUser();
        user_student.setUsername(Name + Rollno);
        user_student.setPassword(Rollno + Name + Rollno);


        user_student.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {// Handle the response

                if (e != null) {
                    // Show the error message
                    Toast.makeText(Students.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    userRef[0] = user_student;
                   // Toast.makeText(Students.this, "Student User made " + " " + user_student.getObjectId(), Toast.LENGTH_LONG).show();
                    Log.d("role", "added Student role of " + user_student.getObjectId());
                    ParseObject roleobject = new ParseObject("Role");
                    roleobject.put("createdBy", user_student);
                    roleobject.put("roleName", "Student");
                    roleobject.saveInBackground();

                    ParseObject parent=new ParseObject("Parent");
                    parent.put("child", user_student);
                    parent.saveEventually();

                    u.put("userId", user_student);


                    try {

                        ParseUser.become(presession);
                       // Log.d("student", "adding userId");
                       // u.put("userId", user_student);
                        u.put("addedBy",ParseUser.getCurrentUser());

                    } catch (ParseException e1) {
                        Toast.makeText(Students.this, "cant add student",
                                Toast.LENGTH_LONG).show();
                    }
                    u.saveInBackground();
                    //addStudent(userRef[0],rollno);

                }

            }
        });

    }





    protected void addParentUser(final String Name, int Age, final int Rollno, final String presession)
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
                    Toast.makeText(Students.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    userRef[0] = user_parent;
                   // Toast.makeText(Students.this, "Parent User made "+ " "+user_parent.getObjectId(),Toast.LENGTH_LONG).show();
                    Log.d("role", "added Parent role of " + user_parent.getObjectId());
                    ParseObject roleobject = new ParseObject("Role");
                    roleobject.put("createdBy", user_parent);
                    roleobject.put("roleName", "Parent");
                    roleobject.saveInBackground();




                    ParseQuery<ParseUser> user=ParseUser.getQuery();
                    user.whereEqualTo("username",Name +String.valueOf(Rollno));
                    Log.d("user", Name+String.valueOf(Rollno));
                    user.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                if (objects.size() != 0) {
                                    Log.d("user", "student user found");
                                    ParseUser student_user = objects.get(0);
                                    ParseQuery<ParseObject> parent_relation = ParseQuery.getQuery("Parent");
                                    parent_relation.whereEqualTo("child", student_user);
                                    parent_relation.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e == null) {
                                                if (objects.size() != 0) {
                                                    objects.get(0).put("userId", user_parent);
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
                        Toast.makeText(Students.this,"cant add parent",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

    }



    protected void addStudent(final ParseUser userRef, final Integer rollno){
        Log.d("in", "add student");
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

                                if (studentListRet.size() != 0) {
                                    ParseObject student = studentListRet.get(0);

                                    student.put("userId", userRef);
                                    student.put("addedBy",ParseUser.getCurrentUser());
                                    student.saveInBackground();

                                    Toast.makeText(getApplicationContext(), "Student details successfully stored", Toast.LENGTH_LONG).show();

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



    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(Students.this,login.class);
            startActivity(nouser);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(Students.this,MainActivity.class);
                startActivity(i);
                finish();
                //do your own thing here
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


}
