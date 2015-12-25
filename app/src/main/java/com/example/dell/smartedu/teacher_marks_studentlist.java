package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by Dell on 10/7/2015.
 */
public class teacher_marks_studentlist extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;

    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView studentList;
    Notification_bar noti_bar;
    String classId;
    String examId;
    TextView examname;
    EditText marksobt;
    Button addmarks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_marks_studentlist);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Students");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher");
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
        Intent from_student = getIntent();
        examId=from_student.getStringExtra("examid");
        classId = from_student.getStringExtra("classId");

        studentList = (ListView) findViewById(R.id.studentList);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);
        Toast.makeText(teacher_marks_studentlist.this, "id class selected is = " +classId, Toast.LENGTH_LONG).show();

        /*ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
        studentQuery.whereEqualTo("class",classname);
        studentQuery.whereEqualTo("teacher",ParseUser.getCurrentUser());*/
        final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
        classQuery.whereEqualTo("objectId",classId);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                    classRef[0] = studentListRet.get(0);

                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                    studentQuery.whereEqualTo("class", classRef[0]);
                    studentQuery.addAscendingOrder("rollNumber");
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, ParseException e) {
                            if (e == null) {

                                ArrayList<String> studentLt = new ArrayList<String>();
                                ArrayAdapter adapter = new ArrayAdapter(teacher_marks_studentlist.this, android.R.layout.simple_list_item_1, studentLt);
                                //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                Log.d("user", "Retrieved " + studentListRet.size() + " students");
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < studentListRet.size(); i++) {
                                    ParseObject u = (ParseObject) studentListRet.get(i);
                                    //  if(u.getString("class").equals(id)) {
                                    int rollnumber=u.getInt("rollNumber");
                                    String name = u.getString("name");
                                    name= String.valueOf(rollnumber) + ". " + u.getString("name").trim();
                                    //name += "\n";
                                    // name += u.getInt("age");

                                    adapter.add(name);
                                    // }

                                }


                                studentList.setAdapter(adapter);


                                studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String item = ((TextView) view).getText().toString();

                                        String[] itemValues = item.split(". ");

                                        final String[] details = new String[2];
                                        int j = 0;

                                        for (String x : itemValues) {
                                            details[j++] = x;
                                        }

                                        Log.d("user", "rno: " + details[0].trim()+"name "+details[1]);  //extracts Chit as Chi and query fails???

                                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                                        studentQuery.whereEqualTo("rollNumber", Integer.parseInt(details[0].trim()));
                                        studentQuery.whereEqualTo("name", details[1].trim());
                                        studentQuery.whereEqualTo("class", classRef[0]);
                                        studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                                if (e == null) {
                                                    ParseObject student =studentListRet.get(0);
                                                    //Toast.makeText(Students.this,"id of student selected is = " + id, Toast.LENGTH_LONG).show();
                                                   /* Intent to_addmarks = new Intent(teacher_marks_studentlist.this, StudentInfo.class);
                                                    to_addmarks.putExtra("examId",examId);
                                                    to_addmarks.putExtra("id", studentid);
                                                    to_addmarks.putExtra("classId",classId);
                                                    startActivity(to_addmarks);*/
                                                    addMarks(examId,student);
                                                } else {
                                                    Log.d("user", "Error: " + e.getMessage());
                                                }
                                            }
                                        });


                                    }
                                });


                            } else {
                                Toast.makeText(teacher_marks_studentlist.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });


                } else {
                    Toast.makeText(teacher_marks_studentlist.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });



        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();







    }



    protected void addMarks(String examId, final ParseObject studentobject)
    {
        final Dialog marks_add=new Dialog(teacher_marks_studentlist.this);
        marks_add.setContentView(R.layout.activity_add_marks);
        marks_add.setTitle("add marks");
        examname=(TextView)marks_add.findViewById(R.id.exam);
        marksobt = (EditText)marks_add.findViewById(R.id.marksDesc);
        addmarks=(Button)marks_add.findViewById(R.id.addMarks);
        ParseQuery<ParseObject> examQuery = ParseQuery.getQuery("Exam");
        examQuery.whereEqualTo("objectId",examId);
        examQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> examListRet, ParseException e) {
                if (e == null) {
                    final ParseObject examobject =examListRet.get(0);
                    examname.setText((CharSequence) examobject.get("examName"));






                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Marks");
                    studentQuery.whereEqualTo("exam",examobject);
                    studentQuery.whereEqualTo("student",studentobject);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> marksListRet, ParseException e) {
                            if (e == null) {
                                if(marksListRet.size()!=0)
                                {
                                    final ParseObject marksobject=marksListRet.get(0);
                                    marksobt.setText(marksobject.getNumber("marksObtained").toString().trim());


                                    addmarks.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String obtmarks = String.valueOf(marksobt.getText()).trim();
                                            if (obtmarks.equals("")) {
                                                Toast.makeText(getApplicationContext(), "no marks obtained entered", Toast.LENGTH_LONG)
                                                        .show();
                                            } else {

                                                marksobject.put("marksObtained", Float.parseFloat(obtmarks));
                                                marksobject.saveEventually();
                                                Toast.makeText(getApplicationContext(), "Marks Edited", Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                            marks_add.dismiss();
                                        }
                                    });
                                    marks_add.show();
                                }else
                                    {
                                        addmarks.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String obtmarks = String.valueOf(marksobt.getText());
                                                if (obtmarks.equals("")) {
                                                    Toast.makeText(getApplicationContext(), "no marks obtained entered", Toast.LENGTH_LONG)
                                                            .show();
                                                } else {
                                                    ParseObject marksobj = new ParseObject("Marks");
                                                    marksobj.put("exam", examobject);
                                                    marksobj.put("student", studentobject);
                                                    marksobj.put("marksObtained", Integer.parseInt(obtmarks));
                                                    marksobj.saveEventually();
                                                    Toast.makeText(getApplicationContext(), "Marks Added", Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                                marks_add.dismiss();
                                            }
                                        });
                                        marks_add.show();
                                    }
                            } else {
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });






                   /* addmarks.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String obtmarks =String.valueOf(marksobt.getText());
                            if(obtmarks.equals(""))
                            {
                                Toast.makeText(getApplicationContext(), "no marks obtained entered", Toast.LENGTH_LONG)
                                        .show();
                            }else
                            {
                                ParseObject exam = new ParseObject("Exam");
                                exam.put("exam",examobject);
                                exam.put("student", studentobject);
                                exam.put("marksObtained", Integer.parseInt(obtmarks));
                                exam.saveEventually();
                            }
                        }
                    }); */

                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(teacher_marks_studentlist.this,login.class);
            startActivity(nouser);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(teacher_marks_studentlist.this,MainActivity.class);
                startActivity(i);
                finish();
                //do your own thing here
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {

        if(position==0)
        {
            /*Intent i = new Intent(MainActivity.this,CurrentOrder.class);
            startActivity(i);*/
        }

        if(position==2)
        {
            //  Intent i = new Intent(MainActivity.this,HomeSlider.class);
            //startActivity(i);
        }

        if(position==8)
        {
            Intent i = new Intent(teacher_marks_studentlist.this,ChooseRole.class);
            startActivity(i);
        }
        if(position==9)
        {
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            Intent i = new Intent(teacher_marks_studentlist.this, login.class);
            startActivity(i);
        }
    }
}
