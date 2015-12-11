package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class Tasks extends BaseActivity  implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button addTaskButton;
    private FragmentDrawer drawerFragment;
    Notification_bar noti_bar;
    MyDBHandler dbHandler;
    Task task = new Task();
    ArrayList<Task> myList;
    ListView taskList;
    String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tasks");

        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        addTaskButton = (Button)findViewById(R.id.addButton);
        taskList = (ListView) findViewById(R.id.taskList);

        myList = dbHandler.getAllTasks();

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        
        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);
        Bundle fromrole= getIntent().getExtras();
        role = fromrole.getString("role");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(),role);
        ParseQuery<ParseObject> taskQuery = ParseQuery.getQuery("Task");
        taskQuery.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        taskQuery.whereEqualTo("addedByRole", role);
        taskQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> taskListRet, ParseException e) {
                if (e == null) {

                    ArrayList<String> taskLt = new ArrayList<String>();
                    ArrayAdapter adapter = new ArrayAdapter(Tasks.this, android.R.layout.simple_list_item_1, taskLt);
                    //Log.d("user", "Retrieved " + userList.size() + " users");

                    Log.d("user", "Retrieved " + taskListRet.size() + " users");
                    Toast.makeText(getApplicationContext(), taskListRet.toString(), Toast.LENGTH_LONG)
                            .show();
                    for (int i = 0; i < taskListRet.size(); i++) {
                        ParseObject u = (ParseObject) taskListRet.get(i);
                        String name = u.getString("TaskName").toString();
                        name += "\n";
                        name += u.getString("TaskDescription").toString();

                        adapter.add(name);

                    }


                    taskList.setAdapter(adapter);

                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });





        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Tasks.this, NewTask.class);
                i.putExtra("role",role);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(Tasks.this,login.class);
            startActivity(nouser);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(Tasks.this,MainActivity.class);
                startActivity(i);
                finish();
                //do your own thing here
                return true;
            default: return super.onOptionsItemSelected(item);
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
            Intent i = new Intent(Tasks.this,ChooseRole.class);
            startActivity(i);
        }

        if(position==9)
        {
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            Intent i = new Intent(Tasks.this, login.class);
            startActivity(i);
        }
    }
}
