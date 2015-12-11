package com.example.dell.smartedu;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class student_result extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View android = inflater.inflate(R.layout.fragment_student_result, container, false);
        String id= getArguments().getString("id");
        ((TextView)android.findViewById(R.id.textView)).setText(id);
        return android;
    }}