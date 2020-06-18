package com.pikopako.LocalStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pikopako.Model.AddressModel;

import java.util.ArrayList;
import java.util.List;



public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "placeManager";
    // Contacts table name
    private static final String TABLE_ADDRESS = "address";
    // Contacts Table Columns names
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LOCATIONID = "location_id";

    static DatabaseHandler mInstance;


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_ADDRESS + "("
                + KEY_LOCATIONID + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);

        // Create tables again
        onCreate(db);
    }

    public void addSAddress(AddressModel contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATIONID, contact.getLocationId()); //
        values.put(KEY_ADDRESS, contact.getAddress()); //
        values.put(KEY_LATITUDE, contact.getLatitude());
        values.put(KEY_LONGITUDE, contact.getLongitude());
        // Inserting Row
        db.insert(TABLE_ADDRESS, null, values);
        db.close(); // Closing database connection
    }

    public List<AddressModel> getAllAddress() {
        List<AddressModel> contactList = new ArrayList<AddressModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ADDRESS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AddressModel addressModel = new AddressModel();
                addressModel.setLocationId(cursor.getString(0));
                addressModel.setAddress(cursor.getString(1));
                addressModel.setLatitude(cursor.getString(2));
                addressModel.setLongitude(cursor.getString(3));
                // Adding contact to list
                contactList.add(addressModel);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public void deleteAddress(AddressModel addressModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADDRESS, KEY_LOCATIONID + " = ?",
                new String[]{String.valueOf(addressModel.getLocationId())});
        db.close();
    }
}