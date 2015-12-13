package com.example.dell.smartedu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Dell on 9/10/2015.
 */
public class CustomListAdapter extends ArrayAdapter<Task> {

    public CustomListAdapter(Context context, List<Task> tasks) {
        super(context, R.layout.custom_task_row, tasks);
    }
/*

    public static String[] objIdOrder = new String[100];
    public static int[] orderFlag = new int[100];
        public CustomParseOrdersListAdapter(Context context, List<ParseObject> order) {
        super(context, R.layout.custom_orders_shop, order);
    }

    @Override
     public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.custom_orders_shop, parent, false);

        ParseObject myOrder = (ParseObject) getItem(position);
        objIdOrder[position] = myOrder.getObjectId();
        TextView orderId = (TextView) customView.findViewById(R.id.orderId);
        TextView rangeString = (TextView) customView.findViewById(R.id.rangeString);
        TextView numOfCopies = (TextView) customView.findViewById(R.id.numOfCopies);
        TextView category = (TextView) customView.findViewById(R.id.category);
        TextView listingName = (TextView) customView.findViewById(R.id.listingName);
        TextView listingText = (TextView) customView.findViewById(R.id.listingText);
        LinearLayout pagesView = (LinearLayout) customView.findViewById(R.id.pagesView);
        LinearLayout listingView = (LinearLayout) customView.findViewById(R.id.listingView);

        orderId.setText(myOrder.getObjectId());//int to String
        rangeString.setText(myOrder.getString("rangeString"));
        category.setText(myOrder.getString("category"));
        listingName.setText(myOrder.getString("listingTitle"));
        Integer i =  myOrder.getInt("numOfCopies");
        numOfCopies.setText(i.toString());
        if(category.getText().equals("Xerox")) orderFlag[position] = 0;
        return customView;
    }
*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.custom_task_row, parent, false);

        Task myTask = (Task)getItem(position);

        TextView taskTitle = (TextView) customView.findViewById(R.id.taskTitle);
        TextView taskDescription = (TextView) customView.findViewById(R.id.taskDesc);

        taskTitle.setText(myTask.getTitle());
        taskDescription.setText(myTask.getDescription());

        return customView;
    }
}
