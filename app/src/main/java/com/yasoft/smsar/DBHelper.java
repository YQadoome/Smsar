package com.yasoft.smsar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yasoft.smsar.models.Property;
import com.yasoft.smsar.models.SmsarModel;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    /*
    * CREATE TABLE Property (
        `PropertyID`	INTEGER NOT NULL,
        `SmsarUsername`	TEXT NOT NULL,
        `City`	TEXT,
        `Country`	TEXT,
        `Neighborhood`	TEXT,
        `Apartment Number`	INTEGER,
        `Description`	TEXT,
        `Longitude`	REAL,
        `Latitude`	REAL,
        `Pictures`	BLOB,
        FOREIGN KEY(`SmsarUsername`) REFERENCES `Smsar`(`username`),
        PRIMARY KEY(`PropertyID`)
    );
    *
    * */
    public static final String DATABASE_NAME = "Smsar.db";

    //CREATE TABLE Smsar
    public static final String SMSAR_TABLE_NAME = "Smsars";
    public static final String SMSAR_COLUMN_USERNAME = "username";
    public static final String SMSAR_COLUMN_NAME = "name";
    public static final String SMSAR_COLUMN_EMAIL = "email";
    public static final String SMSAR_COLUMN_PASSWORD= "password";
    public static final String SMSAR_COLUMN_PHONE = "phone";

    // CREATE TABLE Property
    public static final String PROPERTY_TABLE_NAME = "Property";
    public static final String  PROPERTY_COLUMN__PROPERTYID = "propertyID";
    public static final String  PROPERTY_COLUMN_SMSARUSERNAME = "smsarUsername";
    public static final String  PROPERTY_COLUMN__CITY = "city";
    public static final String  PROPERTY_COLUMN__DESCRIPTION = "description";
    public static final String  PROPERTY_COLUMN__PRICE = "price";

    // CREATE TABLE Images
    public static final String IMAGES_TABLE_NAME = "Property";
    public static final String  IMAGES_COLUMN__PROPERTYID = "propertyID";
    public static final String  IMAGES_COLUMN_IMAGEID= "imageID";
    public static final String  IMAGES_COLUMN__IMAGE = "image";

    private HashMap hp;



    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 13);
    }

    @Override
    public void onCreate(SQLiteDatabase db) throws SQLException{
        // TODO Auto-generated method stub
        db.execSQL(
                "create table Smsars " +
                        "(username text primary key unique, name text,email text, password text,phone text)"
        );
        db.execSQL(
                "create table Property " +
                        "(propertyID INTEGER primary key AUTOINCREMENT unique, smsarUsername TEXT " +
                        ",city TEXT, picture BLOB,description TEXT,price float ," +
                        "FOREIGN KEY(`smsarUsername`) REFERENCES `Smsar`(`username`))"
        );
        db.execSQL(
                "create table Images " +
                        "(imageID INTEGER primary key AUTOINCREMENT unique, propertyID INTEGER " +
                        ",image BLOB,"  +
                        "FOREIGN KEY(`propertyID`) REFERENCES `Property`(`propertyID`))"
        );
        db.execSQL(
               "create view propertyDataView AS " +
                       "select username ,name , phone, city " +
                       "from Smsars "+
                       "inner join Property ON Smsars.username=Property.smsarUsername"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)throws SQLException {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS Smsars");
        db.execSQL("DROP TABLE IF EXISTS Property");
        db.execSQL("DROP TABLE IF EXISTS Images");
        db.execSQL("DROP View IF EXISTS propertyDataView");
        onCreate(db);
    }

    public boolean insertProperty (String smsarusername,String City, String Description, float Price)throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("city", City);
        contentValues.put("smsarUsername", smsarusername);
        contentValues.put("description", Description);
        contentValues.put("price", Price);
        db.insert("Property", null, contentValues);
        return true;
    }
    public boolean insertImage (int Property_ID)throws SQLException {

        return true;
    }
    public boolean insertSmsar (String username,String name, String email, String password, String phone)throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("phone", phone);
        db.insert("Smsars", null, contentValues);
        return true;
    }

    public Cursor getDataCity(String citY)throws SQLException {

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from Property where city='"+citY+"'", null);
            return res;

          }
    public Cursor getData(String userName)throws SQLException {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Smsars where username='"+userName+"'", null);
        res.moveToFirst();
        return res;

    }



    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SMSAR_TABLE_NAME);
        return numRows;
    }

    public boolean updateSmsar (String username,String name, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("phone", phone);
        db.update("Smsars", contentValues, "username = ? ", new String[] { username } );
        return true;
    }


    public void deleteSmsar (String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("Smsars",
                "username = ? ",
                new String[] { (username) });
        db.delete("Property",
                "smsarUsername = ? ",
                new String[] { (username) });

    }

    public boolean deletePropperty (int id) {
        SQLiteDatabase db = this.getWritableDatabase();

       //  db.execSQL("delete from Property where propertyID='id'",null);
        return db.delete(PROPERTY_TABLE_NAME, PROPERTY_COLUMN__PROPERTYID + "=" + id, null) > 0;
    }

    //For ArrayList and Adapters
    public ArrayList<Property> getAllProperty(String Username){
        ArrayList<Property> array_list = new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from Property where smsarUsername='"+Username+"'",null);
        res.moveToFirst();

        while(res.moveToNext()){
            int id =res.getInt(res.getColumnIndex(PROPERTY_COLUMN__PROPERTYID));
            String city=res.getString(res.getColumnIndex(PROPERTY_COLUMN__CITY));
            String desc=res.getString(res.getColumnIndex(PROPERTY_COLUMN__DESCRIPTION));
            String price=res.getString(res.getColumnIndex(PROPERTY_COLUMN__PRICE));
           // String username=res.getString(res.getColumnIndex(PROPERTY_COLUMN_SMSARUSERNAME));
            Property property=new Property(id,city,desc,price);
            array_list.add(property);

        }

        return array_list;

    }

    //For Cursor;
    public Cursor getProperty(String Username){

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from Property where smsarUsername='"+Username+"'",null);
        res.moveToFirst();
            return res;

    }


    public ArrayList<Property> getAllProperty(){
        ArrayList<Property> array_list = new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from Property ",null);
        res.moveToFirst();

        while(res.moveToNext()){
            int id =res.getInt(res.getColumnIndex(PROPERTY_COLUMN__PROPERTYID));
            String city=res.getString(res.getColumnIndex(PROPERTY_COLUMN__CITY));
            String desc=res.getString(res.getColumnIndex(PROPERTY_COLUMN__DESCRIPTION));
            String price=res.getString(res.getColumnIndex(PROPERTY_COLUMN__PRICE));
            String username=res.getString(res.getColumnIndex(PROPERTY_COLUMN_SMSARUSERNAME));
            Property property=new Property(id,username,city,desc,price);
            array_list.add(property);

        }

        return array_list;

    }
    public Cursor getAllData(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Smsars", null);

        return res;
    }

    public ArrayList<String> propertyView(String Username){
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from propertyDataView where username='"+Username+"'",null);

        res.moveToFirst();

        while(res.moveToNext()){
            array_list.add(res.getString(res.getColumnIndex(PROPERTY_COLUMN__CITY)));
            array_list.add(res.getString(res.getColumnIndex(PROPERTY_COLUMN__DESCRIPTION)));
            array_list.add(res.getString(res.getColumnIndex(PROPERTY_COLUMN__PRICE)));

        }

        return array_list;
    }
    public ArrayList<SmsarModel> getAllSmsar(String userName) {
        ArrayList<SmsarModel> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Smsars where username='" + userName + "'", null );
        res.moveToFirst();

        while(res.moveToNext()){
            String username=res.getString(res.getColumnIndex(SMSAR_COLUMN_USERNAME));
            String name=res.getString(res.getColumnIndex(SMSAR_COLUMN_NAME));
            String pn=res.getString(res.getColumnIndex(SMSAR_COLUMN_PHONE));
            SmsarModel smsarModel=new SmsarModel(username,name,pn);
            array_list.add(smsarModel);

        }

        return array_list;
    }
    public ArrayList<SmsarModel> getAllSmsar() {
        ArrayList<SmsarModel> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Smsars ", null );
        res.moveToFirst();

        while(res.moveToNext()){
            String username=res.getString(res.getColumnIndex(SMSAR_COLUMN_USERNAME));
            String name=res.getString(res.getColumnIndex(SMSAR_COLUMN_NAME));
            String pn=res.getString(res.getColumnIndex(SMSAR_COLUMN_PHONE));
            SmsarModel smsarModel=new SmsarModel(username,name,pn);
            array_list.add(smsarModel);

        }

        return array_list;
    }

    public String getPhone(String userName) {
        ArrayList<SmsarModel> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Smsars where username='" + userName + "'", null );
        res.moveToFirst();
        String pn="";
            pn=res.getString(res.getColumnIndex(SMSAR_COLUMN_PHONE));


        return pn;
    }
}
