package com.example.dell.smartedu;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.parse.ParseUser;

public class Schedule extends BaseActivity implements FragmentDrawer.FragmentDrawerListener, ActionBar.TabListener{
    ViewPager Tab;
    ScheduleTabsAdapter TabAdapter;
    ActionBar actionBar;
    private FragmentDrawer drawerFragment;
    private Toolbar mToolbar;
    TabLayout tabLayout;
    String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
       /* drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);*/
        Bundle fromrole= getIntent().getExtras();
        role = fromrole.getString("role");
        Tab = (ViewPager) findViewById(R.id.pager);
        Tab.setAdapter(new ScheduleTabsAdapter(getSupportFragmentManager(), role));


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(Tab);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

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

        /*
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
*/

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
            Intent nouser=new Intent(Schedule.this,login.class);
            startActivity(nouser);
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