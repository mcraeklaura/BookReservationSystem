package edu.csumb.mcrae.bookreservation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by mcrae on 5/9/2016.
 */

//Upgrade Database when making a change to this code
public class TaskDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "info.laura.username_password.db";
    public static final int DB_VERSION = 27;
    public static final String TABLE_USER = "user";
    public static final String TABLE_BOOK = "book";
    public static final String TABLE_TRANSACTION = "t";
    public static final String TABLE_HOLDS = "holds";

    //User Table
    public static final String COL_USER_ID = "_id";
    public static final String COL_USER_USERNAME = "username";
    public static final String COL_USER_PASSWORD = "password";

    //Book Table
    public static final String COL_BOOK_ID = "_id";
    public static final String COL_BOOK_TITLE = "title";
    public static final String COL_BOOK_AUTHOR = "author";
    public static final String COL_BOOK_PRICEPERHOUR = "price_per_hour";
    public static final String COL_BOOK_AVAILABLE = "available";
    public static final String COL_BOOK_ISBN = "isbn";

    //Transaction Table
    public static final String COL_TRANSACTION_ID = "_id";
    public static final String COL_TRANSACTION_TRANSACTIONTYPE = "transaction_type";
    public static final String COL_TRANSACTION_USER = "user_id";   //Username of the person making the transaction
    public static final String COL_TRANSACTION_BOOKID = "book_id";  //Connect this to the ID in the book table
    public static final String COL_TRANSACTION_CODATE = "codates";     //Check out date
    public static final String COL_TRANSACTION_RDATE = "rdate";     //Return date
    public static final String COL_TRANSACTION_DATETIME = "date_time";   //Getting the date and time of the transaction
    public static final String COL_TRANSACTION_ACTIVE = "active";   //If you have a book checked out and you want to know if it has been returned or not.

    //Hold Table
    public static final String COL_HOLDS_ID = "_id";
    public static final String COL_HOLDS_BOOKID = "book_id";
    public static final String COL_HOLDS_USERID = "user_id";
    public static final String COL_HOLDS_ISBN = "isbn";
    public static final String COL_HOLDS_PD = "pick_up_date";
    public static final String COL_HOLDS_PT = "pick_up_time";
    public static final String COL_HOLDS_DD = "drop_off_date";
    public static final String COL_HOLDS_DT = "drop_off_time";

    public TaskDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }


    //TODO Password column not being created
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE_USER = "CREATE TABLE user ( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL, "
                + "password TEXT NOT NULL );";

        db.execSQL(CREATE_TABLE_USER);

        String CREATE_TABLE_BOOK = "CREATE TABLE book ( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "author TEXT NOT NULL, "
                + "price_per_hour TEXT NOT NULL, "    //make it into a double when you go to hours
                + "available  INTEGER, "    //Boolean 1:True 0:False
                + "isbn TEXT NOT NULL ) ;";

        db.execSQL(CREATE_TABLE_BOOK);

        String CREATE_TABLE_T = "CREATE TABLE t ( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "transaction_type TEXT NOT NULL, "
                + "user_id INTEGER REFERENCES " + TABLE_USER + "(" + COL_USER_ID + "), "
                + COL_TRANSACTION_BOOKID + " INTEGER REFERENCES " + TABLE_BOOK + "(" + COL_BOOK_ID + "), "
                + "codates TEXT NOT NULL, "
                + "rdate TEXT NOT NULL,"
                + "date_time TEXT NOT NULL, "
                + "active INTEGER ) ;";

        db.execSQL(CREATE_TABLE_T);

        String CREATE_TABLE_HOLDS = "CREATE TABLE holds ( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "book_id INTEGER REFERENCES book(_id), "
                + "user_id INTEGER REFERENCES user(_id), "
                + "isbn TEXT NOT NULL, "
                + "pick_up_date TEXT NOT NULL, "
                + "pick_up_time TEXT NOT NULL, "
                + "drop_off_date TEXT NOT NULL, "
                + "drop_off_time TEXT NOT NULL );";

        db.execSQL(CREATE_TABLE_HOLDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS book");
        db.execSQL("DROP TABLE IF EXISTS t");
        db.execSQL("DROP TABLE IF EXISTS holds");
        onCreate(db);
    }

    public boolean searchKeyString(String key, String table) {
        boolean flag = true;
        String rtn = "";
        //Log.d("searchKeyString", TABLE_USER);

        //Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_USER + " where username=\"" + COL_USER_USERNAME + "\"";

        SQLiteDatabase d = this.getReadableDatabase();
        Cursor cursor = d.rawQuery(selectQuery, null);

        //Looping through all rows
        if (cursor.moveToFirst()) {
            do {
                //Log.d("searchKeyString", "searching");
                System.err.println(cursor.getString(1));
                if (cursor.getString(1).equals(key)) {
                    flag = false;
                    break;
                }
            } while (cursor.moveToNext());
        }
            cursor.close();
            d.close();
            Log.d("searchKeyString", "finish search");

            return flag;
    }

    public boolean usernameAndPassword(String u, String p){
        boolean flag = true;

        //Usernames
        String selectUsername = "SELECT * FROM " + TABLE_USER + " where username=\"" + COL_USER_USERNAME + "\"";
        SQLiteDatabase d = this.getReadableDatabase();
        Cursor cursorU = d.rawQuery(selectUsername, null);

        //Passwords
        String selectPassword = "SELECT * FROM " + TABLE_USER + " where password=\"" + COL_USER_PASSWORD + "\"";
        Cursor cursorP = d.rawQuery(selectPassword, null);

        int i = 1;
        if(cursorU.moveToFirst() && cursorP.moveToFirst()){
            do{
                if(cursorU.getString(1).equals(u) && cursorP.getString(1).equals(p)){
                    flag = true;
                    break;
                }
                i++;
            }while(cursorP.moveToNext() && cursorU.moveToNext());
        }
        cursorP.close();
        cursorU.close();
        d.close();

        return flag;
    }
}
