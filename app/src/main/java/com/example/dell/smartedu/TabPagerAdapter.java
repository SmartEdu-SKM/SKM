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

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    String id;
    String classId;
    String institution_code;
    String  institution_name;
    String classGradeId;
    private String fragments[] ={"Info","Attendance","Result"};
    public TabPagerAdapter(FragmentManager fm, String id, String classId,String classGradeId,String institution_name,String institution_code) {
        super(fm);
        this.id=id;
        this.classId=classId;
        this.classGradeId=classGradeId;
        this.institution_name=institution_name;
        this.institution_code=institution_code;
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle=new Bundle();
        bundle.putString("institution_name",institution_name);
        bundle.putString("institution_code",institution_code);
        bundle.putString("id",id);
        bundle.putString("classId",classId);
        bundle.putString("classGradeId",classGradeId);
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
        return fragments.length; //No of Tabs
    }

    @Override
    public CharSequence getPageTitle(int position){
        return fragments[position];
    }

}