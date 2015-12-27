package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class student_info extends Fragment implements FragmentDrawer.FragmentDrawerListener{

    TextView studentName;
    TextView studentAge;
    Button deleteStudent;
    String studentId;
    String classId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View android = inflater.inflate(R.layout.fragment_student_info, container, false);
        studentId= getArguments().getString("id");
        classId= getArguments().getString("classId");
        studentName=(TextView)android.findViewById(R.id.student_name);
        studentAge=(TextView)android.findViewById(R.id.student_age);
        deleteStudent=(Button)android.findViewById(R.id.delete_student);
        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
        studentQuery.whereEqualTo("objectId",studentId);
        studentQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    ParseObject u = (ParseObject) studentListRet.get(0);
                    studentName.setText(u.getString("name").toString());
                    studentAge.setText(u.getNumber("age").toString());
                    //Toast.makeText(Students.this,"id of student selected is = " + id, Toast.LENGTH_LONG).show();

                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

    deleteStudent.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final ParseObject[] studentRef = new ParseObject[1];
            ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
            studentQuery.whereEqualTo("objectId", studentId);
            studentQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> studentListRet, ParseException e) {
                    if (e == null) {
                        if(studentListRet.size()!=0) {
                            studentRef[0] = studentListRet.get(0);

                            ParseQuery<ParseObject> attendanceQuery = ParseQuery.getQuery("Attendance");
                            attendanceQuery.whereEqualTo("student", ParseObject.createWithoutData("Student", studentId));
                            attendanceQuery.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> attendanceListRet, ParseException e) {
                                    if (e == null) {
                                        if (attendanceListRet.size() != 0) {
                                            attendanceListRet.get(0).deleteEventually();
                                            Log.d("user", "Deleted: " + "student attendance");
                                        } else {

                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                                        Log.d("user", "Error: " + e.getMessage());
                                    }
                                }
                            });



                           /* String userid= String.valueOf( ParseObject.createWithoutData("Student",studentId).get("userId"));
                            ParseQuery<ParseObject> roleQuery = ParseQuery.getQuery("Role");
                            roleQuery.whereEqualTo("createdBy", ParseUser.createWithoutData("User", userid));

                            roleQuery.whereEqualTo("roleName","Student");
                            roleQuery.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> attendanceListRet, ParseException e) {
                                    if (e == null) {
                                        if(attendanceListRet.size()!=0) {
                                            attendanceListRet.get(0).deleteEventually();
                                            Log.d("role", "Deleted from roles");
                                        }else
                                        {

                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                                        Log.d("user", "Error: " + e.getMessage());
                                    }
                                }
                            });*/



                            ParseQuery<ParseObject> marksQuery = ParseQuery.getQuery("Marks");
                            marksQuery.whereEqualTo("student", ParseObject.createWithoutData("Student",studentId));
                            marksQuery.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> marksListRet, ParseException e) {
                                    if (e == null) {
                                        if(marksListRet.size()!=0) {
                                            for(int i=0; i<marksListRet.size(); i++) {
                                                marksListRet.get(i).deleteEventually();
                                                Log.d("user", "Deleted: " + "student marks");
                                            }
                                        }else
                                        {

                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                                        Log.d("user", "Error: " + e.getMessage());
                                    }
                                }
                            });


                        }else
                        {

                        }
                    } else {
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                        Log.d("user", "Error: " + e.getMessage());
                    }
                }
            });





            Toast.makeText(getActivity(), "student deleted", Toast.LENGTH_LONG).show();
            ParseObject.createWithoutData("Student", studentId).deleteEventually();

            Intent to_student=new Intent(getActivity(),Students.class);
            to_student.putExtra("id", classId);
            startActivity(to_student);

        }
    });
        return android;
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }
}