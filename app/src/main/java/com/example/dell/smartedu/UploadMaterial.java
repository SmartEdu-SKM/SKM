package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UploadMaterial extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button uploadButton;
    Button editButton;
    Button doneButton;

    TextView myDate;
    TextView editmyDate;
    TextView myDueDate;
    EditText subject;
    Date date1;
    CalendarView calendarView;
    Calendar calendar;
    ImageView cal;
    int Year;
    int Month;
    int Day;
    String classId;
    String typeSelected;
    String subjectDesc;
    String uploadId;
    Spinner type;
    int Yearcal;
    int Monthcal;
    int Daycal;

    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;

    ListView uploadList;
    Notification_bar noti_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_material);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Uploads");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher");
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
        Intent from_main = getIntent();
        classId=from_main.getStringExtra("id");
        uploadList = (ListView) findViewById(R.id.uploadList);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


        final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery("Class");
        classQuery.whereEqualTo("objectId", classId);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> classListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    classRef[0] = classListRet.get(0);


                    ParseQuery<ParseObject> uploadQuery = ParseQuery.getQuery("ImageUploads");
                    uploadQuery.whereEqualTo("class", classRef[0]);

                    uploadQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> uploadListRet, ParseException e) {
                            if (e == null) {

                                ArrayList<String> uploadLt = new ArrayList<String>();
                                ArrayAdapter adapter = new ArrayAdapter(UploadMaterial.this, android.R.layout.simple_list_item_1, uploadLt);


                                Log.d("user", "Retrieved " + uploadListRet.size() + " uploads");
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < uploadListRet.size(); i++) {
                                    ParseObject u = (ParseObject) uploadListRet.get(i);
                                    //  if(u.getString("class").equals(id)) {

                                    String name = u.getString("type");
                                    name += "\n";

                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                    final String dateString = formatter.format(new Date(u.getLong("dueDate")));
                                    name += dateString;

                                    adapter.add(name);

                                }


                                uploadList.setAdapter(adapter);


                                  /*  uploadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                            String[] item = ((TextView) view).getText().toString().split(". ");
                                            int itemvalue = Integer.parseInt(item[0]);

                                            final ParseObject[] studentRef = new ParseObject[1];
                                            ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                                            studentQuery.whereEqualTo("rollNumber", itemvalue);
                                            studentQuery.whereEqualTo("class", classRef[0]);
                                            studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                                public void done(List<ParseObject> studentListRet, ParseException e) {
                                                    if (e == null) {
                                                        Log.d("class", "Retrieved the class");
                                                        //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                                                        studentRef[0] = studentListRet.get(0);
                                                        information(studentRef[0], view);

                                                    } else {
                                                        Toast.makeText(AddAttendance_Students.this, "error", Toast.LENGTH_LONG).show();
                                                        Log.d("user", "Error: " + e.getMessage());
                                                    }
                                                }
                                            });


                                        }
                                    }); */

                            } else {
                                Toast.makeText(UploadMaterial.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });


                } else {
                    Toast.makeText(UploadMaterial.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }

            }
        });


        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();


    }


    public void uploadNew(final View view){

        final Dialog dialog_upload = new Dialog(UploadMaterial.this);
        dialog_upload.setContentView(R.layout.upload_material);
        dialog_upload.setTitle("Upload Material");


        myDate = (TextView) dialog_upload.findViewById(R.id.dateText);
        myDueDate= (TextView) dialog_upload.findViewById(R.id.deadline);
        subject=(EditText) dialog_upload.findViewById(R.id.subject);
        cal=(ImageView) dialog_upload.findViewById(R.id.calButton);

        final String[] values=new String[2];
            values[0]="Assignment";
            values[1]="Study Material";
        type = (Spinner)dialog_upload.findViewById(R.id.type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, values);
        type.setAdapter(adapter);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        calendar = Calendar.getInstance();
        //System.out.println("Current time =&gt; " + calendar.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final String string_current_date = df.format(calendar.getTime());
        myDate.setText(string_current_date);

        String[] uploaddate = myDate.getText().toString().split("/");

        final String[] datedetails_upload = new String[3];
        int j = 0;

        for (String x : uploaddate) {
            datedetails_upload[j++] = x;
        }
        Log.d("Post retrieval", datedetails_upload[0]);
       // Toast.makeText(getApplicationContext(), datedetails_upload[0], Toast.LENGTH_LONG).show();
        Day = Integer.parseInt(datedetails_upload[0]);
        Month = Integer.parseInt(datedetails_upload[1]);
        Year = Integer.parseInt(datedetails_upload[2]);

    String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);

    Toast.makeText(getApplicationContext(), "updated date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

    SimpleDateFormat f1 = new SimpleDateFormat("dd-MM-yyyy");
    Date d = null;
    try {
        d = f1.parse(string_date);
    } catch (java.text.ParseException e) {
        e.printStackTrace();
    }
    final long upload_date_milliseconds = d.getTime();


        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                open(view);
                myDueDate.setText(String.valueOf(Daycal) + "/" + String.valueOf(Monthcal) + "/" + String.valueOf(Yearcal));
            }
        });






        uploadButton= (Button) dialog_upload.findViewById(R.id.uploadButton);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                typeSelected=type.getSelectedItem().toString().trim();
                subjectDesc=subject.getText().toString().trim();

                if (typeSelected.equals("") || subjectDesc.equals("") ) {
                    Toast.makeText(getApplicationContext(), "Type or Subject details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    final ParseObject upload = new ParseObject("ImageUploads");

                    upload.put("createdBy", ParseUser.getCurrentUser());
                    upload.put("class", ParseObject.createWithoutData("Class", classId));
                    upload.put("type", typeSelected);
                    upload.put("subject", subjectDesc);
                    upload.put("uploadDate", upload_date_milliseconds);



                        String string_duedate = String.valueOf(Daycal) + "-" + String.valueOf(Monthcal) + "-" + String.valueOf(Yearcal);

                        Toast.makeText(getApplicationContext(), "updated duedate = " + Daycal + "/" + Monthcal + "/" + Yearcal, Toast.LENGTH_LONG).show();

                        SimpleDateFormat f2 = new SimpleDateFormat("dd-MM-yyyy");
                        Date d = null;
                        try {
                            d = f2.parse(string_duedate);
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                        long due_date_milliseconds = d.getTime();
                        upload.put("dueDate", due_date_milliseconds);


                    upload.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Success!
                                uploadId = upload.getObjectId();
                                Log.d("user", "Object Id: " + uploadId);

                                Intent to_upload_image = new Intent(UploadMaterial.this, UploadImage.class);
                                to_upload_image.putExtra("classId", classId);
                                to_upload_image.putExtra("uploadId", uploadId);
                                startActivity(to_upload_image);

                            } else {
                                // Failure!
                            }
                        }
                    });



                }

                dialog_upload.dismiss();

            }
        });

        dialog_upload.show();

    }








    public void open(View view)
    {

        final Dialog dialogcal = new Dialog(UploadMaterial.this);
        dialogcal.setContentView(R.layout.activity_calendar2);
        dialogcal.setTitle("Select Date");
        calendarView= (CalendarView)dialogcal.findViewById(R.id.calendar);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                date1=null;
                Yearcal = year;
                Monthcal = month+1;
                Daycal = dayOfMonth;
                date1 = new Date(Yearcal - 1900, Monthcal-1, Daycal);
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                myDueDate.setText(dateFormat.format(date1), TextView.BufferType.EDITABLE);
                Toast.makeText(getApplicationContext(), Daycal + "/" + Monthcal + "/" + Yearcal, Toast.LENGTH_LONG).show();
                dialogcal.dismiss();

            }
        });
        dialogcal.show();



    }


    @Override
    protected void onPostResume () {
        super.onPostResume();
        if (ParseUser.getCurrentUser() == null) {
            Intent nouser = new Intent(UploadMaterial.this, login.class);
            startActivity(nouser);
        }
    }


}


