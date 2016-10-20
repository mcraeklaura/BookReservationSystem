package edu.csumb.mcrae.bookreservation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Time;
import java.util.Date;

public class PlaceHold extends AppCompatActivity implements View.OnClickListener{
    //Dates
    private EditText mPickUpDate;
    private EditText mDropOffDate;
    //Times
    private EditText mPickUpTime;
    private EditText mDropOffTime;

    private Button mSubmitButton;

    private String u;
    private String p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_hold);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            u = extras.getString("username");
            p = extras.getString("password");
        }

        mSubmitButton = (Button) findViewById(R.id.placeHoldSubmit);
        mSubmitButton.setOnClickListener(this);

        mPickUpDate = (EditText) findViewById(R.id.pickUpDate);
        mPickUpDate.setOnClickListener(this);

        mDropOffDate = (EditText) findViewById(R.id.dropOffDate);
        mDropOffDate.setOnClickListener(this);

        mPickUpTime = (EditText) findViewById(R.id.pickUpTime);
        mPickUpTime.setOnClickListener(this);

        mDropOffTime = (EditText) findViewById(R.id.dropOffTime);
        mDropOffTime.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v.getId() == R.id.placeHoldSubmit){
            //TODO Need to verify dates and times are not overlapping or in the past
            String pickD = mPickUpDate.getText().toString();
            String pickT = mPickUpTime.getText().toString();
            String dropD = mDropOffDate.getText().toString();
            String dropT = mDropOffTime.getText().toString();

            boolean flag = verifyDateTime(pickD, pickT, dropD, dropT);

            if(flag){
                Intent i = new Intent(PlaceHold.this, ReserveBook.class);
                Bundle extraInfo = new Bundle();
                extraInfo.putString("username", u);
                extraInfo.putString("password", p);
                extraInfo.putString("pickupdate", pickD);
                extraInfo.putString("pickuptime", pickT);
                extraInfo.putString("dropoffdate", dropD);
                extraInfo.putString("dropofftime", dropT);
                i.putExtras(extraInfo);
                startActivity(i);
            }
            else{Toast.makeText(PlaceHold.this, "Invalid date/time", Toast.LENGTH_LONG).show();}
        }
    }

    public boolean verifyDateTime(String pickD, String pickT, String dropD, String dropT){
        int month1 = 0, month2 = 0, date1 = 0, date2 = 0, year1 = 0, year2 = 0;
        int hour1 = 0, hour2 = 0, minute1 = 0, minute2 = 0;
        boolean dates = false, times = false;
        //Making sure the date is in the right format
        if(pickD.length() != 10 || dropD.length() != 10 || pickT.length() !=5 || dropT.length() != 5){
            return false;
        }
        else{
            try {
                month1 = Integer.parseInt(pickD.substring(0, 2));
                month2 = Integer.parseInt(dropD.substring(0, 2));

                date1 = Integer.parseInt(pickD.substring(3, 5));
                date2 = Integer.parseInt(dropD.substring(3, 5));

                year1 = Integer.parseInt(pickD.substring(6));
                year2 = Integer.parseInt(dropD.substring(6));

                hour1 = Integer.parseInt(pickT.substring(0, 2));
                hour2 = Integer.parseInt(dropT.substring(0, 2));

                minute1 = Integer.parseInt(pickT.substring(3));
                minute2 = Integer.parseInt(dropT.substring(3));
            }catch(Exception e){
                Toast.makeText(PlaceHold.this, "You input either the time or date in an incorrect format.", Toast.LENGTH_LONG).show();
            }

            //Checking that the dates are valid and they are at the most 7 days apart
            if((month1 > 12 || month1 < 1) || (month2 > 12 || month2 < 1) || year1 < 2016 || year2 < 2016 || (date1 < 1 || date1 > 31) || (date2 < 1 || date2 > 31) ){
                return false;
            }

            //Checking the times
            if((hour1 > 24 || hour1 < 0) || (hour2 > 24 || hour2 < 0) || (minute1 > 59 || minute1 < 0) || (minute2 > 59 || minute2 < 0) ){
                return false;
            }

            //Checking the months of the year with how many dates they have
            if(month1 == 1){if(date1 > 31){return false;}} if(month2 == 1){if(date2 > 31){return false;}}   //January : 31
            if(month1 == 2){if(date1 > 28){return false;}} if(month2 == 2){if(date2 > 28){return false;}}   //February : 28 I am assuming there are no leap years
            if(month1 == 3){if(date1 > 31){return false;}} if(month2 == 3){if(date2 > 31){return false;}}   //March : 31
            if(month1 == 4){if(date1 > 30){return false;}} if(month2 == 4){if(date2 > 30){return false;}}   //April : 30
            if(month1 == 5){if(date1 > 31){return false;}} if(month2 == 5){if(date2 > 31){return false;}}   //May : 31
            if(month1 == 6){if(date1 > 30){return false;}} if(month2 == 6){if(date2 > 30){return false;}}   //June : 30
            if(month1 == 7){if(date1 > 31){return false;}} if(month2 == 7){if(date2 > 31){return false;}}   //July : 31
            if(month1 == 8){if(date1 > 31){return false;}} if(month2 == 8){if(date2 > 31){return false;}}   //August : 31
            if(month1 == 9){if(date1 > 30){return false;}} if(month2 == 9){if(date2 > 30){return false;}}   //September : 30
            if(month1 == 10){if(date1 > 31){return false;}} if(month2 == 10){if(date2 > 31){return false;}} //October : 31
            if(month1 == 11){if(date1 > 30){return false;}} if(month2 == 11){if(date2 > 30){return false;}} //November : 30
            if(month1 == 12){if(date1 > 31){return false;}} if(month2 == 12){if(date2 > 31){return false;}} //December : 31

            int count = 0;
            System.out.println("Month1: " + month1);
            System.out.println("Month2: " + month2);
            System.out.println("Date1: " + date1);
            System.out.println("Date2: " + date2);
            System.out.println("Year1: " + year1);
            System.out.println("Year2: " + year2);

            System.out.println("Hour1: " + hour1);
            System.out.println("Hour2: " + hour2);
            System.out.println("Minutes1: " + minute1);
            System.out.println("Minute2: " + minute2);
            if(year1 < year2 && year2 != year1 + 1){
                System.out.println(1);    //1
                return false;
            }
            else{
                if(year2 > year1 && (year2 == year1 + 1)){
                    //EXAMPLE 12/30/16 - 1/03/17
                    if(month1 != 12 || month2 != 1){
                        System.out.println(2);    //2
                        return false;
                    }
                    if(date1 - date2 < 24){System.out.println(3);return false;}   //3
                    else{
                        //Check the time
                        if(date2 - date1 == 24){
                            if(hour2 > hour1){
                                if(minute2 > minute1){System.out.println(4);return false;}    //4
                                else{return true;}}
                            else{return true;}
                        }
                    }
                }
                else {
                    if (year1 == year2 && (month1 + 1 == month2|| month1 == month2)) {
                        if (month1 < month2) {
                            System.out.println(5);    //5
                            return false;
                        } else {
                            if (month1 > month2 && (month1 == month2 + 1)) {
                                //EXAMPLE 1/30/16 - 2/02/16
                                if (month1 == 1 || month1 == 3 || month1 == 5 || month1 == 7 || month1 == 8 || month1 == 10 || month1 == 1) {
                                    if ((31 - date1) + date2 > 7) {
                                        System.out.println(6);    //6
                                        return false;
                                    } else {
                                        //Check time
                                        if ((31 - date1) + date2 == 7) {
                                            if (hour1 == hour2) {
                                                if (minute2 > minute1) {
                                                    System.out.println(7);    //7
                                                    return false;
                                                } else {
                                                    System.out.println(8);    //8
                                                    return false;
                                                }
                                            } else if (hour1 > hour2 && hour1 == hour2 + 1) {
                                                return true;
                                            } else {
                                                System.out.println(9);    //9
                                                return false;
                                            }
                                        }
                                    }
                                } else if (month1 == 4 || month1 == 6 || month1 == 9 || month1 == 11) {
                                    if ((30 - date1) + date2 > 7) {
                                        System.out.println(10);    //10
                                        return false;
                                    } else {
                                        //Check time
                                        if ((30 - date1) + date2 == 7) {
                                            if (hour1 == hour2) {
                                                if (minute2 > minute1) {
                                                    System.out.println(11);    //11
                                                    return false;
                                                } else {
                                                    System.out.println(12);    //12
                                                    return false;
                                                }
                                            } else if (hour1 > hour2 && hour1 == hour2 + 1) {
                                                return true;
                                            } else {
                                                System.out.println(13);    //13
                                                return false;
                                            }
                                        }
                                    }
                                } else {
                                    if ((28 - date1) + date2 > 7) {
                                        System.out.println(14);    //14
                                        return false;
                                    } else {
                                        //Check time
                                        if ((28 - date1) + date2 == 7) {
                                            if (hour1 == hour2) {
                                                if (minute2 > minute1) {
                                                    System.out.println(15);    //15
                                                    return false;
                                                } else {
                                                    System.out.println(16);    //16
                                                    return false;
                                                }
                                            } else if (hour1 > hour2 && hour1 == hour2 + 1) {
                                                return true;
                                            } else {
                                                System.out.println(17);    //17
                                                return false;
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (month1 == month2) {
                                    if (date1 > date2) {
                                        System.out.println(18);    //18
                                        return false;
                                    } else {
                                        if (date2 - date1 <= 7) {
                                            if (date2 - date1 == 7) {
                                                //Check times
                                                if (hour2 > hour1) {
                                                    System.out.println(19);    //19
                                                    return false;
                                                } else {
                                                    if (hour2 == hour1) {
                                                        if (minute2 > minute1) {
                                                            System.out.println(20);    //20
                                                            return false;
                                                        } else {
                                                            System.out.println(21);    //21
                                                            return false;
                                                        }
                                                    } else {
                                                        System.out.println(22);    //22
                                                        return false;
                                                    }
                                                }
                                            } else {

                                                return true;
                                            }
                                        } else {
                                            System.out.println(23);    //23
                                            return false;
                                        }
                                        //Check times
                                        //dates = true;
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println(24); //24
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
