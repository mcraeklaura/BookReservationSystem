package edu.csumb.mcrae.bookreservation;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ManageSystem extends AppCompatActivity implements View.OnClickListener{
    private Button mSubmitButton;
    private EditText mUsername;
    private EditText mPassword;

    public int tries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tries = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_system);

        mSubmitButton = (Button) findViewById(R.id.msSubmitButton);
        mSubmitButton.setOnClickListener(this);

        mUsername = (EditText) findViewById(R.id.msUsername);
        mPassword = (EditText) findViewById(R.id.msPassword);
    }

    public void onClick(View v){
        if(v.getId() == R.id.msSubmitButton){
            if((mUsername.getText().toString().equals("!admin2")) && mPassword.getText().toString().equals("!admin2")){
                Intent i = new Intent(ManageSystem.this, MSManager.class);    //TODO Go to the manage system activity
                startActivity(i);
            }
            else{
                System.out.println(mUsername.getText().toString());
                System.out.println(mPassword.getText().toString());

                if(tries == 1){
                    Intent i = new Intent(ManageSystem.this, MainActivity.class);
                    startActivity(i);
                }
                else {
                    tries++;
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Wrong Administrator username or password.")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                }
            }
        }
    }
}
