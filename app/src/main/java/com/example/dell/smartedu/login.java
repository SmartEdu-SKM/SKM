package com.example.dell.smartedu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class login extends AppCompatActivity {

    EditText user;
    EditText pass;

    TextView noUser;

    Button login;

    RelativeLayout layoutLogin;
    RelativeLayout layoutLoading;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        layoutLoading=(RelativeLayout)findViewById(R.id.loadingPanel);
        layoutLogin=(RelativeLayout)findViewById(R.id.loginScreen);

        layoutLoading.setVisibility(View.GONE);

        user =(EditText)findViewById(R.id.userEmailInput);
        pass= (EditText)findViewById(R.id.userPasswordInput);

        noUser=(TextView) findViewById(R.id.noUser);

        login=(Button)findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //layoutLogin.setVisibility(View.GONE);

                layoutLoading.setVisibility(View.VISIBLE);

                //layoutLoading.setVisibility(View.VISIBLE);


                onClickLogin();
                //layoutLoading.setVisibility(View.GONE);

            }
        });


        noUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickNoUser();
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
      /*  if(ParseUser.getCurrentUser()!=null)
        {
            Intent nouser=new Intent(login.this,Role.class);
            startActivity(nouser);
        } */

        adminCheck();


    }

    public void onClickLogin() {

        // get The User name and Password



        String userName=user.getText().toString().trim();
        String password=pass.getText().toString().trim();

        new LoadingSyncClass(this,layoutLoading,layoutLogin).execute(userName,password);

/*
        ParseUser.logInInBackground(userName, password,
                new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // If user exist and authenticated, send user to Welcome.class

                            Toast.makeText(getApplicationContext(),
                                    "Successfully Logged in",
                                    Toast.LENGTH_LONG).show();

                            ParseQuery institution_admin_query = ParseQuery.getQuery(InstitutionTable.TABLE_NAME);
                            institution_admin_query.whereEqualTo(InstitutionTable.ADMIN_USER, ParseUser.getCurrentUser());
                            institution_admin_query.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> institutionListRet, ParseException e) {
                                    if (e == null) {

                                        if (institutionListRet.size() == 0) {
                                            Intent i = new Intent(login.this, Role.class);
                                            startActivity(i);
                                        } else {
                                            try {
                                                ParseObject insti = institutionListRet.get(0);
                                                Intent i = new Intent(login.this, admin_home.class);
                                                i.putExtra("institution_name", insti.fetchIfNeeded().getString(InstitutionTable.INSTITUTION_NAME));
                                                i.putExtra("institution_code", insti.fetchIfNeeded().getObjectId());
                                                i.putExtra("role", "Admin");
                                                startActivity(i);
                                            } catch (Exception admin_excep) {
                                                Toast.makeText(login.this, "ERROR FOR ADMIN", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } else {
                                        Log.d("institution", "error");
                                    }


                                }

                            });

                        } else {

                            Toast.makeText(
                                    getApplicationContext(), e.getMessage()
                                    ,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        //new LoadingSyncClass(layoutLoading,layoutLogin).execute();
        //layoutLoading.setVisibility(View.GONE); */


    }




    public void onClickNoUser()
    {
        Intent intent = new Intent(login.this , SignUp.class);
        startActivity(intent);
    }


    public void adminCheck(){
        if(ParseUser.getCurrentUser()!=null)
        {
            // layoutLoading.setVisibility(View.VISIBLE);
            // layoutSignUp.setVisibility(View.GONE);
           // new LoadingSyncClass(this,layoutLoading,layoutLogin);
            //findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

            ParseQuery institution_admin_query=ParseQuery.getQuery(InstitutionTable.TABLE_NAME);
            institution_admin_query.whereEqualTo(InstitutionTable.ADMIN_USER, ParseUser.getCurrentUser());
            institution_admin_query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> institutionListRet, ParseException e) {
                    if (e == null) {

                        if (institutionListRet.size() == 0) {
                            Intent i = new Intent(login.this, Role.class);
                            startActivity(i);
                        } else {
                            try {
                                ParseObject insti = institutionListRet.get(0);
                                Intent i = new Intent(login.this, admin_home.class);
                                i.putExtra("institution_name", insti.fetchIfNeeded().getString(InstitutionTable.INSTITUTION_NAME));
                                i.putExtra("institution_code", insti.fetchIfNeeded().getObjectId());
                                i.putExtra("role", "Admin");
                                startActivity(i);
                            } catch (Exception admin_excep) {
                                Toast.makeText(login.this, "ERROR FOR ADMIN", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Log.d("institution", "error");
                    }


                }

            });

            //layoutLoading.setVisibility(View.GONE);
            //findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event " + event.getRawX() + "," + event.getRawY() + " " + x + "," + y + " rect " + w.getLeft() + "," + w.getTop() + "," + w.getRight() + "," + w.getBottom() + " coords " + scrcoords[0] + "," + scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
