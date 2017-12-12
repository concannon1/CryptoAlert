package com.example.hugh.cryptoalert;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Vibrator;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Testing 1 - Send Http GET request");
        final TextView t = findViewById(R.id.bitcoin_value);
        String latestValue = "no data obtained for some reason";
        String url;
        try {
            url = "https://www.bitstamp.net/api/v2/ticker/btceur/";

            HttpGetRequest getRequest = new HttpGetRequest();
            latestValue = getRequest.execute(url).get();
            latestValue = parseJSON(latestValue);
            latestValue = getBTCinEUR(latestValue);


        } catch (Exception e) {
            e.printStackTrace();
        }

        t.setText(latestValue);

        //TODO: ADD VIBRATOR :^)

    }

    public static String sendGET(URL url) throws Exception{
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        for(int i = 0; i < 400; i++)System.out.print("uhhhhh");
        String prettyResults = parseJSON(response.toString());

        return(getBTCinEUR(parseJSON(response.toString())));



    }

    public static String parseJSON(String json){
        //replace all commas followed by spaces with newline
        String parsed = json.replaceAll(", ", "\n");
        //replace all curly braces and double quotes with nothing
        parsed = parsed.replaceAll("\\{|\\}|\"", "");
        return parsed;
    }

    public static String getBTCinEUR(String data){
        //hideous regex coming up, only works when you get data from bitstamp.net

        //gets rid of everything in the JSON except for "last", which is the latest value of bitcoin
        String s = data.replaceAll("high: .*\\n", "");
        s = s.replaceAll("timestamp:.*\\n", "");
        s = s.replaceAll("bid:.*", "");//[0-9]+\\.[0-9]{2}\\n", "");
        s = s.replaceAll("vwap:.*\\n", "");//[0-9]+\\.[0-9]{2}\\n", "");
        s = s.replaceAll("volume:.*\\n", "");//[0-9]+\\.[0-9]{2}\\n", "");
        s = s.replaceAll("low:.*\\n", "");//[0-9]+\\.[0-9]{2}\\n", "");
        s = s.replaceAll("ask:.*\\n", "");//[0-9]+\\.[0-9]{2}\\n", "");
        s = s.replaceAll("open:.*", "");//[0-9]+\\.[0-9]{2}\\n", "");
        s = s.replaceAll("last: ", "€");

        return s;

    }
}


