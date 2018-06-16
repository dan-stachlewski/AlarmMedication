package com.example.dnafv.alarmmedication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
//This class is used to set up and assign the data to the user list rows in activity_user_list xml file
//Need to extend the Class allowing access to BaseAdapter
//Implement the getCount, getItem, getItemId, getView Methods
//Implement Constructor including super
public class UserListAdapter extends BaseAdapter {

    private Context context;
    private int userListLayout;
    ArrayList<UserProfileModel> userList;

    //Create the Constructor using: right-click>Generate>Constructor>Choose Variables to use
    public UserListAdapter(Context context, int userListLayout, ArrayList<UserProfileModel> userList) {
        this.context = context;
        this.userListLayout = userListLayout;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Setting up the Display of the data for the user_row xml file where we will update TextViews
    // with data saved to database and retrieved
    private class userViewHolder{
        ImageView imageProfileView;
        TextView textFirstName, textLastName, textDOB, textBloodGroup, textContactNumber;

    }

    //This Method Retrieves the data for the View and assigns the data to the corresponding elements
    //ie; TextViews & userIconImage for each Row within the list.
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View userRow = view;
        userViewHolder holder = new userViewHolder();

        if(userRow == null){
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            userRow = inflater.inflate(userListLayout, null);
            //The below text links the textfields in the list row to the data saved in the database.
            holder.textFirstName = userRow.findViewById(R.id.textFirstName);
            holder.textLastName = userRow.findViewById(R.id.textLastName);
            holder.textDOB = userRow.findViewById(R.id.textDOB);
            holder.textBloodGroup = userRow.findViewById(R.id.textBloodGroup);
            holder.textContactNumber = userRow.findViewById(R.id.textContactNumber);
            holder.imageProfileView = userRow.findViewById(R.id.userImageIcon);
            userRow.setTag(holder);
        } else {
            holder = (userViewHolder) userRow.getTag();
        }
        UserProfileModel userModel = userList.get(position);

        //The below code uses the getters & setters to display the data from the database to
        //the assigned textfields
        holder.textFirstName.setText(userModel.getFirstName());
        holder.textLastName.setText(userModel.getLastName());
        holder.textDOB.setText(userModel.getDOB());
        holder.textBloodGroup.setText(userModel.getBloodGroup());
        holder.textContactNumber.setText(userModel.getContactNumber());

        //The below code uses the getters & setters to display the data from the database to
        //the assigned image
        byte[]userProfileImage = userModel.getUserProfileImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(userProfileImage, 0, userProfileImage.length);
        holder.imageProfileView.setImageBitmap(bitmap);
        //The below text returns the row for the userProfile List
        return userRow;
    }
}
