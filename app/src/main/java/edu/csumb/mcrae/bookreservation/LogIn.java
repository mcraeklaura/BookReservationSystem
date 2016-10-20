package edu.csumb.mcrae.bookreservation;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends AppCompatActivity implements View.OnClickListener{
    private TaskDbHelper mHelper;
    private Button mSubmit;
    private EditText mUsername;
    private EditText mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mSubmit = (Button) findViewById(R.id.logInSubmitButton);
        mSubmit.setOnClickListener(this);

        mUsername = (EditText) findViewById(R.id.logInUsername);
        mUsername.setOnClickListener(this);

        mPassword = (EditText) findViewById(R.id.logInPassword);
        mPassword.setOnClickListener(this);

        mHelper = new TaskDbHelper(this);
    }

    public void onClick(View v){
        if(v.getId() == R.id.logInSubmitButton){
            final String u = mUsername.getText().toString();
            final String p = mPassword.getText().toString();

            boolean flag = mHelper.usernameAndPassword(u,p);

            if(flag){
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Logged in")
                        .setMessage("You have been successfully logged in.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                Intent i = new Intent(LogIn.this, MainActivity.class);
                                Bundle extraInfo = new Bundle();
                                extraInfo.putString("username", u);
                                extraInfo.putString("password", p);
                                extraInfo.putBoolean("loggedIn", true);
                                i.putExtras(extraInfo);
                                startActivity(i);
                            }
                        })
                        .create();
                dialog.show();
            }
        }
    }
}
