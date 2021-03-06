package com.example.dell.smartedu;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UploadImage_students extends ListActivity {

    private Toolbar mToolbar;
    public static final int TAKE_PIC_REQUEST_CODE = 0;
    public static final int CHOOSE_PIC_REQUEST_CODE = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;


    Button doneButton;
    protected ImageView mPreviewImageView;
    ImageView viewImage;
    protected ListView lv;
    String uploadId;
    String classId;
    ImageLoaderAdapter adapter;
    String classGradeId;
    String institution_name;
    String institution_code;
    String role;
    private Uri mMediaUri;
    Activity context;
    RelativeLayout layoutLoading;

    public void queryImagesFromParse(){
        ParseQuery<ParseObject> imagesQuery = new ParseQuery<>(ImageUploadsTable.TABLE_NAME);
        imagesQuery.whereEqualTo(ImageUploadsTable.OBJECT_ID,uploadId);
        imagesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> images, ParseException e) {
                if(e == null){

                    if(images.size()!=0) {
                        ParseObject u = (ParseObject) images.get(0);

                        List<ParseFile> pFileList = (ArrayList<ParseFile>) u.get(ImageUploadsTable.UPLOAD_CONTENT);


                        // if(images.size()!=0) {
                        //   for (int i = 0; i < images.size(); i++) {
                        //     if (images.get(i).get("imageContent") != null) {
                        if (u.get(ImageUploadsTable.UPLOAD_CONTENT) != null) {
                            adapter = new ImageLoaderAdapter(UploadImage_students.this, pFileList);
                            //lv.setAdapter(adapter);
                            setListAdapter(adapter);
                            new LoadingSyncList(context, layoutLoading, lv).execute();

                            adapter.notifyDataSetChanged();
                        }
                        //       }
                        // }
                    }

                }else{
                    Toast.makeText(UploadImage_students.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image_students);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        layoutLoading= (RelativeLayout)findViewById(R.id.loadingPanel);

        /*
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Uploads"); */

        Intent from_upload_material_students = getIntent();
        context=this;

        role= from_upload_material_students.getStringExtra("role");
        classId=from_upload_material_students.getStringExtra("classId");
        uploadId=from_upload_material_students.getStringExtra("uploadId");
        classGradeId=from_upload_material_students.getStringExtra("classGradeId");
        institution_code=from_upload_material_students.getStringExtra("institution_code");
        institution_name=from_upload_material_students.getStringExtra("institution_name");

        Log.d("user", "Object Id: uploadImage" + uploadId);

        //lv = (ListView)findViewById(R.id.imageList);

        queryImagesFromParse();


        lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                onListItemClick(v, pos, id);
            }
        });



    }

    protected void onListItemClick(View v, int pos, long id) {
        ParseFile  itemValue    = (ParseFile) lv.getItemAtPosition(pos);

        final Dialog dialog = new Dialog(UploadImage_students.this);
        dialog.setContentView(R.layout.view_image);
        dialog.setTitle("Image");
        viewImage=(ImageView) dialog.findViewById(R.id.viewImage);
        dialog.findViewById(R.id.delButton).setVisibility(View.GONE);

        Picasso.with(this)
                .load(itemValue.getUrl()).into(viewImage);

        viewImage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                try {
                    saveImage(v);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    public void saveImage(View v)throws FileNotFoundException {
        v.buildDrawingCache();
        Bitmap bm=v.getDrawingCache();

        OutputStream fOut = null;

        Uri imageUri= getOutputMediaFileUri(2);

        try {
            fOut = new FileOutputStream(imageUri.getPath());

            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

        } catch (Exception e) {
            Toast.makeText(this, "Error occured in Save. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        }
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        getApplicationContext().sendBroadcast(scanIntent);
        Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_LONG).show();

    }

    //inner helper method
    private Uri getOutputMediaFileUri(int mediaTypeImage) {

        if(isExternalStorageAvailable()){
            //get the URI
            //get external storage dir
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "UPLOADIMAGES");
            //create subdirectore if it does not exist
            if(!mediaStorageDir.exists()){
                //create dir
                if(! mediaStorageDir.mkdirs()){

                    return null;
                }
            }
            //create a file name
            //create file
            File mediaFile = null;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if(mediaTypeImage == MEDIA_TYPE_IMAGE){
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
            }
            //return file uri
            Log.d("UPLOADIMAGE", "FILE: "+Uri.fromFile(mediaFile));

            return Uri.fromFile(mediaFile);
        }else {

            return null;
        }

    }
    //check if external storage is mounted. helper method
    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(UploadImage_students.this,UploadMaterial_students.class);
        i.putExtra("institution_name",institution_name);
        i.putExtra("institution_code",institution_code);
        i.putExtra("id", classGradeId);
        i.putExtra("role", role);
        i.putExtra("classId", classId);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == CHOOSE_PIC_REQUEST_CODE){
                if(data == null){
                    Toast.makeText(getApplicationContext(), "Image cannot be null!", Toast.LENGTH_LONG).show();
                }else{
                    mMediaUri = data.getData();
                    //set previews
                    mPreviewImageView.setImageURI(mMediaUri);
                }
            }else {

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
                //set previews

                mPreviewImageView.setImageURI(mMediaUri);

            }

        }else if(resultCode != RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Cancelled!", Toast.LENGTH_LONG).show();
        }
    }




}
