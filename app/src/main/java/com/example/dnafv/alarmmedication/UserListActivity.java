package com.example.dnafv.alarmmedication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
//This class is used to
public class UserListActivity extends AppCompatActivity {

    //Declare Variable for the ListView being displayed in the Activity
    ListView mUserListView;

    ArrayList<UserProfileModel> mUserList;
    UserListAdapter mUserAdapter = null;

    ImageView userImageViewIcon;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        //This code below allows the user to navigate back to MainActivity using the Back Button
        //For this to work we need to set the Parent Activity as MainActivity in AndroidManifest File
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User Profile List");

    //Connect the Variable to the ListView in the activity_user_list xml file
        mUserListView = findViewById(R.id.userListView);

        mUserList = new ArrayList<>();
        mUserAdapter = new UserListAdapter(this, R.layout.user_row, mUserList);
        mUserListView.setAdapter(mUserAdapter);

        //Retrieve all data from the userProfile table using the SQLiteHelper Object and getData Method
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM userProfile");
        mUserList.clear();

        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String firstName = cursor.getString(1);
            String lastName = cursor.getString(2);
            String DOB = cursor.getString(3);
            String bloodGroup = cursor.getString(4);
            String contactNumber = cursor.getString(5);
            String emailAddress = cursor.getString(6);
            byte[]userImage = cursor.getBlob(7);

