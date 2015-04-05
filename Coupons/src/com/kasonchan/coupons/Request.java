package com.kasonchan.coupons;

import android.app.Activity;
import android.os.AsyncTask;

public class Request extends Activity {

  public void postRequest(final String[] resource) {
    new PostRequest().execute(resource);
  }

  public void getRequest(final String[] resource) {
    new GetRequest().execute(resource);
  }

  private class PostRequest extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
      // TODO Auto-generated method stub
      return null;
    }
  }

  private class GetRequest extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
      // TODO Auto-generated method stub
      return null;
    }

  }
}
