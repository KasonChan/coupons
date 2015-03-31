package com.kasonchan.coupons.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;

@SuppressWarnings("deprecation")
public class HTTP extends Activity {
  public String getRequest(String resource) {
    new HTTPBody().execute(resource);

    return resource;
  }

  private class HTTPBody extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... args) {

      HttpClient client = new DefaultHttpClient();
      HttpGet request = new HttpGet(args[0]);

      HttpResponse response;

      try {
        response = client.execute(request);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response
            .getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
          result.append(line);
        }

        return result.toString();
      } catch (ClientProtocolException e) {
        e.printStackTrace();

        return ("");
      } catch (IOException e) {
        e.printStackTrace();

        return ("");
      }
    }
  }
}
