package com.example.dell.smartedu;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

public class Admin_edit_subjects extends BaseActivity {
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    Notification_bar noti_bar;
    Button ok;
    Button deleteSelected;
    ListView editsubjects;
    String classGradeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_subjects);
        ok=(Button)findViewById(R.id.doneedit);
        deleteSelected=(Button)findViewById(R.id.deleteSelected);
        editsubjects=(ListView)findViewById(R.id.subjectlist);
        Intent from_classes=getIntent();
        classGradeId=from_classes.getStringExtra("classGradeId");
        role=from_classes.getStringExtra("role");
        institution_code=from_classes.getStringExtra("institution_code");
        institution_name=from_classes.getStringExtra("institution_name");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Subjects");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institution_name);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
        drawerFragment.setDrawerListener(this);

        ArrayList<String> list=new ArrayList<>();

        SpinnerModel data = new SpinnerModel(this,list);
        SpinnerModel data1 = new SpinnerModel(this,list);
        SpinnerModel data2 = new SpinnerModel(this,list);
        SpinnerModel data3 = new SpinnerModel(this,list);
        SpinnerModel data4 = new SpinnerModel(this,list);

        CustomSpinnerListAdapter d = new CustomSpinnerListAdapter(this, R.layout.list_item_spinner, new SpinnerModel[]{ data, data1, data2, data3, data4 },list);

        editsubjects.setAdapter(d);



    }


}


