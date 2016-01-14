package com.example.dell.smartedu;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
<<<<<<< HEAD
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
=======
import android.widget.Button;
>>>>>>> 50aafe1c56528dbd62d27b41eb195dae799721ee
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class login extends AppCompatActivity {

    EditText user;
    EditText pass;
<<<<<<< HEAD
    TextView noUser;
=======
Button login;
    TextView notAUser;
>>>>>>> 50aafe1c56528dbd62d27b41eb195dae799721ee


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user =(EditText)findViewById(R.id.userEmailInput);
        pass= (EditText)findViewById(R.id.userPasswordInput);
<<<<<<< HEAD
        t=(TextView)findViewById(R.id.userEmailText);
        noUser=(TextView) findViewById(R.id.noUser);

        noUser.setOnClickListener(new View.OnClickListener() {
=======
       notAUser=(TextView)findViewById(R.id.noUser);
        login=(Button)findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogin();
            }
        });


        notAUser.setOnClickListener(new View.OnClickListener() {
>>>>>>> 50aafe1c56528dbd62d27b41eb195dae799721ee
            @Override
            public void onClick(View v) {
                onClickNoUser();
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()!=null)
        {
            Intent nouser=new Intent(login.this,Role.class);
            startActivity(nouser);
        }
    }

    public void onClickLogin() {
        // get The User name and Password
        String userName=user.getText().toString().trim();
        String password=pass.getText().toString().trim();

        ParseUser.logInInBackground(userName, password,
                new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // If user exist and authenticated, send user to Welcome.class

                            Toast.makeText(getApplicationContext(),
                                    "Successfully Logged in",
                                    Toast.LENGTH_LONG).show();
                            Intent i = new Intent(login.this, Role.class);
                            startActivity(i);

                        } else {
                           /* Toast.makeText(
                                    getApplicationContext(),
                                    "No such user exist, please signup",
                                    Toast.LENGTH_LONG).show();*/
                            Toast.makeText(
                                    getApplicationContext(),e.getMessage()
                                    ,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }




    public void onClickNoUser()
    {
        Intent intent = new Intent(login.this , SignUp.class);
        startActivity(intent);
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
