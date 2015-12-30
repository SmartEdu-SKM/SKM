package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class student_exams extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button addExamButton;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView examsList;
    Notification_bar noti_bar;
    String classId;
    String studentId;
    String examid;
    String examName;

    Number totalMarks;
    Number marksObtained;

    TextView myExamName;
    TextView myTotalMarks;
    TextView myMarksObtained;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_exams);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Exams");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Student");
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
        Intent from_student = getIntent();
        classId = from_student.getStringExtra("classId");
        studentId= from_student.getStringExtra("studentId");
        addExamButton = (Button)findViewById(R.id.addExam);
        examsList = (ListView) findViewById(R.id.examList);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);

        /*ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
        studentQuery.whereEqualTo("class",classname);
        studentQuery.whereEqualTo("teacher",ParseUser.getCurrentUser());*/
        final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
        classQuery.whereEqualTo("objectId", classId);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                    classRef[0] = studentListRet.get(0);

                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Exam");
                    studentQuery.whereEqualTo("class", classRef[0]);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> examListRet, ParseException e) {
                            if (e == null) {

                                ArrayList<String> studentLt = new ArrayList<String>();
                                //ArrayAdapter adapter = new ArrayAdapter(teacher_exams.this, android.R.layout.simple_list_item_1, studentLt);
                                //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        getApplicationContext(), android.R.layout.simple_list_item_1, studentLt) {

                                    @Override
                                    public View getView(int position, View convertView,
                                                        ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);

                                        TextView textView = (TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
                                        textView.setTextColor(Color.WHITE);

                                        return view;
                                    }
                                };


                                Log.d("user", "Retrieved " + examListRet.size() + " students");
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < examListRet.size(); i++) {
                                    ParseObject u = (ParseObject) examListRet.get(i);
                                    //  if(u.getString("class").equals(id)) {
                                    String name = u.getString("examName");
                                    //name += "\n";
                                    // name += u.getInt("age");

                                    adapter.add(name);
                                    // }

                                }


                                examsList.setAdapter(adapter);


                                examsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                        String item = ((TextView) view).getText().toString().trim();

                                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Exam");
                                        studentQuery.whereEqualTo("examName", item);
                                        studentQuery.whereEqualTo("class", classRef[0]);
                                        studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> examListRet, ParseException e) {
                                                if (e == null) {
                                                    ParseObject u = examListRet.get(0);
                                                    examid = u.getObjectId();
                                                    Log.d("user", "examId: " + examid);
                                                    Log.d("user", "examId: " + studentId);
                                                    examName = u.get("examName").toString().trim();
                                                    totalMarks = u.getNumber("totalMarks");

                                                    ParseQuery<ParseObject> marksQuery = ParseQuery.getQuery("Marks");
                                                    marksQuery.whereEqualTo("student", ParseObject.createWithoutData("Student", studentId));
                                                    marksQuery.whereEqualTo("exam", ParseObject.createWithoutData("Exam", examid));
                                                    marksQuery.findInBackground(new FindCallback<ParseObject>() {
                                                        public void done(List<ParseObject> marksListRet, ParseException e) {
                                                            if (e == null) {

                                                                if(marksListRet.size()!=0) {
                                                                    marksObtained = marksListRet.get(0).getNumber("marksObtained");
                                                                    callDialog(view);
                                                                }
                                                                else{
                                                                    Toast.makeText(student_exams.this, "Not Yet Added", Toast.LENGTH_LONG).show();
                                                                }

                                                            } else {
                                                                Log.d("user", "ErrorIn: " + e.getMessage());
                                                            }
                                                        }
                                                    });

                                                            } else {
                                                                Log.d("user", "ErrorOut: " + e.getMessage());
                                                            }
                                                        }
                                                    });


                                                }
                                            }

                                            );


                                        }else{
                                            Toast.makeText(student_exams.this, "error", Toast.LENGTH_LONG).show();
                                            Log.d("user", "Error: " + e.getMessage());
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(student_exams.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });



        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();




    }

    public void callDialog(View view){
        Dialog dialog = new Dialog(student_exams.this);
        dialog.setContentView(R.layout.view_marks);
        dialog.setTitle("Marks Information");

        myExamName= (TextView) dialog.findViewById(R.id.exam);
        myMarksObtained= (TextView) dialog.findViewById(R.id.obtained);
        myTotalMarks= (TextView) dialog.findViewById(R.id.totalMarks);
        myExamName.setText(examName);
        myMarksObtained.setText(marksObtained.toString());
        myTotalMarks.setText(totalMarks.toString());

        dialog.dismiss();

        dialog.show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(student_exams.this,login.class);
            startActivity(nouser);
        }
    }


}
