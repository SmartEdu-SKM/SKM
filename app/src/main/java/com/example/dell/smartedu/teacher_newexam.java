package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class teacher_newexam extends BaseActivity {


    EditText examName;
    EditText maxMarks;
   String ExamName;
    String marks;
    Button addExamButton;
    String classId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_newexam);
        classId=getIntent().getStringExtra("id");
       /* mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Task");*/

        examName = (EditText) findViewById(R.id.editText3);
        maxMarks = (EditText) findViewById(R.id.editText4);
        addExamButton = (Button) findViewById(R.id.button2);



        addExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExamName = examName.getText().toString();
                marks = maxMarks.getText().toString();
                if (ExamName.equals("") || marks.equals("")) {
                    Toast.makeText(getApplicationContext(), "Exam details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    final ParseObject[] classRef = new ParseObject[1];
                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
                    studentQuery.whereEqualTo("objectId", classId);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, com.parse.ParseException e) {
                            if (e == null) {
                                classRef[0] = studentListRet.get(0);


                                ParseObject exam = new ParseObject("Exam");
                                exam.put("examName", ExamName);
                                exam.put("totalMarks",Integer.parseInt(marks));
                                exam.put("class",classRef[0]);
                                //String id = task.getObjectId();
                                exam.saveInBackground();
                                Toast.makeText(getApplicationContext(), "Event details successfully stored", Toast.LENGTH_LONG).show();

                                Intent to_exams = new Intent(teacher_newexam.this, teacher_exams.class);
                                to_exams.putExtra("id", classId);
                                startActivity(to_exams);
                                finish();


                            } else {
                                Toast.makeText(teacher_newexam.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });




                }
            }
        });


    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(teacher_newexam.this,login.class);
            startActivity(nouser);
        }
    }
}
