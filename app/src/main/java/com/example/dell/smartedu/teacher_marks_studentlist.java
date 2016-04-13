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
import android.widget.RelativeLayout;
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

    ListView studentList;
    Notification_bar noti_bar;

    String examId;
    TextView examname;
    EditText marksobt;
    Button addmarks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_marks_studentlist);

        Intent from_student = getIntent();
        examId=from_student.getStringExtra("examid");
        classId = from_student.getStringExtra("classId");
        classGradeId= from_student.getStringExtra("classGradeId");
        role= from_student.getStringExtra("role");
        institution_name = from_student.getStringExtra("institution_name");
        institution_code=from_student.getStringExtra("institution_code");

        context = this;
        layoutLoading=(RelativeLayout) findViewById(R.id.loadingPanel);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Students");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institution_name);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);


        studentList = (ListView) findViewById(R.id.studentList);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,"Teacher");
        drawerFragment.setDrawerListener(this);


        final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        classQuery.whereEqualTo(ClassTable.OBJECT_ID,classId);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");

                    classRef[0] = studentListRet.get(0);

                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(StudentTable.TABLE_NAME);
                    studentQuery.whereEqualTo(StudentTable.CLASS_REF, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME, classGradeId));
                    studentQuery.addAscendingOrder(StudentTable.ROLL_NUMBER);
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
                                    int rollnumber=u.getInt(StudentTable.ROLL_NUMBER);
                                    String name = u.getString("name");
                                    name= String.valueOf(rollnumber) + ". " + u.getString(StudentTable.STUDENT_NAME).trim();
                                    //name += "\n";
                                    // name += u.getInt("age");

                                    adapter.add(name);
                                    // }

                                }


                                studentList.setAdapter(adapter);
                                new LoadingSyncList(context,layoutLoading,studentList).execute();


                                studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String item = ((TextView) view).getText().toString();

                                        String[] itemValues = item.split("\\. ");

                                        final String[] details = new String[2];
                                        int j = 0;

                                        for (String x : itemValues) {
                                            details[j++] = x;
                                        }

                                        Log.d("user", "rno: " + details[0].trim()+"name "+details[1]);  //extracts Chit as Chi and query fails???

                                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(StudentTable.TABLE_NAME);
                                        studentQuery.whereEqualTo(StudentTable.ROLL_NUMBER, Integer.parseInt(details[0].trim()));
                                        studentQuery.whereEqualTo(StudentTable.STUDENT_NAME, details[1].trim());
                                        studentQuery.whereEqualTo(StudentTable.CLASS_REF, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME, classGradeId));
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
                                                    new LoadingSyncList(context,layoutLoading,studentList).execute();
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
        marks_add.setTitle("Add Marks");
        examname=(TextView)marks_add.findViewById(R.id.exam);
        marksobt = (EditText)marks_add.findViewById(R.id.marksDesc);
        addmarks=(Button)marks_add.findViewById(R.id.addMarks);
        ParseQuery<ParseObject> examQuery = ParseQuery.getQuery(ExamTable.TABLE_NAME);
        examQuery.whereEqualTo(ExamTable.OBJECT_ID,examId);
        examQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> examListRet, ParseException e) {
                if (e == null) {
                    final ParseObject examobject = examListRet.get(0);
                    examname.setText((CharSequence) examobject.get(ExamTable.EXAM_NAME));


                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(MarksTable.TABLE_NAME);
                    studentQuery.whereEqualTo(MarksTable.EXAM_REF, examobject);
                    studentQuery.whereEqualTo(MarksTable.STUDENT_USER_REF, studentobject);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> marksListRet, ParseException e) {
                            if (e == null) {
                                if (marksListRet.size() != 0) {
                                    final ParseObject marksobject = marksListRet.get(0);
                                    marksobt.setText(marksobject.getNumber(MarksTable.MARKS_OBTAINED).toString().trim());


                                    addmarks.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String obtmarks = String.valueOf(marksobt.getText()).trim();
                                            if (obtmarks.equals("")) {
                                                Toast.makeText(getApplicationContext(), "no marks obtained entered", Toast.LENGTH_LONG)
                                                        .show();
                                            } else {

                                                marksobject.put(MarksTable.MARKS_OBTAINED, Float.parseFloat(obtmarks));
                                                marksobject.saveEventually();
                                                Toast.makeText(getApplicationContext(), "Marks Edited", Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                            marks_add.dismiss();
                                        }
                                    });
                                    marks_add.show();
                                } else {
                                    addmarks.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String obtmarks = String.valueOf(marksobt.getText());
                                            if (obtmarks.equals("")) {
                                                Toast.makeText(getApplicationContext(), "no marks obtained entered", Toast.LENGTH_LONG)
                                                        .show();
                                            } else {
                                                ParseObject marksobj = new ParseObject(MarksTable.TABLE_NAME);
                                                marksobj.put(MarksTable.EXAM_REF, examobject);
                                                marksobj.put(MarksTable.STUDENT_USER_REF, studentobject);
                                                marksobj.put(MarksTable.MARKS_OBTAINED, Integer.parseInt(obtmarks));
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


}
