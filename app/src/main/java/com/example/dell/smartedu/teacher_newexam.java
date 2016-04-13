package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class teacher_newexam extends BaseActivity {


    EditText examName;
    EditText maxMarks;
   String ExamName;
    String marks;
    Button addExamButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_newexam);
        classId=getIntent().getStringExtra("classId");
        classGradeId=getIntent().getStringExtra("classGradeId");
        role=getIntent().getStringExtra("role");
        institution_name=getIntent().getStringExtra("institution_name");
        institution_code=getIntent().getStringExtra("institution_code");
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
                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
                    studentQuery.whereEqualTo(ClassTable.OBJECT_ID, classId);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, com.parse.ParseException e) {
                            if (e == null) {
                                classRef[0] = studentListRet.get(0);


                                ParseObject exam = new ParseObject(ExamTable.TABLE_NAME);
                                exam.put(ExamTable.EXAM_NAME, ExamName);
                                exam.put(ExamTable.MAX_MARKS,Integer.parseInt(marks));
                                exam.put(ExamTable.FOR_CLASS,classRef[0]);
                                //String id = task.getObjectId();
                                exam.saveInBackground();
                                Toast.makeText(getApplicationContext(), "Event details successfully stored", Toast.LENGTH_LONG).show();

                                Intent to_exams = new Intent(teacher_newexam.this, teacher_exams.class);
                                to_exams.putExtra("institution_name",institution_name);
                                to_exams.putExtra("institution_code",institution_code);
                                to_exams.putExtra("classId", classId);
                                to_exams.putExtra("classGradeId", classGradeId);
                                to_exams.putExtra("role", role);
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
