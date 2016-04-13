package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class parent_choose_child extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;

    ListView childList;
    Notification_bar noti_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_choose_child);

        Intent from_student = getIntent();

        role=from_student.getStringExtra("role");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select child");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,"-");
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        childList = (ListView) findViewById(R.id.childList);


        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, "");
        drawerFragment.setDrawerListener(this);


        Toast.makeText(parent_choose_child.this, "role selected = " +role, Toast.LENGTH_LONG).show();


        ParseQuery<ParseObject> childQuery = ParseQuery.getQuery(ParentTable.TABLE_NAME);
        childQuery.whereEqualTo(ParentTable.PARENT_USER_REF,ParseUser.getCurrentUser());
        childQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> childListRet, ParseException e) {
                if (e == null) {
                    if(childListRet.size()==0) {

                    }else if(childListRet.size()==1){

                        Log.d("child", "Single child");

                        try {
                            ParseUser child=(ParseUser)childListRet.get(0).fetchIfNeeded().get(ParentTable.CHILD_USER_REF);
                            String child_code=child.fetchIfNeeded().getObjectId();

                            String child_username=child.fetchIfNeeded().getUsername();

                            Intent institute_selection=new Intent(parent_choose_child.this,select_institution.class);
                            institute_selection.putExtra("role", role);
                           // institute_selection.putExtra("child_code",child_code);
                            institute_selection.putExtra("child_username",child_username);
                            startActivity(institute_selection);

                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }




                    }else {
                        Log.d("child", "Retrieved children");

                        ArrayList<String> studentLt = new ArrayList<String>();
                        ArrayAdapter adapter = new ArrayAdapter(parent_choose_child.this, android.R.layout.simple_list_item_1, studentLt);
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                        Log.d("user", "Retrieved " + childListRet.size() + " children");
                        //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < childListRet.size(); i++) {
                            try{
                            ParseUser child=(ParseUser)childListRet.get(0).fetchIfNeeded().get(ParentTable.CHILD_USER_REF);

                                String name = child.fetchIfNeeded().getUsername();





                            adapter.add(name);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }


                        }


                        childList.setAdapter(adapter);


                        childList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final String selected_child = ((TextView) view).getText().toString();
                                Log.d("child", selected_child);




                                Intent parent_home_page = new Intent(parent_choose_child.this, select_institution.class);
                                parent_home_page.putExtra("role", role);
                                parent_home_page.putExtra("child_username", selected_child);

                                startActivity(parent_home_page);


                            }
                        });


                    }
                } else {
                    Toast.makeText(parent_choose_child.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(parent_choose_child.this,login.class);
            startActivity(nouser);
        }
    }


  

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(parent_choose_child.this,MainActivity.class);
                startActivity(i);
                finish();
                //do your own thing here
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


}
