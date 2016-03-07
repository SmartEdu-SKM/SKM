package com.example.dell.smartedu;

/**
 * Created by kamya batra on 9/02/2016.
 */


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;


class LoadingSyncList extends AsyncTask<String, Integer, Boolean> {

    private Activity context;
    ListView listView;
    RelativeLayout layout;
    ArrayAdapter adapter;

    LoadingSyncList(Activity context,RelativeLayout layout, ListView listView) {
        this.layout = layout;
        this.listView = listView;
        this.context=context;

        // this.adapter= adapter;

    }


    @Override
    protected Boolean doInBackground(String... params) {

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        lockScreenOrientation();
        layout.setVisibility(View.VISIBLE);
        if(listView!=null)
        listView.setVisibility(View.GONE);

    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        unlockScreenOrientation();

        layout.setVisibility(View.GONE);
        if(listView!=null)
        listView.setVisibility(View.VISIBLE);
        //listView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

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