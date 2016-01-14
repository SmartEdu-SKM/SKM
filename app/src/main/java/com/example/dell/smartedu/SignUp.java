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
<<<<<<< HEAD
import android.view.inputmethod.InputMethodManager;
=======
import android.widget.ArrayAdapter;
import android.widget.Button;
>>>>>>> 50aafe1c56528dbd62d27b41eb195dae799721ee
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUp extends AppCompatActivity  {

    EditText UserNameSignup;
    EditText EmailSignup;
    EditText PasswordSignup;
    EditText ConfirmPasswordSignup;
<<<<<<< HEAD
    TextView already;
=======
    Button signUp;
    TextView alreadyUser;
>>>>>>> 50aafe1c56528dbd62d27b41eb195dae799721ee


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        UserNameSignup=(EditText)findViewById(R.id.userNameSignup);
        EmailSignup= (EditText)findViewById(R.id.emailSignup);
        PasswordSignup= (EditText)findViewById(R.id.passwordSignup);
        ConfirmPasswordSignup= (EditText)findViewById(R.id.confirmPasswordSignup);
<<<<<<< HEAD
        already= (TextView) findViewById(R.id.already);

        already.setOnClickListener(new View.OnClickListener() {
=======
        signUp=(Button)findViewById(R.id.signUpButton);
        alreadyUser=(TextView)findViewById(R.id.already);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });


        alreadyUser.setOnClickListener(new View.OnClickListener() {
>>>>>>> 50aafe1c56528dbd62d27b41eb195dae799721ee
            @Override
            public void onClick(View v) {
                onClickAlreadyUser();
            }
        });
<<<<<<< HEAD

=======
>>>>>>> 50aafe1c56528dbd62d27b41eb195dae799721ee
        if(ParseUser.getCurrentUser()!=null)
        {
            Intent i=new Intent(SignUp.this,Role.class);
            startActivity(i);
        }

    }


    public void addUser()
    {
        String userName = UserNameSignup.getText().toString();
        String password = PasswordSignup.getText().toString();
        String confirmPassword = ConfirmPasswordSignup.getText().toString();
        String email = EmailSignup.getText().toString();

        // check if any of the fields are vaccant
        if (userName.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("")) {
            Toast.makeText(getApplicationContext(), "Field Vaccant", Toast.LENGTH_LONG).show();
            return;
        }
        else if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
            return;
        } else {

            // Set up a new Parse user
            ParseUser user = new ParseUser();
            user.setUsername(userName);
            user.setPassword(password);
            user.setEmail(email);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {// Handle the response

                    if (e != null) {
                        // Show the error message
                        Toast.makeText(SignUp.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    } else {

                        Toast.makeText(SignUp.this, "Signup done",
                                Toast.LENGTH_LONG).show();
                        Intent i=new Intent(SignUp.this,login.class);
                        startActivity(i);
                    }

                }
            });

        }
    }


    public void onClickAlreadyUser()
    {
        Intent intent = new Intent(getApplicationContext(),login.class);
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
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if( ParseUser.getCurrentUser()!=null)
        {
            Intent i=new Intent(SignUp.this,Role.class);
            startActivity(i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
