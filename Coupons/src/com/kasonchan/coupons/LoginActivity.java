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

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

@SuppressWarnings("deprecation")
public class LoginActivity extends Activity {

  // Set up client, context and cookie store for the activity
  private DefaultHttpClient client      = new DefaultHttpClient();
  private BasicHttpContext  context     = new BasicHttpContext();
  private CookieStore       cookieStore = new BasicCookieStore();

  @Override
  public void onBackPressed() {
    // Override back button pressed to go back to home screen
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Display login layout
    setContentView(R.layout.login);

    // Find edittexts and buttons
    final EditText email = (EditText) findViewById(R.id.login_email);
    final TextView emailError = (TextView) findViewById(R.id.login_email_error);
    final EditText password = (EditText) findViewById(R.id.login_password);
    final TextView passwordError = (TextView) findViewById(R.id.login_password_error);
    final TextView postResult = (TextView) findViewById(R.id.login_result);
    final Button login = (Button) findViewById(R.id.login_login);
    final Button signup = (Button) findViewById(R.id.login_signup);

    // Set up cookie store
    context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

    /**
     * Login process
     */
    // Login resource
    final String[] resource = new String[3];
    resource[0] = "http://api.bluepromocode.com/v2/users/login";

    // Set invisible
    emailError.setVisibility(View.GONE);
    passwordError.setVisibility(View.GONE);
    postResult.setVisibility(View.GONE);

    // Check email
    email.addTextChangedListener(new TextWatcher() {
      public void afterTextChanged(Editable s) {
        String emailValue = s.toString();
        if (emailValue.isEmpty()) {
          emailError.setText("Invalid input - empty email");
          emailError.setVisibility(View.VISIBLE);
        } else if (!emailValue
            .matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
          emailError.setText("Invalid input - invalid email");
          emailError.setVisibility(View.VISIBLE);
        } else {
          emailError.setText("");
          emailError.setVisibility(View.GONE);
        }
      }

      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {
      }

      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
    });

    // Check password
    password.addTextChangedListener(new TextWatcher() {
      public void afterTextChanged(Editable s) {
        String passwordValue = s.toString();
        if (passwordValue.isEmpty()) {
          passwordError.setText("Invalid input - empty password");
          passwordError.setVisibility(View.VISIBLE);
        } else {
          passwordError.setText("");
          passwordError.setVisibility(View.GONE);
        }
      }

      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {
      }

      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
    });

    // Login button listener
    login.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        postResult.setVisibility(View.GONE);

        // Check email
        String emailValue = (email.getText()).toString();
        if (emailValue.isEmpty()) {
          emailError.setText("Invalid input - empty email");
          emailError.setVisibility(View.VISIBLE);
        } else if (!emailValue
            .matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
          emailError.setText("Invalid input - invalid email");
          emailError.setVisibility(View.VISIBLE);
        } else {
          emailError.setText("");
          emailError.setVisibility(View.GONE);
        }

        // Check password
        String passwordValue = (password.getText()).toString();
        if (passwordValue.isEmpty()) {
          passwordError.setText("Invalid input - empty password");
          passwordError.setVisibility(View.VISIBLE);
        } else {
          passwordError.setText("");
          passwordError.setVisibility(View.GONE);
        }

        if ((emailError.getVisibility() == View.GONE)
            && (passwordError.getVisibility() == View.GONE)) {

          // Valid formatted email and password
          resource[1] = (email.getText()).toString();
          resource[2] = (password.getText()).toString();

          // Call api, parse post request
          // If user is not found, show user not found error message
          // If user credentials are invalid, show invalid user credentials
          // If user credentials are valid, go to coupon activity
          new PostRequest().execute(resource);
        }
      }
    });

    /**
     * Signup process
     */
    // Coupon intent
    final Intent signupIntent = new Intent(this, SignupActivity.class);
    signupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    signup.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {

        // Start signup activity
        startActivity(signupIntent);
      }
    });
  }

  /**
   * PostRequest class extends AsyncTask enables proper and easy use of the UI
   * thread. This class allows to perform background operations and publish
   * results on the UI thread without having to manipulate threads and/or
   * handlers.
   */
  private class PostRequest extends AsyncTask<String, Void, String> {

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

    final String   META       = "meta";
    final String   ERROR      = "error";
    final String   USER       = "user";
    final String   USERS      = "users";
    final String   USERNAME   = "username";
    final String   EMAIL      = "email";

    final TextView postResult = (TextView) findViewById(R.id.login_result);
    private String username   = "";
    private String password   = "";
    private String email      = "";
    private String response   = "";

    @Override
    protected String doInBackground(String... args) {

      // Save password for passing to next intent for next activity
      String response = args[0];
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

      // If there is no error in json parsing, the resultTag will equals to
      // "No error"
      // Otherwise, error or exception occured
      if (resultTag.equals("No error")) {

        // Log username, email, password and response
        Log.i("ParseLoginResponse-onPostExecute-username", username);
        Log.i("ParseLoginResponse-onPostExecute-email", email);
        Log.i("ParseLoginResponse-onPostExecute-password", password);
        Log.i("ParseLoginResponse-onPostExecute-response", response);

        postResult.setText("You are logged in as " + username);
        postResult.setVisibility(View.VISIBLE);

        // Create a new intent for activate coupon activity
        // Coupon intent
        final Intent couponIntent = new Intent(LoginActivity.this,
            CouponActivity.class);
        couponIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Save username, email, password and result to intent; and pass to
        // the next activity
        couponIntent.putExtra("username", username);
        couponIntent.putExtra("email", email);
        couponIntent.putExtra("password", password);

        // Start coupon activity
        LoginActivity.this.startActivity(couponIntent);
      } else {
        postResult.setText(resultTag);
        postResult.setVisibility(View.VISIBLE);
      }
    }
  }
}
