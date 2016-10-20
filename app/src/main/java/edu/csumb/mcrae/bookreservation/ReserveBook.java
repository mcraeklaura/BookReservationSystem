package edu.csumb.mcrae.bookreservation;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReserveBook extends AppCompatActivity implements View.OnClickListener {
    private TaskDbHelper mHelper;
    private String u;
    private String p;
    private String pickD;
    private String pickT;
    private String dropD;
    private String dropT;

    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    private DateFormat df;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_book);

        df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        date = new Date();

        mTaskListView = (ListView) findViewById(R.id.listBooksAvailable);

        mHelper = new TaskDbHelper(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            u = extras.getString("username");
            p= extras.getString("password");
            pickD = extras.getString("pickupdate");
            pickT = extras.getString("pickuptime");
            dropD = extras.getString("dropoffdate");
            dropT = extras.getString("dropofftime");

        }

        mTaskListView = (ListView) findViewById(R.id.listBooksAvailable);

        updateUI();

    }

    private void updateUI(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getWritableDatabase();

        Cursor cursorH = db.query(TaskDbHelper.TABLE_HOLDS, new String[]{"_id", "book_id", "user_id", "isbn", "pick_up_date", "pick_up_time", "drop_off_date", "drop_off_time"}, null, null, null, null, null);
        ArrayList<String> ISBNList = new ArrayList<>();
        cursorH.moveToFirst();
        Cursor cursorB = db.query(TaskDbHelper.TABLE_BOOK, new String[]{"_id", "title", "author", "isbn", "price_per_hour"}, null, null, null, null, null);
        cursorB.moveToFirst();
        boolean t = false; boolean f = true; boolean didItNeverGoInside = false;

        while(cursorB.moveToNext()){
            //Want to check to see if the book is on hold, and if it is, want to check
            //if that book can be checked out at the specific time

            int bookTitle = cursorB.getColumnIndex("title");
            int bookAuthor = cursorB.getColumnIndex("author");
            int bookPPH = cursorB.getColumnIndex("price_per_hour");
            int ISBNnum = cursorB.getColumnIndex("isbn");
            cursorH.moveToFirst();
            while(cursorH.moveToNext()){
                if(cursorB.getString(ISBNnum).equals(cursorH.getString(cursorB.getColumnIndex("isbn")))){
                    //If this is still false, then it never had a hold and should be printed
                    //out after this while loop
                    didItNeverGoInside = true;
                    int book_id_NUM = cursorH.getColumnIndex("book_id");
                    int user_id_NUM = cursorH.getColumnIndex("user_id");
                    int idPD = cursorH.getColumnIndex("pick_up_date");
                    int idPT = cursorH.getColumnIndex("pick_up_time");
                    int idDD = cursorH.getColumnIndex("drop_off_date");
                    int idDT = cursorH.getColumnIndex("drop_off_time");

                    Integer hMonth1 = Integer.parseInt(cursorH.getString(idPD).substring(0, 2));
                    Integer hMonth2 = Integer.parseInt(cursorH.getString(idDD).substring(0, 2));
                    Integer hDate1 = Integer.parseInt(cursorH.getString(idPD).substring(3, 5));
                    Integer hDate2 = Integer.parseInt(cursorH.getString(idDD).substring(3, 5));
                    Integer hYear1 = Integer.parseInt(cursorH.getString(idPD).substring(6));
                    Integer hYear2 = Integer.parseInt(cursorH.getString(idDD).substring(6));
                    Integer hHour1 = Integer.parseInt(cursorH.getString(idPT).substring(0, 2));
                    Integer hHour2 = Integer.parseInt(cursorH.getString(idDT).substring(0, 2));
                    Integer hMinute1 = Integer.parseInt(cursorH.getString(idPT).substring(3));
                    Integer hMinute2 = Integer.parseInt(cursorH.getString(idDT).substring(3));

                    //System.currentTimeMillis() USE THIS FOR CHECKING THE DATE in placehold.class
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    Date uPD, uDD, hPD, hDD;

                    //Use this to see if you can use this book or not


                    try{
                        sdf.setLenient(false);
                        uPD = sdf.parse(pickD + " " + pickT);
                        uDD = sdf.parse(dropD + " " + dropT);
                        hPD = sdf.parse(hMonth1 + "/" + hDate1 + "/" + hYear1 + " " + hHour1 + ":" + hMinute1);
                        hDD = sdf.parse(hMonth2 + "/" + hDate2 + "/" + hYear2 + " " + hHour2 + ":" + hMinute2);

                        if (uPD.before(hPD) && (uDD.before(hDD) && uDD.after(hPD))) {
                            f = false;
                        } else if (uPD.equals(hPD) || uDD.equals(hDD)) {
                            f = false;
                        } else if ((uPD.after(hPD) && uPD.before(hDD)) && uDD.after(hDD)) {
                            f = false;
                        } else if (uPD.before(hPD) && uDD.after(hDD)) {
                            f = false;
                        } else {
                            f = true;
                        }
                    }
                    catch(ParseException e){
                        e.printStackTrace();
                    }

                    if(f){
                        //This means there were no overlaps for this hold and you can print it

                        //Get the information from book_id from hold table
                        String title = cursorB.getString(bookTitle);
                        String author = cursorB.getString(bookAuthor);
                        String ISBN = cursorB.getString(ISBNnum);
                        double pph = cursorB.getDouble(bookPPH);

                        String book = "Title: " + title + "\n"
                                + "Author: " + author + "\n"
                                + "ISBN: " + ISBN + "\n"
                                + "Price Per Hour: " + pph;

                        taskList.add(book);

                    }


                    break;
                }
            }
            if(!didItNeverGoInside){
                //Print out the book
                String title = cursorB.getString(bookTitle);
                String author = cursorB.getString(bookAuthor);
                String ISBN = cursorB.getString(ISBNnum);
                double pph = cursorB.getDouble(bookPPH);

                String book = "Title: " + title + "\n"
                        + "Author: " + author + "\n"
                        + "ISBN: " + ISBN + "\n"
                        + "Price Per Hour: " + pph;

                taskList.add(book);

            }
        }
        cursorB.close();
        cursorH.close();
        db.close();
    }

    /*
    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getWritableDatabase();

        Cursor cursorH = db.query(TaskDbHelper.TABLE_HOLDS, new String[]{"_id", "book_id", "user_id", "isbn", "pick_up_date", "pick_up_time", "drop_off_date", "drop_off_time"}, null, null, null, null, null);
        ArrayList<String> ISBNList = new ArrayList<>();
        boolean t = false;
        boolean r = true;
        String isbn;
        ArrayList<Integer> prevHolds = new ArrayList<>();
        cursorH.moveToFirst();
        int p = 1;
        Cursor cursorB;
        while(cursorH.moveToNext()){
                t = true;

                int BI = cursorH.getColumnIndex("book_id");
                int UN = cursorH.getColumnIndex("user_id");
                int idPD = cursorH.getColumnIndex("pick_up_date");
                int idPT = cursorH.getColumnIndex("pick_up_time");
                int idDD = cursorH.getColumnIndex("drop_off_date");
                int idDT = cursorH.getColumnIndex("drop_off_time");

                //Hold dates and times
                Integer hMonth1 = Integer.parseInt(cursorH.getString(idPD).substring(0, 2));
                Integer hMonth2 = Integer.parseInt(cursorH.getString(idDD).substring(0, 2));
                Integer hDate1 = Integer.parseInt(cursorH.getString(idPD).substring(3, 5));
                Integer hDate2 = Integer.parseInt(cursorH.getString(idDD).substring(3, 5));
                Integer hYear1 = Integer.parseInt(cursorH.getString(idPD).substring(6));
                Integer hYear2 = Integer.parseInt(cursorH.getString(idDD).substring(6));
                Integer hHour1 = Integer.parseInt(cursorH.getString(idPT).substring(0, 2));
                Integer hHour2 = Integer.parseInt(cursorH.getString(idDT).substring(0, 2));
                Integer hMinute1 = Integer.parseInt(cursorH.getString(idPT).substring(3));
                Integer hMinute2 = Integer.parseInt(cursorH.getString(idDT).substring(3));

                //System.currentTimeMillis() USE THIS FOR CHECKING THE DATE in placehold.class
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                Date uPD, uDD, hPD, hDD;
                boolean f = true;
                boolean v = false;

                try {
                    sdf.setLenient(false);
                    uPD = sdf.parse(pickD + " " + pickT);
                    uDD = sdf.parse(dropD + " " + dropT);
                    hPD = sdf.parse(hMonth1 + "/" + hDate1 + "/" + hYear1 + " " + hHour1 + ":" + hMinute1);
                    hDD = sdf.parse(hMonth2 + "/" + hDate2 + "/" + hYear2 + " " + hHour2 + ":" + hMinute2);
                    if (uPD.before(hPD) && (uDD.before(hDD) && uDD.after(hPD))) {
                        f = false;
                    } else if (uPD.equals(hPD) || uDD.equals(hDD)) {
                        f = false;
                    } else if ((uPD.after(hPD) && uPD.before(hDD)) && uDD.after(hDD)) {
                        f = false;
                    } else if (uPD.before(hPD) && uDD.after(hDD)) {
                        f = false;
                    } else {
                        f = true;
                    }

                    System.out.println(p++);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int BN_ = cursorH.getInt(BI);
                int BA_ = cursorH.getInt(UN);

                String selectB = "SELECT " + TaskDbHelper.COL_BOOK_TITLE + ", " + TaskDbHelper.COL_BOOK_AUTHOR
                        + ", isbn, price_per_hour FROM book WHERE " + BN_ + " = _id";

                String selectU = "SELECT username FROM user WHERE " + BA_ + " = _id";

                cursorB = db.rawQuery(selectB, null);
                Cursor cursorU = db.rawQuery(selectU, null);

                cursorB.moveToFirst();
                cursorU.moveToFirst();

                if (f) {
//                    boolean y = false;
//                    for (int i = 0; i < prevHolds.size(); i++) {
//                        if (prevHolds.get(i) == cursorH.getInt(cursorH.getColumnIndex("_id"))) {
//                            y = true;
//                            break;
//                        }
//                    }
//
//                    if (!y) {
                        prevHolds.add(cursorH.getInt(cursorH.getColumnIndex("_id")));
                        //Check all the books using the ISBN of the book
                        String bookName = cursorB.getString(cursorB.getColumnIndex("title"));
                        String bookAuthor = cursorB.getString(cursorB.getColumnIndex("author"));
                        double price = cursorB.getDouble(cursorB.getColumnIndex("price_per_hour"));
                        isbn = cursorB.getString(cursorB.getColumnIndex("isbn"));
                        String tRow = "Title: " + bookName + "\n"
                                + "Author: " + bookAuthor + "\n"
                                + "ISBN: " + isbn + "\n"
                                + "Price Per Hour: " + price;

                        taskList.add(tRow);
                        ISBNList.add(isbn);

                } else {
                    isbn = cursorB.getString(cursorB.getColumnIndex("isbn"));
                    ISBNList.add(isbn);
                }


                //Only show books that are not in the isbn  list
                //cursorB.close();
                cursorU.close();
            }

        for (int i = 0; i < ISBNList.size(); i++) {
            System.out.println("Index " + i + ": " + ISBNList.get(i));
        }
        if(){
            Cursor cursorB = db.query("book", new String[]{"_id", "isbn", "title", "author", "price_per_hour"}, null, null, null, null, null);

        }
        cursorB.moveToFirst();


        int counter = 0;
        while(cursorB.moveToNext()){
            //Want to go through the list and if the count > 1, break and start searching through the next one
            counter = 0;
            int colInx = cursorB.getColumnIndex("isbn");
            int colTIT = cursorB.getColumnIndex("title");
            int colAU = cursorB.getColumnIndex("author");
            int colPPH = cursorB.getColumnIndex("price_per_hour");
            int i;
            for(i = 0; i < ISBNList.size(); i++){
                if(cursorB.getString(colInx).equals(ISBNList.get(i))){
                    ISBNList.remove(i);
                    counter++;
                    break;
                }

            }
            if(counter < 1){
                    String bookName = cursorB.getString(colTIT);
                    String bookAuthor = cursorB.getString(colAU);
                    double price = cursorB.getDouble(colPPH);
                    isbn = cursorB.getString(colInx);
                    String tRow = "Title: " + bookName + "\n"
                            + "Author: " + bookAuthor + "\n"
                            + "ISBN: " + isbn + "\n"
                            + "Price Per Hour: " + price;

                    taskList.add(tRow);
            }


//        while (true) {
//            //String selectAllBook = "SELECT * FROM book WHERE " + ISBNList.get(i) + " = isbn";
//            for (int i = 0; i < ISBNList.size(); i++) {
//                int colInx = cursorRB.getColumnIndex("isbn");
//                int colTIT = cursorRB.getColumnIndex("title");
//                int colAU = cursorRB.getColumnIndex("author");
//                int colPPH = cursorRB.getColumnIndex("price_per_hour");
//
//                if (!cursorRB.getString(colInx).equals(ISBNList.get(i))) {
//                    String bookName = cursorRB.getString(colTIT);
//                    String bookAuthor = cursorRB.getString(colAU);
//                    double price = cursorRB.getDouble(colPPH);
//                    isbn = cursorRB.getString(colInx);
//                    String tRow = "Title: " + bookName + "\n"
//                            + "Author: " + bookAuthor + "\n"
//                            + "ISBN: " + isbn + "\n"
//                            + "Price Per Hour: " + price;
//
//                    taskList.add(tRow);
//                    break;
//                }
//            }
//            if(!cursorRB.moveToNext()){
//                break;
//            }
//        }
        cursorRB.close();
        if(!t){
            //Then there were no holds to begin with.
            //Show all books in the system.
            Cursor cursorB = db.query(TaskDbHelper.TABLE_BOOK, new String[]{"_id", "title", "author", "isbn", "price_per_hour"}, null, null, null, null, null);
            int title = cursorB.getColumnIndex("title");
            int author = cursorB.getColumnIndex("author");
            int isbN = cursorB.getColumnIndex("isbn");
            int pph = cursorB.getColumnIndex("price_per_hour");
            while(cursorB.moveToNext()){
                String tRow = "Title: " + cursorB.getString(title) + "\n"
                        + "Author: " + cursorB.getString(author) + "\n"
                        + "ISBN: " + cursorB.getString(isbN) + "\n"
                        + "Price Per Hour: $" + cursorB.getDouble(pph);
                taskList.add(tRow);
            }
        }
        if(mAdapter == null){
            mAdapter = new ArrayAdapter<>(ReserveBook.this, R.layout.item_book, R.id.task_book, taskList);
            mTaskListView.setAdapter(mAdapter);
        }
        else{
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursorH.close();
        db.close();
}}
    */
    public void onClick(View v){

    }

    public void reserveBook(View v){
        View parent = (View) v.getParent();
        TextView bookTextView = (TextView) parent.findViewById(R.id.task_book);
        String book = String.valueOf(bookTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursorT = db.query(TaskDbHelper.TABLE_TRANSACTION, new String[]{"_id", "transaction_type", "user_id", "book_id", "codates", "rdate", "active"}, null, null, null, null, null);

        String title, author, isbn;
        double pph = 0;

        isbn = book.substring(book.indexOf("ISBN: ") + 6, book.indexOf("\nPrice Per Hour: "));
        String s = book.substring(book.indexOf("Price Per Hour: ") + 17);
        try {
            Double a = Double.parseDouble(s);
            pph = a;
        }
        catch(Exception e){
            System.out.println("String --> Double ERROR");
        }

        //Put the transaction and hold into the database
        //Transaction
        ContentValues valuesT = new ContentValues();
        valuesT.put("transaction_type", "Book Reservation");

        String selectU = "SELECT _id FROM user WHERE '" + u + "' = username";
        String selectB = "SELECT _id FROM book WHERE '" + isbn + "' = isbn";
        Cursor cursorU = db.rawQuery(selectU, null);
        Cursor cursorB = db.rawQuery(selectB, null);
        cursorU.moveToFirst();
        cursorB.moveToFirst();

        valuesT.put("user_id", cursorU.getInt(cursorU.getColumnIndex("_id")));
        valuesT.put("book_id", cursorB.getInt(cursorB.getColumnIndex("_id")));
        valuesT.put("codates", pickD);
        valuesT.put("rdate", dropD);
        valuesT.put("date_time", df.format(date));

        //Insert into transaction table
        db.insertWithOnConflict("t", null, valuesT, SQLiteDatabase.CONFLICT_REPLACE);

        Cursor cursorh = db.query("holds", new String[]{"_id", "isbn", "book_id", "user_id", "pick_up_date", "pick_up_time", "drop_off_date", "drop_off_time"}
            , null, null, null, null, null);
        ContentValues valuesH = new ContentValues();
        valuesH.put("book_id", cursorB.getInt(cursorB.getColumnIndex("_id")));
        valuesH.put("user_id", cursorU.getInt(cursorU.getColumnIndex("_id")));
        valuesH.put("pick_up_date", pickD);
        valuesH.put("pick_up_time", pickT);
        valuesH.put("drop_off_date", dropD);
        valuesH.put("drop_off_time", dropT);
        valuesH.put("isbn", isbn);

        //Insert into holds table
        db.insertWithOnConflict("holds", null, valuesH, SQLiteDatabase.CONFLICT_REPLACE);
        cursorB.close();
        cursorU.close();
        cursorT.close();
        db.close();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Reservation")
                .setMessage("Your book has been successfully reserved. Do you wish to reserve another?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        Intent i = new Intent(ReserveBook.this, PlaceHold.class);
                        Bundle extraInfo = new Bundle();
                        extraInfo.putString("username", u);
                        extraInfo.putString("password", p);
                        i.putExtras(extraInfo);
                        startActivity(i);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        Intent i = new Intent(ReserveBook.this, MainActivity.class);
                        Bundle extraInfo = new Bundle();
                        extraInfo.putString("username", u);
                        extraInfo.putString("password", p);
                        i.putExtras(extraInfo);
                        startActivity(i);
                    }
                })
                .create();
        dialog.show();
    }
}
