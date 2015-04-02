package com.kasonchan.coupons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class CouponActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Display coupon layout
    setContentView(R.layout.coupons);

    TextView couponUsername = (TextView) findViewById(R.id.coupons_username);

    // Get username from login or signup page
    try {
      String username = this.getIntent().getStringExtra("username");

      // If username is not passed from previous intent, hide the username
      // Otherwise, show username
      if (username.isEmpty()) {
        couponUsername.setVisibility(View.GONE);
      } else {
        couponUsername.setText("@" + username);
      }
    } catch (Exception e) {

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

    // TextView coupon = (TextView) findViewById(R.id.coupon_text);

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

      final String PROMOTIONS = "promotions";
      final String SAVINGS = "savings";
      final String PERCENT_OFF = "percentOff";
      final String AMOUNT_OFF = "amountOff";
      final String TYPES = "types";
      final String MERCHANT = "merchant";
      final String MERCHANT_NAME = "name";
      final String FOLLOWS_COUNT = "followsCount";
      final String DESCRIPTION = "description";

      ArrayList<HashMap<String, String>> couponList = new ArrayList<HashMap<String, String>>();
      ;
      JSONObject merchant;
      String merchantName;
      JSONObject savings;
      Double percentOff;
      Double amountOff;
      JSONArray typeArray;
      String types;
      String description;
      int followsCount;

      try {
        JSONObject jsonObj = new JSONObject(result);

        JSONArray coupons = jsonObj.getJSONArray(PROMOTIONS);

        for (int i = 0; i < coupons.length() - 1; i++) {
          JSONObject coupon = coupons.getJSONObject(i);

          // Get merchant name
          merchant = coupon.getJSONObject(MERCHANT);
          merchantName = merchant.getString(MERCHANT_NAME);

          // Get followsCount
          followsCount = coupon.getInt(FOLLOWS_COUNT);

          // Get coupon description
          savings = coupon.getJSONObject(SAVINGS);

          if (savings.has(PERCENT_OFF) == true) {
            // If percentOff exists, save it and types
            percentOff = savings.getDouble(PERCENT_OFF);

            description = percentOff + "%" + " Off";

            // Create temp coupon
            HashMap<String, String> tempCoupon = new HashMap<String, String>();

            // Save json datas to new coupon
            tempCoupon.put(MERCHANT_NAME, merchantName);
            tempCoupon.put(DESCRIPTION, description);
            tempCoupon.put(FOLLOWS_COUNT, Integer.toString(followsCount));

            // Save temp coupon to the coupon list
            couponList.add(tempCoupon);

          } else if (savings.has(AMOUNT_OFF) == true) {
            // If amountOff exists, save it and types
            amountOff = savings.getDouble(AMOUNT_OFF);

            description = "$" + amountOff + " Off";

            // Create temp coupon
            HashMap<String, String> tempCoupon = new HashMap<String, String>();

            // Save json datas to new coupon
            tempCoupon.put(MERCHANT_NAME, merchantName);
            tempCoupon.put(DESCRIPTION, description);
            tempCoupon.put(FOLLOWS_COUNT, Integer.toString(followsCount));

            // Save temp coupon to the coupon list
            couponList.add(tempCoupon);
          } else {
            // If the doesn't contain either of both
            typeArray = savings.getJSONArray(TYPES);

            types = "";
            description = "";

            for (int t = 0; t < typeArray.length(); t++) {
              String type = typeArray.getString(t);
              types = types + " " + type;
            }

            description = types;

            // Create temp coupon
            HashMap<String, String> tempCoupon = new HashMap<String, String>();

            // Save json datas to new coupon
            tempCoupon.put(MERCHANT_NAME, merchantName);
            tempCoupon.put(DESCRIPTION, description);
            tempCoupon.put(FOLLOWS_COUNT, Integer.toString(followsCount));

            // Save temp coupon to the coupon list
            couponList.add(tempCoupon);
          }
        }

        // Show coupons
        // Create grid adaptor and set coupon grid
        GridAdaptor adapter = new GridAdaptor(CouponActivity.this, couponList);
        GridView grid = (GridView) findViewById(R.id.coupons_grid);
        grid.setAdapter(adapter);

      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
