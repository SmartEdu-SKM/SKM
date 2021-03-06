package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAttendance_everyday extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button addButton;
    Button editButton;
    Button doneButton;

    Calendar calendar;
    ImageView cal;
    int Year;
    int Month;
    int Day;

    //int Yearcal;
    //int Monthcal;
    //int Daycal;

    ListView lv;
    Model[] modelItems;
    Model[] modelItemsRetrieved;
    Button saveButton;
    CustomAdapter adapter;

    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView studentList;
    Notification_bar noti_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendance_everyday);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attendance");
        Intent from_student = getIntent();
        //final String id = from_student.getStringExtra("id");
        role = from_student.getStringExtra("role");
        classId = from_student.getStringExtra("classId");
        classGradeId = from_student.getStringExtra("classGradeId");
        institution_name = from_student.getStringExtra("institution_name");
        institution_code = from_student.getStringExtra("institution_code");
        noti_bar = (Notification_bar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher", institution_name);
        dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);




        studentList = (ListView) findViewById(R.id.studentList);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setVisibility(View.INVISIBLE);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
        drawerFragment.setDrawerListener(this);


        context = this;
        layoutLoading=(RelativeLayout) findViewById(R.id.loadingPanel);


        final ParseObject[] classRef = new ParseObject[1];

        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        classQuery.whereEqualTo(ClassTable.OBJECT_ID, classId);
        classQuery.whereEqualTo(ClassTable.TEACHER_USER_REF, ParseUser.getCurrentUser());
        classQuery.whereEqualTo(ClassTable.IF_CLASS_TEACHER, true);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    //Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                    if (studentListRet.size() == 0) {
                        Toast.makeText(AddAttendance_everyday.this, "Not the ClassTeacher", Toast.LENGTH_LONG).show();
                    } else {
                        classRef[0] = studentListRet.get(0);

                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(StudentTable.TABLE_NAME);
                        studentQuery.whereEqualTo(StudentTable.ADDED_BY_USER_REF,ParseUser.getCurrentUser());
                        studentQuery.whereEqualTo(StudentTable.CLASS_REF, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME, classGradeId));
                        studentQuery.addAscendingOrder(StudentTable.ROLL_NUMBER);
                        studentQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                if (e == null) {
                                    modelItems = new Model[studentListRet.size()];
                                    //ArrayList<String> studentLt = new ArrayList<String>();
                                    // ArrayAdapter adapter = new ArrayAdapter(AddAttendance_everyday.this, android.R.layout.simple_list_item_1, studentLt);
                                    //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                    Log.d("user", "Retrieved " + studentListRet.size() + " students");
                                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                    for (int i = 0; i < studentListRet.size(); i++) {
                                        ParseObject u = (ParseObject) studentListRet.get(i);
                                        //  if(u.getString("class").equals(id)) {

                                        String rollnumber = u.getNumber(StudentTable.ROLL_NUMBER).toString().trim();
                                        String name = u.getString(StudentTable.STUDENT_NAME);
                                        //name += "\n";
                                        // name += u.getInt("age");
                                        String student = rollnumber + ". " + name;
                                        modelItems[i] = new Model(student, 0);
                                        // adapter.add(student);

                                        // }

                                    }


                                    adapter = new CustomAdapter(AddAttendance_everyday.this, modelItems, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME, classGradeId));
                                    studentList.setAdapter(adapter);
                                    new LoadingSyncList(context,layoutLoading,studentList).execute();
                                    if(studentListRet.size()!=0) {
                                        saveButton.setVisibility(View.VISIBLE);
                                    }
                                    else
                                        Toast.makeText(AddAttendance_everyday.this, "No List to Display", Toast.LENGTH_LONG).show();

                                    saveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            save();
                                        }
                                    });



                                } else {
                                    Toast.makeText(AddAttendance_everyday.this, "error", Toast.LENGTH_LONG).show();
                                    Log.d("user", "Error in student query: " + e.getMessage());
                                }
                            }
                        });

                    }
                } else {
                    Toast.makeText(AddAttendance_everyday.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error in class query: " + e.getMessage());
                }

            }
        });





    }

    public void save() {


        calendar = java.util.Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final String string_current_date = df.format(calendar.getTime());

        String[] date = string_current_date.trim().split("/");
        final String[] datedetails = new String[3];
        int j = 0;

        for (String x : date) {
            datedetails[j++] = x;
        }

        Day = Integer.parseInt(datedetails[0]);
        Month = Integer.parseInt(datedetails[1]);
        Year = Integer.parseInt(datedetails[2]);


        final String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);
        Toast.makeText(AddAttendance_everyday.this, "current date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        Date d = null;
        try {
            d = f.parse(string_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        final long newmilliseconds = d.getTime();

        try {
            //int checked=0;
            for (int i = 0; i < adapter.getCount(); i++) {
                final Model item = adapter.getItem(i);
                if (item.isChecked()) {
                    // checked = 1;
                    break;
                }

            }



            ParseQuery<ParseObject> attQuery = ParseQuery.getQuery(AttendanceDailyTable.TABLE_NAME);
            attQuery.whereEqualTo(AttendanceDailyTable.ATTENDANCE_DATE, newmilliseconds);
            attQuery.whereEqualTo(AttendanceDailyTable.TEACHER_USER_REF, ParseUser.getCurrentUser());
            attQuery.whereEqualTo(AttendanceDailyTable.FOR_CLASS, ParseObject.createWithoutData(ClassTable.TABLE_NAME, classId));
            attQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> attListRet, ParseException e) {
                    if (e == null) {
                        Log.d("user in attendaily", "Retrieved " + attListRet.size() + " students");
                        if (attListRet.size() != 0) {

                            Toast.makeText(getApplicationContext(), "Attendance Already added for today", Toast.LENGTH_LONG).show();
                        } else {

                            for (int i = 0; i < adapter.getCount(); i++) {
                                final Model item = adapter.getItem(i);


                                String[] studentdetails = item.getName().split("\\. ");
                                final int itemvalue = Integer.parseInt(studentdetails[0]);


                                final ParseObject[] classRef = new ParseObject[1];
                                ParseQuery<ParseObject> classQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
                                classQuery.whereEqualTo(ClassTable.TEACHER_USER_REF, ParseUser.getCurrentUser());
                                classQuery.whereEqualTo(ClassTable.IF_CLASS_TEACHER, true);
                                classQuery.whereEqualTo(ClassTable.OBJECT_ID, classId);
                                classQuery.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(final List<ParseObject> classListRet, ParseException e) {
                                        if (e == null) {

                                            if (classListRet.size() == 0) {
                                                Toast.makeText(AddAttendance_everyday.this, "Not the ClassTeacher", Toast.LENGTH_LONG).show();
                                            } else {
                                                classRef[0] = classListRet.get(0);
                                                final ParseObject[] studentRef = new ParseObject[1];
                                                ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(StudentTable.TABLE_NAME);
                                                studentQuery.whereEqualTo(StudentTable.ADDED_BY_USER_REF, ParseUser.getCurrentUser());
                                                studentQuery.whereEqualTo(StudentTable.ROLL_NUMBER, itemvalue);
                                                studentQuery.whereEqualTo(StudentTable.CLASS_REF, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME, classGradeId));
                                                studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                                    public void done(final List<ParseObject> studentListRet, ParseException e) {
                                                        if (e == null) {
                                                            if (studentListRet.size() != 0) {
                                                                 Log.d("class", "Retrieved the class"+studentListRet.size());
                                                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();


                                                                studentRef[0] = studentListRet.get(0);


                                                                ParseObject attendance = new ParseObject(AttendanceDailyTable.TABLE_NAME);
                                                                attendance.put(AttendanceDailyTable.STUDENT_USER_REF, studentRef[0]);
                                                                attendance.put(AttendanceDailyTable.ATTENDANCE_DATE, newmilliseconds);
                                                                if (item.isChecked()) {
                                                                    attendance.put(AttendanceDailyTable.STATUS, "A");
                                                                    giveMessageParent(studentRef[0], string_date);
                                                                    //sleep(1000);
                                                                    Log.d("user", item.getName() + " is Checked!!");
                                                                } else
                                                                    attendance.put(AttendanceDailyTable.STATUS, "P");
                                                                attendance.put(AttendanceDailyTable.TEACHER_USER_REF, ParseUser.getCurrentUser());
                                                                // attendance.put(AttendanceDailyTable.FOR_CLASS, classRef[0]);
                                                                attendance.put(AttendanceDailyTable.FOR_CLASS, ParseObject.createWithoutData(ClassTable.TABLE_NAME, classId));

                                                                attendance.saveEventually();
                                                                // Toast.makeText(getApplicationContext(), "Attendance Successfully Added", Toast.LENGTH_LONG).show();


                                                            }
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                                                            Log.d("user", "Error: " + e.getMessage());
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                                            Log.d("user", "Error: " + e.getMessage());
                                        }
                                    }
                                });
                            }


                            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                        Log.d("user", "Error: " + e.getMessage());
                    }
                }
            });


            Intent task_intent = new Intent(AddAttendance_everyday.this, AddAttendance_everyday.class);
            task_intent.putExtra("institution_code", institution_code);
            task_intent.putExtra("institution_name", institution_name);
            task_intent.putExtra("role", role);
            task_intent.putExtra("classId", classId);
            task_intent.putExtra("classGradeId", classGradeId);
            startActivity(task_intent);

    }

    catch(
    Exception ex
    )

    {
        Toast.makeText(getApplicationContext(), "error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        Log.d("user", "Error catch: " + ex.getMessage());
    }

}

    public void giveMessageParent(final ParseObject studentRef, final String string_date) {

        Log.d("user", "in give message");
        ParseUser student_ofclient = (ParseUser) studentRef.get(StudentTable.STUDENT_USER_REF);
        ParseQuery<ParseObject> parent_relation = ParseQuery.getQuery(ParentTable.TABLE_NAME);
        parent_relation.whereEqualTo(ParentTable.CHILD_USER_REF, student_ofclient);
        parent_relation.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() != 0) {
                        Log.d("user", "in query");
                        ParseUser client_user = (ParseUser) objects.get(0).get(ParentTable.PARENT_USER_REF);
                        ParseObject newmessage = new ParseObject(MessageTable.TABLE_NAME);
                        newmessage.put(MessageTable.FROM_USER_REF, ParseUser.getCurrentUser());
                        newmessage.put(MessageTable.TO_USER_REF, client_user);
                        Log.d("user", "to parent " + client_user.getObjectId());
                        newmessage.put(MessageTable.MESSAGE_CONTENT, studentRef.get("name") + " was absent today on " + string_date);

                        newmessage.put(MessageTable.DELETED_BY_SENDER,false);
                        newmessage.put(MessageTable.DELETED_BY_RECEIVER,false);

                        java.util.Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                        String date = format.format(new Date(calendar.getTimeInMillis()));
                        Date d = null;
                        try {
                            d = format.parse(date);
                        } catch (java.text.ParseException e1) {
                            e1.printStackTrace();
                        }

                        newmessage.put(MessageTable.SENT_AT, d.getTime());
                        newmessage.put(MessageTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                        newmessage.saveEventually();
                        //Toast.makeText(AddAttendance_everyday.this, "Message Successfully Sent to Parent", Toast.LENGTH_LONG).show();


                    }else{
                        Log.d("user ", "error in query");
                    }
                } else {
                    Log.d("user ", e.getMessage());
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



                        @Override
                        protected void onPostResume () {
                            super.onPostResume();
                            if (ParseUser.getCurrentUser() == null) {
                                Intent nouser = new Intent(AddAttendance_everyday.this, login.class);
                                startActivity(nouser);
                            }
                        }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent tohome = new Intent(AddAttendance_everyday.this, SelectSubject.class);
        tohome.putExtra("role",role);
        tohome.putExtra("institution_name",institution_name);
        tohome.putExtra("institution_code",institution_code);
        tohome.putExtra("for","attendance");
        tohome.putExtra("classGradeId",classGradeId);

        startActivity(tohome);

    }

                    }


