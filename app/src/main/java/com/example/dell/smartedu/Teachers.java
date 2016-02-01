package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class Teachers extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button addTeacherButton;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;

    Notification_bar noti_bar;
    String classId;
    String name;
    Integer age;

    Button createIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);

        Intent from_student = getIntent();

        role=from_student.getStringExtra("role");
        institution_code=from_student.getStringExtra("institution_code");
        institution_name=from_student.getStringExtra("institution_name");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Teachers");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institution_name);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        addTeacherButton = (Button)findViewById(R.id.addTeacher);

        createIDs=(Button)findViewById(R.id.shareCode);
        //createIDs.setVisibility(View.INVISIBLE);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);


        /*ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
        studentQuery.whereEqualTo("class",classname);
        studentQuery.whereEqualTo("teacher",ParseUser.getCurrentUser());*/




        addTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Teachers.this, NewTeacher.class);
                i.putExtra("institution_name",institution_name);
                i.putExtra("institution_code",institution_code);
                i.putExtra("id",classId);
                startActivity(i);
            }
        });

        createIDs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCode();
            }
        });



    }

    public void shareCode(){

        final String[] instituteName = {null};
        ParseQuery institution_admin_query=ParseQuery.getQuery(InstitutionTable.TABLE_NAME);
        institution_admin_query.whereEqualTo(InstitutionTable.ADMIN_USER, ParseUser.getCurrentUser());
        institution_admin_query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> institutionListRet, ParseException e) {
                if (e == null) {

                    if (institutionListRet.size() != 0) {

                        final ParseObject institute = institutionListRet.get(0);
                        instituteName[0] =institute.getString(InstitutionTable.INSTITUTION_NAME);

                        ParseQuery<ParseObject> teacherQuery = ParseQuery.getQuery(TeacherTable.TABLE_NAME);
                        teacherQuery.whereEqualTo(TeacherTable.INSTITUTION, null);
                        teacherQuery.whereEqualTo(TeacherTable.INSTITUTION_NAME, instituteName[0]);
                        teacherQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> teacheListRet, ParseException e) {
                                if (e == null) {
                                    if (teacheListRet.size() != 0) {

                                        for (int i = 0; i < teacheListRet.size(); i++) {
                                            ParseObject u = teacheListRet.get(i);

                                            u.put(TeacherTable.INSTITUTION, institute);
                                            u.put(TeacherTable.BY_USER_REF,ParseUser.getCurrentUser());
                                            u.saveEventually();
                                            Log.d("shareCode", "Main: ");
                                            // u.put("addedBy", ParseUser.getCurrentUser());
                                            //u.saveEventually();
                                        }
                                        Toast.makeText(Teachers.this, "Done", Toast.LENGTH_LONG).show();
                                    }


                                } else {
                                    //Toast.makeText(NewStudent.this, "errorInner", Toast.LENGTH_LONG).show();
                                    Log.d("user", "Error: " + e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    Log.d("institution", "error");
                }


            }

        });


    }

                protected void sleep ( int time)
                {
                    for (int x = 0; x < time; x++) {

                    }
                }


                @Override
                protected void onPostResume () {
                    super.onPostResume();
                    if (ParseUser.getCurrentUser() == null) {
                        Intent nouser = new Intent(Teachers.this, login.class);
                        startActivity(nouser);
                    }
                }

                @Override
                public boolean onOptionsItemSelected (MenuItem item){
                    switch (item.getItemId()) {
                        case android.R.id.home:
                            Intent i = new Intent(Teachers.this, MainActivity.class);
                            startActivity(i);
                            finish();
                            //do your own thing here
                            return true;
                        default:
                            return super.onOptionsItemSelected(item);
                    }
                }


            }
