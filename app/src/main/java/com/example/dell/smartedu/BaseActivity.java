package com.example.dell.smartedu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static final int RESULT_LOAD_IMAGE = 1;
    private File selectedFile;
    EditText Password;
    EditText ConfirmPassword;
    EditText UserName;
    Button changeButton;
    String password;
    String confirmPassword;
    String userName;
    String role="";
    String institution_name;
    String institution_code;
    String studentId;
    String classId;
    int densityX;
    int densityY;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //Parse.enableLocalDatastore(getApplicationContext());
        //Parse.initialize(getApplicationContext(), "5pPTGNabAK5TyJDfxKMuhzATUnMXS3GvjOS98IGD", "TRPqa2TRC5JmF2NUJLKvcdlH7j9c4saF4TODVwlG");


        //found width of Screen for Gridview
        WindowManager windowManager = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
        Display display = windowManager.getDefaultDisplay();
        densityX = display.getWidth();
        densityY= display.getHeight();
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
                final Dialog dialog1 = new Dialog(BaseActivity.this);
                dialog1.setContentView(R.layout.change_username);
                dialog1.setTitle("Change UserName");

                UserName = (EditText) dialog1.findViewById(R.id.username);
                changeButton = (Button) dialog1.findViewById(R.id.change_userButton);

                changeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        userName= UserName.getText().toString().trim();

                        if (userName.equals("")) {
                            Toast.makeText(getApplicationContext(), "User Name cannot be empty", Toast.LENGTH_LONG).show();

                        } else {
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            currentUser.setUsername(userName);
                            currentUser.saveInBackground();
                            Toast.makeText(getApplicationContext(), "User Name change successful", Toast.LENGTH_LONG).show();

                            dialog1.dismiss();
                        }
                    }

                });

                dialog1.show();
                break;



            case R.id.menu_password:
                Toast.makeText(getApplicationContext(), "To change password module", Toast.LENGTH_SHORT).show();
                final Dialog dialog = new Dialog(BaseActivity.this);
                dialog.setContentView(R.layout.change_password);
                dialog.setTitle("Change Password");

                Password = (EditText) dialog.findViewById(R.id.password);
                ConfirmPassword = (EditText) dialog.findViewById(R.id.confirm_password);
                changeButton = (Button) dialog.findViewById(R.id.change_passButton);

                changeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        password= Password.getText().toString().trim();
                        confirmPassword= ConfirmPassword.getText().toString().trim();
                        if (!password.equals(confirmPassword)) {
                            Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();

                        } else {

                            ParseUser currentUser = ParseUser.getCurrentUser();
                            currentUser.setPassword(password);
                            currentUser.saveInBackground();
                            Toast.makeText(getApplicationContext(), "Password change successful", Toast.LENGTH_LONG).show();

                            dialog.dismiss();
                        }
                    }

                });

                dialog.show();
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
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();

            /*
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
           // BitmapFactory.decodeFile(picturePath, options);

            // Locate the image in res > drawable-hdpi
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath,options);

            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;
            */
            final Bitmap[] bitmap = new Bitmap[1];
            final CircleImageView imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.circleView);
            imageView.post(new Runnable() {
                @Override
                public void run() {


                    bitmap[0] = decodeSampledBitmapFromFile(picturePath, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
                    imageView.setImageBitmap(bitmap[0]);

            // bitmap[0]= decodeSampledBitmapFromFile(picturePath,imageView.getWidth(),imageView.getHeight());





            // Convert it to byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bitmap[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();


            final ParseUser currentUser = ParseUser.getCurrentUser();
            // Create the ParseFile
            final ParseFile file = new ParseFile(image);


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
            });

        }


    }

    public static Bitmap decodeSampledBitmapFromFile(String picturePath,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);

        // Calculate inSampleSize
        options.inSampleSize =2;
        //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(picturePath, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        Log.d("memory ",
                "height "+height+ " reqd "+reqHeight+ " width "+width+" reqd "+reqWidth);
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {


        displayView(position);
    }

    private void displayView(int position) {

        if(this.role.equals("Teacher")){
            if (position == 0) { //dashboard
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("role", role);
                i.putExtra("institution_code", institution_code);
                i.putExtra("institution_name",institution_name);
                startActivity(i);
            }

            if (position == 1) { //tasks
                Intent task_intent = new Intent(BaseActivity.this, Tasks.class);
                task_intent.putExtra("institution_code", institution_code);
                task_intent.putExtra("institution_name", institution_name);
                task_intent.putExtra("role", role);
                startActivity(task_intent);
            }

            if (position == 2) { //attendance
                Intent attendance_intent = new Intent(BaseActivity.this, teacher_classes.class);
                attendance_intent.putExtra("institution_code",institution_code);
                attendance_intent.putExtra("institution_name",institution_name);
                attendance_intent.putExtra("for","attendance");
                attendance_intent.putExtra("role", role);
                startActivity(attendance_intent);
            }

            if (position == 3) { //schedule
                Intent schedule_intent = new Intent(BaseActivity.this, Schedule.class);
                schedule_intent.putExtra("institution_code",institution_code);
                schedule_intent.putExtra("institution_name",institution_name);
                schedule_intent.putExtra("role", role);
                startActivity(schedule_intent);
            }

            if (position == 4) { //assignments
                Intent upload_intent = new Intent(BaseActivity.this, teacher_classes.class);
                upload_intent.putExtra("institution_code",institution_code);
                upload_intent.putExtra("institution_name", institution_name);
                upload_intent.putExtra("role", role);
                upload_intent.putExtra("for", "upload");
                startActivity(upload_intent);

            }

            if (position == 5) { //grades
                Intent addmarks_intent = new Intent(BaseActivity.this, teacher_classes.class);
                addmarks_intent.putExtra("institution_code",institution_code);
                addmarks_intent.putExtra("institution_name", institution_name);
                addmarks_intent.putExtra("role", role);
                addmarks_intent.putExtra("for", "exam");
                startActivity(addmarks_intent);
            }

            if(position==7) //choose another role
            {
                Intent i = new Intent(BaseActivity.this,ChooseRole.class);
                startActivity(i);
            }

            if(position==8) //logout
            {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                Intent i = new Intent(BaseActivity.this, login.class);
                startActivity(i);
            }

        }

        else if(this.role.equals("Student")){
            if (position == 0) { //dashboard
                Intent i = new Intent(getApplicationContext(),student_home_activity.class);
                i.putExtra("institution_code",institution_code);
                i.putExtra("institution_name",institution_name);
                i.putExtra("role", role);
                i.putExtra("institution_code", institution_code);
                startActivity(i);
            }

            if (position == 1) { //tasks
                Intent task_intent = new Intent(BaseActivity.this, Tasks.class);
                task_intent.putExtra("institution_code",institution_code);
                task_intent.putExtra("institution_name",institution_name);
                task_intent.putExtra("role", role);
                startActivity(task_intent);
            }

            if (position == 2) { //attendance
                Intent atten_intent = new Intent(BaseActivity.this, view_attendance.class);

                atten_intent.putExtra("role", role);
                atten_intent.putExtra("studentId", studentId);
                atten_intent.putExtra("classId", classId);
                startActivity(atten_intent);
            }

            if (position == 3) { //schedule
                Intent schedule_intent = new Intent(BaseActivity.this, Schedule.class);
                schedule_intent.putExtra("institution_code",institution_code);
                schedule_intent.putExtra("institution_name",institution_name);
                schedule_intent.putExtra("role", role);
                startActivity(schedule_intent);
            }

            if (position == 4) { //assignments
                Intent exam_intent = new Intent(BaseActivity.this, UploadMaterial_students.class);
                exam_intent.putExtra("institution", institution_name);
                exam_intent.putExtra("institution_code", institution_code);
                exam_intent.putExtra("id", classId);
                startActivity(exam_intent);
            }

            if (position == 5) { //grades
                Intent exam_intent = new Intent(BaseActivity.this, student_exams.class);
                exam_intent.putExtra("institution", institution_name);
                exam_intent.putExtra("institution_code", institution_code);
                exam_intent.putExtra("role", role);
                exam_intent.putExtra("classId", classId);
                exam_intent.putExtra("studentId", studentId);
                startActivity(exam_intent);
            }

            if(position==7) //choose another role
            {
                Intent i = new Intent(BaseActivity.this,ChooseRole.class);
                startActivity(i);
            }

            if(position==8) //logout
            {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                Intent i = new Intent(BaseActivity.this, login.class);
                startActivity(i);
            }

        }

        else if(role.equals("Parent")){
            if (position == 0) { //dashboard
                Intent i = new Intent(getApplicationContext(),parent_home_activity.class);
                i.putExtra("institution_code",institution_code);
                i.putExtra("institution_name",institution_name);
                i.putExtra("role", role);
                startActivity(i);
            }

            if (position == 1) { //tasks
                Intent task_intent = new Intent(getApplicationContext(), Tasks.class);
                task_intent.putExtra("institution_code",institution_code);
                task_intent.putExtra("institution_name",institution_name);
                task_intent.putExtra("role", role);
                startActivity(task_intent);
            }

            if (position == 2) { //attendance
                Intent atten_intent = new Intent(getApplicationContext(), view_attendance.class);
                atten_intent.putExtra("role", "Parent");
                atten_intent.putExtra("studentId", studentId);
                atten_intent.putExtra("classId", classId);
                startActivity(atten_intent);
            }

            if (position == 3) { //grades

                Intent exam_intent = new Intent(getApplicationContext(), student_exams.class);
                exam_intent.putExtra("role", "Parent");
                exam_intent.putExtra("classId", classId);
                exam_intent.putExtra("studentId", studentId);
                startActivity(exam_intent);

            }

            if (position == 4) { //messages

                Intent message_intent = new Intent(getApplicationContext(), view_messages.class);
                message_intent.putExtra("role", "Parent");
                message_intent.putExtra("classId", classId);
                message_intent.putExtra("studentId", studentId);
                message_intent.putExtra("institution", institution_name);
                message_intent.putExtra("institution_code", institution_code);
                message_intent.putExtra("_for", "received");
                startActivity(message_intent);
            }

            if (position == 5) { //settings

            }

            if(position==6) //choose another role
            {
                Intent i = new Intent(getApplicationContext(),ChooseRole.class);
                startActivity(i);
            }

            if(position==7) //logout
            {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                Intent i = new Intent(BaseActivity.this, login.class);
                startActivity(i);
            }

        } else if(role.equals("Admin")){
            if (position == 0) { //dashboard
                Intent i = new Intent(getApplicationContext(),admin_home.class);
                i.putExtra("institution_code",institution_code);
                i.putExtra("institution_name",institution_name);
                i.putExtra("role", role);
                startActivity(i);
            }

            if (position == 1) { //tasks
                Intent task_intent = new Intent(getApplicationContext(), Tasks.class);
                task_intent.putExtra("institution_code",institution_code);
                task_intent.putExtra("institution_name",institution_name);
                task_intent.putExtra("role", role);
                startActivity(task_intent);
            }

            if (position == 2) { //teachers

            }

            if (position == 3) { //classes


            }

            if (position == 4) { //allotments

            }

            if (position == 5) { //settings

            }


            if(position==6) //logout
            {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                Intent i = new Intent(BaseActivity.this, login.class);
                startActivity(i);
            }

        } else{
            if(position==0)
            {

                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                Intent i = new Intent(BaseActivity.this, login.class);
                startActivity(i);
            }
        }


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ParseUser.getCurrentUser()==null)
        {
            Intent nouser=new Intent(getApplicationContext(),login.class);
            startActivity(nouser);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event " + event.getRawX() + "," + event.getRawY() + " " + x + "," + y + " rect " + w.getLeft() + "," + w.getTop() + "," + w.getRight() + "," + w.getBottom() + " coords " + scrcoords[0] + "," + scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    void checkDate(int Day, int Month, int Year, long milliseconds[]){

        Date d;
        java.util.Calendar calendar= java.util.Calendar.getInstance();
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
        String date= format.format(new Date(calendar.getTimeInMillis()));
        d=null;
        try {
            d=format.parse(date);
        } catch (java.text.ParseException e1) {
            e1.printStackTrace();
        }
        milliseconds[0]= d.getTime();

        String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);

        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        d = null;
        try {
            d = f.parse(string_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        milliseconds[1] = d.getTime();

        Log.d("date test base", milliseconds[0] + " selected:" + milliseconds[1]);


    }

    void setDialogSize(Dialog dialogcal){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogcal.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        dialogcal.getWindow().setAttributes(lp);

    }
}