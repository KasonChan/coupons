package com.kasonchan.coupons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class CouponActivity extends Activity {

  // Set up client, context and cookie store for the activity
  private DefaultHttpClient client = new DefaultHttpClient();
  private BasicHttpContext context = new BasicHttpContext();
  private CookieStore cookieStore = new BasicCookieStore();

  // Create action bar
  private ActionBar actionBar;

  // Create tabs and the corresponding fragments
  private ActionBar.Tab popularTab, personalizedTab;
  private Fragment fragmentPopularTab = new FragmentPopularTab(client, context);
  private Fragment fragmentPersonalizedTab = new FragmentPersonalizedTab(
      client, context);

  // Create local username, email, and password
  private String username;
  private String email;
  private String password;

  // Resource
  String resource = "http://api.bluepromocode.com/v2/promotions";
  String resourcePersonalized = "http://api.bluepromocode.com/v2/users/self/promotions/suggestions";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Display coupon layout
    setContentView(R.layout.coupons);

    // Create action bar
    actionBar = getActionBar();

    // Create action bar tabs
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    // Set custom tab text
    popularTab = actionBar.newTab().setText(R.string.popular);
    personalizedTab = actionBar.newTab().setText(R.string.justForYou);

    // Set tab listeners
    popularTab.setTabListener(new TabListener(fragmentPopularTab));
    personalizedTab.setTabListener(new TabListener(fragmentPersonalizedTab));

    // Add tabs to the action bar, set persoanlized tab as default
    actionBar.addTab(personalizedTab, 0, true);
    actionBar.addTab(popularTab, 1, false);

    // Set up cookie store
    context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

    // Fint textview and view
    TextView couponError = (TextView) findViewById(R.id.coupons_error);
    View couponErrorShadow = (View) findViewById(R.id.coupons_error_shadow);
    couponError.setVisibility(View.GONE);
    couponErrorShadow.setVisibility(View.GONE);

    // Get username from login or signup page
    try {
      username = this.getIntent().getStringExtra("username");
      email = this.getIntent().getStringExtra("email");
      password = this.getIntent().getStringExtra("password");
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Login resource
    final String[] resource = new String[3];
    resource[0] = "http://api.bluepromocode.com/v2/users/login";
    resource[1] = email;
    resource[2] = password;

    // Call api, parse post request, and get authorized
    new getAuthorized().execute(resource);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);

    if (username.isEmpty()) {
      menu.getItem(0).setTitle("");
    } else {
      menu.getItem(0).setTitle("@" + username);
    }

    return super.onCreateOptionsMenu(menu);
  }

  /**
   * getAuthorized class extends AsyncTask enables proper and easy use of the UI
   * thread. This class allows to perform background operations and publish
   * results on the UI thread without having to manipulate threads and/or
   * handlers.
   */
  private class getAuthorized extends AsyncTask<String, Void, String> {

    private String password = "";

    @Override
    protected String doInBackground(String... args) {

      password = args[2];

      // Build http post request with argument url
      HttpPost request = new HttpPost(args[0]);
      request.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");

      // Create json object for post request
      JSONObject jsonObj = new JSONObject();
      try {
        jsonObj.put("email", args[1]);
        jsonObj.put("password", args[2]);
      } catch (JSONException e2) {
        e2.printStackTrace();
        return ("JSONException");
      }

      // Set json entity to request
      StringEntity se;
      try {
        se = new StringEntity(jsonObj.toString());
        se.setContentType("application/json;charset=UTF-8");
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
            "application/json;charset=UTF-8"));
        request.setEntity(se);
      } catch (UnsupportedEncodingException e1) {
        // Catch unsupported encoding exception
        e1.printStackTrace();
        return ("UnsupportedEncodingException");
      }

      try {
        // Execute post request
        HttpResponse response = client.execute(request, context);

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

      final String[] resource = new String[2];
      resource[0] = response;
      resource[1] = password;

      new ParseLoginResponse().execute(resource);
    }
  }

  private class ParseLoginResponse extends AsyncTask<String, Void, String> {

    final String META = "meta";
    final String ERROR = "error";
    final String USER = "user";
    final String USERS = "users";
    final String USERNAME = "username";
    final String EMAIL = "email";
    private String response = "";

    @Override
    protected String doInBackground(String... args) {

      // Save password for passing to next intent for next activity
      response = args[0];
      password = args[1];

      String resultTag = "";

      // Parse result to json object
      try {
        JSONObject jsonObj = new JSONObject(response);

        // If error occurs, meta will be returned
        // Check error is true, then get the user error message
        if (jsonObj.has(META) == true) {
          JSONObject meta = jsonObj.getJSONObject(META);
          if (meta.getString(ERROR) == "true") {
            String error = meta.getString(USER);
            resultTag = error;
            response = error;
          }
        }
        // If there is not error, a list of user will be returned
        // Parse the user array and get the first user and parse the username
        else if (jsonObj.has(USERS) == true) {
          JSONArray users = jsonObj.getJSONArray(USERS);
          JSONObject user = users.getJSONObject(0);
          username = user.getString(USERNAME);
          email = user.getString(EMAIL);
          resultTag = "No error";
        }
      } catch (JSONException e) {
        resultTag = "JSON Parser" + " Error parsing data " + e.toString();
        response = "JSON Parser" + " Error parsing data " + e.toString();
      }

      return resultTag;
    }

    protected void onPostExecute(String resultTag) {

      // Log result
      Log.i("ParseLoginResponse-onPostExecute-resultTag", resultTag);

      // resultTag
      if (resultTag.equals("No error")) {
        // If there is no error, do nothing
      } else {
        // Otherwise end the activity
        CouponActivity.this.finish();
      }
    }
  }
}
