package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

/**
 * Created by Dell on 10/7/2015.
 */
public class view_messages extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView messageList;
    Notification_bar noti_bar;
    TextView message;
    String role;
    TextView messageFrom;
    Button delete;
    Button ok;
    TextView messagedate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Messages");
        Intent from_student = getIntent();
        role = from_student.getStringExtra("role");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);


        messageList= (ListView) findViewById(R.id.messageList);


                    ParseQuery<ParseObject> messageQuery = ParseQuery.getQuery("Message");
                    messageQuery.whereEqualTo("to", ParseUser.getCurrentUser());
                    messageQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> messageListRet, ParseException e) {
                            if (e == null) {
                                if (messageListRet.size() != 0) {

                                    ArrayList<String> messageLt = new ArrayList<String>();

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                            getApplicationContext(), android.R.layout.simple_list_item_1, messageLt) {

                                        @Override
                                        public View getView(int position, View convertView,
                                                            ViewGroup parent) {
                                            View view = super.getView(position, convertView, parent);

                                            TextView textView = (TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
                                            textView.setTextColor(Color.WHITE);

                                            return view;
                                        }
                                    };


                                    Log.d("user", "Retrieved " + messageListRet.size() + " messages");
                                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                    for (int i = 0; i < messageListRet.size(); i++) {
                                        ParseObject u = messageListRet.get(i);
                                        //  if(u.getString("class").equals(id)) {
                                        ParseUser senderuser = (ParseUser)u.get("from");

                                        String from= null;
                                        try {
                                            from = senderuser.fetchIfNeeded().getUsername();
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                        //name += "\n";
                                        // name += u.getInt("age");
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                                        Log.d("user", String.valueOf(u.getLong("sentAt")));
                                        Log.d("user", String.valueOf(new Date(u.getLong("sentAt"))));
                                        final String dateString = formatter.format(new Date(u.getLong("sentAt")));
                                        Log.d("user", dateString);
                                        String name=from + "\nat " + dateString;
                                        adapter.add(name);
                                        // }

                                    }


                                    messageList.setAdapter(adapter);


                                    messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                            String item = ((TextView) view).getText().toString();

                                            String[] itemValues = item.split("\nat ");

                                            final String[] details = new String[2];
                                            int j = 0;

                                            for (String x : itemValues) {
                                                details[j++] = x;
                                            }

                                            Log.d("user", "from " + details[0].trim() + " at " + details[1]);  //extracts Chit as Chi and query fails???
                                            final Dialog dialog = new Dialog(view_messages.this);
                                            dialog.setContentView(R.layout.messsage_info);
                                            dialog.setTitle("Message");
                                            message=(TextView)dialog.findViewById(R.id.message);
                                            messageFrom=(TextView)dialog.findViewById(R.id.message_from);
                                            messagedate=(TextView)dialog.findViewById(R.id.date);
                                            delete=(Button)dialog.findViewById(R.id.delButton);
                                            ok=(Button)dialog.findViewById(R.id.okButton);

                                            messageFrom.setText(details[0]);
                                            messagedate.setText(details[1]);

                                            ok.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                                            Log.d("user", details[1]);
                                            Date d = null;
                                            try {
                                                d = f.parse(details[1].trim());
                                            } catch (java.text.ParseException x) {
                                                x.printStackTrace();
                                            }
                                            final java.util.Calendar calendar= Calendar.getInstance();
                                            Log.d("user", String.valueOf(d));
                                            calendar.setTime(d);

                                            delete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

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

                                                                    final long milliseconds = calendar.getTimeInMillis();
                                                                    Log.d("user", String.valueOf(milliseconds));
                                                                    getMessageQuery.whereEqualTo("sentAt", milliseconds);
                                                                    getMessageQuery.findInBackground(new FindCallback<ParseObject>() {
                                                                        @Override
                                                                        public void done(List<ParseObject> objects, ParseException e) {
                                                                            if (e == null) {
                                                                                if (objects.size() != 0) {
                                                                                   objects.get(0).deleteEventually();
                                                                                    dialog.dismiss();
                                                                                    onRestart();
                                                                                } else {
                                                                                    Log.d("user", "query logic in deleting messages");
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


                                                }
                                            });


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
                                                            final long milliseconds = calendar.getTimeInMillis();
                                                            Log.d("user", String.valueOf(milliseconds));
                                                            getMessageQuery.whereEqualTo("sentAt", milliseconds);
                                                            getMessageQuery.findInBackground(new FindCallback<ParseObject>() {
                                                                @Override
                                                                public void done(List<ParseObject> objects, ParseException e) {
                                                                    if (e == null) {
                                                                        if (objects.size() != 0) {

                                                                            message.setText(objects.get(0).getString("message"));
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
                                    Toast.makeText(view_messages.this, "No messsages", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(view_messages.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Intent to_view_messages = new Intent(view_messages.this, view_messages.class);
        to_view_messages.putExtra("role", role);
        startActivity(to_view_messages);
        finish();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(view_messages.this,login.class);
            startActivity(nouser);
        }
    }


}
