package com.example.dell.smartedu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Dell on 10/7/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    String role;
    public ImageAdapter(Context c,String role) {
        mContext = c;
        this.role=role;
    }

    public int getCount() {
        if(role.equals("Teacher")) {
            return mThumbIdsTeacher.length;
        }
        else if(role.equals("Parent"))  {
            return mThumbIdsParent.length;
        }
        else if(role.equals("Student")) {
            return mThumbIdsStudent.length;
        }
        return mThumbIdsTeacher.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        if(role.equals("Teacher")) {
            imageView.setImageResource(mThumbIdsTeacher[position]);
        }
        else if(role.equals("Parent"))  {
            imageView.setImageResource(mThumbIdsParent[position]);
        }
        else if(role.equals("Student")) {
            imageView.setImageResource(mThumbIdsStudent[position]);
        }

        imageView.setLayoutParams(new GridView.LayoutParams(360, 360)); //dimension in px
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIdsTeacher = {
            R.drawable.timetable_image, R.drawable.task_image,
            R.drawable.students, R.drawable.timetable_image,
            R.drawable.marks, R.drawable.uploads,
            R.drawable.messages
    };

    private Integer[] mThumbIdsParent = {
            R.drawable.attendance, R.drawable.task_image,
            R.drawable.messages, R.drawable.marks
    };

    private Integer[] mThumbIdsStudent = {
            R.drawable.attendance, R.drawable.task_image,
            R.drawable.messages, R.drawable.timetable_image,
            R.drawable.marks, R.drawable.uploads
    };
}
