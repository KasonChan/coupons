package com.kasonchan.coupons;

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
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class CouponActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Display coupon layout
    setContentView(R.layout.coupon);

    TextView couponUsername = (TextView) findViewById(R.id.coupon_username);

    // Get username from login or signup page
    String username = this.getIntent().getStringExtra("username");

    // If username is not passed from previous intent, hide the username
    // Otherwise, show username
    if (username.isEmpty()) {
      couponUsername.setVisibility(View.GONE);
    } else {
      couponUsername.setText("@" + username);
    }

    // Resource
    String resource = "http://api.bluepromocode.com/v2/promotions";

    // Get request from the resource
    new GetRequest().execute(resource);
  }

  /**
   * GetRequest class extends AsyncTask enables proper and easy use of the UI
   * thread. This class allows to perform background operations and publish
   * results on the UI thread without having to manipulate threads and/or
   * handlers.
   */
  private class GetRequest extends AsyncTask<String, Void, String> {

    TextView coupon = (TextView) findViewById(R.id.coupon_text);

    @Override
    protected String doInBackground(String... args) {

      // Create new client, build http get request with argument url
      HttpClient client = new DefaultHttpClient();
      HttpGet request = new HttpGet(args[0]);
      HttpResponse response;

      try {
        // Execute request
        response = client.execute(request);

        // Append request to result string
        BufferedReader rd = new BufferedReader(new InputStreamReader(response
            .getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
          result.append(line);
        }

        return result.toString();
      } catch (ClientProtocolException e) {
        // Catch client protocol exception
        e.printStackTrace();
        return ("ClientProtocolExecption");
      } catch (IOException e) {
        // Catch io exception
        e.printStackTrace();
        return ("IOExeption");
      }
    }

    protected void onPostExecute(String result) {
      // After get request is executed, set coupon text to result
      coupon.setText(result);
    }
  }
}
