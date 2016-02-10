package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class NewClass extends BaseActivity {

    private Toolbar mToolbar;
    String classname;
   String classsection;


    MyDBHandler dbHandler;

    Notification_bar noti_bar;

    EditText classGradeName;
    EditText newSection;
    Button addClassButton;
    Spinner classteacherspinner;
    String classteachername;
    EditText classteachersubject;
    String subjectofclassteacher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Class");

        Intent for_new_class = getIntent();
        role=for_new_class.getStringExtra("role");
        institution_code=for_new_class.getStringExtra("institution_code");
        institution_name=for_new_class.getStringExtra("institution_name");

        classGradeName = (EditText) findViewById(R.id.newClassGrade);
        newSection = (EditText) findViewById(R.id.newClassSection);
classteacherspinner=(Spinner)findViewById(R.id.classteacherselection);
        addClassButton = (Button) findViewById(R.id.addClassButton);
        classteachersubject=(EditText)findViewById(R.id.classteachersubject);
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role, institution_name);


        final HashMap<String,String> teacherMap=new HashMap<String,String>();
        ParseQuery teacherListQuery=ParseQuery.getQuery(TeacherTable.TABLE_NAME);
        teacherListQuery.whereEqualTo(TeacherTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
        teacherListQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> teacherListRet, ParseException e) {
                if (e == null) {
                    if (teacherListRet.size() != 0) {
                        Log.d("here", "here");
                        ArrayList<String> teacherLt = new ArrayList<String>();
                        ArrayAdapter teacheradapter = new ArrayAdapter(NewClass.this, android.R.layout.simple_spinner_item, teacherLt);
                        teacheradapter.add("");
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();
                        for (int x = 0; x < teacherListRet.size(); x++) {
                            ParseObject teacher = teacherListRet.get(x);
                            String teacher_name = teacher.getString(TeacherTable.TEACHER_NAME);
                            teacheradapter.add(teacher_name);
                            teacherMap.put(teacher_name,teacher.getObjectId());
                        }

                        classteacherspinner.setAdapter(teacheradapter);


                        addClassButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                classname = classGradeName.getText().toString().trim();
                                classsection = newSection.getText().toString().trim();
                                classteachername=classteacherspinner.getSelectedItem().toString();
                                subjectofclassteacher=classteachersubject.getText().toString();

                if( (classname.equals("")) || (classsection.equals("")) || (classteachername.equals("")) || subjectofclassteacher.equals("")) {
                    Toast.makeText(getApplicationContext(), "New Class details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    ParseQuery checkClass=ParseQuery.getQuery(ClassGradeTable.TABLE_NAME);
                    checkClass.whereEqualTo(ClassGradeTable.CLASS_GRADE, classname);
                    checkClass.whereEqualTo(ClassGradeTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                    checkClass.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> classGradeobjects, ParseException e) {
                            if (e == null) {
                                if (classGradeobjects.size() == 0) {
                                    Log.d("objid",teacherMap.get(classteachername));
                                    addClassGrade(classname, classsection, teacherMap.get(classteachername),subjectofclassteacher);


                                    Toast.makeText(NewClass.this, "New Class Added", Toast.LENGTH_LONG).show();
                                    Intent to_admin_classes = new Intent(NewClass.this, Admin_classes.class);
                                    to_admin_classes.putExtra("institution_code", institution_code);
                                    to_admin_classes.putExtra("institution_name", institution_name);
                                    to_admin_classes.putExtra("role", role);
                                    startActivity(to_admin_classes);


                                } else {
                                    Toast.makeText(NewClass.this, "Class is Already added", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.d("ClassGrade", "error");
                            }
                        }
                    });

                }
                            }
                        });


                    } else {
                        Toast.makeText(NewClass.this, "no teacher is added in this institution", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("teachers", "error");
                }
            }

        });






    }


    protected void  addClassGrade(String name,String section, final String selectedteacherObjectId, final String subject){
        final ParseObject newClass = new ParseObject(ClassGradeTable.TABLE_NAME);
        newClass.put(ClassGradeTable.CLASS_GRADE, name);
        newClass.put(ClassGradeTable.SECTION, section);
        newClass.put(ClassGradeTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
        newClass.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    addClass(newClass,selectedteacherObjectId,subject);
                } else {
                    Log.d("classGrade", "error in saving");
                }
            }
        });

    }


    protected void addClass(ParseObject classGradeObject,String selectedteacherObjectId,String subject){
        ParseObject newClass=new ParseObject(ClassTable.TABLE_NAME);
        newClass.put(ClassTable.SUBJECT, subject);
        newClass.put(ClassTable.IF_CLASS_TEACHER, true);
        newClass.put(ClassTable.CLASS_NAME,classGradeObject);
        ParseUser  teacheruser=(ParseUser)(ParseObject.createWithoutData(TeacherTable.TABLE_NAME, selectedteacherObjectId)).get(TeacherTable.TEACHER_USER_REF);
        newClass.put(ClassTable.TEACHER_USER_REF, teacheruser);
        newClass.saveEventually();


    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(NewClass.this,login.class);
            startActivity(nouser);
        }
    }

}
