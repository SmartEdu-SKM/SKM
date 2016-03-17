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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shubham Bhasin on 10/7/2015.
 */
public class Admin_classes extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button addClassButton;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView classList;
    ListView classSectionList;
    Notification_bar noti_bar;
    Button ok;
    Button deleteClassButton;
    Button addSectionButton;
    EditText getNewSection;

    Button done;
    Button deleteSectionButton;
    Button addSubjectButton;
    ListView classSubjectList;
    TextView dialog_heading;
    TextView confirm_message;
    Button cancel;
    Button proceed;
    EditText newSubject;
    Spinner subjectTeacherSpinner;
    Button addSubject;
    Spinner newsectionclassteacher;
    EditText classTeacherSubject;
    TextView edit;
    Button deleteSubject;
    TextView subjectname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_classes);

        Intent from_student = getIntent();
        role=from_student.getStringExtra("role");
        institution_code=from_student.getStringExtra("institution_code");
        institution_name=from_student.getStringExtra("institution_name");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Classes");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institution_name);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        addClassButton = (Button)findViewById(R.id.addClassButton);
        classList = (ListView) findViewById(R.id.classList);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
        drawerFragment.setDrawerListener(this);


        ParseQuery<ParseObject> classGradeQuery = ParseQuery.getQuery(ClassGradeTable.TABLE_NAME);
        classGradeQuery.whereEqualTo(ClassGradeTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
        classGradeQuery.orderByAscending(ClassGradeTable.CLASS_GRADE);
        classGradeQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> classGradeListRet, ParseException e) {
                if (e == null) {

                    Log.d("class", "Retrieved the classes with insti code " + institution_code);

                    if (classGradeListRet.size() != 0) {
                        ArrayList<String> classGradeLt = new ArrayList<String>();
                        ArrayAdapter adapter = new ArrayAdapter(Admin_classes.this, android.R.layout.simple_list_item_1, classGradeLt);
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                        Log.d("user", "Retrieved " + classGradeListRet.size() + " classes");
                        //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                        HashMap<String, String> unique_class_map = new HashMap<String, String>();
                        for (int i = 0; i < classGradeListRet.size(); i++) {
                            ParseObject u = (ParseObject) classGradeListRet.get(i);
                            //  if(u.getString("class").equals(id)) {

                            String name = u.getString(ClassGradeTable.CLASS_GRADE);
                            // String subject= u.getString(ClassTable.SUBJECT);
                            // String item= name + ". " + subject;
                            //name += "\n";
                            // name += u.getInt("age");
                            if (unique_class_map.get(name) == null) {
                                unique_class_map.put(name, "1");
                                adapter.add(name);
                            }

                            // }

                        }


                        classList.setAdapter(adapter);


                        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final String item = ((TextView) view).getText().toString();
                                Log.d("class", item);


                                final Dialog class_info = new Dialog(Admin_classes.this);
                                class_info.setContentView(R.layout.class_details);
                                class_info.setTitle(item);

                                setDialogSize(class_info);
                                edit = (TextView) class_info.findViewById(R.id.edittext);
                                edit.setVisibility(View.INVISIBLE);
                                dialog_heading = (TextView) class_info.findViewById(R.id.description);
                                dialog_heading.setText("Sections");
                                classSectionList = (ListView) class_info.findViewById(R.id.subjectList);
                                ok = (Button) class_info.findViewById(R.id.doneButton);
                                deleteClassButton = (Button) class_info.findViewById(R.id.delClassButton);
                                addSectionButton = (Button) class_info.findViewById(R.id.addSubjectButton);
                                addSectionButton.setText("Add Section");

                                final HashMap<String, String> classSectionMap = new HashMap<String, String>();
                                ArrayList<String> sectionLt = new ArrayList<String>();
                                ArrayAdapter sectionadapter = new ArrayAdapter(class_info.getContext(), android.R.layout.simple_list_item_1, sectionLt);
                                //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                Log.d("user", "Retrieved " + classGradeListRet.size() + " sections");
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < classGradeListRet.size(); i++) {
                                    ParseObject u = (ParseObject) classGradeListRet.get(i);

                                    if (u.getString(ClassGradeTable.CLASS_GRADE).equals(item)) {
                                        String name = u.getString(ClassGradeTable.CLASS_GRADE);
                                        String section = u.getString(ClassGradeTable.SECTION);

                                        if (section != null) {
                                            String object = name + " " + section;
                                            sectionadapter.add(object);
                                            classSectionMap.put(object, u.getObjectId());
                                        } else {
                                            String object = name;
                                            sectionadapter.add(object);
                                        }
                                    }
                                    //name += "\n";
                                    // name += u.getInt("age");


                                    // }

                                }


                                classSectionList.setAdapter(sectionadapter);

                                classSectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        final String item = ((TextView) view).getText().toString();
                                        sectionSelected(item, classSectionMap.get(item));

                                    }
                                });


                                class_info.show();


                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        class_info.dismiss();
                                    }
                                });


                                deleteClassButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteClassGrade(item);
                                        class_info.dismiss();
                                    }
                                });

                                addSectionButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addSectionCall(item);

                                    }
                                });


                            }
                        });


                    } else {
                        Log.d("class", "error in query");
                    }

                } else {
                    Toast.makeText(Admin_classes.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("class", "Error: " + e.getMessage());
                }


            }
        });



        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_new_class = new Intent(Admin_classes.this, NewClass.class);
                to_new_class.putExtra("institution_name", institution_name);
                to_new_class.putExtra("institution_code", institution_code);
                to_new_class.putExtra("role", role);
                startActivity(to_new_class);
            }
        });

    }




    protected void sectionSelected(final String item, final String sectionObjectId)
    {

        String[] classSection=item.split(" ");

        Log.d("class", "class " + classSection[0] + " section " + classSection[1]);
        final Dialog classSection_details=new Dialog(Admin_classes.this);
        classSection_details.setContentView(R.layout.class_details);
        setDialogSize(classSection_details);
        edit=(TextView)classSection_details.findViewById(R.id.edittext);

        edit.setVisibility(View.VISIBLE);
        deleteSectionButton=(Button)classSection_details.findViewById(R.id.delClassButton);
        addSubjectButton=(Button)classSection_details.findViewById(R.id.addSubjectButton);
        dialog_heading=(TextView)classSection_details.findViewById(R.id.description);
        dialog_heading.setText("Subjects");
        addSubjectButton.setText("Add Subject");
        deleteSectionButton.setText("Delete Section");
        done=(Button)classSection_details.findViewById(R.id.doneButton);
        classSubjectList=(ListView)classSection_details.findViewById(R.id.subjectList);


        final HashMap<String,String> subjectMap=new HashMap<String,String>();
        final ParseObject classSectionObject=ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME,sectionObjectId);
        ParseQuery<ParseObject> subjects = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        subjects.whereEqualTo(ClassTable.CLASS_NAME, classSectionObject);
        subjects.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> subjectobjects, ParseException e) {
                if (e == null) {
                    if (subjectobjects.size() != 0) {


                        ArrayList<String> subjectLt = new ArrayList<String>();
                        final ArrayAdapter subjectadapter = new ArrayAdapter(classSection_details.getContext(), android.R.layout.simple_list_item_1, subjectLt);
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                        Log.d("user", "Retrieved " + subjectobjects.size() + " subjects");
                        //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < subjectobjects.size(); i++) {
                            final ParseObject u = (ParseObject) subjectobjects.get(i);
                            //  if(u.getString("class").equals(id)) {
                            final String subject_name = u.getString(ClassTable.SUBJECT);
                            final ParseUser teacher_object =u.getParseUser(ClassTable.TEACHER_USER_REF);
                            final String idclass=u.getObjectId();

                            final String[] teacher_name = {null};
                                ParseQuery<ParseObject> teacherquery=ParseQuery.getQuery(TeacherTable.TABLE_NAME);
                                teacherquery.whereEqualTo(TeacherTable.TEACHER_USER_REF,teacher_object);
                                teacherquery.whereEqualTo(TeacherTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                                teacherquery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            if (objects.size() != 0) {

                                                try {
                                                teacher_name[0] = objects.get(0).fetchIfNeeded().getString(TeacherTable.TEACHER_NAME);
                                                String entry = subject_name + " by " + teacher_name[0];

                                                subjectMap.put(entry, idclass);
                                                subjectadapter.add(entry);

                                                    if (u.getBoolean(ClassTable.IF_CLASS_TEACHER)) {
                                                        dialog_heading.setText("Class Teacher : " + teacher_name[0] + "\n" + "Subjects:");
                                                    }
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }

                                            } else {
                                                Log.d("teacher", "error in query");
                                            }
                                        } else {
                                            Log.d("teacher", "error");
                                        }
                                    }
                                });



                            // }

                        }


                        classSubjectList.setAdapter(subjectadapter);
                        classSection_details.show();


                        classSubjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final String item = ((TextView) view).getText().toString();
                                final Dialog edit_subject_dialog=new Dialog(Admin_classes.this);
                                edit_subject_dialog.setContentView(R.layout.subject_edit);
                                setDialogSize(edit_subject_dialog);
                                cancel=(Button)edit_subject_dialog.findViewById(R.id.cancelButton);
                                done=(Button)edit_subject_dialog.findViewById(R.id.doneButton);
                                deleteSubject=(Button)edit_subject_dialog.findViewById(R.id.delSubjectButton);
                                subjectTeacherSpinner=(Spinner)edit_subject_dialog.findViewById(R.id.subjectTeacher);
                                subjectname=(TextView)edit_subject_dialog.findViewById(R.id.subjectname);
                                final String subid=subjectMap.get(item);
                                final ParseObject classobj=ParseObject.createWithoutData(ClassTable.TABLE_NAME,subid);

                                String sub[]=item.split(" by ");


                                    subjectname.setText(sub[0]);

                                final HashMap<String,String> teacherMap=new HashMap<String,String>();
                                ParseQuery teacherListQuery=ParseQuery.getQuery(TeacherTable.TABLE_NAME);
                                teacherListQuery.whereEqualTo(TeacherTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                                teacherListQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> teacherListRet, ParseException e) {
                                        if (e == null) {
                                            if (teacherListRet.size() != 0) {

                                                ArrayList<String> teacherLt = new ArrayList<String>();
                                                ArrayAdapter teacheradapter = new ArrayAdapter(Admin_classes.this, android.R.layout.simple_spinner_item, teacherLt);
                                                teacheradapter.add("");
                                                //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();
                                                for (int x = 0; x < teacherListRet.size(); x++) {
                                                    ParseObject teacher = teacherListRet.get(x);
                                                    String teacher_name = teacher.getString(TeacherTable.TEACHER_NAME);
                                                    teacheradapter.add(teacher_name);
                                                    teacherMap.put(teacher_name, teacher.getObjectId());
                                                }

                                                subjectTeacherSpinner.setAdapter(teacheradapter);
                                                edit_subject_dialog.show();


                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        edit_subject_dialog.dismiss();
                                                    }
                                                });


                                                deleteSubject.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        ParseQuery<ParseObject> edit=ParseQuery.getQuery(ClassTable.TABLE_NAME);
                                                        edit.whereEqualTo(ClassTable.OBJECT_ID, subid);
                                                        edit.findInBackground(new FindCallback<ParseObject>() {
                                                            @Override
                                                            public void done(List<ParseObject> objects, ParseException e) {
                                                                if (e == null) {
                                                                    if (objects.size() != 0) {

                                                                        objects.get(0).deleteEventually();
                                                                        Toast.makeText(Admin_classes.this, "Deletion Done", Toast.LENGTH_LONG).show();


                                                                        edit_subject_dialog.dismiss();
                                                                    } else {
                                                                        Log.d("class", "error in query");
                                                                    }
                                                                } else {
                                                                    Log.d("class", "error");
                                                                }
                                                            }
                                                        });
                                                    }
                                                });


                                                done.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {



                                                        if(subjectTeacherSpinner.getSelectedItem().equals(""))
                                                        {
                                                            Toast.makeText(Admin_classes.this,"No teacher selected",Toast.LENGTH_LONG).show();
                                                        }else {


                                                            String teacherid = teacherMap.get(subjectTeacherSpinner.getSelectedItem());
                                                            ParseObject teacher = ParseObject.createWithoutData(TeacherTable.TABLE_NAME, teacherid);
                                                            final ParseUser teacheruser;
                                                            try {
                                                                teacheruser = teacher.fetchIfNeeded().getParseUser(TeacherTable.TEACHER_USER_REF);

                                                           ParseQuery<ParseObject> edit=ParseQuery.getQuery(ClassTable.TABLE_NAME);
                                                            edit.whereEqualTo(ClassTable.OBJECT_ID, subid);
                                                            edit.findInBackground(new FindCallback<ParseObject>() {
                                                                @Override
                                                                public void done(List<ParseObject> objects, ParseException e) {
                                                                    if (e == null) {
                                                                        if (objects.size() != 0) {

                                                                            objects.get(0).put(ClassTable.TEACHER_USER_REF,teacheruser);
                                                                            objects.get(0).saveEventually();
                                                                            Toast.makeText(Admin_classes.this, "Edit Done", Toast.LENGTH_LONG).show();


                                                                            edit_subject_dialog.dismiss();
                                                                        } else {
                                                                            Log.d("class", "error in query");
                                                                        }
                                                                    } else {
                                                                        Log.d("class", "error");
                                                                    }
                                                                }
                                                            });
                                                            } catch (ParseException e1) {
                                                                e1.printStackTrace();
                                                            }




                                                        }
                                                    }
                                                });



                                            } else {
                                                Toast.makeText(Admin_classes.this, "no teacher is added in this institution", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Log.d("teachers", "error");
                                        }
                                    }

                                });











                            }
                        });


                        deleteSectionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Dialog confirm_delete = new Dialog(Admin_classes.this);
                                confirm_delete.setContentView(R.layout.confirm_message);
                                confirm_message = (TextView) confirm_delete.findViewById(R.id.confirm_message);
                                cancel = (Button) confirm_delete.findViewById(R.id.cancelButton);
                                proceed = (Button) confirm_delete.findViewById(R.id.proceedButton);
                                confirm_message.setText("All data related to  " + item + ",including students,attendance,uploads etc, will be deleted permanently!!");
                                confirm_delete.show();
                                proceed.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        confirm_delete.dismiss();
                                        deleteClass(classSectionObject);
                                        deleteStudent(classSectionObject);

                                    }
                                });

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        confirm_delete.dismiss();
                                    }
                                });

                            }
                        });

                        done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                classSection_details.dismiss();
                            }
                        });

                        addSubjectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addSubjectCall(classSectionObject);
                            }
                        });


                    } else {
                        Log.d("subjects", "error in query");
                    }
                } else {
                    Log.d("subjects", "error");
                }
            }
        });


    }



    protected void addSubjectCall(final ParseObject classSectionObject){
        final Dialog newSubejectDialog=new Dialog(Admin_classes.this);
        newSubejectDialog.setContentView(R.layout.new_subject);
        newSubject=(EditText)newSubejectDialog.findViewById(R.id.newsubject);
        subjectTeacherSpinner=(Spinner)newSubejectDialog.findViewById(R.id.subjectteacherselection);
        addSubject=(Button)newSubejectDialog.findViewById(R.id.addsubjectButton);


        final HashMap<String,String> teacherMap=new HashMap<String,String>();
        ParseQuery teacherListQuery=ParseQuery.getQuery(TeacherTable.TABLE_NAME);
        teacherListQuery.whereEqualTo(TeacherTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
        teacherListQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> teacherListRet, ParseException e) {
                if (e == null) {
                    if (teacherListRet.size() != 0) {

                        ArrayList<String> teacherLt = new ArrayList<String>();
                        ArrayAdapter teacheradapter = new ArrayAdapter(Admin_classes.this, android.R.layout.simple_spinner_item, teacherLt);
                        teacheradapter.add("");
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();
                        for (int x = 0; x < teacherListRet.size(); x++) {
                            ParseObject teacher = teacherListRet.get(x);
                            String teacher_name = teacher.getString(TeacherTable.TEACHER_NAME);
                            teacheradapter.add(teacher_name);
                            teacherMap.put(teacher_name, teacher.getObjectId());
                        }

                        subjectTeacherSpinner.setAdapter(teacheradapter);
                        newSubejectDialog.show();

                        addSubject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String subjectname = newSubject.getText().toString().trim();
                                String teachername = subjectTeacherSpinner.getSelectedItem().toString();
                                ParseUser teacheruser = null;
                                try {
                                    teacheruser = (ParseObject.createWithoutData(TeacherTable.TABLE_NAME, teacherMap.get(teachername))).fetchIfNeeded().getParseUser(TeacherTable.TEACHER_USER_REF);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }

                                if ((subjectname.equals("")) || (teachername.equals(""))) {
                                    Toast.makeText(getApplicationContext(), "New Subject details cannot be empty!", Toast.LENGTH_LONG).show();
                                } else {
                                    ParseObject newClass = new ParseObject(ClassTable.TABLE_NAME);
                                    newClass.put(ClassTable.SUBJECT, subjectname);
                                    newClass.put(ClassTable.TEACHER_USER_REF, teacheruser);
                                    newClass.put(ClassTable.IF_CLASS_TEACHER, false);
                                    newClass.put(ClassTable.CLASS_NAME, classSectionObject);
                                    newClass.saveEventually();
                                    newSubejectDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "New Subject Added Successfully", Toast.LENGTH_LONG).show();


                                }
                            }
                        });


                    } else {
                        Toast.makeText(Admin_classes.this, "no teacher is added in this institution", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("teachers", "error");
                }
            }

        });


    }



   protected void deleteClassGrade(final String selectedClass)                 //incomplete code....class must be deleted from other tables too
   {
       final Dialog confirm_step=new Dialog(Admin_classes.this);
       confirm_step.setContentView(R.layout.confirm_message);
       confirm_message=(TextView)confirm_step.findViewById(R.id.confirm_message);
       proceed=(Button)confirm_step.findViewById(R.id.proceedButton);
       cancel=(Button)confirm_step.findViewById(R.id.cancelButton);
       confirm_message.setText("All data related to class " + selectedClass + ",including sections,students,attendance,uploads etc, will be deleted permanently!!");
       //setDialogSize(confirm_step);

       confirm_step.show();

       proceed.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               confirm_step.dismiss();

               ParseQuery<ParseObject> deleteClassGradeQuery = ParseQuery.getQuery(ClassGradeTable.TABLE_NAME);
               deleteClassGradeQuery.whereEqualTo(ClassGradeTable.CLASS_GRADE, selectedClass);
               deleteClassGradeQuery.whereEqualTo(ClassGradeTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
               deleteClassGradeQuery.findInBackground(new FindCallback<ParseObject>() {
                   @Override
                   public void done(List<ParseObject> classgradeobjects, ParseException e) {
                       if (e == null) {
                           if (classgradeobjects.size() != 0) {
                               for (int x = 0; x < classgradeobjects.size(); x++) {


                                   deleteClass(classgradeobjects.get(x));
                                   deleteStudent(classgradeobjects.get(x));


                                   //class object deletion

                                   classgradeobjects.get(x).deleteEventually();//classgrade object deletion
                                   Toast.makeText(Admin_classes.this, "Deletion completed", Toast.LENGTH_LONG).show();
                               }
                           } else {
                               Log.d("classGrade", "error in query");
                           }
                       } else {
                           Log.d("classGrade", "error");
                       }
                   }
               });


           }
       });


       cancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               confirm_step.dismiss();
           }
       });


   }



    protected void deleteClass(ParseObject classGradeObject){
        ParseQuery<ParseObject> deleteClassQuery=ParseQuery.getQuery(ClassTable.TABLE_NAME);
        deleteClassQuery.whereEqualTo(ClassTable.CLASS_NAME, classGradeObject);
        deleteClassQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> classobjects, ParseException e) {
                if (e == null) {
                    if (classobjects.size() != 0) {
                        for (int x = 0; x < classobjects.size(); x++) {

                            deleteUpload(classobjects.get(x));          //uploads deletion related to class
                            deleteAttendance(classobjects.get(x));          //attendance deletion related to class
                            deleteExam(classobjects.get(x));            //exam deletions related to class

                            classobjects.get(x).deleteEventually(); //class object deletion
                        }


                    } else {
                        Log.d("class", "error in query");
                    }
                } else {
                    Log.d("class", "exception error in class deletion");
                }
            }
        });

    }


    protected void deleteUpload(ParseObject classObject){
        ParseQuery<ParseObject> deleteUploads=ParseQuery.getQuery(ImageUploadsTable.TABLE_NAME);
        deleteUploads.whereEqualTo(ImageUploadsTable.CLASS_REF, classObject);
        deleteUploads.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> uploadsobjects, ParseException e) {
                if (e == null) {
                    if (uploadsobjects.size() != 0) {
                        for (int x = 0; x < uploadsobjects.size(); x++) {
                            uploadsobjects.get(x).deleteEventually();
                        }
                    } else {
                        Log.d("uploads", "error in query");
                    }
                } else {
                    Log.d("uploads", "exception error in class deletion");
                }
            }
        });
    }


    protected void deleteAttendance(ParseObject classObject){
        ParseQuery<ParseObject> deleteAttendance=ParseQuery.getQuery(AttendanceDailyTable.TABLE_NAME);
        deleteAttendance.whereEqualTo(AttendanceDailyTable.FOR_CLASS,classObject);
        deleteAttendance.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> attendanceobjects, ParseException e) {
                if(e==null){
                    if(attendanceobjects.size()!=0){
                        for(int x=0;x<attendanceobjects.size();x++){
                            attendanceobjects.get(x).deleteEventually();
                        }
                    }else{
                        Log.d("attendance", "error in query");
                    }
                }else
                {
                    Log.d("attendance", "exception error in class deletion");
                }
            }
        });
    }

    protected void deleteExam(ParseObject classObject){
        ParseQuery<ParseObject> deleteExams=ParseQuery.getQuery(ExamTable.TABLE_NAME);
        deleteExams.whereEqualTo(ExamTable.FOR_CLASS,classObject);
        deleteExams.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> examobjects, ParseException e) {
                if(e==null){
                    if(examobjects.size()!=0){
                        for(int x=0;x<examobjects.size();x++){
                            examobjects.get(x).deleteEventually();
                        }
                    }else{
                        Log.d("exam", "error in query");
                    }
                }else
                {
                    Log.d("exam", "exception error in class deletion");
                }
            }
        });

    }



    protected void deleteStudent(ParseObject classGradeObject)
    {
        ParseQuery<ParseObject> deleteStudent=ParseQuery.getQuery(StudentTable.TABLE_NAME);
        deleteStudent.whereEqualTo(StudentTable.CLASS_REF,classGradeObject);
        deleteStudent.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> studentobjects, ParseException e) {
                if(e==null){
                    if(studentobjects.size()!=0){

                        ParseObject institution=ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code);

                        for(int x=0;x<studentobjects.size();x++){
                           deleteMarks(studentobjects.get(x));      //deletion marks objects related to student
                            deleteStudentRelatedData(studentobjects.get(x),institution);        //parent object,roles deletion

                            studentobjects.get(x).deleteEventually();
                            //parent object deletion
                            //role deletion
                            //student deletion
                        }

                    }else{
                        Log.d("student","error in query");
                    }

                }else{
                    Log.d("student","exception error in student query while deletion");
                }
            }
        });
    }



    protected void deleteMarks(ParseObject studentObject){

        ParseQuery<ParseObject> deleteMarks=ParseQuery.getQuery(MarksTable.TABLE_NAME);
        deleteMarks.whereEqualTo(MarksTable.STUDENT_USER_REF, studentObject);
        deleteMarks.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> marksobjects, ParseException e) {
                if (e == null) {
                    if (marksobjects.size() != 0) {
                        for (int x = 0; x < marksobjects.size(); x++) {
                            marksobjects.get(x).deleteEventually();
                        }
                    } else {
                        Log.d("marks", "error in query");
                    }
                } else {
                    Log.d("marks", "exception error in class deletion");
                }
            }
        });

    }


    protected void deleteStudentRelatedData(ParseObject studentObject,ParseObject institution){
        ParseUser studentUser=studentObject.getParseUser(StudentTable.STUDENT_USER_REF);
        deleteParentData(studentUser, institution);          //deleting parent relationand role
        deleteStudentData(studentUser, institution);         //deleting student role
    }



    protected void deleteParentData(final ParseUser studentUser, final ParseObject institution)
    {
        ParseQuery<ParseObject> deleteParentRelation=ParseQuery.getQuery(ParentTable.TABLE_NAME);
        deleteParentRelation.whereEqualTo(ParentTable.CHILD_USER_REF,studentUser);
        deleteParentRelation.whereEqualTo(ParentTable.INSTITUTION, institution);
        deleteParentRelation.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parentrelationobjects, ParseException e) {
                if (e == null) {
                    if (parentrelationobjects.size() != 0) {
                        for (int x = 0; x < parentrelationobjects.size(); x++) {
                            ParseUser u = parentrelationobjects.get(x).getParseUser(ParentTable.PARENT_USER_REF);

                            deleteParentRole(u,institution);                    //parent role deletion
                            parentrelationobjects.get(x).deleteEventually();        //deleting relation
                        }
                    } else {
                        Log.d("parent", "error in query");
                    }
                } else {
                    Log.d("parent", "exception error in class deletion");
                }
            }
        });
    }




    protected void deleteStudentData(ParseUser studentUser,ParseObject institution)
    {
        ParseQuery<ParseObject> deleteStudentRole=ParseQuery.getQuery(RoleTable.TABLE_NAME);
        deleteStudentRole.whereEqualTo(RoleTable.OF_USER_REF,studentUser);
        deleteStudentRole.whereEqualTo(RoleTable.ENROLLED_WITH,institution);
        deleteStudentRole.whereEqualTo(RoleTable.ROLE,"Student");
        deleteStudentRole.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> roleobjects, ParseException e) {
                if(e==null){
                    if(roleobjects.size()!=0){
                        for(int x=0;x<roleobjects.size();x++){
                            roleobjects.get(x).deleteEventually();
                        }
                    }else{
                        Log.d("parent", "error in query");
                    }
                }else
                {
                    Log.d("parent", "exception error in class deletion");
                }
            }
        });
    }



    protected void deleteParentRole(ParseUser parentUser,ParseObject institution){
        ParseQuery<ParseObject> deleteParentRole=ParseQuery.getQuery(RoleTable.TABLE_NAME);
        deleteParentRole.whereEqualTo(RoleTable.OF_USER_REF,parentUser);
        deleteParentRole.whereEqualTo(RoleTable.ROLE, "Parent");
        deleteParentRole.whereEqualTo(RoleTable.ENROLLED_WITH, institution);
        deleteParentRole.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> roleobjects, ParseException e) {
                if (e == null) {
                    if (roleobjects.size() != 0) {
                        for (int x = 0; x < roleobjects.size(); x++) {
                            roleobjects.get(x).deleteEventually();
                        }
                    } else {
                        Log.d("role", "error in query");
                    }
                } else {
                    Log.d("role", "exception error in class deletion");
                }
            }
        });
    }


    protected  void addSectionCall(final String selectedClass)
    {
        final Dialog newSection=new Dialog(Admin_classes.this);
        newSection.setContentView(R.layout.new_section);
        newsectionclassteacher=(Spinner)newSection.findViewById(R.id.sectionclassteacherselection);
        done=(Button)newSection.findViewById(R.id.addsectionButton);
        getNewSection=(EditText)newSection.findViewById(R.id.newsectionname);
        classTeacherSubject=(EditText)newSection.findViewById(R.id.classteachersubject);



        final HashMap<String,String> teacherMap=new HashMap<String,String>();
        ParseQuery teacherListQuery=ParseQuery.getQuery(TeacherTable.TABLE_NAME);
        teacherListQuery.whereEqualTo(TeacherTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
        teacherListQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> teacherListRet, ParseException e) {
                if (e == null) {
                    if (teacherListRet.size() != 0) {

                        ArrayList<String> teacherLt = new ArrayList<String>();
                        ArrayAdapter teacheradapter = new ArrayAdapter(Admin_classes.this, android.R.layout.simple_spinner_item, teacherLt);
                        teacheradapter.add("");
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();
                        for (int x = 0; x < teacherListRet.size(); x++) {
                            ParseObject teacher = teacherListRet.get(x);
                            String teacher_name = teacher.getString(TeacherTable.TEACHER_NAME);
                            teacheradapter.add(teacher_name);
                            teacherMap.put(teacher_name, teacher.getObjectId());
                        }

                        newsectionclassteacher.setAdapter(teacheradapter);
                        newSection.show();


                    } else {
                        Toast.makeText(Admin_classes.this, "no teacher is added in this institution", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("teachers", "error");
                }
            }

        });





        newSection.show();


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String selectedTeacher = newsectionclassteacher.getSelectedItem().toString();
                if ((getNewSection.getText().toString().equals("")) || (newsectionclassteacher.getSelectedItem().equals("")) || (classTeacherSubject.getText().equals(""))) {
                    Toast.makeText(Admin_classes.this, "Information incomplete", Toast.LENGTH_LONG).show();
                } else {


                    ParseQuery<ParseObject> newSectionQuery = ParseQuery.getQuery(ClassGradeTable.TABLE_NAME);
                    newSectionQuery.whereEqualTo(ClassGradeTable.CLASS_GRADE, selectedClass);
                    newSectionQuery.whereEqualTo(ClassGradeTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                    newSectionQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> sectionsList, ParseException e) {
                            if (e == null) {
                                Log.d("classSection", sectionsList.size() + " Sections");
                                int flag = 0;
                                if (sectionsList.size() != 0) {
                                    for (int x = 0; x < sectionsList.size(); x++) {
                                        if ((sectionsList.get(x).getString(ClassGradeTable.SECTION)).equalsIgnoreCase(getNewSection.getText().toString())) {
                                            Toast.makeText(Admin_classes.this, "Section already added. Type Another section name", Toast.LENGTH_LONG).show();
                                            flag = 1;
                                            break;
                                        }
                                    }
                                    if (flag == 0) {

                                        final ParseObject newSectionObject = new ParseObject(ClassGradeTable.TABLE_NAME);
                                        newSectionObject.put(ClassGradeTable.CLASS_GRADE, selectedClass);
                                        newSectionObject.put(ClassGradeTable.SECTION, getNewSection.getText().toString());
                                        newSectionObject.put(ClassGradeTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                                        newSectionObject.saveEventually(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {


                                                ParseObject newClass = new ParseObject(ClassTable.TABLE_NAME);
                                                newClass.put(ClassTable.SUBJECT, classTeacherSubject.getText().toString());
                                                newClass.put(ClassTable.IF_CLASS_TEACHER, true);
                                                newClass.put(ClassTable.CLASS_NAME, newSectionObject);
                                                String selectedteacherObjectId = teacherMap.get(selectedTeacher);
                                                ParseUser teacheruser = (ParseUser) (ParseObject.createWithoutData(TeacherTable.TABLE_NAME, selectedteacherObjectId)).get(TeacherTable.TEACHER_USER_REF);
                                                newClass.put(ClassTable.TEACHER_USER_REF, teacheruser);
                                                newClass.saveEventually();


                                            }
                                        });
                                        newSection.dismiss();
                                    }
                                } else {
                                    Log.d("classSection", "error in query logic");
                                }
                            } else {
                                Log.d("classSection", "error");
                            }
                        }
                    });


                }
            }
        });             //end os on click of done button while adding new section
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(Admin_classes.this,login.class);
            startActivity(nouser);
        }
    }



}
