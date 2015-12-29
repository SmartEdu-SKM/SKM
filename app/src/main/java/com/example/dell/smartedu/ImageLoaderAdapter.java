package com.example.dell.smartedu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.parse.ParseFile;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageLoaderAdapter extends ArrayAdapter<ParseFile>{
    private Context mContext;
    private List<ParseFile> mImages;

    public ImageLoaderAdapter(Context context, List<ParseFile> images){
        super(context, R.layout.single_row, images);

        mContext = context;
        mImages = images;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.single_row, null);
            holder = new ViewHolder();
            holder.homeImage = (ImageView) convertView
                    .findViewById(R.id.imageViewHome);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();

        }


        ParseFile file = mImages.get(position);
        //get the image



        Picasso.with(getContext().getApplicationContext()).load(file.getUrl()).noFade().into(holder.homeImage);



        return convertView;

    }


    public static class ViewHolder {
        ImageView homeImage;

    }
}
