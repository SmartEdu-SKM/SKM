package com.example.dell.smartedu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class SelectSubject extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    String _for;
    String classGradeId;
    MyDBHandler dbHandler;
    Activity context;

    ListView classList;
    RelativeLayout layout;

    Notification_bar noti_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        try {
            //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
            setContentView(R.layout.activity_select_subject);
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

            Intent from_home = getIntent();
            _for = from_home.getStringExtra("_for");
            role = from_home.getStringExtra("role");
            institution_code = from_home.getStringExtra("institution_code");
            institution_name = from_home.getStringExtra("institution_name");
            classGradeId = from_home.getStringExtra("classGradeId");


            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Subjects");
            noti_bar = (Notification_bar) getSupportFragmentManager().findFragmentById(R.id.noti);
            noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role, super.institution_name);


            dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
            classList = (ListView) findViewById(R.id.classesList);
            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, "Teacher");
            drawerFragment.setDrawerListener(this);

            layout = (RelativeLayout) findViewById(R.id.loadingPanel);

            // findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        } catch (Exception create_error) {
            Log.d("user", "error in create teacher_classes: " + create_error.getMessage());
            Toast.makeText(SelectSubject.this, "error " + create_error, Toast.LENGTH_LONG).show();
        }

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);
//        Log.d("institution", institution_code);






/*        ParseQuery<ParseObject> classGradeQuery=ParseQuery.getQuery(ClassGradeTable.TABLE_NAME);
        classGradeQuery.whereEqualTo(ClassGradeTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
        classGradeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> classgradeobjects, ParseException e) {
                if (e == null) {
                    if (classgradeobjects.size() != 0) {
                        HashMap<String, String> classGradeMap = new HashMap<String, String>();
                        for (int x = 0; x < classgradeobjects.size(); x++) {
                            ParseObject u = classgradeobjects.get(x);
                            String name = u.getString(ClassGradeTable.CLASS_GRADE);
                            if (classGradeMap.get(name) == null) {
                                classGradeMap.put(name, "1");
                            }
                        }
                    } else {
                        Toast.makeText(teacher_classes.this, "no classes added for this institution", Toast.LENGTH_LONG).show();
                        Log.d("classGrade", "error in query");
                    }
                } else {
                    Log.d("classGrade", "error");
                }
            }
        });
*/


        //new LoadingSyncList(layout,classList).execute();

        final HashMap<String, String> classMap = new HashMap<String, String>();


        ParseQuery<ParseObject> classQueryz = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        classQueryz.whereEqualTo(ClassTable.TEACHER_USER_REF, ParseUser.getCurrentUser());
        classQueryz.whereEqualTo(ClassTable.CLASS_NAME, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME, classGradeId));
        classQueryz.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> classListRet, ParseException e) {
                if (e == null) {
                    ArrayList<String> classLt = new ArrayList<String>();
                    ArrayAdapter adapter = new ArrayAdapter(SelectSubject.this, android.R.layout.simple_list_item_1, classLt);


                    Log.d("classes", "Retrieved " + classListRet.size() + " users");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    for (int i = 0; i < classListRet.size(); i++) {
                        ParseObject u = (ParseObject) classListRet.get(i);

                        String name = u.getString(ClassTable.SUBJECT);
                        //name += "\n";
                        // name += u.getInt("age");


                        adapter.add(name);
                        //new LoadingSyncList(layout,classList,adapter).execute();
                        classMap.put(name, u.getObjectId());


                    }


                    classList.setAdapter(adapter);
                    new LoadingSyncList(context, layout, classList).execute();

                    //findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }

            }
        });


        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = ((TextView) view).getText().toString();
                //String[] classSpecs=item.split(" ");
                ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
                studentQuery.whereEqualTo(ClassTable.OBJECT_ID, classMap.get(item));
                //studentQuery.whereEqualTo(ClassTable.TEACHER_USER_REF, ParseUser.getCurrentUser());
                // studentQuery.whereEqualTo(ClassTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                studentQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> classObjRet, ParseException e) {
                        if (e == null) {
                            ParseObject u = (ParseObject) classObjRet.get(0);
                            String id = u.getObjectId();
                            // Toast.makeText(teacher_classes.this, "id of class selected is = " + id, Toast.LENGTH_LONG).show();
                           /* if (_for.equals("students")) {
                                Intent to_student = new Intent(teacher_classes.this, Students.class);
                                to_student.putExtra("institution_code", institution_code);
                                to_student.putExtra("institution_name", institution_name);
                                to_student.putExtra("role", role);
                                to_student.putExtra("id", id);
                                startActivity(to_student);
                            } else if (_for.equals("exam")) {
                                Intent to_exams = new Intent(teacher_classes.this, teacher_exams.class);
                                to_exams.putExtra("institution_code", institution_code);
                                to_exams.putExtra("institution_name", institution_name);
                                to_exams.putExtra("role", role);
                                to_exams.putExtra("id", id);
                                startActivity(to_exams);
                            } else if (_for.equals("upload")) {
                                Intent to_uploads = new Intent(teacher_classes.this, UploadMaterial.class);
                                to_uploads.putExtra("institution_code", institution_code);
                                to_uploads.putExtra("institution_name", institution_name);
                                to_uploads.putExtra("role", role);
                                to_uploads.putExtra("id", id);
                                startActivity(to_uploads);
                            } else if (_for.equals("message")) {
                                Intent to_message = new Intent(teacher_classes.this, teacher_message.class);
                                to_message.putExtra("institution_code", institution_code);
                                to_message.putExtra("institution_name", institution_name);
                                to_message.putExtra("role", role);
                                to_message.putExtra("id", id);
                                to_message.putExtra("classGradeId", classMap.get(item));
                                to_message.putExtra("_for", _for);
                                startActivity(to_message);
                            } else */
                            if (_for.equals("attendance")) {
                                Intent to_att = new Intent(SelectSubject.this, AddAttendance_everyday.class);
                                to_att.putExtra("institution_code", institution_code);
                                to_att.putExtra("institution_name", institution_name);
                                to_att.putExtra("role", role);
                                to_att.putExtra("classId", id);
                                to_att.putExtra("classGradeId", classGradeId);
                                startActivity(to_att);
                            } else if (_for.equals("upload")) {
                                Intent to_uploads = new Intent(SelectSubject.this, UploadMaterial.class);
                                to_uploads.putExtra("institution_code", institution_code);
                                to_uploads.putExtra("institution_name", institution_name);
                                to_uploads.putExtra("role", role);
                                to_uploads.putExtra("classGradeId",classGradeId);
                                to_uploads.putExtra("classId", id);
                                startActivity(to_uploads);
                            }

                            else if (_for.equals("exam")) {
                                Intent to_exams = new Intent(SelectSubject.this, teacher_exams.class);
                                to_exams.putExtra("institution_code", institution_code);
                                to_exams.putExtra("institution_name", institution_name);
                                to_exams.putExtra("role", role);
                                to_exams.putExtra("classId", id);
                                to_exams.putExtra("classGradeId", classGradeId);
                                startActivity(to_exams);
                            }
                        }else {
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    }

                    );


                }
            }

            );


        }

        @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(SelectSubject.this,login.class);
            startActivity(nouser);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent tohome = new Intent(SelectSubject.this, teacher_classes.class);
        tohome.putExtra("role",role);
        tohome.putExtra("institution_name",institution_name);
        tohome.putExtra("institution_code",institution_code);
        tohome.putExtra("for","exam");
        startActivity(tohome);

    }

}
