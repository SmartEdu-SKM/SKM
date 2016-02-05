package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class Teachers extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;

    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;

    Notification_bar noti_bar;
    String classId;
    String name;
    Integer age;

    Button addTeacherButton;
    Button createIDs;
    Button delButton;
    ListView teacherList;

    TextView Name;
    TextView Age;


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

        teacherList = (ListView) findViewById(R.id.teacherList);
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

        final HashMap<String,String> teacherMap=new HashMap<String,String>();

        ParseQuery<ParseObject> teacherQuery = ParseQuery.getQuery(TeacherTable.TABLE_NAME);
        teacherQuery.whereEqualTo(TeacherTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
        teacherQuery.addAscendingOrder(TeacherTable.SERIAL_NUMBER);
        teacherQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> teacherListRet, ParseException e) {
                if (e == null) {

                    ArrayList<String> teacherLt = new ArrayList<String>();
                    ArrayAdapter adapter = new ArrayAdapter(Teachers.this, android.R.layout.simple_list_item_1, teacherLt);
                    //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                    Log.d("user", "Retrieved " + teacherListRet.size() + " students");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    for (int i = 0; i < teacherListRet.size(); i++) {
                        ParseObject u = (ParseObject) teacherListRet.get(i);
                        //  if(u.getString("class").equals(id)) {
                        int serialno = u.getInt(TeacherTable.SERIAL_NUMBER);
                        String name = u.getString(TeacherTable.TEACHER_NAME);
                        name = String.valueOf(serialno) + ". " + name;
                        //name += "\n";
                        // name += u.getInt("age");

                        adapter.add(name);
                        teacherMap.put(name.trim(),u.getObjectId());

                        // }

                    }


                    teacherList.setAdapter(adapter);
                    createIDs.setVisibility(View.VISIBLE);
                    createIDs.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareCode();
                        }
                    });


                    teacherList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String item = ((TextView) view).getText().toString();
                            Log.d("user", item);

                            //item  = item.replaceAll("[\n\r\\s]", "");
                            String[] itemValues = item.split("\\. ");

                            final String[] details = new String[2];
                            int j = 0;
                            for (int i = 0; i <= 1; i++) {
                                Log.d("user", itemValues[i]);
                            }

                            for (String x : itemValues) {
                                details[j++] = x;
                            }

                            Log.d("user", "rno: " + details[0].trim() + "name " + details[1]);

                            ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(TeacherTable.TABLE_NAME);
                            studentQuery.whereEqualTo(TeacherTable.OBJECT_ID, teacherMap.get(item.trim()));
                            studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> studentListRet, ParseException e) {
                                    if (e == null) {
                                        if (studentListRet.size() != 0) {
                                            ParseObject u = (ParseObject) studentListRet.get(0);
                                            final String id = u.getObjectId();
                                           // Toast.makeText(Teachers.this, "id of student selected is = " + id, Toast.LENGTH_LONG).show();

                                            final Dialog dialog = new Dialog(Teachers.this);
                                            dialog.setContentView(R.layout.view_teacher_details);
                                            dialog.setTitle("Teacher Details");

                                            setDialogSize(dialog);

                                            Name = (TextView) dialog.findViewById(R.id.name);
                                            Age = (TextView) dialog.findViewById(R.id.age);
                                            delButton = (Button) dialog.findViewById(R.id.delButton);

                                            Name.setText(details[1]);
                                            Age.setText(u.get(TeacherTable.TEACHER_AGE).toString());

                                            delButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ParseObject.createWithoutData(TeacherTable.TABLE_NAME, id).deleteEventually();

                                                    onRestart();


                                                    dialog.dismiss();
                                                }
                                            });
                                        dialog.show();

                                        }
                                    } else {
                                        Log.d("user", "Error: " + e.getMessage());
                                    }
                                }
                            });


                        }
                    });


                } else {
                    Toast.makeText(Teachers.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });







        addTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Teachers.this, NewTeacher.class);
                i.putExtra("institution_name",institution_name);
                i.putExtra("institution_code",institution_code);
                i.putExtra("role",role);
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
                        Log.d("shareCode", "insti size: "+institutionListRet.size());

                        final ParseObject institute = institutionListRet.get(0);
                        instituteName[0] =institute.getString(InstitutionTable.INSTITUTION_NAME);

                        ParseQuery<ParseObject> teacherQuery = ParseQuery.getQuery(TeacherTable.TABLE_NAME);
                        teacherQuery.whereEqualTo(TeacherTable.INSTITUTION, null);
                        teacherQuery.whereEqualTo(TeacherTable.TEACHER_USER_REF, null);
                        teacherQuery.whereEqualTo(TeacherTable.INSTITUTION_NAME, instituteName[0]);
                        teacherQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> teacheListRet, ParseException e) {
                                if (e == null) {
                                    if(teacheListRet.size()==0){
                                        Toast.makeText(Teachers.this, "No ID to be added. Already Updated", Toast.LENGTH_LONG).show();
                                    }

                                    else{
                                        Log.d("shareCode", "insti size: "+teacheListRet.size());
                                        for (int i = 0; i < teacheListRet.size(); i++) {
                                            ParseObject u = teacheListRet.get(i);

                                            final String sessionToken = ParseUser.getCurrentUser().getSessionToken();
                                            addTeacherUser(u.getString(TeacherTable.TEACHER_NAME), u.getInt(TeacherTable.TEACHER_AGE), sessionToken, u,institute);
                                            sleep(3000);


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

    protected void sleep(int time)
    {
        for(int x=0;x<time;x++)
        {

        }
    }
    protected void addTeacherUser(final String Name, int Age, final String presession, final ParseObject teacher, final ParseObject institute)
    {
        final ParseUser[] userRef = {new ParseUser()};
        // Set up a new Parse user
        final ParseUser user_teacher = new ParseUser();
        user_teacher.setUsername(Name + institution_name);
        user_teacher.setPassword(institution_name + Name + institution_name);


        user_teacher.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {// Handle the response

                if (e != null) {
                    // Show the error message
                    Toast.makeText(Teachers.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    userRef[0] = user_teacher;
                    //  Toast.makeText(NewStudent.this, "Student User made " + " " + user_student.getObjectId(), Toast.LENGTH_LONG).show();
                    Log.d("role", "added Teacher role of " + user_teacher.getObjectId());
                    ParseObject roleobject = new ParseObject(RoleTable.TABLE_NAME);
                    roleobject.put(RoleTable.OF_USER_REF, user_teacher);
                    roleobject.put(RoleTable.ROLE, "Teacher");
                    roleobject.put(RoleTable.ENROLLED_WITH, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                    roleobject.saveInBackground();


                    try {

                        ParseUser.become(presession);
                        addTeacher(userRef[0], teacher, institute);
                    } catch (ParseException e1) {
                        Toast.makeText(Teachers.this, "cant add teacher",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

    }




    protected void addTeacher(final ParseUser userRef, ParseObject teacher, ParseObject institute){


        teacher.put(TeacherTable.INSTITUTION, institute);
        teacher.put(TeacherTable.BY_USER_REF, ParseUser.getCurrentUser());
        teacher.put(TeacherTable.TEACHER_USER_REF, userRef);
        teacher.saveEventually();

        Toast.makeText(getApplicationContext(), "Teacher details successfully stored", Toast.LENGTH_LONG).show();


    }



    @Override
    protected void onRestart() {
        super.onRestart();
        Intent to_teachers = new Intent(Teachers.this, Teachers.class);
        to_teachers.putExtra("institution_name", institution_name);
        to_teachers.putExtra("institution_code", institution_code);
        to_teachers.putExtra("role", role);
        startActivity(to_teachers);
        finish();

    }

            @Override
            protected void onPostResume () {
                    super.onPostResume();
                    if (ParseUser.getCurrentUser() == null) {
                        Intent nouser = new Intent(Teachers.this, login.class);
                        startActivity(nouser);
                    }
                }


            }
