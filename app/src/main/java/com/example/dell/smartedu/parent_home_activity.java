package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class parent_home_activity extends BaseActivity{

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    ArrayList<Task> myList;

    String classId;
    String studentId;
String role="Parent";
    MyDBHandler dbHandler;
    Notification_bar noti_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home_activity);

        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Parent");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Dashboard");



        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);

        final ParseObject[] childRef = new ParseObject[1];
        final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Parent");
        studentQuery.whereEqualTo("userId", ParseUser.getCurrentUser());
        studentQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studListRet, ParseException e) {
                if (e == null) {

                    if (studListRet.size() != 0) {

                        ParseObject u = (ParseObject) studListRet.get(0);
                        childRef[0] = (ParseObject) u.get("child");

                        ParseQuery<ParseObject> studQuery = ParseQuery.getQuery("Student");
                        studQuery.whereEqualTo("userId",childRef[0]);
                        studQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    studentId= objects.get(0).getObjectId().trim();
                                    classRef[0]= (ParseObject) objects.get(0).get("class");
                                    ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
                                    classQuery.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e == null) {
                                                for (int i = 0; i < objects.size(); i++) {
                                                    if (objects.get(i) == classRef[0]) {
                                                        classId = objects.get(i).getObjectId().trim();
                                                        //Log.d("classQuery", "ClassId: " + classId);
                                                        break;
                                                    }
                                                }

                                    GridView gridview = (GridView) findViewById(R.id.gridview);
                                    gridview.setAdapter(new ImageAdapter(getApplicationContext(), "Parent"));
                                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        public void onItemClick(AdapterView<?> parent, View v,
                                                                int position, long id) {
                                            if (position == 0) {
                                                Intent atten_intent = new Intent(parent_home_activity.this, view_attendance.class);
                                                atten_intent.putExtra("role", "Parent");
                                                atten_intent.putExtra("id",studentId);
                                                startActivity(atten_intent);

                                            } else if (position == 1) {
                                                Intent task_intent = new Intent(parent_home_activity.this, Tasks.class);
                                                task_intent.putExtra("role", "Parent");
                                                startActivity(task_intent);
                                            } else if (position == 2) {
                                                Intent message_intent = new Intent(parent_home_activity.this, view_messages.class);
                                                message_intent.putExtra("role", "Parent");
                                                message_intent.putExtra("_for","received");
                                                startActivity(message_intent);

                                            } else if (position == 3) {

                                                Intent exam_intent = new Intent(parent_home_activity.this, student_exams.class);
                                                exam_intent.putExtra("role", "Parent");
                                                exam_intent.putExtra("classId", classId);
                                                exam_intent.putExtra("studentId", studentId);
                                                startActivity(exam_intent);

                                            } else if (position == 4) {
                                                Intent message_intent = new Intent(parent_home_activity.this, view_messages.class);
                                                message_intent.putExtra("role", "Parent");
                                                message_intent.putExtra("_for","sent");
                                                startActivity(message_intent);

                                            } else if (position == 5) {

                                                Intent messsagetoteacher_intent = new Intent(parent_home_activity.this, message_to_teacher.class);
                                                messsagetoteacher_intent.putExtra("role", "Parent");
                                                messsagetoteacher_intent.putExtra("classId", classId);
                                                messsagetoteacher_intent.putExtra("studentId", studentId);
                                                startActivity(messsagetoteacher_intent);

                                            }
                                        }
                                    });

                                            } else {

                                                Log.d("user", "Error: in classQuery" + e.getMessage());
                                            }

                                        }
                                    });
                                } else {

                                    Log.d("user", "Error: in studQuery" + e.getMessage());
                                }

                            }
                        });
                    }


                } else {
                    Log.d("user", "Error: in ParentQuery " + e.getMessage());
                }
            }
        });

        ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(parent_home_activity.this,login.class);
            startActivity(nouser);
        }
    }

   /* public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/


}
