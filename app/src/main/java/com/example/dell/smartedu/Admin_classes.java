package com.example.dell.smartedu;

import android.app.Dialog;
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
 * Created by Shubham Bhasin on 10/7/2015.
 */
public class Admin_classes extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button addClassButton;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView classList;
    ListView classSubjectList;
    Notification_bar noti_bar;
    Button ok;
    Button deleteClassButton;
    Button addSubjectButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_classes);

        Intent from_student = getIntent();
        role=from_student.getStringExtra("role");
        institution_code=from_student.getStringExtra("institution_code");
        institution_name=from_student.getStringExtra("institution_name");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Classes");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institution_name);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        addClassButton = (Button)findViewById(R.id.addClassButton);
        classList = (ListView) findViewById(R.id.classList);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);


        /*ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
        studentQuery.whereEqualTo("class",classname);
        studentQuery.whereEqualTo("teacher",ParseUser.getCurrentUser());*/
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        classQuery.whereEqualTo(ClassTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> classListRet, ParseException e) {
                if (e == null) {


                    Log.d("class", "Retrieved the classes with insti code "+ institution_code);


                    if(classListRet.size()!=0)
                    {
                        ArrayList<String> studentLt = new ArrayList<String>();
                        ArrayAdapter adapter = new ArrayAdapter(Admin_classes.this, android.R.layout.simple_list_item_1, studentLt);
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                        Log.d("user", "Retrieved " + classListRet.size() + " classes");
                        //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < classListRet.size(); i++) {
                            ParseObject u = (ParseObject) classListRet.get(i);
                            //  if(u.getString("class").equals(id)) {
                            String name = u.getString(ClassTable.CLASS_NAME);
                           // String subject= u.getString(ClassTable.SUBJECT);
                           // String item= name + ". " + subject;
                            //name += "\n";
                            // name += u.getInt("age");

                            adapter.add(name);
                            // }

                        }


                        classList.setAdapter(adapter);


                        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String item = ((TextView) view).getText().toString();
                                Log.d("class", item);

                                final Dialog class_info=new Dialog(Admin_classes.this);
                                class_info.setContentView(R.layout.class_details);
                                class_info.setTitle(item);
                                classSubjectList=(ListView)class_info.findViewById(R.id.subjectList);
                                ok=(Button)class_info.findViewById(R.id.doneButton);
                                deleteClassButton=(Button)class_info.findViewById(R.id.delClassButton);
                                addSubjectButton=(Button)class_info.findViewById(R.id.addSubjectButton);
                                ParseQuery<ParseObject> classSubjectQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
                                classSubjectQuery.whereEqualTo(ClassTable.CLASS_NAME, item);
                                classSubjectQuery.whereEqualTo(ClassTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                                classSubjectQuery.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> subjectListRet, ParseException e) {
                                        if (e == null) {
                                            if (subjectListRet.size() != 0) {



                                                ArrayList<String> subjectLt = new ArrayList<String>();
                                                ArrayAdapter subjectadapter = new ArrayAdapter(class_info.getContext(), android.R.layout.simple_list_item_1, subjectLt);
                                                //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                                Log.d("user", "Retrieved " + subjectListRet.size() + " subjects");
                                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                                for (int i = 0; i < subjectListRet.size(); i++) {
                                                    ParseObject u = (ParseObject) subjectListRet.get(i);
                                                    //  if(u.getString("class").equals(id)) {
                                                    String name = u.getString(ClassTable.SUBJECT);
                                                    // String subject= u.getString(ClassTable.SUBJECT);
                                                    // String item= name + ". " + subject;
                                                    //name += "\n";
                                                    // name += u.getInt("age");

                                                    subjectadapter.add(name);
                                                    // }

                                                }


                                                classSubjectList.setAdapter(subjectadapter);
                                                class_info.show();






                                            }else
                                            {
                                                Log.d("class","Error in subject query");
                                            }
                                        } else {
                                            Log.d("user", "Error: " + e.getMessage());
                                        }
                                    }
                                });


                            }
                        });





                    }else
                    {
                        Log.d("class","error in query");
                    }

                } else {
                    Toast.makeText(Admin_classes.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("class", "Error: " + e.getMessage());
                }
            }
        });



        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();



        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Admin_classes.this, NewStudent.class);
                i.putExtra("institution_name",institution_name);
                i.putExtra("institution_code",institution_code);
                i.putExtra("id",classId);
                startActivity(i);
            }
        });




    }



    protected void sleep(int time)
    {
        for(int x=0;x<time;x++)
        {

        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(Admin_classes.this,login.class);
            startActivity(nouser);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(Admin_classes.this,MainActivity.class);
                startActivity(i);
                finish();
                //do your own thing here
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


}
