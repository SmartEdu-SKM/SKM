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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AddAttendance extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView classList;
    Notification_bar noti_bar;
    String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_classes);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attendance");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher");
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
        classList = (ListView) findViewById(R.id.classesList);
        role=getIntent().getStringExtra("role");
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
        classQuery.whereEqualTo("teacher", ParseUser.getCurrentUser());
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> classListRet, ParseException e) {
                if (e == null) {
                    ArrayList<String> classLt = new ArrayList<String>();
                    ArrayAdapter adapter = new ArrayAdapter(AddAttendance.this, android.R.layout.simple_list_item_1, classLt);


                    Log.d("classes", "Retrieved " + classListRet.size() + " users");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    for (int i = 0; i < classListRet.size(); i++) {
                        ParseObject u = (ParseObject) classListRet.get(i);
                        String name = u.getString("class").toString();
                        //name += "\n";
                        // name += u.getInt("age");

                        adapter.add(name);

                    }


                    classList.setAdapter(adapter);
                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }

            }
        });

        /*ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
        studentQuery.whereEqualTo("addedBy", ParseUser.getCurrentUser());
        studentQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {

                    ArrayList<String> studentLt = new ArrayList<String>();
                    ArrayAdapter adapter = new ArrayAdapter(Students.this, android.R.layout.simple_list_item_1, studentLt);


                    Log.d("user", "Retrieved " + studentListRet.size() + " users");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    for (int i = 0; i < studentListRet.size(); i++) {
                        ParseObject u = (ParseObject) studentListRet.get(i);
                        String name = u.getString("name").toString();
                        //name += "\n";
                        // name += u.getInt("age");

                        adapter.add(name);

                    }


                    studentList.setAdapter(adapter);

                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });
*/




        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = ((TextView) view).getText().toString();

                ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
                studentQuery.whereEqualTo("class", item);
                studentQuery.whereEqualTo("teacher", ParseUser.getCurrentUser());
                studentQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> classObjRet, ParseException e) {
                        if (e == null) {
                            ParseObject u = (ParseObject) classObjRet.get(0);
                            String id = u.getObjectId();
                            Toast.makeText(AddAttendance.this, "id of class selected is = " + id, Toast.LENGTH_LONG).show();
                            Intent to_student=new Intent(AddAttendance.this,AddAttendance_Students.class);
                            to_student.putExtra("id",id);
                            startActivity(to_student);
                        } else {
                            Log.d("user", "Error: " + e.getMessage());
                        }
                    }
                });


            }
        });


    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(AddAttendance.this,login.class);
            startActivity(nouser);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(AddAttendance.this,MainActivity.class);
                startActivity(i);
                finish();
                //do your own thing here
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

}