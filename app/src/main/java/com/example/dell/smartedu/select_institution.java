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
public class select_institution extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;

    ListView institutionList;
    Notification_bar noti_bar;
    ImageView noinsti;

    String child_username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_institution);

        Intent from_student = getIntent();

        role=from_student.getStringExtra("role");

        if(role.equals("Parent"))
        {
            //child_code=from_student.getStringExtra("child_code");
            child_username=from_student.getStringExtra("child_username");
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select institution");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,"-");
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        institutionList = (ListView) findViewById(R.id.institutionList);
        noinsti=(ImageView)findViewById(R.id.noinstitute);
        noinsti.setVisibility(View.INVISIBLE);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,"");
        drawerFragment.setDrawerListener(this);


        ParseQuery<ParseObject> institutionQuery = ParseQuery.getQuery(RoleTable.TABLE_NAME);
        institutionQuery.whereEqualTo(RoleTable.ROLE,role);
        institutionQuery.whereEqualTo(RoleTable.OF_USER_REF,ParseUser.getCurrentUser());
        institutionQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> institutionListRet, ParseException e) {
                if (e == null) {
                    if(institutionListRet.size()==0) {
                        noinsti.setVisibility(View.VISIBLE);
                    }else if(institutionListRet.size()==1){

                        Log.d("institution", "Single institution");

                        try {
                            ParseObject insti=(ParseObject)institutionListRet.get(0).fetchIfNeeded().get(RoleTable.ENROLLED_WITH);
                            institution_code=insti.fetchIfNeeded().getObjectId();
                            institution_name=insti.getString(InstitutionTable.INSTITUTION_NAME);

                            if(role.equals("Teacher"))
                            {
                                Intent teacher_home_page=new Intent(select_institution.this,MainActivity.class);
                                teacher_home_page.putExtra("role",role);
                                teacher_home_page.putExtra("institution_code",institution_code);
                                teacher_home_page.putExtra("institution_name",institution_name);
                                startActivity(teacher_home_page);
                            }else if(role.equals("Student"))
                            {
                                Intent student_home_page=new Intent(select_institution.this,student_home_activity.class);
                                student_home_page.putExtra("role",role);
                                student_home_page.putExtra("institution_code",institution_code);
                                student_home_page.putExtra("institution_name",institution_name);
                                startActivity(student_home_page);
                            }else if(role.equals("Parent"))
                            {
                                Intent parent_home_page=new Intent(select_institution.this,parent_home_activity.class);
                                parent_home_page.putExtra("role", role);
                                parent_home_page.putExtra("child_username",child_username);
                                parent_home_page.putExtra("institution_code",institution_code);
                                parent_home_page.putExtra("institution_name",institution_name);
                                startActivity(parent_home_page);
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }




                    }else {
                        Log.d("institution", "Retrieved the institutions");

                        ArrayList<String> studentLt = new ArrayList<String>();
                        ArrayAdapter adapter = new ArrayAdapter(select_institution.this, android.R.layout.simple_list_item_1, studentLt);

                        Log.d("user", "Retrieved " + institutionListRet.size() + " institutions");
                        for (int i = 0; i < institutionListRet.size(); i++) {
                            ParseObject u = (ParseObject) institutionListRet.get(i);
                            ParseObject inst=(ParseObject)u.get(RoleTable.ENROLLED_WITH);
                            String name = null;
                            try {
                                name = inst.fetchIfNeeded().getString(InstitutionTable.INSTITUTION_NAME);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            String type = null;
                            try {
                                type = inst.fetchIfNeeded().getString(InstitutionTable.INSTITUTION_TYPE);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            String item = name + ", " + type;

                            adapter.add(item);


                        }


                        institutionList.setAdapter(adapter);


                        institutionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final String institution = ((TextView) view).getText().toString();
                                Log.d("institution", institution);

                                //item  = item.replaceAll("[\n\r\\s]", "");
                                String[] institutiondetails = institution.split("\\, ");

                                final String[] details = new String[2];
                                int j = 0;
                                for (int i = 0; i <= 1; i++) {
                                    Log.d("user", institutiondetails[i]);
                                }

                                for (String x : institutiondetails) {
                                    details[j++] = x;
                                }

                                Log.d("institution", "name: " + details[0].trim() + " type: " + details[1]);

                                ParseQuery instiQuery=ParseQuery.getQuery(InstitutionTable.TABLE_NAME);
                                instiQuery.whereEqualTo(InstitutionTable.INSTITUTION_NAME,details[0]);
                                instiQuery.whereEqualTo(InstitutionTable.INSTITUTION_TYPE,details[1]);
                                instiQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> institutions, ParseException e) {
                                      if(e==null){
                                          if(institutions.size()!=0)
                                          {

                                              try {
                                                  institution_code=institutions.get(0).fetchIfNeeded().getObjectId();

                                              if(role.equals("Teacher"))
                                              {
                                                  Intent teacher_home_page=new Intent(select_institution.this,MainActivity.class);
                                                  teacher_home_page.putExtra("role",role);
                                                  teacher_home_page.putExtra("institution_code",institution_code);
                                                  teacher_home_page.putExtra("institution_name",details[0]);
                                                  startActivity(teacher_home_page);
                                              }else if(role.equals("Student"))
                                              {
                                                  Intent student_home_page=new Intent(select_institution.this,student_home_activity.class);
                                                  student_home_page.putExtra("role",role);
                                                  student_home_page.putExtra("institution_code",institution_code);
                                                  student_home_page.putExtra("institution_name",details[0]);
                                                  startActivity(student_home_page);
                                              }else if(role.equals("Parent"))
                                              {
                                                  Intent parent_home_page=new Intent(select_institution.this,parent_home_activity.class);
                                                  parent_home_page.putExtra("role", role);
                                                  parent_home_page.putExtra("institution_code",institution_code);
                                                  parent_home_page.putExtra("institution_name",details[0]);
                                                  startActivity(parent_home_page);
                                              }
                                              } catch (ParseException e1) {
                                                  e1.printStackTrace();
                                              }


                                          }else
                                          {
                                              Log.d("institution","error in query");
                                          }
                                      }else
                                      {
                                          Log.d("institution","error");
                                      }
                                    }

                                });


                            }
                        });


                    }
                } else {
                    Toast.makeText(select_institution.this, "error", Toast.LENGTH_LONG).show();
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
            Intent nouser=new Intent(select_institution.this,login.class);
            startActivity(nouser);
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(select_institution.this,MainActivity.class);
                startActivity(i);
                finish();
                //do your own thing here
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


}
