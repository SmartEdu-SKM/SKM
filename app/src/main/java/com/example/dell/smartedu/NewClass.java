package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

/**
 * Created by Dell on 10/7/2015.
 */
public class NewClass extends BaseActivity {

    private Toolbar mToolbar;
    String classname;
   String classsection;


    MyDBHandler dbHandler;

    Notification_bar noti_bar;

    EditText classGradeName;
    EditText newSection;
    Button addClassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Class");

        Intent for_new_class = getIntent();
        role=for_new_class.getStringExtra("role");
        institution_code=for_new_class.getStringExtra("institution_code");
        institution_name=for_new_class.getStringExtra("institution_name");

        classGradeName = (EditText) findViewById(R.id.newClassGrade);
        newSection = (EditText) findViewById(R.id.newClassSection);

        addClassButton = (Button) findViewById(R.id.addClassButton);
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role, institution_name);


        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classname = classGradeName.getText().toString().trim();
               classsection = newSection.getText().toString().trim();


                if (classname.equals("") || (classsection.equals(""))) {
                    Toast.makeText(getApplicationContext(), "New Class details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    addClass(classname,classsection);
                    Toast.makeText(NewClass.this, "New Class Added", Toast.LENGTH_LONG).show();
                    Intent to_admin_classes=new Intent(NewClass.this,Admin_classes.class);
                    to_admin_classes.putExtra("institution_code",institution_code);
                    to_admin_classes.putExtra("institution_name",institution_name);
                    to_admin_classes.putExtra("role",role);
                    startActivity(to_admin_classes);
                }
            }
        });
    }



    protected void addClass(String classname,String classsection){
        ParseObject newClass=new ParseObject(ClassGradeTable.TABLE_NAME);
        newClass.put(ClassGradeTable.CLASS_GRADE,classname);
        newClass.put(ClassGradeTable.SECTION,classsection);
        newClass.put(ClassGradeTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
        newClass.saveEventually();

    }




    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(NewClass.this,login.class);
            startActivity(nouser);
        }
    }

}
