package com.example.dell.smartedu;

import android.content.Intent;
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

public class AddRole extends BaseActivity {

    private Toolbar mToolbar;
    Button teacher;
    Button student;
    Button parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_role);
        teacher=(Button)findViewById(R.id.button_teacher);
        student=(Button)findViewById(R.id.button_student);
        parent=(Button)findViewById(R.id.button_parent);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Role");

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            addParent();
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             addStudent();
            }
        });

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             addTeacher();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_role, menu);
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

    public void addStudent() {


        ParseQuery<ParseObject> roleQuery = ParseQuery.getQuery(RoleTable.TABLE_NAME);
        roleQuery.whereEqualTo(RoleTable.OF_USER_REF, ParseUser.getCurrentUser());
        roleQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> roleListRet, ParseException e) {
                if (e == null) {

                    int k = 0;
                    Log.d("role", "Retrieved " + roleListRet.size() + " roles");

                    for (int i = 0; i < roleListRet.size(); i++) {
                        ParseObject u = (ParseObject) roleListRet.get(i);
                        String name = u.getString(RoleTable.ROLE).toString().trim();
                        if (name.equals("Student")) {
                            Toast.makeText(getApplicationContext(), "Role already added. Go to Choose Role", Toast.LENGTH_LONG)
                                    .show();
                            k=1;
                            break;
                        }
                    }


                    if (k==0) {
                        ParseObject role = new ParseObject(RoleTable.TABLE_NAME);
                        role.put(RoleTable.OF_USER_REF, ParseUser.getCurrentUser());
                        role.put(RoleTable.ROLE, "Student");
                        role.saveInBackground();

                    /*
                    dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
                    Log.i("abcd", "(Inside NewTask) before calling dbHandler.addTask()");
                    dbHandler.addTask(task);
                    Log.i("abcd", "(Inside NewTask) after calling dbHandler.addTask()"); */
                        Toast.makeText(getApplicationContext(), "Role successfully added", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(AddRole.this, Role.class);
                        startActivity(i);
                        finish();
                    }

                } else {
                    Log.d("role", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void addTeacher() {

        ParseQuery<ParseObject> roleQuery = ParseQuery.getQuery(RoleTable.TABLE_NAME);
        roleQuery.whereEqualTo(RoleTable.OF_USER_REF, ParseUser.getCurrentUser());
        roleQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> roleListRet, ParseException e) {
                if (e == null) {

                    int k = 0;
                    Log.d("role", "Retrieved " + roleListRet.size() + " roles");

                    for (int i = 0; i < roleListRet.size(); i++) {
                        ParseObject u = (ParseObject) roleListRet.get(i);
                        String name = u.getString(RoleTable.ROLE).toString();
                        if (name.equals("Teacher")) {
                            Toast.makeText(getApplicationContext(), "Role already added. Go to Choose Role", Toast.LENGTH_LONG)
                                    .show();
                            k = 1;
                            break;
                        }
                    }


                    if (k == 0) {
                        ParseObject role = new ParseObject(RoleTable.TABLE_NAME);
                        role.put(RoleTable.OF_USER_REF, ParseUser.getCurrentUser());
                        role.put(RoleTable.ROLE, "Teacher");
                        role.saveInBackground();

                    /*
                    dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
                    Log.i("abcd", "(Inside NewTask) before calling dbHandler.addTask()");
                    dbHandler.addTask(task);
                    Log.i("abcd", "(Inside NewTask) after calling dbHandler.addTask()"); */
                        Toast.makeText(getApplicationContext(), "Role successfully added", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(AddRole.this, Role.class);
                        startActivity(i);
                        finish();
                    }

                } else {
                    Log.d("role", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void addParent() {

        ParseQuery<ParseObject> roleQuery = ParseQuery.getQuery(RoleTable.TABLE_NAME);
        roleQuery.whereEqualTo(RoleTable.OF_USER_REF, ParseUser.getCurrentUser());
        roleQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> roleListRet, ParseException e) {
                if (e == null) {

                    int k = 0;
                    Log.d("role", "Retrieved " + roleListRet.size() + " roles");

                    for (int i = 0; i < roleListRet.size(); i++) {
                        ParseObject u = (ParseObject) roleListRet.get(i);
                        String name = u.getString(RoleTable.ROLE).toString();
                        if (name.equals("Parent")) {
                            Toast.makeText(getApplicationContext(), "Role already added. Go to Choose Role", Toast.LENGTH_LONG)
                                    .show();
                            k=1;
                            break;
                        }
                    }


                    if (k==0) {
                        ParseObject role = new ParseObject(RoleTable.TABLE_NAME);
                        role.put(RoleTable.OF_USER_REF, ParseUser.getCurrentUser());
                        role.put(RoleTable.ROLE, "Parent");
                        role.saveInBackground();

                    /*
                    dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
                    Log.i("abcd", "(Inside NewTask) before calling dbHandler.addTask()");
                    dbHandler.addTask(task);
                    Log.i("abcd", "(Inside NewTask) after calling dbHandler.addTask()"); */
                        Toast.makeText(getApplicationContext(), "Role successfully added", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(AddRole.this, Role.class);
                        startActivity(i);
                        finish();
                    }

                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

    }


}
