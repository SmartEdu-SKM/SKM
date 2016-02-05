package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class teacher_classes extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    String _for;
    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView classList;
    Notification_bar noti_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_teacher_classes);

            Intent from_home = getIntent();
            _for = from_home.getStringExtra("for");
            role = from_home.getStringExtra("role");
            institution_code=from_home.getStringExtra("institution_code");
            institution_name=from_home.getStringExtra("institution_name");


            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Classes");
            noti_bar = (Notification_bar) getSupportFragmentManager().findFragmentById(R.id.noti);
            noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,super.institution_name);


            dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
            classList = (ListView) findViewById(R.id.classesList);
            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, "Teacher");
            drawerFragment.setDrawerListener(this);

        }catch(Exception create_error){
            Log.d("user", "error in create teacher_classes: " + create_error.getMessage());
            Toast.makeText(teacher_classes.this,"error " + create_error, Toast.LENGTH_LONG).show();
        }

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);
        Log.d("institution", institution_code);






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














        final HashMap<String,String> classMap=new HashMap<String,String>();






        ParseQuery<ParseObject> classQueryz = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        classQueryz.whereEqualTo(ClassTable.TEACHER_USER_REF, ParseUser.getCurrentUser());
       // classQueryz.whereEqualTo(ClassTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
        classQueryz.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> classListRet, ParseException e) {
                if (e == null) {
                    ArrayList<String> classLt = new ArrayList<String>();
                    ArrayAdapter adapter = new ArrayAdapter(teacher_classes.this, android.R.layout.simple_list_item_1, classLt);


                    Log.d("classes", "Retrieved " + classListRet.size() + " users");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    for (int i = 0; i < classListRet.size(); i++) {
                        ParseObject u = (ParseObject) classListRet.get(i);
                        ParseObject classGradeObject=((ParseObject)u.get(ClassTable.CLASS_NAME));
                        try {
                            if((((ParseObject)classGradeObject.fetchIfNeeded().get(ClassGradeTable.INSTITUTION)).fetchIfNeeded().getObjectId()).equals(institution_code)) {
                                String name = classGradeObject.getString(ClassGradeTable.CLASS_GRADE);
                                //name += "\n";
                                // name += u.getInt("age");

                                String section=classGradeObject.getString(ClassGradeTable.SECTION);
                                String item=name+" "+section;
                                adapter.add(item);
                                classMap.put(item,classGradeObject.getObjectId());
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                    }


                    classList.setAdapter(adapter);
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
                studentQuery.whereEqualTo(ClassTable.CLASS_NAME, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME,classMap.get(item)));
                studentQuery.whereEqualTo(ClassTable.TEACHER_USER_REF, ParseUser.getCurrentUser());
               // studentQuery.whereEqualTo(ClassTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                studentQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> classObjRet, ParseException e) {
                        if (e == null) {
                            ParseObject u = (ParseObject) classObjRet.get(0);
                            String id = u.getObjectId();
                            Toast.makeText(teacher_classes.this, "id of class selected is = " + id, Toast.LENGTH_LONG).show();
                            if (_for.equals("students")) {
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
                                startActivity(to_message);
                            } else if (_for.equals("attendance")) {
                                Intent to_att = new Intent(teacher_classes.this, AddAttendance_everyday.class);
                                to_att.putExtra("institution_code", institution_code);
                                to_att.putExtra("institution_name", institution_name);
                                to_att.putExtra("role", role);
                                to_att.putExtra("id", id);
                                startActivity(to_att);
                            }
                        } else {
                            Log.d("user", "Error: " + e.getMessage());
                        }
                    }
                });


            }
        });















        /*classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView) view).getText().toString();

                ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                studentQuery.whereEqualTo("name", item);
                studentQuery.whereEqualTo("addedBy", ParseUser.getCurrentUser());
                studentQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> studentListRet, ParseException e) {
                        if (e == null) {
                                ParseObject u = (ParseObject) studentListRet.get(0);
                                String id = u.getObjectId();
                                //Toast.makeText(Students.this,"id of student selected is = " + id, Toast.LENGTH_LONG).show();
                            Intent  to_student_info=new Intent(teacher_classes.this,StudentInfo.class);
                            to_student_info.putExtra("id",id);
                            startActivity(to_student_info);
                        } else {
                            Log.d("user", "Error: " + e.getMessage());
                        }
                    }
                });


            }
        });*/

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(teacher_classes.this,login.class);
            startActivity(nouser);
        }
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(teacher_classes.this,MainActivity.class);
                startActivity(i);
                finish();
                //do your own thing here
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }*/


}
