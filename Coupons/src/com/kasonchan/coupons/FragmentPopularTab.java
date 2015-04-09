package com.kasonchan.coupons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.app.Fragment;

@SuppressWarnings("deprecation")
public class FragmentPopularTab extends Fragment {

  // Set up client and context
  private DefaultHttpClient client;
  private BasicHttpContext context;

  // Resource
  private String resource = "http://api.bluepromocode.com/v2/promotions";

  public FragmentPopularTab(DefaultHttpClient client, BasicHttpContext context) {
    this.client = client;
    this.context = context;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.popular, container, false);

    // Get request from resource
    new GetRequest().execute(resource);

    return rootView;
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
      HttpGet request = new HttpGet(args[0]);
      HttpResponse response;

      try {
        // Execute request
        response = client.execute(request, context);

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

    protected void onPostExecute(String response) {

      // Log result
      Log.i("PostRequest-onPostExecute-response", response);

      final String[] resource = new String[1];
      resource[0] = response;

      new ParseCouponResponse().execute(resource);

    }
  }

  private class ParseCouponResponse extends AsyncTask<String, Void, String> {

    final String PROMOTIONS = "promotions";
    final String SAVINGS = "savings";
    final String PERCENT_OFF = "percentOff";
    final String AMOUNT_OFF = "amountOff";
    final String TYPES = "types";
    final String MERCHANT = "merchant";
    final String MERCHANT_NAME = "name";
    final String FOLLOWS_COUNT = "followsCount";
    final String DESCRIPTION = "description";

    final ArrayList<HashMap<String, String>> couponList = new ArrayList<HashMap<String, String>>();;

    @Override
    protected String doInBackground(String... args) {

      String resultTag = "No error";

      try {
        final JSONObject jsonObj = new JSONObject(args[0]);

        if (jsonObj.has(PROMOTIONS) == true) {

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

              Log.i("GetRequest-onPostExecute-percentOff", merchantName + " "
                  + description + " " + followsCount);

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

              Log.i("GetRequest-onPostExecute-amountOff", merchantName + " "
                  + description + " " + followsCount);

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

              Log.i("GetRequest-onPostExecute-others", merchantName + " "
                  + description + " " + followsCount);

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
          Log.i("GetRequest-onPostExecute-couponList",
              String.valueOf(couponList.size()));
          Log.i("GetRequest-onPostExecute-couponList", couponList.toString());
        }
      } catch (JSONException e) {
        e.printStackTrace();

        resultTag = "JSONException";
      }

      return resultTag;
    }

    protected void onPostExecute(String resultTag) {

      if (resultTag.equals("No error")) {
        // Create grid adaptor and set coupon grid to show coupons if there are
        // coupons
        if (!couponList.isEmpty()) {
          GridAdaptor adapter = new GridAdaptor(getActivity(), couponList);
          GridView grid = (GridView) getActivity().findViewById(
              R.id.coupons_grid);
          grid.setAdapter(adapter);
        } else {
          // Create temp coupon
          HashMap<String, String> tempCoupon = new HashMap<String, String>();

          // Create no result coupon
          tempCoupon.put(MERCHANT_NAME, "No result");
          tempCoupon.put(DESCRIPTION, "No result");
          tempCoupon.put(FOLLOWS_COUNT, "No result");

          // Save temp coupon to the coupon list
          couponList.add(tempCoupon);

          GridAdaptor adapter = new GridAdaptor(getActivity(), couponList);
          GridView grid = (GridView) getActivity().findViewById(
              R.id.coupons_grid);
          grid.setAdapter(adapter);
        }
      } else {
        Log.i("ParseCouponResponse-onPostExecute-resultTag", resultTag);
      }
    }
  }
}
