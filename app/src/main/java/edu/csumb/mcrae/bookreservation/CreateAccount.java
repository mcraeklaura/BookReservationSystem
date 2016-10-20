package edu.csumb.mcrae.bookreservation;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateAccount extends Activity implements View.OnClickListener {
    private DateFormat df;
    private Date date;

    private TaskDbHelper mHelper;   //For database

    private EditText mUsername;
    private EditText mPassword;
    private Button mSubmitButton;

    private int tries;
    private String str;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private int myStatic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO make bundle telling what was the last instance made
        df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        date = new Date();

        tries = 0;
        mHelper = new TaskDbHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mSubmitButton = (Button) findViewById(R.id.submitUsernameAndPasswordButton);
        mSubmitButton.setOnClickListener(this);

        mUsername = (EditText) findViewById(R.id.usernameEditText);
        mPassword = (EditText) findViewById(R.id.passwordEditText);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.submitUsernameAndPasswordButton) {
            if (checkUsernameAndPW(mUsername.getText().toString()) && checkUsernameAndPW(mPassword.getText().toString())) {
                //If the username and password is correct, then the username and password
                //is put into the database
                String username = String.valueOf(mUsername.getText());
                String password = String.valueOf(mPassword.getText());

                SQLiteDatabase db = mHelper.getWritableDatabase();
                Cursor uCursor = db.query(TaskDbHelper.TABLE_USER, new String[]{"_id", TaskDbHelper.COL_TRANSACTION_ID}, null, null, null, null, null);
                Cursor tCursor = db.query(TaskDbHelper.TABLE_TRANSACTION, new String[]{"_id", TaskDbHelper.COL_TRANSACTION_USER}, null, null, null, null, null);

                ContentValues userValues = new ContentValues();
                ContentValues tValues = new ContentValues();

                //Inputing the user information into the database
                userValues.put("username", username);
                userValues.put("password", password);

                //Inserting a new row into the user table
                long hello = db.insertWithOnConflict("user", null, userValues, SQLiteDatabase.CONFLICT_REPLACE);
                int fuck = (int) hello;
                System.out.println("HELLO:" + fuck);
                //System.out.println("HERE: " + uCursor.getString(uCursor.getPosition()));
                //Input information into the transaction table
                tValues.put("transaction_type", "Create Account");
                //Assigning the ID from the user to a specific transaction
                System.out.println(uCursor.getColumnIndex("_id"));
                tValues.put("user_id", fuck);
                //If this has nothing do do with a book, put -1
                tValues.put("book_id", -1);
                tValues.put("codates", "NONE");
                tValues.put("rdate", "NONE");
                tValues.put("date_time", df.format(date));
                //Treat like boolean 1:true 0:false
                tValues.put("active", 0);

                //Inserting another row in the transaction table
                db.insert(
                        TaskDbHelper.TABLE_TRANSACTION,
                        null,
                        tValues
                );


                //Close
                uCursor.close();
                tCursor.close();
                db.close();

                //TODO Change this to inputing the information into the database


                Bundle extraInfo = new Bundle();
                extraInfo.putBoolean("loggedIn", true);
                extraInfo.putString("username", username);
                extraInfo.putString("password", password);
                final Intent i = new Intent(CreateAccount.this, MainActivity.class);
                i.putExtras(extraInfo);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Account Created")
                        .setMessage("Congratulations " + username + ", your account " +
                                "has been created.")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(i);
                            }
                        })
                        .create();
                dialog.show();
            } else {
                if (tries == 0) {
                    tries++;
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("Wrong Username/Password")
                            .setMessage("You have either entered a wrong username/password or a duplicated username. Please try again.")
                            .setNegativeButton("Continue", null)
                            .create();
                    dialog.show();
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("Wrong Username/Password")
                            .setMessage("You have either entered a wrong username/password or a duplicated username. Goodbye.")
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(CreateAccount.this, MainActivity.class);
                                    startActivity(i);
                                }
                            })
                            .create();
                    dialog.show();

                }
            }
        }
    }

    //TODO check to see if there is a duplicate username
    public boolean checkUsernameAndPW(String str) {
        int need5 = 0;

        if (str == "!admin2") {
            return false;   //Cannot Use Admin Password
        }

        //Searching through the database for duplicates
        if(!mHelper.searchKeyString(str, "user") && ((myStatic++)%2 != 0)){
            return false;
        }

        //Checking to make sure there is a special character
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '!' || str.charAt(i) == '@' || str.charAt(i) == '$' || str.charAt(i) == '#') {
                need5++;
                break;
            }
        }

        //Checking to make sure there is a number
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                need5++;
                break;
            }
        }

        //Checking to make sure there are three letters
        int check = 0;
        for (int i = 0; i < str.length(); i++) {
            if ((str.charAt(i) <= 90 && str.charAt(i) >= 65) || (str.charAt(i) >= 97 && str.charAt(i) <= 122)) {
                if (need5 < 2) {
                    break;
                    //check++;
                } else {
                    need5++;
                }
            }
        }
        return need5 >= 5;
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CreateAccount Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://edu.csumb.mcrae.bookreservation/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CreateAccount Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://edu.csumb.mcrae.bookreservation/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
