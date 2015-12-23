package com.example.dell.smartedu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class AddMarks extends BaseActivity {

    String studentId;
    String classId;
    String sub;
    TextView subject;
    EditText examDesc;
    EditText marksObtained;
    EditText outOf;
    Button addMarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marks);

        Bundle from_stud_result = getIntent().getExtras();
        studentId = from_stud_result.getString("studentId");
        classId = from_stud_result.getString("classId");

        examDesc = (EditText) findViewById(R.id.examDesc);
        marksObtained = (EditText) findViewById(R.id.marksDesc);
        outOf = (EditText) findViewById(R.id.outOfDesc);
        addMarks = (Button) findViewById(R.id.addMarks);
        subject= (TextView) findViewById(R.id.subjectText);

        final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
        classQuery.whereEqualTo("objectId", classId);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> classListRet, ParseException e) {
                if (e == null) {
                    classRef[0] = classListRet.get(0);
                    sub = classRef[0].getString("subject");
                    subject.setText(sub);

                    final ParseObject[] studentRef = new ParseObject[1];
                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                    studentQuery.whereEqualTo("class", classRef[0]);
                    studentQuery.whereEqualTo("objectId", studentId);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, ParseException e) {
                            if (e == null) {
                                studentRef[0] = studentListRet.get(0);

                                addMarks.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (examDesc.equals("") || marksObtained.equals("") || outOf.equals("")) {
                                            Toast.makeText(getApplicationContext(), "Marks details cannot be empty!", Toast.LENGTH_LONG).show();
                                        } else {
                                            ParseObject exam=new ParseObject("Exam");
                                            exam.put("class",classRef[0]);
                                            exam.put("examName", examDesc.getText().toString());
                                            exam.put("totalMarks",Float.parseFloat(outOf.getText().toString()));
                                            exam.saveEventually();


                                            ParseObject marks = new ParseObject("Marks");
                                            marks.put("student", studentRef[0]);
                                            marks.put("exam", exam);
                                            marks.put("marksObtained", Float.parseFloat(marksObtained.getText().toString()));
                                            marks.saveEventually();

                                            Toast.makeText(getApplicationContext(), "Marks details added!", Toast.LENGTH_LONG).show();

                                            new student_result();
                                            finish();

                                        }
                                    }

                                });



                            } else {
                                Toast.makeText(AddMarks.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });


                } else {
                    Toast.makeText(AddMarks.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }



            }
        });


    }


}
