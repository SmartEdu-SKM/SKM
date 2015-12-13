package com.example.dell.smartedu;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static final int RESULT_LOAD_IMAGE = 1;
    private File selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (id) {
            case R.id.menu_name:

                Toast.makeText(getApplicationContext(), "To change username module", Toast.LENGTH_SHORT).show();

                break;

            case R.id.menu_password:
                Toast.makeText(getApplicationContext(), "To change password module", Toast.LENGTH_SHORT).show();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void selectPicture(View v)
    {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            CircleImageView imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.circleView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));


            // Locate the image in res > drawable-hdpi
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            // Convert it to byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();


            final ParseUser currentUser = ParseUser.getCurrentUser();
            // Create the ParseFile
            final ParseFile file = new ParseFile(image);
            // Upload the image into Parse Cloud

            // Create a New Class called "ImageUpload" in Parse
          /*  ParseObject imgupload = new ParseObject("UserPhoto");

            // Create a column named "ImageName" and set the string
            imgupload.put("createdBy", ParseUser.getCurrentUser());

            // Create a column named "ImageFile" and insert the image
            imgupload.put("imageFile", file);

            // Create the class and the columns
            imgupload.saveInBackground();
*/
            file.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if(e== null) {
                        Toast.makeText(BaseActivity.this, "Image Uploaded to Parse",Toast.LENGTH_SHORT).show();
                        currentUser.put("imageFile", file);
                    }

                    else{
                        Log.d("test",
                                "There was a problem uploading the data.");
                    }
                }
            });


            /*ParseQuery<ParseUser> query = ParseQuery.getQuery("User");

// Retrieve the object by id
            query.getInBackground(String.valueOf(ParseUser.getCurrentUser()), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject imgupload, com.parse.ParseException e) {
                    if (e == null) {
                        // Now let's update it with some new data. In this case, only cheatMode and score
                        // will get sent to the Parse Cloud. playerName hasn't changed.
                        imgupload.put("imageFile", file);
                        imgupload.saveInBackground();
                    }
                }
            });*/
            // Show a simple toast message
            Toast.makeText(BaseActivity.this, "Image Uploaded",
                    Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {

        if (position == 0) {
            /*Intent i = new Intent(MainActivity.this,CurrentOrder.class);
            startActivity(i);*/
        }

        if (position == 2) {
            //  Intent i = new Intent(MainActivity.this,HomeSlider.class);
            //startActivity(i);
        }

        if(position==8)
        {
            Intent i = new Intent(BaseActivity.this,ChooseRole.class);
            startActivity(i);
        }

        if(position==9)
        {
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            Intent i = new Intent(BaseActivity.this, login.class);
            startActivity(i);
        }

    }
}