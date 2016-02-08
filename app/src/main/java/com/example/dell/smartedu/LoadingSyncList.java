package com.example.dell.smartedu;

/**
 * Created by kamya batra on 9/02/2016.
 */


import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;


class LoadingSyncList extends AsyncTask<String, Integer, Boolean> {

    ListView listView;
    RelativeLayout layout;
    ArrayAdapter adapter;

    LoadingSyncList(RelativeLayout layout, ListView listView) {
        this.layout = layout;
        this.listView = listView;
        // this.adapter= adapter;

    }


    @Override
    protected Boolean doInBackground(String... params) {

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        layout.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        //loadingMore=

        layout.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        //listView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

    }
}