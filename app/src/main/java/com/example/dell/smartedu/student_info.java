package com.example.dell.smartedu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class student_info extends Fragment implements FragmentDrawer.FragmentDrawerListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View android = inflater.inflate(R.layout.fragment_student_info, container, false);
        String id= getArguments().getString("id");

        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
        studentQuery.whereEqualTo("objectId", id);
        studentQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    ParseObject u = (ParseObject) studentListRet.get(0);
                    ((TextView)android.findViewById(R.id.textView2)).setText(u.getString("name").toString());
                    ((TextView)android.findViewById(R.id.textView3)).setText(u.getNumber("age").toString());
                    //Toast.makeText(Students.this,"id of student selected is = " + id, Toast.LENGTH_LONG).show();

                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

        return android;
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }
}