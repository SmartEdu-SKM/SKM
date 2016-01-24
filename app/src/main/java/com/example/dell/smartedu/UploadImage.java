package com.example.dell.smartedu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
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

public class UploadImage extends ListActivity {

    private Toolbar mToolbar;
    public static final int TAKE_PIC_REQUEST_CODE = 0;
    public static final int CHOOSE_PIC_REQUEST_CODE = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;

    protected Button mAddImageBtn;
    protected Button mUploadImageBtn;
    Button doneButton;
    //protected ImageView mPreviewImageView[];
    protected ImageView mPreviewImageView;

    ImageView viewImage;
    protected ListView lv;
    String uploadId;
    String classId;
    String role;
    ImageLoaderAdapter adapter;
    File mediaStorageDir;

    private Uri mMediaUri;

    public void queryImagesFromParse(){
        Log.d("user", "Object Id: uploadImage: " + uploadId);
        ParseQuery<ParseObject> imagesQuery = new ParseQuery<>("ImageUploads");
        imagesQuery.whereEqualTo("objectId",uploadId.trim());
        imagesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> images, ParseException e) {
                if(e == null){

                    if(images.size()!=0) {
                    ParseObject u = (ParseObject) images.get(0);

                    List<ParseFile> pFileList = (ArrayList<ParseFile>) u.get("imageContent");


                   // if(images.size()!=0) {
                     //   for (int i = 0; i < images.size(); i++) {
                       //     if (images.get(i).get("imageContent") != null) {
                        if (u.get("imageContent") != null) {
                            adapter = new ImageLoaderAdapter(UploadImage.this, pFileList);
                            //lv.setAdapter(adapter);
                            setListAdapter(adapter);

                            adapter.notifyDataSetChanged();
                        }
                     //       }
                       // }
                   }

                }else{
                    Toast.makeText(UploadImage.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

       mToolbar = (Toolbar) findViewById(R.id.toolbar);

        /*
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Uploads"); */

        Intent from_upload_material = getIntent();

        classId=from_upload_material.getStringExtra("classId");
        uploadId=from_upload_material.getStringExtra("uploadId");
        role= from_upload_material.getStringExtra("role");



        //lv = (ListView)findViewById(R.id.imageList);

       queryImagesFromParse();


        lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                onListItemClick(v,pos,id);
            }
        });




        //initialize
        mAddImageBtn = (Button)findViewById(R.id.addImageButton);
        mUploadImageBtn = (Button)findViewById(R.id.uploadImageButton);
        //mPreviewImageView  = new ImageView[3];

        mPreviewImageView= (ImageView)findViewById(R.id.previewImageView1);
       // mPreviewImageView[0] = (ImageView)findViewById(R.id.previewImageView1);
        //mPreviewImageView[1]= (ImageView) findViewById(R.id.previewImageView2);
        //mPreviewImageView[2]= (ImageView) findViewById(R.id.previewImageView3);

        //listen to add button click
        mAddImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadImage.this);
                builder.setTitle("Upload or Take a photo");
                builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //upload image
                        Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        choosePictureIntent.setType("image/*");
                        startActivityForResult(choosePictureIntent, CHOOSE_PIC_REQUEST_CODE);

                    }
                });
                builder.setNegativeButton("Take Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //take photo
                        //Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        if (mMediaUri == null) {
                            //display error
                            Toast.makeText(getApplicationContext(), "Sorry there was an error! Try again.", Toast.LENGTH_LONG).show();
                        } else {
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                            startActivityForResult(takePicture, TAKE_PIC_REQUEST_CODE);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //listen to upload button click
        mUploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    //String path= (String) mPreviewImageView[i].getTag();
                    //convert image to bytes for upload.
                    final byte[][] fileBytes = {FileHelper.getByteArrayFromFile(UploadImage.this, mMediaUri)};
                    if(fileBytes[0] == null){
                        //there was an error
                        Toast.makeText(getApplicationContext(), "There was an error. Try again!", Toast.LENGTH_LONG).show();
                    }else{

                        ParseQuery<ParseObject> imageQuery = ParseQuery.getQuery("ImageUploads");
                        imageQuery.whereEqualTo("objectId", uploadId);
                        imageQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(final List<ParseObject> objectRet, ParseException e) {

                                if (e == null) {

                                    try {
                                        fileBytes[0] = FileHelper.reduceImageForUpload(fileBytes[0]);
                                        String fileName = FileHelper.getFileName(UploadImage.this, mMediaUri, "image");
                                        final ParseFile file = new ParseFile(fileName, fileBytes[0]);

                                    objectRet.get(0).saveEventually(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {

                                                objectRet.get(0).add("imageContent", file);
                                                objectRet.get(0).saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        Toast.makeText(getApplicationContext(), "Success Uploading iMage!", Toast.LENGTH_LONG).show();

                                                       Intent to_upload_image = new Intent(UploadImage.this, UploadImage.class);
                                                        to_upload_image.putExtra("uploadId", uploadId);
                                                        startActivity(to_upload_image);
                                                        //finish();
                                                    }
                                                });

                                            } else {
                                                //there was an error
                                                //there was an error
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                    }catch (Exception file_error){
                                        Toast.makeText(getApplicationContext(), file_error.getMessage(), Toast.LENGTH_LONG).show();
                                       // onPostResume();
                                    }
                                }else {
                                        Log.d("Post retrieval", "Error: " + e.getMessage());
                                    }

                                }
                            });

                    }

                }catch (Exception e1){
                    Toast.makeText(getApplicationContext(), e1.getMessage(), Toast.LENGTH_LONG).show();
                   // onPostResume();
                }
            }
        });




    }

    protected void onListItemClick(View v, int pos, long id) {
        ParseFile  itemValue    = (ParseFile) lv.getItemAtPosition(pos);

        final Dialog dialog = new Dialog(UploadImage.this);
        dialog.setContentView(R.layout.view_image);
        dialog.setTitle("Image");
        viewImage=(ImageView) dialog.findViewById(R.id.viewImage);

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

    public void saveImage(View v) throws FileNotFoundException {
        //File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "UPLOADIMAGES");
       // Uri imageUri = Uri.fromFile(f);

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
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "UPLOADIMAGES");
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

                   /* if(mPreviewImageView[0].getDrawable() == null){
                        mPreviewImageView[0].setImageURI(mMediaUri);
                        //String path= mediaStorageDir.getAbsolutePath();
                        //mPreviewImageView[0].setTag(path);
                    }
                    else if(mPreviewImageView[1].getDrawable() == null){
                        mPreviewImageView[1].setImageURI(mMediaUri);
                        //String path= mediaStorageDir.getPath();
                        //mPreviewImageView[1].setTag(path);
                    }else {
                        mPreviewImageView[2].setImageURI(mMediaUri);
                        //String path= mediaStorageDir.getPath();
                        //mPreviewImageView[2].setTag(path);
                    } */
                }
            }else {

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
                //set previews
                mPreviewImageView.setImageURI(mMediaUri);

               /* if(mPreviewImageView[0].getDrawable() == null){
                    mPreviewImageView[0].setImageURI(mMediaUri);
                }
                else if(mPreviewImageView[1].getDrawable() == null){
                    mPreviewImageView[1].setImageURI(mMediaUri);
                }else {
                    mPreviewImageView[2].setImageURI(mMediaUri);
                } */

            }

        }else if(resultCode != RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Cancelled!", Toast.LENGTH_LONG).show();
        }
    }




}
