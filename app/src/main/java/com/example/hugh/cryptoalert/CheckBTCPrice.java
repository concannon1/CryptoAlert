package com.example.hugh.cryptoalert;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*all this class does is check the price of BTC every 3 seconds, and broadcasts it to the main activity*/

public class CheckBTCPrice extends Service {
    public CheckBTCPrice() {
    //    super("CheckBTCPrice");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
//        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate(){
        Log.i("create tag", "created");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        Thread thread = new Thread(){
            public void run(){
                while (true) {
                    try{
                        Thread.sleep(3000);
                        // Send local broadcast
                        sendResult(getBTCinEUR());
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void sendResult(String val){
                Intent intent = new Intent("service result");
                if(val != null){
                    intent.putExtra("service message", val);
                }
                localBroadcastManager.sendBroadcast(intent);
            }
        };
        thread.start();
        stopSelf();
        return super.onStartCommand(intent, flags, startID);
    }

    public static String getBTCinEUR(){
        String url = "https://www.bitstamp.net/api/v2/ticker/btceur/";
        try {

            HttpGetRequest getRequest = new HttpGetRequest();
            String data = getRequest.execute(url).get();
            //takes in a JSON object in string form
            //converts it to a HTTPResponse object
            //returns the Ask attribute of said object - the lowest price BTC is available for
            Gson gson = new Gson();
            HTTPResponse response = gson.fromJson(data, HTTPResponse.class);
            return String.valueOf(response.getAsk());
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

    }

}
