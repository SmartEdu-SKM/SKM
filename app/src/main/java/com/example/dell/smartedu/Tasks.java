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
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    ListView taskListnew;
    String role;
    TextView myTitle;
    TextView myDesc;
    TextView myDate;
    Button okButton;
    Button delButton;
    Button editButton;
    Button EditButton;
    int Year;
    int Month;
    int Day;
    int Yearcal;
    int Monthcal;
    int Daycal;
    Date date1;
    CalendarView calendar;
    EditText Title;
    EditText Desc;
    EditText Date;
    String taskid;
    ArrayAdapter adapter=null;
    ArrayList<String> taskLt;
    String [] items;
    ImageButton cal;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
        taskListnew =(ListView) findViewById(R.id.taskList);

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
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role);

        ParseQuery<ParseObject> taskQuery = ParseQuery.getQuery("Task");
        taskQuery.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        taskQuery.whereEqualTo("addedByRole", role);
        taskQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> taskListRet, ParseException e) {
                if (e == null) {
                    //Log.d("user", "Retrieved " + userList.size() + " users");


                    Log.d("user", "Retrieved " + taskListRet.size() + " users");
                    Toast.makeText(getApplicationContext(), taskListRet.toString(), Toast.LENGTH_LONG)
                            .show();
                    items = new String[taskListRet.size()];
                    for (int i = 0; i < taskListRet.size(); i++) {
                        ParseObject u = (ParseObject) taskListRet.get(i);
                        String name = u.getString("TaskName");
                        name += "\n";
                        name += u.getString("TaskDescription");

                        name += "\n";
                        Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG);

                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        final String dateString = formatter.format(new Date(u.getLong("dueDate")));
                        name += dateString;
                        items[i] = name;

                        // adapter.add(name);

                    }

                    taskLt = new ArrayList<>(Arrays.asList(items));
                    adapter = new ArrayAdapter(Tasks.this, android.R.layout.simple_list_item_1, taskLt);
                    taskList.setAdapter(adapter);

                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });


        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, final long id) {


                // selected item
                String[] product = ((TextView) view).getText().toString().split("\n");
                final String[] details = new String[3];
                int i = 0;

                for (String x : product) {
                    details[i++] = x;
                }

                final Dialog dialog = new Dialog(Tasks.this);
                dialog.setContentView(R.layout.activity_show_details);
                dialog.setTitle("Task Details");

                myTitle = (TextView) dialog.findViewById(R.id.start_time);
                myDesc = (TextView) dialog.findViewById(R.id.end_time);
                myDate = (TextView) dialog.findViewById(R.id.dateText);

                myTitle.setText(details[0].trim());
                myDesc.setText(details[1]);

                myDate.setText(details[2].trim());

                String[] date = details[2].split("/");
                final String[] datedetails = new String[3];
                int j = 0;

                for (String x : date) {
                    datedetails[j++] = x;
                }

                Day = Integer.parseInt(datedetails[0]);
                Month = Integer.parseInt(datedetails[1]);
                Year = Integer.parseInt(datedetails[2]);

                String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);
                //Toast.makeText(Tasks.this, "date = " + string_date, Toast.LENGTH_LONG).show();
                SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                Date d = null;
                try {
                    d = f.parse(string_date);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                final long milliseconds = d.getTime();

                //Toast.makeText(Tasks.this, "date = " + d.toString() + "ms" + milliseconds, Toast.LENGTH_LONG).show();

                ParseQuery<ParseObject> taskQuery = ParseQuery.getQuery("Task");
                taskQuery.whereEqualTo("TaskName", details[0].trim());
                taskQuery.whereEqualTo("createdBy", ParseUser.getCurrentUser());
                taskQuery.whereEqualTo("addedByRole", role);
                taskQuery.whereEqualTo("dueDate", milliseconds);
                taskQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> taskListRet, com.parse.ParseException e) {
                        if (e == null) {
                            ParseObject u = (ParseObject) taskListRet.get(0);
                            taskid = u.getObjectId();
                            Toast.makeText(Tasks.this, "id of task selected is = " + taskid, Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("user", "Error: " + e.getMessage());
                        }
                    }
                });

                okButton = (Button) dialog.findViewById(R.id.okButton);
                delButton = (Button) dialog.findViewById(R.id.delButton);
                editButton = (Button) dialog.findViewById(R.id.editButton);


                okButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                delButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        ParseObject.createWithoutData("Task", taskid).deleteEventually();
                       /* ParseQuery<ParseObject> taskQuery = ParseQuery.getQuery("Task");
                        taskQuery.whereEqualTo("objectId", id);
                        taskQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> objectRet, ParseException e) {
                                if (e == null) {
                                    ParseObject object = (ParseObject) objectRet.get(0);

                                    object.deleteInBackground();

                                    //object.saveInBackground();
                                    Toast.makeText(getApplicationContext(), "Task Deleted", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "here", Toast.LENGTH_LONG).show();
                                }
                            }


                        }); */
                        //taskLt.remove(position);
                        //adapter.notifyDataSetChanged();
                        //taskList.setAdapter(adapter);

                        onRestart();


                        dialog.dismiss();

                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {


                        final Dialog dialog_in = new Dialog(Tasks.this);
                        dialog_in.setContentView(R.layout.activity_new_event__teacher);
                        dialog_in.setTitle("Edit Details");

                        Title = (EditText) dialog_in.findViewById(R.id.taskTitle);
                        Desc = (EditText) dialog_in.findViewById(R.id.scheduleinfo);
                        myDate = (TextView) dialog_in.findViewById(R.id.dateText);
                        EditButton = (Button) dialog_in.findViewById(R.id.editButton);
                        cal = (ImageButton) dialog_in.findViewById(R.id.test);
                        Title.setText(details[0]);
                        Desc.setText(details[1]);
                        myDate.setText(details[2]);
                        final int[] flag = {0};
                        cal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDate.setText(details[2]);
                                open(view);
                                flag[0] = 1;
                                myDate.setText(String.valueOf(Daycal) + "/" + String.valueOf(Monthcal) + "/" + String.valueOf(Yearcal));
                            }
                        });

                        EditButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                ParseQuery<ParseObject> taskQuery = ParseQuery.getQuery("Task");
                                taskQuery.whereEqualTo("objectId", taskid);
                                taskQuery.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> objectRet, ParseException e) {

                                        if (e == null) {
                                            objectRet.get(0).put("TaskName", ((EditText) dialog_in.findViewById(R.id.taskTitle)).getText().toString());
                                            objectRet.get(0).put("TaskDescription", ((EditText) dialog_in.findViewById(R.id.scheduleinfo)).getText().toString());
                                            if (flag[0] == 1) {
                                                Day = Daycal;
                                                Month = Monthcal;
                                                Year = Yearcal;
                                            } else {
                                                String[] datenew = myDate.getText().toString().split("/");

                                                final String[] datedetailsnew = new String[3];
                                                int j = 0;

                                                for (String x : datenew) {
                                                    datedetailsnew[j++] = x;
                                                }
                                                Log.d("Post retrieval", datedetailsnew[0]);
                                                Toast.makeText(getApplicationContext(), datedetailsnew[0], Toast.LENGTH_LONG);
                                                Day = Integer.parseInt(datedetailsnew[0]);
                                                Month = Integer.parseInt(datedetailsnew[1]);
                                                Year = Integer.parseInt(datedetailsnew[2]);
                                            }
                                            String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);
                                            Toast.makeText(getApplicationContext(), "updated date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

                                            SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                                            Date d = null;
                                            try {
                                                d = f.parse(string_date);
                                            } catch (java.text.ParseException e1) {
                                                e.printStackTrace();
                                            }
                                            long newmilliseconds = d.getTime();
                                            objectRet.get(0).put("dueDate", newmilliseconds);
                                            objectRet.get(0).saveEventually();

                                        } else {
                                            Log.d("Post retrieval", "Error: " + e.getMessage());
                                        }

                                    }
                                });
                                dialog_in.dismiss();
                                onRestart();
                            }

                        });

                        dialog_in.show();
                        // taskLt.set(position, Title.getText().toString() + "\n" + Desc.getText().toString() + "\n" + myDate.getText().toString());
                        //adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                dialog.show();
                //recreate();


            }
        });


        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Tasks.this, NewTask.class);
                i.putExtra("role", role);
                startActivity(i);
            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Intent to_tasks = new Intent(Tasks.this, Tasks.class);
        to_tasks.putExtra("role", role);
        startActivity(to_tasks);
        finish();

    }

    public void open(View view)
    {

        final Dialog dialogcal = new Dialog(Tasks.this);
        dialogcal.setContentView(R.layout.activity_calendar2);
        dialogcal.setTitle("Select Date");
        calendar= (CalendarView)dialogcal.findViewById(R.id.calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                date1=null;
                Yearcal = year;
                Monthcal = month+1;
                Daycal = dayOfMonth;
                date1 = new Date(Yearcal - 1900, Monthcal-1, Daycal);
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                myDate.setText(dateFormat.format(date1), TextView.BufferType.EDITABLE);
                Toast.makeText(getApplicationContext(), Daycal + "/" + Monthcal + "/" + Yearcal, Toast.LENGTH_LONG).show();
                dialogcal.dismiss();

            }
        });
        dialogcal.show();



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
