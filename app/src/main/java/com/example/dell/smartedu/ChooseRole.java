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

        try{
        setContentView(R.layout.activity_choose_role);

        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(getApplicationContext(),login.class);
            startActivity(nouser);
        }

        try {
            String sessionToken = ParseUser.getCurrentUser().getSessionToken();

            ParseUser.become(sessionToken);

        } catch (ParseException e1) {
            Log.d("session", e1.getMessage());
            Toast.makeText(getApplicationContext(), "Error in choose role "+e1.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose Role");

        }catch(Exception create_error){
            Log.d("user", "error in create choose role: " + create_error.getMessage());
            Toast.makeText(ChooseRole.this,"error " + create_error, Toast.LENGTH_LONG).show();
        }


        student=(Button)findViewById(R.id.button_student);
        parent=(Button)findViewById(R.id.button_parent);
        teacher=(Button)findViewById(R.id.button_teacher);

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roleChosen("Teacher");

            }
        });

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               roleChosen("Parent");

            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              roleChosen("Student");

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



    public void roleChosen(final String role) {

        ParseQuery<ParseObject> roleQuery = ParseQuery.getQuery(RoleTable.TABLE_NAME);
        roleQuery.whereEqualTo(RoleTable.OF_USER_REF, ParseUser.getCurrentUser());
        roleQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> roleListRet, ParseException e) {
                if (e == null) {
                    int flag = 0;
                    Log.d("role", "Retrieved " + roleListRet.size() + " roles");

                    for (int i = 0; i < roleListRet.size(); i++) {
                        flag=0;
                        ParseObject u = roleListRet.get(i);
                        String name = u.getString(RoleTable.ROLE).toString();
                        if (name.equals(role)) {
                            flag =1;
                            if(role.equals("Parent")) {
                                Intent j = new Intent(ChooseRole.this, parent_choose_child.class);
                                j.putExtra("role", role);
                                startActivity(j);
                            }else
                            {
                                Intent j = new Intent(ChooseRole.this, select_institution.class);
                                j.putExtra("role", role);
                                startActivity(j);
                            }

                            Toast.makeText(getApplicationContext(), role + " Module", Toast.LENGTH_LONG)
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


