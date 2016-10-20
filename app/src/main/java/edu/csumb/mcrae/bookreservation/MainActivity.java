package edu.csumb.mcrae.bookreservation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button mButton;
    private Button mCreateAccountButton;
    private Button mPlaceHoldButton;
    private Button mCancelHoldButton;
    private Button mManageSystemButton;
    private Button mLogInButton;
    private Button mLogOutButton;

    private TaskDbHelper mHelper;

    private boolean loggedIn;
    private String u;   //Username that is logged in
    private String p;   //Password that is logged in

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelper = new TaskDbHelper(this);

        //Getting extras for USER LOGGED IN
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            u = extras.getString("username");
            p = extras.getString("password");
            loggedIn = extras.getBoolean("loggedIn");
        }

        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.createAccountButton);
        mButton.setOnClickListener(this);

        mCreateAccountButton = (Button) findViewById(R.id.createAccountButton);
        mCreateAccountButton.setOnClickListener(this);

        mPlaceHoldButton = (Button) findViewById(R.id.placeHoldButton);
        mPlaceHoldButton.setOnClickListener(this);

        mCancelHoldButton = (Button) findViewById(R.id.cancelHoldButton);
        mCancelHoldButton.setOnClickListener(this);

        mManageSystemButton = (Button) findViewById(R.id.manageSystemButton);
        mManageSystemButton.setOnClickListener(this);

        mLogInButton = (Button) findViewById(R.id.logInButton);
        mLogInButton.setOnClickListener(this);

        mLogOutButton = (Button) findViewById(R.id.logOutButton);
        mLogOutButton.setOnClickListener(this);

        System.out.println("\nUser logged in: " + u + "\n");
    }

    public void onClick(View v){
        if(v.getId() == R.id.logInButton){
            if(u != null){
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Oops")
                        .setMessage("You are already logged in. Do you wish to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                u = p = null;
                                loggedIn = false;
                                Toast.makeText(MainActivity.this, "You have been logged off.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create();
                dialog.show();
            }
            else{
                final EditText uInput = new EditText(this);
                final EditText pInput = new EditText(this);

                Intent i = new Intent(this, LogIn.class);
                startActivity(i);
            }
        }
        else if(v.getId() == R.id.logOutButton){
            if(u == null){
                Toast.makeText(MainActivity.this, "You are already logged off", Toast.LENGTH_LONG).show();
            }
            else{
                u = p = null;
                loggedIn = false;
                Toast.makeText(MainActivity.this, "You have been logged off", Toast.LENGTH_LONG).show();
            }
        }
        else if(v.getId() == R.id.createAccountButton){
            if(u == null && p == null) {
                Intent i = new Intent(MainActivity.this, CreateAccount.class);
                startActivity(i);
            }
            else{
                Toast.makeText(MainActivity.this, "Please log off to make a new account.", Toast.LENGTH_LONG).show();
            }
        }
        else if(v.getId() == R.id.placeHoldButton){
            if(u != null && p != null) {
                Intent i = new Intent(this, PlaceHold.class);
                Bundle extraInfo = new Bundle();
                extraInfo.putString("username", u);
                extraInfo.putString("password", p);
                i.putExtras(extraInfo);
                startActivity(i);
            }
            else{
                Toast.makeText(MainActivity.this, "Please log in first.", Toast.LENGTH_LONG).show();
            }
        }
        else if(v.getId() == R.id.cancelHoldButton){

        }
        //Manage system button. Do not need a bundle because you should only use this when you are not logged in
        else if(v.getId() == R.id.manageSystemButton){
            if(loggedIn && u != "!admin2"){
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("You are not the System Manager. You can either log out or continue.")
                        .setPositiveButton("Continue", null)
                        .setNegativeButton("Log-out", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                u = p = null;
                                loggedIn = false;
                                Intent i = new Intent(MainActivity.this, ManageSystem.class);
                                startActivity(i);
                            }
                        })
                        .create();
                dialog.show();
                return;
            }
            Intent i = new Intent(this, ManageSystem.class);
            startActivity(i);
        }
    }
}
