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
    EditText editabsentDays;
    //EditText editpercentage;
    EditText edittotalDays;
    TextView absentDays;
    TextView percentage;
    TextView totalDays;
    TextView myDate;
    TextView editmyDate;
    Date date1;
    // CalendarView calendar;
    Calendar calendar;
    ImageView cal;
    int Year;
    int Month;
    int Day;
    String role;
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
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher");
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
        Intent from_student = getIntent();
        //final String id = from_student.getStringExtra("id");
        role=from_student.getStringExtra("role");
        studentList = (ListView) findViewById(R.id.studentList);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


        final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
        classQuery.whereEqualTo("teacher", ParseUser.getCurrentUser());
        classQuery.whereEqualTo("classTeacher", true);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                    if (studentListRet.size() == 0) {
                        Toast.makeText(AddAttendance_everyday.this, "Not the ClassTeacher", Toast.LENGTH_LONG).show();
                    } else {
                        classRef[0] = studentListRet.get(0);


                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                        studentQuery.whereEqualTo("class", classRef[0]);
                        studentQuery.addAscendingOrder("rollNumber");
                        studentQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                if (e == null) {
                                    modelItems= new Model[studentListRet.size()];
                                    //ArrayList<String> studentLt = new ArrayList<String>();
                                   // ArrayAdapter adapter = new ArrayAdapter(AddAttendance_everyday.this, android.R.layout.simple_list_item_1, studentLt);
                                    //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                    Log.d("user", "Retrieved " + studentListRet.size() + " students");
                                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                    for (int i = 0; i < studentListRet.size(); i++) {
                                        ParseObject u = (ParseObject) studentListRet.get(i);
                                        //  if(u.getString("class").equals(id)) {
                                        String rollnumber = u.getNumber("rollNumber").toString().trim();
                                        String name=u.getString("name");
                                        //name += "\n";
                                        // name += u.getInt("age");
                                        String student=rollnumber + ". " + name;
                                        modelItems[i]= new Model(student,0);
                                       // adapter.add(student);
                                        // }

                                    }


                                    adapter = new CustomAdapter(AddAttendance_everyday.this, modelItems, classRef[0]);
                                    studentList.setAdapter(adapter);


                                    /*
                                    studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                            String[] item = ((TextView) view).getText().toString().split(". ");
                                            int itemvalue = Integer.parseInt(item[0]);

                                            final ParseObject[] studentRef = new ParseObject[1];
                                            ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                                            studentQuery.whereEqualTo("rollNumber", itemvalue);
                                            studentQuery.whereEqualTo("class", classRef[0]);
                                            studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                                public void done(List<ParseObject> studentListRet, ParseException e) {
                                                    if (e == null) {
                                                        Log.d("class", "Retrieved the class");
                                                        //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                                                        studentRef[0] = studentListRet.get(0);
                                                        information(studentRef[0], view);

                                                    } else {
                                                        Toast.makeText(AddAttendance_Students.this, "error", Toast.LENGTH_LONG).show();
                                                        Log.d("user", "Error: " + e.getMessage());
                                                    }
                                                }
                                            });


                                        }
                                    }); */

                                } else {
                                    Toast.makeText(AddAttendance_everyday.this, "error", Toast.LENGTH_LONG).show();
                                    Log.d("user", "Error: " + e.getMessage());
                                }
                            }
                        });

                    }
                }else{
                    Toast.makeText(AddAttendance_everyday.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }

            }
        });


        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();





    }

    public void save(final View view){

        for (int i = 0; i < adapter.getCount(); i++)
        {
            final Model item = adapter.getItem(i);


                calendar = java.util.Calendar.getInstance();
                //System.out.println("Current time =&gt; " + calendar.getTime());

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

                        String[] studentdetails = item.getName().split(". ");
                        final int itemvalue = Integer.parseInt(studentdetails[0]);


                final ParseObject[] classRef = new ParseObject[1];
                ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
                classQuery.whereEqualTo("teacher", ParseUser.getCurrentUser());
                classQuery.whereEqualTo("classTeacher", true);
                classQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(final List<ParseObject> classListRet, ParseException e) {
                        if (e == null) {
                            Log.d("class", "Retrieved the class");
                            //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                            if (classListRet.size() == 0) {
                                Toast.makeText(AddAttendance_everyday.this, "Not the ClassTeacher", Toast.LENGTH_LONG).show();
                            } else {
                                classRef[0] = classListRet.get(0);
                        final ParseObject[] studentRef = new ParseObject[1];
                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                        studentQuery.whereEqualTo("rollNumber", itemvalue);
                        studentQuery.whereEqualTo("class", classRef[0]);
                        studentQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                if (e == null) {
                                   if(studentListRet.size()!=0) {
                                       Log.d("class", "Retrieved the class");
                                       //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();


                                       studentRef[0] = studentListRet.get(0);

                                       ParseQuery<ParseObject> attQuery = ParseQuery.getQuery("AttendanceDaily");
                                       attQuery.whereEqualTo("date", newmilliseconds);
                                       attQuery.findInBackground(new FindCallback<ParseObject>() {
                                           public void done(List<ParseObject> attListRet, ParseException e) {
                                               if (e == null) {
                                                   if (attListRet.size() == classListRet.size()) {
                                                       Toast.makeText(getApplicationContext(), "Attendance Already added for today", Toast.LENGTH_LONG).show();
                                                   } else {
                                                       ParseObject attendance = new ParseObject("AttendanceDaily");
                                                       attendance.put("student", studentRef[0]);
                                                       attendance.put("date", newmilliseconds);
                                                       if (item.isChecked()) {
                                                           attendance.put("p_a", "A");
                                                           giveMessageParent(view, studentRef[0], string_date);
                                                           Log.d("user", item.getName() + " is Checked!!");
                                                       } else
                                                           attendance.put("p_a", "P");

                                                       attendance.saveEventually();
                                                      // Toast.makeText(getApplicationContext(), "Attendance Successfully Added", Toast.LENGTH_LONG).show();

                                                   }
                                               }else {
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
                            }} else {
                                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });

        }
        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();

        Intent task_intent = new Intent(AddAttendance_everyday.this, AddAttendance_everyday.class);
        task_intent.putExtra("role", role);
        startActivity(task_intent);
    }

    public void giveMessageParent(View view, final ParseObject studentRef, final String string_date) {

        Log.d("user", "in give message");
        ParseUser student_ofclient = (ParseUser) studentRef.get("userId");
        ParseQuery<ParseObject> parent_relation = ParseQuery.getQuery("Parent");
        parent_relation.whereEqualTo("child", student_ofclient);
        parent_relation.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() != 0) {
                        ParseUser client_user = (ParseUser) objects.get(0).get("userId");
                        ParseObject newmessage = new ParseObject("Message");
                        newmessage.put("from", ParseUser.getCurrentUser());
                        newmessage.put("to", client_user);
                        newmessage.put("message", studentRef.get("name") + " was absent today on " + string_date);
                        // SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
                        newmessage.put("sentAt", (new Date()).getTime());
                        newmessage.saveEventually();
                        //Toast.makeText(AddAttendance_everyday.this, "Message Successfully Sent to Parent", Toast.LENGTH_LONG).show();


                    }
                } else {
                    Log.d("user", "Error in query");
                }
            }
        });
    }




    /*
    public void information(final ParseObject studentRef, final View view){
        ParseQuery<ParseObject> attendanceQuery = ParseQuery.getQuery("Attendance");
        attendanceQuery.whereEqualTo("student", studentRef);
        attendanceQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> attendanceListRet, ParseException e) {
                if (e == null) {
                    if (attendanceListRet.size() == 0) {
                        final Dialog dialog1 = new Dialog(AddAttendance_Students.this);
                        dialog1.setContentView(R.layout.attendance_not_added);
                        dialog1.setTitle("Attendance Information");

                        addButton = (Button) dialog1.findViewById(R.id.addButton);

                        addButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                addDialog(studentRef, view);
                                dialog1.dismiss();

                            }
                        });

                        dialog1.show();
                    } else {

                        final Dialog dialog2 = new Dialog(AddAttendance_Students.this);
                        dialog2.setContentView(R.layout.show_attendance);
                        dialog2.setTitle("Attendance Information");
                        editButton = (Button) dialog2.findViewById(R.id.editButton);
                        absentDays = (TextView) dialog2.findViewById(R.id.exam);
                        totalDays = (TextView) dialog2.findViewById(R.id.totalMarks);
                        percentage = (TextView) dialog2.findViewById(R.id.percentage);
                        myDate = (TextView) dialog2.findViewById(R.id.dateText1);

                        ParseObject u = attendanceListRet.get(0);
                        absentDays.setText(u.getNumber("absentDays").toString().trim());
                        totalDays.setText(u.getNumber("totalDays").toString().trim());
                        percentage.setText(u.getNumber("percentage").toString().trim());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        final String dateString = formatter.format(new Date(u.getLong("date")));
                        myDate.setText(dateString);


                        editButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                editDialog(studentRef, view);

                                dialog2.dismiss();
                            }
                        });

                        dialog2.show();
                    }

                } else {
                    Toast.makeText(AddAttendance_Students.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });
    }


    public void addDialog(final ParseObject studentRef, final View view){

        final Dialog dialog_in = new Dialog(AddAttendance_Students.this);
        dialog_in.setContentView(R.layout.edit_attendance);
        dialog_in.setTitle("Edit Details");

        editabsentDays = (EditText) dialog_in.findViewById(R.id.exam);
        edittotalDays = (EditText) dialog_in.findViewById(R.id.totalMarks);
        editmyDate = (TextView) dialog_in.findViewById(R.id.dateText1);

        calendar = Calendar.getInstance();
        //System.out.println("Current time =&gt; " + calendar.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final String string_current_date = df.format(calendar.getTime());
        editmyDate.setText(string_current_date);

        doneButton = (Button) dialog_in.findViewById(R.id.doneButton);
       /* cal = (ImageButton) dialog_in.findViewById(R.id.test);
        //final int[] flag = {0};
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(view);
                //flag[0] = 1;
                editmyDate.setText(String.valueOf(Daycal) + "/" + String.valueOf(Monthcal) + "/" + String.valueOf(Yearcal));
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (editabsentDays.equals("") || edittotalDays.equals("")) {
                    Toast.makeText(getApplicationContext(), "Attendance details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    ParseObject attendance= new ParseObject("Attendance");
                    attendance.put("student",studentRef);
                    attendance.put("absentDays", Float.parseFloat(editabsentDays.getText().toString()));
                    attendance.put("totalDays", Float.parseFloat(edittotalDays.getText().toString()));
                    float abs = Float.parseFloat(editabsentDays.getText().toString());
                    float total = Float.parseFloat(edittotalDays.getText().toString());
                    float percent = ((total - abs) / total) * 100;
                    attendance.put("percentage", percent);

                    String[] date = string_current_date.trim().split("/");
                    final String[] datedetails = new String[3];
                    int j = 0;

                    for (String x : date) {
                        datedetails[j++] = x;
                    }

                    Day = Integer.parseInt(datedetails[0]);
                    Month = Integer.parseInt(datedetails[1]);
                    Year = Integer.parseInt(datedetails[2]);


                    String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);
                    Toast.makeText(getApplicationContext(), "current date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

                    SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                    Date d = null;
                    try {
                        d = f.parse(string_date);
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                    long newmilliseconds = d.getTime();
                    attendance.put("date", newmilliseconds);
                    attendance.saveEventually();



                    dialog_in.dismiss();
                }
            }

        });

        dialog_in.show();

    }

    public void editDialog(final ParseObject studentRef, final View view){

        final Dialog dialog_in = new Dialog(AddAttendance_Students.this);
        dialog_in.setContentView(R.layout.edit_attendance);
        dialog_in.setTitle("Edit Details");

        editabsentDays = (EditText) dialog_in.findViewById(R.id.exam);
        edittotalDays = (EditText) dialog_in.findViewById(R.id.totalMarks);
        editmyDate = (TextView) dialog_in.findViewById(R.id.dateText1);

        editabsentDays.setText(absentDays.getText().toString().trim());
        edittotalDays.setText(totalDays.getText().toString().trim());

        doneButton = (Button) dialog_in.findViewById(R.id.doneButton);
        cal = (ImageButton) dialog_in.findViewById(R.id.test);
        //final int[] flag = {0};

        calendar = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final String string_current_date = df.format(calendar.getTime());
        editmyDate.setText(string_current_date);


     /*   cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(view);
                flag[0] = 1;
                myDate.setText(String.valueOf(Daycal) + "/" + String.valueOf(Monthcal) + "/" + String.valueOf(Yearcal));
            }
        }); */
            /*

        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (editabsentDays.equals("") || edittotalDays.equals("") ) {
                    Toast.makeText(getApplicationContext(), "Attendance details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    ParseQuery<ParseObject> inQuery = ParseQuery.getQuery("Attendance");
                    inQuery.whereEqualTo("student", studentRef);
                    inQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> objectRet, ParseException e) {

                            if (e == null) {
                                objectRet.get(0).put("absentDays", Float.parseFloat(editabsentDays.getText().toString()));
                                objectRet.get(0).put("totalDays", Float.parseFloat(edittotalDays.getText().toString()));
                                float abs = Float.parseFloat(editabsentDays.getText().toString());
                                float total = Float.parseFloat(edittotalDays.getText().toString());
                                float percent = ((total - abs) / total) * 100;
                                objectRet.get(0).put("percentage", percent);

                                String[] datenew = string_current_date.split("/");

                                final String[] datedetailsnew = new String[3];
                                int j = 0;

                                for (String x : datenew) {
                                    datedetailsnew[j++] = x;
                                }
                                Log.d("Post retrieval", datedetailsnew[0]);
                                Toast.makeText(getApplicationContext(), datedetailsnew[0], Toast.LENGTH_LONG);
                                Day = Integer.parseInt(datedetailsnew[0]);
                                Month = Integer.parseInt(datedetailsnew[1]);
                                Year = Integer.parseInt(datedetailsnew[2]);

                                String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);
                                Toast.makeText(getApplicationContext(), "current date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

                                SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                                Date d = null;
                                try {
                                    d = f.parse(string_date);
                                } catch (java.text.ParseException e1) {
                                    e.printStackTrace();
                                }
                                long newmilliseconds = d.getTime();
                                objectRet.get(0).put("date", newmilliseconds);
                                objectRet.get(0).saveEventually();

                            } else {
                                Log.d("Post retrieval", "Error: " + e.getMessage());
                            }

                        }
                    });

                    dialog_in.dismiss();
                }
            }

        });

        dialog_in.show();

    } */

   /* public void open(View view)
    {

        final Dialog dialogcal = new Dialog(AddAttendance_Students.this);
        dialogcal.setContentView(R.layout.activity_calendar2);
        dialogcal.setTitle("Select Date");
        calendar= (CalendarView)dialogcal.findViewById(R.id.calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                date1=null;
                Yearcal = year;
                Monthcal = month+1;
                Daycal = dayOfMonth;
                date1 = new Date(Yearcal - 1900, Monthcal-1, Daycal);
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                editmyDate.setText(dateFormat.format(date1), TextView.BufferType.EDITABLE);
                Toast.makeText(getApplicationContext(), Daycal + "/" + Monthcal + "/" + Yearcal, Toast.LENGTH_LONG).show();
                dialogcal.dismiss();

            }
        });
        dialogcal.show();



    }*/


                        @Override
                        protected void onPostResume () {
                            super.onPostResume();
                            if (ParseUser.getCurrentUser() == null) {
                                Intent nouser = new Intent(AddAttendance_everyday.this, login.class);
                                startActivity(nouser);
                            }
                        }


                    }

