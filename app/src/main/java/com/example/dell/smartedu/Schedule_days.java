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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Schedule_days extends Fragment {

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
    TextView noschedule;
    ImageView noScheduleImage;
    String institution_name;
    String institution_code;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View schedule = inflater.inflate(R.layout.fragment_schedule_days, container, false);
        day= getArguments().getString("day");
        role=getArguments().getString("role");
        institution_name=getArguments().getString("institution_name");
        institution_code=getArguments().getString("institution_code");
        scheduleList=(ListView)schedule.findViewById(R.id.scheduleList);
        scheduleAdd=(Button)schedule.findViewById(R.id.addSchedule);
        noschedule=(TextView)schedule.findViewById(R.id.noSchedule);
        noScheduleImage= (ImageView)schedule.findViewById(R.id.noScheduleImage);

       Log.d("institution",institution_code + " " + institution_name);

        ParseQuery<ParseObject> scheduleQuery = ParseQuery.getQuery(ScheduleTable.TABLE_NAME);
        scheduleQuery.whereEqualTo(ScheduleTable.BY_USER_REF, ParseUser.getCurrentUser());
        scheduleQuery.whereEqualTo(ScheduleTable.DAY, day);
        scheduleQuery.whereEqualTo(ScheduleTable.BY_USER_ROLE, role);
        scheduleQuery.whereEqualTo(ScheduleTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
        scheduleQuery.addAscendingOrder(ScheduleTable.START_TIME);
        scheduleQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scheduleListRet, ParseException e) {
                if (e == null) {
                    int size=scheduleListRet.size();
                    Log.d("user", "Retrieved " + size + " schedules" + day);
                    if(size==0)
                    {
                        scheduleList.setVisibility(View.INVISIBLE);
                        noschedule.setText("No Schedule Added");
                    }else if (size>0) {
                        LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(0,0);
                        noScheduleImage.setLayoutParams(layoutParams);
                        noschedule.setLayoutParams(layoutParams);
                        noschedule.setVisibility(View.INVISIBLE);
                        noScheduleImage.setVisibility(View.INVISIBLE);
                        scheduleList.setVisibility(View.VISIBLE);
                        //Toast.makeText(getActivity(), scheduleListRet.toString(), Toast.LENGTH_LONG).show();
                        items = new String[scheduleListRet.size()];
                        for (int i = 0; i < scheduleListRet.size(); i++) {
                            ParseObject u = (ParseObject) scheduleListRet.get(i);
                            long start = TimeUnit.MILLISECONDS.toMinutes(u.getInt(ScheduleTable.START_TIME));
                            long end = TimeUnit.MILLISECONDS.toMinutes(u.getInt(ScheduleTable.END_TIME));
                            String st = String.valueOf(start / 60) + ":" + String.valueOf(start % 60);
                            String et = String.valueOf(end / 60) + ":" + String.valueOf(end % 60);
                            Toast.makeText(getActivity(), "done", Toast.LENGTH_LONG);

                            String info = u.getString(ScheduleTable.SCHEDULE_INFO);
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
                endtimedisplay.setText(details[1].trim());

                infodisplay.setText(details[2].trim());

                String[] sttimes = details[0].split(":");
                long time = TimeUnit.MINUTES.toMillis(Integer.parseInt(sttimes[0]) * 60 + Integer.parseInt(sttimes[1]));

                final String[] scheduleId = new String[1];
                ParseQuery<ParseObject> scheduleQuery = ParseQuery.getQuery(ScheduleTable.TABLE_NAME);
                scheduleQuery.whereEqualTo(ScheduleTable.START_TIME, time);
                scheduleQuery.whereEqualTo(ScheduleTable.BY_USER_REF, ParseUser.getCurrentUser());
                scheduleQuery.whereEqualTo(ScheduleTable.BY_USER_ROLE, role);
                scheduleQuery.whereEqualTo(ScheduleTable.DAY, day);
                scheduleQuery.whereEqualTo(ScheduleTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                scheduleQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> scheduleListRet, com.parse.ParseException e) {
                        if (e == null) {
                            ParseObject u = (ParseObject) scheduleListRet.get(0);
                            scheduleId[0] = u.getObjectId();
                           // Toast.makeText(getActivity(), "id of schedule selected is = " + scheduleId[0], Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("user", "Error: " + e.getMessage());
                        }
                    }
                });

                okButton = (Button) show_dialog.findViewById(R.id.doneButton);
                delButton = (Button) show_dialog.findViewById(R.id.delButton);
                editButton = (Button) show_dialog.findViewById(R.id.editButton);


                okButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        show_dialog.dismiss();

                    }
                });

                delButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "schedule item deleted ", Toast.LENGTH_LONG).show();

                        ParseObject.createWithoutData(ScheduleTable.TABLE_NAME, scheduleId[0]).deleteEventually();
                        show_dialog.dismiss();
                        reload();

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

                                ParseQuery<ParseObject> taskQuery = ParseQuery.getQuery(ScheduleTable.TABLE_NAME);
                                taskQuery.whereEqualTo(ScheduleTable.OBJECT_ID, scheduleId[0]);
                                taskQuery.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> objectRet, ParseException e) {

                                        if (e == null) {
                                            objectRet.get(0).put(ScheduleTable.SCHEDULE_INFO, ((EditText) dialog_in.findViewById(R.id.scheduleinfo)).getText().toString());
                                            objectRet.get(0).saveEventually();
                                            reload();

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






        endhours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedendhours = Integer.parseInt(endhours.getSelectedItem().toString());
                if (!(selectedendhours == Integer.parseInt(starthours.getSelectedItem().toString()))) {
                    String[] mins_end = new String[60];
                    for (int i = 0; i < 60; i++) {
                        mins_end[i] = String.valueOf(i);
                    }

                    ArrayAdapter<String> adapter_end = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_item, mins_end);
                    endmins.setAdapter(adapter_end);
                } else {
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(view, dialog);
                //dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void  add(View v, Dialog dialog)
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
            Toast.makeText(getActivity(), "selected time overlaps with other schedule in this or some other institute", Toast.LENGTH_LONG).show();
        }else
        {
            Log.d("while adding", institution_code + " " + institution_name);

            ParseObject schedule = new ParseObject(ScheduleTable.TABLE_NAME);
            schedule.put(ScheduleTable.BY_USER_REF, ParseUser.getCurrentUser());
            schedule.put(ScheduleTable.BY_USER_ROLE, role);
            schedule.put(ScheduleTable.DAY, day);
            schedule.put(ScheduleTable.SCHEDULE_INFO, info.getText().toString());
          schedule.put(ScheduleTable.START_TIME, startmilli);
           schedule.put(ScheduleTable.END_TIME, endmilli);
            schedule.put(ScheduleTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
            schedule.saveEventually();
            Toast.makeText(getActivity(), "schedule added ", Toast.LENGTH_LONG).show();
            dialog.dismiss();
           reload();
        }
    }

    protected void reload(){
        Intent reload=new Intent(getActivity(),Schedule.class);
        reload.putExtra("institution_code",institution_code);
        reload.putExtra("institution_name",institution_name);
        reload.putExtra("day", day);
        reload.putExtra("role", role);
        startActivity(reload);
    }

    public boolean checkTime(final long start, final long end)
    {   // Log.d("user", "checking and flag = " + String.valueOf(flag));
        final int[] check = new int[1];

        ParseQuery<ParseObject> taskQuery = ParseQuery.getQuery(ScheduleTable.TABLE_NAME);
        taskQuery.whereEqualTo(ScheduleTable.BY_USER_REF, ParseUser.getCurrentUser());
        taskQuery.whereEqualTo(ScheduleTable.DAY, day);
        taskQuery.whereEqualTo(ScheduleTable.BY_USER_ROLE, role);

        try {
            List<ParseObject> scheduleListRet = taskQuery.find();
            if (scheduleListRet.size() == 0) {
                Toast.makeText(getActivity(), "No schedule added", Toast.LENGTH_LONG).show();
            } else {
                check[0] = 0;
                Log.d("user", "checking new schedule timings with " + scheduleListRet.size() + " schedules");
                Toast.makeText(getActivity(), scheduleListRet.toString(), Toast.LENGTH_LONG).show();
                for (int i = 0; i < scheduleListRet.size(); i++) {

                    ParseObject u = (ParseObject) scheduleListRet.get(i);
                    long ret_start = u.getLong(ScheduleTable.START_TIME);
                    long ret_end = u.getLong(ScheduleTable.END_TIME);
                    if (start >= ret_start && start < ret_end) {
                        Log.d("user", "here");
                        // Schedule_days.this.flag = 1;

                        //flag=1;
                        check[0] = 1;
                        return true;
                        //break;
                    }
                    if (end > ret_start && end <= ret_end) {
                        //Schedule_days.this.flag = 1;
                        check[0] = 1;
                        return true;
                       // break;
                    }
                    if (start < ret_start && end > ret_end) {
                        Schedule_days.this.flag = 1;
                        check[0] = 1;
                        return true;
                       // break;
                    }
                }
                //Log.d("user", "checking and flag = " + String.valueOf(check[0]));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;


    }



}