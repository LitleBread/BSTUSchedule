package com.example.bstuschedule;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

interface PostExecutable{
    void onPostExecute(String html);
}
public class HTMLClient {
    public String requestMethod;
    public int timeout;
    public String charset;

    public HTMLClient(String requestMethod, int timeout, String
            charset) {
        this.requestMethod = requestMethod;
        this.timeout = timeout;
        this.charset = charset;
    }

    public void get(final String address, final PostExecutable obj) throws IOException {

        Log.i("download request", address);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                try
                {
                    URL url = new URL(address);
                    HttpURLConnection c = (HttpURLConnection)url.openConnection();
                    c.setRequestMethod(requestMethod);
                    c.setReadTimeout(timeout);
                    c.connect();
                    reader = new BufferedReader(new
                            InputStreamReader(c.getInputStream(), charset));
                    StringBuilder buf = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        buf.append(line).append("\n");
                    }
                    reader.close();

                    c.disconnect();
                    Log.i("dowloaded", String.valueOf(buf.toString().length()));
                    obj.onPostExecute(buf.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        t.start();
        try{
            t.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




}


