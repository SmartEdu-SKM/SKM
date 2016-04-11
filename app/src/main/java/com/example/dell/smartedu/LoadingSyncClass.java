package com.example.dell.smartedu;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


class LoadingSyncClass extends AsyncTask<String, Integer, String> {

    private Activity context;
    RelativeLayout layoutOriginal;
    RelativeLayout layoutLoading;
    String _for;


    protected LoadingSyncClass(Activity context,RelativeLayout layoutLoading, RelativeLayout layoutOriginal, String _for) {
        this.layoutLoading = layoutLoading;
        this.layoutOriginal= layoutOriginal;
        this.context=context;
        this._for= _for;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

        lockScreenOrientation();
        layoutLoading.setVisibility(View.VISIBLE);
        if(layoutOriginal!=null)
            layoutOriginal.setVisibility(View.GONE);


        Log.d("async task ", "pre ");


    }

    @Override
    protected String doInBackground(String... params) {

        if (_for.equals("login"))
        {
            String userName = params[0];
        String password = params[1];
        ParseUser.logInInBackground(userName, password,
                new LogInCallback() {

                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user != null) {
                            // If user exist and authenticated, send user to Welcome.class

                            Toast.makeText(context,
                                    "Successfully Logged in",
                                    Toast.LENGTH_LONG).show();

                            ParseQuery institution_admin_query = ParseQuery.getQuery(InstitutionTable.TABLE_NAME);
                            institution_admin_query.whereEqualTo(InstitutionTable.ADMIN_USER, ParseUser.getCurrentUser());
                            institution_admin_query.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> institutionListRet, com.parse.ParseException e) {
                                    if (e == null) {

                                        if (institutionListRet.size() == 0) {
                                            Intent i = new Intent(context, Role.class);
                                            context.startActivity(i);
                                        } else {
                                            try {
                                                ParseObject insti = institutionListRet.get(0);
                                                Intent i = new Intent(context, admin_home.class);
                                                i.putExtra("institution_name", insti.fetchIfNeeded().getString(InstitutionTable.INSTITUTION_NAME));
                                                i.putExtra("institution_code", insti.fetchIfNeeded().getObjectId());
                                                i.putExtra("role", "Admin");
                                                context.startActivity(i);
                                            } catch (Exception admin_excep) {
                                                Toast.makeText(context, "ERROR FOR ADMIN", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } else {
                                        Log.d("institution", "error");
                                    }


                                }

                            });

                        } else {

                            Toast.makeText(
                                    context, e.getMessage()
                                    ,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
        else if(_for.equals("choose_role")){

            final String role = params[0];
            ParseQuery<ParseObject> roleQuery = ParseQuery.getQuery(RoleTable.TABLE_NAME);
            roleQuery.whereEqualTo(RoleTable.OF_USER_REF, ParseUser.getCurrentUser());
            roleQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> roleListRet, ParseException e) {
                    if (e == null) {
                        int flag = 0;
                        Log.d("role", "Retrieved " + roleListRet.size() + " roles");

                        for (int i = 0; i < roleListRet.size(); i++) {
                            flag=0;
                            ParseObject u = roleListRet.get(i);
                            String name = u.getString(RoleTable.ROLE).toString();
                            if (name.equals(role)) {
                                flag =1;
                                if(role.equals("Parent")) {
                                    Intent j = new Intent(context, parent_choose_child.class);
                                    j.putExtra("role", role);
                                    context.startActivity(j);
                                }else
                                {
                                    Intent j = new Intent(context, select_institution.class);
                                    j.putExtra("role", role);
                                    context.startActivity(j);
                                }

                                Toast.makeText(context, role + " Module", Toast.LENGTH_LONG)
                                        .show();
                                break;
                            }


                        }
                        if(flag==0) {
                            Toast.makeText(context, "Role not added", Toast.LENGTH_LONG)
                                    .show();
                        }



                    } else {
                        Log.d("user", "Error: " + e.getMessage());
                    }
                }
            });
        }
        /*
        else if(_for.equals("student_create_id")){

            String Year = params[0];
            String Month = params[1];
            String Day = params[2];
            final Dialog dialog = new Dialog(context);
            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setTitle("Select Date");
            dialog.setContentView(R.layout.activity_calendar2);

            context.setDialogSize(dialog);

            calendar= (CalendarView)dialog.findViewById(R.id.calendar);

            calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                    Year = year;
                    Month = month;
                    Day = dayOfMonth;
                    long[] milliseconds = new long[2];

                    checkDate(Day, Month+1, Year, milliseconds);
                    Log.d("date test ", milliseconds[0] + " selected:" + milliseconds[1]);
                    if(milliseconds[1] <= milliseconds[0]){
                        Toast.makeText(getApplicationContext(), "Choose Future Date!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        date1 = new Date(Year - 1900, Month, Day);
                        DATE.setText(df.format(date1), TextView.BufferType.EDITABLE);
                        Toast.makeText(getApplicationContext(), dayOfMonth + "/" + (Month + 1) + "/" + year, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                }
            });
            dialog.show();
            layoutLoading.setVisibility(View.GONE);


        } */

        return "All Done";
    }



    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //loadingMore=

        Log.d("async task ", "post ");
        unlockScreenOrientation();
        layoutLoading.setVisibility(View.GONE);
        if(layoutOriginal!=null)
            layoutOriginal.setVisibility(View.VISIBLE);

    }

    private void lockScreenOrientation() {

        int currentOrientation = context.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}