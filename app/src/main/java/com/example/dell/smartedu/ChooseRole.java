package com.example.dell.smartedu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ChooseRole extends AppCompatActivity {

    private Toolbar mToolbar;
    Button student;
    Button parent;
    Button teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);
        String sessionToken = ParseUser.getCurrentUser().getSessionToken();
        try {

            ParseUser.become(sessionToken);

        } catch (ParseException e1) {
            Log.d("session", e1.getMessage());
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose Role");
        student=(Button)findViewById(R.id.button_student);
        parent=(Button)findViewById(R.id.button_parent);
        teacher=(Button)findViewById(R.id.button_teacher);

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTeacher();
            }
        });

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseParent();
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseStudent();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_role, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void chooseTeacher() {

        ParseQuery<ParseObject> roleQuery = ParseQuery.getQuery("Role");
        roleQuery.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        roleQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> roleListRet, ParseException e) {
                if (e == null) {
                        int flag = 0;
                    Log.d("role", "Retrieved " + roleListRet.size() + " roles");

                    for (int i = 0; i < roleListRet.size(); i++) {
                        flag=0;
                        ParseObject u = (ParseObject) roleListRet.get(i);
                        String name = u.getString("roleName").toString();
                        if (name.equals("Teacher")) {
                            flag =1;
                            Intent j = new Intent(ChooseRole.this, MainActivity.class);
                            startActivity(j);
                            Toast.makeText(getApplicationContext(), "Teacher Module", Toast.LENGTH_LONG)
                                    .show();
                            break;
                        }


                    }
                    if(flag==0) {
                        Toast.makeText(getApplicationContext(), "Role not added", Toast.LENGTH_LONG)
                                .show();
                    }



                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void chooseStudent() {

        ParseQuery<ParseObject> roleQuery = ParseQuery.getQuery("Role");
        roleQuery.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        roleQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> roleListRet, ParseException e) {
                if (e == null) {
                    int flag = 0;
                    Log.d("role", "Retrieved " + roleListRet.size() + " roles");

                    for (int i = 0; i < roleListRet.size(); i++) {
                        flag=0;
                        ParseObject u = (ParseObject) roleListRet.get(i);
                        String name = u.getString("roleName").toString();
                        if (name.equals("Student")) {
                            flag =1;
                            Intent j = new Intent(ChooseRole.this, student_home_activity.class);
                            startActivity(j);
                            Toast.makeText(getApplicationContext(), "Student Module", Toast.LENGTH_LONG)
                                    .show();
                            break;
                        }


                    }
                    if(flag==0)
                        Toast.makeText(getApplicationContext(), "Role not added", Toast.LENGTH_LONG)
                                .show();



                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void chooseParent() {

        ParseQuery<ParseObject> roleQuery = ParseQuery.getQuery("Role");
        roleQuery.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        roleQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> roleListRet, ParseException e) {
                if (e == null) {
                    int flag = 0;
                    Log.d("role", "Retrieved " + roleListRet.size() + " roles");

                    for (int i = 0; i < roleListRet.size(); i++) {
                        flag=0;
                        ParseObject u = (ParseObject) roleListRet.get(i);
                        String name = u.getString("roleName").toString();
                        if (name.equals("Parent")) {
                            flag =1;
                            Intent j = new Intent(ChooseRole.this, parent_home_activity.class);
                            startActivity(j);
                            Toast.makeText(getApplicationContext(), "Parent Module", Toast.LENGTH_LONG)
                                    .show();
                            break;
                        }


                    }
                    if(flag==0)
                        Toast.makeText(getApplicationContext(), "Role not added", Toast.LENGTH_LONG)
                                .show();



                } else {
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
            Intent nouser=new Intent(getApplicationContext(),login.class);
            startActivity(nouser);
        }

    }
}


