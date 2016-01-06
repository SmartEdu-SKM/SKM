package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

/**
 * Created by Dell on 10/7/2015.
 */
public class message_to_teacher extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;

    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView teacherList;
    Notification_bar noti_bar;
    String classId;
    String role;
    Button selected_button;
    EditText message;
    Button broadcast;
    Button sendmessage;

    Model[] modelItems;
    CustomAdapter customAdapter;
    String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_to_teacher);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Teachers");
        Intent from_student = getIntent();
        role = from_student.getStringExtra("role");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(),role);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        classId = from_student.getStringExtra("classId");
        studentId=from_student.getStringExtra("studentId");
        broadcast=(Button)findViewById(R.id.broadcast);;
        teacherList = (ListView) findViewById(R.id.studentList);
        selected_button=(Button)findViewById(R.id.selected);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);
        Toast.makeText(message_to_teacher.this, "id class selected is = " +classId, Toast.LENGTH_LONG).show();

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
            public void done(List<ParseObject> classListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    classRef[0] = classListRet.get(0);
                    String classname=classRef[0].getString("class");


                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
                    studentQuery.whereEqualTo("class", classname);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, ParseException e) {
                            if (e == null) {


                                Log.d("user", "Retrieved " + studentListRet.size() + " teachers");
                                modelItems= new Model[studentListRet.size()];
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < studentListRet.size(); i++) {
                                    ParseObject u = (ParseObject) studentListRet.get(i);
                                    //  if(u.getString("class").equals(id)) {

                                    String subject = u.getString("subject");
                                    String teacher_name= null;
                                    try {
                                        teacher_name = ((ParseUser)u.get("teacher")).fetchIfNeeded().getUsername();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    String name= teacher_name + ", " + subject;
                                    //name += "\n";
                                    // name += u.getInt("age");

                                    //  adapter.add(name);
                                    // }
                                    modelItems[i]=new Model(name,0);

                                }


                                customAdapter = new CustomAdapter(message_to_teacher.this, modelItems, classRef[0]);
                                teacherList.setAdapter(customAdapter);

                                selected_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sendToSelected(classRef[0]);
                                    }
                                });


                            } else {
                                Toast.makeText(message_to_teacher.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });


                } else {
                    Toast.makeText(message_to_teacher.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

    }



    protected void broadcasting(String classid)
    {
        final Dialog marks_add=new Dialog(message_to_teacher.this);
        marks_add.setContentView(R.layout.sending_message_to_teacher);
        marks_add.setTitle("Give Message");

        message = (EditText)marks_add.findViewById(R.id.message);
        sendmessage=(Button)marks_add.findViewById(R.id.send_message);

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(message.getText().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "no message or role not selected", Toast.LENGTH_LONG).show();
                }else
                {


                        final ParseObject[] classRef = new ParseObject[1];
                        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
                        classQuery.whereEqualTo("objectId", classId);
                        classQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                if (e == null) {
                                    classRef[0] = studentListRet.get(0);
                                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
                                    studentQuery.whereEqualTo("class", classRef[0].get("class"));
                                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                        public void done(List<ParseObject> studentListRet, ParseException e) {
                                            if (e == null) {

                                                for (int i = 0; i < studentListRet.size(); i++) {
                                                    ParseUser client_user = (ParseUser) studentListRet.get(i).get("teacher");
                                                    ParseObject newmessage = new ParseObject("Message");
                                                    newmessage.put("from", ParseUser.getCurrentUser());
                                                    newmessage.put("to", client_user);
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
                                                    Toast.makeText(message_to_teacher.this, "Message Successfully Broadcasted to teachers", Toast.LENGTH_LONG).show();


                                                }

                                            } else {
                                                Toast.makeText(message_to_teacher.this, "error broadcasting to teachers", Toast.LENGTH_LONG).show();
                                                Log.d("user", "Error: " + e.getMessage());
                                            }
                                        }
                                    });


                                } else {
                                    Toast.makeText(message_to_teacher.this, "error", Toast.LENGTH_LONG).show();
                                    Log.d("user", "Error: " + e.getMessage());
                                }
                            }
                        });


                }

            }
        });
        marks_add.show();
    }


    protected void sendToSelected(final ParseObject classobject)
    {
        int count=0;
        for(int i=0;i<customAdapter.getCount();i++)
        {
            Model item=customAdapter.getItem(i);
            if (item.isChecked())
            {
                count++;
                break;
            }
        }
        if (count==0)
        {
            Toast.makeText(getApplicationContext(), "no recipients selected", Toast.LENGTH_LONG).show();
        }else {

            final Dialog marks_add = new Dialog(message_to_teacher.this);
            marks_add.setContentView(R.layout.sending_message_to_teacher);
            marks_add.setTitle("Give Message");
            message = (EditText) marks_add.findViewById(R.id.message);
            sendmessage = (Button) marks_add.findViewById(R.id.send_message);

            sendmessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (message.getText().equals("")) {
                        Toast.makeText(getApplicationContext(), "no message", Toast.LENGTH_LONG).show();
                    } else {
                        //   giveMessage(classobject);


                        for (int i = 0; i < customAdapter.getCount(); i++) {
                            final Model item = customAdapter.getItem(i);

                            if(item.isChecked()) {
                                String[] studentdetails = item.getName().split(", ");

                                final String[] details = new String[2];
                                int j = 0;

                                for (String x : studentdetails) {
                                    details[j++] = x;
                                }
                                Log.d("user", "username: " + details[0].trim() + "subject " + details[1]);  //extracts Chit as Chi and query fails???

                                ParseQuery<ParseUser> studentQuery = ParseUser.getQuery();
                                studentQuery.whereEqualTo("username", details[0]);
                                studentQuery.findInBackground(new FindCallback<ParseUser>() {
                                    @Override
                                    public void done(List<ParseUser> objects, ParseException e) {
                                        if (e == null) {
                                            if (objects.size() != 0) {
                                                ParseUser teacher = objects.get(0);
                                                giveMessage(teacher);
                                                marks_add.dismiss();
                                            }
                                        } else {
                                            Log.d("user", "Error: " + e.getMessage());
                                        }
                                    }
                                });

                            }

                        }

                    }
                }
            });
            marks_add.show();
        }
    }




    protected void giveMessage(final ParseUser teacher)
    {

            ParseUser client_user=teacher;
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
            Toast.makeText(message_to_teacher.this, "Message Successfully Sent to Teacher", Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(message_to_teacher.this,login.class);
            startActivity(nouser);
        }
    }

}
