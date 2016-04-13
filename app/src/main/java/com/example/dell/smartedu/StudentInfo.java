package com.example.dell.smartedu;


import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseUser;

public class StudentInfo extends FragmentActivity{
    ViewPager Tab;
    TabPagerAdapter TabAdapter;
    ActionBar actionBar;

    private Toolbar mToolbar;
    TabLayout tabLayout;
    String classId;
    String id;
    String institution_name;
    String institution_code;
    String classGradeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        // mToolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent from_student = getIntent();
        id = from_student.getStringExtra("id");
        classId = from_student.getStringExtra("classId");
        classGradeId = from_student.getStringExtra("classGradeId");
        institution_code = from_student.getStringExtra("institution_code");
        institution_name = from_student.getStringExtra("institution_name");
        // Toast.makeText(StudentInfo.this, "id of student selected is = " + id, Toast.LENGTH_LONG).show();
        //TabAdapter = new TabPagerAdapter(getSupportFragmentManager(),id);

        Tab = (ViewPager) findViewById(R.id.pager);
        Tab.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), id, classId, classGradeId, institution_name, institution_code));


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(Tab);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Tab.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Tab.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Tab.setCurrentItem(tab.getPosition());
            }
        });


    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(StudentInfo.this,login.class);
            startActivity(nouser);
        }
    }


}