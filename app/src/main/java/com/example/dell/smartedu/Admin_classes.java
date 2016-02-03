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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    ListView classSectionList;
    Notification_bar noti_bar;
    Button ok;
    Button deleteClassButton;
    Button addSectionButton;
    EditText getNewSection;
    TextView singleInputField;
    Button doneNewSection;


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

        ParseQuery<ParseObject> classGradeQuery = ParseQuery.getQuery(ClassGradeTable.TABLE_NAME);
        classGradeQuery.whereEqualTo(ClassGradeTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
        classGradeQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> classGradeListRet, ParseException e) {
                if (e == null) {

                    if (e == null) {




                        Log.d("class", "Retrieved the classes with insti code " + institution_code);


                        if(classGradeListRet.size()!=0)
                        {
                            ArrayList<String> classGradeLt = new ArrayList<String>();
                            ArrayAdapter adapter = new ArrayAdapter(Admin_classes.this, android.R.layout.simple_list_item_1,classGradeLt);
                            //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                            Log.d("user", "Retrieved " + classGradeListRet.size() + " classes");
                            //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < classGradeListRet.size(); i++) {
                                ParseObject u = (ParseObject) classGradeListRet.get(i);
                                //  if(u.getString("class").equals(id)) {
                                String name = u.getString(ClassGradeTable.CLASS_GRADE);
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
                                    final String item = ((TextView) view).getText().toString();
                                    Log.d("class", item);



                                    final Dialog class_info=new Dialog(Admin_classes.this);
                                    class_info.setContentView(R.layout.class_details);
                                    class_info.setTitle(item);

                                    setDialogSize(class_info);

                                    classSectionList=(ListView)class_info.findViewById(R.id.subjectList);
                                    ok=(Button)class_info.findViewById(R.id.doneButton);
                                    deleteClassButton=(Button)class_info.findViewById(R.id.delClassButton);
                                    addSectionButton=(Button)class_info.findViewById(R.id.addSubjectButton);
                                    addSectionButton.setText("Add Section");
                                    ParseQuery<ParseObject> classSectionQuery = ParseQuery.getQuery(ClassGradeTable.TABLE_NAME);
                                    classSectionQuery.whereEqualTo(ClassGradeTable.CLASS_GRADE, item);
                                    classSectionQuery.whereEqualTo(ClassGradeTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                                    classSectionQuery.findInBackground(new FindCallback<ParseObject>() {
                                        public void done(List<ParseObject> sectionListRet, ParseException e) {
                                            if (e == null) {
                                                if (sectionListRet.size() != 0) {



                                                    ArrayList<String> sectionLt = new ArrayList<String>();
                                                    ArrayAdapter sectionadapter = new ArrayAdapter(class_info.getContext(), android.R.layout.simple_list_item_1, sectionLt);
                                                    //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                                    Log.d("user", "Retrieved " + sectionListRet.size() + " sections");
                                                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                                    for (int i = 0; i < sectionListRet.size(); i++) {
                                                        ParseObject u = (ParseObject) sectionListRet.get(i);
                                                        //  if(u.getString("class").equals(id)) {
                                                        String name = u.getString(ClassGradeTable.CLASS_GRADE);
                                                        String section= u.getString(ClassGradeTable.SECTION);
                                                        if(section!=null)
                                                        {
                                                            String item= name + " " + section;
                                                            sectionadapter.add(item);
                                                        }
                                                        else
                                                        {
                                                            String item=name;
                                                            sectionadapter.add(item);
                                                        }
                                                        //name += "\n";
                                                        // name += u.getInt("age");


                                                        // }

                                                    }


                                                    classSectionList.setAdapter(sectionadapter);
                                                    class_info.show();


                                                    ok.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            class_info.dismiss();
                                                        }
                                                    });


                                                    addSectionButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            final Dialog newSection=new Dialog(Admin_classes.this);
                                                            newSection.setContentView(R.layout.get_single_detail);
                                                            singleInputField=(TextView)newSection.findViewById(R.id.single_entity);
                                                            doneNewSection=(Button)newSection.findViewById(R.id.okButton);
                                                            getNewSection=(EditText)newSection.findViewById(R.id.inputdata);
                                                            singleInputField.setText("Section Name:");
                                                            newSection.show();


                                                            doneNewSection.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    if(getNewSection.getText().toString().equals(""))
                                                                    {
                                                                        Toast.makeText(Admin_classes.this,"Section field left empty",Toast.LENGTH_LONG).show();
                                                                    }else
                                                                    {


                                                                        ParseQuery<ParseObject> newSectionQuery=ParseQuery.getQuery(ClassGradeTable.TABLE_NAME);
                                                                        newSectionQuery.whereEqualTo(ClassGradeTable.CLASS_GRADE,item);
                                                                        newSectionQuery.whereEqualTo(ClassGradeTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                                                                        newSectionQuery.findInBackground(new FindCallback<ParseObject>() {
                                                                            @Override
                                                                            public void done(List<ParseObject> sectionsList, ParseException e) {
                                                                                if(e==null)
                                                                                {
                                                                                    Log.d("classSection",sectionsList.size() + " Sections");
                                                                                    int flag=0;
                                                                                    if(sectionsList.size()!=0)
                                                                                    {
                                                                                        for(int x=0;x<sectionsList.size();x++)
                                                                                        {
                                                                                            if((sectionsList.get(x).getString(ClassGradeTable.SECTION)).equalsIgnoreCase(getNewSection.getText().toString())){
                                                                                                Toast.makeText(Admin_classes.this,"Section already added. Type Another section name",Toast.LENGTH_LONG).show();
                                                                                                flag=1;
                                                                                            break;
                                                                                            }
                                                                                        }
                                                                                        if(flag==0)
                                                                                        {
                                                                                            ParseObject newSectionObject=new ParseObject(ClassGradeTable.TABLE_NAME);
                                                                                            newSectionObject.put(ClassGradeTable.CLASS_GRADE,item);
                                                                                            newSectionObject.put(ClassGradeTable.SECTION,getNewSection.getText().toString());
                                                                                            newSectionObject.put(ClassGradeTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                                                                                            newSectionObject.saveInBackground();
                                                                                        newSection.dismiss();
                                                                                        }
                                                                                    }else
                                                                                    {
                                                                                        Log.d("classSection","error in query logic");
                                                                                    }
                                                                                }else
                                                                                {
                                                                                    Log.d("classSection","error");
                                                                                }
                                                                            }
                                                                        });



                                                                    }
                                                                }
                                                            });             //end os on click of done button while adding new section


                                                        }
                                                    });



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
            }
        });





/*
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        classQuery.whereEqualTo(ClassTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> classListRet, ParseException e) {

            }
        });
*/


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
