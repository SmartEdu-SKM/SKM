package com.example.dell.smartedu;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


class LoadingSyncClass extends AsyncTask<String, Integer, Boolean> {

    private Context context;
    RelativeLayout layoutOriginal;
    RelativeLayout layoutLoading;


    protected LoadingSyncClass(Context context,RelativeLayout layoutLoading, RelativeLayout layoutOriginal) {
        this.layoutLoading = layoutLoading;
        this.layoutOriginal= layoutOriginal;
        this.context=context;
    }


    @Override
    protected Boolean doInBackground(String... params) {

       String userName=params[0];
        String password= params[1];
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




        return null;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

        layoutOriginal.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);

        Log.d("async task ", "pre ");


    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        //loadingMore=

        layoutLoading.setVisibility(View.GONE);
        layoutOriginal.setVisibility(View.VISIBLE);

        Log.d("async task ", "post ");

    }
}