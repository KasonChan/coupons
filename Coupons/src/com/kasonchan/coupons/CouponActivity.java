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
import android.util.Log;
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

    // // Resource
    String resource = "http://api.bluepromocode.com/v2/promotions";

    // TODO: Finish up get authentication
    // Login resource for authentication
    final String[] resourcePersonalized = new String[3];
    resourcePersonalized[0] = "http://api.bluepromocode.com/v2/users/self/promotions/suggestions";
    resourcePersonalized[1] = this.getIntent().getStringExtra("username").toString();
    resourcePersonalized[2] = this.getIntent().getStringExtra("password").toString();

    // Get request from the resource
    new GetRequest().execute(resource);

    // Get personalized coupon from the resource
    new GetPersonalized().execute(resourcePersonalized);
  }

  /**
   * GetRequest class extends AsyncTask enables proper and easy use of the UI
   * thread. This class allows to perform background operations and publish
   * results on the UI thread without having to manipulate threads and/or
   * handlers.
   */
  private class GetRequest extends AsyncTask<String, Void, String> {

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

      final ArrayList<HashMap<String, String>> couponList = new ArrayList<HashMap<String, String>>();
      ;

      try {
        final JSONObject jsonObj = new JSONObject(result);

        final JSONArray coupons = jsonObj.getJSONArray(PROMOTIONS);

        for (int i = 0; i < coupons.length(); i++) {
          JSONObject coupon = coupons.getJSONObject(i);

          // Get coupon description
          final JSONObject savings = coupon.getJSONObject(SAVINGS);

          if (savings.has(PERCENT_OFF) == true) {
            // Get merchant name
            final JSONObject merchant = coupon.getJSONObject(MERCHANT);
            final String merchantName = merchant.getString(MERCHANT_NAME);

            // Get followsCount
            final int followsCount = coupon.getInt(FOLLOWS_COUNT);

            // If percentOff exists, save it and types
            final Double percentOff = savings.getDouble(PERCENT_OFF);

            String description = percentOff + "%" + " Off";

            Log.i("percentOff", merchantName + " " + description + " "
                + followsCount);

            // Create temp coupon
            HashMap<String, String> tempCoupon = new HashMap<String, String>();

            // Save json datas to new coupon
            tempCoupon.put(MERCHANT_NAME, merchantName);
            tempCoupon.put(DESCRIPTION, description);
            tempCoupon.put(FOLLOWS_COUNT, Integer.toString(followsCount));

            // Save temp coupon to the coupon list
            couponList.add(tempCoupon);

          } else if (savings.has(AMOUNT_OFF) == true) {
            // Get merchant name
            final JSONObject merchant = coupon.getJSONObject(MERCHANT);
            final String merchantName = merchant.getString(MERCHANT_NAME);

            // Get followsCount
            final int followsCount = coupon.getInt(FOLLOWS_COUNT);

            // If amountOff exists, save it and types
            Double amountOff = savings.getDouble(AMOUNT_OFF);

            final String description = "$" + amountOff + " Off";

            Log.i("amountOff", merchantName + " " + description + " "
                + followsCount);

            // Create temp coupon
            HashMap<String, String> tempCoupon = new HashMap<String, String>();

            // Save json datas to new coupon
            tempCoupon.put(MERCHANT_NAME, merchantName);
            tempCoupon.put(DESCRIPTION, description);
            tempCoupon.put(FOLLOWS_COUNT, Integer.toString(followsCount));

            // Save temp coupon to the coupon list
            couponList.add(tempCoupon);
          } else {
            // Get merchant name
            final JSONObject merchant = coupon.getJSONObject(MERCHANT);
            final String merchantName = merchant.getString(MERCHANT_NAME);

            // Get followsCount
            final int followsCount = coupon.getInt(FOLLOWS_COUNT);

            // If the doesn't contain either of both
            final JSONArray typeArray = savings.getJSONArray(TYPES);

            String types = "";

            if (typeArray.length() > 0)
              for (int t = 0; t < typeArray.length(); t++) {
                String type = typeArray.getString(t);
                if (t == 0)
                  types = type;
                else
                  types = types + "\n" + type;
              }

            final String description = types;

            Log.i("others", merchantName + " " + description + " "
                + followsCount);

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

        // Log couponList
        Log.i("printing", String.valueOf(couponList.size()));
        Log.i("printing", couponList.toString());

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

  /**
   * GetPersonalized class extends AsyncTask enables proper and easy use of the
   * UI thread. This class allows to perform background operations and publish
   * results on the UI thread without having to manipulate threads and/or
   * handlers.
   */
  private class GetPersonalized extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... args) {

      // Create new client, build http get request with argument url
      HttpClient client = new DefaultHttpClient();
      HttpGet request = new HttpGet(args[0]);

      // Create credentials and then add to request
      // TODO: Finish up get authentication

      HttpResponse response;

      try {
        // Execute request
        response = client.execute(request);

        Log.i("RESPONSE", response.toString());

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

      final ArrayList<HashMap<String, String>> couponList = new ArrayList<HashMap<String, String>>();
      ;

      try {
        final JSONObject jsonObj = new JSONObject(result);

        final JSONArray coupons = jsonObj.getJSONArray(PROMOTIONS);

        for (int i = 0; i < coupons.length(); i++) {
          JSONObject coupon = coupons.getJSONObject(i);

          // Get coupon description
          final JSONObject savings = coupon.getJSONObject(SAVINGS);

          if (savings.has(PERCENT_OFF) == true) {
            // Get merchant name
            final JSONObject merchant = coupon.getJSONObject(MERCHANT);
            final String merchantName = merchant.getString(MERCHANT_NAME);

            // Get followsCount
            final int followsCount = coupon.getInt(FOLLOWS_COUNT);

            // If percentOff exists, save it and types
            final Double percentOff = savings.getDouble(PERCENT_OFF);

            String description = percentOff + "%" + " Off";

            Log.i("percentOff", merchantName + " " + description + " "
                + followsCount);

            // Create temp coupon
            HashMap<String, String> tempCoupon = new HashMap<String, String>();

            // Save json datas to new coupon
            tempCoupon.put(MERCHANT_NAME, merchantName);
            tempCoupon.put(DESCRIPTION, description);
            tempCoupon.put(FOLLOWS_COUNT, Integer.toString(followsCount));

            // Save temp coupon to the coupon list
            couponList.add(tempCoupon);

          } else if (savings.has(AMOUNT_OFF) == true) {
            // Get merchant name
            final JSONObject merchant = coupon.getJSONObject(MERCHANT);
            final String merchantName = merchant.getString(MERCHANT_NAME);

            // Get followsCount
            final int followsCount = coupon.getInt(FOLLOWS_COUNT);

            // If amountOff exists, save it and types
            Double amountOff = savings.getDouble(AMOUNT_OFF);

            final String description = "$" + amountOff + " Off";

            Log.i("amountOff", merchantName + " " + description + " "
                + followsCount);

            // Create temp coupon
            HashMap<String, String> tempCoupon = new HashMap<String, String>();

            // Save json datas to new coupon
            tempCoupon.put(MERCHANT_NAME, merchantName);
            tempCoupon.put(DESCRIPTION, description);
            tempCoupon.put(FOLLOWS_COUNT, Integer.toString(followsCount));

            // Save temp coupon to the coupon list
            couponList.add(tempCoupon);
          } else {
            // Get merchant name
            final JSONObject merchant = coupon.getJSONObject(MERCHANT);
            final String merchantName = merchant.getString(MERCHANT_NAME);

            // Get followsCount
            final int followsCount = coupon.getInt(FOLLOWS_COUNT);

            // If the doesn't contain either of both
            final JSONArray typeArray = savings.getJSONArray(TYPES);

            String types = "";

            if (typeArray.length() > 0)
              for (int t = 0; t < typeArray.length(); t++) {
                String type = typeArray.getString(t);
                if (t == 0)
                  types = type;
                else
                  types = types + "\n" + type;
              }

            final String description = types;

            Log.i("others", merchantName + " " + description + " "
                + followsCount);

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

        // Log couponList
        Log.i("printing", String.valueOf(couponList.size()));
        Log.i("printing", couponList.toString());

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
