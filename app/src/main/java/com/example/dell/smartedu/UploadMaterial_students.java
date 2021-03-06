package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UploadMaterial_students extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;

    Button doneButton;

    Button viewAllButton;
    ListView classList;
    TextView myDate;
    TextView editmyDate;
    TextView myDueDate;
    TextView myType;
    TextView mySubject;
    TextView myTopic;
    EditText subject;

    Calendar calendar;

    ImageView imageUpload;
    int Year;
    int Month;
    int Day;

    String uploadid;


    int flag=0;

    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;

    ListView uploadList;
    Notification_bar noti_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_material_students);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Uploads");
        Intent from_main = getIntent();
        //classGradeId=from_main.getStringExtra("id");
        classId= from_main.getStringExtra("classId");
        institution_code=from_main.getStringExtra("institution_code");
        institution_name=from_main.getStringExtra("institution_name");
        classGradeId= from_main.getStringExtra("classGradeId");
        studentId= from_main.getStringExtra("studentId");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), "Student",institution_name);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        uploadList = (ListView) findViewById(R.id.uploadList);
        layoutLoading= (RelativeLayout)findViewById(R.id.loadingPanel);
        context= this;


        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,"Student");
        drawerFragment.setDrawerListener(this);


        final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        //classQuery.whereEqualTo(ClassTable.CLASS_NAME, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME, classGradeId));
        classQuery.whereEqualTo(ClassTable.OBJECT_ID, classId);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> classListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    //   classRef[0] = classListRet.get(0);


                       /* final Dialog class_selection_dialog=new Dialog(UploadMaterial_students.this);
                        class_selection_dialog.setContentView(R.layout.singelistdisplay);
                        setDialogSize(class_selection_dialog);
                        classList=(ListView)class_selection_dialog.findViewById(R.id.examList); */
                        ArrayList<String> classLt = new ArrayList<String>();
                        //ArrayAdapter adapter = new ArrayAdapter(teacher_exams.this, android.R.layout.simple_list_item_1, studentLt);
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                        ArrayAdapter adapter = new ArrayAdapter(UploadMaterial_students.this, android.R.layout.simple_list_item_1, classLt) ;



                    /*
                        final HashMap<String,String> classMap=new HashMap<String,String>();
                        for(int x=0;x<classListRet.size();x++)
                        {
                            ParseObject u=classListRet.get(x);
                            String name=u.getString(ClassTable.SUBJECT);
                            classMap.put(name,u.getObjectId());
                            adapter.add(name);
                        }
                        classList.setAdapter(adapter);
                        class_selection_dialog.show();


                        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String item = ((TextView) view).getText().toString().trim();
                               classId =classMap.get(item);
                                class_selection_dialog.dismiss();
                                showUploads();
                            }
                        }); */
                    showUploads();



                } else {
                    Toast.makeText(UploadMaterial_students.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }

            }
        });


        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();


    }


    protected void showUploads()
    {

        final HashMap<String,String> uploadMap=new HashMap<String,String>();

        ParseQuery<ParseObject> uploadQuery = ParseQuery.getQuery(ImageUploadsTable.TABLE_NAME);
        uploadQuery.whereEqualTo(ImageUploadsTable.CLASS_REF, ParseObject.createWithoutData(ClassTable.TABLE_NAME, classId));

        uploadQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> uploadListRet, ParseException e) {
                if (e == null) {

                    ArrayList<String> uploadLt = new ArrayList<String>();
                    ArrayAdapter adapter = new ArrayAdapter(UploadMaterial_students.this, android.R.layout.simple_list_item_1, uploadLt){
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);

                            TextView textView = (TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
                            textView.setTextColor(Color.BLACK);

                            return view;
                        }
                    };



                    Log.d("user", "Retrieved " + uploadListRet.size() + " uploads");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    for (int i = 0; i < uploadListRet.size(); i++) {
                        ParseObject u = (ParseObject) uploadListRet.get(i);
                        //  if(u.getString("class").equals(id)) {

                        String name = u.getString(ImageUploadsTable.TOPIC);
                        name += "\n";
                        name += u.getString(ImageUploadsTable.SUBJECT);

                        if (u.getLong(ImageUploadsTable.DUE_DATE) != 0) {
                            name += "\n";

                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            final String dateString = formatter.format(new Date(u.getLong(ImageUploadsTable.DUE_DATE)));
                            name += dateString;
                        }

                        adapter.add(name);
                        uploadMap.put(name,u.getObjectId());

                    }


                    uploadList.setAdapter(adapter);
                    new LoadingSyncList(context,layoutLoading,uploadList).execute();


                    uploadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                            // selected item
                            String item = ((TextView) view).getText().toString().trim();
                            String upload_id = uploadMap.get(item);
                            Log.d("upload ", "retrieved id: " + upload_id);

                            String[] product = ((TextView) view).getText().toString().split("\n");
                            final String[] details = new String[3];
                            details[2] = "";
                            int i = 0;

                            for (String x : product) {
                                details[i++] = x;
                            }

                            final Dialog dialog = new Dialog(UploadMaterial_students.this);
                            dialog.setContentView(R.layout.show_upload_details_students);
                            dialog.setTitle("Upload Details");
                            setDialogSize(dialog);
                            myType = (TextView) dialog.findViewById(R.id.typeDesc);
                            mySubject = (TextView) dialog.findViewById(R.id.subject);
                            myDate = (TextView) dialog.findViewById(R.id.uploadDate);
                            myDueDate = (TextView) dialog.findViewById(R.id.dueDate);
                            imageUpload = (ImageView) dialog.findViewById(R.id.imageUpload);
                            myTopic = (TextView) dialog.findViewById(R.id.topic);

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

                            ParseQuery<ParseObject> uploadQuery = ParseQuery.getQuery(ImageUploadsTable.TABLE_NAME);
                            uploadQuery.whereEqualTo(ImageUploadsTable.OBJECT_ID, uploadMap.get(item));

                            uploadQuery.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> uploadListRet, com.parse.ParseException e) {
                                    if (e == null) {
                                        ParseObject u = (ParseObject) uploadListRet.get(0);

                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                        final String dateString = formatter.format(new Date(u.getLong(ImageUploadsTable.DATE_UPLOADED)));
                                        myDate.setText(dateString.trim());

                                        myType.setText(u.get(ImageUploadsTable.UPLOAD_TYPE).toString().trim());

                                        // if (u.get("imageContent") != null) {
                                        //ArrayList<ParseFile> pFileList = new ArrayList<ParseFile>();
                                        List<ParseFile> pFileList = (ArrayList<ParseFile>) u.get(ImageUploadsTable.UPLOAD_CONTENT);
                                        if (u.get(ImageUploadsTable.UPLOAD_CONTENT) != null) {
                                            if (!pFileList.isEmpty()) {
                                                flag = 1;
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

                                        uploadid = u.getObjectId();
                                        Log.d("user", "upload id: " + uploadid);

                                        viewAllButton = (Button) dialog.findViewById(R.id.viewAll);


                                        viewAllButton.setOnClickListener(new View.OnClickListener() {

                                            public void onClick(View v) {

                                                if (flag == 1) {
                                                    Intent to_upload_image = new Intent(UploadMaterial_students.this, UploadImage_students.class);
                                                    to_upload_image.putExtra("institution_name", institution_name);
                                                    to_upload_image.putExtra("institution_code", institution_code);
                                                    to_upload_image.putExtra("classGradeId", classGradeId);
                                                    to_upload_image.putExtra("classId", classId);
                                                    to_upload_image.putExtra("uploadId", uploadid);
                                                    to_upload_image.putExtra("role", role);
                                                    startActivity(to_upload_image);
                                                } else
                                                    Toast.makeText(UploadMaterial_students.this, "None Uploaded", Toast.LENGTH_LONG).show();

                                                dialog.dismiss();


                                            }
                                        });

                                        dialog.show();

                                        new LoadingSyncList(context,layoutLoading,uploadList).execute();

                                    } else {
                                        Log.d("user", "Error: " + e.getMessage());
                                    }
                                }
                            });


                        }


                    });

                    new LoadingSyncList(context,layoutLoading,uploadList).execute();
                } else {
                    Toast.makeText(UploadMaterial_students.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });



    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Intent to_uploads = new Intent(UploadMaterial_students.this, UploadMaterial.class);
        to_uploads.putExtra("institution_name",institution_name);
        to_uploads.putExtra("institution_code",institution_code);
        to_uploads.putExtra("id", classId);
        startActivity(to_uploads);
        finish();

    }

    @Override
    protected void onPostResume () {
        super.onPostResume();
        if (ParseUser.getCurrentUser() == null) {
            Intent nouser = new Intent(UploadMaterial_students.this, login.class);
            startActivity(nouser);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

            Intent tohome = new Intent(UploadMaterial_students.this,student_classes.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institution_name);
            tohome.putExtra("institution_code",institution_code);
          tohome.putExtra("classGradeId",classGradeId);
        tohome.putExtra("studentId",studentId);
        tohome.putExtra("for","upload");
            startActivity(tohome);
        }



}


