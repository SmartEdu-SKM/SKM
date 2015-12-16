package com.example.dell.smartedu;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Schedule_days extends Fragment implements FragmentDrawer.FragmentDrawerListener{

    ListView scheduleList;
    Button scheduleAdd;
    EditText info;
    String day;
    String role;
    Spinner starthours;
    Spinner startmins;
    Spinner endhours;
    Spinner endmins;
    String [] items;
    ArrayList<String> scheduleLt;
    ArrayAdapter adapter=null;
    TextView starttimedisplay;
    TextView endtimedisplay;
    TextView infodisplay;
    Button okButton;
    Button delButton;
    Button editButton;
    EditText Desc;
    Button EditButton;
    int flag=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View schedule = inflater.inflate(R.layout.fragment_schedule_days, container, false);
        day= getArguments().getString("day");
        role=getArguments().getString("role");
        scheduleList=(ListView)schedule.findViewById(R.id.scheduleList);
        scheduleAdd=(Button)schedule.findViewById(R.id.addSchedule);
        ParseQuery<ParseObject> taskQuery = ParseQuery.getQuery("Schedule");
        taskQuery.whereEqualTo("addedBy", ParseUser.getCurrentUser());
        taskQuery.whereEqualTo("day", day);
        taskQuery.whereEqualTo("addedByRole", role);
        taskQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scheduleListRet, ParseException e) {
                if (e == null) {
                    //Log.d("user", "Retrieved " + userList.size() + " users");

                    if (scheduleListRet.size() == 0) {
                        Toast.makeText(getActivity(), "No schedule added", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("user", "Retrieved " + scheduleListRet.size() + " schedules");
                        Toast.makeText(getActivity(), scheduleListRet.toString(), Toast.LENGTH_LONG).show();
                        items = new String[scheduleListRet.size()];
                        for (int i = 0; i < scheduleListRet.size(); i++) {
                            ParseObject u = (ParseObject) scheduleListRet.get(i);
                            long start = TimeUnit.MILLISECONDS.toMinutes(u.getInt("startTime"));
                            long end = TimeUnit.MILLISECONDS.toMinutes(u.getInt("endTime"));
                            String st = String.valueOf(start / 60) + ":" + String.valueOf(start % 60);
                            String et = String.valueOf(end / 60) + ":" + String.valueOf(end % 60);
                            Toast.makeText(getActivity(), "done", Toast.LENGTH_LONG);

                            String info = u.getString("info");
                            String schedule = st + "\n" + et + "\n" + info;
                            items[i] = schedule;

                            // adapter.add(name);

                        }

                        scheduleLt = new ArrayList<>(Arrays.asList(items));
                        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, scheduleLt);
                        scheduleList.setAdapter(adapter);
                    }
                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });






        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, final long id) {


                // selected item
                String[] scheduleobject = ((TextView) view).getText().toString().split("\n");
                final String[] details = new String[3];
                int i = 0;

                for (String x : scheduleobject) {
                    details[i++] = x;
                }

                final Dialog show_dialog = new Dialog(getActivity());
                show_dialog.setContentView(R.layout.show_schedule_details);
                show_dialog.setTitle("Schedule Details");

                starttimedisplay = (TextView) show_dialog.findViewById(R.id.start_time);
                endtimedisplay = (TextView) show_dialog.findViewById(R.id.end_time);
                infodisplay = (TextView) show_dialog.findViewById(R.id.info);

                starttimedisplay.setText(details[0].trim());
                endtimedisplay.setText(details[1]);

                infodisplay.setText(details[2].trim());

                String[] sttimes = details[0].split(":");
                long time = TimeUnit.MINUTES.toMillis(Integer.parseInt(sttimes[0]) * 60 + Integer.parseInt(sttimes[1]));

                final String[] scheduleId = new String[1];
                ParseQuery<ParseObject> scheduleQuery = ParseQuery.getQuery("Schedule");
                scheduleQuery.whereEqualTo("startTime", time);
                scheduleQuery.whereEqualTo("addedBy", ParseUser.getCurrentUser());
                scheduleQuery.whereEqualTo("addedByRole", role);
                scheduleQuery.whereEqualTo("day", day);
                scheduleQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> scheduleListRet, com.parse.ParseException e) {
                        if (e == null) {
                            ParseObject u = (ParseObject) scheduleListRet.get(0);
                            scheduleId[0] = u.getObjectId();
                            Toast.makeText(getActivity(), "id of schedule selected is = " + scheduleId[0], Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("user", "Error: " + e.getMessage());
                        }
                    }
                });

                okButton = (Button) show_dialog.findViewById(R.id.okButton);
                delButton = (Button) show_dialog.findViewById(R.id.delButton);
                editButton = (Button) show_dialog.findViewById(R.id.editButton);


                okButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        show_dialog.dismiss();

                    }
                });

                delButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        ParseObject.createWithoutData("Schedule", scheduleId[0]).deleteEventually();
                        show_dialog.dismiss();

                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {


                        final Dialog dialog_in = new Dialog(getActivity());
                        dialog_in.setContentView(R.layout.activity_edit_schedule);
                        dialog_in.setTitle("Edit Details");


                        Desc = (EditText) dialog_in.findViewById(R.id.scheduleinfo);

                        EditButton = (Button) dialog_in.findViewById(R.id.editButton);

                        Desc.setText(details[2]);


                        EditButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                ParseQuery<ParseObject> taskQuery = ParseQuery.getQuery("Schedule");
                                taskQuery.whereEqualTo("objectId", scheduleId[0]);
                                taskQuery.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> objectRet, ParseException e) {

                                        if (e == null) {
                                            objectRet.get(0).put("info", ((EditText) dialog_in.findViewById(R.id.scheduleinfo)).getText().toString());
                                            objectRet.get(0).saveEventually();

                                        } else {
                                            Log.d("Post retrieval", "Error: " + e.getMessage());
                                        }
                                    }
                                });
                                dialog_in.dismiss();
                            }

                        });

                        dialog_in.show();
                        show_dialog.dismiss();
                    }
                });

                show_dialog.show();
                //recreate();


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
    {   flag=0;
        int start=Integer.parseInt(starthours.getSelectedItem().toString())*60 + Integer.parseInt(startmins.getSelectedItem().toString());
        int end=Integer.parseInt(endhours.getSelectedItem().toString())*60 + Integer.parseInt(endmins.getSelectedItem().toString());
        long startmilli= TimeUnit.MINUTES.toMillis(start);
        long endmilli=TimeUnit.MINUTES.toMillis(end);
        if(info.getText().toString().equals(""))
        {
            Toast.makeText(getActivity(), "add schedule info ", Toast.LENGTH_LONG).show();
        }else if(checkTime(startmilli,endmilli))
        {
            Toast.makeText(getActivity(), "selected time overlaps with other schedule ", Toast.LENGTH_LONG).show();
        }else
        {
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


    public boolean checkTime(final long start, final long end)
    {    Log.d("user", "checking and flag = " + String.valueOf(flag));


        ParseQuery<ParseObject> taskQuery = ParseQuery.getQuery("Schedule");
        taskQuery.whereEqualTo("addedBy", ParseUser.getCurrentUser());
        taskQuery.whereEqualTo("day", day);
        taskQuery.whereEqualTo("addedByRole", role);
        taskQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scheduleListRet, ParseException e) {
                if (e == null) {
                    if (scheduleListRet.size() == 0) {
                        Toast.makeText(getActivity(), "No schedule added", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("user", "checking new schedule timings with " + scheduleListRet.size() + " schedules");
                        Toast.makeText(getActivity(), scheduleListRet.toString(), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < scheduleListRet.size(); i++) {
                            ParseObject u = (ParseObject) scheduleListRet.get(i);
                            long ret_start = u.getLong("startTime");
                            long ret_end = u.getLong("endTime");
                            if (start >= ret_start && start < ret_end) {
                                Log.d("user", "here");
                                flag = 1;
                                break;
                            }
                            if (end > ret_start && end <= ret_end) {
                                flag = 1;
                                break;
                            }
                            if (start < ret_start && end > ret_end) {
                                flag = 1;
                                break;
                            }
                        }

                    }
                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });
        Log.d("user", "checking and flag = " + String.valueOf(flag));
        if(flag==1)
            return true;
        else
            return false;
    }




    @Override
    public void onDrawerItemSelected(View view, int position) {

    }
}