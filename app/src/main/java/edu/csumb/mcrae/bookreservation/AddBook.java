package edu.csumb.mcrae.bookreservation;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddBook extends AppCompatActivity implements View.OnClickListener{
    private Button mSubmitButton;
    private EditText mtitle;
    private EditText mauthor;
    private EditText misbn;
    private EditText mpriceperhour;

    private TaskDbHelper mHelper;

    private DateFormat df;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        date = new Date();

        mHelper = new TaskDbHelper(this);

        mSubmitButton = (Button) findViewById(R.id.addBookSubmitButton);
        mSubmitButton.setOnClickListener(this);

        mtitle = (EditText) findViewById(R.id.title);
        mauthor = (EditText) findViewById(R.id.author);
        misbn = (EditText) findViewById(R.id.ISBN);
        mpriceperhour = (EditText) findViewById(R.id.feePerHour);

    }

    public void onClick(View v){
        String t = ".", a = ".", i = ".";
        double f = -1;
        t = mtitle.getText().toString();
        a = mauthor.getText().toString();
        i = misbn.getText().toString();
        f = Double.parseDouble(mpriceperhour.getText().toString());
        if(v.getId() == R.id.addBookSubmitButton){
            if(t.equals(".") || a.equals(".") || i.equals(".") || f == -1){
                Toast.makeText(AddBook.this, "Please input the correct information.", Toast.LENGTH_LONG).show();
            }
            else{
                //Add the book into the database
                SQLiteDatabase db = mHelper.getWritableDatabase();
                Cursor cursorB = db.query(TaskDbHelper.TABLE_BOOK, new String[]{"_id", "title", "author", "price_per_hour", "available", "isbn"}, null, null, null, null, null);
                Cursor cursorT = db.query(TaskDbHelper.TABLE_TRANSACTION, new String[]{"_id", "transaction_type", "user_id", "book_id", "codates", "rdate", "date_time", "active"}, null, null, null, null, null);

                ContentValues valuesB = new ContentValues();
                ContentValues valuesT = new ContentValues();

                valuesB.put("title", t); valuesB.put("author", a); valuesB.put("price_per_hour", f); valuesB.put("available", 1); valuesB.put("isbn", i);
                long faca = db.insertWithOnConflict("book", null, valuesB, SQLiteDatabase.CONFLICT_REPLACE);
                int yay = (int) faca;

                //TODO figure out if its active or inactive here (I just put inactive
                valuesT.put("transaction_type", "Add Book"); valuesT.put("book_id", yay); valuesT.put("codates", "NONE"); valuesT.put("rdate", "NONE");
                valuesT.put("date_time", df.format(date));valuesT.put("active", 0);
                db.insertWithOnConflict("t", null, valuesT, SQLiteDatabase.CONFLICT_REPLACE);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add Book")
                        .setMessage("Do you want to add another book?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                Intent i = new Intent(AddBook.this, AddBook.class);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                Intent i = new Intent(AddBook.this, MainActivity.class);
                                startActivity(i);
                            }

                        })
                        .create();
                dialog.show();

                }
            }
        }
    }