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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class message_read extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView messageList;
    Notification_bar noti_bar;
    String role;
    TextView message;
    TextView messageFrom;
    Button delete;
    Button ok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_read);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Messages");
        Intent from_home = getIntent();
        role=from_home.getStringExtra("role");
        messageList = (ListView) findViewById(R.id.studentList);
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(),role );
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);


        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);



        ParseQuery<ParseObject> messageQuery = ParseQuery.getQuery("Messages");
        messageQuery.whereEqualTo("to", ParseUser.getCurrentUser());
        messageQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> messageListRet, ParseException e) {
                if (e == null) {
                    if (messageListRet.size() != 0) {
                        Log.d("class", "Messages retrieved");
                        //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();


                                    ArrayList<String> studentLt = new ArrayList<String>();
                                    ArrayAdapter adapter = new ArrayAdapter(message_read.this, android.R.layout.simple_list_item_1, studentLt);
                                    //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                    Log.d("user", "Retrieved " + messageListRet.size() + " students");
                                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                    for (int i = 0; i < messageListRet.size(); i++) {
                                        ParseObject u = (ParseObject) messageListRet.get(i);

                                        String from = ((ParseUser)u.get("from")).getString("username");
                                        String time = u.getString("createdAt");
                                        String name = from + "\n at " + time;
                                        //name += "\n";
                                        // name += u.getInt("age");

                                        adapter.add(name);
                                        // }

                                    }


                        messageList.setAdapter(adapter);


                        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String item = ((TextView) view).getText().toString();

                                            String[] itemValues = item.split("\n at");

                                            final String[] details = new String[2];
                                            int j = 0;

                                            for (String x : itemValues) {
                                                details[j++] = x;
                                            }

                                            Log.d("user", "from " + details[0].trim() + " at " + details[1]);  //extracts Chit as Chi and query fails???
                                            final Dialog dialog = new Dialog(message_read.this);
                                            dialog.setContentView(R.layout.messsage_info);
                                            dialog.setTitle("Message");
                                            message=(TextView)dialog.findViewById(R.id.message);
                                            messageFrom=(TextView)dialog.findViewById(R.id.message_from);
                                            delete=(Button)dialog.findViewById(R.id.delButton);
                                            ok=(Button)dialog.findViewById(R.id.okButton);


                                            ParseQuery<ParseUser> user=ParseUser.getQuery();
                                            user.whereEqualTo("username",details[0]);
                                            user.findInBackground(new FindCallback<ParseUser>() {
                                                @Override
                                                public void done(List<ParseUser> objects, ParseException e) {
                                                    if (e == null) {
                                                        if (objects.size() != 0) {
                                                            ParseQuery<ParseObject> getMessageQuery = ParseQuery.getQuery("Message");
                                                            getMessageQuery.whereEqualTo("from", objects.get(0));
                                                            getMessageQuery.whereEqualTo("to", ParseUser.getCurrentUser());
                                                           /* SimpleDateFormat dateFormat=new SimpleDateFormat("");
                                                            Date to_date=*/
                                                            getMessageQuery.whereEqualTo("createdAt", Date.parse(details[1]));
                                                            getMessageQuery.findInBackground(new FindCallback<ParseObject>() {
                                                                @Override
                                                                public void done(List<ParseObject> objects, ParseException e) {
                                                                    if (e == null) {
                                                                        if (objects.size() != 0) {
                                                                            message.setText(objects.get(0).getString("message"));
                                                                            messageFrom.setText(objects.get(0).getString("from"));
                                                                        } else {
                                                                            Log.d("user", "query logic in getting messages");
                                                                        }
                                                                    } else {
                                                                        Log.d("user", "no message retrived");
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Log.d("user", "query logic error");
                                                        }
                                                    } else {
                                                        Log.d("user", "error in getting user");
                                                    }
                                                }
                                            });

dialog.show();

                                        }

                                    });




                    }else
                    {
                        Toast.makeText(message_read.this, "No messages", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(message_read.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });



        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(message_read.this,login.class);
            startActivity(nouser);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(message_read.this,MainActivity.class);
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
            Intent i = new Intent(message_read.this,ChooseRole.class);
            startActivity(i);
        }
        if(position==9)
        {
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            Intent i = new Intent(message_read.this, login.class);
            startActivity(i);
        }
    }
}
