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
public class teacher_exams extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button addExamButton;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView examsList;
    Notification_bar noti_bar;
    String classId;
    String classGradeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_exams);

        Intent from_student = getIntent();
        classId = from_student.getStringExtra("classId");
        classGradeId = from_student.getStringExtra("classGradeId");
        role=from_student.getStringExtra("role");
        institution_name = from_student.getStringExtra("institution_name");
        institution_code=from_student.getStringExtra("institution_code");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Exams");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role, institution_name);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        addExamButton = (Button)findViewById(R.id.addExam);
        examsList = (ListView) findViewById(R.id.examList);

        context = this;
        layoutLoading=(RelativeLayout) findViewById(R.id.loadingPanel);

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
        final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        classQuery.whereEqualTo(ClassTable.OBJECT_ID,classId);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                    classRef[0] = studentListRet.get(0);

                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(ExamTable.TABLE_NAME);
                    studentQuery.whereEqualTo(ExamTable.FOR_CLASS, classRef[0]);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> examListRet, ParseException e) {
                            if (e == null) {

                                ArrayList<String> studentLt = new ArrayList<String>();
                                ArrayAdapter adapter = new ArrayAdapter(teacher_exams.this, android.R.layout.simple_list_item_1, studentLt);
                                //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                

                                Log.d("user", "Retrieved " + examListRet.size() + " students");
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < examListRet.size(); i++) {
                                    ParseObject u = (ParseObject) examListRet.get(i);
                                    //  if(u.getString("class").equals(id)) {
                                    String name = u.getString(ExamTable.EXAM_NAME);
                                    //name += "\n";
                                    // name += u.getInt("age");

                                    adapter.add(name);
                                    // }

                                }


                                examsList.setAdapter(adapter);
                                new LoadingSyncList(context,layoutLoading,examsList).execute();


                               examsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String item = ((TextView) view).getText().toString();

                                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(ExamTable.TABLE_NAME);
                                        studentQuery.whereEqualTo(ExamTable.EXAM_NAME,item);
                                        studentQuery.whereEqualTo(ExamTable.FOR_CLASS, classRef[0]);
                                        studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                                if (e == null) {
                                                    ParseObject u = studentListRet.get(0);
                                                    String examid = u.getObjectId();
                                                    //Toast.makeText(Students.this,"id of student selected is = " + id, Toast.LENGTH_LONG).show();
                                                    Intent to_marksstudent = new Intent(teacher_exams.this, teacher_marks_studentlist.class);
                                                    to_marksstudent.putExtra("institution_code",institution_code);
                                                   to_marksstudent.putExtra("institution_name",institution_name);
                                                    to_marksstudent.putExtra("examid", examid);
                                                    to_marksstudent.putExtra("classId", classId);
                                                    to_marksstudent.putExtra("classGradeId", classGradeId);
                                                    to_marksstudent.putExtra("role",role);

                                                    startActivity(to_marksstudent);
                                                } else {
                                                    Log.d("user", "Error: " + e.getMessage());
                                                }
                                            }
                                        });


                                    }
                                });

                                examsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                                                   int pos, long id) {

                                        String item = ((TextView) arg1).getText().toString();

                                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(ExamTable.TABLE_NAME);
                                        studentQuery.whereEqualTo(ExamTable.EXAM_NAME,item);
                                        studentQuery.whereEqualTo(ExamTable.FOR_CLASS, classRef[0]);
                                        studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                                if (e == null) {
                                                    ParseObject u = studentListRet.get(0);
                                                    final String examid = u.getObjectId();
                                                    final Dialog dialog = new Dialog(teacher_exams.this);
                                                    dialog.setContentView(R.layout.delete);
                                                   // dialog.setTitle("Upload Details");

                                                    TextView delText= (TextView) dialog.findViewById(R.id.delText);
                                                    delText.setOnClickListener(new View.OnClickListener() {
                                                        public void onClick(View v) {

                                                            ParseObject.createWithoutData(ExamTable.TABLE_NAME, examid).deleteEventually();


                                                            onRestart();


                                                            dialog.dismiss();

                                                        }
                                                    });
                                                    dialog.show();



                                                } else {
                                                    Log.d("user", "Error: " + e.getMessage());
                                                }
                                            }
                                        });

                                        Log.v("long clicked","pos: " + pos);

                                        return true;
                                    }
                                });


                            } else {
                                Toast.makeText(teacher_exams.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });


                } else {
                    Toast.makeText(teacher_exams.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });



        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();



        addExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(teacher_exams.this, teacher_newexam.class);
                i.putExtra("institution_name",institution_name);
                i.putExtra("institution_code",institution_code);
                i.putExtra("role",role);
                i.putExtra("classId",classId);
                i.putExtra("classGradeId",classId);
                startActivity(i);
            }
        });




    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Intent to_uploads = new Intent(teacher_exams.this, teacher_exams.class);
        to_uploads.putExtra("institution_name",institution_name);
        to_uploads.putExtra("institution_code",institution_code);
        to_uploads.putExtra("role",role);
        to_uploads.putExtra("classId", classId);
        to_uploads.putExtra("classGradeId", classGradeId);
        startActivity(to_uploads);
        finish();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(teacher_exams.this,login.class);
            startActivity(nouser);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(teacher_exams.this,MainActivity.class);
                startActivity(i);
                finish();
                //do your own thing here
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent tohome = new Intent(teacher_exams.this, SelectSubject.class);
        tohome.putExtra("role",role);
        tohome.putExtra("institution_name",institution_name);
        tohome.putExtra("institution_code",institution_code);
        tohome.putExtra("for","exam");
        tohome.putExtra("classGradeId", classGradeId);

        startActivity(tohome);

    }

}
