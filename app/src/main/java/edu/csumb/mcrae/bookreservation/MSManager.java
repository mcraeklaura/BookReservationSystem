package edu.csumb.mcrae.bookreservation;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MSManager extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MSManager";

    private TaskDbHelper mHelper;

    private Button mOKButton;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msmanager);

        mHelper = new TaskDbHelper(this);

        mOKButton = (Button) findViewById(R.id.msManagerOKButton);
        mOKButton.setOnClickListener(MSManager.this);

        mTaskListView = (ListView) findViewById(R.id.listTransactions);

        updateUI();
    }

    private void updateUI(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskDbHelper.TABLE_TRANSACTION, new String[]{"_id", TaskDbHelper.COL_TRANSACTION_TRANSACTIONTYPE, TaskDbHelper.COL_TRANSACTION_USER, TaskDbHelper
                .COL_TRANSACTION_BOOKID, "date_time", "active"}, null, null, null, null, null);

        Cursor cursorU = db.query(TaskDbHelper.TABLE_USER, new String[]{"_id", TaskDbHelper.COL_USER_USERNAME}, null, null, null, null, null);
        Cursor cursorB = db.query(TaskDbHelper.TABLE_BOOK, new String[]{"_id", TaskDbHelper.COL_BOOK_TITLE}, null, null, null, null, null);
//        cursorU.moveToFirst();
//        cursorB.moveToFirst();
//        cursor.moveToFirst();
        while(cursor.moveToNext() ){
            //might have to change this....
            cursorU.moveToNext();
            cursorB.moveToNext();
            int idTT = cursor.getColumnIndex(TaskDbHelper.COL_TRANSACTION_TRANSACTIONTYPE);
            int idUI = cursor.getColumnIndex(TaskDbHelper.COL_TRANSACTION_USER);
            int idDT = cursor.getColumnIndex(TaskDbHelper.COL_TRANSACTION_DATETIME);
            int idU = cursorU.getColumnIndex(TaskDbHelper.COL_USER_USERNAME);

            //Transaction Type
            String transactionType = cursor.getString(idTT);
            int u = cursor.getInt(idUI);
            //Username
            Integer hold = cursor.getInt(u);
            System.err.println(idUI);
            String user_id;
            try {
                user_id = cursorU.getString(idU);
            }catch(Exception e){
                user_id = "ADMIN";
            }
            //Date
            String date = cursor.getString(idDT);

            String tRow = "Transaction Type: " + transactionType + "\n"
                    + "User: " + user_id + "\n"
                    + "Date: " + date;
            System.out.println(tRow);


            taskList.add(tRow);

        }
        if(mAdapter == null){
            mAdapter = new ArrayAdapter<>(this, R.layout.item_todo, R.id.task_title, taskList);
            mTaskListView.setAdapter(mAdapter);
        }
        else{
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    public void onClick(View v){
        if(v.getId() == R.id.msManagerOKButton){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Add Book")
                    .setMessage("Do you wish to add a book to the system?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            Intent p = new Intent(MSManager.this, AddBook.class);
                            startActivity(p);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            Intent p = new Intent(MSManager.this, MainActivity.class);
                            startActivity(p);
                        }
                    })
            .create();
            dialog.show();
            //Do something
        }
    }
}
