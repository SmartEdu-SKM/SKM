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

    String classGradeId;
    String studentId;
    MyDBHandler dbHandler;
    Notification_bar noti_bar;
//String child_code;
String child_username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home_activity);

        Intent home=getIntent();

        role=home.getStringExtra("role");
        institution_name=home.getStringExtra("institution_name");
        institution_code=home.getStringExtra("institution_code");
        child_username=home.getStringExtra("child_username");

        Log.d("user",role);

        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institution_name);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Dashboard");



        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);

        final ParseObject[] childRef = new ParseObject[1];
        final ParseObject[] classGradeRef = new ParseObject[1];
        final GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getApplicationContext(), densityX,densityY, "Parent"));


        ParseQuery<ParseUser> studentQuery = ParseUser.getQuery();
        studentQuery.whereEqualTo("username", child_username);
        studentQuery.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> studListRet, ParseException e) {
                if (e == null) {

                    if (studListRet.size() != 0) {

                        ParseUser u = studListRet.get(0);
                        childRef[0] = u;

                        ParseQuery<ParseObject> studQuery = ParseQuery.getQuery(StudentTable.TABLE_NAME);
                        studQuery.whereEqualTo(StudentTable.STUDENT_USER_REF, childRef[0]);
                        studQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {

                                    for (int x = 0; x < objects.size(); x++) {
                                        ParseObject u = (ParseObject) objects.get(x);

                                        ParseObject classGradeobject = ((ParseObject) u.get(StudentTable.CLASS_REF));
                                        try {
                                            //ParseObject classGradeobject=(ParseObject) for_class_check.fetchIfNeeded().get(ClassTable.CLASS_NAME);
                                            ParseObject test_insti = (ParseObject) classGradeobject.fetchIfNeeded().get(ClassGradeTable.INSTITUTION);
                                            if (test_insti.fetchIfNeeded().getString(InstitutionTable.INSTITUTION_NAME).equals(institution_name)) {
                                                studentId = u.getObjectId();
                                                classGradeRef[0] = classGradeobject;
                                                break;
                                            }
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }

                                    }

                                 /*   try {
                                        Log.d("insti", ((ParseObject) ((ParseObject) classRef[0].fetchIfNeeded().get(ClassTable.CLASS_NAME)).get(ClassGradeTable.INSTITUTION)).getString(InstitutionTable.INSTITUTION_NAME));
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    } */

                                    classGradeId = classGradeRef[0].getObjectId();


                                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        public void onItemClick(AdapterView<?> parent, View v,
                                                                int position, long id) {
                                            if (position == 0) {
                                                Intent atten_intent = new Intent(parent_home_activity.this, student_classes.class);
                                                atten_intent.putExtra("role", "Parent");
                                                atten_intent.putExtra("studentId", studentId);
                                                atten_intent.putExtra("for","attendance");
                                                atten_intent.putExtra("classGradeId", classGradeId);
                                                atten_intent.putExtra("institution_code",institution_code);
                                                atten_intent.putExtra("institution_name",institution_name);
                                                atten_intent.putExtra("child_username",child_username);
                                                startActivity(atten_intent);

                                            } else if (position == 1) {
                                                Intent task_intent = new Intent(parent_home_activity.this, Tasks.class);
                                                task_intent.putExtra("institution_name",institution_name);
                                                task_intent.putExtra("institution_code",institution_code);
                                                task_intent.putExtra("role","Parent");
                                                task_intent.putExtra("child_username",child_username);
                                                startActivity(task_intent);
                                            } else if (position == 2) {
                                                Intent message_intent = new Intent(parent_home_activity.this, view_messages.class);
                                                message_intent.putExtra("role", "Parent");
                                                message_intent.putExtra("classGradeId", classGradeId);
                                                message_intent.putExtra("studentId", studentId);
                                                message_intent.putExtra("institution_name", institution_name);
                                                message_intent.putExtra("institution_code", institution_code);
                                                message_intent.putExtra("child_username",child_username);
                                                message_intent.putExtra("for", "received");
                                                startActivity(message_intent);

                                            } else if (position == 3) {

                                                Intent exam_intent = new Intent(parent_home_activity.this, student_exams.class);
                                                exam_intent.putExtra("role", "Parent");
                                                exam_intent.putExtra("institution_name", institution_name);
                                                exam_intent.putExtra("institution_code", institution_code);
                                                exam_intent.putExtra("classGradeId", classGradeId);
                                                exam_intent.putExtra("studentId", studentId);
                                                startActivity(exam_intent);

                                            } else if (position == 4) {


                                            } else if (position == 5) {


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
