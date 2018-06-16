package com.example.dnafv.alarmmedication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

//Need to extend the Class allowing access to SQLiteOpenHelper
//Implement the onCreate & onUpgrade Methods
//Implement Constructor including super

public class SQLiteHelper extends SQLiteOpenHelper {


    public SQLiteHelper(Context context,
                        String name,
                        SQLiteDatabase.CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    //This Method opens a connection to the DB and provides the ability for us to pass in an SQL query
    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //This Method allows us to INSERT data into the DB
    //EditText mEditFirstName, mEditLastName, mEditBloodGroup, mEditContactNumber, mEditDOB, mEditEmailAddress;
    public void insertData(
            String firstName,
            String lastName,
            String bloodGroup,
            String contactNumber,
            String DOB,
            String emailAddress,
            byte[] image){
        SQLiteDatabase database = getWritableDatabase();

        //This is the query that will insert data into the table where userProfile is the name of the table
        String sql = "INSERT INTO userProfile VALUES (NULL,?,?,?,?,?,?,?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        // This code binds the data to their respecting columns within the table
        statement.bindString(1, firstName);
        statement.bindString(2, lastName);
        statement.bindString(3, bloodGroup);
        statement.bindString(4, contactNumber);
        statement.bindString(5, DOB);
        statement.bindString(6, emailAddress);
        statement.bindBlob(7, image);

        //Inserts the data into the database
        statement.executeInsert();
        //Close the database connection to prevent data leakage
        database.close();
    }

    //This Method allows us to UPDATE the data in the userProfile table
    //Make sure to add the id for the record to the Method defining which record should be updated
    public void updateData(
            String firstName,
            String lastName,
            String bloodGroup,
            String contactNumber,
            String DOB,
            String emailAddress,
            byte[] image,
            int id){

        SQLiteDatabase database = getWritableDatabase();

        //This is the query that will updated the record within the useProfile table using the userId
        String sql = "UPDATE userProfile SET " +
                "firstName = ?, " +
                "lastName = ?, " +
                "bloodGroup = ?, " +
                "contactNumber = ?, " +
                "DOB = ?, " +
                "emailAddress = ?, " +
                "image = ? " +
                "WHERE id = ?";

        SQLiteStatement statement = database.compileStatement(sql);

        // This code binds the data to their respecting columns within the table
        statement.bindString(1, firstName);
        statement.bindString(2, lastName);
        statement.bindString(3, bloodGroup);
        statement.bindString(4, contactNumber);
        statement.bindString(5, DOB);
        statement.bindString(6, emailAddress);
        statement.bindBlob(7, image);
        statement.bindDouble(8, id);

        //Inserts the data into the database
        statement.execute();
        //Close the database connection to prevent data leakage
        database.close();
    }

    //This Method allows us to DELETE the data in the userProfile table
    //Make sure to add the id for the record to the Method defining which record should be deleted
    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();

        //This is the query that will delete the record within the userProfile table using the userId
        String sql = "DELETE FROM userProfile " +
                "WHERE id = ?";

        SQLiteStatement statement = database.compileStatement(sql);

        //Using the record id to identify which record needs to be removed from the userProfile table
        statement.clearBindings();
        statement.bindDouble(1, (double)id);

        //Deletes the data from the userProfile table
        statement.execute();
        //Close the database connection to prevent data leakage
        database.close();
    }

    //This method is used to RETRIEVE data from the userProfile table
    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
