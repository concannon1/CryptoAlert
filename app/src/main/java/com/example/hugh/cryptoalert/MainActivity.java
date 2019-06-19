package com.example.hugh.cryptoalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.VibrationEffect;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.Toast;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    float alertValue;
    TextView alertValTextView;
    TextView bitcoinValue;
    Button okButton;
    EditText userInput;
    boolean showAlert = false;
    Vibrator v;
    //exchangeURL = "https://www.bitstamp.net/api/v2/ticker/btceur/";
    private BroadcastReceiver listener;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        bitcoinValue = findViewById(R.id.bitcoin_value);
        //set this to empty while obtaining data after opening the app
        bitcoinValue.setText("");
        userInput = findViewById(R.id.editText);
        okButton = findViewById(R.id.ok_button);

        loadSavedAlertValue();
        serviceIntent = new Intent(getApplicationContext(), CheckBTCPrice.class);
        startService(serviceIntent);

        //updates the price onscreen, and alerts user if BTC value is within tolerance of the value they've set
        listener = new BroadcastReceiver() {
            @Override
            public void onReceive( Context context, Intent intent ) {
                String data = intent.getStringExtra("service message");
                bitcoinValue.setText("€" + data);
                float latestValue = Float.parseFloat(data);
                //tolerance of €2
                if(alertValue >= latestValue - 1 && alertValue <= latestValue + 1){
                    showAlert = true;
                    // Vibrate for 500 milliseconds
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(500);
                    }
                    showToast("Bitcoin has reached " + latestValue + ". Press OK to mute");
                }
            }
        };

        //listen for user to click OK, then save that value
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the phone is notifying and the user has pressed ok to mute
                if(showAlert){
                    showAlert = false;
                    saveAlertValue("0");
                }
                //user is just changing alert value
                else{
                    saveAlertValue(userInput.getText().toString());
                    showToast("You will be alerted when Bitcoin reaches €" + userInput.getText().toString());
                }
            }
        });

    }

    private void showToast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    //stores value as "storedVal" in sharedPreferences object
    private void loadSavedAlertValue(){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        String storedAlertValue = sharedPreferences.getString("storedVal", "0");
        userInput.setText(storedAlertValue);
        alertValue = Float.parseFloat(storedAlertValue);

    }

    /* set up for broadcast listener that listens to CheckBTCPrice service */
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((listener),
                new IntentFilter("service result"));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
        super.onStop();
    }

    //runs when user hits OK button, saves the value
    private void saveAlertValue(String alertVal){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        Editor editor = sharedPreferences.edit();
        editor.putString("storedVal", alertVal);
        editor.commit();
        alertValue = Float.parseFloat(alertVal);
        userInput.setText(alertVal);
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

