package com.example.dell.smartedu;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;

public class Notification_bar extends Fragment {

    public static TextView user;
    public static TextView role;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_notification_bar, container, false);
        user=(TextView)layout.findViewById(R.id.user);
        role=(TextView)layout.findViewById(R.id.role);
        return layout;
    }

    public void setTexts(String username,String userrole,String institution)
    {
        user.setText("Hi,"+username);
        role.setText("(as "+ userrole + " at " + institution +")");
    }
}
