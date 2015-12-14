package com.example.dell.smartedu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class Schedule_days extends Fragment implements FragmentDrawer.FragmentDrawerListener{

    TextView Schedule_day;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View schedule = inflater.inflate(R.layout.fragment_schedule_days, container, false);
        String day= getArguments().getString("day");
        Schedule_day= (TextView)schedule.findViewById(R.id.scheduleday);
        Schedule_day.setText(day);

               return schedule;
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }
}