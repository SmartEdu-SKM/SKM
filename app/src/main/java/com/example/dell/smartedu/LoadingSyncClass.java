package com.example.dell.smartedu;

import android.os.AsyncTask;
import android.view.View;
import android.widget.RelativeLayout;


class LoadingSyncClass extends AsyncTask<String, Integer, Boolean> {

    RelativeLayout layoutOriginal;
    RelativeLayout layoutLoading;


    LoadingSyncClass(RelativeLayout layoutLoading, RelativeLayout layoutOriginal) {
        this.layoutLoading = layoutLoading;
        this.layoutOriginal= layoutOriginal;
    }


    @Override
    protected Boolean doInBackground(String... params) {


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        layoutOriginal.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);


    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        //loadingMore=

        layoutLoading.setVisibility(View.GONE);
        layoutOriginal.setVisibility(View.VISIBLE);

    }
}