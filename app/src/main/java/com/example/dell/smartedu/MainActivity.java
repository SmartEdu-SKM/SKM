package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.parse.ParseUser;

import java.util.ArrayList;

public class MainActivity extends BaseActivity{

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    ArrayList<Task> myList;

    MyDBHandler dbHandler;
    Notification_bar noti_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Dashboard");



        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this,"Teacher"));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (position == 0) {
                    Intent attendance_intent = new Intent(MainActivity.this, AddAttendance_Students.class);
                   attendance_intent.putExtra("role","Teacher");
                    startActivity(attendance_intent);

                } else if (position == 1) {
                    Intent task_intent = new Intent(MainActivity.this, Tasks.class);
                    task_intent.putExtra("role","Teacher");
                    startActivity(task_intent);
                } else if (position == 2) {
                    Intent student_intent = new Intent(MainActivity.this, teacher_classes.class);
                    student_intent.putExtra("for","students");
                    startActivity(student_intent);
                } else if (position == 3) {
                    Intent schedule_intent = new Intent(MainActivity.this, Schedule.class);
                    schedule_intent.putExtra("role","Teacher");
                    startActivity(schedule_intent);
                } else if (position == 4) {
                    Intent addmarks_intent = new Intent(MainActivity.this, teacher_classes.class);
                    addmarks_intent.putExtra("role","Teacher");
                    addmarks_intent.putExtra("for","exam");
                    startActivity(addmarks_intent);
                } else if (position == 5) {
                    Intent upload_intent = new Intent(MainActivity.this, teacher_classes.class);
                    upload_intent.putExtra("role","Teacher");
                    upload_intent.putExtra("for","upload");
                    startActivity(upload_intent);

                }else if (position == 6) {

                    Intent message_intent = new Intent(MainActivity.this, teacher_classes.class);
                    message_intent.putExtra("role","Teacher");
                    message_intent.putExtra("for","message");
                    startActivity(message_intent);
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
            Intent nouser=new Intent(MainActivity.this,login.class);
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
