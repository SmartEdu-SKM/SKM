package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class student_result extends Fragment {

    String studentId;
    String classId;
    Button addMarks;
    String institution_name;
    String institution_code;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View android = inflater.inflate(R.layout.fragment_student_result, container, false);
        studentId= getArguments().getString("id");
        classId= getArguments().getString("classId");
        institution_name= getArguments().getString("institution_name");
        institution_code= getArguments().getString("institution_code");
        addMarks= (Button) android.findViewById(R.id.addMarks);

        addMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*FragmentTransaction ft = getFragmentManager().beginTransaction();
                MyDialogFragment dialog = MyDialogFragment.newInstance();
                dialog.show(ft, "fragmentDialog");*/



                //Log.d("studId: "+studentId, "classId: " + classId);

                Intent i = new Intent(getActivity(), AddMarks.class);
                i.putExtra("institution_name",institution_name);
                i.putExtra("institution_code",institution_code);
                i.putExtra("studentId", studentId);
                i.putExtra("classId", classId);
                startActivity(i);


            }
        });


        return android;
    }}