            //The retrieved data is then constructed using the constrictor from the UserProfileModel
            //and passed onto the user_row xml file where the relevant data is displayed
            //Note need to update constructor for this as we don't need the email address however
            //I get an error if I leave it out
            mUserList.add(new UserProfileModel(id, firstName, lastName, DOB, bloodGroup, contactNumber, emailAddress,userImage));
        }

        mUserAdapter.notifyDataSetChanged();
        if(mUserList.size() == 0){
            //If there are no records within the userProfile Table = ListView is empty
            Toast.makeText(this, "No records found...", Toast.LENGTH_SHORT).show();
        }

        //ON CLICK "LONG" - DISPLAYS UPDATE OR DELETE FOR:
        //USER PROFILE LIST
        //This is ON LONG ITEM CLICK for it to work!
        mUserListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //Create the Alert Dialog to display the Update & Delete Options for Records within the ListView

                //These are the Options within the Alert Dialog
                final CharSequence[] items = {"Update", "Delete", "Cancel"};

                //Create the AlertDialog using the Builder to be used within this Activity
                AlertDialog.Builder dialog = new AlertDialog.Builder(UserListActivity.this);

                dialog.setTitle("Choose an Action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //This is the Update Option
                        if(i == 0){
                            //Update
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM userProfile");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while(c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            //Display the Update Dialog
                            //https://www.youtube.com/playlist?list=PLs1bCj3TvmWlh7m1g82KDSV0IJIGFNLkV
                            //1:35:10
                            //This code displays the dialog update
                            showDialogUpdateUserProfile(UserListActivity.this, arrID.get(position));
                        }
                        //This is the Delete Option
                        if(i == 1) {
                            //Delete
                            //Sets up the Cursor object to use the getData Method (with SQL guery)
                            // to get a single record from the database based on record if
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM userProfile");
                            //Create a new Array which holds integer (userID)
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while(c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            //Use the below Method to delete the row of data based on the record id
                            // from the userProfile table
                            //Remeber to create the below Method - generate method for this class (below)
                            showDialogDeleteUserProfile(arrID.get(position));
                        }
                        //This is the Cancel Option
                        if(i == 3) {
                            //Cancel
                            //This code will close the Dialog Window
                            dialogInterface.dismiss();
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });

    }

    //DIALOG WINDOW - DELETE USER PROFILE
    //This Method constructs and displays the Dialog Window for Deleting the UserProfile.
    private void showDialogDeleteUserProfile(final Integer idUserRecord) {
        //Create the Alert Dialog that will be displayed when the user chooses to Delete the record
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(UserListActivity.this);
        //Alert Dialog Title
        dialogDelete.setTitle("Warning! Deleting User Profile.");
        //Alert Dialog Message
        dialogDelete.setMessage("Are you sure you want to Delete the User Profile?");

        //Set the Buttons:
        //Positive = Ok
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    //Delete the record using the deleteData Method from SQLiteHelper passing in the
                    //idUserRecord = id = PK
                    MainActivity.mSQLiteHelper.deleteData(idUserRecord);
                    //Display Message delete successful
                    Toast.makeText(UserListActivity.this, "User Profile Deleted!", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    Log.e("Error", e.getMessage());
                }
                //Call the Method used to update the userProfileList displayed in the activity_user_list
                updateUserProfileList();
            }
        });

        //Negative = Cancel
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //This code will close the Dialog Window
                dialogInterface.dismiss();
            }
        });

        //Display the DialogDelete Window
        dialogDelete.show();

    }

    //DIALOG WINDOW - UPDATE USER PROFILE
    //This Method constructs and displays the Dialog Window for Updating the UserProfile.
    private void showDialogUpdateUserProfile(Activity activity, final int position){
        //Create an instance of the Dialog - instantiate the dialog
        final Dialog dialog = new Dialog(activity);
        //Bind the dialog to the correct view - user_update_dialog xml file just created
        dialog.setContentView(R.layout.user_update_dialog);
        dialog.setTitle("Update User Profile");

        userImageViewIcon = dialog.findViewById(R.id.imageViewUserRecord);
        final EditText editFirstName = dialog.findViewById(R.id.editFirstName);
        final EditText editLastName = dialog.findViewById(R.id.editLastName);
        final EditText editDOB = dialog.findViewById(R.id.editDOB);
        final EditText editBloodGroup = dialog.findViewById(R.id.editBloodGroup);
        final EditText editContactNumber = dialog.findViewById(R.id.editContactNumber);
        final EditText editEmailAddress = dialog.findViewById(R.id.editEmailAddress);
        Button buttonUpdateUserProfile = dialog.findViewById(R.id.buttonUpdateUserProfile);

        //Fixing the Update Dialog Window so that it populates the User Profile textfields
        //with data from the userProfile table for the user to update - currently the Update Dialog
        //displays empty fields: https://www.youtube.com/watch?v=HSK4edgThP4
        //Retrieve all data from the userProfile table using the SQLiteHelper Object and getData Method
        //for the userProfile clicked on in the List
        //Update the sql query to: ("SELECT * FROM userProfile WHERE id = " + position)
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM userProfile WHERE id = " + position);
        mUserList.clear();

        //Update the below code to populate the textfields with the data retrieved from the
        //userProfile table for the selected record (based on userId) to each corresponding column
        while(cursor.moveToNext()){
            //userId used to retrieve the record
            int id = cursor.getInt(0);

            //firstName string retrieved from database
            String firstName = cursor.getString(1);
            //updating the firstName textfield with firstName saved in userProfile table and retrieved above
            editFirstName.setText(firstName); //Set the firstName to the Update UserProfile Dialog Window

            String lastName = cursor.getString(2);
            editLastName.setText(lastName);
            String DOB = cursor.getString(3);
            editDOB.setText(DOB);
            String bloodGroup = cursor.getString(4);
            editBloodGroup.setText(bloodGroup);
            String contactNumber = cursor.getString(5);
            editContactNumber.setText(contactNumber);
            String emailAddress = cursor.getString(6);
            editEmailAddress.setText(emailAddress);
            //Set the image saved to the SQLite database table userProfile
            byte[]userImage = cursor.getBlob(7);
            userImageViewIcon.setImageBitmap(BitmapFactory.decodeByteArray(userImage, 0, userImage.length));

            //The retrieved data is then constructed using the constrictor from the UserProfileModel
            //and passed onto the user_row xml file where the relevant data is displayed
            //Note need to update constructor for this as we don't need the email address however
            //I get an error if I leave it out
            mUserList.add(new UserProfileModel(id, firstName, lastName, DOB, bloodGroup, contactNumber, emailAddress,userImage));
        }



        //Create the dimensions of the Dialog Box
        //Dialog Width
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.95);
        //Dialog Height
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.75);
        //Create the Dialog window
        dialog.getWindow().setLayout(width,height);
        //Display the Dialog widow when method called
        dialog.show();

        //Within the Update User Profile Dialog window - to change the image - need to add click image view
        userImageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check to see if we still have External Storage Permissions:

                //Set External Storage Permission to allow user to access and select image from phone gallery
                //Add this code to AndroidManifest File: <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
                //This gives Runtime Permission for Android Devices 6.0 & above
                //Allows the MainActivity to Access the External Storage where code is stored
                ActivityCompat.requestPermissions(
                        UserListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        //BUTTON - UPDATE USER PROFILE
        //This Method codes the Button for Updating the UserProfile.
        buttonUpdateUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    MainActivity.mSQLiteHelper.updateData(
                            editFirstName.getText().toString().trim(),
                            editLastName.getText().toString().trim(),
                            editDOB.getText().toString().trim(),
                            editBloodGroup.getText().toString().trim(),
                            editContactNumber.getText().toString().trim(),
                            editEmailAddress.getText().toString().trim(),
                            MainActivity.imageViewToByte(userImageViewIcon),
                            position

                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "User Profile Updated!", Toast.LENGTH_SHORT).show();
                }
                catch(Exception error){
                    Log.e("Update Error!", error.getMessage());
                }
                //Create the below Method to update the User Profile List when data is updated!
                updateUserProfileList();
            }
        });

    }

    //CURSOR ADVISING USER PROFILE DATA HAS BEEN MODIFIED - UPDATE USER PROFILE
    //This Method retrieves all the data from the userProfile table,
    //passes it to the Cursor Object which
    //displays the data as rows (using the user_row xml file) in a list within the UserListActivity
    //This list is updated and displayed when:
    //user profile data is updated
    //user profile data is deleted
    private void updateUserProfileList() {
        //Retrieve all data from userProfile Table
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM userProfile");
        mUserList.clear();

        //Use the while loop to loop through each row in the table passing the data in an array to
        //the Cursor Object.
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String firstName = cursor.getString(1);
            String lastName = cursor.getString(2);
            String DOB = cursor.getString(3);
            String bloodGroup = cursor.getString(4);
            String contactNumber = cursor.getString(5);
            String emailAddress = cursor.getString(6);
            byte[] userProfileImage = cursor.getBlob(7);

            //This code constructs the data retrieved to be displayed in the list of users
            mUserList.add(new UserProfileModel(
                    id,
                    firstName,
                    lastName,
                    DOB,
                    bloodGroup,
                    contactNumber,
                    emailAddress,
                    userProfileImage));
        }
        //This code updates the list of any changes made to the data before it is displayed
        mUserAdapter.notifyDataSetChanged();
    }

    //IMAGE - PROCESSING TO SAVE TO userProfile TABLE
    //This Method processes the selected image to be inserted into the userProfile table
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
        if(requestCode == 888){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Gallery Intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 888);
            } else {
                Toast.makeText(this, "Don't have permission to Access File Location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 888 && resultCode == RESULT_OK){
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
                userImageViewIcon.setImageURI(resultUri);
                //Provide error if image cannot be set.
            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
