package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;
import android.widget.Toast;

public class Calendar extends AppCompatActivity {

    CalendarView calendar;
    String role;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);

        calendar= (CalendarView) findViewById(R.id.calendar);



        Bundle fromrole= getIntent().getExtras();
        role = fromrole.getString("role");

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){

                Intent launchEvent;
                launchEvent= new Intent(getApplicationContext(),NewEvent_Teacher.class);
                launchEvent.putExtra("role",role);
                launchEvent.putExtra("year",year);
                launchEvent.putExtra("month",month);
                launchEvent.putExtra("dayOfMonth",dayOfMonth);
                startActivity(launchEvent);


                Toast.makeText(getApplicationContext(),dayOfMonth+"/"+month+"/"+year,Toast.LENGTH_LONG).show();


            }
        });

    }
}
