package com.example.hugh.cryptoalert;

/**
 * Created by hugh on 11/03/19.
 */

public class HTTPResponse {
    private float high, last, timestamp, bid, vwap, vol, ask, open;

    HTTPResponse(){

    }

    HTTPResponse(float ask, float bid){
        this.ask = ask;
        this.bid = bid;
    }

    HTTPResponse(float high, float last, float timestamp, float bid,
                 float vwap, float vol, float ask, float open){
        this.high = high;
        this.last = last;
        this.timestamp = timestamp;
        this.bid = bid;
        this.vwap = vwap;
        this.vol = vol;
        this.ask = ask;
        this.open = open;
    }

    public float getAsk() {
        return ask;
    }

    public float getBid() {
        return bid;
    }
}
