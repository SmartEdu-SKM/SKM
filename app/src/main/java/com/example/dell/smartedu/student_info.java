package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class student_info extends Fragment{

    TextView studentName;
    TextView studentAge;
    Button deleteStudent;
    String studentId;
    String classId;
    String userid;
    String institution_code;
    String institution_name;
    TextView confirm_message;
    Button cancel;
    Button proceed;
    String classGradeId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View android = inflater.inflate(R.layout.fragment_student_info, container, false);
        studentId= getArguments().getString("id");
        classId= getArguments().getString("classId");
        institution_code=getArguments().getString("institution_code");
        institution_name=getArguments().getString("institution_name");
        classGradeId=getArguments().getString("classGradeId");
        studentName=(TextView)android.findViewById(R.id.student_name);
        studentAge=(TextView)android.findViewById(R.id.student_age);
        deleteStudent=(Button)android.findViewById(R.id.delete_student);
        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(StudentTable.TABLE_NAME);
        studentQuery.whereEqualTo(StudentTable.OBJECT_ID,studentId);
        studentQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    ParseObject u = (ParseObject) studentListRet.get(0);
                    studentName.setText(u.getString(StudentTable.STUDENT_NAME).toString());
                    studentAge.setText(u.getNumber(StudentTable.STUDENT_AGE).toString());
                    //Toast.makeText(Students.this,"id of student selected is = " + id, Toast.LENGTH_LONG).show();

                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

    deleteStudent.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {



            final Dialog confirm_step=new Dialog(getActivity());
            confirm_step.setContentView(R.layout.confirm_message);
            confirm_message=(TextView)confirm_step.findViewById(R.id.confirm_message);
            proceed=(Button)confirm_step.findViewById(R.id.proceedButton);
            cancel=(Button)confirm_step.findViewById(R.id.cancelButton);
            confirm_message.setText("All data related to this student including attendance,marks etc, will be deleted permanently!!");


            confirm_step.show();

            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseObject institution = ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code);
                    ParseObject studentObject = ParseObject.createWithoutData(StudentTable.TABLE_NAME, studentId);
                    ParseUser studentUser = studentObject.getParseUser(StudentTable.STUDENT_USER_REF);
                    deleteParentData(institution, studentUser);
                    deleteStudentData(institution, studentUser, studentObject);

                    confirm_step.dismiss();
                    Intent to_student = new Intent(getActivity(), Students.class);
                    to_student.putExtra("role","Teacher");
                    to_student.putExtra("institution_code", institution_code);
                    to_student.putExtra("institution_name", institution_name);
                    to_student.putExtra("id", classId);
                    to_student.putExtra("classGradeId",classGradeId);
                    startActivity(to_student);

                }
            });


            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirm_step.dismiss();
                }
            });








        }
    });
        return android;
    }

    protected void deleteParentData(final ParseObject institution,ParseUser studentUser)
    {

        ParseQuery<ParseObject> parentrelationQuery = ParseQuery.getQuery(ParentTable.TABLE_NAME);
        parentrelationQuery.whereEqualTo(ParentTable.CHILD_USER_REF, studentUser);
        parentrelationQuery.whereEqualTo(ParentTable.INSTITUTION, institution);
        parentrelationQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parentReltionListRet, ParseException e) {
                if (e == null) {
                    if (parentReltionListRet.size() != 0) {
                        ParseUser parent_user = parentReltionListRet.get(0).getParseUser(ParentTable.PARENT_USER_REF);
                        deleteParentRole(parent_user, institution);
                        parentReltionListRet.get(0).deleteEventually();
                       // Toast.makeText(getActivity(), "parent data deleted", Toast.LENGTH_LONG).show();
                    } else {
                     //   Toast.makeText(getActivity(), "error deleting parent info", Toast.LENGTH_LONG).show();
                        Log.d("parent child relation", "error in query");
                    }
                } else {
                   /// Toast.makeText(getActivity(), "error deleting parent info", Toast.LENGTH_LONG).show();
                    Log.d("parent child relation", "Error: " + e.getMessage());
                }
            }
        });



    }






    protected void deleteParentRole(ParseUser parentUser,ParseObject institution){
        ParseQuery<ParseObject> deleteParentRole=ParseQuery.getQuery(RoleTable.TABLE_NAME);
        deleteParentRole.whereEqualTo(RoleTable.OF_USER_REF,parentUser);
        deleteParentRole.whereEqualTo(RoleTable.ROLE,"Parent");
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
                        Log.d("parent role", "error in query");
                     //   Toast.makeText(getActivity(), "error deleting parent role", Toast.LENGTH_LONG).show();

                    }
                } else {
                 //   Toast.makeText(getActivity(), "error deleting parent role", Toast.LENGTH_LONG).show();
                    Log.d("parent role", "exception error in class deletion");
                }
            }
        });
    }




    protected void deleteStudentData(ParseObject institution,ParseUser studentUser,ParseObject studentObject)
    {
        ParseQuery<ParseObject> deleteAttendanceQuery = ParseQuery.getQuery(AttendanceDailyTable.TABLE_NAME);
        deleteAttendanceQuery.whereEqualTo(AttendanceDailyTable.STUDENT_USER_REF, studentObject);
        ///////////add class in query
        deleteAttendanceQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> attendanceListRet, ParseException e) {
                if (e == null) {
                    if (attendanceListRet.size() != 0) {
                        for(int x=0;x<attendanceListRet.size();x++) {
                            attendanceListRet.get(x).deleteEventually();
                        }
                      //  Toast.makeText(getActivity(), "deleted student attendance", Toast.LENGTH_LONG).show();

                    } else {
                       // Toast.makeText(getActivity(), "error deleting attendance", Toast.LENGTH_LONG).show();

                        Log.d("attendance", "error in query");
                    }
                } else {
                   // Toast.makeText(getActivity(), "error deleting attendance", Toast.LENGTH_LONG).show();

                    Log.d("attendance", "Exceptional error");
                }
            }
        });





        ParseQuery<ParseObject> deleteMarksQuery = ParseQuery.getQuery(MarksTable.TABLE_NAME);
        deleteMarksQuery.whereEqualTo(MarksTable.STUDENT_USER_REF, studentUser);
        deleteMarksQuery.findInBackground(new FindCallback<ParseObject>()

                                    {
                                        public void done(List<ParseObject> marksListRet, ParseException e) {
                                            if (e == null) {
                                                if (marksListRet.size() != 0) {
                                                    for (int i = 0; i < marksListRet.size(); i++) {
                                                        marksListRet.get(i).deleteEventually();
                                                    }
                                                  //  Toast.makeText(getActivity(), "Marks deleted", Toast.LENGTH_LONG).show();
                                                } else {
                                                    //Toast.makeText(getActivity(), "error deleting marks", Toast.LENGTH_LONG).show();
                                                    Log.d("marks", "error in query");
                                                }
                                            } else {
                                               // Toast.makeText(getActivity(), "error deleting marks", Toast.LENGTH_LONG).show();
                                                Log.d("marks", "Error: " + e.getMessage());
                                            }
                                        }
                                    }

        );



        ParseQuery<ParseObject> roleQuery = ParseQuery.getQuery(RoleTable.TABLE_NAME);
        roleQuery.whereEqualTo(RoleTable.OF_USER_REF,studentUser);
        //roleQuery.whereEqualTo("createdBy",ParseUser.createWithoutData("User",userid));
        roleQuery.whereEqualTo(RoleTable.ROLE, "Student");
        roleQuery.whereEqualTo(RoleTable.ENROLLED_WITH, institution);
        roleQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> roleListRet, ParseException e) {
                if (e == null) {
                    if (roleListRet.size() != 0) {
                        roleListRet.get(0).deleteEventually();
                        Log.d("role", "Student Deleted from roles");
                    } else {
                      //  Toast.makeText(getActivity(), "error in deleting student role", Toast.LENGTH_LONG).show();
                        Log.d("role", "error in query");
                    }
                } else {
                    //Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });


studentObject.deleteEventually();
    }

  /*  @Override
    public void onDrawerItemSelected(View view, int position) {

    }*/

}


