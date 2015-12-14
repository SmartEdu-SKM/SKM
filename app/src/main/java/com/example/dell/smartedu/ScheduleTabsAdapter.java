package com.example.dell.smartedu;

/**
 * Created by Shubham Bhasin on 07-Nov-15.
 */
/**
 * Created by Shubham Bhasin on 07-Nov-15.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class ScheduleTabsAdapter extends FragmentStatePagerAdapter {
    public ScheduleTabsAdapter(FragmentManager fm) {
        super(fm);

        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle=new Bundle();
       // bundle.putString("id",id);
        Fragment day = new Schedule_days();
        switch (i) {
            case 0:
                //Fragement for student information
                bundle.putString("day","Monday");
               // Fragment monday = new student_info();
                day.setArguments(bundle);
                return day;
            case 1:
                //Fragment for attendance
                bundle.putString("day","Tuesday");
               // Fragment tuesday=new student_attendance();
                day.setArguments(bundle);
                return  day;
            case 2:
                //Fragment for result
                bundle.putString("day","Wednesday");
                //Fragment wednesday=new student_result();
               day.setArguments(bundle);
                return  day;
            case 3:
                //Fragement for student information
                bundle.putString("day","Thursday");
               // Fragment thursday = new student_info();
                day.setArguments(bundle);
                return day;
            case 4:
                //Fragment for attendance
                bundle.putString("day","Friday");
               // Fragment friday=new student_attendance();
                day.setArguments(bundle);
                return  day;
            case 5:
                //Fragment for result
                bundle.putString("day","Saturday");
               // Fragment saturday=new student_result();
                day.setArguments(bundle);
                return  day;
            case 6:
                //Fragment for result
                bundle.putString("day","Sunday");
               // Fragment sunday=new student_result();
                day.setArguments(bundle);
                return  day;
        }
        return null;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 7; //No of Tabs
    }

}