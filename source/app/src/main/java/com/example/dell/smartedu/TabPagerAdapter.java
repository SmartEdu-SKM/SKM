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

import com.example.akshdeep.smartedu.*;
import com.example.akshdeep.smartedu.MainActivity;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    String id;
    public TabPagerAdapter(FragmentManager fm, String id) {
        super(fm);
        this.id=id;
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle=new Bundle();
        bundle.putString("id",id);
        switch (i) {
            case 0:
                //Fragement for student information
                Fragment student_info = new student_info();
                student_info.setArguments(bundle);
                return student_info;
            case 1:
                //Fragment for attendance
                Fragment student_attendance=new student_attendance();
                student_attendance.setArguments(bundle);
                return  student_attendance;
            case 2:
                //Fragment for result
                Fragment student_result=new student_result();
                student_result.setArguments(bundle);
                return  student_result;
        }
        return null;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 3; //No of Tabs
    }

}