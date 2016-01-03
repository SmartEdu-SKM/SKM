package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

/**
 * Created by Dell on 10/7/2015.
 */
public class teacher_message extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;

    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView studentList;
    Notification_bar noti_bar;
    String classId;


    EditText message;
    Button broadcast;
    Button sendmessage;
    Spinner role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_message);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Students");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher");
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
        Intent from_student = getIntent();
        classId = from_student.getStringExtra("id");
        broadcast=(Button)findViewById(R.id.broadcast);;
        studentList = (ListView) findViewById(R.id.studentList);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);
        Toast.makeText(teacher_message.this, "id class selected is = " +classId, Toast.LENGTH_LONG).show();

        /*ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
        studentQuery.whereEqualTo("class",classname);
        studentQuery.whereEqualTo("teacher",ParseUser.getCurrentUser());*/
        broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcasting(classId);
            }
        });




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
                                ArrayAdapter adapter = new ArrayAdapter(teacher_message.this, android.R.layout.simple_list_item_1, studentLt);
                                //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                Log.d("user", "Retrieved " + studentListRet.size() + " students");
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < studentListRet.size(); i++) {
                                    ParseObject u = (ParseObject) studentListRet.get(i);
                                    //  if(u.getString("class").equals(id)) {
                                    int rollnumber=u.getInt("rollNumber");
                                    String name = u.getString("name");
                                    name= String.valueOf(rollnumber) + ". " + u.getString("name").trim();
                                    //name += "\n";
                                    // name += u.getInt("age");

                                    adapter.add(name);
                                    // }

                                }


                                studentList.setAdapter(adapter);


                                studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String item = ((TextView) view).getText().toString();

                                        String[] itemValues = item.split(". ");

                                        final String[] details = new String[2];
                                        int j = 0;

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
                                                        ParseObject student = studentListRet.get(0);
                                                        giveMessage(student);
                                                    }
                                                } else {
                                                    Log.d("user", "Error: " + e.getMessage());
                                                }
                                            }
                                        });


                                    }
                                });


                            } else {
                                Toast.makeText(teacher_message.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });


                } else {
                    Toast.makeText(teacher_message.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

    }



    protected void broadcasting(String classid)
    {
        final Dialog marks_add=new Dialog(teacher_message.this);
        marks_add.setContentView(R.layout.teacher_message);
        marks_add.setTitle("Give Message");
        role = (Spinner) marks_add.findViewById(R.id.role);
        ArrayAdapter<String> adapter;
        List<String> list;
        list = new ArrayList<String>();
        list.add("Student");
        list.add("Parent");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role.setAdapter(adapter);
        message = (EditText)marks_add.findViewById(R.id.message);
        sendmessage=(Button)marks_add.findViewById(R.id.send_message);

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(message.getText().equals("")||role.getSelectedItem().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "no message or role not selected", Toast.LENGTH_LONG).show();
                }else
                {
                    if(role.getSelectedItem().equals("Student"))
                    {

                        final ParseObject[] classRef = new ParseObject[1];
                        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
                        classQuery.whereEqualTo("objectId", classId);
                        classQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                if (e == null) {
                                    classRef[0] = studentListRet.get(0);
                                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                                    studentQuery.whereEqualTo("class", classRef[0]);
                                    studentQuery.addAscendingOrder("rollNumber");
                                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                        public void done(List<ParseObject> studentListRet, ParseException e) {
                                            if (e == null) {

                                                for (int i = 0; i < studentListRet.size(); i++) {
                                                    ParseUser client_user = (ParseUser) studentListRet.get(i).get("userId");
                                                    ParseObject newmessage = new ParseObject("Message");
                                                    newmessage.put("from", ParseUser.getCurrentUser());
                                                    newmessage.put("to", client_user);
                                                    newmessage.put("message", message.getText().toString());

                                                    java.util.Calendar calendar= Calendar.getInstance();
                                                    SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                                                 //   format.parse(format.format(new Date(calendar.getTimeInMillis())));

                                                    newmessage.put("sentAt", calendar.getTimeInMillis());
                                                    newmessage.saveEventually();
                                                    marks_add.dismiss();
                                                    Toast.makeText(teacher_message.this, "Message Successfully Broadcasted to Students", Toast.LENGTH_LONG).show();


                                                }

                                            } else {
                                                Toast.makeText(teacher_message.this, "error broadcasting to students", Toast.LENGTH_LONG).show();
                                                Log.d("user", "Error: " + e.getMessage());
                                            }
                                        }
                                    });


                                } else {
                                    Toast.makeText(teacher_message.this, "error", Toast.LENGTH_LONG).show();
                                    Log.d("user", "Error: " + e.getMessage());
                                }
                            }
                        });


                    }else       //if parent selected
                    {

                        final ParseObject[] classRef = new ParseObject[1];
                        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
                        classQuery.whereEqualTo("objectId", classId);
                        classQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                if (e == null) {
                                    classRef[0] = studentListRet.get(0);
                                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                                    studentQuery.whereEqualTo("class", classRef[0]);
                                    studentQuery.addAscendingOrder("rollNumber");
                                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                        public void done(List<ParseObject> studentListRet, ParseException e) {
                                            if (e == null) {

                                                for (int i = 0; i < studentListRet.size(); i++) {


                                                    ParseUser student_ofclient=(ParseUser)studentListRet.get(i).get("userId");
                                                    ParseQuery<ParseObject> parent_relation= ParseQuery.getQuery("Parent");
                                                    parent_relation.whereEqualTo("child",student_ofclient);
                                                    parent_relation.findInBackground(new FindCallback<ParseObject>() {
                                                        @Override
                                                        public void done(List<ParseObject> objects, ParseException e) {
                                                            if (e == null) {
                                                                if (objects.size() != 0) {

                                                                    ParseUser client_user = (ParseUser) objects.get(0).get("userId");
                                                                    ParseObject newmessage = new ParseObject("Message");
                                                                    newmessage.put("from", ParseUser.getCurrentUser());
                                                                    newmessage.put("to", client_user);
                                                                    newmessage.put("message", message.getText().toString());
                                                                    java.util.Calendar calendar= Calendar.getInstance();
                                                                    SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                                                                    //   format.parse(format.format(new Date(calendar.getTimeInMillis())));

                                                                    newmessage.put("sentAt", calendar.getTimeInMillis());
                                                                    newmessage.saveEventually();
                                                                    marks_add.dismiss();
                                                                    Toast.makeText(teacher_message.this, "Message Successfully Broadcasted to Parents", Toast.LENGTH_LONG).show();


                                                                } else {
                                                                    Log.d("user", "Error in query");
                                                                }
                                                            } else {
                                                                Log.d("user", "Error in finding parent child relation");
                                                            }
                                                        }
                                                    });

                                                }

                                            } else {
                                                Toast.makeText(teacher_message.this, "error broadcasting to parents", Toast.LENGTH_LONG).show();
                                                Log.d("user", "Error: " + e.getMessage());
                                            }
                                        }
                                    });


                                } else {
                                    Toast.makeText(teacher_message.this, "error", Toast.LENGTH_LONG).show();
                                    Log.d("user", "Error: " + e.getMessage());
                                }
                            }
                        });












                    }
                }

            }
        });
        marks_add.show();
    }

    protected void giveMessage(final ParseObject studentobject)
    {
        final Dialog marks_add=new Dialog(teacher_message.this);
        marks_add.setContentView(R.layout.teacher_message);
        marks_add.setTitle("Give Message");
        role = (Spinner) marks_add.findViewById(R.id.role);
        ArrayAdapter<String> adapter;
        List<String> list;
        list = new ArrayList<String>();
        list.add("Student");
        list.add("Parent");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role.setAdapter(adapter);
        message = (EditText)marks_add.findViewById(R.id.message);
        sendmessage=(Button)marks_add.findViewById(R.id.send_message);

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(message.getText().equals("")||role.getSelectedItem().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "no message", Toast.LENGTH_LONG).show();
                }else
                {
                    if(role.getSelectedItem().equals("Student"))
                    {
                        ParseUser client_user=(ParseUser)studentobject.get("userId");
                        ParseObject newmessage=new ParseObject("Message");
                        newmessage.put("from",ParseUser.getCurrentUser());
                        newmessage.put("to",client_user);
                        newmessage.put("message", message.getText().toString());
                        java.util.Calendar calendar= Calendar.getInstance();
                        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                        //   format.parse(format.format(new Date(calendar.getTimeInMillis())));

                        newmessage.put("sentAt", calendar.getTimeInMillis());
                        newmessage.saveEventually();
                        marks_add.dismiss();
                        Toast.makeText(teacher_message.this, "Message Successfully Sent to Student", Toast.LENGTH_LONG).show();

                    }else       //if parent selected
                    {
                        ParseUser student_ofclient=(ParseUser)studentobject.get("userId");
                        ParseQuery<ParseObject> parent_relation= ParseQuery.getQuery("Parent");
                        parent_relation.whereEqualTo("child",student_ofclient);
                        parent_relation.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e==null)
                                {
                                    if(objects.size()!=0)
                                    {
                                        ParseUser client_user=(ParseUser)objects.get(0).get("userId");
                                        ParseObject newmessage=new ParseObject("Message");
                                        newmessage.put("from",ParseUser.getCurrentUser());
                                        newmessage.put("to",client_user);
                                        newmessage.put("message", message.getText().toString());


                                        java.util.Calendar calendar= Calendar.getInstance();
                                        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                                        String date= format.format(new Date(calendar.getTimeInMillis()));
                                        Date d=null;
                                        try {
                                            d=format.parse(date);
                                        } catch (java.text.ParseException e1) {
                                            e1.printStackTrace();
                                        }

                                        newmessage.put("sentAt", d.getTime());
                                        newmessage.saveEventually();
                                        marks_add.dismiss();
                                        Toast.makeText(teacher_message.this, "Message Successfully Sent to Parent", Toast.LENGTH_LONG).show();

                                    }else
                                    {
                                        Log.d("user", "Error in query");
                                    }
                                }else
                                {
                                    Log.d("user", "Error in finding parent child relation");
                                }
                            }
                        });
                    }
                }

            }
        });
        marks_add.show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(teacher_message.this,login.class);
            startActivity(nouser);
        }
    }

}
