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

public class student_home_activity extends BaseActivity{

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    ArrayList<Task> myList;

    MyDBHandler dbHandler;
    Notification_bar noti_bar;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home_activity);

        Intent home=getIntent();
        role=home.getStringExtra("role");
        institution_name=home.getStringExtra("institution_name");
        institution_code=home.getStringExtra("institution_code");
        Log.d("user",role);

        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(),role,institution_name);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Dashboard");



        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);

        final GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getApplicationContext(),densityX,densityY, role));

        final ParseObject[] classGradeRef = new ParseObject[1];
        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(StudentTable.TABLE_NAME);
        studentQuery.whereEqualTo(StudentTable.STUDENT_USER_REF, ParseUser.getCurrentUser());
        studentQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studListRet, ParseException e) {
                if (e == null) {

                    if(studListRet.size()!=0){


                        for(int x=0;x<studListRet.size();x++)
                        {
                            ParseObject u = (ParseObject) studListRet.get(x);

                            ParseObject classGradeobject= ( (ParseObject)u.get(StudentTable.CLASS_REF) );
                            try {
                                ParseObject test_insti=(ParseObject)classGradeobject.fetchIfNeeded().get(ClassGradeTable.INSTITUTION);
                                if(test_insti.fetchIfNeeded().getString(InstitutionTable.INSTITUTION_NAME).equals(institution_name))
                                {
                                    studentId = u.getObjectId();
                                    classGradeRef[0] = classGradeobject;
                                    break;
                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                        }

                        try {
                            ParseObject i=(ParseObject) classGradeRef[0].fetchIfNeeded().get(ClassGradeTable.INSTITUTION);
                            Log.d("insti", i.getString(InstitutionTable.INSTITUTION_NAME));
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        classGradeId=classGradeRef[0].getObjectId();

                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        public void onItemClick(AdapterView<?> parent, View v,
                                                                int position, long id) {
                                            if (position == 0) {

                                                Intent atten_intent = new Intent(student_home_activity.this, student_classes.class);

                                                atten_intent.putExtra("role", role);
                                                atten_intent.putExtra("studentId", studentId);
                                                atten_intent.putExtra("classGradeId", classGradeId);
                                                atten_intent.putExtra("institution_code",institution_code);
                                                atten_intent.putExtra("institution_name",institution_name);
                                                atten_intent.putExtra("for", "attendance");
                                                startActivity(atten_intent);

                                            } else if (position == 1) {
                                                Intent task_intent = new Intent(student_home_activity.this, Tasks.class);
                                                task_intent.putExtra("institution_code",institution_code);
                                                task_intent.putExtra("institution_name",institution_name);
                                                task_intent.putExtra("role", role);
                                                task_intent.putExtra("studentId", studentId);
                                                task_intent.putExtra("classGradeId", classGradeId);
                                                startActivity(task_intent);
                                            } else if (position == 2) {
                                                Intent message_intent = new Intent(student_home_activity.this, view_messages.class);
                                                message_intent.putExtra("role", role);
                                                message_intent.putExtra("classGradeId", classGradeId);
                                                message_intent.putExtra("studentId", studentId);
                                                message_intent.putExtra("institution_name", institution_name);
                                                message_intent.putExtra("institution_code", institution_code);
                                                message_intent.putExtra("for","received");
                                                startActivity(message_intent);

                                            } else if (position == 3) {
                                                Intent schedule_intent = new Intent(student_home_activity.this, Schedule.class);
                                                schedule_intent.putExtra("institution_name",institution_name);
                                                schedule_intent.putExtra("institution_code",institution_code);
                                                schedule_intent.putExtra("role",role);
                                                schedule_intent.putExtra("studentId", studentId);
                                                startActivity(schedule_intent);

                                            } else if (position == 4) {
                                                Intent exam_intent = new Intent(student_home_activity.this, student_exams.class);
                                                exam_intent.putExtra("institution_name", institution_name);
                                                exam_intent.putExtra("institution_code", institution_code);
                                                exam_intent.putExtra("role", role);
                                                exam_intent.putExtra("classGradeId", classGradeId);
                                                exam_intent.putExtra("studentId", studentId);
                                                startActivity(exam_intent);

                                            } else if (position == 5) {
                                                Intent upload_intent = new Intent(student_home_activity.this, student_classes.class);
                                                upload_intent.putExtra("institution_name", institution_name);
                                                upload_intent.putExtra("institution_code", institution_code);
                                                upload_intent.putExtra("role", role);
                                                upload_intent.putExtra("classGradeId", classGradeId);
                                                upload_intent.putExtra("for", "upload");
                                                upload_intent.putExtra("studentId", studentId);
                                                startActivity(upload_intent);
                                            }else if (position == 6) {


                                            }else if (position == 7) {

                                            }
                                        }
                                    });






                }


                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

        //Log.d("userStudent", "Id: " + studentId);



                    ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());

                }

    @Override
    protected void onPostResume () {
        super.onPostResume();
        if (ParseUser.getCurrentUser() == null) {
            Intent nouser = new Intent(student_home_activity.this, login.class);
            startActivity(nouser);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


 }
