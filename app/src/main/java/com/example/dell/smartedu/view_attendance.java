package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class view_attendance extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {


    MyDBHandler dbHandler;
    TextView absentDays;
    TextView totalDays;
    TextView percentage;
    java.util.Calendar calendar;
    TextView myDate;


    Notification_bar noti_bar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_attendance_daily);

        Intent from_home = getIntent();
        role = from_home.getStringExtra("role");
        studentId = from_home.getStringExtra("studentId");
        classId = from_home.getStringExtra("classId");
        classGradeId= from_home.getStringExtra("classGradeId");
        institution_code=from_home.getStringExtra("institution_code");
        institution_name=from_home.getStringExtra("institution_name");

if(role.equals("Parent")){
    child_username=from_home.getStringExtra("child_username");
}



        Log.d("view atten ", "stud id: " + studentId+" class id: " + classId);
            ParseQuery<ParseObject> attQuery = ParseQuery.getQuery(AttendanceDailyTable.TABLE_NAME);
        attQuery.whereEqualTo(AttendanceDailyTable.STUDENT_USER_REF, ParseObject.createWithoutData(StudentTable.TABLE_NAME, studentId));
        attQuery.whereEqualTo(AttendanceDailyTable.FOR_CLASS, ParseObject.createWithoutData(ClassTable.TABLE_NAME, classId));
        attQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> attListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class" + attListRet.size());

                    if(attListRet.size()!=0) {
                        double present = 0;
                        double absent = 0;
                        double totalDays = 0;
                        double percentage = 0.0;
                        for (int i = 0; i < attListRet.size(); i++) {
                            ParseObject u = attListRet.get(i);
                            if (u.get(AttendanceDailyTable.STATUS).equals("A")) {
                                absent++;
                            }
                            totalDays++;
                        }
                        present = totalDays - absent;
                        percentage = (present / totalDays) * 100;
                        information(absent, totalDays, percentage);
                    } else{
                        Toast.makeText(getApplicationContext(), "Attendance Not Added", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void information(double absent, double totalDays, double percentage){
        this.absentDays = (TextView) findViewById(R.id.exam);
        this.totalDays = (TextView) findViewById(R.id.totalMarks);
        this.percentage = (TextView) findViewById(R.id.percentage);
        myDate = (TextView) findViewById(R.id.dateText1);
        String absentDays= absent+"";
        String total= totalDays+"";
        String PER= percentage+"";

        this.absentDays.setText(absentDays.trim());
        this.totalDays.setText(total.trim());
        this.percentage.setText(PER.trim());

        calendar = java.util.Calendar.getInstance();
        //System.out.println("Current time =&gt; " + calendar.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final String string_current_date = df.format(calendar.getTime());
        myDate.setText(string_current_date);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(view_attendance.this,login.class);
            startActivity(nouser);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(role.equals("Teacher")) {
            Intent tohome = new Intent(view_attendance.this, student_classes.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institution_name);
            tohome.putExtra("institution_code",institution_code);
            startActivity(tohome);
        }else if(role.equals("Student")){
            Intent tohome = new Intent(view_attendance.this, student_classes.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institution_name);
            tohome.putExtra("institution_code",institution_code);
            tohome.putExtra("classGradeId",classGradeId);
            tohome.putExtra("studentId",studentId);
            tohome.putExtra("for","attendance");
            startActivity(tohome);
        }else if(role.equals("Parent")){
            Intent tohome = new Intent(view_attendance.this,student_classes.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institution_name);
            tohome.putExtra("institution_code",institution_code);
            tohome.putExtra("child_username",child_username);
            tohome.putExtra("classGradeId",classGradeId);
            tohome.putExtra("studentId",studentId);
            tohome.putExtra("for","attendance");
            startActivity(tohome);
        }
    }

}
