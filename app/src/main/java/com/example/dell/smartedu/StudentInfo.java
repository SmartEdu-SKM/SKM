package com.example.dell.smartedu;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseUser;

public class StudentInfo extends FragmentActivity implements FragmentDrawer.FragmentDrawerListener, ActionBar.TabListener{
    ViewPager Tab;
    TabPagerAdapter TabAdapter;
    ActionBar actionBar;
    private FragmentDrawer drawerFragment;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
               Intent from_student = getIntent();
        String id = from_student.getStringExtra("id");
        Toast.makeText(StudentInfo.this, "id of student selected is = " + id, Toast.LENGTH_LONG).show();
        TabAdapter = new TabPagerAdapter(getSupportFragmentManager(),id);

        Tab = (ViewPager)findViewById(R.id.pager);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {

                        actionBar = getActionBar();
                        if (actionBar!=null)
                            actionBar.setSelectedNavigationItem(position);                    }
                });
        Tab.setAdapter(TabAdapter);

        actionBar = getActionBar();
        if (
                actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        //Enable Tabs on Action Bar
        if (
                actionBar != null) {

            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }


        if (
                actionBar != null) {
            Tab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int postion) {

                    actionBar.setSelectedNavigationItem(postion);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });

        }


       /*ActionBar.TabListener tabListener = new ActionBar.TabListener(){

            @Override
            public void onTabReselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

                Tab.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }}; */
        //Add New Tab
        // actionBar.addTab(actionBar.newTab().setText("Android").setTabListener(tabListener));
        //actionBar.addTab(actionBar.newTab().setText("iOS").setTabListener(tabListener));
        // actionBar.addTab(actionBar.newTab().setText("Windows").setTabListener(tabListener));

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

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {

        if(position==0)
        {
            /*Intent i = new Intent(MainActivity.this,CurrentOrder.class);
            startActivity(i);*/
        }

        if(position==2)
        {
            //  Intent i = new Intent(MainActivity.this,HomeSlider.class);
            //startActivity(i);
        }

        if(position==8)
        {
            Intent i = new Intent(StudentInfo.this,ChooseRole.class);
            startActivity(i);
        }

        if(position==9)
        {
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            Intent i = new Intent(StudentInfo.this, login.class);
            startActivity(i);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        Tab.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}