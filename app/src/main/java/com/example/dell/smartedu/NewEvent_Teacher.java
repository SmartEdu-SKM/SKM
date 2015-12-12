package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewEvent_Teacher extends AppCompatActivity {

    String role;
    String myTitle;
    String myDesc;
    EditText eventTitle;
    EditText eventDescription;
    Button EventButton;
    TextView DATE;
    int Year;
    int Month;
    int Day;
    CalendarView calendar;
ImageButton test;
    Date date1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event__teacher);

        DATE= (TextView) findViewById(R.id.dateText);
        eventTitle = (EditText) findViewById(R.id.taskTitle);
        eventDescription = (EditText) findViewById(R.id.taskDescription);
        EventButton = (Button) findViewById(R.id.editButton);
        test=(ImageButton)findViewById(R.id.test);

        Bundle from= getIntent().getExtras();
        role = from.getString("role");

        String string_date = "12-December-2012";

        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
        Date d = null;
        try {
            d = f.parse(string_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final long milliseconds = d.getTime();

        final Date date2= new Date(Year-1900,Month,Day+1);

          EventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myTitle = eventTitle.getText().toString().trim();
                    myDesc = eventDescription.getText().toString();


                    if (myTitle.equals("") || myDesc.equals("") || ((DATE.getText().equals("Select Date")))) {
                        Toast.makeText(getApplicationContext(), "Event details cannot be empty!", Toast.LENGTH_LONG).show();
                    } else {
                        ParseObject calendar = new ParseObject("CalendarEvent");
                        calendar.put("createdBy", ParseUser.getCurrentUser());
                        calendar.put("addedByRole", role);
                        calendar.put("eventTitle", myTitle);
                        calendar.put("eventDescription", myDesc);
                        calendar.put("test", milliseconds);
                        calendar.saveInBackground();
                        Toast.makeText(getApplicationContext(), "Event details successfully stored", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(NewEvent_Teacher.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });


    }


    public void open(View view)
    {

        final Dialog dialog = new Dialog(NewEvent_Teacher.this);
        dialog.setContentView(R.layout.activity_calendar2);
        dialog.setTitle("Select Date");
        calendar= (CalendarView)dialog.findViewById(R.id.calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                Year = year;
                Month = month;
                Day = dayOfMonth;
                date1 = new Date(Year - 1900, Month, Day);
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                DATE.setText(dateFormat.format(date1), TextView.BufferType.EDITABLE);
                Toast.makeText(getApplicationContext(), dayOfMonth + "/" + Month + "/" + year, Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });
        dialog.show();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_event__teacher, menu);
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
}
