package com.example.hugh.cryptoalert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Vibrator;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    float alertValue;
    private Intent serviceIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView bitcoinValue = findViewById(R.id.bitcoin_value);
      //  final TextView displayAlertValue = findViewById(R.id.displayAlertValue);
      //  displayAlertValue.setText("");
        final Button okButton = findViewById(R.id.ok_button);
        String latestValue = "no data obtained for some reason";
        String url;
        serviceIntent = new Intent(getApplicationContext(), CheckBTCPrice.class);
        startService(serviceIntent);
        try {
            url = "https://www.bitstamp.net/api/v2/ticker/btceur/";

            HttpGetRequest getRequest = new HttpGetRequest();
            latestValue = getRequest.execute(url).get();
            Log.i("tag2", latestValue);
            latestValue = getBTCinEUR(latestValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String s = CheckBTCPrice.getBTCinEUR();
        bitcoinValue.setText("€"+latestValue);
        //get the user's previously saved alert value, if it exists
/*
        SharedPreferences sp = getSharedPreferences("Alert Value", 0);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("Alert Value", (float)0.1);
        System.out.println(sp.getFloat("Alert Value", 0));

        /*
        Context context = this;
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
   //     editor.putfloat(getString(R.xml.preferences.);
     //
        //
        // editor.commit();
*/
        /*
        final EditText userInput = findViewById(R.id.editText2);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertValue = Float.parseFloat(userInput.getText().toString());
                editor.putFloat(getString(R.string.Alert_Value));
                displayAlertValue.setText("You will be alerted at €"+alertValue);
                System.out.println("Set value to " + alertValue);
            }
        });
        */

    }

    //takes in a JSON object in string form
    //converts it to a HTTPResponse object
    //returns the Ask attribute of said object - the lowest price BTC is available for
    public String getBTCinEUR(String data){
        Gson gson = new Gson();

        HTTPResponse response = gson.fromJson(data, HTTPResponse.class);

        return String.valueOf(response.getAsk());
    }
}

