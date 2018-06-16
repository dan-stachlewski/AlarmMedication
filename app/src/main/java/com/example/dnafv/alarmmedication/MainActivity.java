package com.example.dnafv.alarmmedication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    //Declare Variables
    EditText mEditFirstName, mEditLastName, mEditBloodGroup, mEditContactNumber, mEditDOB, mEditEmailAddress;
    Button mButtonAddProfile, mButtonViewProfile;
    ImageView mImageProfileView;

    //Declare the mSQLiteHelper variable
    //It also gives the MainActivity access to the SQLiteHelper Class and the Methods within
    public static SQLiteHelper mSQLiteHelper;

    //Used to Access the External Storage:
    final int REQUEST_CODE_GALLERY = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("New User Profile");

        //Create the Database alarmMedication.db
        mSQLiteHelper = new SQLiteHelper(this, "alarmMedication.db", null, 2);

        //Create the userProfile Table
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS userProfile" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "firstName VARCHAR," +
                "lastName VARCHAR, " +
                "bloodGroup VARCHAR, " +
                "contactNumber VARCHAR, " +
                "DOB VARCHAR, " +
                "emailAddress VARCHAR, " +
                "image BLOB)");

        //Link declared variables to TextFields in MainActivity
        mEditFirstName = findViewById(R.id.editFirstName);
        mEditLastName = findViewById(R.id.editLastName);
        mEditBloodGroup = findViewById(R.id.editBloodGroup);
        mEditContactNumber = findViewById(R.id.editContactNumber);
        mEditDOB = findViewById(R.id.editDOB);
        mEditEmailAddress = findViewById(R.id.editEmailAddress);

        //Link declared variables to Buttons in MainActivity
        mButtonAddProfile = findViewById(R.id.buttonAddProfile);
        mButtonViewProfile = findViewById(R.id.buttonViewProfile);


        //Link declared variables to ImageView in MainActivity
        mImageProfileView = findViewById(R.id.imageProfileView);



        //Create onClick Method allowing us to select Image from clicking on mImageProfileView
        mImageProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set External Storage Permission to allow user to access and select image from phone gallery
                //Add this code to AndroidManifest File: <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
                //This gives Runtime Permission for Android Devices 6.0 & above
                //Allows the MainActivity to Access the External Storage where code is stored
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });


        //Add Record to SQLite Database when Save Profile Button Clicked:
        mButtonAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Code Later
                try{
                    mSQLiteHelper.insertData(
                            mEditFirstName.getText().toString().trim(),
                            mEditLastName.getText().toString().trim(),
                            mEditBloodGroup.getText().toString().trim(),
                            mEditContactNumber.getText().toString().trim(),
                            mEditDOB.getText().toString().trim(),
                            mEditEmailAddress.getText().toString().trim(),
                            imageViewToByte(mImageProfileView)); //Create method to deal with inserting images into the database
                    //Display message advising the data has been added to the database
                    Toast.makeText(MainActivity.this, "User Profile Added to Database!", Toast.LENGTH_SHORT).show();

                    //Clear the fields where the data was added - resetting the views
                    mEditFirstName.setText("");
                    mEditLastName.setText("");
                    mEditBloodGroup.setText("");
                    mEditContactNumber.setText("");
                    mEditDOB.setText("");
                    mEditEmailAddress.setText("");
                    mImageProfileView.setImageResource(R.drawable.adduser);//Clear the image chosen and replace with adduser image
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        //Show Profile/Record List when View Profile Button Clicked:
        mButtonViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start the UserListActivity when the View Profile Button is clicked
                startActivity(new Intent(MainActivity.this, UserListActivity.class));
            }
        });
    }

    //Custom Method to insert images into the userProfile table
    //Must make this public static so it can be accessed anywhere within the app
    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[]byteArray = stream.toByteArray();
        return byteArray;
    }

    //This code provides access to the Image Gallery where the user can choose Images to add to their profile.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Gallery Intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(this, "Don't have permission to Access File Location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON) //enables Image Guidelines
                        .setAspectRatio(1, 1) //Image will be Square
                            .start(this); //begins the Activity
        }
        //This sends a Cropped Version of the chosen image if the image chosen has been manipulated
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            //Set the Image chosen from the gallery as the imageView
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                mImageProfileView.setImageURI(resultUri);
            //Provide error if image cannot be set.
            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

/*
* https://www.youtube.com/playlist?list=PLs1bCj3TvmWlh7m1g82KDSV0IJIGFNLkV
*
* 1. Install Libraries for the project from build.gradle (Module: app)
* 2. Design the Main Screen to Input the Images & Text - changed colours!
* 3. Declare Variables and Connect the Variables to Elements in MainActivity - TextFields, Buttons, Images
* 4. Set up Buttons with onClickListener to perform tasks when Buttons Clicked
* 5. Set External Storage Permission to select Images from Gallery in AndroidManifest file <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
* 6. Add Cropping Activity in the ManifestFile with the following code:

        <!-- Image Cropping Activity -->
        <activity
            android:name="com.theartofdev.edmodo.cropper.CropImageActivity"
            android:theme="@style/Base.Theme.AppCompat" /> <!-- optional (needed if default theme has no action bar) -->

* 7. Create the SQLiteHelper class for creating the database, tables, etc...
* 7.1 Create a new Class SQLiteHelper with Methods to Query, Insert, Update & Delete Data.
* 8 Create a New Activity to display data in a userListView with Custom Adapter
* 8.1 Create a userListView in the activity_user_list xml file
* 8.2 Add Code to mButtonViewProfile
* 8.3 Design the Rows for the List View - Create Layout Resource File user_row
* 8.4 Create the Class Model - UserProfileModel
* 8.5 Declare Variables
* 8.6 Create Constructor & Getters/Setters
* 9. Create Custom Adapter for ListView
* 9.1 Extend the Adapter to use BaseAdapter
* 9.2 Generate the __________ Methods
* 10. Create the UserListActivity that will get the data from the userProfile table to display in the user_row.xml
* 11. Create the Update & Delete Alert Dialogs
* 12. Design the Update Dialog - create a New Resource File (xml)
* 13. Create the Update Dialog to be displayed
* 14. Add the Code for the Update Button in Alert Dialog
* 15. Add the Code for the Delete Button in Alert Dialog
* 16. Add Navigation 'Back to MainActivity' within Action Bar by adding the following code to the AndroidMaifest File
         <activity android:name=".UserListActivity"
            android:parentActivityName=".MainActivity"></activity>
* 17.
*
*
*
*
*
*
*
* We can test for things like if no record found = ListView empty...
*
* */
