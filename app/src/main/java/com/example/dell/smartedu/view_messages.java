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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class view_messages extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button addExamButton;
    private FragmentDrawer drawerFragment;

    MyDBHandler dbHandler;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView messageList;
    Notification_bar noti_bar;
    TextView message;
    String role;



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
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);

        /*ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
        studentQuery.whereEqualTo("class",classname);
        studentQuery.whereEqualTo("teacher",ParseUser.getCurrentUser());*/

        messageList= (ListView) findViewById(R.id.messageList);


                    ParseQuery<ParseObject> messageQuery = ParseQuery.getQuery("Message");
                    messageQuery.whereEqualTo("to", ParseUser.getCurrentUser());
                    messageQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> messageListRet, ParseException e) {
                            if (e == null) {
                                if (messageListRet.size() != 0) {

                                    ArrayList<String> messageLt = new ArrayList<String>();
                                    //ArrayAdapter adapter = new ArrayAdapter(teacher_exams.this, android.R.layout.simple_list_item_1, studentLt);
                                    //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();
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
                                        ParseObject u = (ParseObject) messageListRet.get(i);
                                        //  if(u.getString("class").equals(id)) {
                                        String name = u.getString("message");
                                        //name += "\n";
                                        // name += u.getInt("age");

                                        adapter.add(name);
                                        // }

                                    }


                                    messageList.setAdapter(adapter);


                                    messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                            final String item = ((TextView) view).getText().toString().trim();

                                            ParseQuery<ParseObject> msgQuery = ParseQuery.getQuery("Message");
                                            msgQuery.whereEqualTo("message", item);
                                            msgQuery.whereEqualTo("to", ParseUser.getCurrentUser());
                                            msgQuery.findInBackground(new FindCallback<ParseObject>() {
                                                public void done(List<ParseObject> msgListRet, ParseException e) {
                                                    if (e == null) {
                                                        ParseObject u = msgListRet.get(0);
                                                        callDialog(view,item);


                                                    } else {
                                                        Log.d("user", "Error: " + e.getMessage());
                                                    }
                                                }
                                            });


                                        }
                                    });

                                }

                            } else {
                                Toast.makeText(view_messages.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });



        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();




    }

    public void callDialog(View view, String item){
        Dialog dialog = new Dialog(view_messages.this);
        dialog.setContentView(R.layout.view_message);
        dialog.setTitle("Message");

        message= (TextView) dialog.findViewById(R.id.message);
        message.setText(item);

        dialog.dismiss();

        dialog.show();
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
