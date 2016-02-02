package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;

public class admin_home extends BaseActivity{

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    ArrayList<Task> myList;

    MyDBHandler dbHandler;
    Notification_bar noti_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_admin_home);
            Intent home=getIntent();
            role=home.getStringExtra("role");
            institution_name=home.getStringExtra("institution_name");
            institution_code=home.getStringExtra("institution_code");
            Log.d("user", role);
            Log.d("insti code",institution_code);

            dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
            noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
            noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institution_name);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Dashboard");



            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);//pass role
            drawerFragment.setDrawerListener(this);




            GridView gridview = (GridView) findViewById(R.id.gridview);
            //  SharedPreferences mySettings;
            //mySettings = getSharedPreferences(SyncStateContract.Constants.PREFERENCES, Context.MODE_PRIVATE);
            // int gridSize = 50 * Integer.parseInt(mySettings.getString("gridSize", "3"));
            //gridview.setColumnWidth(gridSize + 10);
            gridview.setAdapter(new ImageAdapter(this, densityX, role));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    if (position == 0) { //teachers
                        Intent student_intent = new Intent(admin_home.this, Teachers.class);
                        student_intent.putExtra("institution_code",institution_code);
                        student_intent.putExtra("institution_name",institution_name);
                        student_intent.putExtra("role", role);
                        startActivity(student_intent);
                    } else if (position == 1) {
                        Intent task_intent = new Intent(admin_home.this, Tasks.class);
                        task_intent.putExtra("institution_code",institution_code);
                        task_intent.putExtra("institution_name",institution_name);
                        task_intent.putExtra("role", role);
                        startActivity(task_intent);
                    } else if (position == 2) {
                        Intent student_intent = new Intent(admin_home.this, Admin_classes.class);
                        student_intent.putExtra("institution_code",institution_code);
                        student_intent.putExtra("institution_name",institution_name);
                        student_intent.putExtra("role", role);
                        startActivity(student_intent);
                    }
                    else if (position == 3) {
                        Intent student_intent = new Intent(admin_home.this, teacher_classes.class);
                        student_intent.putExtra("institution_code",institution_code);
                        student_intent.putExtra("institution_name",institution_name);
                        student_intent.putExtra("role", role);
                        student_intent.putExtra("for", "students");
                        startActivity(student_intent);
                    }

                }
            });
            ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());

        }catch(Exception create_error){
            Log.d("user", "error in create admin home: " + create_error.getMessage());
            Toast.makeText(admin_home.this, "error " + create_error, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(admin_home.this,login.class);
            startActivity(nouser);
        }
    }

   /* public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/



}
