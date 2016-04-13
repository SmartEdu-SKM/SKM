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
import java.util.HashMap;
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


    Button selected_button;
    EditText message;
    Button broadcast;
    Button sendmessage;


    Model[] modelItems;
    CustomAdapter customAdapter;


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
        institution_name= from_student.getStringExtra("institution_name");
        institution_code= from_student.getStringExtra("institution_code");
       // classGradeId=from_student.getStringExtra("classGradeId");
        studentId=from_student.getStringExtra("studentId");
        if(role.equals("Parent")){
            child_username=from_student.getStringExtra("child_username");
        }
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(),role,institution_name);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        classGradeId = from_student.getStringExtra("classGradeId");
        studentId=from_student.getStringExtra("studentId");
        broadcast=(Button)findViewById(R.id.broadcast);;
        teacherList = (ListView) findViewById(R.id.studentList);
        selected_button=(Button)findViewById(R.id.selected);
        selected_button.setVisibility(View.INVISIBLE);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);


        broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcasting();
            }
        });


        final HashMap<String,ParseObject> teacherMap=new HashMap<>();
                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
                    studentQuery.whereEqualTo(ClassTable.CLASS_NAME, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME,classGradeId));

                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, ParseException e) {
                            if (e == null) {


                                Log.d("user", "Retrieved " + studentListRet.size() + " teachers");
                                modelItems= new Model[studentListRet.size()];
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < studentListRet.size(); i++) {
                                    ParseObject u = (ParseObject) studentListRet.get(i);
                                    //  if(u.getString("class").equals(id)) {

                                    String subject = u.getString(ClassTable.SUBJECT);
                                    String teacher_name= null;
                                    try {
                                        teacher_name = ((ParseUser)u.get(ClassTable.TEACHER_USER_REF)).fetchIfNeeded().getUsername();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    String name= teacher_name + ", " + subject;
                                    teacherMap.put(name,(ParseObject)(u.get(ClassTable.TEACHER_USER_REF)));
                                    //name += "\n";
                                    // name += u.getInt("age");

                                    //  adapter.add(name);
                                    // }
                                    modelItems[i]=new Model(name,0);

                                }


                                customAdapter = new CustomAdapter(message_to_teacher.this, modelItems, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME,classGradeId));
                                // no use of the reference context here but in attendance_everyday

                                teacherList.setAdapter(customAdapter);
                                selected_button.setVisibility(View.VISIBLE);

                                selected_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sendToSelected();
                                    }
                                });


                            } else {
                                Toast.makeText(message_to_teacher.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });



    }



    protected void broadcasting()
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

                                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
                                    studentQuery.whereEqualTo(ClassTable.CLASS_NAME, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME,classGradeId));
                                   // studentQuery.whereEqualTo(ClassTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                        public void done(List<ParseObject> studentListRet, ParseException e) {
                                            if (e == null) {

                                                for (int i = 0; i < studentListRet.size(); i++) {
                                                    ParseUser client_user = (ParseUser) studentListRet.get(i).get(ClassTable.TEACHER_USER_REF);
                                                    ParseObject newmessage = new ParseObject(MessageTable.TABLE_NAME);
                                                    newmessage.put(MessageTable.FROM_USER_REF, ParseUser.getCurrentUser());
                                                    newmessage.put(MessageTable.TO_USER_REF, client_user);
                                                    newmessage.put(MessageTable.MESSAGE_CONTENT, message.getText().toString());
                                                    newmessage.put(MessageTable.DELETED_BY_SENDER,false);
                                                    newmessage.put(MessageTable.DELETED_BY_RECEIVER,false);
                                                    newmessage.put(MessageTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));


                                                    java.util.Calendar calendar= Calendar.getInstance();
                                                    SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                                                    String date= format.format(new Date(calendar.getTimeInMillis()));
                                                    Date d=null;
                                                    try {
                                                        d=format.parse(date);
                                                    } catch (java.text.ParseException e1) {
                                                        e1.printStackTrace();
                                                    }

                                                    newmessage.put(MessageTable.SENT_AT, d.getTime());
                                                    newmessage.saveEventually();
                                                    marks_add.dismiss();


                                                }
                                                goToViewMessages();

                                                Toast.makeText(message_to_teacher.this, "Message Successfully Broadcasted to teachers", Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(message_to_teacher.this, "error broadcasting to teachers", Toast.LENGTH_LONG).show();
                                                Log.d("user", "Error: " + e.getMessage());
                                            }
                                        }
                                    });


                }

            }
        });
        marks_add.show();
    }


    protected void sendToSelected()
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
                        goToViewMessages();

                        Toast.makeText(message_to_teacher.this, "Message Successfully Sent to Teacher", Toast.LENGTH_LONG).show();

                    }
                }
            });
            marks_add.show();
        }
    }




    protected void giveMessage(final ParseUser teacher)
    {

            ParseUser client_user=teacher;
            ParseObject newmessage=new ParseObject(MessageTable.TABLE_NAME);
            newmessage.put(MessageTable.FROM_USER_REF,ParseUser.getCurrentUser());
            newmessage.put(MessageTable.TO_USER_REF,client_user);
            newmessage.put(MessageTable.MESSAGE_CONTENT, message.getText().toString());
            newmessage.put(MessageTable.DELETED_BY_SENDER,false);
            newmessage.put(MessageTable.DELETED_BY_RECEIVER,false);
            newmessage.put(MessageTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
            java.util.Calendar calendar= Calendar.getInstance();
            SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
            String date= format.format(new Date(calendar.getTimeInMillis()));
            Date d=null;
            try {
                d=format.parse(date);
            } catch (java.text.ParseException e1) {
                e1.printStackTrace();
            }

            newmessage.put(MessageTable.SENT_AT, d.getTime());
            newmessage.saveEventually();


    }



    protected void goToViewMessages(){
        if(role.equals("Parent")) {
            Intent message_intent = new Intent(message_to_teacher.this, view_messages.class);
            message_intent.putExtra("role", "Parent");
            message_intent.putExtra("classGradeId", classGradeId);
            message_intent.putExtra("studentId", studentId);
            message_intent.putExtra("institution_name", institution_name);
            message_intent.putExtra("institution_code", institution_code);
            message_intent.putExtra("child_username", child_username);
            message_intent.putExtra("for", "sent");
            startActivity(message_intent);
        }else if(role.equals("Student")){
            Intent message_intent = new Intent(message_to_teacher.this, view_messages.class);
            message_intent.putExtra("role", role);
            message_intent.putExtra("classGradeId", classGradeId);
            message_intent.putExtra("studentId", studentId);
            message_intent.putExtra("institution_name", institution_name);
            message_intent.putExtra("institution_code", institution_code);
            message_intent.putExtra("for","sent");
            startActivity(message_intent);
        }
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
