package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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
    Button okButton;
    Button delButton;
    Button removeDueDate;
    Button addMoreButton;
    Button viewAllButton;
    Button upload;

    TextView myDate;
    TextView editmyDate;
    TextView myDueDate;
    TextView myType;
    TextView mySubject;
    TextView myTopic;
    EditText subject;
    EditText topic;
    Date date1;
    CalendarView calendarView;
    Calendar calendar;
    ImageButton cal;
    ImageView imageUpload;
    int Year;
    int Month;
    int Day;
    String classId;
    String typeSelected;
    String subjectDesc;
    String uploadId;
    String uploadid;
    String topicDesc;
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

        Intent from_main = getIntent();
        classId=from_main.getStringExtra("id");
        role=from_main.getStringExtra("role");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Uploads");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Teacher");
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        uploadList = (ListView) findViewById(R.id.uploadList);
        upload=(Button)findViewById(R.id.uploadButton);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadNew();
            }
        });
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,"Teacher");
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

                                    String name = u.getString("topic");
                                    name += "\n";
                                    name += u.getString("subject");

                                    if (u.getLong("dueDate") != 0) {
                                        name += "\n";

                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                        final String dateString = formatter.format(new Date(u.getLong("dueDate")));
                                        name += dateString;
                                    }

                                    adapter.add(name);

                                }


                                uploadList.setAdapter(adapter);


                                uploadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                        // selected item
                                        String[] product = ((TextView) view).getText().toString().split("\n");
                                        final String[] details = new String[3];
                                        details[2] = "";
                                        int i = 0;

                                        for (String x : product) {
                                            details[i++] = x;
                                        }

                                        final Dialog dialog = new Dialog(UploadMaterial.this);
                                        dialog.setContentView(R.layout.show_upload_details);
                                        dialog.setTitle("Upload Details");

                                        myType = (TextView) dialog.findViewById(R.id.typeDesc);
                                        mySubject = (TextView) dialog.findViewById(R.id.subject);
                                        myDate = (TextView) dialog.findViewById(R.id.uploadDate);
                                        myDueDate = (TextView) dialog.findViewById(R.id.dueDate);
                                        imageUpload = (ImageView) dialog.findViewById(R.id.imageUpload);
                                        myTopic = (TextView) dialog.findViewById(R.id.topic);
                                        okButton = (Button) dialog.findViewById(R.id.okButton);
                                        delButton = (Button) dialog.findViewById(R.id.delButton);
                                        viewAllButton = (Button) dialog.findViewById(R.id.viewAll);
                                        myTopic.setText(details[0].trim());
                                        mySubject.setText(details[1]);

                                        final long milliseconds;
                                        if (!details[2].equals("")) {
                                            myDueDate.setText(details[2].trim());

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
                                            milliseconds = d.getTime();
                                        } else {
                                            myDueDate.setText("Not Set");
                                            milliseconds = 0;
                                        }



                                        //Toast.makeText(Tasks.this, "date = " + d.toString() + "ms" + milliseconds, Toast.LENGTH_LONG).show();

                                        ParseQuery<ParseObject> uploadQuery = ParseQuery.getQuery("ImageUploads");
                                        uploadQuery.whereEqualTo("topic", details[0].trim());
                                        uploadQuery.whereEqualTo("subject", details[1].trim());
                                        uploadQuery.whereEqualTo("class", ParseObject.createWithoutData("Class", classId));
                                        uploadQuery.whereEqualTo("createdBy", ParseUser.getCurrentUser());
                                        uploadQuery.whereEqualTo("dueDate", milliseconds);
                                        uploadQuery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> uploadListRet, com.parse.ParseException e) {
                                                if (e == null) {
                                                    if (uploadListRet.size() != 0) {
                                                        ParseObject u = (ParseObject) uploadListRet.get(0);

                                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                                        final String dateString = formatter.format(new Date(u.getLong("uploadDate")));
                                                        myDate.setText(dateString.trim());

                                                        myType.setText(u.get("type").toString().trim());

                                                        // if (u.get("imageContent") != null) {
                                                        //ArrayList<ParseFile> pFileList = new ArrayList<ParseFile>();

                                                        List<ParseFile> pFileList = (ArrayList<ParseFile>) u.get("imageContent");

                                                        if (u.get("imageContent") != null) {
                                                            if (!pFileList.isEmpty()) {
                                                                ParseFile pFile = pFileList.get(0);
                                                                byte[] bitmapdata = new byte[0];  // here it throws error
                                                                try {
                                                                    bitmapdata = pFile.getData();
                                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                                                                    imageUpload.setImageBitmap(bitmap);
                                                                } catch (ParseException e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                                // Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                                                            }
                                                        }


                                                        //pFileList = u.getList("imageContent");
                                                        /*ParseFile imageFile = (ParseFile) u.get("imageContent");
                                                        imageFile.getDataInBackground(new GetDataCallback() {
                                                            @Override
                                                            public void done(byte[] data, ParseException e) {
                                                                if (e == null) {
                                                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                                    imageUpload.setImageBitmap(bmp);
                                                                } else {
                                                                    Log.d("test",
                                                                            "There was a problem downloading the data.");
                                                                }
                                                            }

                                                        }); */
                                                        //  }

                                                        uploadid = u.getObjectId();
                                                        Log.d("user", "upload id: " + uploadid);



                                                        viewAllButton.setOnClickListener(new View.OnClickListener() {

                                                            public void onClick(View v) {

                                                                Intent to_upload_image = new Intent(UploadMaterial.this, UploadImage.class);
                                                                to_upload_image.putExtra("classId", classId);
                                                                to_upload_image.putExtra("uploadId", uploadid);
                                                                //to_upload_image.putExtra("role", role);
                                                                startActivity(to_upload_image);


                                                            }
                                                        });

                                                        okButton.setOnClickListener(new View.OnClickListener() {

                                                            public void onClick(View v) {

                                                                dialog.dismiss();

                                                            }
                                                        });

                                                        dialog.show();


                                                        delButton.setOnClickListener(new View.OnClickListener() {
                                                            public void onClick(View v) {

                                                                ParseObject.createWithoutData("ImageUploads", uploadid).deleteEventually();


                                                                onRestart();


                                                                dialog.dismiss();

                                                            }
                                                        });
                                                        dialog.show();


                                                    }
                                                } else {
                                                    Log.d("user", "Error: " + e.getMessage());
                                                }
                                            }
                                        });

                                        //dialog.show();


                                    }


                                });


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


    public void uploadNew(){

        final Dialog dialog_upload = new Dialog(UploadMaterial.this);
        dialog_upload.setContentView(R.layout.upload_material);
        dialog_upload.setTitle("Upload Material");


        myDate = (TextView) dialog_upload.findViewById(R.id.date);
        myDueDate= (TextView) dialog_upload.findViewById(R.id.deadline);
        subject=(EditText) dialog_upload.findViewById(R.id.subject);
        cal=(ImageButton) dialog_upload.findViewById(R.id.calButton);
        removeDueDate=(Button) dialog_upload.findViewById(R.id.removeDueDate);
        topic=(EditText) dialog_upload.findViewById(R.id.topic);

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

                open();
                myDueDate.setText(String.valueOf(Daycal) + "/" + String.valueOf(Monthcal) + "/" + String.valueOf(Yearcal));
            }
        });


        removeDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myDueDate.setText("Deadline Date");
                Daycal=0;
                Monthcal=0;
                Yearcal=0;
            }
        });



        uploadButton= (Button) dialog_upload.findViewById(R.id.uploadButton);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                typeSelected=type.getSelectedItem().toString().trim();
                subjectDesc=subject.getText().toString().trim();
                topicDesc=topic.getText().toString().trim();

                if (typeSelected.equals("") || subjectDesc.equals("") ||topicDesc.equals("")) {
                    Toast.makeText(getApplicationContext(), "Type or Subject details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {

                    ParseQuery<ParseObject> uploadQuery = ParseQuery.getQuery("ImageUploads");
                    uploadQuery.whereEqualTo("type", typeSelected);
                    uploadQuery.whereEqualTo("subject", subjectDesc);
                    uploadQuery.whereEqualTo("topic", topicDesc);

                    uploadQuery.findInBackground(new FindCallback<ParseObject>() {
                                                     public void done(List<ParseObject> uploadListRet, ParseException e) {
                                                         if (e == null) {

                                                             if (uploadListRet.size() != 0) {
                                                                 Toast.makeText(getApplicationContext(), "Similar details exist. Add under same entry of Type, Subject and Topic!", Toast.LENGTH_LONG).show();
                                                             } else {

                                                                 final ParseObject upload = new ParseObject("ImageUploads");

                                                                 upload.put("createdBy", ParseUser.getCurrentUser());
                                                                 upload.put("class", ParseObject.createWithoutData("Class", classId));
                                                                 upload.put("type", typeSelected);
                                                                 upload.put("topic", topicDesc);
                                                                 upload.put("subject", subjectDesc);
                                                                 upload.put("uploadDate", upload_date_milliseconds);


                                                                 if (Daycal == 0 && Monthcal == 0 && Yearcal == 0)
                                                                     upload.put("dueDate", 0);

                                                                 else {
                                                                     String string_duedate = String.valueOf(Daycal) + "-" + String.valueOf(Monthcal) + "-" + String.valueOf(Yearcal);

                                                                     Toast.makeText(getApplicationContext(), "updated duedate = " + Daycal + "/" + Monthcal + "/" + Yearcal, Toast.LENGTH_LONG).show();

                                                                     SimpleDateFormat f2 = new SimpleDateFormat("dd-MM-yyyy");
                                                                     Date d = null;
                                                                     try {
                                                                         d = f2.parse(string_duedate);
                                                                     } catch (java.text.ParseException e1) {
                                                                         e1.printStackTrace();
                                                                     }
                                                                     long due_date_milliseconds = d.getTime();
                                                                     upload.put("dueDate", due_date_milliseconds);
                                                                 }


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
                                                         }else{
                                                             Log.d("Post Query", "Error in Query: " + e.getMessage());
                                                         }

                                                     }
                                                 });
                }



                        dialog_upload.dismiss();

                    }
                });

        dialog_upload.show();

    }








    public void open()
    {

        final Dialog dialogcal = new Dialog(UploadMaterial.this);
        dialogcal.setContentView(R.layout.activity_calendar2);
        dialogcal.setTitle("Select Date");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogcal.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        dialogcal.getWindow().setAttributes(lp);

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
    protected void onRestart() {
        super.onRestart();
        Intent to_uploads = new Intent(UploadMaterial.this, UploadMaterial.class);
        to_uploads.putExtra("id", classId);
        startActivity(to_uploads);
        finish();

    }



}


