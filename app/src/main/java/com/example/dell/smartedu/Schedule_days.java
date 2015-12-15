package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Schedule_days extends Fragment implements FragmentDrawer.FragmentDrawerListener{

    TextView Schedule_day;
    ListView scheduleList;
    Button scheduleAdd;
    EditText info;
    String day;
    String role;
    Spinner starthours;
    Spinner startmins;
    Spinner endhours;
    Spinner endmins;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View schedule = inflater.inflate(R.layout.fragment_schedule_days, container, false);
        day= getArguments().getString("day");
        role=getArguments().getString("role");
        Schedule_day= (TextView)schedule.findViewById(R.id.scheduleday);
        Schedule_day.setText(day);
        scheduleList=(ListView)schedule.findViewById(R.id.scheduleList);
        scheduleAdd=(Button)schedule.findViewById(R.id.addSchedule);
        ParseQuery<ParseObject> scheduleQuery = ParseQuery.getQuery("Schedule");
        scheduleQuery.whereEqualTo("addedBy", ParseUser.getCurrentUser());
        scheduleQuery.whereEqualTo("day", day);
        scheduleQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scheduleListRet, ParseException e) {
                if (e == null) {
                    Log.d("schedule", "Retrieved the schedule");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    ArrayList<String> scheduleLt = new ArrayList<String>();
                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, scheduleLt);
                    //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                    Log.d("user", "Retrieved " + scheduleListRet.size() + " schedules");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    if (scheduleListRet.size() == 0) {
                        Toast.makeText(getActivity(), "no Schedule added ", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < scheduleListRet.size(); i++) {
                            ParseObject u = (ParseObject) scheduleListRet.get(i);
                            //  if(u.getString("class").equals(id)) {
                            int startTime = u.getInt("startTime");
                            int endTime = u.getInt("endTime");
                            String info = u.getString("info");
                            String schedule= String.valueOf(startTime) + " " + String.valueOf(endTime) + " " + info;

                            //name += "\n";
                            // name += u.getInt("age");

                            adapter.add(schedule);
                            // }

                        }
                    }

                    scheduleList.setAdapter(adapter);


                   /* scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String item = ((TextView) view).getText().toString();

                                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                                        studentQuery.whereEqualTo("name", item);
                                        studentQuery.whereEqualTo("class", classRef[0]);
                                        studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                                if (e == null) {
                                                    ParseObject u = (ParseObject) studentListRet.get(0);
                                                    String id = u.getObjectId();
                                                    //Toast.makeText(Students.this,"id of student selected is = " + id, Toast.LENGTH_LONG).show();
                                                    Intent to_student_info = new Intent(getActivity(), StudentInfo.class);
                                                    to_student_info.putExtra("id", id);
                                                    startActivity(to_student_info);
                                                } else {
                                                    Log.d("user", "Error: " + e.getMessage());
                                                }
                                            }
                                        });


                                    }
                                });*/


                } else {
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });


                scheduleAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newSchedule(v);
                    }
                });

               return schedule;
    }


    public void newSchedule(final View view)
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.new_schedule);
        dialog.setTitle("Select Date");
        final Button addnew=(Button)dialog.findViewById(R.id.add);
        info=(EditText)dialog.findViewById(R.id.info);

        final String[] hours= new String[24];
        for(int i=0;i<24;i++)
        {
            hours[i]=String.valueOf(i);
        }
        final String[] mins=new String[60];
        for(int i=0;i<60;i++)
        {
            mins[i]=String.valueOf(i);
        }
        starthours = (Spinner)dialog.findViewById(R.id.start_hours);
        startmins = (Spinner)dialog.findViewById(R.id.start_mins);
        endhours = (Spinner)dialog.findViewById(R.id.end_hours);
        endmins = (Spinner)dialog.findViewById(R.id.end_mins);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, hours);
        starthours.setAdapter(adapter);
        adapter=new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, mins);
        startmins.setAdapter(adapter);
        starthours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedstarthours = Integer.parseInt(starthours.getSelectedItem().toString());
                String[] hours_end = new String[24 - selectedstarthours];
                int x = selectedstarthours;
                for (int i = 0; i < 24 - selectedstarthours; i++) {
                    hours_end[i] = String.valueOf(x);
                    x++;
                }
                ArrayAdapter<String> adapter_end = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, hours_end);
                endhours.setAdapter(adapter_end);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        startmins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedstartmins = Integer.parseInt(startmins.getSelectedItem().toString());
                String[] mins_end = new String[60 - selectedstartmins];
                int x = selectedstartmins + 1;
                for (int i = 0; i < 59 - selectedstartmins; i++) {
                    mins_end[i] = String.valueOf(x);
                    x++;
                }
                ArrayAdapter<String> adapter_end = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, mins_end);
                endmins.setAdapter(adapter_end);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(view);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void  add(View v)
    {
        if(info.getText().toString().equals(""))
        {
            Toast.makeText(getActivity(), "add schedule info ", Toast.LENGTH_LONG).show();
        }else
        {
            int start=Integer.parseInt(starthours.getSelectedItem().toString())*60 + Integer.parseInt(startmins.getSelectedItem().toString());
            int end=Integer.parseInt(endhours.getSelectedItem().toString())*60 + Integer.parseInt(endmins.getSelectedItem().toString());
            long startmilli= TimeUnit.MINUTES.toMillis(start);
            long endmilli=TimeUnit.MINUTES.toMillis(end);
            ParseObject schedule = new ParseObject("Schedule");
            schedule.put("addedBy", ParseUser.getCurrentUser());
            schedule.put("addedByRole", role);
            schedule.put("day", day);
            schedule.put("info", info.getText().toString());
          schedule.put("startTime", startmilli);
           schedule.put("endTime", endmilli);
            schedule.saveEventually();
            Toast.makeText(getActivity(), "schedule added ", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onDrawerItemSelected(View view, int position) {

    }
}