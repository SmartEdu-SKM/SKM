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

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    String role;
    String studentId;
    String classId;
    MyDBHandler dbHandler;
    TextView absentDays;
    TextView totalDays;
    TextView percentage;
    java.util.Calendar calendar;
    TextView myDate;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView classList;
    Notification_bar noti_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_attendance_daily);

        Intent from_home = getIntent();
        role = from_home.getStringExtra("role");
        studentId = from_home.getStringExtra("studentId");
        classId = from_home.getStringExtra("classId");

        /*
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attendance");



        noti_bar = (Notification_bar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role);

        dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
        classList = (ListView) findViewById(R.id.classesList);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this); */


            ParseQuery<ParseObject> attQuery = ParseQuery.getQuery(AttendanceDailyTable.TABLE_NAME);
        attQuery.whereEqualTo(AttendanceDailyTable.STUDENT_USER_REF, ParseObject.createWithoutData(StudentTable.TABLE_NAME, studentId));
        attQuery.whereEqualTo(AttendanceDailyTable.FOR_CLASS, ParseObject.createWithoutData(StudentTable.TABLE_NAME, classId));
        attQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> attListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class" + attListRet.size());
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                    //studentRef[0] = studentListRet.get(0);
                    //information(studentRef[0], v);
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
                    information( absent, totalDays, percentage);

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


}
