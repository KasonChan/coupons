package com.kasonchan.coupons;

import android.app.Activity;
import android.os.Bundle;

import com.kasonchan.coupons.http.HTTP;

public class CouponActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Display coupon layout
    setContentView(R.layout.coupon);

    // Get request
    // TODO: Finish up coupon activity
    new HTTP().getRequest("https://api.bluepromocode.com/v2/promotions");
  }

}
